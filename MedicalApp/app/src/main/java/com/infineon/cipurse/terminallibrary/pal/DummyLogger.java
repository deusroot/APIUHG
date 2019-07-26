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
 * Class defines a Dummy Logger implementation which can be used to disable all logging events.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
public class DummyLogger implements ILogger {

	@Override
	public void log(Throwable object) {

	}

	@Override
	public void log(String message) {

	}

	@Override
	public void log(int type, String message) {

	}

	@Override
	public void log(int type, byte[] data) {

	}

	@Override
	public void log(int type, String message, byte[] data) {

	}

	@Override
	public void log(int type, String target, String message, byte[] data) {

	}

}