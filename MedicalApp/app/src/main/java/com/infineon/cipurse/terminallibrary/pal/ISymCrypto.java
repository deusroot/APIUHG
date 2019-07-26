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
package com.infineon.cipurse.terminallibrary.pal;

import com.infineon.cipurse.terminallibrary.CipurseException;

/**
 * Interface for Symmetric Crypto services in PAL.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public interface ISymCrypto {

	/**
	 * @author Ejanthak
	 *
	 */
	public enum ALGO_TYPE{
		/**
		 *
		 */
		AES_128;
	}
	/**
	 * Method to perform AES encryption on given plain data with given key.
	 * @param type  ALGORITHAM TYPE
	 * @param key - key value
	 * @param iv Initial Vector Value (Optional)
	 * @param data - plain text
	 * @return byte[] - cipher text
	 * @throws CipurseException In case of any error while processing
	 */
	public byte[] symCryptoEncryption(ALGO_TYPE type, byte[] key, byte[] iv, byte[] data) throws CipurseException;
	/**
	 * Method to perform AES decryption on given cipher data with given key.
	 * @param type  ALGORITHAM TYPE
	 * @param key - key value
	 * @param iv  Initial Vector Value (Optional)
	 * @param data - cipher text
	 * @return byte[] - plain text
	 * @throws CipurseException	In case of any error while processing
	 */
	public byte[] symCryptoDecryption(ALGO_TYPE type, byte[] key, byte[] iv, byte[] data) throws CipurseException;

	/**
	 * Method to get the random number.
	 * @param randomSize size of the random number to be generated.
	 * @return generated random number as byte []
	 */
	public byte[] getRandomNumber(int randomSize);

	/**
	 * Method to get the Key Value from Key ID.
	 * @param keyID key id of respective key value.
	 * @return Key Value as a byte []
	 * @throws CipurseException {@link CipurseException}
	 */
	public byte[] getKeyValue(int keyID) throws CipurseException;

	/**
	 * Method to set the key value to respective key id.
	 * @param keyID keyid to which key value to be set.
	 * @param keyValue Key value to be set.
	 * @throws CipurseException {@link CipurseException}
	 */
	public void setKeyValue(int keyID, byte[] keyValue) throws CipurseException;
	/**
	 * Method to get the KVV from the Key Value.
	 * @param bytes Key value to calculate the KVV.
	 * @return KVV value as byte[].
	 * @throws CipurseException {@link CipurseException}
	 */
	public byte[] getKVV(byte[] bytes) throws CipurseException;

}
