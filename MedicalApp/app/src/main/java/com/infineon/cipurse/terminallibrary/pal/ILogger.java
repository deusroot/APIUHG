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
package com.infineon.cipurse.terminallibrary.pal;

/**
 * Interface for Logging Handler in PAL.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public interface ILogger {
	/**Logging in case of Error message*/
	public static int ERROR_MESSAGE = 1;
	/**Logging in case of Info message*/
	public static int INFO_MESSAGE = 2;
	/**Logging in case of Warning message*/
	public static int WARNING_MESSAGE = 3;
	/**Logging in case of Command message*/
	public static int COMMAND_MESSAGE = 4;
	/**Logging in case of Response message*/
	public static int RESPONSE_MESSAGE = 5;
	/**Logging in case of Wrapped command message*/
	public static int WRAPPED_COMMAND_MESSAGE = 6;
	/**Logging in case of wrapped response message*/
	public static int WRAPPED_RESPONSE_MESSAGE = 7;

	/**
	 * Method to log with the associated throwable information.
	 * @param object throwable associated with log message.
	 */
	public void log(Throwable object);

	/**
	 * Method to log the given message. The message will be of type Info.
	 * @param message string message.
	 */
	public void log(String message);

	/**
	 * Method to log the given message with the message type. The message can be error/info/warning etc.
	 * @param type Type of the message.
	 * @param message string message.
	 */
	public void log(int type, String message);

	/**
	 * Method to log and given data with its type.
	 * @param type Type of the data.
	 * @param data Byte array of data.
	 */
	public void log(int type, byte[] data);
	/**
	 * Method to log the given message, data and type of the message.
	 * @param type Type of the message.
	 * @param message string message.
	 * @param data byte array of data.
	 */
	public void log(int type, String message, byte[] data);

	/**
	 * Method to log with the given target, message and byte array
	 * Final Message is printed in the format target:TYPE(String):message:data
	 * If message is null then it will be printed target:TYPE(String):data.
	 *
	 * @param type Type of the message
	 * @param target Target String, generally SAM or CBP
	 * @param message String message
	 * @param data byte array of data
	 */
	public void log(int type, String target, String message, byte[] data);

}
