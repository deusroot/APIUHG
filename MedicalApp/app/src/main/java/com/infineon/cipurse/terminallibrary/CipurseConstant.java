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
* Defines Constants used in CIPURSE(TM) communication.
*
* @since 1.0.0
* @version 1.0.1
*/
public class CipurseConstant {

	/**
	 * APDU related Constants
	 */

	/**   **/
	public static final int OFFSET_CLA = 0x00;
	/**   **/
	public static final int OFFSET_INS = 0x01;
	/**   **/
	public static final int OFFSET_P1 = 0x02;
	/**   **/
	public static final int OFFSET_P2 = 0x03;
	/**   **/
	public static final int OFFSET_LC = 0x04;
	/**   **/
	public static final byte OFFSET_CMD_DATA = 0x05;

	/**
	 * Crypto related Constants
	 */

	/**   **/
	public static final int AES_BLOCK_LENGTH = 16;
	/**   **/
	public static final int CIPURSE_SECURITY_PARAM_N = 6;
	/**   **/
	public static final int MIC_LENGH = 4;
	/**   **/
	public static final int OSPT_MAC_LENGTH = 8;

	/**
	 * SM related Constants
	 */

	/**   **/
	public static final byte SM_COMMAND_RESPONSE_PLAIN = 0x00;
	/**   **/
	public static final byte BITMAP_SMI_FOR_RESPONSE = 0x0C;
	/**   **/
	public static final byte SM_BIT = 0x04;
	/**   **/
	public static final byte LE_DASH = 0x00;
	/**   **/
	public static final int PLAIN_MAX_LC_WITHLE = 253;
	/**   **/
	public static final int PLAIN_MAX_LC_WITHOUTLE = 254;
	/**   **/
	public static final int MAC_MAX_LC_WITHLE = 245;
	/**   **/
	public static final int MAC_MAX_LC_WITHOUTLE = 246;
	/**   **/
	public static final int ENC_MAX_LC = 231;
	/**   **/
	public  static final int CIPURSE_KVV_LENGTH = 3;

	/**
	 * Indicates value of SMI for PLAIN command
	 * 00-plain, 01-MAC, 10-ENC, 11-RFU
	 * **/
	public static final  byte SM_COMMAND_PLAIN = 0x00;

	/**
	 * Indicates value of SMI for MECed command
     * 00-plain, 01-MAC, 10-ENC, 11-RFU
     * **/
	public static final  byte SM_COMMAND_MACED = 0x40;

   /** Indicates value of SMI for ENCed response
     * 00-plain, 01-MAC, 10-ENC, 11-RFU
     * **/
	public static final byte SM_COMMAND_ENCED = (byte)0x80;

	/**
	 * Bits which indicate SM format for command in SMI
	 * Bit1 : XX-- ----
	 ***/
	public static final byte BITMAP_SMI_COMMAND = (byte)0xC0;


	/**
	 * Bits which indicate SM format for Response in SMI
	 * Bit1 : XX-- ----
	 **/
	public static final byte BITMAP_SMI_RESPONSE = (byte)0x0C;

	/**
	 * Indicates value of SMI for MECed response
     * 00-plain, 01-MAC, 10-ENC, 11-RFU
	 * **/
	public static final byte SM_RESPONSE_MACED = 0x04;

	/**
	 * Indicates value of SMI for ENCed response
     * 00-plain, 01-MAC, 10-ENC, 11-RFU
    **/
	public static final byte SM_RESPONSE_ENCED = 0x08;

	/**
	 * Const q used for ENCed commands
	 * **/
	public static final byte[] qConstant = new byte[] {
        0x74, 0x74, 0x74, 0x74, 0x74, 0x74, 0x74, 0x74,
        0x74, 0x74, 0x74, 0x74, 0x74, 0x74, 0x74, 0x74
    };



	/**
	 *  Exception Message
	 */
	/**   **/
	public static final String INVALID_SMI = "Invalid SMI value";
	/**   **/
	public static final String INVALID_PARAMS = "Invalid parameters";
	/**   **/
	public static final String LC_GRT_ALLOWED = "Lc > allowed";
	/**   **/
	public static final String RESP_LESS_THAN_MAC_RESP_LEN = "Response is less than min MACed response length(=8)";
	/**   **/
	public static final String RESP_MAC_FAILED = "Response MAC verification failed";
	/**   **/
	public static final String RESP_NOT_MUL_AES_BLOCK = "Response is not multiple of AES Blok";
	/**   **/
	public static final String RESP_LESS_THAN_MIN_ENC_RESP = "Response is less than min ENCed response length(=6)";
	/**   **/
	public static final String MISSMATCHED_SW = "SW in ENCed response != actual SW";
	/**   **/
	public static final String MIC_VER_FAIL = "MIC verify failed";
	/**   **/
	public static final String INVALID_MAC_BLOCK = "MAC Data not block aligned";
	/**   **/
	public static final String INVALID_CASE = "Command with invalid case";
	/**   **/
	public static final String INVALID_PAD_LEN = "Data to pad length > 128 bits";
	/**   **/
	public static final String INVALID_2PAD_LEN = "Length of data to pad * 2 > 128 bits";
	/**   **/
	public static final String INVALID_GETCHALLENGE_RES_LEN = "Get Challenge response is != 16H";
	/**   **/
	public static final String INVALID_MUTUAL_AUTH_RES_LEN = "Mutual Authentication response length != 10H";
	/**   **/
	public static final String INVALID_KEY_LEN = "Not a valid key length";
	/**   **/
	public static final String KEY_NOT_INIT = "Key value is not initialized";
	/**   **/
	public static final String INVALID_RESET_TYPE = "Invalid reset type, COLD_RESET = 0, WARM_RESET = 1";

	/**
	 * Terminal Library related Error Codes
	 */
	/**  Indicates terminal library API success status word.   **/
	public static final int  CTL_SUCCESS =  0x0000;
	/** Indicates terminal library comms module success status word.   **/
	public static final int  CTL_COMMS_SUCCESS =  CTL_SUCCESS;
	/**  Indicates comms module transmission and reception error status word. */
	public static final int  CTL_COMMS_TXRX_ERROR =  0xFFFF;
	/**  Indicates internal error status word. */
	public static final int CTL_STATUS_INT_VALUE  = 0x4526;
	/** Indicates NULL pointer error status word.   **/
	public static final int  CTL_NULL_PTR =  0x0001;
	/** Indicate the incorrect secure messaging error status word.   **/
	public static final int  CTL_INCORRECT_SM_DATAOBJECT =  0x1002;
	/** Indicates the invalid parameter error status word.   **/
	public static final int  CTL_INVALID_PARAMETER  = 0x1102;
	/** Indicates the invalid APDU error status word.   **/
	public static final int  CTL_INVALID_APDU_COMMAND =  0x1103;
	/** Indicates the operation not allowed error status word.   **/
	public static final int  CTL_OPERATION_NOT_ALLOWED =  0x1104;
	/** Indicates the internal error status word.   **/
	public static final int  CTL_INT_ERROR =  0x30FF;
	/** Indicates the secure messaging not in progress error status word.   **/
	public static final int  CTL_SM_NOT_IN_PROGRESS =  0x3002;
	/** Indicates the invalid length error status word.  **/
	public static final int  CTL_SM_INVALID_LENGTH =  0x3003;
	/** Indicates the incorrect SMI error status word.  **/
	public static final int  CTL_INCORRECT_SMI_LE  = 0x3004;
	/** Indicates the invalid APDU error status word.   **/
	public static final int  CTL_INVALID_APDU_RESPONSE =  0x3006;
	/**Indicates the logger error status word.   **/
	public static final int  CTL_LOGGER_ERROR =  0xFFFF;
	/** Indicates the security not satisfied error status word.   **/
	public static final int  CTL_SECURITY_NOT_SATISFIED =  0x4001;
	/** Indicates the invalid data length error status word.  **/
	public static final int  CTL_INVALID_DATA_LENGTH =  0x4002;
	/** Indicates the command not allowed error status word.   **/
	public static final int  CTL_CMD_NOT_ALLOWED =  0x4003;
	/** Indicates the incorrect parameter in command data error status word.  **/
	public static final int  CTL_INCORRECT_PARAM_IN_CMD_DATA =  0x4004;
	/** Indicates the function not supported error status word.   **/
	public static final int  CTL_FUNCTION_NOT_SUPPORTED =  0x4005;
	/** Reference Record Not Found (Key referenced by KeyID not found)   **/
	public static final int  CTL_REFERENCE_RECORD_NOT_FOUND =  0x4006;
	/**Referenced data or reference data not found (e.g. wrong key number)   **/
	public static final int  CTL_REFERENCE_DATA_NOT_FOUND =  0x4007;
	/** Conditions of use not satisfied. **/
	public static final int  CTL_CONDITIONS_OF_USE_NOT_SATISFIED =  0x4008;
	/** Referenced key is not in usable state. **/
	public static final int  CTL_REFERENCED_KEY_NOT_USABLE =  0x4009;
	/** Retry counter reached its limit. **/
	public static final int  CTL_AUTH_METHOD_BLOCKED =  0x400A;
	/**   Authentication Failed. **/
	public static final int  CTL_AUTHENTICATION_FAILED =  0x400B;
	/** This error code is returned when secure messaging in progress and plain data in
	 *  the last block of SM_ENC mode matches with immediate first plain data block of
	 *  SM_MAC or SM_PLAIN mode over which frame key is computed.
	 **/
	public static final int  CTL_SECURITY_RELATED_ISSUES =  0x400C;
	/** Command not allowed. CIPURSE SAM use does not support command. **/
	public static final int  CTL_CMD_NOT_ALLOWED_SAM =  0x400D;
	/**  Invalid INS.  **/
	public static final int  CTL_STATUS_INVALID_INS =  0x400E;
	/** Invalid CLA.   **/
	public static final int  CTL_STATUS_INVALID_CLA  = 0x400F;
	/** Incorrect Parameters P1-P2.  **/
	public static final int  CTL_STATUS_INCORRECT_P1P2 =  0x4010;
	/** Command Incompatible with File Structure.   **/
	public static final int  CTL_STATUS_CMD_INCOMPATIBLE_WITH_FILETYPE =  0x4011;
	/** Memory Failure.  **/
	public static final int  CTL_STATUS_MEMORY_FAILURE =  0x4012;
	/** Selected File Invalidated.  **/
	public static final int  CTL_STATUS_FILE_INVALIDATED =  0x4013;
	/** File or Application not found.   **/
	public static final int  CTL_STATUS_FILE_NOT_FOUND  = 0x4014;
	/** Not enough memory on chip to satisfy conditions.   **/
	public static final int  CTL_STATUS_NOT_ENOUGH_MEMORY =  0x4015;
	/** FileID / SFID already exists.   **/
	public static final int  CTL_STATUS_FILEID_ALREADY_EXISTS =  0x4016;
	/** AID already exists.   **/
	public static final int  CTL_STATUS_AID_ALREADY_EXISTS  = 0x4017;
	/** Authorization Failed, X Remaining attempts.   **/
	public static final int  CTL_STATUS_AUTHORISATION_FAILED =  0x4018;
	/** Provided response length less than 2 bytes.   **/
	public static final int  CTL_STATUS_INVALID_RSP_LENGTH =  0x4019;
	/** Provided response ISO status word can not be mapped.   **/
	public static final int  CTL_STATUS_UNMAPPER_ERRORCODE =  0x401A;
	/**	  Key not found error in case of requested key is not present in the key vault.   **/
	public static final int  CTL_KEY_NOT_FOUND =  0x7001;
	/** Lc exceeds maximum Limit */
	public static final int CTL_LC_MORE_THAN_MAX = 0x401B;
	/** Encryption Key not specified */
	public static final int CTL_KEY_NOT_SPECIFIED = 0x401C;
	/** Invalid Key length provided */
	public static final int CTL_INVALID_KEY_LENGTH = 0X401D;
	/** Invalid Key ID */
	public static final int CTL_INVALID_KEYID = 0x401E;
	/** Invalid Key */
	public static final int CTL_INVALID_KEY = 0x401F;
	/** Error at Crypto Layer */
	public static final int CTL_CRYPTO_ERROR = 0x4020;
	/** Invalid Diversification Mode */
	public static final int CTL_INVALID_DIVERSIFICATION_MODE = 0X4021;
	/** Invalid length of Diversification Data */
	public static final int CTL_INVALID_DIVERSIFICATION_DATA = 0X4022;
	/** Invalid Key Algorithm ID */
	public static final int CTL_INVALID_ALGO_ID = 0x4023;
	/** Invalid AES block data/length */
	public static final int CTL_INVALID_AES_BLOCK = 0x4024;
	/** MIC verification Failed */
	public static final int CTL_MIC_VER_FAIL = 0x4025;
	/*
	 * ISO status codes
	 */
	/** OK status **/
    public static final int eISO_STATUS_OK                              = 0x9000;
    /** Invalid INS **/
	public static final int eISO_STATUS_INVALID_INS                     = 0x6D00;
    /** Invalid CLA **/
	public static final int eISO_STATUS_INVALID_CLA                     = 0x6E00;
    /** Unknown error **/
	public static final int eISO_STATUS_UNKNOWN                         = 0x6F00;
    /** Incorrect Parameters P1-P2 **/
	public static final int eISO_STATUS_INCORRECT_P1P2                  = 0x6B00;
    /** Command Incompatible with File Structure **/
	public static final int eISO_STATUS_CMD_INCOMPATIBLE_WITH_FILETYPE  = 0x6981;
    /** Memory Failure  **/
	public static final int eISO_STATUS_MEMORY_FAILURE                  = 0x6581;
    /** Wrong Length **/
	public static final int eISO_STATUS_INVALID_DATA_LENGTH             = 0x6700;
    /** Selected File Invalidated **/
	public static final int eISO_STATUS_FILE_INVALIDATED                = 0x6283;
    /** Security Status Not satisfied **/
	public static final int eISO_STATUS_SECURITY_NOT_SATISFIED          = 0x6982;
    /** Command Not Allowed **/
	public static final int eISO_STATUS_COMMAND_NOT_ALLOWED             = 0x6986;
    /** Incorrect Secure Message Data Object **/
	public static final int eISO_STATUS_INCORRECT_SM_DATAOBJECT         = 0x6988;
    /** Incorrect Parameters in Command Data Field **/
	public static final int eISO_STATUS_INCORRECT_PARAM_IN_CMD_DATA     = 0x6A80;
    /** Function  Not Supported **/
	public static final int eISO_STATUS_FUNCTION_NOT_SUPPORTED          = 0x6A81;
    /** File or Application not found **/
	public static final int eISO_STATUS_FILE_NOT_FOUND                  = 0x6A82;
    /** referenced record not found **/
	public static final int eISO_STATUS_REFERENCE_RECORD_NOT_FOUND      = 0x6A83;
    /** Referenced data or Reference Data not Found (e.g. Key number) **/
	public static final int eISO_STATUS_REFERENCE_DATA_NOT_FOUND        = 0x6A88;
    /** Conditions of use not satisfied **/
	public static final int eISO_STATUS_CONDITIONS_OF_USE_NOT_SATISFIED = 0x6985;
    /** Not enough memory on chip to satisfy conditions **/
	public static final int eISO_STATUS_NOT_ENOUGH_MEMORY               = 0x6A84;
    /** FileID / SFID already exists **/
	public static final int eISO_STATUS_FILEID_ALREADY_EXISTS           = 0x6A89;
    /** AID already exists **/
	public static final int eISO_STATUS_AID_ALREADY_EXISTS              = 0x6A8A;
    /** Referenced key is not in usable state **/
	public static final int eISO_STATUS_REFERENCED_KEY_NOT_USABLE       = 0x6984;
    /** Retry counter reached its limit **/
	public static final int eISO_STATUS_AUTH_METHOD_BLOCKED             = 0x6983;
    /** Authentication Failed **/
	public static final int eISO_STATUS_AUTHENTICATION_FAILED           = 0x6300;
    /** Authentication Failed; X Remaining attempts **/
	public static final int eISO_STATUS_AUTHORISATION_FAILED            = 0x63C0;
    /** Security related issue. **/
	public static final int eISO_STATUS_SECURITY_RELATED_ISSUES         = 0x6600;
    /** Command not allowed. CIPURSE SAM use does not support command **/
	public static final int eISO_STATUS_CMD_NOT_ALLOWED_SAM             = 0x6900;
    /** Warning: Referenced Counter  has reached the warning level **/
	public static final int eISO_STATUS_GENERAL_WARNING                 = 0x63F0;
    /** UnWrap Response in Chaining Mode **/
	public static final int eISO_STATUS_CHAINING_MODE                   = 0x9F00;
    /** Warning message on sector write count reached maximum **/
	public static final int eISO_STATUS_CNTR_TRXN_WARNING               = 0x63F1;
    /** Warning indicate if generated or diversified 2kTDES/3kTDES key **/
	public static final int eISO_STATUS_WEAK_KEY_WARNING                = 0x63F2;
}
