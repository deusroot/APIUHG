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
 * Defines all Constants used related to CIPURSE(TM) Commands.
 *
 * @since 1.0.0
 * @version 1.0.1
 */
public interface CipurseCommandsConstants {


	/**Select file instruction code */
	public static final byte INS_SELECT              	= (byte)0xA4;

	/**Read Binary instruction code */
	public static final byte INS_READ_BINARY         	= (byte)0xB0;

	/**Update Binary instruction code */
	public static final byte INS_UPDATE_BINARY       	= (byte)0xD6;

	/**Update Binary instruction code */
	public static final byte INS_GET_RANDOM          	= (byte)0x84;

	/**Mutual Authenticate instruction code */
	public static final byte INS_MUTUAL_AUTH			= (byte)0x82;

	/**Read Record instruction code */
	public static final byte INS_READ_RECORD			= (byte)0xB2;

	/**Update Record instruction code */
	public static final byte INS_UPDATE_RECORD			= (byte)0xDC;

	/**Read Value instruction code */
	public static final byte INS_READ_VALUE				= (byte)0x34;

	/**Increase Value instruction code */
	public static final byte INS_INCREASE_VALUE			= (byte)0x32;

	/**Decrease Value instruction code */
	public static final byte INS_DECREASE_VALUE			= (byte)0x30;

	/**Append Record instruction code */
	public static final byte INS_APPEND_RECORD			= (byte)0xE2;

	/**Read File Attributes instruction code */
	public static final byte INS_READ_FILE_ATTRIB		= (byte)0xCE;

	/**Update File Attributes instruction code */
	public static final byte INS_UPDATE_FILE_ATTRIB		= (byte)0xDE;

	/**Format All instruction code */
	public static final byte INS_FORMAT_ALL				= (byte)0xFC;

	/**Update Key instruction code */
	public static final byte INS_UPDATE_KEY				= (byte)0x52;

	/**Update Key Attributes instruction code */
	public static final byte INS_UPDATE_KEY_ATTRIB		= (byte)0x4E;

	/**Create file instruction code */
	public static final byte INS_CREATE_FILE			= (byte)0xE0;

	/**Delete file instruction code */
	public static final byte INS_DELETE_FILE			= (byte)0xE4;

	/**Activate file instruction code */
	public static final byte INS_ACTIVATE_FILE			= (byte)0x44;

	/**De-activate file instruction code */
	public static final byte INS_DEACTIVATE_FILE		= (byte)0x04;

	/**Perform Transaction instruction code */
	public static final byte INS_PERFORM_TRANSACTION	= (byte)0x7E;

	/**Cancel Transaction instruction code */
	public static final byte INS_CANCEL_TRANSACTION		= (byte)0x7C;

	/**Class byte Offset in Command APDU */
	public static final byte APDU_OFFSET_CLA			= (byte)0x00;

	/**Parameter1 byte Offset in Command APDU */
	public static final byte APDU_OFFSET_P1				= (byte)0x02;

	/**Parameter2 byte Offset in Command APDU */
	public static final byte APDU_OFFSET_P2				= (byte)0x03;

	/**Lc/Le byte Offset in Command APDU */
	public static final byte APDU_OFFSET_P3				= (byte)0x04;

	/**Command data Offset in Command APDU */
	public static final byte APDU_OFFSET_CDATA			= (byte)0x05;

	
	/** INS for limited increase */
	public static final byte INS_LIMITED_INCREASE		=(byte)0x3C;
	/** INS for limited decrease */
	public static final byte INS_LIMITED_DENCREASE		=(byte)0x3E;
	

	/**
	 *
	 */
	public final static byte[] AUTHENTICATE_CBP_HEADER=new byte[]{(byte) 0x80, (byte) 0xA6, 0, 0, 0};
	/**
	 *
	 */
	public final static byte[] GENERATESM_HEADER=new byte[]{(byte) 0x80, 0x56, 0, 0, 0};
	/**
	 *
	 */
	public final static byte[] VERIFYSM_HEADER=new byte[]{(byte) 0x80, 0x58, 0, 0, 0};
	/**
	 *
	 */
	public final static byte[] AUTHENTICATE_SAM_HEADER=new byte[]{(byte) 0x80, (byte) 0xA8, 0, 0, 0};
	/**
	 *
	 */
	public final static byte[] DIVERSIFY_HEADER=new byte[]{(byte) 0x80, 0x76, 0, 0, 0};
	/**
	 *
	 */
	public final static byte[] ENDSESSION_HEADER=new byte[]{(byte) 0x80, 0x5C, 0, 0, 0};
	/**
	 *
	 */
	public final static byte[] READSESSIONKEY_HEADER=new byte[]{(byte) 0x80, 0x5E, 0, 0, 0};
	/**
	 * Both command APDU and Response APDU are in Plain mode.
	 */
	public static final byte SMI_PLAIN_PLAIN = 0x00;

	/**
	 * Both command APDU and Response APDU are in Plain mode with original Le present in command APDU.
	 */
	public static final byte SMI_PLAIN_PLAIN_LE_PRESENT = 0x01;

	/**
	 * Command APDU is in plain mode and response is in MAC mode.
	 */
	public static final byte SMI_PLAIN_MAC = 0x04;

	/**
	 * Command APDU is in plain mode, response is in MAC mode and original Le present in command APDU.
	 */
	public static final byte SMI_PLAIN_MAC_LE_PRESENT = 0x05;

	/**
	 * Command APDU is in plain mode and response is in encryption mode.
	 */
	public static final byte SMI_PLAIN_ENC = 0x08;

	/**
	 * Command APDU is in plain mode, response is in encryption mode and original Le present in command APDU.
	 */
	public static final byte SMI_PLAIN_ENC_LE_PRESENT = 0x09;

	/**
	 * Command APDU is in MAC mode and response is in plain mode.
	 */
	public static final byte SMI_MAC_PLAIN = 0x40;

	/**
	 * Command APDU is in MAC mode, response is in plain mode and original Le present in command APDU.
	 */
	public static final byte SMI_MAC_PLAIN_LE_PRESENT = 0x41;

	/**
	 * Command APDU is in MAC mode and response is in MAC mode.
	 */
	public static final byte SMI_MAC_MAC = 0x44;

	/**
	 * Command APDU is in MAC mode, response is in MAC mode and original Le present in command APDU.
	 */
	public static final byte SMI_MAC_MAC_LE_PRESENT = 0x45;

	/**
	 * Command APDU is in MAC mode and response is in encryption mode.
	 */
	public static final byte SMI_MAC_ENC = 0x48;

	/**
	 * Command APDU is in MAC mode, response is in encryption mode and original Le present in command APDU.
	 */
	public static final byte SMI_MAC_ENC_LE_PRESENT = 0x49;

	/**
	 * Command APDU is in encryption mode and response is in plain mode.
	 */
	public static final byte SMI_ENC_PLAIN = (byte)0x80;

	/**
	 * Command APDU is in encryption mode, response is in plain mode and original Le present in command APDU.
	 */
	public static final byte SMI_ENC_PLAIN_LE_PRESENT = (byte)0x81;

	/**
	 * Command APDU is in encryption mode and response is in MAC mode.
	 */
	public static final byte SMI_ENC_MAC = (byte)0x84;

	/**
	 * Command APDU is in encryption mode, response is in MAC mode and original Le present in command APDU.
	 */
	public static final byte SMI_ENC_MAC_LE_PRESENT = (byte)0x85;

	/**
	 * Command APDU is in encryption mode and response is in encryption mode.
	 */
	public static final byte SMI_ENC_ENC = (byte)0x88;

	/**
	 * Command APDU is in encryption mode, response is in encryption mode and original Le present in command APDU.
	 */
	public static final byte SMI_ENC_ENC_LE_PRESENT = (byte)0x89;

	/**
	 * APDU header for MUTUAL AUTHENTICATE command
	 */
	public static final byte[] mutualAuthHeader = new byte[] { 0x00, (byte) 0x82, 0x00, 0x00, 0};

	/**
	 * APDU header for VERIFY SAM PASSWORD command
	 */
	public final static byte[] VERIFYPASSWORD_HEADER=new byte[]{(byte) 0x80, 0x18, 0, 0, 0x10};

	/**
	 * APDU header for GET KEY INFO command
	 */
	public final static byte[] GETKEYINFO_HEADER=new byte[]{(byte) 0x80, 0x54, 0, 0, 0};
}
