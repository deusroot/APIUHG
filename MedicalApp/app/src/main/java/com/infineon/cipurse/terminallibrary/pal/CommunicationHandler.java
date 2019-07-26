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

import android.nfc.tech.IsoDep;

import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;

import java.io.IOException;

/**
 * Implementation class for communication Handler.
 *
 * @since 1.0.0
 * @version 1.0.1
 */
public class CommunicationHandler implements ICommunicationHandler {
	private IsoDep nfc;

	public CommunicationHandler(IsoDep nfc) throws CipurseException {
		this.nfc = nfc;
		open();
	}

    public void close() throws CipurseException {
        disconnect();
	}

	private void disconnect() {
        try {
            if (nfc != null)
				nfc.close();
        }
        catch (IOException e) {
            // ignore any exception;
        }
		nfc = null;
	}

	public boolean isOpen() {
		if (nfc.isConnected()) {
			//state = SESSION_STARTED_STATE;
			return true;
		} else {
			//state = SESSION_STOPPED_STATE;
			return false;
		}
	}

	public void open() throws CipurseException {
		if (isOpen()) { return; }
		try {
			nfc.connect();
			if (!nfc.isConnected()) {
				throw new CipurseException(CipurseConstant.CTL_COMMS_TXRX_ERROR);
			}
			//state = SESSION_STARTED_STATE;
		} catch (IOException e) {
			throw new CipurseException(CipurseConstant.CTL_COMMS_TXRX_ERROR);
		}
	}

	public byte[] reset(int type) throws CipurseException {

		disconnect();

		// reconnect again
		open();
		if(nfc.isConnected()){
			// for now there is no way to get the ATR in android nfc api
			//return mCard.getATR().getBytes();
			return new byte[0];
		}
		return null;
	}

	@Override
	public byte[] transReceive(byte[] command) throws CipurseException {
		try {
			if(nfc.isConnected()){
				byte[] data = nfc.transceive(command);
				 if(data != null){
					 return data;
				 }
			}
		} catch (IOException e) {
			throw new CipurseException(CipurseConstant.CTL_COMMS_TXRX_ERROR);
		}
		return null;
	}
}
