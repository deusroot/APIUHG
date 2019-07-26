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
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.pal.ICommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.ILogger;

/**
 * Class Implementing APIs to perform Back-Office operation like, compute MAC, Verify MAC, Encrypt and Decrypt.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public class GenericApi implements IGenericApi,CipurseCommandsConstants {

	private ICommunicationHandler commsSAMHandler;
	private ISessionManager sessionManager;
	/** Command Header constant for Perform Sym Crypto command */
	private static final byte[] PERFORMSYMCRYPTO_HEADER=new byte[]{(byte) 0x80, 0x5A, 0, 0, 0};
	private final static byte[] GETKEYINFO_HEADER=new byte[]{(byte) 0x80, 0x54, 0, 0, 0};

	/**
	 * Parameterized Constructor
	 * @param iSessionManager {@link ISessionManager}
	 */
	public GenericApi(ISessionManager iSessionManager) {
		this.sessionManager = iSessionManager;
		this.commsSAMHandler = sessionManager.getSAMCommsHandler();

	}

	@Override
	public ByteArray performSymCrypto(SYM_CRYPTO_MODE mode, short keyId,
			byte algorithmtoUse,
			ByteArray inputData) throws CipurseException {
		sessionManager.getLogger().log("\"performSymCrypto\" function parameters :\n"
						+ String.format("SymCrypto mode: %s%nKey ID: 0x%04X%nAlgorithm to Use: 0x%02X%nInput data: %s",
								mode, keyId, algorithmtoUse, inputData));
		byte[] cmd = new byte[PERFORMSYMCRYPTO_HEADER.length + 3 + inputData.size() + 1];
		int offset = Utils.arrayCopy(PERFORMSYMCRYPTO_HEADER, 0, cmd, 0, PERFORMSYMCRYPTO_HEADER.length);
		cmd[offset++] = (byte) ((keyId >> 8) & 0x00FF);
		cmd[offset++] = (byte) (keyId & 0x00FF);
		cmd[offset++] = algorithmtoUse;
		offset = Utils.arrayCopy(inputData.getBytes(), 0, cmd, offset, inputData.size());
		cmd[APDU_OFFSET_P1] = mode.getMode();
		cmd[APDU_OFFSET_P2] = 0x00;
		cmd[4] = (byte) (cmd.length - PERFORMSYMCRYPTO_HEADER.length - 1);

		return new ByteArray(transmit(cmd));
	}

	@Override
	public ByteArray getKeyInfo(byte keySetNumber, short keyID) throws CipurseException {
		sessionManager.getLogger().log(
				"\"getKeyInfo\" function parameters :\n"
						+ String.format("Key set number: 0x%02X%nKey ID: 0x%04X", keySetNumber,
								keyID));
		int cmdDataLength = 3;

		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];

		int offset=Utils.arrayCopy(GETKEYINFO_HEADER, 0, cmd, 0, GETKEYINFO_HEADER.length);

		cmd[offset ++] = (byte)keySetNumber;
		cmd[offset ++]=(byte)((keyID >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(keyID & 0x00FF);

		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;
		return new ByteArray( transmit(cmd));
	}

	/**
	 * Method to transmit the data to reader.
	 * @param command to be transmited
	 * @return response as byte [].
	 * @throws CipurseException
	 */
	private byte[] transmit(byte[] command) throws CipurseException{
		sessionManager.getLogger().log(ILogger.COMMAND_MESSAGE,"SAM:","", command);
		byte[] response =commsSAMHandler.transReceive(command);
		sessionManager.getLogger().log(ILogger.RESPONSE_MESSAGE,"SAM:","", response);
		return response;
	}

}