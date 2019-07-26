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

import java.util.Arrays;

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseCommandsConstants;
import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.cryptoservices.ICipurseSMApi;
import com.infineon.cipurse.terminallibrary.cryptoservices.ICipurseSMApi.SM_OPTIONS;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.model.DFFileAttributes;
import com.infineon.cipurse.terminallibrary.model.EFFileAttributes;
import com.infineon.cipurse.terminallibrary.model.EncryptionKeyInfo;
import com.infineon.cipurse.terminallibrary.model.FCPInfo;
import com.infineon.cipurse.terminallibrary.model.KeyAttributeInfo;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CREATE_ADF_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CRYPTO_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.SMLevel;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.UPDATEKEY_MODE;

/**
 * Class Implementing APIs for all CBP Commands.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public class CipurseCommandApi implements CipurseCommandsConstants,ICipurseCommandApi{
	/** File selection mode: Selection by file identifier.*/
	public final byte SELECT_FILE_BY_FID = 0x00;

	/** File selection mode: Selection by application identifier [DF name].*/
	public final byte SELECT_FILE_BY_AID = 0x04;

	/** Record reference mode: Read single record.*/
	public final byte READ_RECORD_REFERENCED = 0x04;

	/** Record reference mode: Read multiple records.*/
	public final byte READ_ALL_RECORDS_FROM_REFERENCED = 0x05;

	/** Header for Create command */
	protected byte[] createHeader = new byte[] { 0x00, INS_CREATE_FILE, 0x00, 0x00 };
	/** Header for Format All command */
	protected byte[] formatAllHeader = new byte[] { (byte)0x80, INS_FORMAT_ALL, 0x00, 0x00};
	/** Command header for Select command */
	protected byte[] selectHeader = new byte[] { 0x00, INS_SELECT, 0x00, 0x00 };
	/** Command header for Read File Attribute command */
	protected byte[] readFileAttribHeader = new byte[] { (byte)0x80, INS_READ_FILE_ATTRIB, 0x00, 0x00 };
	/** Command header for Update File Attribute command */
	protected byte[] updateFileAttribHeader = new byte[] { (byte)0x80, INS_UPDATE_FILE_ATTRIB, 0x00, 0x00 };
	/** Command header for Update Key command */
	protected byte[] updateKeyHeader = new byte[] { (byte)0x80, INS_UPDATE_KEY, 0x00, 0x00 };
	/** Command header for Update Key Attributes command */
	protected byte[] updateKeyAttribHeader = new byte[] { (byte)0x80, INS_UPDATE_KEY_ATTRIB, 0x00, 0x00, 0x01, 0x00 };
	/** Command header for Activate File command */
	protected byte[] activateFileHeader = new byte[] { 0x00, INS_ACTIVATE_FILE, 0x00, 0x00 };
	/** Command header for Deactivate File command */
	protected byte[] deactivateFileHeader = new byte[] { 0x00, INS_DEACTIVATE_FILE, 0x00, 0x00 };
	/** Command header for Read Binary command */
	protected byte[] readBinaryHeader = new byte[] { 0x00, INS_READ_BINARY, 0x00, 0x00, 0x00 };
	/** Command header for Update Binary command */
	protected byte[] updateBinaryHeader = new byte[] { 0x00, INS_UPDATE_BINARY, 0x00, 0x00 };
	/** Command header for Read Record command */
	protected byte[] readRecordHeader = new byte[] { 0x00, INS_READ_RECORD, 0x00, 0x00, 0x00 };
	/** Command header for Update Record command */
	protected byte[] updateRecordHeader = new byte[] { 0x00, INS_UPDATE_RECORD, 0x00, 0x00 };
	/** Command header for Append Record command */
	protected byte[] appendRecordHeader = new byte[] { 0x00, INS_APPEND_RECORD, 0x00, 0x00 };
	/** Command header for Read Value command */
	protected byte[] readValueHeader = new byte[] { (byte)0x80, INS_READ_VALUE, 0x00, 0x00, 0x00 };
	/** Command header for Increase Value command */
	protected byte[] increaseValueHeader = new byte[] { (byte)0x80, INS_INCREASE_VALUE, 0x00, 0x00 };
	/** Command header for Decrease Value command */
	protected byte[] decreaseValueHeader = new byte[] { (byte)0x80, INS_DECREASE_VALUE, 0x00, 0x00 };
	/** Command header for Perform Transaction command */
	protected byte[] performTransactionHeader = new byte[] { (byte)0x80, INS_PERFORM_TRANSACTION, 0x00, 0x00 };
	/** Command header for Cancel Transaction command */
	protected byte[] cancelTransactionHeader = new byte[] { (byte)0x80, INS_CANCEL_TRANSACTION, 0x00, 0x00 };

	
	protected byte[] limitedIncreaseValueHeader = new byte[] { (byte)0x80, INS_LIMITED_INCREASE, 0x00, 0x00 };
	protected byte[] limitedDecreaseValueHeader = new byte[] { (byte)0x80, INS_LIMITED_DENCREASE, 0x00, 0x00 };
	

	private ICipurseSMApi cipurseSMApi;

	protected byte[] deleteHeader = new byte[] { 0x00, INS_DELETE_FILE, 0x00, 0x00 };

	private ISessionManager cipurseSessionManager;

	/**
	 * Parameterized Constructor
	 * @param mSessionManager {@link ISessionManager}
	 */
	public CipurseCommandApi(ISessionManager mSessionManager) {
		this.cipurseSessionManager = mSessionManager;
		cipurseSMApi = cipurseSessionManager.getCipurseSMApi();
	}

	@Override
	public ByteArray activateADF() throws CipurseException {

		return new ByteArray(cipurseSMApi.transmit(activateFileHeader));
	}


	@Override
	public ByteArray deactivateADF() throws CipurseException {

		return new ByteArray(cipurseSMApi.transmit(deactivateFileHeader));
	}


	@Override
	public ByteArray readFileAttributes(byte expLen) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"readFileAttributes\" function parameters :\n"
						+ String.format("Expected response length: 0x%02X", expLen));
		return sendCase2Command(readFileAttribHeader, (byte)0x00, (byte)0x00, (byte)expLen);
	}


	@Override
	public ByteArray updateFileAttributes(byte noOfkeys, ByteArray smrArt,
			ByteArray propSecurityAttributes) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"updateFileAttributes\" function parameters :\n"
						+ String.format(
								"Number of keys: 0x%02X%nSMR ART: %s%nProprietary security attributes: %s",
								noOfkeys, smrArt, propSecurityAttributes));
		byte[] updateFileAttributesdata;
		ByteArray smrart = new ByteArray(1);
		smrart.setByte(noOfkeys, 0);
		smrart.append(smrArt);

		if( propSecurityAttributes != null && propSecurityAttributes.size() > 0) {
			updateFileAttributesdata = new byte[smrart.size()+ 2 + propSecurityAttributes.size()];
			updateFileAttributesdata[smrart.size()] = (byte)0x86;
			updateFileAttributesdata[smrart.size()+ 1] = (byte)propSecurityAttributes.size();
			System.arraycopy(propSecurityAttributes.getBytes(),0,updateFileAttributesdata,smrart.size()+2, propSecurityAttributes.size());
		}
		else if (propSecurityAttributes != null && propSecurityAttributes.size() == 0) {
			updateFileAttributesdata = new byte[smrart.size()+ 2];
			updateFileAttributesdata[smrart.size()] = (byte)0x86;
			updateFileAttributesdata[smrart.size()+ 1] = (byte)0x00;
		}
		else {
			updateFileAttributesdata = new byte[smrart.size()];
		}

		System.arraycopy(smrart.getBytes(),0,updateFileAttributesdata,0,smrart.size());
		return sendCase3Command(updateFileAttribHeader, (byte)0x00, (byte)0x00,
				new ByteArray(updateFileAttributesdata));
	}


	@Override
	public ByteArray updateKeyAttributes(byte keyNum,
			byte keySecAttribute) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"updateKeyAttributes\" function parameters :\n"
						+ String.format("Key number: 0x%02X%nKey security attributes: 0x%02X",
								keyNum, keySecAttribute));
		updateKeyAttribHeader[APDU_OFFSET_P2] = keyNum;
		updateKeyAttribHeader[APDU_OFFSET_CDATA] = keySecAttribute;

		return new ByteArray(cipurseSMApi.transmit(updateKeyAttribHeader));
	}

	@Override
	public ByteArray performTransaction() throws CipurseException {

		return new ByteArray(cipurseSMApi.transmit(performTransactionHeader));
	}


	@Override
	public ByteArray cancelTransaction() throws CipurseException {

		return new ByteArray(cipurseSMApi.transmit(cancelTransactionHeader));
	}


	@Override
	public boolean establishSecureChannel(byte authKeyNum,
			KeyDiversificationInfo authKeyInfo) throws CipurseException {

		cipurseSessionManager.getLogger().log("\"establishSecureChannel\" function parameters :\n"
				+ String.format("Authentication key number: 0x%02X%nPpAuthKeyInfo: %s",
						authKeyNum, authKeyInfo));
		if(cipurseSessionManager.getCurrentCryptoMode() == CRYPTO_MODE.HYBRID)
		{
			((SessionManager)cipurseSessionManager).restoreHybridMode();
		}
		return cipurseSMApi.setUpSecureChannel(authKeyNum, authKeyInfo);
	}


	@Override
	public ByteArray selectMF(byte paramP2, short expLength)
			throws CipurseException {
		cipurseSessionManager.getLogger().log("\"selectMF\" function parameters :\n"
				+ String.format("P2 value: 0x%02X%nExpected response length: 0x%02X",
						paramP2, expLength));

		if(expLength < 0)
			return new ByteArray(cipurseSMApi.transmit(selectHeader));
		else
			return sendCase2Command(selectHeader,(byte)0x00, paramP2, (byte)expLength);
	}



	@Override
	public ByteArray selectFileByFID(byte paramP2, int fid,
			short expLength) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"selectFileByFID\" function parameters :\n"
						+ String.format("P2 value: 0x%02X%nFileID: 0x%04X%nExpected response length: 0x%02X", paramP2,
								fid, expLength));
		if(expLength < 0) {
			return sendCase3Command(selectHeader, SELECT_FILE_BY_FID, paramP2, new ByteArray(new byte[]{(byte)((fid>>8) & 0x00FF),(byte) (fid & 0x00FF)}));

		} else {
			return sendCase4Command(selectHeader, SELECT_FILE_BY_FID, paramP2,
					new ByteArray(new byte[]{(byte)((fid>>8) & 0x00FF), (byte) (fid & 0x00FF)}), (byte)expLength);
		}

	}



	@Override
	public ByteArray selectFileByAID(byte p2, ByteArray AID, short expLength
			) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"selectFileByAID\" function parameters :\n"
						+ String.format("P2 value: 0x%02X%nAID: %s%nExpected response length: 0x%02X", p2,
								AID, expLength));
		if(expLength < 0) {
			return sendCase3Command(selectHeader, SELECT_FILE_BY_AID, (byte)0x00, AID);
		} else {
			return sendCase4Command(selectHeader, SELECT_FILE_BY_AID, (byte)p2, AID,(byte)expLength);
		}
	}



	@Override
	public ByteArray readBinary(byte SFID, int offset,
			byte expLength) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"readBinary\" function parameters :\n"
						+ String.format(
								"SFID: 0x%02X%nOffset: 0x%04X\nExpected response length: 0x%02X", SFID,
								offset, expLength));
		if(SFID == 0) {
			return sendCase2Command(readBinaryHeader, (byte)((offset & 0xFF00) >> 0x08),
					(byte)(offset & 0xFF), (byte)expLength);
		} else {
			return sendCase2Command(readBinaryHeader, (byte)(0x80 | SFID), (byte)offset, (byte)expLength);
		}

	}



	@Override
	public ByteArray updateBinary(byte SFID, short offset,
			ByteArray newData) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"updateBinary\" function parameters :\n"
						+ String.format(
								"SFID: 0x%02X%nOffset: 0x%04X%nData to be updated: %s",
								SFID, offset, newData));
		if(SFID == 0x00){
			return sendCase3Command(updateBinaryHeader, (byte)((offset & 0xFF00) >> 0x08),
				(byte)(offset & 0xFF), newData);
		} else {
			return sendCase3Command(updateBinaryHeader, (byte)(0x80 | SFID), (byte)offset, newData);
		}
	}



	@Override
	public ByteArray readRecord(byte SFID, byte recNumber,
			byte readMode, byte expLength) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"readRecord\" function parameters :\n"
						+ String.format(
								"SFID: 0x%02X%nRecord number: 0x%02X%nRead mode: 0x%02X%nExpected response length: 0x%02X",
								SFID, recNumber, readMode, expLength));
		if(SFID == 0x00){
			return sendCase2Command(readRecordHeader, (byte)recNumber,
					(byte)(readMode), (byte)expLength);
		} else {
			return sendCase2Command(readRecordHeader, (byte)recNumber,
					(byte)((SFID << 3)| readMode), (byte)expLength);
		}
	}



	@Override
	public ByteArray updateRecord(byte SFID, byte recNumber,byte updateMode,
			ByteArray newData) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"updateRecord\" function parameters :\n"
						+ String.format(
								"SFID: 0x%02X%nRecord number: 0x%02X%nUpdate mode: 0x%02X%nData to be updated: %s",
								SFID, recNumber, updateMode, newData));
		if(SFID ==0x00) {
			return sendCase3Command(updateRecordHeader, (byte)recNumber,
					updateMode, newData);
		} else {
			return sendCase3Command(updateRecordHeader, (byte)recNumber,
					(byte)((SFID << 3)|updateMode), newData);
		}
	}



	@Override
	public ByteArray appendRecord(byte SFID, ByteArray newData)
			throws CipurseException {
		cipurseSessionManager.getLogger().log("\"appendRecord\" function parameters :\n"
						+ String.format("SFID: 0x%02X%nData to be appended: %s", SFID,
								newData));
		if(SFID ==0x00) {
			return sendCase3Command(appendRecordHeader, (byte)0x00, (byte)0x00, newData);
		} else {
			return sendCase3Command(appendRecordHeader, (byte)0x00, (byte)((SFID << 3)&0xF8), newData);
		}
	}


	@Override
	public ByteArray readValue(byte readMode,byte SFID, byte recNumber,
			byte expLength) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"readValue\" function parameters :\n"
						+ String.format(
								"Read mode: 0x%02X%nSFID: 0x%02X%nRecord number: 0x%02X%nExpected response length: 0x%02X",
								readMode, SFID, recNumber, expLength));
		if(SFID ==0x00) {
			return sendCase2Command(readValueHeader, (byte)recNumber, readMode, (byte)expLength);
		} else {
			return sendCase2Command(readValueHeader, (byte)recNumber,
					(byte)((SFID << 3)|readMode), (byte)expLength);
		}

	}


	@Override
	public ByteArray increaseValue(byte SFID, byte recNumber,
			ByteArray increaseValue, short expLength)
			throws CipurseException {
		cipurseSessionManager.getLogger().log("\"increaseValue\" function parameters :\n"
						+ String.format(
								"SFID: 0x%02X%nRecord number: 0x%02X%nValue to be increased: %s%nExpected response length: 0x%02X",
								SFID, recNumber, increaseValue, expLength));
		byte p1=recNumber;
		byte p2=(byte)((SFID << 3)|READ_RECORD_REFERENCED);

		if(expLength < 0)
			return sendCase3Command(increaseValueHeader, p1, p2, increaseValue);
		else
			return sendCase4Command(increaseValueHeader, p1, p2, increaseValue, (byte)expLength);
	}

	
	@Override
	public ByteArray limitedIncreaseValue(byte SFID, byte recNumber,
			ByteArray refundValue, short expLength)
			throws CipurseException {
		cipurseSessionManager.getLogger().log("\"limitedIncreaseValue\" function parameters :\n"
						+ String.format(
								"SFID: 0x%02X%nRecord number: 0x%02X%nValue to be increased: %s%nExpected response length: 0x%02X",
								SFID, recNumber, refundValue, expLength));

		byte p1=recNumber;
		byte p2=(byte)((SFID << 3)|READ_RECORD_REFERENCED);

		if(expLength < 0)
			return sendCase3Command(limitedIncreaseValueHeader, p1, p2, refundValue);
		else
			return sendCase4Command(limitedIncreaseValueHeader, p1, p2, refundValue, (byte)expLength);

	}
	

	@Override
	public ByteArray decreaseValue(byte SFID, byte recNumber,
			ByteArray decreaseValue, short expLength)
			throws CipurseException {
		cipurseSessionManager.getLogger().log("\"decreaseValue\" function parameters :\n"
						+ String.format(
								"SFID: 0x%02X%nRecord number: 0x%02X%nValue to be decreased: %s%nExpected response length: 0x%02X",
								SFID, recNumber, decreaseValue, expLength));
		byte p1=recNumber;
		byte p2=(byte)((SFID << 3)|READ_RECORD_REFERENCED);

		if(expLength < 0)
			return sendCase3Command(decreaseValueHeader, p1, p2, decreaseValue);
		else
			return sendCase4Command(decreaseValueHeader, p1, p2, decreaseValue, (byte)expLength);
	}

	
	@Override
	public ByteArray limitedDecreaseValue(byte SFID, byte recNumber,
			ByteArray decreaseValue, short expLength)
			throws CipurseException {
		cipurseSessionManager.getLogger().log("\"limitedDecreaseValue\" function parameters :\n"
						+ String.format(
								"SFID: 0x%02X%nRecord number: 0x%02X%nValue to be decreased: %s%nExpected response length: 0x%02X",
								SFID, recNumber, decreaseValue, expLength));
		byte p1=recNumber;
		byte p2=(byte)((SFID << 3)|READ_RECORD_REFERENCED);

		if(expLength < 0)
			return sendCase3Command(limitedDecreaseValueHeader, p1, p2, decreaseValue);
		else
			return sendCase4Command(limitedDecreaseValueHeader, p1, p2, decreaseValue, (byte)expLength);

	}
	

	@Override
	public ByteArray updateKey(UPDATEKEY_MODE updateKeyMode,
			byte keyNo,
			EncryptionKeyInfo encKeyInfo,
			KeyAttributeInfo keyInfo,
			KeyDiversificationInfo keyDivInfo) throws CipurseException {

		cipurseSessionManager.getLogger().log("\"updateKey\" function parameters :\n"
						+ String.format(
								"UpdateKey mode: %s%nKey to be updated: 0x%02X%nKey encryption key info: %s%nKey attributes: %s%nKey diversification info: %s",
								updateKeyMode, keyNo, encKeyInfo, keyInfo,
								keyDivInfo));

		SMLevel origionalSmi = cipurseSessionManager.getSMIValue();
		try
		{
			if(updateKeyMode == UPDATEKEY_MODE.PLAIN || updateKeyMode == UPDATEKEY_MODE.ENC_KEY)
			{
				return prepareAndSendUpdateKey(updateKeyMode, keyNo, encKeyInfo, keyInfo, keyDivInfo);
			}
			else //KEYPERSO_MODE.SM_ENC
			{
				SMLevel tempSMI = SMLevel.eSM_ENC_ENC;//(byte) (CipurseConstant.SM_COMMAND_ENCED|CipurseConstant.SM_RESPONSE_ENCED);
				cipurseSessionManager.setSMIValue(tempSMI);
				byte[] smCommand = buildUpdateKeyCommandFromSAM(keyNo, tempSMI.getSMLevel(), keyInfo, keyDivInfo);
				byte[] resp = cipurseSMApi.transmit(smCommand, SM_OPTIONS.NO_WRAP_CMD);
				return new ByteArray(resp);
			}
		}
		finally
		{
			cipurseSessionManager.setSMIValue(origionalSmi);
		}

	}


	protected byte[] buildUpdateKeyCommandFromSAM(byte keyNo, byte smi_updateKey,
			KeyAttributeInfo keyAttributes,
			KeyDiversificationInfo keyDivdata) throws CipurseException
	{
		//Call buildLoadKey with SMI ENC_ENC
		byte[] lUpdateKeyHeader = Arrays.copyOf(updateKeyHeader, 5);
		lUpdateKeyHeader[APDU_OFFSET_P2]=keyNo;

		byte[] resp = cipurseSMApi.getCryptoAPI().buildUpdateKey(new ByteArray(lUpdateKeyHeader),
				smi_updateKey, keyAttributes, keyDivdata);
		byte[] smCommandData = Utils.extractBytes(resp, 0, resp.length-2);
		if(smCommandData.length==0)
			throw new CipurseException(CipurseConstant.CTL_INVALID_APDU_RESPONSE);
		byte[] smCommand = new byte[lUpdateKeyHeader.length + 1 + smCommandData.length + 1];
		int offset = Utils.arrayCopy(lUpdateKeyHeader, 0, smCommand, 0, lUpdateKeyHeader.length);
		smCommand[offset++]=smi_updateKey;
		Utils.arrayCopy(smCommandData, 0, smCommand, offset, smCommandData.length);

		smCommand[0]=(byte)0x84;
		smCommand[4]=(byte) (smCommandData.length + 1);

		return smCommand;
	}

	protected  ByteArray prepareAndSendUpdateKey(UPDATEKEY_MODE updateKeyMode,
			byte keyNo,
			EncryptionKeyInfo encKeyInfo,
			KeyAttributeInfo keyInfo,
			KeyDiversificationInfo keyDivInfo) throws CipurseException {

		byte[] updateKeyCommand = new byte[APDU_OFFSET_CDATA+3+keyInfo.getKeyLength()+3];

		// Update the command header.
		System.arraycopy(updateKeyHeader, 0, updateKeyCommand, APDU_OFFSET_CLA, updateKeyHeader.length);
		updateKeyCommand[APDU_OFFSET_P2] = keyNo;
		updateKeyCommand[APDU_OFFSET_P3] = (byte)(3+keyInfo.getKeyLength()+3);
		updateKeyCommand[APDU_OFFSET_CDATA] = (byte)keyInfo.getKeyAddInfo();
		updateKeyCommand[APDU_OFFSET_CDATA+1] = (byte)keyInfo.getKeyLength();
		updateKeyCommand[APDU_OFFSET_CDATA+2] = (byte)keyInfo.getKeyAlgoId();

		// Calculate the KVV value and update it in command.
		byte[] keyValueAndKvv = null;

		if(updateKeyMode == UPDATEKEY_MODE.PLAIN)
			keyValueAndKvv = cipurseSMApi.getCryptoAPI().diversifyPlainKeySet(keyDivInfo);
		else if(updateKeyMode == UPDATEKEY_MODE.ENC_KEY)
		{
			if(encKeyInfo!=null && encKeyInfo.getEncKeyNumber() > 0)
			{
				updateKeyCommand[APDU_OFFSET_P1] = (byte)encKeyInfo.getEncKeyNumber();

				keyValueAndKvv = cipurseSMApi.getCryptoAPI().diversifyEncryptedKeySet(encKeyInfo.getEncKeyID(), keyDivInfo);
			}
			else
				throw new CipurseException(CipurseConstant.CTL_INVALID_PARAMETER);
		}

		int offset = APDU_OFFSET_CDATA+3;
		// Copy Key value in to the byte array.
		offset = Utils.arrayCopy(keyValueAndKvv, 0, updateKeyCommand, offset, keyInfo.getKeyLength());
		if(keyInfo.getKvv()!=null)
			offset=Utils.arrayCopy(keyInfo.getKvv(), 0, updateKeyCommand, offset, keyInfo.getKvv().length);
		else if(keyValueAndKvv != null)
			offset=Utils.arrayCopy(keyValueAndKvv, keyInfo.getKeyLength(), updateKeyCommand, offset, keyValueAndKvv.length - keyInfo.getKeyLength());
		else
			throw new CipurseException(CipurseConstant.CTL_KEY_NOT_FOUND);

		return new ByteArray(cipurseSMApi.transmit(updateKeyCommand));
	}


	@Override
	public ByteArray createADF(CREATE_ADF_MODE mode,
			 DFFileAttributes objDFFileAttributes,
			 KeyAttributeInfo[] keyAttributes,
			 EncryptionKeyInfo encKeyInfo,
			 KeyDiversificationInfo[] keyDivInfo,
			 byte noOfKeysToLoad) throws CipurseException {

		cipurseSessionManager.getLogger().log("\"createADF\" function parameters :\n"
						+ String.format(
								"CreateADF mode: %s%nDF file attributes: %s%nKey attributes: %s%nKey encryption key info: %s%nKey diversification info: %s%nNumber of keys to load: %s",
								mode, objDFFileAttributes, Arrays.toString(keyAttributes),
								encKeyInfo, Arrays.toString(keyDivInfo), noOfKeysToLoad));

		SMLevel origionalSmi = cipurseSessionManager.getSMIValue();
		try
		{
			if(mode == CREATE_ADF_MODE.PLAIN || mode == CREATE_ADF_MODE.ENC_KEY)
			{
				//As of now from SAM Card, load key does not support Plain Key loading with SMI != ENC_XX.
				//In case Plain key load requested, we still proceed with default SMI. Card may reject this command.
				//User has to explicitly set SMI = ENC_XX
				return prepareAndSendCreateADF((byte)(mode == CREATE_ADF_MODE.ENC_KEY?1:0),objDFFileAttributes,
						keyAttributes,encKeyInfo,keyDivInfo,noOfKeysToLoad);

			} else //KEYPERSO_MODE.SM_ENC
			{
				SMLevel tempSMI = SMLevel.eSM_ENC_ENC;//(byte) (CipurseConstant.SM_COMMAND_ENCED|CipurseConstant.SM_RESPONSE_ENCED);
				cipurseSessionManager.setSMIValue(tempSMI);
				byte[] smCommand = buildCreateADFCommandFromSAM(3,tempSMI.getSMLevel(), objDFFileAttributes, keyDivInfo, encKeyInfo, keyAttributes, noOfKeysToLoad);
				byte[] resp = cipurseSMApi.transmit(smCommand, SM_OPTIONS.NO_WRAP_CMD);
				return new ByteArray(resp);

			}
		}
		finally
		{
			cipurseSessionManager.setSMIValue(origionalSmi);
		}
	}

	private ByteArray prepareAndSendCreateADF(byte b,
			DFFileAttributes objDFFileAttributes,
			KeyAttributeInfo[] keyAttributes, EncryptionKeyInfo encKeyInfo,
			KeyDiversificationInfo[] keyDiffInfo, byte noOfKeysToLoad) throws CipurseException {
		byte[] fileAttributes = objDFFileAttributes.getFileAttributes();
		FCPInfo fcpInfo = objDFFileAttributes.getFcpInfo();
		short keyObjLen = 0x00;
		int keyNum;

		if(objDFFileAttributes.getNumOfKeys() > 0)
		{
			keyObjLen = (short) ((noOfKeysToLoad * KeyAttributeInfo.KEY_OBJECT_SIZE) & 0x00FF);
		}

		int cmdLength = (APDU_OFFSET_CDATA + fileAttributes.length + 1 + (keyAttributes.length * KeyAttributeInfo.KEY_SET_SIZE)+fcpInfo.getFCPBytes().length)  + keyObjLen + 5;
		
		cmdLength  = cmdLength + objDFFileAttributes.getTagC8().length;
		
		if(keyObjLen==0)
			cmdLength=cmdLength-3;
		byte[] createFile = new byte[cmdLength];

		// Set the Lc.
		int lc=createFile.length-APDU_OFFSET_CDATA;
		if(lc > 255)
			throw new CipurseException(CipurseConstant.CTL_LC_MORE_THAN_MAX);
		createFile[APDU_OFFSET_P3] = (byte)(lc);

		//set the Tag 9200
		createFile[APDU_OFFSET_P3+1] = (byte)0x92;
		createFile[APDU_OFFSET_P3+2] = (byte)0x00;
		//set the length of Tag 9200

		byte tag9200Len = (byte) (fileAttributes.length +1+ (keyAttributes.length * KeyAttributeInfo.KEY_SET_SIZE)+fcpInfo.getFCPBytes().length -1 );
		
		tag9200Len +=  (byte) (objDFFileAttributes.getTagC8().length );
		
		createFile[APDU_OFFSET_P3+3] = tag9200Len;

		// Format the create command.
		System.arraycopy(createHeader, 0, createFile, 0, createHeader.length);
		createFile[APDU_OFFSET_P1] = 0;
		createFile[APDU_OFFSET_P2] = 0;
		if(encKeyInfo !=null)
			createFile[APDU_OFFSET_P1] = encKeyInfo.getEncKeyNumber();

		//set the encryption key no.
		System.arraycopy(fileAttributes, 0, createFile, APDU_OFFSET_CDATA+3, fileAttributes.length);

		// Get key set information.
		for (keyNum=0; keyNum<objDFFileAttributes.getNumOfKeys(); keyNum++)
		{
			System.arraycopy(keyAttributes[keyNum].getKeySetInBytes(), 0,
					createFile, APDU_OFFSET_CDATA+3+fileAttributes.length+(keyNum * KeyAttributeInfo.KEY_SET_SIZE),
					KeyAttributeInfo.KEY_SET_SIZE);
		}

		// Copy the Application identifier.
		int offset =Utils.arrayCopy(fcpInfo.getFCPBytes(), 0, createFile, (APDU_OFFSET_CDATA + 3 + fileAttributes.length + (keyAttributes.length * KeyAttributeInfo.KEY_SET_SIZE)), fcpInfo.getFCPBytes().length);
		
		offset =Utils.arrayCopy(objDFFileAttributes.getTagC8(), 0, createFile, offset , objDFFileAttributes.getTagC8().length);
		

		//Reuse keyObjLen to store the offset to KeyObject Tag
		int keyObjoff = offset;

		if(noOfKeysToLoad > 0 && keyObjLen > 0){
			//set Key Object Tag A00F
			createFile[keyObjoff] = (byte)0xA0;
			createFile[keyObjoff+1] = (byte)0x0F;
			//set Tag length
			createFile[keyObjoff+2] = (byte)keyObjLen;

			// Get key value, KVV, key add information.
			for (keyNum=0; keyNum<noOfKeysToLoad; keyNum++)
			{
				System.arraycopy(getKeyInfoDataInBytes(cipurseSMApi, keyAttributes[keyNum], encKeyInfo, keyDiffInfo[keyNum]), 0,
						createFile, keyObjoff + 3 + (keyNum*KeyAttributeInfo.KEY_OBJECT_SIZE),
						KeyAttributeInfo.KEY_OBJECT_SIZE);
			}
		}

		return new ByteArray(cipurseSMApi.transmit(createFile));
	}

	protected byte[] buildCreateADFCommandFromSAM(int cipurseVer,byte smi, DFFileAttributes dfAttributes,
			KeyDiversificationInfo[] keyDivInfo, EncryptionKeyInfo encKeyInfo, KeyAttributeInfo[] psKeyAttributeInfo, int noOfKeys2Perso) throws CipurseException
	{
		//Call buildLoadKey with SMI ENC_ENC
		ByteArray CREATE_HEADER = new ByteArray("00 E0 00 00 00");

		byte[] resp = cipurseSMApi.getCryptoAPI().buildCreateADF(CREATE_HEADER,cipurseVer, smi,
				dfAttributes,keyDivInfo,encKeyInfo,
				psKeyAttributeInfo,noOfKeys2Perso);
		byte[] smCommandData = Utils.extractBytes(resp, 0, resp.length-2);
		if(smCommandData.length==0)
			throw new CipurseException(CipurseConstant.CTL_INVALID_APDU_RESPONSE);
		byte[] smCommand = new byte[CREATE_HEADER.size() + 1 + smCommandData.length + 1];
		int offset = Utils.arrayCopy(CREATE_HEADER.getBytes(), 0, smCommand, 0, CREATE_HEADER.size());
		smCommand[offset++]=smi;
		Utils.arrayCopy(smCommandData, 0, smCommand, offset, smCommandData.length);

		smCommand[0]=(byte)0x04;
		smCommand[4]=(byte) (smCommandData.length + 1);

		return smCommand;
	}


	@Override
	public ByteArray createElementaryFile(EFFileAttributes objEFFileAttributes)throws CipurseException{
		cipurseSessionManager.getLogger().log("\"createElementaryFile\" function parameters :\n"
						+ String.format("EF file attributes: %s", objEFFileAttributes));

		if(objEFFileAttributes.getSmr() !=null && objEFFileAttributes.getArt() !=null ){
			return createElementaryFile(objEFFileAttributes, objEFFileAttributes.getSmr(),objEFFileAttributes.getArt());
		} else {
			byte[] fileAttributes = getEFFileAttributes(objEFFileAttributes);
			byte[] createFile = new byte[APDU_OFFSET_CDATA + fileAttributes.length+3]; //Tag 9201 + L = 3 bytes

			// Set the Lc.
			createFile[APDU_OFFSET_P3] = (byte)(fileAttributes.length + 3);
			//Set the Tag 9201
			createFile[APDU_OFFSET_P3+1] = (byte)(0x92);
			createFile[APDU_OFFSET_P3+2] = (byte)(0x01);
			//set the Length of Tag 9201
			createFile[APDU_OFFSET_P3+3] = (byte)(fileAttributes.length);

			// Format the create command.
			System.arraycopy(createHeader, 0, createFile, 0, createHeader.length);
			System.arraycopy(fileAttributes, 0, createFile, APDU_OFFSET_CDATA + 3, fileAttributes.length);
			return new ByteArray(cipurseSMApi.transmit(createFile));
		}
	}

	protected ByteArray createElementaryFile(EFFileAttributes objEFFileAttributes,byte[] smr,byte[] art)
			throws CipurseException {
		byte[] fileAttributes = getEFFileAttributes(objEFFileAttributes);
		byte[] efSecurityAttrib = new byte[1];
		efSecurityAttrib[0] = (byte) objEFFileAttributes.getNumOfKeys();
		efSecurityAttrib = Utils.concat(efSecurityAttrib, smr);
		efSecurityAttrib= Utils.concat(efSecurityAttrib, art);

		int cmdDataLen = fileAttributes.length + efSecurityAttrib.length + 3;
		
		cmdDataLen += objEFFileAttributes.getTagC8().length;
		
		byte[] createFile = new byte[APDU_OFFSET_CDATA + cmdDataLen]; //Tag 9201 +L = 3 bytes

		// Set the Lc.
		createFile[APDU_OFFSET_P3] = (byte) cmdDataLen;

		//Set the Tag 9201
		createFile[APDU_OFFSET_P3+1] = (byte)(0x92);
		createFile[APDU_OFFSET_P3+2] = (byte)(0x01);

		//set the Length of Tag 9201
		createFile[APDU_OFFSET_P3+3] = (byte) (cmdDataLen-3);

		// Format the create command.
		System.arraycopy(createHeader, 0, createFile, 0, createHeader.length);
		System.arraycopy(fileAttributes, 0, createFile, APDU_OFFSET_CDATA+3, fileAttributes.length);
		if(objEFFileAttributes.getNumOfKeys() != 0)
			efSecurityAttrib[0] = (byte)objEFFileAttributes.getNumOfKeys();
		
		int offset =
		
		Utils.arrayCopy(efSecurityAttrib, 0, createFile, APDU_OFFSET_CDATA+fileAttributes.length+3, efSecurityAttrib.length);

		
		System.arraycopy(objEFFileAttributes.getTagC8(), 0, createFile, offset, objEFFileAttributes.getTagC8().length);
		

		return new ByteArray(cipurseSMApi.transmit(createFile));
	}

	@Override
	public ByteArray deleteFile(ByteArray deleteAID)
			throws CipurseException {
		cipurseSessionManager.getLogger().log("\"deleteFile\" function parameters :\n"
						+ String.format("AID: %s", deleteAID));

		byte[] deleteFile=null;
		if(deleteAID!=null && deleteAID.size() > 0)
		{
			deleteFile = new byte[APDU_OFFSET_CDATA + deleteAID.size()];
			System.arraycopy(deleteHeader, 0, deleteFile, 0, deleteHeader.length);
			deleteFile[APDU_OFFSET_P1]=0x04;
			System.arraycopy(deleteAID.getBytes(), 0, deleteFile, APDU_OFFSET_CDATA, deleteAID.size());
			deleteFile[APDU_OFFSET_P3]=(byte) deleteAID.size();
		}
		else
		{
			deleteFile = new byte[deleteHeader.length];
			System.arraycopy(deleteHeader, 0, deleteFile, 0, deleteHeader.length);
		}


		return new ByteArray(cipurseSMApi.transmit(deleteFile));
	}


	/**
	 *  Method to delete a file using the file identifier.
	 *  The MF cannot be deleted.
	 *
	 * @param FID File id of the file to be deleted.
	 * @return ByteArray Response status
	 */
	@Override
	public ByteArray deleteFilebyFID(int FID) throws CipurseException {
		cipurseSessionManager.getLogger().log("\"deleteFilebyFID\" function parameters :\n"
						+ String.format("FileID: 0x%04X", FID));
		byte[] deleteFile = new byte[APDU_OFFSET_CDATA + 2]; //File Id is 2 bytes
		System.arraycopy(deleteHeader, 0, deleteFile, 0, deleteHeader.length);

		// Set the Lc value.
		deleteFile[APDU_OFFSET_P3] = (byte)0x02;
		//set the command data to File ID
		deleteFile[APDU_OFFSET_P3+1] = (byte)((FID >> 8) & 0x00FF);
		deleteFile[APDU_OFFSET_P3+2] = (byte)(FID & 0x00FF);

		return new ByteArray(cipurseSMApi.transmit(deleteFile));

	}

	@Override
	public ByteArray formatAll(ByteArray confirmationData)
			throws CipurseException {
		cipurseSessionManager.getLogger().log("\"formatAll\" function parameters :\n"
						+ String.format("formatAllHearder: %s",
								confirmationData));

		byte[] cmdData = new byte[formatAllHeader.length];
		if(confirmationData != null) {
			cmdData = new byte[formatAllHeader.length + confirmationData.size()+1];
			System.arraycopy(confirmationData.getBytes(), 0, cmdData, APDU_OFFSET_CDATA, confirmationData.size());
			cmdData[APDU_OFFSET_P3] = (byte) confirmationData.size();
		}
		System.arraycopy(formatAllHeader, 0, cmdData, 0, formatAllHeader.length);
		return new ByteArray(cipurseSMApi.transmit(cmdData));
	}

	/**
	 * Method to receive the mandatory EF file attributes.
	 *
	 * @return EF file mandatory attributes in byte array format.
	 */
	protected byte[] getEFFileAttributes(EFFileAttributes objEFFileAttributes){
		byte[] efAttributes = new byte[6];
		efAttributes[0] = (byte)objEFFileAttributes.getFileDescriptor();
		efAttributes[1] = (byte)objEFFileAttributes.getSFID();
		efAttributes[2] = (byte)((objEFFileAttributes.getFileID() >> 0x08) & 0x00FF);
		efAttributes[3] = (byte)(objEFFileAttributes.getFileID() & 0x00FF);

		//set File data attributes
		byte[] fileSizeAttrib = objEFFileAttributes.getFileAttributes();
		efAttributes[4] = fileSizeAttrib[0];
		efAttributes[5] = fileSizeAttrib[1];
		return efAttributes;
	}


	/**
	 * Returns the key information(Key[16 bytes]+Key Add Info[1 byte] + KVV[3 Bytes]) in byte[] format.
	 * This method internally calculates KVV value and encrypts the key value in the key info in case
	 * the provided encryption key value is valid.
	 *
	 * @param cipurseSMApi Object to the Cipurse sm handler to be used for KVV calculation and encryption.
	 * @param keyInfo Key AttributeInfo.
	 * @param  encryptionKeyInfo Key value to encrypt the key value in Key sets.
	 * @param  keyDivInfo KeyDiversificationInfo
	 * @return Key info bytes
	 * @throws CipurseException In case of any error while processing
	 */
	private byte[] getKeyInfoDataInBytes(ICipurseSMApi cipurseSMApi, KeyAttributeInfo keyInfo, EncryptionKeyInfo  encryptionKeyInfo,KeyDiversificationInfo keyDivInfo) throws CipurseException
	{
		byte[] keyInfoData = new byte[KeyAttributeInfo.KEY_OBJECT_SIZE];// 4+16+3

		byte[] keyValueAndKvv = null;
		if(encryptionKeyInfo!=null && encryptionKeyInfo.getEncKeyNumber() > 0)
			keyValueAndKvv = cipurseSMApi.getCryptoAPI().diversifyEncryptedKeySet(encryptionKeyInfo.getEncKeyID(), keyDivInfo);
		else
			keyValueAndKvv = cipurseSMApi.getCryptoAPI().diversifyPlainKeySet(keyDivInfo);

		// Copy Key value in to the byte array.
		int offset = Utils.arrayCopy(keyValueAndKvv, 0, keyInfoData, 0, keyInfo.getKeyLength());
		keyInfoData[offset] = (byte)keyInfo.getKeyAddInfo();
		if(keyInfo.getKvv()!=null)
			Utils.arrayCopy(keyInfo.getKvv(), 0, keyInfoData, offset+1, keyInfo.getKvv().length);
		else
			Utils.arrayCopy(keyValueAndKvv, keyInfo.getKeyLength(), keyInfoData, offset+1, keyValueAndKvv.length-keyInfo.getKeyLength());
		return keyInfoData;
	}

	/**
	 * Method to send case 2 command, used internally.
	 *
	 * @param commandHeader Command header in byte array format.
	 * @param P1 Parameter 1.
	 * @param P2 Parameter 2.
	 * @param P3 Expected length.
	 * @return Response data in ByteArray format.
	 * @throws CipurseException
	 */
	protected ByteArray sendCase2Command(byte[] commandHeader, byte P1, byte P2, byte P3) throws CipurseException{
		byte[] commandApdu = new byte[APDU_OFFSET_CDATA];

		// Frame the update binary command.
		System.arraycopy(commandHeader, 0, commandApdu, 0, APDU_OFFSET_P3);

		commandApdu[APDU_OFFSET_P1] = P1;
		commandApdu[APDU_OFFSET_P2] = P2;
		commandApdu[APDU_OFFSET_P3] = P3;

		return new ByteArray(cipurseSMApi.transmit(commandApdu));
	}
	/**
	 * Method to send case 3 command, used internally.
	 *
	 * @param commandHeader 4 bytes command header in byte array format.
	 * @param p1 Parameter 1.
	 * @param p2 Parameter 2.
	 * @param commandData Command data field in ByteArray format.
	 * @return Response data in ByteArray format.
	 * @throws CipurseException
	 */
	protected ByteArray sendCase3Command(byte[] commandHeader, byte p1, byte p2, ByteArray commandData) throws CipurseException{

		int commandDataLength = ((commandData == null)? 0x00: commandData.getBytes().length);
		byte[] commandApdu = new byte[APDU_OFFSET_CDATA + commandDataLength];

		// Frame the update binary command.
		System.arraycopy(commandHeader, 0, commandApdu, 0, APDU_OFFSET_P3);

		commandApdu[APDU_OFFSET_P1] = p1;
		commandApdu[APDU_OFFSET_P2] = p2;
		commandApdu[APDU_OFFSET_P3] = (byte)(commandApdu.length - APDU_OFFSET_CDATA);

		// Frame the update binary command.
		if(commandData != null)
		{
			System.arraycopy(commandData.getBytes(), 0, commandApdu, APDU_OFFSET_CDATA,
					commandData.size());
		}

		return new ByteArray(cipurseSMApi.transmit(commandApdu));
	}

	/**
	 * Method to send case 4 command, used internally.
	 *
	 * @param commandHeader 4 bytes command header in byte array format.
	 * @param P1 Parameter 1.
	 * @param P2 Parameter 2.
	 * @param commandData Command data field in ByteArray format.
	 * @param expLen Expected length.
	 * @return Response data in ByteArray format.
	 * @throws CipurseException
	 */
	protected ByteArray sendCase4Command(byte[] commandHeader, byte P1, byte P2, ByteArray commandData, byte expLen) throws CipurseException{
		byte[] commandApdu = new byte[APDU_OFFSET_CDATA + commandData.getBytes().length+1]; // +1 is for Le.

		// Frame the update binary command.
		System.arraycopy(commandHeader, 0, commandApdu, 0, APDU_OFFSET_P3);

		commandApdu[APDU_OFFSET_P1] = P1;
		commandApdu[APDU_OFFSET_P2] = P2;
		commandApdu[APDU_OFFSET_P3] = (byte)(commandApdu.length - APDU_OFFSET_CDATA - 1);

		// Frame the update binary command.
		System.arraycopy(commandData.getBytes(), 0, commandApdu, APDU_OFFSET_CDATA,
				commandData.getBytes().length);
		commandApdu[APDU_OFFSET_CDATA + commandData.getBytes().length] = expLen;

		return new ByteArray(cipurseSMApi.transmit(commandApdu));
	}

}
