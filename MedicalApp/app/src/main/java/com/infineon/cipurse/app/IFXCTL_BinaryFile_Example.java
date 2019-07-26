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

package com.infineon.cipurse.app;

import android.content.Context;
import android.nfc.tech.IsoDep;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.model.DFFileAttributes;
import com.infineon.cipurse.terminallibrary.model.FCPInfo;
import com.infineon.cipurse.terminallibrary.model.KeyAttributeInfo;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.pal.Logger;
import com.infineon.cipurse.terminallibrary.presentation.CipurseCommandApi;
import com.infineon.cipurse.terminallibrary.presentation.ICipurseCommandApi;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CREATE_ADF_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CRYPTO_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.SMLevel;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.SMTYPE;
import com.infineon.cipurse.terminallibrary.presentation.SessionManager;

/**
* This is used for demonstrate the Binary File Operations
* 
* @since 1.0.0
* @version 1.0.1
*/
public class IFXCTL_BinaryFile_Example {

	private ISessionManager 			sessionMgr 				= null; 
	private ILogger 					logger					= null;
	private ICipurseCommandApi 			cmdApi					= null;
		
	private CRYPTO_MODE 				mode					= null;
	public ByteArray 					response 				= null;
	public boolean 						isCTMSupported 			= true;
	private IsoDep						cbpReader				= null;
	private boolean						useHwSAM				= false;
	
	
	//********************************	Common Data	****************************************//
	public static final byte[] 			EF_SMR 					= new byte[] {0x00, 0x00}; /* SMR :minimum MAC MAC for update,Plain MAC for read */
	public static final byte[] 			EF_ART 					= new byte[] {(byte)0xFF, (byte)0xFF, 0x00};	/* ART: Key1 for update and read ,key2:read */
	public static final byte[] 			EF_UPDATED_SMR_ART		= new byte[] {0x00, 0x00, (byte)0xFF, (byte)0xFF, 0x01}; /* SMR :minimum MAC MAC for update,Plain MAC for read */
																											/* ART: Key1 for update and read ,key2:read */
	public static final byte 			NO_DIVERSIFICATION_MODE = 0x00;
	public short 						efUpdateOffset			= 0x00;
		
	//********************************	ADF PROPERTIES	****************************************//
	// public static final byte[] 			BIN_ADF_AID 			= new byte[] {(byte) 0xA0, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04};
	public static final byte[] 			BIN_ADF_AID 			= new byte[] {0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88};
	public static final int 			BIN_ADF_FID				= 0x3301;
	public static final byte		    BIN_SFID		        = 0x01;
	public static final byte 			PUIi1_P2				= 0x00;
	public byte 						binADFAppProfile 		= 0x00;
	public static final byte 			BIN_ADF_NBRK 			= 0x03;
	public static final short 			BIN_ADF_NUM_OF_EFS 		= 0x03;
	public static final byte 			BIN_ADF_NUM_OF_SFIDS 	= 0x03;
	public static final byte[] 			BIN_ADF_SMR				= new byte[] {0x00, 0x00};
	public static final byte[] 			BIN_ADF_ART 			= new byte[] {(byte) 0xFF, (byte)0xFF, (byte)0xFF};
	public static final byte 			REG_AUTO_SEL_PXSE		= 0x03;
	
	//********************************	ADF KEYS INFO	****************************************//
	public static final byte 			UPDATEKEY_BIN			= 0x01;
	public static final byte 			READKEY_BIN 			= 0x02;
	public static final int[] 			KEY_IDS 				= new int[] {0x0003, 0x0004};
	
	
	public static final byte 		KEY_ADDITIONAL_INFO			= 0x01;
	public static final byte 		KEY_LENGTH 					= 0x10;
	public static final byte 		KEY_ALGO_ID 				= 0x09;
	public static final byte[] 		KEY_SEC_ATTRS 				= new byte[] {(short)0x02, (short)0x02, (short)0x10, (short)0x09};
		
	// KEYVAULT KEYS INFO
	public static final byte 		VAULT_UPDATEKEY_BIN			= 0x01;
	public static final byte 		VAULT_READKEY_BIN 			= 0x01;
	

	//********************************	EF.CARDIDENTIFICATION FILE data****************************************//
	public byte 					efBinaryFD					= 0x11;
	public static final int 		EF_BINARY_FILE1_FID			= 0x3301;
	public static final byte		EF_BINARY_FILE1_SFID		= 0x01;
	public static final byte  		EF_CYCLIC_SFID				= 0x03;
	public static final int 		EF_BINARY_FILE1_SIZE		= 200;

	public static final int 	EF_BINARY_FILE1_READUPDATE_SIZE	= 200;
	public static final byte 	EF_CYCLIC_READ_MUL_REC_SIZE = (byte)0xC8;
	public static final byte	EF_CYCLIC__READ_RECORD_NUM = 0x01;
	public static final byte[] 	EF_BINARY_FILE1_UPDATE_DATA 	= new byte[EF_BINARY_FILE1_READUPDATE_SIZE];
	
	
	
	public IFXCTL_BinaryFile_Example(int mode, IsoDep cbpReaderNfc, boolean enableHwSAM) throws IOException, CipurseException{

		this.mode 		= CRYPTO_MODE.getMode(mode);
		this.cbpReader  = cbpReaderNfc;
		this.useHwSAM 	= enableHwSAM;
		sessionMgr 		= new SessionManager();
		logger 			= new Logger();
	}
	
	/**
	 * Steps involved in personalization of binary files:-
	 * > Select MF
	 * > Select EF.ID_INFO
	 * > Read binary file EF.ID_INFO to get profile type
	 * > Create ADF PxSE 
	 * > Select MF
	 * > Create CIPURSE ADF with 'Automatic selection Indicator' value set 
	 * > Create EF.BINARY_FILE1 file
	 * > Update Binary on EF.BINARY_FILE1 file
	 * > Update file attribute to change the security rights
	 * > [Conditional] Perform transaction
	 */
	/**
	* Interface used to implement the personalization of binary files
	*/
	public void binaryFilePersonalization() throws CipurseException{
		
		logger.log("\n----------- Binary Files Personalization Begins --------------\n\n");
		
		byte EF_ID_INFO_OFFSET			= 0x03;
		byte EF_ID_INFO_LENGTH			= 0x01;
		byte[] EF_BINFILE1_DATA 		= new byte[]{ 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
											0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
											0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
											0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
											0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
											0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
											0x03, 0x04, 0x05, 0x06}; /*Data */
		int EF_BINFILE1_READUPDATE_SIZE = EF_BINFILE1_DATA.length;
		
		IFXCTL_ExampleUtils.initializeTargets(sessionMgr, mode, SMTYPE.CIPURSE, cbpReader, useHwSAM, logger);
		
		if(mode != CRYPTO_MODE.SW_SAM){
			response = sessionMgr.authorizeHWSAM(IFXCTL_ExampleUtils.FIELD_SAM_ADF_AID, IFXCTL_ExampleUtils.getSAMPassword());
			IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		}
		
		cmdApi = new CipurseCommandApi(sessionMgr);
		
		logger.log("\n----------- 1) Select MF --------------");
		response = cmdApi.selectFileByFID((byte) 0x00, IFXCTL_ExampleUtils.MF_FILEID, (short) 0x00);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		logger.log("\n----------- 2) Select EF.ID_INFO --------------");
		response = cmdApi.selectFileByFID((byte) 0x00, IFXCTL_ExampleUtils.EF_ID_INFO, (short) IFXCTL_ExampleUtils.LE_NOT_REQUIRED);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		logger.log("\n----------- 3) Read binary file EF.ID_INFO to get profile type --------------");
		ByteArray fileContent 	= cmdApi.readBinary((byte) 0x00, EF_ID_INFO_OFFSET, (byte) EF_ID_INFO_LENGTH);
		IFXCTL_ExampleUtils.errorHandler(logger, fileContent, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		byte[] profileSupport 	= Utils.extractBytes(Utils.toBytes(fileContent), 0, 1);
		isCTMSupported 			= (profileSupport[0] & IFXCTL_ExampleUtils.PROFILE_T)==IFXCTL_ExampleUtils.PROFILE_T; 
		binADFAppProfile		= (isCTMSupported)?IFXCTL_ExampleUtils.CIP_T_APP:IFXCTL_ExampleUtils.CIP_S_APP;	
		efBinaryFD				= (byte) (isCTMSupported?efBinaryFD:(efBinaryFD & 0x0F));
				
		logger.log("\n----------- 5) Select MF --------------");
		response = cmdApi.selectFileByFID((byte) 0x00, IFXCTL_ExampleUtils.MF_FILEID, (short) 0x00);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		logger.log("\n----------- 6) Create ADF to demonstrate binary file operations with 'Automatic selection Indicator' value set  --------------");
		sessionMgr.setSMIValue(SMLevel.eSM_PLAIN_ENC);	// SM_PLAIN_ENC
		binADFAppProfile |= REG_AUTO_SEL_PXSE;
		response = createBinaryADF();
		
		if(response.equals(IFXCTL_ExampleUtils.ADF_EXISTS_STATUS))
		{
			logger.log("-----------   CIPURSE ADF is already present    ------------");
			logger.log("---------------------------------------------------------------");
			throw new CipurseException(IFXCTL_ExampleUtils.ADF_EXISTS_ERROR_MSG);
		}
				
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		logger.log("\n----------- 7) Create EF.BINARY_FILE1 file --------------");
		response  = IFXCTL_ExampleUtils.createBinaryEF(cmdApi, efBinaryFD, EF_BINARY_FILE1_FID, EF_BINARY_FILE1_SIZE, EF_SMR, EF_ART, EF_BINARY_FILE1_SFID, BIN_ADF_NBRK);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		logger.log("\n----------- 8) Update Binary on EF.BINARY_FILE1 file --------------");
		int modulus		= EF_BINARY_FILE1_SIZE % EF_BINFILE1_READUPDATE_SIZE;
		int loopCounter = (EF_BINARY_FILE1_SIZE / EF_BINFILE1_READUPDATE_SIZE)+(modulus>0?1:0);
		for (int i=0; i<loopCounter; i++){
			
			efUpdateOffset  = (short) (EF_BINFILE1_READUPDATE_SIZE * i);
			int len			= EF_BINFILE1_READUPDATE_SIZE;
			if (efUpdateOffset+EF_BINFILE1_READUPDATE_SIZE > EF_BINARY_FILE1_SIZE)
				len = EF_BINFILE1_READUPDATE_SIZE - ((efUpdateOffset+EF_BINFILE1_READUPDATE_SIZE) - EF_BINARY_FILE1_SIZE);
			
			byte[] newData = Utils.extractBytes(EF_BINFILE1_DATA, 0, len);
			logger.log("\nreading from offset - " + efUpdateOffset +" and len is -" + len);
			
			response = cmdApi.updateBinary(IFXCTL_ExampleUtils.CURRENT_SEL_FILE, (short) efUpdateOffset, new ByteArray(newData));
					
			IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		}
		
		logger.log("\n----------- 9) Update file attribute to change the security rights --------------");
		cmdApi.updateFileAttributes(BIN_ADF_NBRK, new ByteArray(EF_UPDATED_SMR_ART), null);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		if (isCTMSupported){
			logger.log("\n----------- 10) [Conditional] Perform transaction  --------------");
			response = cmdApi.performTransaction();
			IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		}
		
		logger.log("\n----------- Binary Files Personalization executed successfully --------------\n\n");
	}
	
	/**
	 * Steps involved in the UpdateBinaryfileOperation :- 
	 *  > Select ADF PxSE 
	 *	> Select EF.ID_INFO
	 *	> Read binary file EF.ID_INFO to get Profile type
	 *	> Perform the authentication with the update Key
	 *	> Set SMI value to SM_ENC_MAC
	 *	> [Conditional] Switch to SWSAM(hybrid mode)
	 *  > Update entire EF.BINARY_FILE1 with SFID 
	 *	> [Conditional]Perform Transaction 
	 */
	/**
	* Interface used to update binary file operation.
	*/
	public void updateBinaryfileOp() throws CipurseException{
		
		logger.log("\n----------- Update Binary File Operation Begins --------------\n\n");
		
		byte EF_ID_INFO_OFFSET			= 0x03;
		byte EF_ID_INFO_LENGTH			= 0x01;
		
		IFXCTL_ExampleUtils.initializeTargets(sessionMgr, mode, SMTYPE.CIPURSE, cbpReader, useHwSAM, logger);
		
		if(mode != CRYPTO_MODE.SW_SAM){
			response = sessionMgr.authorizeHWSAM(IFXCTL_ExampleUtils.FIELD_SAM_ADF_AID, IFXCTL_ExampleUtils.getSAMPassword());
			IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		}
		
		cmdApi = new CipurseCommandApi(sessionMgr);
		
		Arrays.fill(EF_BINARY_FILE1_UPDATE_DATA, (byte) 0x33);
		
		
		logger.log("\n----------- 1)	Select ADF PxSE  --------------");
		response = cmdApi.selectFileByAID(PUIi1_P2, new ByteArray(BIN_ADF_AID), IFXCTL_ExampleUtils.LE_NOT_REQUIRED);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		logger.log("\n----------- 2)	Select EF.ID_INFO --------------");
		response = cmdApi.selectFileByFID((byte) 0x00, IFXCTL_ExampleUtils.EF_ID_INFO, IFXCTL_ExampleUtils.LE_NOT_REQUIRED);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		logger.log("\n----------- 3)	Read binary file EF.ID_INFO to get profile type --------------");
		sessionMgr.setSMIValue(SMLevel.eSM_PLAIN_ENC);
		ByteArray fileContent 	= cmdApi.readBinary((byte) 0x00, EF_ID_INFO_OFFSET, (byte) EF_ID_INFO_LENGTH);
		IFXCTL_ExampleUtils.errorHandler(logger, fileContent, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		
		byte[] profileSupport 	= Utils.extractBytes(Utils.toBytes(fileContent), 0, 1);
		isCTMSupported 			= (profileSupport[0] & IFXCTL_ExampleUtils.PROFILE_T)==IFXCTL_ExampleUtils.PROFILE_T; 
		binADFAppProfile		= (isCTMSupported)?IFXCTL_ExampleUtils.CIP_T_APP:IFXCTL_ExampleUtils.CIP_S_APP;	
		
		logger.log("\n----------- 4)	Perform the authentication with the Update Key  --------------");
		boolean authStatus = IFXCTL_ExampleUtils.authenticate(cmdApi, UPDATEKEY_BIN, NO_DIVERSIFICATION_MODE, null, VAULT_UPDATEKEY_BIN);
		if (!authStatus){
			throw new CipurseException(IFXCTL_ExampleUtils.AUTH_ERROR_MSG);
		}
		
		if(mode == CRYPTO_MODE.HYBRID)
		{
			logger.log("\n----------- 5) [Conditional] Switch to SWSAM(hybrid mode)  --------------");
			if(!sessionMgr.switchToSWSAM()){
				throw new CipurseException(IFXCTL_ExampleUtils.SWITCH_TO_SW_SAM_ERROR_MSG);
			}
		}else{
			logger.log("\n----------- 5) (Switch to SWSAM)This Step Not Applicable in non Hybrid modes: --------------");
		}
		
		logger.log("\n----------- 6)	Set SMI value to SM_ENC_MAC --------------");
		sessionMgr.setSMIValue(SMLevel.eSM_ENC_MAC);
		
		logger.log("\n----------- 7)	Update entire EF.BINARY_FILE1 with SFID  --------------");
		int modulus		= EF_BINARY_FILE1_SIZE % EF_BINARY_FILE1_READUPDATE_SIZE;
		int loopCounter = (EF_BINARY_FILE1_SIZE / EF_BINARY_FILE1_READUPDATE_SIZE)+(modulus>0?1:0);
		for (int i=0; i<loopCounter; i++){
			
			byte sfid = 0x00;
			if (i==0)
				sfid = EF_BINARY_FILE1_SFID;
			
			efUpdateOffset  = (short) (EF_BINARY_FILE1_READUPDATE_SIZE * i);
			int len			= EF_BINARY_FILE1_READUPDATE_SIZE;
			if (efUpdateOffset+EF_BINARY_FILE1_READUPDATE_SIZE > EF_BINARY_FILE1_SIZE)
				len = EF_BINARY_FILE1_READUPDATE_SIZE - ((efUpdateOffset+EF_BINARY_FILE1_READUPDATE_SIZE) - EF_BINARY_FILE1_SIZE);
			
			byte[] newData = Utils.extractBytes(EF_BINARY_FILE1_UPDATE_DATA, 0, len);
			logger.log("\n Updating from offset -> " + efUpdateOffset +" and len is -" + len);
			
			response = cmdApi.updateBinary(sfid, (short) efUpdateOffset, new ByteArray(newData));
					
			IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		}
		
		if (isCTMSupported){
			logger.log("\n----------- 8)	[Conditional]Perform Transaction in SM_MAC_MAC --------------");
			sessionMgr.setSMIValue(SMLevel.eSM_MAC_MAC);
			response = cmdApi.performTransaction();
			IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		}

		// Reset SM
		sessionMgr.resetSM();
		
		logger.log("\n----------- Update Binary File Example Executed successfully --------------\n\n");
	}

	public byte[][] ReadCrfFileData() throws CipurseException {
		logger.log("\n----------- Read Record File Operation Begins --------------\n\n");

		byte[][] binaryData = new byte[20][200];
		byte recIdx = EF_CYCLIC__READ_RECORD_NUM;

		for(int i = 0; i < 20; i++) {

			logger.log("\n" + (int) recIdx + "----------- ) Read all records (MULTIPLE_RECORD)of EF.CYCLIC_RECORD_FILE   --------------");
			response = cmdApi.readRecord(EF_CYCLIC_SFID, recIdx, IFXCTL_ExampleUtils.SINGLE_RECORD, EF_CYCLIC_READ_MUL_REC_SIZE);
			if (response.getBytes().length > 2) {
				IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
				int len = 200;
				System.arraycopy(Arrays.copyOfRange(response.getBytes(), 0, len), 0, binaryData[i], 0, len);
				recIdx++;
			}
			else
				binaryData[i] = null;
        }


		logger.log("\n----------- Read Record file operations executed successfully --------------\n\n");
		return binaryData;
	}
		
	/**
	 * Steps involved in ReadBinaryfileOperation- 
	 *  > Select ADF PxSE 
	 *	> Perform the authentication with the Read Key
	 *	> Set SMI value to SM_PLAIN_MAC
	 *	> [Conditional] Switch to SWSAM(hybrid mode)
	 *  > Read entire EF.BINARY_FILE1 with SFID
	 */
	/**
	* Interface used to read binary file operation.
	*/
	public byte[] readBinaryfileOp() throws CipurseException{
		
		logger.log("\n----------- Read Binary File Operation Begins --------------\n\n");
		
		IFXCTL_ExampleUtils.initializeTargets(sessionMgr, mode, SMTYPE.CIPURSE, cbpReader, useHwSAM, logger);
		
		if(mode != CRYPTO_MODE.SW_SAM){
			response = sessionMgr.authorizeHWSAM(IFXCTL_ExampleUtils.FIELD_SAM_ADF_AID, IFXCTL_ExampleUtils.getSAMPassword());
			IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		}
		
		cmdApi = new CipurseCommandApi(sessionMgr);

		logger.log("\n----------- 1) Select MF --------------");
		response = cmdApi.selectFileByFID((byte) 0x00, IFXCTL_ExampleUtils.MF_FILEID, (short) 0x00);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);

		logger.log("\n----------- 2) Select ADF --------------");
		response = cmdApi.selectFileByAID(PUIi1_P2, new ByteArray(BIN_ADF_AID), IFXCTL_ExampleUtils.LE_REQUIRED);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);

        logger.log("\n----------- 3) Select Binary File --------------");
        response = cmdApi.selectFileByFID((byte) 0x00, BIN_ADF_FID, (short) 0x00);
        IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);

		logger.log("\n----------- 4) Read File Attributes --------------");
		response = cmdApi.readFileAttributes((byte) 0x00);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
		int binFileLen = ByteBuffer.wrap(Arrays.copyOfRange( response.getBytes(), 4,6)).getShort();
		logger.log(String.format("\n%s = %d", "-----LEN:", binFileLen));
		byte[] binaryData = new byte[binFileLen];
		int modulus		= binFileLen % EF_BINARY_FILE1_READUPDATE_SIZE;
		int loopCounter = (binFileLen / EF_BINARY_FILE1_READUPDATE_SIZE);
		if (modulus == 0);
		else {
			loopCounter += 1;
		}
		for (int i=0; i<loopCounter; i++){

			byte sfid = 0x00;
			int offset  = (EF_BINARY_FILE1_READUPDATE_SIZE * i);
			if (i==0) {
				sfid = BIN_SFID;
			}

			int len		= EF_BINARY_FILE1_READUPDATE_SIZE;
			if (offset+EF_BINARY_FILE1_READUPDATE_SIZE > binFileLen)
				len = EF_BINARY_FILE1_READUPDATE_SIZE - ((offset+EF_BINARY_FILE1_READUPDATE_SIZE) - binFileLen);

			logger.log("\n Reading from offset -> " + offset +" and len is:" + len);
			response = cmdApi.readBinary(sfid, offset, (byte) len);
			IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);
			System.arraycopy(Arrays.copyOfRange( response.getBytes(), 0,len), 0, binaryData, offset, len);
		}
		logger.log("\n----------- Read Binary File Example Executed successfully --------------\n\n");
		return binaryData;
	}
		
		
	private ByteArray createBinaryADF() throws CipurseException{
		
		// FCP AID Info
		FCPInfo fcpInfo = new FCPInfo();
		fcpInfo.setAID(BIN_ADF_AID);
		fcpInfo.setPXSEAID(null);
		fcpInfo.setPropSecurityAttributes(null);
		
		// Keys Info
		KeyAttributeInfo[] 			keyAttributes 	= new KeyAttributeInfo[BIN_ADF_NBRK];
		KeyDiversificationInfo[] 	keyDivInfo 		= new KeyDiversificationInfo[BIN_ADF_NBRK];
		
		for (int i=0; i<BIN_ADF_NBRK; i++){
			
			keyAttributes[i] = new KeyAttributeInfo();
			keyAttributes[i].setKeyAddInfo(KEY_ADDITIONAL_INFO);
			keyAttributes[i].setKeyAlgoId(KEY_ALGO_ID);
			keyAttributes[i].setKeySecAttrib(KEY_SEC_ATTRS[0]);
			keyAttributes[i].setKeyLength(KEY_LENGTH);
			
			keyDivInfo[i] = new KeyDiversificationInfo();
			keyDivInfo[i].setKeyDiversificationMode(NO_DIVERSIFICATION_MODE);
			keyDivInfo[i].setKeyIDorReference(KEY_IDS[i]);
			keyDivInfo[i].setPlainKeyOrDivData(null);
		}
		
		// Assign all ADF File Attributes
		DFFileAttributes adfAttributes = new DFFileAttributes();
		adfAttributes.setFileID(BIN_ADF_FID);
		adfAttributes.setFcpInfo(fcpInfo);
		adfAttributes.setFileDescriptor(IFXCTL_ExampleUtils.CIP_ADF_FD);
		adfAttributes.setAppProfile(binADFAppProfile);
		adfAttributes.setNumOfKeys(BIN_ADF_NBRK);
		adfAttributes.setNumOfEFs(BIN_ADF_NUM_OF_EFS);;
		adfAttributes.setNumOfSFIDs(BIN_ADF_NUM_OF_SFIDS);
		adfAttributes.setSmr(BIN_ADF_SMR);
		adfAttributes.setArt(BIN_ADF_ART);
		
		response = cmdApi.createADF(CREATE_ADF_MODE.PLAIN, adfAttributes, keyAttributes, null, keyDivInfo, BIN_ADF_NBRK);
		return response;
	}
	
	private ByteArray createPXSEADF(byte[] aid, byte FD, int fid, byte noOfKeys) throws CipurseException{
		
		// FCP AID Info
		FCPInfo fcpInfo = new FCPInfo();
		fcpInfo.setAID(aid);
		
		// Assign all ADF File Attributes
		DFFileAttributes adfAttributes = new DFFileAttributes();
		adfAttributes.setFileID(fid);
		adfAttributes.setFcpInfo(fcpInfo);
		adfAttributes.setFileDescriptor(FD);
		adfAttributes.setAppProfile((byte)0x00);
		adfAttributes.setNumOfKeys(noOfKeys);
		adfAttributes.setNumOfEFs((short) 0x01);
		adfAttributes.setNumOfSFIDs((byte) 0x00);
		
		KeyAttributeInfo[] keyAttributes = new KeyAttributeInfo[0];
		KeyDiversificationInfo[] keyDivInfo = new KeyDiversificationInfo[0];
				
		response = cmdApi.createADF(CREATE_ADF_MODE.PLAIN, adfAttributes, keyAttributes, null, keyDivInfo, (byte) 0x00);
		return response; 
	}
}

