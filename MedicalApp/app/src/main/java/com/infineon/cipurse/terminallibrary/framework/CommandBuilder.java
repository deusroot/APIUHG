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
package com.infineon.cipurse.terminallibrary.framework;

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseCommandsConstants;
import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;

/**
* Implementation for building common commands in a convenient way.
*
* @since 1.0.0
* @version 1.0.1
*/
public class CommandBuilder implements ICommandBuilder,CipurseCommandsConstants {

	private final byte[] getChallengeCommand = new byte[] { 0x00, (byte) 0x84, 0x00, 0x00, 0x16 };
	/** Command header for Select command */
	protected byte[] selectHeader = new byte[] { 0x00, INS_SELECT, 0x00, 0x00 };

	/**
	 * Constructor
	 */
	public CommandBuilder() {
	}

	@Override
	public ByteArray buildVerifySAMPassword(ByteArray password) throws CipurseException {
		int cmdDataLength = password.size();
		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength];
		Utils.arrayCopy(VERIFYPASSWORD_HEADER, 0, cmd, 0, VERIFYPASSWORD_HEADER.length);
		Utils.arrayCopy(password.getBytes(), 0, cmd, APDU_OFFSET_CDATA, password.size());
		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;
		return new ByteArray(cmd);
	}

	@Override
	public ByteArray buildSelectFileByAID(byte pui1P2, ByteArray commandData, short expLength) {

		byte[] commandApdu=null;
		if(expLength < 0)
			commandApdu = new byte[APDU_OFFSET_CDATA + commandData.getBytes().length];
		else
		{
			commandApdu = new byte[APDU_OFFSET_CDATA + commandData.getBytes().length + 1];// +1 is for Le.
			commandApdu[commandApdu.length - 1] = (byte) expLength;
		}

		// Frame the update binary command.
		System.arraycopy(selectHeader, 0, commandApdu, 0, APDU_OFFSET_P3);
		commandApdu[APDU_OFFSET_P1] = 0x04;
		commandApdu[APDU_OFFSET_P2] = pui1P2;
		commandApdu[APDU_OFFSET_P3] = (byte)commandData.size();

		// Frame the update binary command.
		System.arraycopy(commandData.getBytes(), 0, commandApdu, APDU_OFFSET_CDATA,
				commandData.size());
		return new ByteArray(commandApdu);
	}


	@Override
	public byte[] buildGetChallenge()  {
			return getChallengeCommand;
	}

	@Override
	public ByteArray buildMutualAuthentication(byte Pui1AuthKeyNum,
			ByteArray mutualAuthData)  {
		byte[] mutualAuthCmd = new byte[ mutualAuthHeader.length +  mutualAuthData.size() + 1 ];
		System.arraycopy(mutualAuthHeader, 0, mutualAuthCmd, 0, mutualAuthHeader.length);
		System.arraycopy(mutualAuthData.getBytes(), 0,mutualAuthCmd , CipurseConstant.OFFSET_CMD_DATA, mutualAuthData.size());
		mutualAuthCmd[CipurseConstant.OFFSET_LC] = (byte) (  mutualAuthData.size() & 0x0FF );
		mutualAuthCmd[mutualAuthCmd.length - 1] = (byte)CipurseConstant.AES_BLOCK_LENGTH;
		mutualAuthCmd[CipurseConstant.OFFSET_P2] = Pui1AuthKeyNum;

		return new ByteArray(mutualAuthCmd);
	}

	@Override
	public ByteArray buildGetKeyInfo(int FNkeyRef, int keyID) throws CipurseException {
		int cmdDataLength = 3;

		byte[] cmd=new byte[APDU_OFFSET_CDATA + cmdDataLength + 1];

		int offset=Utils.arrayCopy(GETKEYINFO_HEADER, 0, cmd, 0, GETKEYINFO_HEADER.length);

		cmd[offset ++] = (byte)FNkeyRef;
		cmd[offset ++]=(byte)((keyID >> 0x08) & 0x00FF);
		cmd[offset ++] = (byte)(keyID & 0x00FF);

		cmd[APDU_OFFSET_P3]=(byte) cmdDataLength;
		return new ByteArray(cmd);
	}

}
