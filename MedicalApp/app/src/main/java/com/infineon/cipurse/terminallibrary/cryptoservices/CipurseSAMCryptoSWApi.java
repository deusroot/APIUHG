/*****************************************************************************
* THE SOURCE CODE AND ITS RELATED DOCUMENTATION IS PROVIDED "AS IS". INFINEON
* TECHNOLOGIES MAKES NO OTHER WARRANTY OF ANY KIND,WHETHER EXPRESS,IMPLIED OR,
* STATUTORY AND DISCLAIMS ANY AND ALL IMPLIED WARRANTIES OF MERCHANTABILITY,
* SATISFACTORY QUALITY, NON INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
*
* THE SOURCE CODE AND DOCUMENTATION MAY INCLUDE ERRORS. INFINEON TECHNOLOGIES
* RESERVES THE RIGHT TO INCORPORATE MODIFICATIONS TO THE SOURCE CODE IN LATER
* REVISIONS OF IT, AND TO MAKE IMPROVEMENTS OR CHANGES IN THE DOCUMENTATION OR
* THE PRODUCTS OR TECHNOLOGIES DESCRIBED THEREIN AT ANY TIME.
*
* INFINEON TECHNOLOGIES SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT OR
* CONSEQUENTIAL DAMAGE OR LIABILITY ARISING FROM YOUR USE OF THE SOURCE CODE OR
* ANY DOCUMENTATION, INCLUDING BUT NOT LIMITED TO, LOST REVENUES, DATA OR
* PROFITS, DAMAGES OF ANY SPECIAL, INCIDENTAL OR CONSEQUENTIAL NATURE, PUNITIVE
* DAMAGES, LOSS OF PROPERTY OR LOSS OF PROFITS ARISING OUT OF OR IN CONNECTION
* WITH THIS AGREEMENT, OR BEING UNUSABLE, EVEN IF ADVISED OF THE POSSIBILITY OR
* PROBABILITY OF SUCH DAMAGES AND WHETHER A CLAIM FOR SUCH DAMAGE IS BASED UPON
* WARRANTY, CONTRACT, TORT, NEGLIGENCE OR OTHERWISE.
*
* (C)Copyright INFINEON TECHNOLOGIES All rights reserved
******************************************************************************
*/
package com.infineon.cipurse.terminallibrary.cryptoservices;

import java.util.Arrays;

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseCommandsConstants;
import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.framework.CommandBuilder;
import com.infineon.cipurse.terminallibrary.framework.ICommandBuilder;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.model.DFFileAttributes;
import com.infineon.cipurse.terminallibrary.model.EncryptionKeyInfo;
import com.infineon.cipurse.terminallibrary.model.KeyAttributeInfo;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.model.LoadKeyInfo;
import com.infineon.cipurse.terminallibrary.pal.ICommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.pal.ISymCrypto;
import com.infineon.cipurse.terminallibrary.pal.ISymCrypto.ALGO_TYPE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CHAINING_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CRYPTO_MODE;

/**
* Defines Secure Messaging service related to S/W SAM.
* 
* @since 1.0.0
* @version 1.0.1
*/
public class CipurseSAMCryptoSWApi implements ICipurseSAMCryptoApi,CipurseCommandsConstants {

	private ISymCrypto symCrypto;
	private ILogger logger;
	private CipurseCrypto cipurseCrypto;
	protected byte currentSmi;
	protected boolean secureSession = false;
	protected CipurseCrypto tempCipurseCrypto;
	private ICommandBuilder commandBuilder;


	/**
	 * Default Constructor
	 */
	public CipurseSAMCryptoSWApi() {
		this.commandBuilder = new CommandBuilder();
	}

	@Override
	public byte[] authenticateSAM(KeyDiversificationInfo PpsAuthKeyInfo,
			byte smiForGetChallengeResponse, byte smiForMutualAuth,
			byte cbpKeyNo,ByteArray getChallengeResp) throws CipurseException {

		if(secureSession)
			return authenticateSAMWithSM(PpsAuthKeyInfo, smiForGetChallengeResponse, smiForMutualAuth, cbpKeyNo, getChallengeResp);

		//RP [16] || rP [6]
		byte[] RP = new byte[CipurseConstant.AES_BLOCK_LENGTH];
		byte[] rP = new byte[CipurseConstant.CIPURSE_SECURITY_PARAM_N];
		System.arraycopy(getChallengeResp.getBytes(), 0, RP, 0, CipurseConstant.AES_BLOCK_LENGTH);
		System.arraycopy(getChallengeResp.getBytes(), CipurseConstant.AES_BLOCK_LENGTH, rP, 0,
				CipurseConstant.CIPURSE_SECURITY_PARAM_N);

		//generate host challenge
		byte[] RT = symCrypto.getRandomNumber(CipurseConstant.AES_BLOCK_LENGTH);
		byte[] rT = symCrypto.getRandomNumber(CipurseConstant.CIPURSE_SECURITY_PARAM_N);

		//generate session keys
		byte[] keyValueAndKvv = diversifyPlainKeySet(PpsAuthKeyInfo);
		byte[] Cp = cipurseCrypto.generateK0AndGetCp(Utils.extractBytes(keyValueAndKvv, 0, CipurseConstant.AES_BLOCK_LENGTH), RP, rP, RT, rT);

		//data for mutual authenticate
		//cp [16] || RT [16] || rT [6]
		byte[] mutualAuth = new byte[CipurseConstant.AES_BLOCK_LENGTH + CipurseConstant.AES_BLOCK_LENGTH
		                             + CipurseConstant.CIPURSE_SECURITY_PARAM_N];
		System.arraycopy(Cp, 0, mutualAuth, 0, CipurseConstant.AES_BLOCK_LENGTH);
		System.arraycopy(RT, 0, mutualAuth, CipurseConstant.AES_BLOCK_LENGTH,
				CipurseConstant.AES_BLOCK_LENGTH);
		System.arraycopy(rT, 0, mutualAuth, (CipurseConstant.AES_BLOCK_LENGTH*2),
				CipurseConstant.CIPURSE_SECURITY_PARAM_N);

		return mutualAuth;
	}



	protected byte[] authenticateSAMWithSM(KeyDiversificationInfo PpsAuthKeyInfo,
			byte smiForGetChallengeResponse, byte smiForMutualAuth,
			int cbpKeyNo,ByteArray getChallengeResp) throws CipurseException {


		byte[] plainChallengeResponse = CipurseCryptoUtils.unWrapResponse(this,getChallengeResp, smiForGetChallengeResponse);

		//RP [16] || rP [6]
		byte[] RP = new byte[CipurseConstant.AES_BLOCK_LENGTH];
		byte[] rP = new byte[CipurseConstant.CIPURSE_SECURITY_PARAM_N];
		System.arraycopy(plainChallengeResponse, 0, RP, 0, CipurseConstant.AES_BLOCK_LENGTH);
		System.arraycopy(plainChallengeResponse, CipurseConstant.AES_BLOCK_LENGTH, rP, 0,
				CipurseConstant.CIPURSE_SECURITY_PARAM_N);

		//generate host challenge
		byte[] RT = symCrypto.getRandomNumber(CipurseConstant.AES_BLOCK_LENGTH);
		byte[] rT = symCrypto.getRandomNumber(CipurseConstant.CIPURSE_SECURITY_PARAM_N);

		//generate session keys
		byte[] keyValueAndKvv = diversifyPlainKeySet(PpsAuthKeyInfo);
		tempCipurseCrypto = new CipurseCrypto(symCrypto, logger);
		byte[] Cp = tempCipurseCrypto.generateK0AndGetCp(Utils.extractBytes(keyValueAndKvv, 0, CipurseConstant.AES_BLOCK_LENGTH), RP, rP, RT, rT);

		//data for mutual authenticate
		//cp [16] || RT [16] || rT [6]
		byte[] mutualAuth = new byte[CipurseConstant.AES_BLOCK_LENGTH + CipurseConstant.AES_BLOCK_LENGTH
		                             + CipurseConstant.CIPURSE_SECURITY_PARAM_N];
		System.arraycopy(Cp, 0, mutualAuth, 0, CipurseConstant.AES_BLOCK_LENGTH);
		System.arraycopy(RT, 0, mutualAuth, CipurseConstant.AES_BLOCK_LENGTH,
				CipurseConstant.AES_BLOCK_LENGTH);
		System.arraycopy(rT, 0, mutualAuth, (CipurseConstant.AES_BLOCK_LENGTH*2),
				CipurseConstant.CIPURSE_SECURITY_PARAM_N);


		byte[] mutualAuthSMData = generateSmElements(smiForMutualAuth, commandBuilder.buildMutualAuthentication((byte)cbpKeyNo, new ByteArray(mutualAuth)));
		return constructAuthenticateSAMSMResponse(mutualAuthSMData, mutualAuth, smiForMutualAuth);
	}


	protected byte[] constructAuthenticateSAMSMResponse(byte[] mutualAuthSMResp, byte[] plainCryptoGram, byte smi) throws CipurseException
	{

		switch (smi & CipurseConstant.BITMAP_SMI_COMMAND) {
			case CipurseConstant.SM_COMMAND_MACED:
				//cryptogram + mac
				return Utils.concat(plainCryptoGram, mutualAuthSMResp);

			case CipurseConstant.SM_COMMAND_ENCED:
				return mutualAuthSMResp;
			case CipurseConstant.SM_COMMAND_RESPONSE_PLAIN:
				return plainCryptoGram;
			default:
				throw new CipurseException(CipurseConstant.CTL_CONDITIONS_OF_USE_NOT_SATISFIED);
		}

	}


	@Override
	public boolean authenticateCBP(ByteArray ppsMutualAuthResponse) throws CipurseException {

		byte[] plainMutAuthResp = ppsMutualAuthResponse.getBytes();
		if(secureSession)
			plainMutAuthResp = CipurseCryptoUtils.unWrapResponse(this,ppsMutualAuthResponse, this.currentSmi);

		if(plainMutAuthResp.length != (CipurseConstant.AES_BLOCK_LENGTH + 2) )
			throw new CipurseException(CipurseConstant.CTL_INCORRECT_SM_DATAOBJECT);


		byte[] cT = new byte[CipurseConstant.AES_BLOCK_LENGTH];
		System.arraycopy(plainMutAuthResp, 0, cT, 0, cT.length);

		boolean verified = false;
		if(secureSession)
		{
			verified = tempCipurseCrypto.verifyPICCResponse(cT);
			if(verified)
				cipurseCrypto = tempCipurseCrypto;
			else
				tempCipurseCrypto = null;
		}
		else
		{
			verified = cipurseCrypto.verifyPICCResponse(cT);
		}

		secureSession = true;
		return verified;
	}

	@Override
	public byte[] generateSmElements(byte SMI, ByteArray plainCommand) throws CipurseException {
		this.currentSmi = SMI;
		return cipurseCrypto.wrapCommand(plainCommand.getBytes(), SMI);
	}

	@Override
	public byte[] verifySmElements(CHAINING_MODE p2_cryptoMode,
			ByteArray respApdu) throws CipurseException {

		return cipurseCrypto.unwrapCommand(respApdu.getBytes(), currentSmi);

	}

	@Override
	public byte[] endSession() throws CipurseException {
		this.secureSession = false;
		return new byte[0];
	}

	@Override
	public byte[] diversifyEncryptedKeySet(short pui2EncKeyID,
			KeyDiversificationInfo ppsKeyDiversifyInfo) throws CipurseException {
		return performKeyDiversification(ppsKeyDiversifyInfo, pui2EncKeyID);
	}

	@Override
	public byte[] diversifyPlainKeySet(KeyDiversificationInfo divInfo) throws CipurseException {

		return performKeyDiversification(divInfo, (short)0x00);
	}

	@Override
	public byte[] buildLoadKey(ByteArray loadKeyCmdHeader,
			byte smiloadKey, LoadKeyInfo loadKeyInfo, KeyDiversificationInfo keyDivdata) throws CipurseException {
		throw new CipurseException(CipurseConstant.CTL_FUNCTION_NOT_SUPPORTED);
	}

	@Override
	public byte[] buildUpdateKey(ByteArray updateKeyCmdHeader,
			int smi_updateKey, KeyAttributeInfo keyInfo,KeyDiversificationInfo keyDivInfo) throws CipurseException {
		throw new CipurseException(CipurseConstant.CTL_FUNCTION_NOT_SUPPORTED);
 	}

	@Override
	public byte[] buildCreateADF(ByteArray createHeader,
			int cipurseVersion, int smi,
			DFFileAttributes objDFFileAttributes,
			KeyDiversificationInfo[] keyDiffInfo,EncryptionKeyInfo encKeyInfo,
			KeyAttributeInfo[] keyAttributes,
			int maxKeysToPersonalize) throws CipurseException {

		throw new CipurseException(CipurseConstant.CTL_FUNCTION_NOT_SUPPORTED);
	}

	@Override
	public ByteArray getSessionKey() throws CipurseException {
		return  new ByteArray(cipurseCrypto.getSessionKey());
	}

	@Override
	public ICipurseSAMCryptoApi initializeCryptoAPI(
			ICipurseSMApi Pprgui1CryptoAPIBuffer,CRYPTO_MODE cryptoMode, ICommunicationHandler commsHandle, ILogger PpsLoggerHandle,
			ISymCrypto PpsAESHandle) throws CipurseException {
		this.logger = PpsLoggerHandle;
		this.symCrypto = PpsAESHandle;
		this.cipurseCrypto = new CipurseCrypto(PpsAESHandle, PpsLoggerHandle);
		return this;
	}


	protected byte[] performKeyDiversification(KeyDiversificationInfo divInfo, short encKeyId) throws CipurseException
	{
		byte[] masterKey = symCrypto.getKeyValue(divInfo.getKeyIDorReference());

		byte[] divData = divInfo.getPlainKeyOrDivData();
		byte[] diversifiedKey=null;
		switch (divInfo.getKeyDiversificationMode()) {
		case 0x01:
			if(divData==null || divData.length<=0)
				diversifiedKey=Arrays.copyOf(masterKey, masterKey.length);
			else
			{
				byte[] encedData = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,masterKey,null, divData);
				diversifiedKey= Utils.xor(encedData, divData);
			}
			break;
		case 0x00:
			diversifiedKey=Arrays.copyOf(masterKey, masterKey.length);
			break;
		default:
			throw new CipurseException(CipurseConstant.CTL_INCORRECT_PARAM_IN_CMD_DATA);
		}

		byte[] kvv = symCrypto.getKVV(diversifiedKey);
		byte[] finalKey = diversifiedKey;
		if(encKeyId > 0)
		{
			byte[] encKey = symCrypto.getKeyValue(encKeyId);
			finalKey = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,encKey,null, diversifiedKey);
		}
		return Utils.concat(finalKey, kvv);
	}

	@Override
	public void setSessionKey(ByteArray psSessionKey) {
		cipurseCrypto.setSessionKey(psSessionKey.getBytes());
		secureSession = true;
	}

}
