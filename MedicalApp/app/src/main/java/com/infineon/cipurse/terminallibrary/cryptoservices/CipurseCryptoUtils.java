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
import java.util.HashMap;
import java.util.Map;

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseCommandsConstants;
import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.framework.Utils.eCaseType;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CHAINING_MODE;

/**
 * Defines Crypto related Utility methods.
 *
 * @since 1.0.0
 * @version 1.0.1
 */
 class CipurseCryptoUtils implements CipurseCommandsConstants{



	 private static Map<Integer,Integer> errorCodeMap = new HashMap<Integer,Integer>();

	 static {
		/** Success */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_OK, CipurseConstant.CTL_SUCCESS);
		/** Warning: Referenced Counter  has reached the warning level */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_GENERAL_WARNING,  CipurseConstant.CTL_SUCCESS);
		/** UnWrap Response in Chaining Mode */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_CHAINING_MODE,  CipurseConstant.CTL_SUCCESS);
		/** Invalid INS */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_INVALID_INS,  CipurseConstant.CTL_STATUS_INVALID_INS);
		/** Invalid CLA */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_INVALID_CLA,  CipurseConstant.CTL_STATUS_INVALID_CLA);
		/** Incorrect Parameters P1-P2 */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_INCORRECT_P1P2,  CipurseConstant.CTL_STATUS_INCORRECT_P1P2);
		/** Command Incompatible with File Structure */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_CMD_INCOMPATIBLE_WITH_FILETYPE,  CipurseConstant.CTL_STATUS_CMD_INCOMPATIBLE_WITH_FILETYPE);
		/** Memory Failure */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_MEMORY_FAILURE,  CipurseConstant.CTL_STATUS_MEMORY_FAILURE);
	 	/** Selected File Invalidated */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_FILE_INVALIDATED,  CipurseConstant.CTL_STATUS_FILE_INVALIDATED);
	 	/** File or Application not found */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_FILE_NOT_FOUND,  CipurseConstant.CTL_KEY_NOT_FOUND);
	 	/** Not enough memory on chip to satisfy conditions */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_NOT_ENOUGH_MEMORY,  CipurseConstant.CTL_STATUS_NOT_ENOUGH_MEMORY);
	 	/** FileID / SFID already exists */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_FILEID_ALREADY_EXISTS,  CipurseConstant.CTL_STATUS_FILEID_ALREADY_EXISTS);
		/** AID already exists */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_AID_ALREADY_EXISTS,  CipurseConstant.CTL_STATUS_AID_ALREADY_EXISTS);
		/** Authorization Failed, X Remaining attempts */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_AUTHORISATION_FAILED,  CipurseConstant.CTL_STATUS_AUTHORISATION_FAILED);
		/** Invalid data length */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_INVALID_DATA_LENGTH,  CipurseConstant.CTL_INVALID_DATA_LENGTH);
		/** Incorrect SM objects */
	 	errorCodeMap.put(CipurseConstant.eISO_STATUS_INCORRECT_SM_DATAOBJECT,  CipurseConstant.CTL_INCORRECT_SM_DATAOBJECT);
		/** Invalid security status in SAM */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_SECURITY_NOT_SATISFIED,  CipurseConstant.CTL_SECURITY_NOT_SATISFIED);
		/** Command not allowed in current state */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_COMMAND_NOT_ALLOWED,  CipurseConstant.CTL_CMD_NOT_ALLOWED);
		/** Incorrect command data */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_INCORRECT_PARAM_IN_CMD_DATA,  CipurseConstant.CTL_INCORRECT_PARAM_IN_CMD_DATA);
		/** Function not supported. Operation not supported for Key Use */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_FUNCTION_NOT_SUPPORTED,  CipurseConstant.CTL_FUNCTION_NOT_SUPPORTED);
		/** Reference Record Not Found (Key referenced by KeyID not found) */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_REFERENCE_RECORD_NOT_FOUND,  CipurseConstant.CTL_KEY_NOT_FOUND);
		/** Referenced data or reference data not found (e.g. wrong key number) */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_REFERENCE_DATA_NOT_FOUND,  CipurseConstant.CTL_REFERENCE_DATA_NOT_FOUND);
		/** Conditions of use not satisfied */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_CONDITIONS_OF_USE_NOT_SATISFIED,  CipurseConstant.CTL_CONDITIONS_OF_USE_NOT_SATISFIED);
		/** Referenced key is not in usable state  */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_REFERENCED_KEY_NOT_USABLE,  CipurseConstant.CTL_REFERENCED_KEY_NOT_USABLE);
		/** Retry counter reached its limit */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_AUTH_METHOD_BLOCKED,  CipurseConstant.CTL_AUTH_METHOD_BLOCKED);
		/** Authentication Failed */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_AUTHENTICATION_FAILED,  CipurseConstant.CTL_AUTHENTICATION_FAILED);
		/** Security related issue. */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_SECURITY_RELATED_ISSUES,  CipurseConstant.CTL_SECURITY_RELATED_ISSUES);
		/** Command not allowed. CIPURSE SAM use does not support command */
		errorCodeMap.put(CipurseConstant.eISO_STATUS_CMD_NOT_ALLOWED_SAM,  CipurseConstant.CTL_CMD_NOT_ALLOWED_SAM);
	 }
	/**
	 * Method to get the UnWrap Response
	 * @param smSAMCryptoApi Handler for ICipurseSAMCryptoApi
	 * @param smResponse Response as ByteArray
	 * @param smi Secure Messaging Value
	 * @return UnWrapped Response as byte array
	 * @throws CipurseException {@link CipurseException}
	 */
	static byte[] unWrapResponse(ICipurseSAMCryptoApi smSAMCryptoApi,ByteArray smResponse, byte smi) throws CipurseException {

		byte[] resp = smSAMCryptoApi.verifySmElements(CHAINING_MODE.PLAIN_MODE, smResponse);

		switch (smi & CipurseConstant.BITMAP_SMI_FOR_RESPONSE) {
		case CipurseConstant.SM_RESPONSE_MACED:
			return processUnwrappedMACedResponse(smResponse.getBytes(), resp);

		case CipurseConstant.SM_RESPONSE_ENCED:
			return resp;

		case CipurseConstant.SM_COMMAND_RESPONSE_PLAIN:
			return smResponse.getBytes();

		default:
			throw new CipurseException(CipurseConstant.CTL_CONDITIONS_OF_USE_NOT_SATISFIED);
		}

	}

	/**
	 * Method to wrap command
	 * @param smSAMCryptoApi handler for ICipurseSAMCryptoApi
	 * @param ppsCommandAPDU command as ByteArray
	 * @param peSMI Secure Message Value
	 * @return Wrapped Command as byte array
	 * @throws CipurseException
	 */
	static byte[] wrapCommand(ICipurseSAMCryptoApi smSAMCryptoApi,ByteArray ppsCommandAPDU, byte peSMI) throws CipurseException {
		byte updateSMI=peSMI;
		eCaseType apduCase = Utils.getCaseType(ppsCommandAPDU.getBytes());
		if(apduCase==eCaseType.CASE_2 || apduCase==eCaseType.CASE_4)
			updateSMI=Utils.updateSMIforLePresence(peSMI, true);
		else
			updateSMI=Utils.updateSMIforLePresence(peSMI, false);

		byte[] response = smSAMCryptoApi.generateSmElements(updateSMI, ppsCommandAPDU);

		byte[] respData = new byte[0];
		if(response != null && response.length>0)
			respData=  Arrays.copyOfRange(response, 0, response.length);

		return processPostWrap(ppsCommandAPDU.getBytes(), respData, (byte) updateSMI);
	}

	/**
	 * Method to process of UnWrapping a Maced response
	 * @param cbpSmResponse response as byte array
	 * @param samResponse sam response
	 * @return result as byte array
	 * @throws CipurseException
	 */
	private static byte[] processUnwrappedMACedResponse(byte[] cbpSmResponse, byte[] samResponse) throws CipurseException {
		byte[] unwrapedResponse=new byte[cbpSmResponse.length - 8];
		int offset=0;
		if(cbpSmResponse.length - 10 > 0)//data
			offset=Utils.arrayCopy(cbpSmResponse, 0, unwrapedResponse, offset, cbpSmResponse.length - 10);
		Utils.arrayCopy(cbpSmResponse, cbpSmResponse.length-2, unwrapedResponse, offset, 2);
		return unwrapedResponse;
	}

	/**
	 * Method to process the Post wrapping
	 * @param command Original Plain Command
	 * @param respData Cryptogram for Wrapped command returned from SW/HW SAM
	 * @param smi SMI used to wrap given command
	 * @return Wrapped command : Completely constructed SM Command APDU
	 * @throws CipurseException {@link CipurseException}
	 */
	private static byte[] processPostWrap(byte[] command, byte[] respData, byte smi) throws CipurseException
	{
		eCaseType orgCase = Utils.getCaseType(command);

		byte[] smHeader = new byte[5];
		Utils.arrayCopy(command, 0, smHeader, 0, 4);
		byte[] smCmdData=new byte[]{smi};

		smHeader[APDU_OFFSET_CLA]= (byte) (smHeader[APDU_OFFSET_CLA] | 0x04);

		switch (smi & CipurseConstant.BITMAP_SMI_COMMAND) {
			case CipurseConstant.SM_COMMAND_MACED:
				//SMI + data
				if(orgCase==eCaseType.CASE_3 || orgCase==eCaseType.CASE_4)
				{
					byte[] plainData=Utils.extractBytes(command, (APDU_OFFSET_CDATA & 0x00FF),(command[APDU_OFFSET_P3] & 0x00FF));
					smCmdData=Utils.concat(smCmdData, plainData);
				}
				//origional le
				if(orgCase==eCaseType.CASE_2)
					smCmdData=Utils.concat(smCmdData, new byte[]{command[CipurseConstant.OFFSET_LC]});
				if(orgCase==eCaseType.CASE_4)
					smCmdData=Utils.concat(smCmdData, new byte[]{command[command.length - 1]});
				//mac
				smCmdData=Utils.concat(smCmdData, respData);
				break;

			case CipurseConstant.SM_COMMAND_ENCED:
				smCmdData=Utils.concat(smCmdData, respData);
				//origional le
				if(orgCase==eCaseType.CASE_2)
					smCmdData=Utils.concat(smCmdData, new byte[]{command[CipurseConstant.OFFSET_LC]});
				if(orgCase==eCaseType.CASE_4)
					smCmdData=Utils.concat(smCmdData, new byte[]{command[command.length - 1]});
				break;

			case CipurseConstant.SM_COMMAND_RESPONSE_PLAIN:
				//SMI + data
				if(orgCase==eCaseType.CASE_3 || orgCase==eCaseType.CASE_4)
				{
					byte[] plainData=Utils.extractBytes(command, APDU_OFFSET_CDATA & 0x00FF, command[APDU_OFFSET_P3] & 0x00FF);
					smCmdData=Utils.concat(smCmdData, plainData);
				}
				//origional le
				if(orgCase==eCaseType.CASE_2)
					smCmdData=Utils.concat(smCmdData, new byte[]{command[CipurseConstant.OFFSET_LC]});
				if(orgCase==eCaseType.CASE_4)
					smCmdData=Utils.concat(smCmdData, new byte[]{command[command.length - 1]});
				break;

			default:
				throw new CipurseException(CipurseConstant.CTL_CONDITIONS_OF_USE_NOT_SATISFIED);
		}

		byte[] smCommand=Utils.concat(smHeader, smCmdData);
		smCommand[APDU_OFFSET_P3]=(byte) smCmdData.length;
		return getLeDashCommand(smCommand, orgCase, smi);
	}

	/**
	 * Method to get the Le Dash Command
	 * @param smCommand Command as byte array
	 * @param orgCaseType Type of the case
	 * @param SMI secure message indicator value
	 * @return Result command as byte array
	 */
	static byte[] getLeDashCommand(byte[] smCommand, eCaseType orgCaseType,
			byte SMI) {
		byte[] smPlainCommand = null;

		if ((orgCaseType == eCaseType.CASE_1)
				|| (orgCaseType == eCaseType.CASE_3)) {
			if ((SMI & CipurseConstant.BITMAP_SMI_FOR_RESPONSE) != CipurseConstant.SM_COMMAND_RESPONSE_PLAIN) {
				smPlainCommand = getPlainCommand(smCommand);
			} else {
				smPlainCommand = smCommand;
			}
		} else {
			smPlainCommand = getPlainCommand(smCommand);
		}

		return smPlainCommand;
	}

	/**
	 * Method to get the Plain Command
	 * @param smCommand command as byte array
	 * @return byte array as plain command
	 */
	private static byte[] getPlainCommand(byte[] smCommand){
		byte[] smPlainCommand = null;
		smPlainCommand = new byte[smCommand.length + 1];
		System.arraycopy(smCommand, 0, smPlainCommand, 0, smCommand.length);
		smPlainCommand[smPlainCommand.length - 1] = CipurseConstant.LE_DASH;
		return smPlainCommand;
	}

	/**
	 * Gives Terminal Library related error code mapping to corresponding Card Status Word.
	 * @param sw Card Status Word
	 * @return Terminal Library related error code.
	 */
	public static Integer getMapedErrorCode(int sw) {
		return errorCodeMap.get(sw);
	}

}
