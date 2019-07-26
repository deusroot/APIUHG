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

import java.util.List;

/*import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;*/

/**
 * Implementation class for communication Handler.
 *
 * @since 1.0.0
 * @version 1.0.1
*/
/*
public class CommunicationHandlerJava implements ICommunicationHandler {

	private CardTerminal mCardTerminal = null;
	private Card mCard = null;
	CardChannel mChannel = null;

	*//**
	 * Identifier for cold reset
	 *//*
	public static final int COLD_RESET = 0;
	*//**
	 * Identifier for warm reset
	 *//*
	public static final int WARM_RESET = 1;


	*//**
	 * Parameterized Constructor
	 * @param terminalName Reader name to be connected with.
	 * @throws CipurseException {@link CipurseException}
	 *//*
	public CommunicationHandlerJava(String terminalName) throws CipurseException {
		init(terminalName);
		open();
	}

	private void init(String terminalName) {
		try {
			// Display the list of terminals
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();
			//System.out.println("Terminals: " + terminals);

			// Use the first terminal
			for (CardTerminal cardTerminal : terminals) {
				if(cardTerminal.getName().equals(terminalName)){
					this.mCardTerminal = cardTerminal;
					break;
				}
			}
		} catch (CardException e) {

		}
	}

	*//**
	 * Method to close the channel.
	 * @throws CipurseException {@link CipurseException}
	 *//*
	public void close() throws CipurseException {
        disconnect(true);
	}

	private void disconnect(boolean reset) {
		// release channel object
        mChannel = null;

        try {
            if (mCard != null)
                mCard.disconnect(reset);
        }
        catch (CardException e) {
            // ignore any exception;
        }

        // release card object
        mCard = null;
	}


	*//**
	 * Method to check is the channel is Opened/Closed.
	 * @return boolean to indicate Opened or closed.
	 * @throws CipurseException {@link CipurseException}
	 *//*
	public boolean isOpen() throws CipurseException {
		return (mCard != null);
	}

	*//**
	 * @throws CipurseException {@link CipurseException}
	 *
	 *//*
	public void open() throws CipurseException {
		if(mCardTerminal != null){
			try {
				mCard = mCardTerminal.connect("*");
				if(mCard != null){
					mChannel = mCard.getBasicChannel();
				}
			} catch (CardException e) {
				throw new CipurseException(CipurseConstant.CTL_COMMS_TXRX_ERROR);
			}
		} else {
			throw new CipurseException(CipurseConstant.CTL_COMMS_TXRX_ERROR);
		}
	}

	*//**
	 * Method to reset the Communication Channel.
	 * @param type reset type as COLD_RESET 0 / WARM_RESET 1
	 * @return result as byte array.
	 * @throws CipurseException {@link CipurseException}
	 *
	 *//*
	public byte[] reset(int type) throws CipurseException {

		disconnect(type==0);

		// reconnect again
		open();
		if(mCard!=null) {
			return mCard.getATR().getBytes();
		}
		return null;
	}

	@Override
	*//**
	 * {@inheritDoc}
	 *//*
	public byte[] transReceive(byte[] command) throws CipurseException {
		try {
			if(mChannel != null){
				 ResponseAPDU r = mChannel.transmit(new CommandAPDU(command));
				 if(r != null){
					 return r.getBytes();
				 }
			}
		} catch (CardException e) {
			throw new CipurseException(CipurseConstant.CTL_COMMS_TXRX_ERROR);
		}
		return null;
	}


}

*/