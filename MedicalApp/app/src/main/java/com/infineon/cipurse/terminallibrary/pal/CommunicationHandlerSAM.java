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

import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.famoco.secommunication.SmartcardReader;

import java.io.IOException;

/**
 * Implementation class for communication Handler for Famoco SAM communication.
 *
 * @since 1.0.0
 * @version 1.0.1
 */
public class CommunicationHandlerSAM implements ICommunicationHandler {
	private boolean mIsOpened = false;
	private SmartcardReader mSmartcardReader;
	private final int mSerialBaudRate = 115200; // was 38400

	public CommunicationHandlerSAM() throws CipurseException {
		mSmartcardReader = SmartcardReader.getInstance();
        open();
	}

    public void close() throws CipurseException {
        disconnect();
	}

	private void disconnect() throws CipurseException {
		try {
			mSmartcardReader.powerOff();
			mSmartcardReader.closeReader();
			mIsOpened = false;
		}
		catch (Exception e) {
			throw new CipurseException(e.getMessage());
		}
	}

	public boolean isOpen() {
		return mIsOpened;
	}

	public void open() throws CipurseException {
		if (isOpen()) { return; }
		try {
			if (!mSmartcardReader.openReader(mSerialBaudRate))
				throw new CipurseException("openReader failed");
			try {
				mSmartcardReader.powerOn();
			}
			catch (Exception ex)
			{
                if(ex.getMessage().contains("Bad FiDi"))    //Ignore this error
                {
                    ex.printStackTrace();
                }
                else
                {
                    throw new CipurseException(ex.getMessage());
                }
			}
			mIsOpened = true;
		}
		catch (Exception e) {
			throw new CipurseException(e.getMessage());
		}
	}

	public byte[] reset(int type) throws CipurseException {
		byte[] data = null;
		try {
			mSmartcardReader.powerOff();
			data = mSmartcardReader.powerOn();
		}
		catch (Exception e) {
			throw new CipurseException(e.getMessage());
		}
		return data;
	}

	@Override
	public byte[] transReceive(byte[] command) throws CipurseException {
		try {
			byte[] data = mSmartcardReader.sendApdu(command);
			if(data != null){
				return data;
			}
		}
		catch (Exception e) {
			throw new CipurseException(e.getMessage());
		}
		return null;
	}
}
