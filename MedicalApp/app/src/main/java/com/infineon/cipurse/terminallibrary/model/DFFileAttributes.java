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
 * Model class for DF File Attributes-Info details.
 *
 * @since 1.0.0
 * @version 1.0.1
 */
public class DFFileAttributes extends FileAttributes{

	/**File type indicator for Binary files*/
	public static final byte ADF_FILE_TYPE = 0x38;
	/**File type indicator for Binary files supporting transactions*/
	public static final byte PXSE_FILE_TYPE = 0x3F;
	/** Application Profile: L Profile.*/
	public static final byte PROFILE_L = 0x01;
	/** Application Profile: S Profile.*/
	public static final byte PROFILE_S = 0x02;
	/** Application Profile: T Profile.*/
	public static final byte PROFILE_T = 0x03;
	/** Application Profile: Unknown Profile.*/
	public static final byte PROFILE_UNKNOWN = 0x00;

	/** Maximum number of custom EFs under the ADF. The value FFH is RFU.*/
	protected short numOfEFs;
	/** Maximum number of EFs with SFID support under the ADF
	(up to 31, must be lower or equal to NbrEF).*/
	protected byte numOfSFIDs;
	/** Number of keys assigned to ADF. For secured ADF, Number of keys shall be
	in the range of 01H to 08H. For non-secured ADF, Number of keys shall be set to 00H.*/

	/** Type of Application Profile.
	 If set to PROFILE_L(0x01H), then L profile.
	 If set to PROFILE_S(0x02H), then S profile.
	 If set to PROFILE_T(0x03H), then T profile.
	 If set to PROFILE_UNKNOWN(0x00H), then unknown profile.*/
	protected byte appProfile;

	protected FCPInfo fcpInfo = new FCPInfo();

	
	private byte[] NRGMap;
	

	/**
	 * Gives Attribute 'Number of EFs'
	 * @return the Number of EFs
	 */
	public short getNumOfEFs() {
		return numOfEFs;
	}

	/**
	 * Sets Attribute 'Number of EFs'
	 * @param numOfEFs Number of EFs
	 */
	public void setNumOfEFs(short numOfEFs) {
		this.numOfEFs = numOfEFs;
	}

	/**
	 * Gives Attribute 'Number of SFIDs'
	 * @return Number of SFIDs
	 */
	public byte getNumOfSFIDs() {
		return numOfSFIDs;
	}

	/**
	 * Sets Attribute 'Number of SFIDs'
	 * @param numOfSFIDs Number of SFIDs
	 */
	public void setNumOfSFIDs(byte numOfSFIDs) {
		this.numOfSFIDs = numOfSFIDs;
	}


	/**
	 * Gives FCPInfo object associated with the Attributes
	 * @return Attribute 'FCP Information'
	 */
	public FCPInfo getFcpInfo() {
		return fcpInfo;
	}

	/**
	 * Sets Attribute 'FCP information'.
	 * @param fcpInfo FCP Information
	 */
	public void setFcpInfo(FCPInfo fcpInfo) {
		this.fcpInfo = fcpInfo;
	}

	/**
	 * Gives attribute 'Application Profile'
	 * @return Application Profile
	 */
	public byte getAppProfile() {
		return appProfile;
	}

	
	/**
	 * Gives attribute 'NRG sector Map'
	 * @return NRG sector Map
	 */
	public byte[] getNRGMap() {
		return NRGMap;
	}

	/**
	 * Sets attribute 'NRG sector Mapping'
	 * @param NRGMap NRG sector Map
	 */
	public void setNRGMap(byte[] NRGMap) {
		this.NRGMap = NRGMap;
	}
	

	/**
	 * Sets attribute 'Application Profile Identifier'
	 * @param appProfile Application Profile Identifier
	 */
	public void setAppProfile(byte appProfile) {
		this.appProfile = appProfile;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public byte[] getFileAttributes(){
		/*
		 * Ignore the ARTs & SMR if Number of Keys == 0
		 */
		byte[] objART = null;
		byte[] smrBytes = null;
		byte[] dfAttributes = null;

		if(getNumOfKeys() > 0)
		{
			objART = getArt();
			smrBytes = getSmr();
			dfAttributes = new byte[9+objART.length];
		}
		else
			dfAttributes = new byte[7];

		dfAttributes[0] = (byte)getFileDescriptor();
		dfAttributes[1] = (byte)getAppProfile();
		dfAttributes[2] = (byte)((getFileID() >> 0x08) & 0x00FF);
		dfAttributes[3] = (byte)(getFileID() & 0x00FF);
		dfAttributes[4] = (byte)getNumOfEFs();
		dfAttributes[5] = (byte)getNumOfSFIDs();
		dfAttributes[6] = (byte)getNumOfKeys();

		if(smrBytes !=null && smrBytes.length > 0 ){
			dfAttributes[7] = (byte)((smrBytes[0]) & 0x00FF);
			dfAttributes[8] = (byte)((smrBytes[1]) & 0x00FF);
		}

		if(objART != null && objART.length > 0 ){
			for(byte loop = 0; loop<objART.length; loop++)
			{
				dfAttributes[loop+9] = objART[loop];
			}
		}
		return dfAttributes;
	}

	
	/**
	 * Gives attribute 'Tag C8' in TLV format.
	 * @return Tag C8 as TLV.
	 */
	public byte[] getTagC8() {
		byte[] tagC8 = new byte[0];
		if(NRGMap != null && NRGMap.length == 0){
			tagC8 = new byte[2];
			tagC8[0] = (byte)0xC8;
			tagC8[1] = 0x00;
		} else if(NRGMap != null){
			tagC8 = new byte[NRGMap.length + 2];
			tagC8[0] = (byte)0xC8;
			tagC8[1] = (byte)NRGMap.length;
			System.arraycopy(NRGMap, 0, tagC8, 2, NRGMap.length);
		}
		return tagC8;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DFFileAttributes [");
		builder.append("\n\t File descriptor=");
		builder.append(String.format("0x%02X", fileDescriptor));
		builder.append("\n\t File type=");
		builder.append(String.format("0x%02X", appProfile));
		builder.append("\n\t FileID=");
		builder.append(String.format("0x%04X", fileID));
		builder.append("\n\t Number of EFs/ADFs=");
		builder.append(String.format("0x%02X", numOfEFs));
		builder.append("\n\t Number of SFID=");
		builder.append(String.format("0x%02X", numOfSFIDs));
		builder.append("\n\t Number of keys=");
		builder.append(String.format("0x%02X", numOfKeys));
		if (smr != null) {
			builder.append("\n\t ");
			builder.append("SMR=");
			builder.append(Utils.toHexString(smr, 0, smr.length, " 0x"));
		}
		if (art != null) {
			builder.append("\n\t ");
			builder.append("ART=");
			builder.append(Utils.toHexString(art, 0, art.length, " 0x"));
		}
		if (fcpInfo != null) {
			builder.append("\n\t ");
			builder.append("FCP information=");
			builder.append(fcpInfo);
		}
		
		if (NRGMap != null) {
			builder.append("\n\t ");
			builder.append("NRG sector mapping=");
			builder.append(Utils.toHexString(NRGMap, 0, NRGMap.length, " 0x"));
		}
		
		builder.append("]");
		return builder.toString();
	}


}

