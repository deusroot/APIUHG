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

import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.pal.ICommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.SMTYPE;

/**
* Interface for CIPURSE related Secure Messaging APIs.
*
* @since 1.0.0
* @version 1.0.1
*/
public interface ICipurseSMApi {


	/**
	 * Enumeration to define various options related to Wrapping and Unwrapping of APDU Commands and Response within in secure session.
	 *
	 */
	public enum SM_OPTIONS{

		/**
		 * Command is NOT Wrapped; Response is Unwrapped
		 */
		NO_WRAP_CMD((byte)0),
		/**
		 * Command is Wrapped based on current SMI; Response is NOT Unwrapped
		 */
		NO_UNWRAP_RSP((byte)1),
		/**
		 * Command is NOT Wrapped; Response is NOT unwrapped
		 */
		NO_WRAP_AND_UNWRAP((byte)2),
		/**
		 * Command is Wrapped based on current SMI; Response is Unwrapped
		 */
		WRAP_AND_UNWRAP((byte)3);

		private byte option;

		private SM_OPTIONS(byte option){
			this.option = option;
		}

		/**
		 * Gives value associated with this Enum. element.
		 * @return option value.
		 */
		public byte getMode(){
			return this.option;
		}
	}

	/**
	Interface used to set up secure session with CIPURSE card.
	<br>This interface performs both GET_CHALLENGE and MUTUAL_AUTHENTICATION
	commands on CIPURSE card.<br>

	Note: In case of HW SAM, it will make use of AUTHENTICATE_SAM and
	AUTHENTICATE_CBP command to compute the cryptograms to
	perform authentication process.

	@param  pui1AuthKeyNum - Authentication key number
	@param  ppAuthKeyInfo - Pointer to structure holding information about
							 Master Key ID and 	Diversification data


	@return SUCCESS or Error of ByteArray as result.
	@throws CipurseException {@link CipurseException}
	 */
	public boolean setUpSecureChannel(byte pui1AuthKeyNum,
                                      KeyDiversificationInfo ppAuthKeyInfo) throws CipurseException;

	/**
		Interface used to transmit a command to CIPURSE card
		<br>If secure message is in progress, then wraps the command before
		transition and also unwraps the response.<br>

		@param  ppsCommand - Command to be transmitted
	    @return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	 */
	 public byte[] transmit(byte[] ppsCommand) throws CipurseException;

	/**
		Interface used to transmit a command to CIPURSE card based on SecureMessage(SM) option
		<br>If secure message is in progress, then wraps the command before
		transition and also unwraps the response based on provided SM option.
		SM option indicates whetherto wrap the command or not
		and also unwrap the response or not.<br>

		@param  ppsCommand - Command to be transmitted
		@param  smOption - Indicates whether to wrap the command
									and unwrap the response or not


		@return SUCCESS or Error of ByteArray as result.
		@throws CipurseException {@link CipurseException}

	 */
	public byte[] transmit(byte[] ppsCommand,
                           SM_OPTIONS smOption) throws CipurseException;

	/**

	 * Interface used to reset the current secure session.
		@return SUCCESS or Error of boolean as result.
		@throws CipurseException {@link CipurseException}

	 */
	public boolean resetSM() throws CipurseException;


	/**
		Interface used to initialize the secure messaging module. This internally initializes the Crypto API module.
	 * @param cryptoApi 	Current secure messaging crypto api.
	 * @param smType 		Type of the SM mode.
	 * @param mCBPHandler		Communication Handler in Case of HW SAM
	 * @param ppsLoggerHandle - Handle to logger to display debug information

	 * @return Secure Messaging handle
	 * @throws CipurseException		{@link CipurseException}

	 */
	public ICipurseSMApi initializeSMAPI(
            ICipurseSAMCryptoApi cryptoApi, SMTYPE smType,
            ICommunicationHandler mCBPHandler,
            ILogger ppsLoggerHandle) throws CipurseException;


	/**
	 * Gives ICipurseSAMCryptoApi object associated with this object.
	 * @return	ICipurseSAMCryptoApi
	 */
	public ICipurseSAMCryptoApi getCryptoAPI();


}

