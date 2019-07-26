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

import com.infineon.cipurse.terminallibrary.framework.Utils;

/**
 * Implementation for Logging Handler.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public class Logger implements ILogger{

	private void logMsg(int type, String target, String message) {

		switch(type) {
		case ILogger.COMMAND_MESSAGE:
			System.out.println(target+"Command APDU: " + message);
			break;
		case ILogger.RESPONSE_MESSAGE:
			System.out.println(target+"Response APDU: " + message);
			break;
		case ILogger.WRAPPED_COMMAND_MESSAGE:
			System.out.println(target+"Wrapped Command APDU: " + message);
			break;
		case ILogger.WRAPPED_RESPONSE_MESSAGE:
			System.out.println(target+"Wrapped Response APDU: "  + message);
			break;
		case ILogger.ERROR_MESSAGE:
			System.err.println(target+"Error Message: "  + message);
			break;
		case ILogger.WARNING_MESSAGE:
			System.out.println(target+"Warning Message: "  + message);
			break;
		default:
			if(target != null && !target.trim().isEmpty()) {
				System.out.println(target+":"+message);
			} else {
				System.out.println(message);
			}

			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void log(Throwable object) {
		object.printStackTrace();
	}

	/**
	 * {@inheritDoc}
	 */
	public void log(String message) {
		log(ILogger.INFO_MESSAGE, message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void log(int type, String message) {
		logMsg(type, "", message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void log(int type, byte[] data) {
		if(data != null) {
			log(type, Utils.toHexString(data, 0, data.length, " 0x"));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void log(int type, String message, byte[] data) {
		if(data != null) {
			log(type, message + " : " + Utils.toHexString(data, 0, data.length, " 0x"));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void log(int type, String target, String message, byte[] data) {
		if(data != null) {
			if(message != null && !message.trim().isEmpty()) {
				logMsg(type, target, message + " : " + Utils.toHexString(data, 0, data.length, " 0x"));
			} else {
				logMsg(type, target, Utils.toHexString(data, 0, data.length, " 0x"));
			}
		}
	}
}
