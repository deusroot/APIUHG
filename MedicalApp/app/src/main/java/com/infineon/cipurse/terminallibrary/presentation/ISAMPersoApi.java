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
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.model.LoadKeyInfo;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.LOADKEY_MODE;


/**
 * Interface defining APIs for SAM personalization related commands.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public interface ISAMPersoApi {

	/**
	 Interface used to load a Key into SAM card by issuing LOAD_KEY command.
	<br>Based on the KEYPERSO_MODE and underlying SAM type, it makes use of variants of DIVERSIFY_KEYSET command.<br>

	@param loadKeyMode - Load Key operation mode: Plain, Encrypted Key or Load Key in SM_ENC mode.
				<br>PLAIN: Includes Diversified Plain Key within Load Key Command. In case of Hardware SAM makes use of method-3<br>
				<br>ENC_KEY: Includes Diversified and Encrypted Key within Load Key Command. In case of Hardware SAM makes use of method-2<br>
				<br>SM_ENC:  Makes use of method-1 of Diversify Key Set Command from underlying SAM. SMI is forced to ENC_ENC<br>

	@param loadKeyInfo - Load key information like FN, Key ID, Algo ID. and Encryption Key ID within Card.
				<br>If loadKeyMode is PLAIN, then Encryption Key ID is ignored<br>
	@param ppsKeyDiversifyInfo - Key diversification information. This is optional field. Set to NULL for no diversification required
	@param encryptedKeyID - Key identifier of key encryption key, value ignore for if PeLoadKeyMode is not eEncryptedKey


	@return Response APDU.
	@throws CipurseException In case of following conditions;
				<br>Option SM_ENC (Diversify Key Set Method-1) not supported by underlying SAM<br>
	 */
	public ByteArray loadKey(LOADKEY_MODE loadKeyMode, LoadKeyInfo loadKeyInfo,
                             KeyDiversificationInfo ppsKeyDiversifyInfo,
                             short encryptedKeyID) throws CipurseException;



	/**
	    Interface used to issue VERIFY_SAM_PASSWORD command on the CIPURSE SAM card.

		@param ppsPassword - Structure holding length of password and pointer
									to location where password stored
		@return Response APDU
	  	@throws CipurseException {@link CipurseException}

	 */
	public  ByteArray verifySAMPassword(ByteArray ppsPassword) throws CipurseException;

	/**
		 Interface used to generate a Key in the specified key set number of
				the perso key file under SAM ADF
		<br>The key will be generated in free slot in the specified keys file
			and returns the key number and KVV in the response data.<br>

		@param pui1keyLen - Key length of key to be generated
		@param pui1KeySetNumber - External KeySetNo, referencing the perso key
										file in which the key shall be created

		@return Response APDU
	  	@throws CipurseException {@link CipurseException}

	 */
	public  ByteArray generateKey(byte pui1keyLen,
                                  byte pui1KeySetNumber) throws CipurseException;

	/**
		 Interface used to retrieve information about a key in CIPURSE SAM card.

		@param mFNkeyRef - FN, Key File Set (60H, 70H or 80H)
		@param pui2KeyID - Reference of the key


		@return Response APDU
	  	@throws CipurseException {@link CipurseException}


	 */
	public  ByteArray getPersoSAMKeyInfo(byte mFNkeyRef, short pui2KeyID) throws CipurseException;

}
