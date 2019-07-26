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

/**
 * Interface to perform Back-Office operation like, compute MAC, Verify MAC, Encrypt and Decrypt.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public interface IGenericApi {

	/**
	 *
	 *
	 */
	public enum SYM_CRYPTO_MODE{
		/**
		 *
		 */
		GEN_MAC((byte)0x00),
		/**
		 *
		 */
		ENC_MAC((byte)0x01),
		/**
		 *
		 */
		VERIFY_MAC((byte)0x02),
		/**
		 *
		 */
		DECRYPT_VERIFY_MAC((byte)0x03);

		private byte mode;
		SYM_CRYPTO_MODE(byte mode){
			this.mode = mode;
		}
		/**
		 * @return mode as byte
		 */
		public byte getMode(){
			return mode;
		}
	}

	/**
	 Interface used to  perform symmetric crypto operation to compute
			MAC over provided data.
	<br>It will make use of PERFORM_SYMCRYPTO command to get MAC over data.<br>

	Note:This interface supported only on the HW SAM.

	@param mode - Perform SymCrypto mode indicator, GEN_MAC, ENC_MAC, VERIFY_MAC, DECRYPT_VMAC
	@param pui2KeyID - Identifier of key to be used for indicated perform sym-crypto operation
	@param pui1AlgorithmtoUse - Algorithm to Use
	@param ppsInputData - Input data : eGEN_MAC - Data over which MAC to be computed:
 									    [Data to be MACed(0x00 to 0xE8 Bytes)]
 										eENC_MAC - Data to be encrypted:
 										[Data to be Encrypted(0x00 to 0xE0 Bytes)]
 										eVERIFY_MAC - Data over which MAC to be computed and verification MAC:
 										[Data to verify MAC(0x00 to 0xE8 Bytes) + KeyCounter(4 Bytes) + SAM ID(8 Bytes) + Calculated MAC(8 Bytes)]
 										eDECRYPT_VERIFY_MAC - Data to be decrypted:
 										[Encrypted data(0x10 to 0xF0 Bytes)]

	@return SUCCESS or Error code as Byte Array
	@throws CipurseException {@link CipurseException}

	*/
	public ByteArray performSymCrypto(SYM_CRYPTO_MODE mode, short pui2KeyID,
                                      byte pui1AlgorithmtoUse,
                                      ByteArray ppsInputData) throws CipurseException;


	/**
		 Interface used to retrieve information about a key in CIPURSE HW SAM card.
		<br>This will issue SELECT case-1 command to CIPURSE card.<br>

		@param fnKeyRef - FN, Key File Set (60H, 70H or 80H)
		@param keyID - Reference of the key

		@return SUCCESS or Error code as Byte Array
		@throws CipurseException {@link CipurseException}

	*/
	public ByteArray getKeyInfo(byte fnKeyRef,
                                short keyID) throws CipurseException;
}
