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

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.framework.Utils;


/**
 * Model class for FCP Info details.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public class FCPInfo {

	protected byte[] aidValue;
	protected byte[] propSecAttrib;
	protected byte[] PXSEAidToRegister;

	/**
	 * Gives attribute 'AID'.
	 * @return AID
	 */
	public byte[] getAID() {
		return aidValue;
	}

	/**
	 * Sets attribute 'AID'.
	 * @param aid AID
	 */
	public void setAID(byte[] aid) {
		this.aidValue = aid;
	}

	/**
	 * Gives attribute 'Proprietary Security Attributes'.
	 * @return the Proprietary Security Attributes
	 */
	public byte[] getPropSecurityAttributes() {
		return propSecAttrib;
	}

	/**
	 * Sets attribute 'Proprietary Security Attributes'.
	 * @param propSecAttrib Proprietary Security Attributes
	 */
	public void setPropSecurityAttributes(byte[] propSecAttrib) {
		this.propSecAttrib = propSecAttrib;
	}

	/**
	 * Gives attribute 'PxSD AID' (AID of the PxSD to which this DF registers to).
	 * @return PxSD AID
	 */
	public byte[] getPXSEAID() {
		return PXSEAidToRegister;
	}

	/**
	 * Sets attribute 'PxSD AID' (AID of the PxSD to which this DF registers to).
	 * @param pXSEAidToRegister PxSD AID
	 */
	public void setPXSEAID(byte[] pXSEAidToRegister) {
		PXSEAidToRegister = pXSEAidToRegister;
	}


	/**
	 * Default Constructor
	 */
	public FCPInfo() {

	}

	/**
	 * Gives FCP attributes in the form of a TLV
	 * @return	FCP TLV
	 */
	public byte[] getFCPBytes()
	{
		ByteArray fcpBytes = new ByteArray(new byte[]{0x62, 0});
		fcpBytes.append(new ByteArray(getTag84()))
			.append(new ByteArray(getTag86())
				.append(new ByteArray(getTagD4())));
		fcpBytes.setByte(fcpBytes.size()-2, 1);
		return fcpBytes.getBytes();
	}

	/**
	 * Method to get the Security attributes value in the DF attributes information.
	 *
	 * @return byte[] 1-64 bytes Security attributes in ByteArray format.
	 */
	protected byte[] getTag86(){
		byte[] tag86 = new byte[0];
		if(propSecAttrib != null && propSecAttrib.length == 0){
			tag86 = new byte[2];
			tag86[0] = (byte)0x86;
			tag86[1] = 0x00;
		} else if(propSecAttrib != null){
			tag86 = new byte[propSecAttrib.length + 2];
			tag86[0] = (byte)0x86;
			tag86[1] = (byte)propSecAttrib.length;
			System.arraycopy(propSecAttrib, 0, tag86, 2, propSecAttrib.length);
		}
		return tag86;
	}

	/**
	 * Method to get the AID of PxSE for registration in the DF attributes information.
	 *
	 * @return byte[] 1-16 bytes PxSE AID value in ByteArray format.
	 */
	protected byte[] getTagD4(){
		byte[] tagD4 = new byte[0];
		if(PXSEAidToRegister !=null && PXSEAidToRegister.length == 0)
		{
			tagD4 = new byte[2];
			tagD4[0] = (byte)0xD4;
			tagD4[1] = 0x00;
		} else if(PXSEAidToRegister != null){
			tagD4 = new byte[PXSEAidToRegister.length + 2];
			tagD4[0] = (byte)0xD4;
			tagD4[1] = (byte)PXSEAidToRegister.length;
			System.arraycopy(PXSEAidToRegister, 0, tagD4, 2, PXSEAidToRegister.length);
		}

		return tagD4;
	}
	/**
	 * Method to get the AID value in the DF attributes information.
	 *
	 * @return byte[] 1-16 bytes AID of ADF in ByteArray format.
	 */
	protected byte[] getTag84(){
		byte[] tag84;
		if(aidValue != null){
			tag84 = new byte[aidValue.length + 2];
			tag84[1] = (byte)aidValue.length;
			System.arraycopy(aidValue, 0, tag84, 2, aidValue.length);
		}
		else
		{
			tag84 = new byte[2];
			tag84[1] = 0x00;
		}
		tag84[0] = (byte)0x84;
		return tag84;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FCPInfo [");
		if (aidValue != null) {
			builder.append("\n\t ");
			builder.append("AID=");
			builder.append(Utils.toHexString(aidValue, 0, aidValue.length, " 0x"));
		}
		if (propSecAttrib != null) {
			builder.append("\n\t ");
			builder.append("Proprietary security attributes=");
			builder.append(Utils.toHexString(propSecAttrib, 0, propSecAttrib.length, " 0x"));
		}
		if (PXSEAidToRegister != null) {
			builder.append("\n\t ");
			builder.append("AID of PxSE ADF=");
			builder.append(Utils.toHexString(PXSEAidToRegister, 0, PXSEAidToRegister.length, " 0x"));
		}
		builder.append("]");
		return builder.toString();
	}
}
