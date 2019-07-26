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

/**
* Defines CIPURSE related Exception.
*
* @since 1.0.0
* @version 1.0.1
*/
public class CipurseException extends Exception{

	private static final long serialVersionUID = 0;
	private int reasonCode =0 ;

	/**
	 * Constructor
	 * @param exceptionMessage	Message
	 */
	public CipurseException(String exceptionMessage) {
		super(exceptionMessage);
	}

	/**
	 * Constructor
	 * @param excObject		Throwable Object
	 */
	public CipurseException(Throwable excObject) {
		super(excObject);
	}

	/**
	 * Constructor
	 * @param reasonCode Terminal Library related Error code
	 */
	public CipurseException(int reasonCode) {
		super("ERROR CODE: "+String.format("%04X", reasonCode));
		this.reasonCode = reasonCode;
	}

	/**
	 * Constructs
	 * @param exceptionMessage		Message
	 * @param excObject				throwable
	 */
	public CipurseException(String exceptionMessage, Throwable excObject){
		super(exceptionMessage, excObject);
	}

	/**
	 * Method to get the Terminal Library related Error Codes
	 * @return Terminal Library related error code
	 */
	public int getReasonCode(){
		return this.reasonCode;
	}

}
