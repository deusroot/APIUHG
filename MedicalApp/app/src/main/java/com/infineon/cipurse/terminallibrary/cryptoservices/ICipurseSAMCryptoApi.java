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
import com.infineon.cipurse.terminallibrary.CipurseException;
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

/**
* Generic Interface for CIPURSE SAM Crypto API.
*
* @since 1.0.0
* @version 1.0.1
*/
public interface ICipurseSAMCryptoApi {

	/**
	Interface used to compute cryptogram which are required to build MUTUAL_AUTHENTICATION command.
	<br>In case of HW SAM, issues an AUTHENTICATE_SAM command to the CIPURSE SAM card to get cryptogram.
	In case of SW SAM, computes	the cryptogram by using AES operation.<br>

		@param ppsAuthKeyInfo - Pointer to structure holding information about
								Master Key ID and Diversification data
		@param pui1SmiForGetChallengeResp - SMI value of GET_CHALLENGE
											command response.
											If set to FFH, the GET_CHALLENGE
											response is not be treated as SM-APDU.
		@param pui1SmiForMutualAuth - SMIvalue of MUTUAL_AUTH command response.
									  If set to FFH, the MUTUAL_AUTH command
									  is not be treated as SM-APDU.
		@param pui1CBPKeyNum - Key reference of CIPURSE Based Product to be
							   sent in MUTUAL_AUTH command.
							   This is required incase of nested authentication
							   to compute cryptogram over complete MUTUAL_AUTH command.
		@param ppsGetChallengeResponse - Response data of GET_CHALLENGE command


	    @return SUCCESS or Error of ByteArray as result.
	 *  @throws CipurseException {@link CipurseException}

	*/
	public byte[] authenticateSAM(KeyDiversificationInfo ppsAuthKeyInfo,
                                  byte pui1SmiForGetChallengeResp,
                                  byte pui1SmiForMutualAuth,
                                  byte pui1CBPKeyNum,
                                  ByteArray ppsGetChallengeResponse) throws CipurseException;

	/**
		Interface used to verify cryptogram received as part of MUTUAL AUTHENTICATION command response
		<br>In case of HW SAM, issues an AUTHENTICATE_CBP command to the CIPURSE SAM card to verify cryptogram.
		In case of SW SAM, computes and verifies the cryptogram by using AES operation.<br>

		@param ppsMutualAuthResponse - byte[] holding mutual authentication
											response data and length of response

		@return SUCCESS or Error of ByteArray as result.
	 *  @throws CipurseException {@link CipurseException}

	*/
	public boolean authenticateCBP(ByteArray ppsMutualAuthResponse) throws CipurseException;

	/**
		Interface used to compute cryptogram for wrap a command.
		<br>In case of HW SAM, issues the GENERATE_SM_ELEMENTS command to the
		CIPURSE SAM card to compute cryptogram. In case of SW SAM,
		computes the cryptogram by using AES operation.
		This interface support generation of cryptogram for
		all SM modes (SM_PLAIN, SM_MAC and SM_ENC).<br>

		@param peSMI - SM level for Command wrapping and response unwrapping
		@param ppsCommandAPDU - Original APDU command to be wrapped

		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public byte[] generateSmElements(byte peSMI,
                                     ByteArray ppsCommandAPDU) throws CipurseException;

	/**
		Interface used to verify the cryptogram in wrapped response
		<br>In case of HW SAM, issues the VERIFY_SM_ELEMENTS command
		to the CIPURSE SAM card to verify cryptogram. In case of SW SAM,
		verifies the cryptogram by using AES operation.
		This interface supports the verification of cryptogram for
		all SM modes (SM_PLAIN, SM_MAC and SM_ENC).<br>

		@param p2_cryptoMode -  00H Mode: Final or only block,
										01H Mode: Chained block
		@param respApdu - As input holds the SM Response
										APDU from CBP.
									Decrypted response data as output in case
									SM_ENC mode. In all other mode,
									response data is not altered.

		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public byte[] verifySmElements(CHAINING_MODE p2_cryptoMode,
                                   ByteArray respApdu) throws CipurseException;

	/**
		Interface used to issue an END_SESSION command to the CIPURSE SAM card.
		<br>Terminate the current SM session progress in HW SAM or SW SAM.<br>

		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public byte[] endSession() throws CipurseException;

	/**
		Interface used to diversify key and encrypt diversified key
				with specified key encryption key.
		<br>In HW SAM, this operation is done by issuing DIVERSIFY_KEY_SET
		with method-2 options. Whereas in SW SAM, operation is done by using
		key diversification function and encrypting
		diversified key using AES encryption.<br>

		@param pui2EncKeyID - Key encryption key reference in SAM
		@param keyDiversifyInfo  - Holds Key diversification information
											like Master KeyID, diversification data
											and mode.

		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public byte[] diversifyEncryptedKeySet(short pui2EncKeyID,
                                           KeyDiversificationInfo keyDiversifyInfo) throws CipurseException;

	/**
		Interface used to diversify plain key
		<br> In HW SAM, this operation is done by issuing DIVERSIFY_KEY_SET
		with method-3 option. Whereas in SW SAM, operation is done by invoking
		key diversification function. <br>

		@param keyDiversifyInfo  - Holds Key diversification information
								like Master KeyID, diversification data and mode

		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public byte[] diversifyPlainKeySet(KeyDiversificationInfo keyDiversifyInfo) throws CipurseException;

	/**
		Interface used to issue DIVERSIFY_KEY_SET command to derive single key
		for LOAD_KEY command in SM_ENC mode
		<br>Key derived using diversification method-1 (P1=0x33).<br>
		Note:This interface supported only in HW SAM mode.

		@param pprgui1LoadKeyCmdHeader - Load Key command header information,
											which is required to wrap Load
											key command in SM_ENC mode
		@param pui1LoadKeyCmdSMI - SMI value to wrap load key command
		@param loadKeyInfo 		  - Hold information of all load key id and FN.
		@param ppsKeyDiversifyInfo  - Holds information which are required
										to diversify the key from SAM


		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public byte[] buildLoadKey(ByteArray pprgui1LoadKeyCmdHeader,
                               byte pui1LoadKeyCmdSMI, LoadKeyInfo loadKeyInfo,
                               KeyDiversificationInfo ppsKeyDiversifyInfo) throws CipurseException;

	/**
		Interface used to issue DIVERSIFY_KEY_SET command to derive single key
		for UPDATE_KEY command in SM_ENC mode
		<br>derived using diversification method-1 (P1=0x13).<br>
		Note:This interface supported only in HW SAM mode.

		@param pprgui1UpdateKeyCmdHeader - Update Key command header
												information, which is required
												to wrap Update key command
												in SM_ENC mode
		@param pui1UpdateKeyCmdSMI - SMI value to wrap update key command
		@param ppKeyAttributeInfo - structure holds the attributes of the key
										to update and also information which are
										required to diversify the key from SAM
		@param keyDivdata  - Holds information which are required
										to diversify the key from SAM


		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public byte[] buildUpdateKey(ByteArray pprgui1UpdateKeyCmdHeader,
                                 int pui1UpdateKeyCmdSMI,
                                 KeyAttributeInfo ppKeyAttributeInfo, KeyDiversificationInfo keyDivdata) throws CipurseException;

	/**
		Interface used to issue DIVERSIFY_KEY_SET command to derive
		ultiple keys for CREATE_ADF command in SM_ENC mode
		<br>Key derived using diversification method-1 (P1=0x23).<br>
		Note:This interface supported only in HW SAM mode.

		@param createFilecmdHeader - Create ADF command header
												information, which is required
												to wrap Create ADF command
												in SM_ENC mode
		@param cipurseVersion - CIPURSE�specification version indicator
		@param smi - SMI value to wrap Create ADF command
		@param dfAttrib - Pointer to structure holding DF attributes
										like File ID, Number of EFs, SFID,
										no of keys, file size,SMR and ART value etc
		@param keyDiffInfo  - Holds information which are required
										to diversify the key from SAM
	 *  @param encKeyInfo   - Holds the Encryption Key Information.
		@param keyInfos - List of key attributes which need to be
										loaded to ADF during creation
		@param maxKeysToPersonalize - Number of keys to be sent in Create ADF
										command as part of 0xA00F DGI


		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public byte[] buildCreateADF(ByteArray createFilecmdHeader,
                                 int cipurseVersion, int smi,
                                 DFFileAttributes dfAttrib,
                                 KeyDiversificationInfo[] keyDiffInfo, EncryptionKeyInfo encKeyInfo,
                                 KeyAttributeInfo[] keyInfos,
                                 int maxKeysToPersonalize) throws CipurseException;

	/**
		Interface used to read current session key from the CIPURSE SAM card.

		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray getSessionKey() throws CipurseException;

	/**
		Interface used to set the session key to SW SAM.

		@param psSessionKey - Structure holds the length of session key
									and session key value to be set to SW SAM
	 * @throws CipurseException	{@link CipurseException}

	*/
	public void setSessionKey(ByteArray psSessionKey) throws CipurseException;

	/**
		Interface used to initialize the crypto API module.


		@param pprgui1CryptoAPIBuffer - buffer to hold crypto API
												session information
	 * @param cryptoMode	Mode of the crypto
	 * 	@param commsHandle - Communication handle to HW SAM.
										This is optional in case of SW SAM.
										This is mandatorily required
										for HW SAM and Hybrid mode.
		@param ppsLoggerHandle - Handle to logger to display debug information
		@param ppsAESHandle - AES handle which is required to perform SW crypto
									operation in case of SW SAM and Hybrid mode

		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	*/
	public ICipurseSAMCryptoApi initializeCryptoAPI(
            ICipurseSMApi pprgui1CryptoAPIBuffer, CRYPTO_MODE cryptoMode, ICommunicationHandler commsHandle,
            ILogger ppsLoggerHandle,
            ISymCrypto ppsAESHandle) throws CipurseException;

}
