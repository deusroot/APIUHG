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
 * Model class for EF File Attributes Info details.
 *
 * @since 1.0.0
 * @version 1.0.1
 */
public class EFFileAttributes extends FileAttributes {

	/**File type indicator for Binary files*/
	public static final byte BINARY_FILE_TYPE = 0x01;
	/**File type indicator for Binary files supporting transactions*/
	public static final byte BINARY_FILE_TRANS_TYPE = 0x11;
	/**File type indicator for Key files*/
	public static final byte KEY_FILE_TYPE = 0x21;
	/**File type indicator for Linear Record files*/
	public static final byte LINEAR_RECORD_TYPE = 0x02;
	/**File type indicator for Linear Record files supporting transactions*/
	public static final byte LINEAR_RECORD_TRANS_TYPE = 0x12;
	/**File type indicator for Cyclic Record files*/
	public static final byte CYCLIC_RECORD_TYPE = 0x06;
	/**File type indicator for Cyclic Record files supporting transactions*/
	public static final byte CYCLIC_RECORD_TRANS_TYPE = 0x16;
	/**File type indicator for Value Record files*/
	public static final byte VALUE_FILE_TYPE = 0x1E;
	/**File type indicator for Value Record files supporting transactions*/
	public static final byte VALUE_FILE_TRANS_TYPE = 0x1F;
	/**File type indicator for Dedicated Files (DF)*/
	public static final byte FILE_TYPE_DF = 0x38;

	/** Number of records (1 to 254).
	/**
	* @note:In case of version V1 this field shall be set to 0 for Binary file type.
	* In case of version V2 & V3 this field shall not be set for Binary file type.
	*/
	protected short numOfRecs;
	/** Record Size (1 to 228).
	/**
	* @note: In case of version V1 this field shall not be set for all file types
	* and the record size results from FileSize divided by numOfRecs.
	* In case of version V2 & V3 this field shall not be set for Binary file type.
	*/
	protected short RecSize;
	/** Size of file in bytes.
	/**
	* @note:In case of version V1 for record files FileSize specifies numOfRecs * record size.
	* In case of version V2 & V3 this field shall not be set for Record file type.
	*/
	protected int fileSize;
	/** Short EF id for an EF. (000)b || SFID. For EFs not supporting SFID the value must be set to 00H.*/
	protected short SFID;

	
	private byte[] NRGMap;
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public byte[] getFileAttributes()
	{
		byte[] fileDataAttrib=new byte[2];
		if (fileDescriptor == BINARY_FILE_TYPE ||
				fileDescriptor == BINARY_FILE_TRANS_TYPE ||
						fileDescriptor == KEY_FILE_TYPE)
		{
			fileDataAttrib[0] = (byte)((fileSize >> 0x08) & 0x00FF);
			fileDataAttrib[1] = (byte)(fileSize & 0x00FF);
		}
		else
		{
			fileDataAttrib[0] = (byte)numOfRecs;
			fileDataAttrib[1] = (byte)RecSize;
		}
		return fileDataAttrib;
	}


	/**
	 * Gives attribute 'Number of Records' in case of a Record Based EF.
	 * @return Number of Records
	 */
	public short getNumOfRecs() {
		return numOfRecs;
	}

	/**
	 * Sets attribute 'Number of Records' in case of a Record Based EF.
	 * @param numOfRecs Number of Records
	 */
	public void setNumOfRecs(short numOfRecs) {
		this.numOfRecs = numOfRecs;
	}

	/**
	 * Gives attribute 'Record Size' for a Record Based EF.
	 * @return Record Size
	 */
	public short getRecSize() {
		return RecSize;
	}

	/**
	 * Sets attribute 'Record Size' for a Record Based EF.
	 * @param recSize Record Size
	 */
	public void setRecSize(short recSize) {
		RecSize = recSize;
	}

	/**
	 * Gives attribute 'File Size' for a Binary type of EF.
	 * @return File Size
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * Sets attribute 'File Size' for a Binary type of EF.
	 * @param fileSize File Size
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * Gives attribute 'SFID'.
	 * @return SFID. If no SFID associated then it returns 0.
	 */
	public short getSFID() {
		return SFID;
	}

	/**
	 * Sets attribute 'SFID'.
	 * @param sFID SFID value. 0, if not applicable.
	 */
	public void setSFID(short sFID) {
		SFID = sFID;
	}

	
	/**
	 * Gives attribute 'NRG Map'.
	 * @return NRG Map. Null if not set or not applicable.
	 */
	public byte[] getNRGMap() {
		return NRGMap;
	}

	/**
	 * Sets attribute 'NRG Block Mapping'.
	 * @param NRGMap NRG Map. Null if not applicable.
	 */
	public void setNRGMap(byte[] NRGMap) {
		this.NRGMap = NRGMap;
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
		builder.append("EFFileAttributes [");
		builder.append("\n\t File descriptor=");
		builder.append(String.format("0x%02X", fileDescriptor));
		builder.append("\n\t SFID=");
		builder.append(String.format("0x%02X", SFID));
		builder.append("\n\t FileID=");
		builder.append(String.format("0x%04X", fileID));
		if (fileDescriptor == BINARY_FILE_TYPE || fileDescriptor == BINARY_FILE_TRANS_TYPE ||
						fileDescriptor == KEY_FILE_TYPE)
		{
			builder.append("\n\t File size=");
			builder.append(String.format("0x%04X", fileSize));
		} else {
			builder.append("\n\t Number of records=");
			builder.append(String.format("0x%02X", numOfRecs));
			builder.append("\n\t Record size=");
			builder.append(String.format("0x%02X", RecSize));
		}
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
		
		if (NRGMap != null) {
			builder.append("\n\t ");
			builder.append("NRG block mapping=");
			builder.append(Utils.toHexString(NRGMap, 0, NRGMap.length, " 0x"));
		}
		
		builder.append("]");
		return builder.toString();
	}


}
