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
 * Model class for common File Attributes Info details.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public abstract class FileAttributes {

	/** DF identifier.*/
	protected int fileID;
	/**
	 * @note: If numOfKeys field is not set during creation of Secured DF, numOfKeys field gets populated
	 * from SecurityAttributes, even if numOfKeys is not provided in SecurityAttributes also, it
	 * calculates the numOfKeys from ART.
	 */
	protected byte numOfKeys = 0;

	protected byte[] smr;

	protected byte[] art;
	/** File descriptor byte.
	 If set to ADF_FILE_TYPE(0x38H), then ADF gets created.
	 If set to PXSE_FILE_TYPE(0x3FH), then PxSE ADF gets created.*/
	protected byte fileDescriptor;

	/**
	 * Gives attribute 'File Descriptor'
	 * @return File Descriptor
	 */
	public byte getFileDescriptor() {
		return (byte)fileDescriptor;
	}

	/**
	 * Sets attribute 'File Descriptor'
	 * @param fileDescriptor File Descriptor
	 */
	public void setFileDescriptor(byte fileDescriptor) {
		this.fileDescriptor = fileDescriptor;
	}

	/**
	 * Gives attribute 'File ID'.
	 * @return File ID
	 */
	public int getFileID() {
		return fileID;
	}

	/**
	 * Sets attribute 'File ID'.
	 * @param fileID File ID
	 */
	public void setFileID(int fileID) {
		this.fileID = fileID;
	}

	/**
	 * Gives attribute 'Number of Keys'.
	 * @return Number of Keys
	 */
	public byte getNumOfKeys() {
		return numOfKeys;
	}

	/**
	 * Sets attribute 'Number of Keys'.
	 * @param numOfKeys Number of Keys
	 */
	public void setNumOfKeys(byte numOfKeys) {
		this.numOfKeys = numOfKeys;
	}

	/**
	 * Gives attribute 'SMR'
	 * @return SMR
	 */
	public byte[] getSmr() {
		return smr;
	}

	/**
	 * Sets attribute 'SMR'
	 * @param smr SMR
	 */
	public void setSmr(byte[] smr) {
		this.smr = smr;
	}

	/**
	 * Gives attribute 'ART'.
	 * @return ART
	 */
	public byte[] getArt() {
		return art;
	}

	/**
	 * Sets attribute 'ART'
	 * @param art ART
	 */
	public void setArt(byte[] art) {
		this.art = art;
	}


	/**
	 * Gives File attributes in a byte array format.
	 * @return File attributes in byte array format.
	 */
	public abstract byte[] getFileAttributes();


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileAttributes [\n\t FileID=");
		builder.append(String.format("0x%04X", fileID));
		builder.append("\n\t Number of keys=");
		builder.append(String.format("0x%02X", numOfKeys));
		builder.append("\n\t File descriptor=");
		builder.append(String.format("0x%02X", fileDescriptor));
		if (smr != null) {
			builder.append("\n\t ");
			builder.append("SMR=");
			builder.append(Utils.toHexString(smr, 0, smr.length, " 0x"));
		}
		if (art != null) {
			builder.append("\n\t ");
			builder.append("ART");
			builder.append(Utils.toHexString(art, 0, art.length, " 0x"));
		}
		builder.append("]");
		return builder.toString();
	}
}
