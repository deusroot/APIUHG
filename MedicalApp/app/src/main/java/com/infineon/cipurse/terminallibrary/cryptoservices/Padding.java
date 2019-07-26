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
package com.infineon.cipurse.terminallibrary.cryptoservices;

import java.util.Arrays;

/**
* Defines Padding schemes used for CIPURSE(TM) Crypto.
*
* @since 1.0.0
* @version 1.0.1
*/
class Padding {

	/**
	 * Padding byte for M2
	 */
	public static final byte M2_PAD_BYTE	= (byte)0x80;


	/**
	 * This method implements the ISO9797 M2 Padding Scheme.
	 * @param inputData Input Data
	 * @param blockLen block size
	 * @return Padded Data
	 */
	public static byte[] schemeISO9797M2(final byte[] inputData, int blockLen) {
		int paddingLength	= 0;
		int inputDataLen	= inputData.length;
		byte[] paddedData	= null;

		//Padding Length
		paddingLength	= blockLen - (inputDataLen % blockLen);
		paddedData		= new byte[inputDataLen + paddingLength];
		Arrays.fill(paddedData, (byte)0x00);

		//Copy input data
		System.arraycopy(inputData, 0, paddedData, 0, inputDataLen);
		//Padiing byte
		paddedData[inputDataLen] = M2_PAD_BYTE;

		return paddedData;
	}


	/**
	 * This method removes the ISO9797 M2 Padding bytes.
	 * @param inputData Decrypted Data
	 * @return Actual Data
	 */
	public static byte[] removeISO9797M2 (final byte[] inputData)	{
		int actualDataLength = 0;
		byte[] actualData = null;
		//Get the offset from where the padding starts
		for (int i = inputData.length - 1; i >= 0; --i){
			if (inputData[i] == M2_PAD_BYTE) {
				actualDataLength = i;
				break;
			}
		}
		if(actualDataLength == 0) {
			return null;
		}else{
			//Allocate and copy the data excluding the padding bytes
			actualData = new byte[actualDataLength];
			System.arraycopy(inputData, 0, actualData, 0, actualDataLength);

			return actualData;
		}
	}


}

