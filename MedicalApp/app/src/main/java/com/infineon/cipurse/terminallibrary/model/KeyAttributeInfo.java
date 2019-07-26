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
package com.infineon.cipurse.terminallibrary.model;

import com.infineon.cipurse.terminallibrary.framework.Utils;


/**
 * Model class for Key Attribute Info details.
 * 
 * @since 1.0.0
 * @version 1.0.1
*/
public class KeyAttributeInfo {
	/** Key set size. */
	public static final byte KEY_SET_SIZE = (byte)3;
	/** Key set size. */
	public static final byte KEY_OBJECT_SIZE = (byte)20;
	/** Key security attribute.*/
	protected byte keySecAttrib;
	/** Additional key information. (May include (e.g.) the key version).*/
	protected byte keyAddInfo;
	/** Key length in bytes.*/
	protected byte keyLength;
	/** Cryptographic algorithm identifier.*/
	protected byte keyAlgoId;
	/** KVV value.*/
	protected byte[] kvv;


	/**
	 * Constructs this object with given parameters
	 * @param addInfo Key Additional Info
	 * @param secAttrib	Key Security Attribute
	 * @param keyLen	Key Length
	 * @param algoId	Key Algo ID
	 */
	public KeyAttributeInfo(byte addInfo, byte secAttrib, byte keyLen, byte algoId) {
		this.keyAddInfo = addInfo;
		this.keySecAttrib=secAttrib;
		this.keyLength = keyLen;
		this.keyAlgoId = algoId;
	}



	/**
	 * Default Constructor
	 */
	public KeyAttributeInfo() {

	}



	/**
	 * Gives attribute 'Key Security Attribute'
	 * @return Key Security Attribute
	 */
	public byte getKeySecAttrib() {
		return (byte) keySecAttrib;
	}



	/**
	 * Sets attribute 'Key Security Attribute'
	 * @param keySecAttrib Key Security Attribute
	 */
	public void setKeySecAttrib(byte keySecAttrib) {
		this.keySecAttrib = keySecAttrib;
	}



	/**
	 * Gives attribute 'Key Additional Info'
	 * @return Key Additional Info
	 */
	public short getKeyAddInfo() {
		return keyAddInfo;
	}



	/**
	 * Sets attribute 'Key Additional Info'
	 * @param keyAddInfo Key Additional Info
	 */
	public void setKeyAddInfo(byte keyAddInfo) {
		this.keyAddInfo = keyAddInfo;
	}



	/**
	 * Gives attribute 'Key Length'
	 * @return Key Length
	 */
	public short getKeyLength() {
		return keyLength;
	}



	/**
	 * Sets attribute 'Key Length'
	 * @param keyLength Key Length
	 */
	public void setKeyLength(byte keyLength) {
		this.keyLength = keyLength;
	}



	/**
	 * Gives attribute 'Key Algo. ID'
	 * @return Key Algo Id
	 */
	public short getKeyAlgoId() {
		return keyAlgoId;
	}



	/**
	 * Sets attribute 'Key Algo. ID'
	 * @param keyAlgoId Key Algo. ID
	 */
	public void setKeyAlgoId(byte keyAlgoId) {
		this.keyAlgoId = keyAlgoId;
	}



	/**
	 * Gives attribute 'KVV'
	 * @return KVV
	 */
	public byte[] getKvv() {
		return kvv;
	}



	/**
	 * Sets attribute 'KVV'. If set to null, then APIs like CreateADF ignore it.
	 * @param kvv KVV or null
	 */
	public void setKvv(byte[] kvv) {
		this.kvv = kvv;
	}

	/**
	 * Gives the key set information(key security attributes[1 byte]+ key length[1 byte]+ key algorithm ID[1 byte]) in byte[] format.
	 *
	 * @return Key Set Information, for example used in Create ADF Command
	  */
	public byte[] getKeySetInBytes()
	{
		byte[] keySet = new byte[KEY_SET_SIZE];
		//keySet
		keySet[0] = (byte)keySecAttrib;
		keySet[1] = (byte)keyLength;
		keySet[2] = (byte)keyAlgoId;
		return keySet;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeyAttributeInfo [");
		builder.append("\n\t Key additional information=");
		builder.append(String.format("0x%02X", keyAddInfo));
		builder.append("\n\t Key security attributes=");
		builder.append(String.format("0x%02X", keySecAttrib));
		builder.append("\n\t Key length=");
		builder.append(String.format("0x%02X", keyLength));
		builder.append("\n\t Key algorithm ID=");
		builder.append(String.format("0x%02X", keyAlgoId));
		if (kvv != null) {
			builder.append("\n\t ");
			builder.append("KVV=");
			builder.append(Utils.toHexString(kvv, 0, kvv.length, " 0x"));
		}
		builder.append("]");
		return builder.toString();
	}

}
