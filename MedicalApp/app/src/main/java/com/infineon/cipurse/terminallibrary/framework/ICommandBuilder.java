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
import com.infineon.cipurse.terminallibrary.CipurseException;

/**
* Interface for building common commands in a convenient way.
*
* @since 1.0.0
* @version 1.0.1
*/
public interface ICommandBuilder {

	/**
	 Interface used to build VERIFY_SAM_PASSWORD command.

	@param ppsPassword - Structure holding length of
						password and pointer to location where password stored
	@return APDU response as a Byte Array
	@throws CipurseException	{@link CipurseException}

	*/
	public ByteArray buildVerifySAMPassword(ByteArray ppsPassword) throws CipurseException;
	/**
		Interface used to build Select an ADF command using AID value.
		<br>Interface builds the case-3 select file command.<br>

	  	@param pui1P2	- P2 Value
		@param ppsAIDValue -  AID to be Selected
		@param pui1ExpLength - Expected Response Length

		@return APDU response as a Byte Array
		@throws CipurseException	{@link CipurseException}

	*/
	public ByteArray buildSelectFileByAID(byte pui1P2, ByteArray ppsAIDValue, short pui1ExpLength) throws CipurseException;


	/**
		Interface used to build GET_CHALLENGE command.

		@return Challenged command As Byte Array

	*/
	public byte[] buildGetChallenge();

	/**
		Interface used to build MUTUAL_AUTHENTICATION command.

		@param Pui1AuthKeyNum - Authentication key number
		@param PpsMutualAuthData - Mutual authentication command data
		@return SUCCESS or Error code as Byte Array It gives Mutual Authenticate command built
	 *  @throws CipurseException {@link CipurseException}

	*/
	public ByteArray buildMutualAuthentication(byte Pui1AuthKeyNum,
                                               ByteArray PpsMutualAuthData) throws CipurseException;

	/**
		 Interface used to build command to retrieve Key Infomration from SAM card.

		@param mFNkeyRef - FN, Key File Set (60H, 70H or 80H)
		@param keyID - Reference of the key

		@return Get KeyInfo command
	 *  @throws CipurseException	{@link CipurseException}

	*/
	public ByteArray buildGetKeyInfo(int mFNkeyRef, int keyID) throws CipurseException;

}
