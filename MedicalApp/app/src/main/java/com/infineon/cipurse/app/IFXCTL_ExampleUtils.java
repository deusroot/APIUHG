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

import android.nfc.tech.IsoDep;

import java.util.HashMap;

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.model.EFFileAttributes;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.pal.CommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.CommunicationHandlerSAM;
import com.infineon.cipurse.terminallibrary.pal.ICommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.pal.ISymCrypto;
import com.infineon.cipurse.terminallibrary.pal.Logger;
import com.infineon.cipurse.terminallibrary.pal.SymCrypto;
import com.infineon.cipurse.terminallibrary.presentation.CipurseCommandApi;
import com.infineon.cipurse.terminallibrary.presentation.ICipurseCommandApi;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager;
import com.infineon.cipurse.terminallibrary.presentation.SessionManager;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CRYPTO_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.SMTYPE;

/**
* This is used for constants and common functions used across the other examples
* 
* @since 1.0.0
* @version 1.0.1
*/
public class IFXCTL_ExampleUtils {
	
	
	/***************************	READER CONFIGURATION	***************************/
	//public static final String CBP_READER_NAME 			= "Duali DE-620R Contactless Reader 0";
	
	//public static final String FIELD_SAM_READER_NAME	= "Identive CLOUD 4700 F Contact Reader 0";
	
	//public static final String MASTER_SAM_READER_NAME	= "Identive CLOUD 4700 F Contact Reader 1";
	
	
	/***************************	SW KEYVAULT		***************************/
	private static HashMap<Integer, String> keyPairs = new HashMap<Integer, String>();
	
	static {
    	
    	keyPairs = new HashMap<Integer, String>();
    	keyPairs.put(0x0001, "0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73, 0x73"); 
    	keyPairs.put(0x0002, "0x2D, 0x13, 0xDA, 0x5D, 0xA9, 0xCA, 0x68, 0x93, 0x95, 0xE9, 0x57, 0x76, 0xC7, 0x6B, 0xE5, 0x87"); 
    	keyPairs.put(0x0003, "0x94, 0xA8, 0x4C, 0x75, 0xBC, 0x8D, 0x3F, 0x5A, 0x10, 0x2B, 0x48, 0x78, 0x86, 0x72, 0x00, 0xE6"); 
    	keyPairs.put(0x0004, "0x1A, 0x49, 0xC7, 0x87, 0x7F, 0x58, 0x8F, 0xA1, 0x54, 0x76, 0xC0, 0xE9, 0xBF, 0x49, 0xD8, 0xD3"); 
    	keyPairs.put(0x0005, "0x3F, 0x54, 0x9B, 0xA4, 0x0B, 0x47, 0x08, 0x2E, 0x1A, 0x8B, 0x5F, 0xDB, 0x79, 0xF2, 0xD3, 0xC5"); 
    	keyPairs.put(0x0006, "0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F"); 
    	keyPairs.put(0x0007, "0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x5F"); 
    	keyPairs.put(0x0008, "0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F"); 
    	keyPairs.put(0x0009, "0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0x7B, 0x7C, 0x7D, 0x7E, 0x7F"); 
    	keyPairs.put(0x000A, "0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8A, 0x8B, 0x8C, 0x8D, 0x8E, 0x8F");	
    }
	
	
	/***************************	CONSTANTS	***************************/
	public static final String 		MODE_SW							= "1";
	
	public static final String 		MODE_HW							= "2";
	
	public static final String 		MODE_HYBRID						= "3";
	
	public static final ByteArray	FIELD_SAM_ADF_AID 				= new ByteArray(new byte[]{(byte) 0xD2, 0x76, 0x00, 0x00, 0x04, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01});
	
	public static final ByteArray	MASTER_SAM_ADF_AID 				= new ByteArray(new byte[]{(byte) 0xD2, 0x76, 0x00, 0x00, 0x04, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x5F, 0x00});
	
	/* CIPURSE SAM Application FD */
	public static final byte 		CIP_SAM_APP_FD					= 0x3E;

	/* CIPURSE T Application */
	public static final byte 		CIP_T_APP						= 0x60;

	/* CIPURSE S Application */
	public static final byte 		CIP_S_APP						= 0x40;
	
	/* CIPURSE L Application */
	public static final byte 		CIP_L_APP						= 0x20;
	
	public static final byte 		CURRENT_SEL_FILE				= 0x00;
	
	public static final byte 		CIP_ADF_FD 						= 0x38;
	
	public static final byte		PXSE_ADF_FD						= 0x3F;
	
	public static final byte		PXSE_ADF_TYPE					= 0x00;
	
	public static final ByteArray 	FORMAT_ALL_COMMAND 				= new ByteArray(new byte[] {0x43, 0x6F, 0x6E, 0x66, 0x69, 0x72, 0x4D});
	
	public static final ByteArray 	COMMAND_SUCCESS_STATUS 			= new ByteArray("90 00");
	
	public static final ByteArray 	VERIFY_SAM_PWD_STATUS 			= new ByteArray("69 85");
	
	public static final ByteArray 	ADF_EXISTS_STATUS 				= new ByteArray("6A 8A");
	
	public static final byte		SINGLE_RECORD					= 0x04;
	
	public static final byte		MULTIPLE_RECORD					= 0x05;
	
	public static final byte		SINGLE_CURRENT_PREV_VALUE		= 0x06;
	
	public static final byte		MULTIPLE_CURRENT_PREV_VALUE		= 0x07;

	public static final byte 		PROFILE_T 						= 0x04;
	
	public static final byte 		PROFILE_S 						= 0x02;
	
	public static final byte 		PROFILE_L 						= 0x01;
	
	public static final int			ID_INFO_DIVDATA_OFFSET			= 8;
	
	public static final int			ID_INFO_DIVDATA_LENGTH			= 0x10;
	
	public static final int 		EF_ID_INFO_OFFSET				= 0x00;
	
	public static final int 		EF_ID_INFO_LENGTH				= 0x18;
	
	public static final byte[] 		SAM_PWD_EF_DATA 				= { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
																		(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
																		(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF }; /*Data */
	
	public static final short 		SAM_PWD_OFFSET 					= 0x00;
	
	public static final short 		SAM_PWD_LENGTH 					= 0x10;
	
	public static final short 		LE_NOT_REQUIRED 				= (short) 0x8000;
	
	public static final short 		LE_REQUIRED 					= 0x00;
	
	public static final int			MF_FILEID						= 0x3F00;

	public static final int 		EF_ID_INFO 						= 0x2FF7;
	
	public static final byte[] 		IC_MAN_L_PROFILE				= new byte[]{0x43, 0x32, 0x4C};
	
	public static final byte[] 		IC_MAN_S_PROFILE				= new byte[]{0x43, 0x32, 0x53};
	
	
    /***************************	ERROR MESSAGES	***************************/
	public static final String 	AUTH_ERROR_MSG 					= "Authentication error";

	public static final String 	ADF_EXISTS_ERROR_MSG 			= "ADF already present on card";

	public static final String 	SWITCH_TO_SW_SAM_ERROR_MSG 		= "Switch to SW SAM failed";

	public static final String 	MASTER_SAM_HW_MODE_ERROR_MSG 	= "MASTER SAM cannot be run in HwSAM mode";

	
	public static final String 	CIP_T_NRG_SUPPORT_ERROR_MSG 	= "CBP: Profile T does not support NRG mapped ADF operation";

	public static final String 	CIP_CARD_NRG_ERROR_MSG 			= "CBP: Card does not support NRG mapped ADF";

	public static final String NRG_FEATURE_NOT_SUPPORTED 		= "CBP: This product does not support NRG migration feature";
	
	
	public static HashMap<Integer, String> getKeyPairs() {
		return keyPairs;
	}
	
/*	public static void initializeTargets(ISessionManager sessionMgr, CRYPTO_MODE mode, SMTYPE smType, ILogger logger) throws CipurseException {

		initializeTargets(sessionMgr, mode, smType, IFXCTL_ExampleUtils.getCBPReaderName(), IFXCTL_ExampleUtils.getFieldSAMReaderName(), logger);
	
	}*/

/*	public static void initializeTargets(ISessionManager sessionMgr, CRYPTO_MODE mode, SMTYPE smType, ILogger logger, IsoDep cbpReader) throws CipurseException {

		initializeTargets(sessionMgr, mode, smType, cbpReader, true, logger);

	}*/
	
//	public static void initializeTargets(ISessionManager sessionMgr, CRYPTO_MODE mode, SMTYPE smType, String cbpReader, String samReader, ILogger logger) throws CipurseException {
	public static void initializeTargets(ISessionManager sessionMgr, CRYPTO_MODE mode, SMTYPE smType, IsoDep cbpReader, boolean connectHwSAM, ILogger logger) throws CipurseException {
		
		ICommunicationHandler cbpHandler = null;

		if (cbpReader != null) {
			cbpHandler = new CommunicationHandler(cbpReader);
			//cbpHandler.reset(CommunicationHandler.COLD_RESET);
		}
/*
		if(cbpReader != null) {
			cbpHandler 	= new CommunicationHandler(cbpReader);
			cbpHandler.reset(CommunicationHandler.COLD_RESET);
		}
*/

		ICommunicationHandler samHandler 	=  null;
        if (connectHwSAM != false && mode != CRYPTO_MODE.SW_SAM){
            samHandler 	= new CommunicationHandlerSAM();
        }
/*
		if (samReader != null && mode != CRYPTO_MODE.SW_SAM){
			samHandler 	= new CommunicationHandler(samReader);
			samHandler.reset(CommunicationHandler.COLD_RESET);
		}
*/
		ISymCrypto crypto 	= initializeKeySet();
		sessionMgr.initializeSession(mode, smType, cbpHandler, samHandler, logger, crypto);
	}
	
	private static ISymCrypto initializeKeySet() throws CipurseException{
		ISymCrypto crypto 	= new SymCrypto(IFXCTL_ExampleUtils.getKeyPairs().size());
		for (int i=0; i<IFXCTL_ExampleUtils.getKeyPairs().size(); i++){
			crypto.setKeyValue(i+1, Utils.toBytes(IFXCTL_ExampleUtils.getKeyPairs().get(i+1)));
		}
		return crypto;
	}
	
	public static boolean authenticate(ICipurseCommandApi cmdApi, byte adfKeyNumber, byte keyDivMode, byte[] divData, byte keyVaultKeyNumber) throws CipurseException{
		
		KeyDiversificationInfo authKeyInfo = new KeyDiversificationInfo();
		authKeyInfo.setKeyDiversificationMode(keyDivMode);
		authKeyInfo.setKeyIDorReference(keyVaultKeyNumber);
		authKeyInfo.setPlainKeyOrDivData(divData);
		return cmdApi.establishSecureChannel(adfKeyNumber, authKeyInfo);
	}

	public static ByteArray createBinaryEF(ICipurseCommandApi commandApi, byte fileType, int fid, int fileSize, 
			byte[] smr, byte[] art, short SFID, byte noOfKeys) throws CipurseException{

		EFFileAttributes ef = new EFFileAttributes();
		ef.setFileDescriptor(fileType);
		ef.setFileID(fid);
		ef.setFileSize(fileSize);
		ef.setNumOfKeys(noOfKeys);
		ef.setSmr(smr);
		ef.setArt(art);
		ef.setSFID(SFID);
		
		return commandApi.createElementaryFile(ef);
	}
	
	public static ByteArray createRecordEF(ICipurseCommandApi commandApi, byte fileType, int fid, short recordCount,
						short recordSize, byte[] smr, byte[] art, short SFID, byte noOfKeys) throws CipurseException{

		EFFileAttributes ef = new EFFileAttributes();
		ef.setFileDescriptor(fileType);
		ef.setFileID(fid);
		ef.setNumOfKeys(noOfKeys);
		ef.setNumOfRecs(recordCount);
		ef.setRecSize(recordSize);
		ef.setSmr(smr);
		ef.setArt(art);
		ef.setSFID(SFID);
		
		return commandApi.createElementaryFile(ef);
	}
	
	public static void errorHandler(ILogger logger, ByteArray receivedStatus, ByteArray expectedStatus) throws CipurseException{

		ByteArray recStatus = new ByteArray(receivedStatus.getBytes());
		ByteArray expStatus = new ByteArray(expectedStatus.getBytes());

		int recSize = recStatus.size();
		if(recSize > 2)
		{
			recStatus = recStatus.subArray(recSize-2, recSize);
		}

		if(!recStatus.equals(expStatus)){
			logger.log("\nReceived Status: " + recStatus);
			logger.log("\nExpected Status: " + expStatus);
			logger.log("\n************************************************************");
			logger.log("-------------- Expected result does NOT MATCH --------------");
			logger.log("----------------	Execution Terminated    --------------");
			logger.log("************************************************************");
			throw new CipurseException("Expected result does NOT MATCH");
		}
	}
	
	public static ByteArray getSAMPassword() throws CipurseException{
		return new ByteArray(Utils.toBytes(Utils.extractBytes(SAM_PWD_EF_DATA, SAM_PWD_OFFSET, SAM_PWD_LENGTH)));
	}
	
/*
	public static String getCBPReaderName(){
		return CBP_READER_NAME;
	}
	
	public static String getFieldSAMReaderName(){
		return FIELD_SAM_READER_NAME;
	}
	
	public static String getMasterSAMReaderName(){
		return MASTER_SAM_READER_NAME;
	}
*/

	/**
	 * Steps involved :-
	 *  > Cold Reset
	 *	> Format all
	 */
	/**
	 * Interface used to Format the card.
	*/
	public static void restoreCard(IsoDep cbpReader, boolean useHwSAM, CRYPTO_MODE crypto_mode) throws CipurseException{
		
		ISessionManager 		sessionMgr 		= new SessionManager();
		ILogger 				logger			= new Logger();
		ByteArray 				response 		= null;
		ICipurseCommandApi 		cmdApi			= null;
		
		logger.log("\n----------- Restore card Begins --------------\n\n");
		
		//IFXCTL_ExampleUtils.initializeTargets(sessionMgr, CRYPTO_MODE.SW_SAM, SMTYPE.CIPURSE, logger);
		IFXCTL_ExampleUtils.initializeTargets(sessionMgr, crypto_mode, SMTYPE.CIPURSE, cbpReader, useHwSAM, logger);
		
		cmdApi = new CipurseCommandApi(sessionMgr);
		
		response = cmdApi.formatAll(IFXCTL_ExampleUtils.FORMAT_ALL_COMMAND);
		IFXCTL_ExampleUtils.errorHandler(logger, response, IFXCTL_ExampleUtils.COMMAND_SUCCESS_STATUS);

		logger.log("\n----------- Restore card Executed successfully --------------\n\n");
	}
}
