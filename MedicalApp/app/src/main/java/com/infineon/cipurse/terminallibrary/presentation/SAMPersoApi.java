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
package com.infineon.cipurse.terminallibrary.presentation;

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseCommandsConstants;
import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.cryptoservices.ICipurseSMApi;
import com.infineon.cipurse.terminallibrary.cryptoservices.ICipurseSMApi.SM_OPTIONS;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.model.LoadKeyInfo;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.LOADKEY_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.SMLevel;


/**
* Class implementing APIs for SAM personalization related commands.
*
* @since 1.0.0
* @version 1.0.1
*/
public class SAMPersoApi implements ISAMPersoApi,CipurseCommandsConstants {

	private ICipurseSMApi cipurseSMApi;
	private ISessionManager sessionManager;
	private static final byte[] LOADKEY_HEADER=new byte[]{(byte) 0x80, (byte)0x72, 0, 0, 0};
	private static final byte[] GENERATEKEY_HEADER=new byte[]{(byte) 0x80, (byte)0x74, 0, 0, 0};

	/**	  **/
	public static final int					KEY_ALGO_ID_DES 	= 0x06;
	/**	  **/
	public static final int					KEY_ALGO_ID_2K_TDES = 0x07;
	/**	  **/
	public static final int					KEY_ALGO_ID_3K_TDES = 0x08;
	
	/**	  **/
	public static final int					KEY_ALGO_ID_NRG_COMPATIBLE_TOKEN = 0xA0;
	
	/**	  **/
	public static final int					KEY_ALGO_ID_AES128 = 0x09;
	/**	  **/
	public static final int					KEY_ALGO_ID_AES192 = 0x0A;
	/**	  **/
	public static final int					KEY_ALGO_ID_AES256 = 0x0B;

	/**
	 * Parameterized Constructor.
	 * @param mSessionManager {@link ISessionManager}
	 */
	public SAMPersoApi(ISessionManager mSessionManager) {
		this.sessionManager = mSessionManager;
		this.cipurseSMApi=sessionManager.getCipurseSMApi();
	}

	@Override
	public  ByteArray loadKey(LOADKEY_MODE mode, LoadKeyInfo loadKeyInfo,
			KeyDiversificationInfo keyDivInfo,
			short encryptionKeyID) throws CipurseException {

		sessionManager.getLogger().log("\"loadKey\" function parameters :\n"
						+ String.format(
								"LoadKey mode: %s%nLoad key information: %s%nKey diversification information: %s%nEncKey ID: 0x%04X",
								mode, loadKeyInfo, keyDivInfo,
								encryptionKeyID));


		SMLevel origionalSmi = this.sessionManager.getSMIValue();
		try
		{
			if(mode == LOADKEY_MODE.PLAIN || mode == LOADKEY_MODE.ENC_KEY)
			{
				//As of now from SAM Card, load key does not support Plain Key loading with SMI != ENC_XX.
				//In case Plain key load requested, we still proceed with default SMI. Card may reject this command.
				//User has to explicitly set SMI = ENC_XX
				return prepareAndSendloadKey((byte)(mode == LOADKEY_MODE.ENC_KEY?1:0),
						loadKeyInfo,
						keyDivInfo,
						encryptionKeyID);
			}
			else //KEYPERSO_MODE.SM_ENC
			{
				SMLevel tempSMI = SMLevel.eSM_ENC_ENC; //(byte) (CipurseConstant.SM_COMMAND_ENCED|CipurseConstant.SM_RESPONSE_ENCED);
				this.sessionManager.setSMIValue(tempSMI);
				byte[] smCommand = buildLoadKeyCommandFromSAM(tempSMI.getSMLevel(), loadKeyInfo, keyDivInfo);
				byte[] resp = cipurseSMApi.transmit(smCommand, SM_OPTIONS.NO_WRAP_CMD);
				return new ByteArray(resp);
			}
		}
		finally
		{
			this.sessionManager.setSMIValue(origionalSmi);
		}

	}

	protected byte[] buildLoadKeyCommandFromSAM(byte smi, LoadKeyInfo loadKeyInfo,
			KeyDiversificationInfo PpsKeyDiversifyInfo) throws CipurseException
	{
		//Call buildLoadKey with SMI ENC_ENC
		byte[] resp = cipurseSMApi.getCryptoAPI().buildLoadKey(new ByteArray(LOADKEY_HEADER), smi,
				loadKeyInfo,
				PpsKeyDiversifyInfo);
		byte[] smCommandData = Utils.extractBytes(resp, 0, resp.length-2);
		if(smCommandData.length==0)
			throw new CipurseException(CipurseConstant.CTL_INVALID_APDU_RESPONSE);
		byte[] smCommand = new byte[LOADKEY_HEADER.length + 1 + smCommandData.length + 1];
		int offset = Utils.arrayCopy(LOADKEY_HEADER, 0, smCommand, 0, LOADKEY_HEADER.length);
		smCommand[offset++]=smi;
		Utils.arrayCopy(smCommandData, 0, smCommand, offset, smCommandData.length);

		smCommand[0]=(byte)0x84;
		smCommand[4]=(byte) (smCommandData.length + 1);

		return smCommand;
	}

	protected  ByteArray prepareAndSendloadKey(byte loadKeyMode, LoadKeyInfo loadKeyInfo,
			KeyDiversificationInfo PpsKeyDiversifyInfo,
			short encryptionKeyID) throws CipurseException {

			byte[] cmdData=null;
			int offset=0;
			byte[] keyAndKvv = null;
			if(loadKeyMode==0)
			{
				cmdData=new byte[4];
				keyAndKvv = cipurseSMApi.getCryptoAPI().diversifyPlainKeySet(PpsKeyDiversifyInfo);
			}
			else
			{

				keyAndKvv = cipurseSMApi.getCryptoAPI().diversifyEncryptedKeySet(encryptionKeyID, PpsKeyDiversifyInfo);
				cmdData=new byte[6];
				cmdData[offset++]=(byte)((loadKeyInfo.getEncKeyID() >> 0x08) & 0x00FF);
				cmdData[offset++] = (byte)(loadKeyInfo.getEncKeyID() & 0x00FF);
			}
			cmdData[offset ++]=(byte) loadKeyInfo.getKeyFileFN();
			cmdData[offset ++]=(byte)((loadKeyInfo.getLoadKeyID() >> 0x08) & 0x00FF);
			cmdData[offset ++] = (byte)(loadKeyInfo.getLoadKeyID() & 0x00FF);
			cmdData[offset ++] = (byte) loadKeyInfo.getKeyAlgoID();
			cmdData = Utils.concat(cmdData, keyAndKvv);

			byte[] cmd=Utils.concat(LOADKEY_HEADER, cmdData);
			cmd[APDU_OFFSET_P1]=loadKeyMode;
			cmd[APDU_OFFSET_P3]=(byte) cmdData.length;

			return new ByteArray(cipurseSMApi.transmit(cmd));
	}


	@Override
	public ByteArray verifySAMPassword(ByteArray password) throws CipurseException {
		sessionManager.getLogger().log("\"verifySAMPassword\" function parameters :\n"
						+ String.format("Password: %s", password));
		int cmdDataLength = password.size();
		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength];
		Utils.arrayCopy(VERIFYPASSWORD_HEADER, 0, cmd, 0, VERIFYPASSWORD_HEADER.length);
		Utils.arrayCopy(password.getBytes(), 0, cmd, APDU_OFFSET_CDATA, password.size());
		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;

		return new ByteArray(cipurseSMApi.transmit(cmd));
	}


	@Override
	public ByteArray generateKey(byte keyLenOrAlgoId,
			byte keySetNumber) throws CipurseException {
		sessionManager.getLogger().log("\"generateKey\" function parameters :\n"
						+ String.format(
								"key length: 0x%02X%nKeySet number: 0x%02X", keyLenOrAlgoId, keySetNumber));

		int cmdDataLength = 2;
		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];
		int offset=Utils.arrayCopy(GENERATEKEY_HEADER, 0, cmd, 0, GENERATEKEY_HEADER.length);

		cmd[APDU_OFFSET_P1] = 0x00;
		cmd[APDU_OFFSET_P2] = 0x00;
		cmd[APDU_OFFSET_P3] = (byte) cmdDataLength;

		cmd[offset ++] = (byte) keyLenOrAlgoId;
		cmd[offset ++] = (byte) keySetNumber;

		return new ByteArray(cipurseSMApi.transmit(cmd));
	}

	@Override
	public  ByteArray getPersoSAMKeyInfo(byte keySetNumber,
			short keyID) throws CipurseException {
		sessionManager.getLogger().log("\"getPersoSAMKeyInfo\" function parameters :\n"
						+ String.format("keySet number: 0x%02X%nKey ID: 0x%04X",
								keySetNumber, keyID));
		int cmdDataLength = 3;

		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];

		int offset=Utils.arrayCopy(GETKEYINFO_HEADER, 0, cmd, 0, GETKEYINFO_HEADER.length);

		cmd[offset ++] = (byte)keySetNumber;
		cmd[offset ++]=(byte)((keyID >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(keyID & 0x00FF);

		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;

		return new ByteArray( cipurseSMApi.transmit(cmd));
	}

}
