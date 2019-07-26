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

import java.util.Arrays;

import com.infineon.cipurse.terminallibrary.framework.Utils;


/**
 * Model class for Key Diversification Info details.
 * 
 * @since 1.0.0
 * @version 1.0.1
*/
public class KeyDiversificationInfo {

	protected byte keyDiversificationMode;
	protected byte[] plainKeyOrDivData;
	protected int keyIDorReference;

	/**
	 * Constructs this Object with given parameters
	 * @param divMode		Key Diversification Mode
	 * @param masterKeyId	Master Key ID
	 * @param divData		Diversification Data
	 */
	public KeyDiversificationInfo(byte divMode, short masterKeyId, byte[] divData) {
		this.keyDiversificationMode = divMode;
		this.keyIDorReference = masterKeyId;
		if(divData!=null)
			this.plainKeyOrDivData = Arrays.copyOf(divData, divData.length);

	}
	/**
	 * Default Constructor
	 */
	public KeyDiversificationInfo() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Gives attribute 'Key Diversification Mode'
	 * @return Key Diversification Mode
	 */
	public byte getKeyDiversificationMode() {
		return keyDiversificationMode;
	}
	/**
	 * Sets attribute 'Key Diversification Mode'
	 * @param keyDiversificationMode Key Diversification Mode
	 */
	public void setKeyDiversificationMode(byte keyDiversificationMode) {
		this.keyDiversificationMode = keyDiversificationMode;
	}
	/**
	 * Gives attribute 'Diversification Data'
	 * @return Diversification Data
	 */
	public byte[] getPlainKeyOrDivData() {
		return plainKeyOrDivData;
	}
	/**
	 * Sets attribute 'Diversification Data'
	 * @param plainKeyOrDivData Diversification Data
	 */
	public void setPlainKeyOrDivData(byte[] plainKeyOrDivData) {
		this.plainKeyOrDivData = plainKeyOrDivData;
	}
	/**
	 * Gives Master Key ID within Vault or SAM
	 * @return Master Key ID
	 */
	public int getKeyIDorReference() {
		return keyIDorReference;
	}
	/**
	 * Sets Master Key ID within Vault or SAM
	 * @param keyIDorReference  Master Key ID
	 */
	public void setKeyIDorReference(int keyIDorReference) {
		this.keyIDorReference = keyIDorReference;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeyDiversificationInfo [\n\t Key diversification mode=");
		builder.append(String.format("0x%02X", keyDiversificationMode));
		builder.append("\n\t Master keyID=");
		builder.append(String.format("0x%04X", keyIDorReference));
		if (plainKeyOrDivData != null) {
			builder.append("\n\t ");
			builder.append("Diversification data=");
			builder.append(Utils.toHexString(plainKeyOrDivData, 0, plainKeyOrDivData.length, " 0x"));
		}
		builder.append("]");
		return builder.toString();
	}

}
