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
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.model.DFFileAttributes;
import com.infineon.cipurse.terminallibrary.model.EFFileAttributes;
import com.infineon.cipurse.terminallibrary.model.EncryptionKeyInfo;
import com.infineon.cipurse.terminallibrary.model.KeyAttributeInfo;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.CREATE_ADF_MODE;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.UPDATEKEY_MODE;


/**
 * Interface for APIs related to all CBP Commands.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public interface ICipurseCommandApi {


	/**
	 *  Interface used to issue Select MF on the CIPURSE card.
	 * <br>This will issue SELECT case-1 command to CIPURSE card.<br>
	 *
	 * @param pui1P2  -  hold the byte of File Identifier
	 * @param pui1ExpLength - Expected response Length.
	 * <br>If value is negative then command is sent without Le, else the command is sent with Le as the lower byte value of this parameter<br>
	 * @return ByteArray of the result
	 *
	 * @throws CipurseException {@link CipurseException}
	*/
	public ByteArray selectMF(byte pui1P2, short pui1ExpLength) throws CipurseException;


	/**
		 Interface used to issue Select an ADF or EF using a file identifier
				with Le present on the CIPURSE card.
		<br>This will issue SELECT case-3 command to CIPURSE card.<br>

		@param pui1P2 Parameter P2 value (0x00 or 0x0C)
		@param pui2FID - File Identifier of ADF or EF
		@param pui1ExpLength - Expected response data length.
		<br>If value of this parameter is negative then command is sent without Le, else the command is sent with Le as the lower byte value of this parameter<br>
		@return ByteArray of the result
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray selectFileByFID(byte pui1P2, int pui2FID,
                                     short pui1ExpLength) throws CipurseException;


	/**
		 Interface used to issue Select a ADF using AID on the CIPURSE card
		<br>This will issue SELECT case-3 command to CIPURSE card.<br>

		@param pui1P2 - P2 value
		@param pui1ExpLength - Expected response data length.
		<br>If value of this parameter is negative then command is sent without Le, else the command is sent with Le as the lower byte value of this parameter<br>
		@param ppsAIDValue - Pointer to structure holding the AID and length of the AID

		@return ByteArray of the result
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray selectFileByAID(byte pui1P2, ByteArray ppsAIDValue, short pui1ExpLength) throws CipurseException;


	/**
		 Interface used to activate the currently selected ADF in the CIPURSE card.

		@return ByteArray of the result
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray activateADF() throws CipurseException;

	/**
		 Interface used to deactivate the currently selected ADF in the CIPURSE card

		@return ByteArray of the result
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray deactivateADF() throws CipurseException;

	/**
		 Interface used to read the file attribute of currently
		selected file (MF/ADF/EF) on the CIPURSE card.

		@param pui1ExpLength - Expected response data length


		@return ByteArray of the result
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray readFileAttributes(byte pui1ExpLength) throws CipurseException;

	/**
		 Interface used to update the file attribute of
				currently selected file (MF/ADF/EF) on the CIPURSE card.

		@param keyNum	 - Number of keys available under current ADF.
								  Number of keys should be equal to the length of the buffer
								  PpsSmrArt + 3
		@param smrArt - Pointer to SMR and ART value (SMR||ART)
		@param propSecurityAttributes - Structure holds the length and reference
										to security attribute information.
										If length is zero then security attribute
										will be updated with zero length ("86 00").
										This is optional parameter,
									    set to NULL if not used.


		@return  ByteArray of the result
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray updateFileAttributes(byte keyNum, ByteArray smrArt,
                                          ByteArray propSecurityAttributes) throws CipurseException;

	/**
		 Interface used to update the key attribute of MF/ADF keys on the CIPURSE card.

		@param pui1KeyNum -Key number of which key attribute to be updated
		@param pui1KeySecAttribute - New key attribute of the key

		@return ByteArray of the result
	 *  @throws CipurseException {@link CipurseException} {@link CipurseException}

	*/
	public ByteArray updateKeyAttributes(byte pui1KeyNum,
                                         byte pui1KeySecAttribute) throws CipurseException;
	/**
		 Interface used to read content of the currently
			selected binary file from specified offset on the CIPURSE card.

		@param pui1SFID  - SFID value
		@param pui2Offset -Offset into binary file from there data to be read
		@param pui1ExpLength - Expected number of bytes to read


		@return ByteArray of the result
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray readBinary(byte pui1SFID,
                                int pui2Offset,
                                byte pui1ExpLength) throws CipurseException;


	/**
		 Interface used to update content of the currently selected binary file
				from specified offset on the CIPURSE card.

		@param pui1SFID  -  SFID value
		@param pui2Offset - Offset into binary file from there
								data to be updated
		@param ppsNewData - ByteArray to the data to be updated


		@return ByteArray of the result
		@throws CipurseException {@link CipurseException} {@link CipurseException}

	*/
	public ByteArray updateBinary(byte pui1SFID,
                                  short pui2Offset, ByteArray ppsNewData) throws CipurseException;


	/**
		 Interface used to read the content of the referenced record(s)
				present in currently selected record file on the CIPURSE card
		<br> Note: In case of read single record, P2[2:1] set to "00" indicates
			 read a record specified record number. In case of read multiple record
			 P2[2:1] set to "01" indicates read all records referenced
			 from specified record number.<br>

		@param pui1SFID 	 -  SFID value
		@param pui1RecNumber -	Record to be read
		@param peRecordMode  - 	Read mode: Single Record / Multiple Records
		@param pui1ExpLength - 	Expected number of bytes to read

		@return ByteArray of the result
		@throws CipurseException {@link CipurseException} {@link CipurseException}

	*/
	public ByteArray readRecord(byte pui1SFID,
                                byte pui1RecNumber,
                                byte peRecordMode,
                                byte pui1ExpLength) throws CipurseException;


	/**
		 Interface used to update the content of the referenced record
				present in currently selected record file on the CIPURSE card.

		@param pui1SFID 	 - SFID value
		@param pui1RecNumber - Record to be updated
	 	@param updateMode    - Update Mode
		@param ppsNewData 	 - ByteArray to the data to be updated

		@return ByteArray of the result
		@throws CipurseException {@link CipurseException} {@link CipurseException}
	*/
	public ByteArray updateRecord(byte pui1SFID,
                                  byte pui1RecNumber, byte updateMode,
                                  ByteArray ppsNewData) throws CipurseException;



	/**
		 Interface used to append record number 1 to currently selected cyclic record file
		either by creating a new record or by replacing the oldest record
		on the CIPURSE card.

		@param pui1SFID 	- SFID value
		@param ppsNewData 	- ByteArray to the data to be appended


		@return ByteArray of the result
		@throws CipurseException {@link CipurseException} {@link CipurseException}

	*/
	public ByteArray appendRecord(byte pui1SFID, ByteArray ppsNewData) throws CipurseException;



	/**
		 Interface used to read the value from the reference record in the
		<br>currently selected linear value record file on the CIPURSE card.
		In case of read single record, P2[2:1] set to "00" indicates read
		a record specified record number. In case of read multiple record P2[2:1]
		set to "01" indicates read all records referenced
		from specified record number.<br>

		@param pui1SFID 	- SFID value
		@param pui1RecNumber -Value record to be read
		@param peReadMode - Read mode: Single Record / Multiple Records
									with current and previous value(s)
		@param pui1ExpLength - Expected number of bytes to read


		@return ByteArray of the result
		@throws CipurseException {@link CipurseException} {@link CipurseException}

	*/
	public ByteArray readValue(byte peReadMode, byte pui1SFID,
                               byte pui1RecNumber,
                               byte pui1ExpLength) throws CipurseException;


	/**
	 *  Interface used to add the specified value to the value of the referenced record present in a value-record file in the CIPURSE card.
	 *
	 *	Interface used to add the specified value to the value of the referenced record present in a value-record file in the CIPURSE card. In case of file already selected, set the SFID to 0x00 (current file) to perform increase value operation. Interface constructs the INCREASE_VALUE case-3 command if Pi2ExpLength value is negative else case-4 command. If Pi2ExpLength value is positive, then lower byte value taken as Le and higher byte value is ignored.
	 *	Parameters
	 *
	 *	@param pui1SFID 		- Short file identifier of file to be selected, set to 0x00 to operate on currently selected file
	 *	@param pui1RecNumber 	- Record to be read
	 *	@param pi4IncreaseValue - Value to be increased in the value record file
	 *	@param pui1ExpLength 	- Expected response data length.
	 *  <br>If value of this parameter is negative then command is sent without Le, else the command is sent with Le as the lower byte value of this parameter<br>
	 * 	@return ByteArray of the result
	 *	@throws CipurseException {@link CipurseException} {@link CipurseException}
	 *
	 */


	public ByteArray increaseValue(byte pui1SFID, byte pui1RecNumber,
                                   ByteArray pi4IncreaseValue,
                                   short pui1ExpLength) throws CipurseException;


	
	/**
		 Interface used to issue LIMITED_INCREASE command on CIPURSE card
		<br> Limited increase value used to add the specified
		value to the value of the referenced record present
		in a currently selected value-record file.<br>

		@param pui1SFID 		- Short file identifier of file to be selected, set to 0x00 to operate on currently selected file
		@param pui1RecNumber 	- Value record number
		@param pi4RefundValue 	- Value to be increased in the value record file
		@param pui1ExpLength 	- Expected response data length.
		<br>If value of this parameter is negative then command is sent without Le, else the command is sent with Le as the lower byte value of this parameter<br>
	 *
	 * 	@return ByteArray of the result
	 *	@throws CipurseException {@link CipurseException} {@link CipurseException}

	*/
	public ByteArray limitedIncreaseValue(byte pui1SFID, byte pui1RecNumber,
                                          ByteArray pi4RefundValue, short pui1ExpLength) throws CipurseException;
	


	/**
		 Interface used to subtract the specified value to the value of the
				referenced record present in a referenced
				value-record file in the CIPURSE card.

		@param pui1SFID 		- Short file identifier of file to be selected, set to 0x00 to operate on currently selected file
		@param pui1RecNumber 	- Record number
		@param pi4DecreaseValue - Value to be decrease in the value record file
		@param pui1ExpLength 	- Expected response data length.
		<br>If value of this parameter is negative then command is sent without Le, else the command is sent with Le as the lower byte value of this parameter<br>
	 * 	@return ByteArray of the result
	 *	@throws CipurseException {@link CipurseException} {@link CipurseException}

	*/
	public ByteArray decreaseValue(byte pui1SFID, byte pui1RecNumber,
                                   ByteArray pi4DecreaseValue,
                                   short pui1ExpLength) throws CipurseException;

	
	/**
		 Interface used to issue LIMITED_DECREASE command on CIPURSE card.
		<br>Interface used to subtract the specified value to the value of the
		referenced record present in a referenced value-record file.<br>

		@param pui1SFID 		- Short file identifier of file to be selected, set to 0x00 to operate on currently selected file
		@param pui1RecNumber 	- Record number
		@param pi4RefundValue	 - Value to be limited decrease in the value record file
		@param pui1ExpLength 	- Expected response data length.
		<br>If value of this parameter is negative then command is sent without Le, else the command is sent with Le as the lower byte value of this parameter<br>
	 *
	 * 	@return ByteArray of the result
	 *	@throws CipurseException {@link CipurseException} {@link CipurseException}


	*/
	public ByteArray limitedDecreaseValue(byte pui1SFID,
                                          byte pui1RecNumber,
                                          ByteArray pi4RefundValue,
                                          short pui1ExpLength) throws CipurseException;
	

	/**
		 Interface used to commit the current transaction using perform transaction command
		<br> Note: No effect if transaction is not in progress.<br>

		@return ByteArray of the result
	 *	@throws CipurseException {@link CipurseException} {@link CipurseException}

	*/
	public ByteArray performTransaction() throws CipurseException;

	/**
		 Interface used to cancel the current transaction using cancel transaction command.
		<br> Note: No effect if transaction is not in progress.<br>

		@return ByteArray of the result
	 *	@throws CipurseException {@link CipurseException} {@link CipurseException}


	*/
	public ByteArray cancelTransaction() throws CipurseException;

	/**
		Interface used to set up secure session with CIPURSE card.
		This interface performs both GET_CHALLENGE and MUTUAL_AUTHENTICATION command on CIPURSE card.
		In case of HW SAM, it will make use of AUTHENTICATE_SAM and AUTHENTICATE_CBP command to compute
		the cryptograms to perform authentication process. If Secure messaging already in progress,
		then these interface performs the nested authentication. In this case caller should set secure messaging level
		(minimum eSM_MAC_MAC) by invoking SetSMIValue method. This method does not change secure messaging level
		and also does not perform the minimum secure messaging level validation.

		@param pui1AuthKeyNum - Authentication key number
		@param ppAuthKeyInfo - KeyDiversificationInfo to holding
									information about Master Key ID and
									Diversification data

		@return SUCCESS or Error as boolean
	 *  @throws CipurseException {@link CipurseException}

	*/
	public boolean establishSecureChannel(byte pui1AuthKeyNum,
                                          KeyDiversificationInfo ppAuthKeyInfo) throws CipurseException;

	/**
		Interface used to update an ADF's key.
		<br>Interface provides option to update diversified plain (Method-3), Encrypted key (Method-2).
		Following are interface's behavior based on diversification mode and diversification data:
		<br>1. Diversification mode set to "No key diversification", then diversification data ignored even if it is provided.<br>
		<br>2. Diversification mode set to "key diversification" and diversification data is not provided then HW SAM try to use
		last used diversification data if any. SW SAM throws Exception.
		Secure Message shall be in progress to executed update key command successfully <br>

		@param updateKeyMode - Update Key operation modes: Plain Key, Encrypted Key or Update Key in SM_ENC mode
		@param keyNo - Key to be updated on the CIPURSE card
		@param encKeyInfo - Key encryption Key info (Key number and Key reference identifier). Ignored if updateKeyMode=PLAIN
		@param keyInfo - Attributes of key like KEY_ADDL_INFO, KEY_ALGO_ID, and Key Length
		@param keyDivInfo - Key diversification information. This is optional field. Set to NULL for no diversification required


		@return Response APDU
	 *  @throws CipurseException {@link CipurseException} In case following errors;
	 * 			<br>Option SM_ENC (Diversify Key Set Method-1) not supported by underlying SAM<br>

	*/
	public ByteArray updateKey(UPDATEKEY_MODE updateKeyMode, byte keyNo, EncryptionKeyInfo encKeyInfo, KeyAttributeInfo keyInfo, KeyDiversificationInfo keyDivInfo) throws CipurseException;



	/**
	 * Interface to construct and send CREATE_FILE command to create an ADF. Based on the KEYPERSO_MODE,
	 * it makes use of variants of DIVERSIFY_KEYSET methods.
	 * <br>Following are interface's behavior based on diversification mode and diversification data:<br>
	 *		<br>1.Diversification mode set to "No key diversification", then diversification data ignored even if it is provided<br>
	 *		<br>2.Diversification mode set to "key diversification" and
	 *			diversification data is not provided then HWSAM try to use last used diversification data if any.
	 *			SW SAM throws Exception<br>
	 *
	 *	@param createADFMode - Create ADF operation modes - Plain Key, Encrypted Key or Create ADF in SM_ENC mode
	 *			<br>PLAIN: Includes Diversified Plain Key within Create ADF Command (method-3)<br>
				<br>ENC_KEY: Includes Diversified and Encrypted Key within Create ADF Command (method-2)<br>
				<br>SM_ENC:  Makes use of method-1 of Diversify Key Set Command from underlying SAM. SMI is forced to ENC_ENC<br>
	 *	@param objDFFileAttributes - Model holding DF attributes like File ID, Number of EFs, SFID, no of keys, file size, SMR and ART value etc.
	 *	@param keyAttributes - List of key attributes which need to be loaded to ADF during creation
	 *  @param encKeyInfo	- Encryption Key Information. Ignored for createADFMode = PLAIN.
	 *	@param keyDiffInfo - list of Key diversification information. This is optional field. Set to NULL for no diversification required.
	 *	@param noOfKeysToLoad - Number of keys to be sent in Create ADF command as part of 0xA00F DGI
	 *
	 * @return Response APDU.
	 * @throws CipurseException {@link CipurseException} In case following errors;
	 * 			<br>Option SM_ENC (Diversify Key Set Method-1) not supported by underlying SAM<br>
	 */
	public ByteArray createADF(
            CREATE_ADF_MODE createADFMode,
            DFFileAttributes objDFFileAttributes,
            KeyAttributeInfo[] keyAttributes,
            EncryptionKeyInfo encKeyInfo,
            KeyDiversificationInfo[] keyDiffInfo,
            byte noOfKeysToLoad
    ) throws CipurseException;

	/**
	 * Method to create an unsecured EF using mandatory file attributes.
	 *
	 * @param objEFFileAttributes Instance of EFFileAttributes that holds the EF mandatory attributes.
	 * @return ByteArray of the result.
	 * @throws CipurseException {@link CipurseException}
	 */
	public ByteArray createElementaryFile(EFFileAttributes objEFFileAttributes)throws CipurseException;

	/**
	 * Interface used to delete either referenced ADF or currently selected EF or DF on the CIPURSE card.
 	 *	@param aid is used to refer ADF to be deleted by constructing case-3 DELETE command.
 	 *			If this field is set NULL, then currently selected EF or DF will be deleted by constructing case-1 DELETE command.
				Parameters
	  * @return ByteArray of the result.
	 * @throws CipurseException {@link CipurseException}
	 */
	public ByteArray deleteFile(ByteArray aid) throws CipurseException;

	/**
	 *  Interface used to delete referenced file on the CIPURSE card.
 	 * 	File ID is used to refer file to be deleted.
	 *
	 *
	 * @param fid - FileID of ADF / EF to be deleted
	 * @return ByteArray of the result.
	 * @throws CipurseException {@link CipurseException}
	 */

	public ByteArray deleteFilebyFID(int fid) throws CipurseException;

	/**
	 *  Interface used to format the CIPURSE card.
 	 *	This will remove user created EF and ADFs under MF.
 	 *
	 * @param formatAllHearder - Holds the reference of confirmation data
	 * @return ByteArray of the result.
	 * @throws CipurseException {@link CipurseException}
	 */

	public ByteArray formatAll(ByteArray formatAllHearder)throws CipurseException;

}
