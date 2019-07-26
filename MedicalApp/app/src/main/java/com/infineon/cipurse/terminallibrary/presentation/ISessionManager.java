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
import com.infineon.cipurse.terminallibrary.cryptoservices.ICipurseSMApi;
import com.infineon.cipurse.terminallibrary.pal.ICommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.pal.ISymCrypto;

/**
 * Interface defining APIs for SessionManager.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public interface ISessionManager {

	/**
	 * Chaining mode is required for the verify sm elements for more than 256 response.
	 *
	 */
	public enum CHAINING_MODE{
		/**
		 * Command Contains Encrypted Key(s).
		 */
		CHAINING_MODE((byte)0X01),
		/**
		 * The Command containing Key(s) must be Encrypted as part of CIPURSE(TM) Secure Messaging.
		 */
		PLAIN_MODE((byte)0X00);

		private byte mode;

		CHAINING_MODE(byte mode){
			this.mode = mode;
		}

		/**
		 * Gives value associated with this option.
		 * @return mode value.
		 */
		public byte getMode(){
			return this.mode;
		}
	}
	/**
	 * Enumeration to define various options to populate Keys into CBP or SAM.
	 * For example, Create ADF, Update Key and Load Key commands.
	 */
	public enum UPDATEKEY_MODE{
		/**
		 * Command contains Plain Key(s).
		 */
		PLAIN((byte)0),
		/**
		 * Command Contains Encrypted Key(s).
		 */
		ENC_KEY((byte)1),
		/**
		 * The Command containing Key(s) must be Encrypted as part of CIPURSE(TM) Secure Messaging.
		 */
		SM_ENC((byte)2);

		private byte mode;

		UPDATEKEY_MODE(byte mode){
			this.mode = mode;
		}

		/**
		 * Gives value associated with this option.
		 * @return mode value.
		 */
		public byte getMode(){
			return this.mode;
		}
	}
	/**
	 * Enumeration to define various options to populate Keys into CBP or SAM.
	 * For example, Create ADF, Update Key and Load Key commands.
	 */
	public enum LOADKEY_MODE{
		/**
		 * Command contains Plain Key(s).
		 */
		PLAIN((byte)0),
		/**
		 * Command Contains Encrypted Key(s).
		 */
		ENC_KEY((byte)1),
		/**
		 * The Command containing Key(s) must be Encrypted as part of CIPURSE(TM) Secure Messaging.
		 */
		SM_ENC((byte)2);

		private byte mode;

		LOADKEY_MODE(byte mode){
			this.mode = mode;
		}

		/**
		 * Gives value associated with this option.
		 * @return mode value.
		 */
		public byte getMode(){
			return this.mode;
		}
	}
	/**
	 * Enumeration to define various options to populate Keys into CBP or SAM.
	 * For example, Create ADF, Update Key and Load Key commands.
	 */
	public enum CREATE_ADF_MODE{
		/**
		 * Command contains Plain Key(s).
		 */
		PLAIN((byte)0),
		/**
		 * Command Contains Encrypted Key(s).
		 */
		ENC_KEY((byte)1),
		/**
		 * The Command containing Key(s) must be Encrypted as part of CIPURSE(TM) Secure Messaging.
		 */
		SM_ENC((byte)2);

		private byte mode;

		CREATE_ADF_MODE(byte mode){
			this.mode = mode;
		}

		/**
		 * Gives value associated with this option.
		 * @return mode value.
		 */
		public byte getMode(){
			return this.mode;
		}
	}

	/**
	 * Crypto Mode where the Communication to be happened.
	 *
	 */
	public enum CRYPTO_MODE{
		/**
		 * Software mode
		 */
		SW_SAM(0),
		/**
		 * Hardware SAM mode
		 */
		HW_SAM(1),
		/**
		 * Hybrid Mode where Authentication done through HW and session continues with SW.
		 */
		HYBRID(2);

		private int mode;

		CRYPTO_MODE(int mode){
			this.mode = mode;
		}

		/**
		 * Method to get the mode.
		 * @return mode as Crypto mode.
		 */
		public int getValue(){
			return this.mode;
		}

		/**
		 * Gives Value of the Enum.
		 * @param value	Value of the Enum
		 * @return	Enum for the given Value
		 */
		public static CRYPTO_MODE getMode(int value) {
			switch (value) {
			case 0:
				return SW_SAM;
			case 1:
				return HW_SAM;
			case 2:
				return HYBRID;
			default:
				return SW_SAM;
			}
		}

	}

	/**
	 * Enum for SM Type as Cipurse or GP
	 *
	 *
	 */
	public enum SMTYPE {
		/**
		 * Mode as Cipurse
		 */
		CIPURSE,
		/**
		 * Mode as GP
		 */
		GP
	}


	/**
	 * Enumerator to specify the desired security level.
	 */
	public enum SMLevel{

		/**
		 * SMI for Command=PLAIN, Response=PLAIN
		 */
		eSM_PLAIN_PLAIN((byte)0x00),
		/**
		 * SMI for Command=PLAIN, Response=MAC
		 */
		eSM_PLAIN_MAC((byte)0x04),
		/**
		 *SMI for Command=PLAIN, Response=ENC
		 */
		eSM_PLAIN_ENC((byte)0x08),
		/**
		 *SMI for Command=MAC, Response=PLAIN
		 */
		eSM_MAC_PLAIN((byte)0x40),
		/**
		 *SMI for Command=MAC, Response=MAC
		 */
		eSM_MAC_MAC((byte)0x44),
		/**
		 *SMI for Command=MAC, Response=ENC
		 */
		eSM_MAC_ENC((byte)0x48),
		/**
		 *SMI for Command=ENC, Response=PLAIN
		 */
		eSM_ENC_PLAIN((byte)0x80),
		/**
		 * SMI for Command=ENC, Response=MAC
		 */
		eSM_ENC_MAC((byte)0x84),
		/**
		 * SMI for Command=ENC, Response=ENC
		 */
		eSM_ENC_ENC((byte)0x88),
		/**
		 * SM Level for NO SM. Command will be sent without Secure Messaging.
		 */
		eSM_NO_SECMSG((byte)0xFF);


		private byte smLevel;

		SMLevel(byte smLevel){
			this.smLevel = smLevel;
		}

		/**
		 * Gives SM Level indicator value
		 * @return SM Level Indicator
		 */
		public byte getSMLevel()
		{
			return this.smLevel;
		}
	}

	/**
	 * Enumerator to specify the CIPURSE Version Number.
	 */
	public enum CIPURSEVersion{

		/**
		 * Version Number
		 */
		eVERSION2R2((byte)0x03);


		private byte cipurseVersion;

		CIPURSEVersion(byte cipurseVersion){
			this.cipurseVersion = cipurseVersion;
		}

		/**
		 * Gives CIPURSE Version Number
		 * @return CIPURSE Version Number
		 */
		public byte getVersion()
		{
			return this.cipurseVersion;
		}
	}

	/**
	 * Enumerator to specify the CIPURSE SAM Version Number.
	 */
	public enum CIPURSESAMVersion{

		/**
		 * CIPURSE SAM Version Number
		 */
		eVERSION2((byte)0x02);


		private byte cipurseSAMVersion;

		CIPURSESAMVersion(byte cipurseSAMVersion){
			this.cipurseSAMVersion = cipurseSAMVersion;
		}

		/**
		 * Gives CIPURSE SAM Version Number
		 * @return CIPURSE SAM Version Number
		 */
		public byte getVersion()
		{
			return this.cipurseSAMVersion;
		}
	}


	/**
	 * Method to Initialize the session.
	 * @param peCryptoMode Indication to use SW SAM or HW SAM or Hybrid
	 * @param smModeType SM Type to indicate CIPURSE or GP authentication
	 * @param psCBPCommsHandle Communication handle to CIPURSE CBP card
	 * @param psHWSAMCommsHandle Communication handle to HW SAM. This is optional in case of SW SAM. This is mandatorily required for HW SAM and Hybrid mode.
	 * @param psLoggerHandle  Handle to logger to display debug information. Set to NULL if logging is not required
	 * @param psAESHandle SymCrypto handle which is required to perform SW crypto operation in case of SW SAM and Hybrid mode. This parameter is optional for HW SAM, set to NULL.
	 * @return Instance of initialized session.
	 * @throws CipurseException {@link CipurseException}
	 */
	public ISessionManager initializeSession(
            CRYPTO_MODE peCryptoMode,
            SMTYPE smModeType,
            ICommunicationHandler psCBPCommsHandle,
            ICommunicationHandler psHWSAMCommsHandle, ILogger psLoggerHandle,
            ISymCrypto psAESHandle) throws CipurseException;

	/**
	 * Method to switch the HW SAM  mode to SW mode.
	 * @return boolean as success or failure.
	 * @throws CipurseException {@link CipurseException}
	 */
	public boolean switchToSWSAM() throws CipurseException;

	/**
	 * Interface used to restore crypto mode to HW_SAM when session is initialized for Hybrid mode and SW_SAM is in use.
	 * This interface will reset the secure session which is in progress in SW_SAM.
	 * Note: This interface invoked by EstablishSecureChannel API to ensure that authentication always
	 * performed using HW_SAM incase of Hybrid mode.Application developer need not required to call this function explicitly.
	 * @return true if successful operation.
	 * @throws CipurseException {@link CipurseException}
	 */
	public boolean restoreHybridMode() throws CipurseException;

	/**
	 Interface used to bring SAM card to authorized state
	<br>This interface will selected the specified ADF in SAM card and
	then issue the verify password command to bring ADF to authorized state.
	If password is not provided then this interface perform only ADF selection.
	In this case SAM should be configured such that reset will bring
	SAM to authorized state. <br>

	@param ppsAIDValue - AID value of application to be
							selected in HW SAM card
	@param ppsPassword - Password value to perform password verification to
							bring card to authorized state

	@return SUCCESS or Error code as byte array
	@throws CipurseException {@link CipurseException}


	*/
	public ByteArray authorizeHWSAM(ByteArray ppsAIDValue,
                                    ByteArray ppsPassword) throws CipurseException;
	/**
	 Interface used to set SMI value for a command or group of command.

	@param peSMLevel - Enum for SMI value to be used to wrap the command
							and un-wrap the response

	*/
	public void setSMIValue(SMLevel peSMLevel);
	/**
	 Interface used to set CIPURSE PICC version number and CIPURSE SAM version number.
	<br>This will help Command API and SAM Perso API module
		in constructing commands according to specified version.<br>

	@param PeCIPURSEVersion - CIPURSE version
	@param PeCIPURSESAMVersion - CIPURSE SAM version
	@throws CipurseException {@link CipurseException}

	*/
	public void setVersionInfo(CIPURSEVersion PeCIPURSEVersion,
                               CIPURSESAMVersion PeCIPURSESAMVersion) throws CipurseException;

	/**
		Interface used to set session context for Terminal library.
		<br>Further, command will be built and transmitted to relevant target based
		on set context. Handle received in the "SessionManagerInitialse" interface
		shall be provided to this interface to set session context.
		If multiple session contexts are created then this interface helps in
		building commands to a specified session context.<br>

		@param ppsHandle - Session manager handle

	*/
	public void setSessionContext(ISessionManager ppsHandle);

	/**
	 *
	 * @return logger object
	 */
	public ILogger getLogger();
	/**
	 * Method to return Cipuse SM API
	 * @return Instance of Cipurse SM API
	 */
	public ICipurseSMApi getCipurseSMApi();

	/**
	 * Interface used to reset the current secure session.
		@return SUCCESS or Error of boolean as result.
		@throws CipurseException {@link CipurseException}

	 */
	public boolean resetSM() throws CipurseException;

	/**
	 * Gives Current SMI as Enum
	 * @return Enum of current SMI
	 */
	public SMLevel getSMIValue();


	/**
	 * Gives Currently set Crypto Mode.
	 * @return	Crypto Mode
	 */
	public CRYPTO_MODE getCurrentCryptoMode();


	/**
	 * Gives Communication handler for HW SAM
	 * @return SAM CommunicationHandler
	 */
	public ICommunicationHandler getSAMCommsHandler();

	/**
	 * Gives Communication handler for CBP.
	 * @return CBP CommunicationHandler
	 *
	 */
	public ICommunicationHandler getCBPCommsHandler();
}
