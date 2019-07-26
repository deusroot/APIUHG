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
 * Model class for Load Key Info details.
 * 
 * @since 1.0.0
 * @version 1.0.1
*/
public class LoadKeyInfo {

	protected byte keyFileFN;
	protected short loadKeyID;
	protected byte keyLenOrAlgoID;
	protected short encKeyID;

	/**
	 * Constructs this Object with given parameters
	 * @param keyFileFN		FN
	 * @param loadKeyID		Destination Key ID at which the Key to be loaded
	 * @param keyAlgoID		Key Algo ID
	 * @param encKeyID		Reference or ID of the key within card used for decryption of the Encrypted Key
	 */
	public LoadKeyInfo(byte keyFileFN, short loadKeyID, byte keyAlgoID, short encKeyID) {
		this.keyFileFN=keyFileFN;
		this.loadKeyID=loadKeyID;
		this.keyLenOrAlgoID=keyAlgoID;
		this.encKeyID=encKeyID;
	}

	/**
	 * Gives Key File FN under which the Key To be Loaded
	 * @return the Key File FN
	 */
	public byte getKeyFileFN() {
		return keyFileFN;
	}

	/**
	 * Sets Key File FN under which the Key To be Loaded
	 * @param keyFileFN Key File FN
	 */
	public void setKeyFileFN(byte keyFileFN) {
		this.keyFileFN = keyFileFN;
	}

	/**
	 * Gives Key ID at which Key to be Loaded
	 * @return Load Key ID
	 */
	public short getLoadKeyID() {
		return loadKeyID;
	}

	/**
	 * Sets Key ID at which Key to be Loaded
	 * @param loadKeyID Load Key ID
	 */
	public void setLoadKeyID(short loadKeyID) {
		this.loadKeyID = loadKeyID;
	}

	/**
	 * Gives attribute 'Key Algo. ID'
	 * @return Key Algo Id
	 */
	public byte getKeyAlgoID() {
		return keyLenOrAlgoID;
	}

	/**
	 * Sets attribute 'Key Algo. ID'
	 * @param keyLenOrAlgoID Key Algo. ID
	 */
	public void setKeyAlgoID(byte keyLenOrAlgoID) {
		this.keyLenOrAlgoID = keyLenOrAlgoID;
	}

	/**
	 * Gives Encryption Key ID (Reference of key to be used to Decrypt in card)
	 * @return Encryption Key ID
	 */
	public short getEncKeyID() {
		return encKeyID;
	}

	/**
	 * Sets Encryption Key ID (Reference of key to be used to Decrypt in card)
	 * @param encKeyID Encryption Key ID
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
		builder.append("LoadKeyInfo [\n\t KeyFile FN=");
		builder.append(String.format("0x%02X", keyFileFN));
		builder.append("\n\t KeyID=");
		builder.append(String.format("0x%04X", loadKeyID));
		builder.append("\n\t Key length=");
		builder.append(String.format("0x%02X", keyLenOrAlgoID));
		builder.append("\n\t Encryption KeyID=");
		builder.append(String.format("0x%04X", encKeyID));
		builder.append("]");
		return builder.toString();
	}


}
