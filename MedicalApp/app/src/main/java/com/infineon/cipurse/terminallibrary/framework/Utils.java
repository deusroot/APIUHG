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
package com.infineon.cipurse.terminallibrary.framework;

import java.util.Arrays;

import com.infineon.cipurse.terminallibrary.CipurseException;


/**
 * Defines general Utility methods used, for example, in data manipulation, conversions and so on.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public class Utils
{
    /**
     * Private default constructor - prevents instantiation.
     */
    private Utils()
    {
        // do nothing
    }

     /**
     * Extracts data bytes from a source array at specified offset and length
     * @param data	source array
     * @param offset	offset at which the data to be extracted
     * @param length	length of data to be extracted
     * @return	extracted data bytes
     */

    public static byte[] extractBytes(byte[] data, int offset, int length)
	{
		byte[] extracted=new byte[length];
		System.arraycopy(data, offset, extracted, 0, length);
		return extracted;
	}

    /**
     * Utility method to convert a variety of objects into a byte array. The supported
     * reference types are:
     * <ul><li>null is converted into an empty byte array</li>
     * <li>byte[] is returned unaltered</li>
     * <li>Integer is returned as byte array of 4 bytes with MSB first</li>
     * <li>Short is returned as byte array of 2 bytes with MSB first</li>
     * <li>Byte is returned as byte array of 1 byte</li>
     * <li>hex string is converted into its byte array representation</li>
     * <li>for any other object the <code>toString()</code> method is called and
     * the result is treated as hex string</li></ul>
     * Hex strings are accepted in various input formats e.g. "ABCDEF", "AB cd EF",
     * "0xab:0xc:0xde", "ab, C, DE". ASCII strings may be included if surrounded by
     * hyphens, e.g 'My String'.
     * @param stream object to be converted into a byte array.
     * @return byte array representation of stream object.
     * @throws CipurseException if conversion into a byte array fails.
     */
    public static byte[] toBytes(Object stream) throws CipurseException
    {
        // check if null reference
        if (stream == null)
            return new byte[0];

        // check if already byte array
        if (stream instanceof byte[])
            return (byte[]) stream;

        // check if integer value
        if (stream instanceof Integer)
            return toBytes(((Integer)stream).intValue(), 4);

        if (stream instanceof Double)
            return toBytes(((Double)stream).intValue(), 4);

        // check if short value
        if (stream instanceof Short)
            return toBytes(((Short)stream).intValue(), 2);

        // check if byte value
        if (stream instanceof Byte)
            return toBytes(((Byte)stream).intValue(), 1);

        // check if String
        if (stream instanceof String)
            return toByteArray((String) stream);

        // try to convert object into string and from there to byte array
        return toByteArray(stream.toString());
    }

    /**
     * Helper function to convert a primitive value into a byte array.
     * @param value integer value to be converted.
     * @param length length of resulting array.
     * @return byte array representation of integer value (MSB first).
     */
    public static byte[] toBytes(int value, int length)
    {
        byte[] abValue = new byte[length];

        while (length > 0)
        {
            abValue[--length] = (byte) value;
            value >>= 8;
        }

        return abValue;
    }

    /**
     * Converts a hex string into a byte array. The hex string may have various formats
     * e.g. "ABCDEF", "AB cd EF", "0xab:0xc:0xde", "ab, C, DE". ASCII strings may be included
     * if surrounded by hyphens, e.g 'My String'.
     * @param data hex string to be converted
     * @return byte array with converted hex string
     * @throws CipurseException if conversion fails for syntactical reasons.
     */
    private static byte[] toByteArray(String data) throws CipurseException
    {
        int i, iOffset, iLength = data.length();
        byte[] abyValue = new byte[iLength];
        boolean bOddNibbleCountAllowed = false;

        for (i = 0, iOffset = 0; i < iLength; i++)
        {
            char c = data.charAt(i);
            int iValue = -1;

            if ((c >= '0') && (c <= '9'))
            {
                iValue = c - '0';
            }
            else if ((c >= 'A') && (c <= 'F'))
            {
                iValue = c - 'A' + 10;
            }
            else if ((c >= 'a') && (c <= 'f'))
            {
                iValue = c - 'a' + 10;
            }
            else if (((c == 'x') || (c == 'X')) && ((iOffset & 1) == 1))
            {
                if (abyValue[iOffset >> 1] == 0)
                {
                    bOddNibbleCountAllowed = true;

                    // ignore 0x..
                    iOffset--;
                }
                else
                {
                    // x but not 0x found
                    throw new CipurseException("Illegal character in hex string");
                }
            }
            else if (c >= 'A')
            {
            	// character cannot be delimiter
                throw new CipurseException("Illegal character in hex string");
            }
            else if (c == '\'')
            {
                // read ASCII values
                for (i++; i < iLength; i++)
                {
                    c = data.charAt(i);
                    if (c == '\'')
                        break;

                    abyValue[iOffset >> 1] = (byte) c;
                    iOffset += 2;
                }

                if (((iOffset & 1) != 0) || (c != '\''))
                {
                    // character cannot be start of ASCII string
                    throw new CipurseException("Illegal character in hex string");
                }
            }
            else if ((iOffset & 1) == 1)
            {
                if (!bOddNibbleCountAllowed && isDedicatedDelimiter(c))
                    bOddNibbleCountAllowed = true;

                if (bOddNibbleCountAllowed)
                {
                    // delimiter found, so just one nibble specified (e.g. 0xA:0xB...)
                    iOffset++;
                }
            }

            if (iValue >= 0)
            {
                abyValue[iOffset >> 1] = (byte)((abyValue[iOffset >> 1] << 4) | iValue);
                iOffset++;
            }
        }

        if (!bOddNibbleCountAllowed && ((iOffset & 1) != 0))
        {
            throw new CipurseException("Hex string has odd nibble count");
        }

        // calculate length of stream
        iLength = (iOffset + 1) >> 1;

        return Arrays.copyOf(abyValue, iLength);
    }

    /**
     * Check if character is a dedicated delimiter character
     * @param c character to be checked.
     * @return true if dedicated delimiter character
     */
    static private boolean isDedicatedDelimiter(char c)
    {
        switch (c)
        {
            case ',':
            case '.':
            case ':':
            case ';':
                return true;
        }

        return (c <= ' ');
    }

    /**
     * Concatenate the content of two arrays and return resulting array.
     * @param array1 first array
     * @param array2 second array
     * @return resulting array which is the concatenation of array1 and array2.
     */
    public static byte[] concat(byte[] array1, byte[] array2)
    {
        byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);

        return result;
    }

    /**
	 * Copies source array to destination array from specified offsets and length.
	 * @param src			Source array
	 * @param srcOffset		Source offset
	 * @param dest			destination array
	 * @param destOffset	destination offset
	 * @param length		length of bytes to copy
	 * @return	The next offset within destination array after copying
	 */
    public static int arrayCopy(byte[] src, int srcOffset, byte[] dest, int destOffset, int length)
	{
		System.arraycopy(src, srcOffset, dest, destOffset, length);
		return destOffset + length;
	}

    /** Array containing hex digits for toString conversions of byte arrays */
    private static final char[] HEX_DIGIT =
    {
         '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Convert a byte array into a hex string. The formatting of the string may be influenced
     * by a delimiter string which is inserted before each byte. For the first byte, all
     * delimiter characters before ',', '.', ':', ';' or ' ' are skipped.
     * @param value byte array to be converted.
     * @param offset offset of data within byte array
     * @param length of data to be converted
     * @param delimiter delimiter string like ", " or " ", ", 0x", ":", ", (byte)0x"
     * @return resulting hex string
     */
    public static String toHexString (byte[] value, int offset, int length, String delimiter)
    {
        int i;

        // create string buffer of required size
        StringBuffer strValue = new StringBuffer(length * (2 + delimiter.length()));

        // go backwards until dedicated delimiter is found
        for (i = delimiter.length(); i > 0; i--)
        {
            if (isDedicatedDelimiter(delimiter.charAt(i-1)))
                break;
        }

        if (i < delimiter.length())
        {
            // append first delimiter string portion
            strValue.append(delimiter.substring(i));
        }

        // transform byte by byte
        for (i = 0; i < length; i++, offset++)
        {
            // append hex value
            strValue.append(HEX_DIGIT[(value[offset] >> 4) & 0xF]).append(HEX_DIGIT[value[offset] & 0xF]);

            // append delimiter if not last value
            if (i < (length - 1))
                strValue.append(delimiter);
        }

        // return resulting string
        return strValue.toString();
    }


    /**
     * Perform bytewise XOR on two arrays. Array a defines the resulting length.
     * Array b may be longer but not shorter.
     * @param a Input array 1.
     * @param b Input array 2.
     * @return Result c = a ^ b.
     */
    public static byte[] xor(byte[] a, byte[] b) {

        int iLength = a.length;
        byte[] c = new byte[iLength];
        for (int i = 0; i < iLength; i++)
            c[i] = (byte) (a[i] ^ b[i]);

        return c;
    }

    /**
     * Helper function to extract a two byte value from a byte array.
     * @param array array with data
     * @param offset offset of two byte value in data array.
     * @return two byte value from byte array.
     */
    public static int getShort(byte[] array, int offset)
    {
        return ((array[offset] & 0xFF) << 8) | (array[offset + 1] & 0xFF);
    }


    /**
     * Enumerates APDU cases
     */
    public enum eCaseType {
		/**
		 * Case 1
		 */
		CASE_1,
		/**
		 *  case 2
		 */
		CASE_2,
		/**
		 *  Case 3
		 */
		CASE_3,
		/**
		 *  Case 4
		 */
		CASE_4,
		/**
		 *  Invalid case
		 */
		INVALID;
	}

	/**
	 * Gives APDU case associated with given APDu command
	 * @param command		APDu command
	 * @return	APDU Case type enumeration
	 */
	public static eCaseType getCaseType(byte[] command) {
		if (command.length == 4) {
			return eCaseType.CASE_1;
		} else if (command.length == 5) {
			return eCaseType.CASE_2;
		} else if (command[4] == (byte)(command.length - 5)) {
			return eCaseType.CASE_3;
		} else if (command[4] == (byte)(command.length - 6)) {
			return eCaseType.CASE_4;
		} else {
			return eCaseType.INVALID;
		}
	}


	/**
	 * Gives SMI byte updated with LE presence indicator.
	 * @param smi SMI byte to be updated
	 * @param setLePresent	true to indicate Le presence in SMI
	 * @return	Updated SMI
	 */
	public static byte updateSMIforLePresence(byte smi, boolean setLePresent) {

        if (setLePresent)
        	return (byte) (smi | 0x01);
        else
        	return (byte) (smi & 0xFE);
	}

}