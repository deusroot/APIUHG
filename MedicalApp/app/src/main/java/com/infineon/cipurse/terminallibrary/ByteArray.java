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


package com.infineon.cipurse.terminallibrary;

import java.util.Arrays;

import com.infineon.cipurse.terminallibrary.framework.Utils;

/**
* ByteArray encapsulates an array of bytes and provides methods to manipulate byte array.
* 
* @since 1.0.0
* @version 1.0.1
*/
public class ByteArray implements Cloneable
{
	/**
	 * Java byte array.
	 */
   byte[] b;

   /**
    * Constructs an empty ByteArray object.
    */
   public ByteArray()
   {
      super();
      this.b = new byte[0];
   }

   /**
    * Constructs a ByteArray object with n bytes.
    * @param length byte array length.
    */
   public ByteArray(int length)
   {
	  super();
	  this.b = new byte[length];
   }

   /**
    * Constructs a ByteArray object from byte array.
    * @param b byte array.
    */
   public ByteArray(byte[] b)
   {
	  super();
	  this.b = new byte[b.length];
	  System.arraycopy(b, 0, this.b, 0, b.length);
   }

   /**
    * Constructs a ByteArray object with byte array starting from beginIndex to endIndex.
    * @param inArray byte array.
    * @param beginIndex beginning index in inArray.
    * @param endIndex end index in inArray.
    */
   public ByteArray(byte[] inArray, int beginIndex, int endIndex)
   {
      this.b = new byte[endIndex - beginIndex];
      System.arraycopy(inArray, beginIndex, this.b, 0, endIndex - beginIndex);
   }

   /**
    * Constructs a ByteArray object from a string.
    * @param string Byte array in String.
    */
   public ByteArray(String string) {
		String substr[] = string.split(" ");
		this.b = new byte[substr.length];
		for (int i=0; i<substr.length; i++){
			int value = Integer.parseInt(substr[i],16);
			b[i]= (byte)((value > 0x7F) ? value-0x100 : value);
		}
   }

	/**
	 * Method to get bytes in the ByteArray.
	 * @return byte[] bytes of ByteArray.
	 */
   public byte[] getBytes()
   {
      return b;
   }


   @Override
	public ByteArray clone() throws CloneNotSupportedException
	{
		ByteArray cloned = (ByteArray) super.clone();
		cloned.b = new byte[this.b.length];
		System.arraycopy(this.b, 0, cloned.b, 0, this.b.length);
		return cloned;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(b);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ByteArray other = (ByteArray) obj;
		if (!Arrays.equals(b, other.b))
			return false;
		return true;
	}

	/**
	 * Method to return the number of bytes in the ByteArray.
	 * @return int size of ByteArray.
	 */
   public int size()
   {
      return b.length;
   }

	/**
	 * Method to set the contents of a ByteArray object.
	 * @param string String containing ByteArray Value e.g "90 00".
	 */
   public void set(String string)
   {
		String substr[] = string.split(" ");

		this.b = new byte[substr.length];

		for (int i=0; i<substr.length; i++){
			int value = Integer.parseInt(substr[i],16);
			b[i]= (byte)((value > 0x7F) ? value-0x100 : value);
		}
   }

	/**
	 * Method to return the byte with index i of the ByteArray ('ByteArray[i]'). If index is Less Than 0 or Greater Than or = the length of the ByteArray, this function returns -1.
	 * @param off offset.
	 * @return int value in that offset.
	 */
   public int getByte(int off) {
	   if (off <0 || off > b.length) return -1;
	   return ((int)b[off] & 0xFF);
   }

	/**
	 * Method to set the byte at 'index' with value. If the index is Less Than 0 or Greater Than or = the length of the ByteArray this function does nothing.
	 * @param value offset value.
	 * @param off offset.
	 * @return ByteArray Returns this object.
	 */
   public ByteArray setByte(int value,int off) {
	   b[off]= (byte)((value > 0x7F) ? value-0x100 : value);
	   return this;
   }

	/**
	 * Method to create a ByteArray object and set its contents to the bytes from beginIndex to endIndex-1 of this object
	 * @param start begin Index.
	 * @param end end Index.
	 * @return ByteArray subArray created from ByteArray.
	 */
   public ByteArray subArray(int start, int end) {

	   return new ByteArray(b, start, end);
   }

	/**
	 * Method to append the ByteArray in parameter arg0 to this object.
	 * @param arg0 ByteArray to append.
	 * @return ByteArray returns this object.
	 */
	public ByteArray append(ByteArray arg0) {
		byte[] ba = arg0.getBytes();
		byte[] temp = b;
		this.b = new byte[ba.length + temp.length];
		System.arraycopy(temp, 0, this.b, 0, temp.length);
	    System.arraycopy(ba, 0, this.b, temp.length, ba.length);
		return this;
	}

	/**
	 * Method to replace the ByteArray of this object from index with the parameter contents of ByteArray arg0.
	 * @param arg0 ByteArray to replace.
	 * @param index index in ByteArray.
	 * @return ByteArray replaced ByteArray.
	 */
	public ByteArray replace(ByteArray arg0,int index) {
		byte[] ba = arg0.getBytes();
		if((index + ba.length) > (b.length))
		{
			byte[] temp = b;
			this.b = new byte[index + ba.length];
			System.arraycopy(temp, 0, this.b, 0, temp.length);
		}
		System.arraycopy(ba,0,this.b ,index,ba.length);
	    return this;
	}

	/**
	 * Method to return a string containing the bytes in the ByteArray as ASCII - Characters
	 * @return String ASCII character string of ByteArray.
	 */
	public String toAscii() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i< b.length; i++) {
			sb.append((char)b[i]);
		}
		return (sb.toString());
	}

	/**
	 * Method to set the ByteArray with the ASCII - Characters of s.
	 * @param s String containing ASCII characters.
	 * @return ByteArray ByteArray object constructed from ASCII string.
	 */
	public ByteArray fromAscii(String s) {
		byte[] ba = s.getBytes();
	    return new ByteArray(ba);
	}

	/**
	 * Method to return a String with the hex representation of the ByteArray. E.g. "05 3f 5a".
	 * @return String hex format of ByteArray.
	 */
	public String toString() {
		if(b != null) {
			return Utils.toHexString(b, 0, b.length, " 0x");	
		}
		return "";
	}
}