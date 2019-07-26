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

/**
 * Model class for Encryption Key Info details.
 * 
 * @since 1.0.0
 * @version 1.0.1
*/
public class EncryptionKeyInfo {

	protected byte encKeyNumber;
	protected short encKeyID;

	/**
	 * Default Constructor
	 */
	public EncryptionKeyInfo() {

	}

	/**
	 * Constructs this object with given parameters.
	 * @param encKeyNo	Decryption Key number within CBP.
	 * @param encKeyID	Encryption Key ID within Vault or SAM.
	 */
	public EncryptionKeyInfo(byte encKeyNo, short encKeyID) {
		this.encKeyNumber=encKeyNo;
		this.encKeyID=encKeyID;
	}



	/**
	 * Gives encryption key number in card. (Key used to decrypt in Card)
	 * @return the encKeyNumber
	 */
	public byte getEncKeyNumber() {
		return encKeyNumber;
	}

	/**
	 * Sets encryption key number in card. (Key used to decrypt in Card)
	 * @param encKeyNumber the encKeyNumber to set
	 */
	public void setEncKeyNumber(byte encKeyNumber) {
		this.encKeyNumber = encKeyNumber;
	}

	/**
	 * Gives The Encryption Key ID from within Key Vault
	 * @return The Encryption Key ID from within Key Vault
	 */
	public short getEncKeyID() {
		return encKeyID;
	}

	/**
	 * Sets The Encryption Key ID from within Key Vault
	 * @param encKeyID The Encryption Key ID from within Key Vault
	 */
	public void setEncKeyID(short encKeyID) {
		this.encKeyID = encKeyID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EncryptionKeyInfo [\n\t Encryption key number=");
		builder.append(String.format("0x%02X", encKeyNumber));
		builder.append("\n\t Encryption keyID=");
		builder.append(String.format("0x%04X", encKeyID));
		builder.append("]");
		return builder.toString();
	}

}
