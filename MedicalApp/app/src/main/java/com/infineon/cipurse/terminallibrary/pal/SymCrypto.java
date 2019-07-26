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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;

/**
 * Implementation for Symmetric Crypto services in PAL.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public class SymCrypto implements ISymCrypto{

	private static Cipher aesCipher = null;
	private SecretKeySpec aesKey = null;
	protected byte[][] keySet;
	protected SecureRandom secRandom;

	static private byte[] nullVector = new byte[] {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };
	/**
	 * <p>
	 * AES class constructor. This constructor creates instance of AES Crypto Engine.
	 * @throws CipurseException  {@link CipurseException}
	 */
	public SymCrypto () throws CipurseException{
		init(8);
	}

	/**
	 * <p>
	 * AES class constructor. This constructor creates instance of AES Crypto Engine.
	 * @param maxNumberOfKeys Number of Maximum keys.
	 * @throws CipurseException {@link CipurseException}
	 */
	public SymCrypto (int maxNumberOfKeys) throws CipurseException{
		init(maxNumberOfKeys);
	}

	private void init(int maxNumberOfKeys) throws CipurseException
	{
		try {
			aesCipher = Cipher.getInstance("AES/ECB/NoPadding");
			keySet = new byte[maxNumberOfKeys][];
			secRandom=new SecureRandom();
		} catch (NoSuchAlgorithmException ae) {
			throw new CipurseException(CipurseConstant.CTL_CRYPTO_ERROR);
		} catch (NoSuchPaddingException pe) {
			throw new CipurseException(CipurseConstant.CTL_CRYPTO_ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] symCryptoEncryption(ALGO_TYPE type, byte[] key,byte[] iv, byte[] data) throws CipurseException {

		try {
			aesKey = new SecretKeySpec(key, "AES");
			aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
			return aesCipher.doFinal(data);
		} catch (InvalidKeyException e) {
			throw new CipurseException(CipurseConstant.CTL_INVALID_KEY);
		} catch (IllegalBlockSizeException e) {
			throw new CipurseException(CipurseConstant.CTL_INVALID_DATA_LENGTH);
		} catch (BadPaddingException e) {
			throw new CipurseException(CipurseConstant.CTL_CRYPTO_ERROR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] symCryptoDecryption(ALGO_TYPE type, byte[] key,byte[] iv, byte[] data) throws CipurseException {

		try {
			aesKey = new SecretKeySpec(key, "AES");
			aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
			return aesCipher.doFinal(data);
		} catch (InvalidKeyException e) {
			throw new CipurseException(CipurseConstant.CTL_INVALID_KEY);
		} catch (IllegalBlockSizeException e) {
			throw new CipurseException(CipurseConstant.CTL_INVALID_DATA_LENGTH);
		} catch (BadPaddingException e) {
			throw new CipurseException(CipurseConstant.CTL_CRYPTO_ERROR);
		}
	}

	@Override
	public byte[] getRandomNumber(int randomSize) {
		byte[] random = new byte[randomSize];
		secRandom.nextBytes(random);
		return random;
	}

	@Override
	public byte[] getKeyValue(int keyID) throws CipurseException {
		if(keyID<=0 || keyID>keySet.length)
			throw new CipurseException(CipurseConstant.CTL_KEY_NOT_FOUND);
		byte[] key = keySet[keyID-1];
		if(key==null)
			throw new CipurseException(CipurseConstant.CTL_INVALID_KEY);
		return key;
	}

	@Override
	public void setKeyValue(int keyID, byte[] keyValue) throws CipurseException {
		if(keyID<=0 || keyID>keySet.length)
			throw new CipurseException(CipurseConstant.CTL_KEY_NOT_FOUND);
		keySet[keyID-1]=new byte[keyValue.length];
		System.arraycopy(keyValue, 0, keySet[keyID-1], 0, keyValue.length);
	}

	@Override
	public byte[] getKVV(byte[] forKey) throws CipurseException {
		 if((forKey.length % CipurseConstant.AES_BLOCK_LENGTH) != 0) {
	            throw new CipurseException(CipurseConstant.CTL_INVALID_DATA_LENGTH);
	        }

	        byte[] cipherText = symCryptoEncryption(ALGO_TYPE.AES_128, forKey,null, nullVector);
	        byte[] kvv = new byte[CipurseConstant.CIPURSE_KVV_LENGTH];

	        //high order bytes
	        System.arraycopy(cipherText, 0, kvv, 0, CipurseConstant.CIPURSE_KVV_LENGTH);

	        return kvv;
	}

}
