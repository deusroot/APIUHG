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

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseCommandsConstants;
import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.model.DFFileAttributes;
import com.infineon.cipurse.terminallibrary.model.EncryptionKeyInfo;
import com.infineon.cipurse.terminallibrary.model.KeyAttributeInfo;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.model.LoadKeyInfo;
import com.infineon.cipurse.terminallibrary.pal.ICommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.pal.ISymCrypto;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CHAINING_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CRYPTO_MODE;
import com.infineon.cipurse.terminallibrary.presentation.SAMPersoApi;

/**
* Defines Secure Messaging service related to H/W SAM.
*
* @since 1.0.0
* @version 1.0.1
*/
public class CipurseSAMCryptoHWApi implements ICipurseSAMCryptoApi,CipurseCommandsConstants {


	private ICommunicationHandler hwSAMHandler;
	private ILogger logger;

	/**
	 * Constructs This object.
	 *
	 *
	 */
	public CipurseSAMCryptoHWApi() {

	}

	@Override
	public boolean authenticateCBP(ByteArray mutualAuthResponse)
			throws CipurseException {
		int cmdDataLength = mutualAuthResponse.size();
		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];
		Utils.arrayCopy(AUTHENTICATE_CBP_HEADER, 0, cmd, 0, AUTHENTICATE_CBP_HEADER.length);
		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;
		Utils.arrayCopy(mutualAuthResponse.getBytes(), 0, cmd, APDU_OFFSET_CDATA, mutualAuthResponse.size());
		byte[] resp =transmit(cmd);
		checkResponseOk(resp);
		return true;
	}

	@Override
	public byte[] generateSmElements(byte commandSmi,ByteArray cmdApdu)
			throws CipurseException {
		if(cmdApdu.size() > 255)
			throw new CipurseException(CipurseConstant.CTL_LC_MORE_THAN_MAX);

		int cmdDataLength = cmdApdu.size();
		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];
		int offset=Utils.arrayCopy(GENERATESM_HEADER, 0, cmd, 0, GENERATESM_HEADER.length);

		cmd[APDU_OFFSET_P2]=(byte) commandSmi;

		Utils.arrayCopy(cmdApdu.getBytes(), 0, cmd, offset, cmdApdu.size());
		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;
		byte[] resp =transmit(cmd);
		checkResponseOk(resp);
		return Utils.extractBytes(resp, 0, resp.length-2);
	}

	@Override
	public byte[] verifySmElements(CHAINING_MODE p2_cryptoMode,
			ByteArray respApdu) throws CipurseException {
		int cmdDataLength = respApdu.size();


		if(cmdDataLength > 255)
			throw new CipurseException(CipurseConstant.CTL_LC_MORE_THAN_MAX);

		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];
		int offset=Utils.arrayCopy(VERIFYSM_HEADER, 0, cmd, 0, VERIFYSM_HEADER.length);
		cmd[APDU_OFFSET_P1]=(byte) 0x03;
		cmd[APDU_OFFSET_P2]=(byte)p2_cryptoMode.getMode();

		offset=Utils.arrayCopy(respApdu.getBytes(), 0, cmd, offset, respApdu.size());

		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;

		byte[] resp =transmit(cmd);
		checkResponseOk(resp);
		return Utils.extractBytes(resp, 0, resp.length-2);
	}

	@Override
	public byte[] endSession() throws CipurseException {

		byte[] cmd=new byte[ENDSESSION_HEADER.length];

		Utils.arrayCopy(ENDSESSION_HEADER, 0, cmd, 0, ENDSESSION_HEADER.length);

		byte[] resp =transmit(cmd);
		checkResponseOk(resp);
		return resp;
	}

	@Override
	public byte[] diversifyEncryptedKeySet(short encKeyRef,
			KeyDiversificationInfo keyDiversifyInfo) throws CipurseException {
		return getDiversifyKeySet(keyDiversifyInfo,5,encKeyRef,(byte)0x03);
	}

	@Override
	public byte[] diversifyPlainKeySet(KeyDiversificationInfo keyDiversifyInfo) throws CipurseException {
		return getDiversifyKeySet(keyDiversifyInfo,3,(short)0,(byte)0x04);
	}

	@Override
	public byte[] buildLoadKey(ByteArray loadKeyCmdHeader,
			byte smiloadKey, LoadKeyInfo loadKeyInfo, KeyDiversificationInfo keyDivdata) throws CipurseException {


		int divDataSize=getDiversificationDataLength(loadKeyInfo.getKeyAlgoID());
		int cmdDataLength = 13 + divDataSize;

		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];
		Utils.arrayCopy(DIVERSIFY_HEADER, 0, cmd, 0, DIVERSIFY_HEADER.length);
		cmd[APDU_OFFSET_P1]=(byte) 0x33;
		cmd[APDU_OFFSET_P2]=(byte)smiloadKey;
		int offset=Utils.arrayCopy(loadKeyCmdHeader.getBytes(), 0, cmd, APDU_OFFSET_CDATA, loadKeyCmdHeader.size());

		cmd[offset ++]=(byte) loadKeyInfo.getKeyAlgoID();


		// User should be indicated of invalid length of Diversification data
		if( keyDivdata.getPlainKeyOrDivData() != null && keyDivdata.getPlainKeyOrDivData().length == divDataSize )
			offset = Utils.arrayCopy(keyDivdata.getPlainKeyOrDivData(), 0, cmd, offset, keyDivdata.getPlainKeyOrDivData().length);
		else if((keyDivdata.getPlainKeyOrDivData() == null || keyDivdata.getPlainKeyOrDivData().length == 0)&& ( keyDivdata.getKeyDiversificationMode()==0 || keyDivdata.getKeyDiversificationMode()==0xAF ))
			offset = offset + divDataSize;
		else
			throw new CipurseException(CipurseConstant.CTL_INVALID_DIVERSIFICATION_DATA);

		cmd[offset ++]=(byte)((keyDivdata.getKeyIDorReference() >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(keyDivdata.getKeyIDorReference() & 0x00FF);
		cmd[offset ++]=(byte) loadKeyInfo.getKeyFileFN();
		cmd[offset ++]=(byte)((loadKeyInfo.getLoadKeyID() >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(loadKeyInfo.getLoadKeyID() & 0x00FF);
		cmd[offset ++]=(byte) keyDivdata.getKeyDiversificationMode();
		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;

		return transmit(cmd);
	}

	@Override
	public byte[] buildUpdateKey(ByteArray updateKeyCmdHeader,
			int smi_updateKey, KeyAttributeInfo keyAttributes, KeyDiversificationInfo keyDivdata) throws CipurseException {


		int cmdDataLength = 28 ;

		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];
		int offset=Utils.arrayCopy(DIVERSIFY_HEADER, 0, cmd, 0, DIVERSIFY_HEADER.length);
		cmd[APDU_OFFSET_P1]=(byte) 0x13;
		cmd[APDU_OFFSET_P2]=(byte)smi_updateKey;

		offset=Utils.arrayCopy(updateKeyCmdHeader.getBytes(), 0, cmd, offset, updateKeyCmdHeader.size());

		cmd[offset ++]=(byte) keyAttributes.getKeyAddInfo();
		cmd[offset ++]=(byte) keyAttributes.getKeyLength();
		cmd[offset ++]=(byte) keyAttributes.getKeyAlgoId();

		// User should be indicated of invalid length of Diversification data
		if(keyDivdata.getPlainKeyOrDivData()!=null && keyDivdata.getPlainKeyOrDivData().length== 16)
			offset = Utils.arrayCopy(keyDivdata.getPlainKeyOrDivData(), 0, cmd, offset, keyDivdata.getPlainKeyOrDivData().length);
		else if(keyDivdata.getKeyDiversificationMode()==0 &&
				(keyDivdata.getPlainKeyOrDivData()==null || keyDivdata.getPlainKeyOrDivData().length != 16))
			offset = offset + 16;
		else
			throw new CipurseException(CipurseConstant.CTL_INVALID_DIVERSIFICATION_DATA);

		cmd[offset ++]=(byte)((keyDivdata.getKeyIDorReference() >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(keyDivdata.getKeyIDorReference()  & 0x00FF);

		cmd[offset ++]=(byte) keyDivdata.getKeyDiversificationMode();

		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;

		return transmit(cmd);
	}

	@Override
	public byte[] buildCreateADF(ByteArray createFilecmdHeader,
			int cipurseVersion, int smi, DFFileAttributes dfAttrib,
			KeyDiversificationInfo[] keyDiffInfo, EncryptionKeyInfo encKeyInfo,
			KeyAttributeInfo[] keyInfos, int maxKeysToPersonalize)
			throws CipurseException {


		if(keyInfos!=null)
		{
			if(maxKeysToPersonalize < 1 && maxKeysToPersonalize > keyInfos.length)
				throw new CipurseException(CipurseConstant.CTL_INVALID_PARAMETER);
		}
		else
			throw new CipurseException(CipurseConstant.CTL_INVALID_PARAMETER);


		int origionalLc=3; //9200 LL

		//normal attributes
		int offset=APDU_OFFSET_CDATA;
		//initial command data length
		byte[] cmd =new byte[cipurseVersion < 0x03?13:12 + APDU_OFFSET_CDATA];
		Utils.arrayCopy(DIVERSIFY_HEADER, 0, cmd, 0, DIVERSIFY_HEADER.length);

		cmd[APDU_OFFSET_P1]=0x23;
		cmd[APDU_OFFSET_P2]=(byte) smi;

		cmd[offset ++] = (byte) cipurseVersion;
		offset=Utils.arrayCopy(createFilecmdHeader.getBytes(), 0, cmd, offset, createFilecmdHeader.size());
		cmd[offset ++] = (byte) dfAttrib.getFileDescriptor();

		cmd[offset++] = (byte)(dfAttrib.getAppProfile());
		cmd[offset ++] = (byte)((dfAttrib.getFileID() >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(dfAttrib.getFileID() & 0x00FF);

		origionalLc+=4;
		//for older versions of CBP
		if(cipurseVersion < 3)
		{
			cmd[offset ++] = (byte)encKeyInfo.getEncKeyNumber();
			origionalLc ++;
		}

		cmd[offset ++] = (byte)dfAttrib.getNumOfEFs();
		cmd[offset ++] = (byte)dfAttrib.getNumOfSFIDs();

		origionalLc+=2;

		//sec attribs: nbrk, SMR, ART
		if(dfAttrib.getSmr() != null && dfAttrib.getArt() != null) {
			byte[] secBytes=new byte[1];
			secBytes[0] = (byte) dfAttrib.getNumOfKeys();
			secBytes = Utils.concat(secBytes, dfAttrib.getSmr());
			secBytes =Utils.concat(secBytes, dfAttrib.getArt());
			cmd=Utils.concat(cmd, secBytes);
			origionalLc+=secBytes.length;
		}

		//keys sets info
		byte[] keySets=new byte[KeyAttributeInfo.KEY_SET_SIZE * dfAttrib.getNumOfKeys()];
		offset=0;
		for (int i=0; i<dfAttrib.getNumOfKeys(); i++)
		{
			offset=Utils.arrayCopy(keyInfos[i].getKeySetInBytes(), 0, keySets, offset, KeyAttributeInfo.KEY_SET_SIZE);
		}
		cmd=Utils.concat(cmd, keySets);
		origionalLc+=keySets.length;


		//FCP
		byte[] fcp = buildTagA5(dfAttrib);
		if (fcp != null) {
			cmd = Utils.concat(cmd, fcp);
			origionalLc  += fcp.length;
		}

		//key set
		if(maxKeysToPersonalize > 0)
		{
			origionalLc+=3; //A0 0F LL
			for(int i=0; i<maxKeysToPersonalize; i++)
			{
				byte[] keyDivSet = getKeySpecificInfo(keyDiffInfo[i], keyInfos[i]);
				cmd=Utils.concat(cmd, keyDivSet);
				origionalLc+= keyDivSet.length;
			}
		}

		cmd[APDU_OFFSET_CDATA + 5]=(byte) (origionalLc);

		if(cmd.length-5 > 255)
			throw new CipurseException(CipurseConstant.CTL_LC_MORE_THAN_MAX);
		cmd[APDU_OFFSET_P3]=(byte) (cmd.length-5);

		cmd=Utils.concat(cmd, new byte[]{0});
		return transmit(cmd);
	}

	@Override
	public ByteArray getSessionKey() throws CipurseException {
		byte[] cmd=new byte[READSESSIONKEY_HEADER.length];

		Utils.arrayCopy(READSESSIONKEY_HEADER, 0, cmd, 0, READSESSIONKEY_HEADER.length);
		cmd[APDU_OFFSET_P3]=0x10;

		byte[] resp =  transmit(cmd);
		checkResponseOk(resp);
		return new ByteArray(Utils.extractBytes(resp, 0, resp.length-2));
	}

	@Override
	public void setSessionKey(ByteArray psSessionKey) throws CipurseException {
		throw new CipurseException(CipurseConstant.CTL_FUNCTION_NOT_SUPPORTED);
	}


	@Override
	public byte[] authenticateSAM(KeyDiversificationInfo PpsAuthKeyInfo,
			byte smiForGetChallengeResponse, byte smiForMutualAuth,
			byte cbpKeyNo, ByteArray getChallengeResp)
			throws CipurseException {
		int cmdDataLength = getChallengeResp.size()
				+ (PpsAuthKeyInfo.getPlainKeyOrDivData() != null ? PpsAuthKeyInfo.getPlainKeyOrDivData().length : 0) + 6;
		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];
		int offset=Utils.arrayCopy(AUTHENTICATE_SAM_HEADER, 0, cmd, 0, AUTHENTICATE_SAM_HEADER.length);
		cmd[offset ++]=(byte) PpsAuthKeyInfo.getKeyDiversificationMode();
		cmd[offset ++]=(byte)((PpsAuthKeyInfo.getKeyIDorReference() >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(PpsAuthKeyInfo.getKeyIDorReference() & 0x00FF);
		cmd[offset ++] = (byte) smiForGetChallengeResponse;
		cmd[offset ++] = (byte) smiForMutualAuth;
		cmd[offset ++] = (byte) cbpKeyNo;

		offset=Utils.arrayCopy(getChallengeResp.getBytes(), 0, cmd, offset, getChallengeResp.size());
		if(PpsAuthKeyInfo.getPlainKeyOrDivData() != null)
			offset=Utils.arrayCopy(PpsAuthKeyInfo.getPlainKeyOrDivData(), 0, cmd, offset, PpsAuthKeyInfo.getPlainKeyOrDivData().length);
		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;
		cmd[APDU_OFFSET_P2]=(byte)0x00;
		byte[] resp =transmit(cmd);
		checkResponseOk(resp);
		return Utils.extractBytes(resp, 0, resp.length-2);
	}

	@Override
	public ICipurseSAMCryptoApi initializeCryptoAPI(
			ICipurseSMApi Pprgui1CryptoAPIBuffer,CRYPTO_MODE cryptoMode,
			ICommunicationHandler commsHandle, ILogger PpsLoggerHandle,
			ISymCrypto PpsAESHandle) {
		this.hwSAMHandler = commsHandle;
		this.logger = PpsLoggerHandle;
		return this;
	}

	/**
	 * Method to check response ok.
	 * @param response received response as byte [].
	 * @throws CipurseException {@link CipurseException}
	 */
	protected void checkResponseOk(byte[] response) throws CipurseException
	{
		if(response!=null && response.length >= 2)
		{
			int sw=Utils.getShort(response, response.length - 2);
			if(sw!= 0x9000 )
				throw new CipurseException(CipurseCryptoUtils.getMapedErrorCode(sw));
		}
		else
			throw new CipurseException(CipurseConstant.CTL_INVALID_APDU_RESPONSE);
	}

	/**
	 * Method to get the Diversification Data Length based on the algorithm.
	 * @param keyLenOrAlgoId
	 * @return Length as integer.
	 * @throws CipurseException {@link CipurseException}
	 */
	protected int getDiversificationDataLength(int keyLenOrAlgoId) throws CipurseException
	{
		switch (keyLenOrAlgoId) {
			case SAMPersoApi.KEY_ALGO_ID_AES128:
			case 0x10: //AES length for back compatibility
			case SAMPersoApi.KEY_ALGO_ID_2K_TDES:
			
			case SAMPersoApi.KEY_ALGO_ID_NRG_COMPATIBLE_TOKEN:
			
				return 16;

			case SAMPersoApi.KEY_ALGO_ID_AES192:
			case SAMPersoApi.KEY_ALGO_ID_AES256:
				return 32;

			case SAMPersoApi.KEY_ALGO_ID_DES:
				return 8;

			case SAMPersoApi.KEY_ALGO_ID_3K_TDES:
				return 24;

			default:
				throw new CipurseException(CipurseConstant.CTL_INVALID_ALGO_ID);
		}
	}

	/**
	 * Method to transmit the data to reader.
	 * @param command to be transmited
	 * @return response as byte [].
	 * @throws CipurseException
	 */
	private byte[] transmit(byte[] command) throws CipurseException{
		logger.log(ILogger.COMMAND_MESSAGE,"SAM:","", command);
		byte[] response =hwSAMHandler.transReceive(command);
		logger.log(ILogger.RESPONSE_MESSAGE,"SAM:","", response);
		return response;
	}

	/**
	 * Method to get the Key Specific Info from the key diversification data.
	 * @param keyDivInfo
	 * @param keyInfo
	 * @return Key specific info
	 * @throws CipurseException
	 */
	protected byte[] getKeySpecificInfo(KeyDiversificationInfo keyDivInfo, KeyAttributeInfo keyInfo) throws CipurseException
	{
		byte[] set=new byte[20];
		int offset=0;
		byte[] divData=keyDivInfo.getPlainKeyOrDivData();


		// User should be indicated of invalid length of Diversification data
		if(divData!=null && divData.length== 16)
			offset = Utils.arrayCopy(divData, 0, set, 0, divData.length);
		else if(divData==null && keyDivInfo.getKeyDiversificationMode()==0)
			offset = offset + 16;
		else
			throw new CipurseException(CipurseConstant.CTL_INVALID_DIVERSIFICATION_DATA);

		set[offset ++] = (byte)((keyDivInfo.getKeyIDorReference() >> 0x08) & 0x00FF);
		set[offset ++] = (byte)(keyDivInfo.getKeyIDorReference()  & 0x00FF);
		set[offset ++] = (byte)keyDivInfo.getKeyDiversificationMode();
		set[offset ++] = (byte)keyInfo.getKeyAddInfo();

		return set;
	}
	/**
	 * Builds TLV object for tag A5 along with FCP TLV.
	 * @param dfAttrib DF file Attribute
	 * @return built TLV structure as byte []
	 */
	protected byte[] buildTagA5(DFFileAttributes dfAttrib) {
			// create A5 empty tag
			byte[] tagA5 = new byte[2];
			tagA5[0] = (byte) 0xA5;

			// append if fcp template with tag 62 is present
			byte[] tag62 = dfAttrib.getFcpInfo().getFCPBytes();
			if(tag62 != null) {
				tagA5 = Utils.concat(tagA5, tag62);
			}

			
			// append if C8 tag is present
			byte[] tagC8 = dfAttrib.getTagC8();
			if( dfAttrib.getNRGMap() != null) {
				tagA5 = Utils.concat(tagA5, tagC8);
			}
			

			// update TLV length for A5 tag
			tagA5[1] = (byte) (tagA5.length - 2);

			return tagA5;
	}

	protected byte[] getDiversifyKeySet(KeyDiversificationInfo keyDiversifyInfo, int cmdDataLength, short encKeyRef, byte p1_2) throws CipurseException{

		if(keyDiversifyInfo.getPlainKeyOrDivData() !=null)
			cmdDataLength +=keyDiversifyInfo.getPlainKeyOrDivData().length;

		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];
		Utils.arrayCopy(DIVERSIFY_HEADER, 0, cmd, 0, DIVERSIFY_HEADER.length);
		cmd[APDU_OFFSET_P1]=(byte)p1_2;
		cmd[APDU_OFFSET_P2]=(byte)p1_2;
		int offset = APDU_OFFSET_CDATA & 0xFF;
		cmd[offset ++]=(byte)((keyDiversifyInfo.getKeyIDorReference()  >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(keyDiversifyInfo.getKeyIDorReference()  & 0x00FF);
		cmd[offset ++] = (byte)keyDiversifyInfo.getKeyDiversificationMode();
		if(encKeyRef > 0) {
			cmd[offset ++]=(byte)((encKeyRef >> 0x08) & 0x00FF);
			cmd[offset ++] = (byte)(encKeyRef & 0x00FF);
		}

		if(keyDiversifyInfo.getPlainKeyOrDivData() !=null)
			offset=Utils.arrayCopy(keyDiversifyInfo.getPlainKeyOrDivData(), 0, cmd, offset, keyDiversifyInfo.getPlainKeyOrDivData().length);

		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;

		byte[] response = transmit(cmd);
		checkResponseOk(response);
		if(response.length > 2){
			return Utils.extractBytes(response, 0, response.length-2);
		} else {
			throw new CipurseException(CipurseConstant.CTL_INVALID_APDU_RESPONSE);
		}
	}

}
