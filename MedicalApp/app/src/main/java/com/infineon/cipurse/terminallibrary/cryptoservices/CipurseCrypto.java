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


package com.infineon.cipurse.terminallibrary.cryptoservices;

import java.util.Arrays;

import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.framework.Utils.eCaseType;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.pal.ISymCrypto;
import com.infineon.cipurse.terminallibrary.pal.ISymCrypto.ALGO_TYPE;

/**
* Defines CIPURSE terminal side crypto processing class.
*
* @since 1.0.0
* @version 1.0.1
*/
class CipurseCrypto {

	private byte[] frameKeyi;

	private byte[] frameKeyiPlus1;


	//<summary>
	//Random challenge of PICC
	//</summary>
	private byte[] RP;

	//<summary>
	//PICC Random number different from Rp
	//</summary>
	private byte[] rP;

	//<summary>
	//Terminal challenge
	//</summary>
	private byte[] RT;

	//<summary>
	//Terminal Random number different from RT
	//</summary>
	private byte[] rT;

	//<summary>
	//OSPT Session key derived rp, Rp, rT, RT
	//</summary>
	private byte[] k0;

	private ISymCrypto symCrypto = null;

	/**
	 * Constructor
	 * @param Aes		AES engine
	 * @param logger	Logger
	 */
	public CipurseCrypto(ISymCrypto Aes,ILogger logger){
		this.symCrypto = Aes;
	}

	/**
	 * Gives current session key
	 * @return	session key
	 */
	public byte[] getSessionKey(){
		return frameKeyi;
	}

	/**
	 * Sets current session key
	 * @param sessionKey	session key
	 */
	public void setSessionKey(byte[] sessionKey){
		frameKeyi = new byte[sessionKey.length];
		Utils.arrayCopy(sessionKey, 0, frameKeyi, 0, sessionKey.length);
	}

	/**
	 * <p>
	 * Modify the given APDU a, according to SMI, Secure Messaging
	 * Indicator. Also generate next frame key.
	 * </p>
	 * @param plainCommand - Plain text command
	 * @param SMI - SMI byte
	 * @return byte[] - Modified SM command
	 * @throws CipurseException In case of any error while processing
	 */
	public byte[] wrapCommand(byte[] plainCommand, byte SMI) throws CipurseException {

		// process according to SMI
		switch (SMI & CipurseConstant.BITMAP_SMI_COMMAND) {
		case CipurseConstant.SM_COMMAND_MACED:
			return getMACedCryptoGram(plainCommand, SMI);

		case CipurseConstant.SM_COMMAND_ENCED:
			return getENCedCryptoGram(plainCommand, SMI);

		case CipurseConstant.SM_COMMAND_RESPONSE_PLAIN:
			return getPlainSMCryptoGram(plainCommand, SMI);

		default:
			throw new CipurseException(CipurseConstant.CTL_CONDITIONS_OF_USE_NOT_SATISFIED);
		}
	}

	/**
	 * <p>
	 * Unwrap the given SM-Command. Also generate next frame key.
	 * </p>
	 * @param smCommand - SM command
	 * @param SMI - SMI byte
	 * @return byte[] - unwrapped command
	 * @throws CipurseException In case of any error while processing
	 */
	public byte[] unwrapCommand(byte[] smCommand, byte SMI) throws CipurseException {
		byte[] resp= null;
		// process according to SMI
		switch (SMI & CipurseConstant.BITMAP_SMI_FOR_RESPONSE) {
		case CipurseConstant.SM_RESPONSE_MACED:
			resp = unwrapMACedCommand(smCommand);
			return resp;

		case CipurseConstant.SM_RESPONSE_ENCED:
			return unwrapENCedCommand(smCommand);

		case CipurseConstant.SM_COMMAND_RESPONSE_PLAIN:
			resp = unwrapPlainSMCommand(smCommand);
			return resp;

		default:
			throw new CipurseException(CipurseConstant.CTL_CONDITIONS_OF_USE_NOT_SATISFIED);
		}

	}

	/**
	 * <p>
	 * Generate session k0 as specified in the OSPT Crypto Doc, section 5.2
	 * </p>
	 * @param kid - base key kid
	 * @param RP1 - PICC random challenge
	 * @param rP1 - PICC random string
	 * @param RT1 - Terminal random challenge
	 * @param rT1 - Terminal random string
	 * @return byte[] - Cp - PICC cryptogram
	 * @throws CipurseException In case of any error while processing
	 */
	public byte[] generateK0AndGetCp(byte[] kid, byte[] RP1, byte[] rP1,
			byte[] RT1, byte[] rT1) throws CipurseException {
		byte[] temp1, temp2;

		this.rP = rP1;
		this.RP = RP1;
		this.RT = RT1;
		this.rT = rT1;

		// session key derivation function
		// kP := NLM(EXT(kID), rP)
		// k0 := AES(key=PAD2(kP) XOR PAD(rT),kID) XOR kID
		temp1 = extFunction(kid, CipurseConstant.CIPURSE_SECURITY_PARAM_N);
		byte[] kp = computeNLM(temp1, rP);

		temp1 = pad2(kp);
		temp2 = pad(rT);
		temp1 = xor(temp1, temp2);

		// session key K0
		k0 = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128, temp1,null, kid);
		k0 = xor(k0, kid);

		// first frame key k1, function to calculate k1,
		// k1 := AES(key = RP; k0 XOR RT) XOR (k0 XOR RT)
		temp1 = xor(k0, RT);
		byte[] temp3 = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,RP,null, temp1);
		frameKeyi = xor(temp3, temp1);

		// function to caluclate cP := AES(key=k0, RP).
		// terminal response
		byte[] cP = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,k0,null, RP);

		return cP;
	}


	/**
	 * <p>
	 * Verify the PICC response
	 * </p>
	 * @param cT - Terminal cryptogram
	 * @return status of verification
	 * @throws CipurseException In case of any error while processing
	 */
    public boolean verifyPICCResponse(byte[] cT) throws CipurseException{
        //function to caluclate c'T := AES(key=k0, RT)
        byte[] cTDash = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,k0,null,RT);
        return Arrays.equals(cT, cTDash);
    }

    /**
     * <p>
     * Generate the plain SM-Command for the given Command
     * </p>
     * @param command - plain command
     * @param SMI - SMI byte
     * @return byte[] Plain SM-Command
     * @throws CipurseException
     */
	private byte[] getPlainSMCryptoGram(byte[] command, byte SMI) throws CipurseException {
		eCaseType orgCaseType =Utils.getCaseType(command);



		// check allowed value for Lc
		if (orgCaseType == eCaseType.CASE_4) {
			if (((short)((command[CipurseConstant.OFFSET_LC]&0x00FF))) > CipurseConstant.PLAIN_MAX_LC_WITHLE) {
				throw new CipurseException(CipurseConstant.CTL_SM_INVALID_LENGTH);
			}
		} else if (orgCaseType == eCaseType.CASE_3) {
			if (((short)((command[CipurseConstant.OFFSET_LC]&0x00FF))) > CipurseConstant.PLAIN_MAX_LC_WITHOUTLE) {
				throw new CipurseException(CipurseConstant.CTL_SM_INVALID_LENGTH);
			}
		}
		// Original command APDU:
		// - CLA - INS - P1 - P2 - {Lc} - {DATA} - {Le}
		// Transferred command SM-APDU:
		// - CLA' - INS - P1 - P2 - Lc' - SMI - {DATA} - {Le} - {Le'}
		// The Le'field is not present for the case where in the
		// original APDU the Le field is not present and
		// the PCD requests the PICC response in SM_PLAIN.
		byte[] smCommand = getOSPTModifiedCommand(command, SMI);
		// Calculate virtual MAC and generate new frame key
		byte[] dataPadded = Padding.schemeISO9797M2(
				smCommand, CipurseConstant.AES_BLOCK_LENGTH);
		generateMAC(dataPadded);

		// set Le' only if original Le not present and request in plain
		// otherwise Le' is always present
		return getLeDashCommand(smCommand, orgCaseType, SMI);
	}

	/**
	 * <p>
	 * Calculate the MAC for the command
	 * </p>
	 * @param command - plain command
	 * @param SMI - SMI byte
	 * @return byte[] - MACed command
	 * @throws CipurseException
	 */
	private byte[] getMACedCryptoGram(byte[] command, byte SMI) throws CipurseException {

		eCaseType orgCaseType = Utils.getCaseType(command);

		// check allowed value for Lc
		if (orgCaseType == eCaseType.CASE_4) {
			if ((short)((command[CipurseConstant.OFFSET_LC]&0x00FF)) > CipurseConstant.MAC_MAX_LC_WITHLE) {
				throw new CipurseException(CipurseConstant.CTL_SM_INVALID_LENGTH);
			}
		} else if (orgCaseType == eCaseType.CASE_3) {
			if ((short)((command[CipurseConstant.OFFSET_LC]&0x00FF)) > CipurseConstant.MAC_MAX_LC_WITHOUTLE) {
				throw new CipurseException(CipurseConstant.CTL_SM_INVALID_LENGTH);
			}
		} else if(orgCaseType == eCaseType.INVALID) {
				throw new CipurseException(CipurseConstant.CTL_INVALID_APDU_COMMAND);
		}

		/*
		 * Original command APDU: - CLA - INS - P1 - P2 - {Lc} - {DATA} - {Le}
		 * Transferred MAC'ed command SM-APDU: - CLA' - INS - P1 - P2 - Lc' -
		 * SMI - {DATA} - {Le} - MAC-{Le'}. The value for Lc' includes SMI,
		 * {DATA}, {Le}, and MAC, hence the limit for the original Lc is 246
		 * (with Le not present) resp. 245 (with Le present).The MAC of 8 byte
		 * in length is calculated over one or multiple 16-byte blocks of the
		 * following composition of elements: - CLA' - INS - P1 - P2 - Lc' - SMI -
		 * {DATA} - {Le} - padding
		 */
		byte[] smCommand = getOSPTModifiedCommand(command, SMI);

		// increase Lc by 8 MAC length
		smCommand[CipurseConstant.OFFSET_LC] =
			(byte) (smCommand[CipurseConstant.OFFSET_LC] + CipurseConstant.OSPT_MAC_LENGTH);
		byte[] smMacData = new byte[smCommand.length];
		System.arraycopy(smCommand, 0, smMacData, 0, smMacData.length);
		byte[] dataPadded = Padding.schemeISO9797M2(smMacData, CipurseConstant.AES_BLOCK_LENGTH);

		// generate MAc and compute next frame key
		byte[] mac = generateMAC(dataPadded);
		return mac;
	}

	/**
	 * <p>
	 * Encrypt the command
	 * </p>
	 * @param command - plain text command
	 * @param SMI - SMI byte
	 * @return byte[] - encrypted command
	 * @throws CipurseException
	 */
	private byte[] getENCedCryptoGram(byte[] command, byte SMI) throws CipurseException {

		eCaseType orgCaseType = Utils.getCaseType(command);

		// check allowed value for Lc
		if ((orgCaseType == eCaseType.CASE_4)
				|| (orgCaseType == eCaseType.CASE_3)) {
			if ((short)((command[CipurseConstant.OFFSET_LC]&0x00FF)) > CipurseConstant.ENC_MAX_LC) {
				throw new CipurseException(CipurseConstant.CTL_SM_INVALID_LENGTH);
			}
		}

		/*
		 * Original command APDU: - CLA - INS - P1 - P2 - {Lc} - {DATA} - {Le}
		 * Transferred ENC'ed command SM-APDU: - CLA' - INS - P1 - P2 - Lc' -
		 * SMI - n*CRYPTOGRAM- {Le} - {Le'}. The value for Lc' includes SMI,
		 * n*CRYPTOGRAM and {Le}.
		 *
		 * The n CRYPTOGRAM(s) of each 16 byte length are calculated from
		 * 16-byte blocks of the following composition of elements: - CLA' - INS -
		 * P1 - P2 - {DATA} - MIC - padding The MIC of 4 byte length is
		 * calculated over the following composition of elements: - CLA' - INS -
		 * P1 - P2 - Lc' - SMI - {DATA} - {Le} - {padding with 00H up to k * 4
		 * byte}
		 */
		byte[] orgLe = null;
		byte[] orgCommandData = null;

		if ((orgCaseType == eCaseType.CASE_3)
				|| (orgCaseType == eCaseType.CASE_4)) {
			orgCommandData = new byte[(short)(command[CipurseConstant.OFFSET_LC] & 0x00FF)];
			System.arraycopy(command, CipurseConstant.OFFSET_CMD_DATA,
					orgCommandData, 0, orgCommandData.length);
		}

		if ((orgCaseType == eCaseType.CASE_2)
				|| (orgCaseType == eCaseType.CASE_4)) {
			orgLe = new byte[1];
			orgLe[0] = command[command.length - 1];
		}

		byte[] smCommand = getOSPTModifiedCommand(command, SMI);

		// calculate length of n*Cryptgram after padding
		// header-Lc(=4) + original data + MIC-4
		byte[] nCryptogramPadded = null;
		if (orgCommandData != null) {
			nCryptogramPadded = new byte[4 + orgCommandData.length
					+ CipurseConstant.MIC_LENGH];
		} else {
			nCryptogramPadded = new byte[4 + CipurseConstant.MIC_LENGH];
		}
		nCryptogramPadded = Padding.schemeISO9797M2(nCryptogramPadded,
				CipurseConstant.AES_BLOCK_LENGTH);
		int nCryptogramPaddedLen = nCryptogramPadded.length;

		// prepare data for MIC calculation
		// - CLA' - INS - P1 - P2 - Lc' - SMI - {DATA} - {Le}
		byte[] dataForMIC = new byte[smCommand.length];
		System.arraycopy(smCommand, 0, dataForMIC, 0, dataForMIC.length);

		// Lc' includes SMI, n*CRYPTOGRAM and {Le}
		if (orgLe != null) {
			// SMI+n*Cryptgram+Le
			dataForMIC[CipurseConstant.OFFSET_LC] = (byte) (1 + nCryptogramPaddedLen + 1);
		} else {
			// SMI+n*Cryptgram
			dataForMIC[CipurseConstant.OFFSET_LC] = (byte) (1 + nCryptogramPaddedLen);
		}
		byte[] mic = computeMIC(dataForMIC);

		// prepare data for ciphering
		// CLA' - INS - P1 - P2 - {DATA} - MIC - padding
		System.arraycopy(smCommand, 0, nCryptogramPadded, 0, 4);

		int micOffset = 4;
		if (orgCommandData != null) {
			System.arraycopy(orgCommandData, 0, nCryptogramPadded, 4,orgCommandData.length);
			micOffset += orgCommandData.length;
		}
		System.arraycopy(mic, 0, nCryptogramPadded, micOffset, mic.length);

		// nCryptogramPadded is already padde, just encrypt
		byte[] nCryptogramCipher = generateCipher(nCryptogramPadded, true);

		return nCryptogramCipher;

	}

	/**
	 * <p>
	 * Unwrap the plain SM-response command
	 * </p>
	 * @param smCommand - Plain SM command
	 * @return byte[] - Unwrapped command
	 * @throws CipurseException
	 */
	private byte[] unwrapPlainSMCommand(byte[] smCommand) throws CipurseException {
		// calculate frame key
		byte[] dataPadded = Padding.schemeISO9797M2(smCommand,
				CipurseConstant.AES_BLOCK_LENGTH);

		// generate virtual MAC and generate next frame key
		generateMAC(dataPadded);

		return smCommand;
	}

	/**
	 * <p>
	 * Unwrap the MACed command
	 * </p>
	 * @param smCommand - MACed SM command
	 * @return byte[] - Unwrapped command
	 * @throws CipurseException
	 */
	private byte[] unwrapMACedCommand(byte[] smCommand) throws CipurseException {

		if (smCommand.length < (CipurseConstant.OSPT_MAC_LENGTH + 2)) {
			throw new CipurseException(
					CipurseConstant.CTL_INVALID_APDU_RESPONSE);
		}

		byte[] dataForMAC = new byte[smCommand.length
				- CipurseConstant.OSPT_MAC_LENGTH];
		System.arraycopy(smCommand, 0, dataForMAC, 0, (dataForMAC.length - 2)); // Data
		System.arraycopy(smCommand, (smCommand.length - 2), dataForMAC,
				(dataForMAC.length - 2), 2); // SW
		byte[] dataPadded = Padding.schemeISO9797M2(dataForMAC,
				CipurseConstant.AES_BLOCK_LENGTH);

		// generate MAC and next frame key
		byte[] hostMac = generateMAC(dataPadded);
		byte[] cardMac = new byte[CipurseConstant.OSPT_MAC_LENGTH];
		System.arraycopy(smCommand, (smCommand.length
				- CipurseConstant.OSPT_MAC_LENGTH - 2), cardMac, 0,
				CipurseConstant.OSPT_MAC_LENGTH);

		if (Arrays.equals(cardMac, hostMac)) {
			return dataForMAC;
		} else {
			throw new CipurseException(CipurseConstant.CTL_INCORRECT_SM_DATAOBJECT);
		}
	}

	/**
	 * <p>
	 * Unwrap the encrypted reponse
	 * </p>
	 * @param smCommand - ENCed SM command
	 * @return byte[] - Unwrapped command
	 * @throws CipurseException
	 */
	private byte[] unwrapENCedCommand(byte[] smCommand) throws CipurseException {

		byte[] encryptedResp = new byte[smCommand.length - 2];
		System.arraycopy(smCommand, 0, encryptedResp, 0, encryptedResp.length);

		if (encryptedResp.length >= 16 && (encryptedResp.length % CipurseConstant.AES_BLOCK_LENGTH) != 0) {
			throw new CipurseException(CipurseConstant.CTL_INVALID_APDU_RESPONSE);
		}

		// decrypt response data
		byte[] clearResp = generateCipher(encryptedResp, false);
		byte[] unpaddedClearResp = Padding.removeISO9797M2(clearResp);

		int minENcedRespLen = CipurseConstant.MIC_LENGH + 2;
		if ((unpaddedClearResp == null) || (unpaddedClearResp.length == 0)
				|| (unpaddedClearResp.length < minENcedRespLen)) {
			throw new CipurseException(CipurseConstant.CTL_INCORRECT_SM_DATAOBJECT);
		}

		//TODO: check if below check is required
		// compare the SW with the one passed in respose
		if ((unpaddedClearResp[unpaddedClearResp.length - 2] != smCommand[smCommand.length - 2])
				&& (unpaddedClearResp[unpaddedClearResp.length - 1] != smCommand[smCommand.length - 1])) {
			throw new CipurseException(CipurseConstant.CTL_INVALID_APDU_RESPONSE);
		}

		// unpadded length - (MIC+SW)
		int actualRespDataLen = unpaddedClearResp.length - (CipurseConstant.MIC_LENGH + 2);

		// data for MIC
		// {DATA} - SW1 - SW2 - {padding with 00H up to k * 4 byte}
		byte[] dataForMIC = new byte[actualRespDataLen + 2];
		System.arraycopy(unpaddedClearResp, 0, dataForMIC, 0, actualRespDataLen);
		System.arraycopy(smCommand, (smCommand.length - 2), dataForMIC, actualRespDataLen, 2);

		byte[] hostMIC = computeMIC(dataForMIC);
		byte[] cardMIC = new byte[CipurseConstant.MIC_LENGH];
		System.arraycopy(unpaddedClearResp, (unpaddedClearResp.length - 6),
				cardMIC, 0, CipurseConstant.MIC_LENGH);

		if (Arrays.equals(cardMIC, hostMIC)) {
			return dataForMIC;
		} else {
			throw new CipurseException(CipurseConstant.CTL_INCORRECT_SM_DATAOBJECT);
		}
	}


	/**
	 * <p>
	 * Generate OSPT MAC on the given input data.
	 * Data should be already padded.
	 * </p>
	 * @param dataMAC - padded input data for MAC
	 * @return byte[] - generated MAC value
	 * @throws CipurseException
	 */
	private byte[] generateMAC(byte[] dataMAC) throws CipurseException {
		/*
		 * Calculation of Mi and ki+1: hx := ki , hx+1 := AES( key = hx ; Dx )
		 * XOR Dx , hx+2 := AES( key = hx+1 ; Dx+1 ) XOR Dx+1, hx+3 := AES( key =
		 * hx+2 ; Dx+2 ) XOR Dx+2, ... hy+1 := AES( key = hy ; Dy ) XOR Dy, ki+1 :=
		 * hy+1 M'i := AES( key = ki ; ki+1 ) XOR ki+1, Mi := m LS bits of M'i = (
		 * (M'i )0, (M'i )1, ..., (M'i )m-1)
		 */

		byte[] blockDx = new byte[CipurseConstant.AES_BLOCK_LENGTH];
		frameKeyiPlus1 = frameKeyi;
		for (int i = 0; i < dataMAC.length; i += CipurseConstant.AES_BLOCK_LENGTH) {
			System.arraycopy(dataMAC, i, blockDx, 0, CipurseConstant.AES_BLOCK_LENGTH);
			byte[] temp = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,frameKeyiPlus1, null,blockDx);
			frameKeyiPlus1 = xor(temp, blockDx);
		}

		byte[] macBlock = xor(symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,frameKeyi,null, frameKeyiPlus1), frameKeyiPlus1);
		System.arraycopy(frameKeyiPlus1, 0, frameKeyi, 0, CipurseConstant.AES_BLOCK_LENGTH);
		byte[] actualMAC = new byte[CipurseConstant.OSPT_MAC_LENGTH];
		System.arraycopy(macBlock, 0, actualMAC, 0, CipurseConstant.OSPT_MAC_LENGTH);

		return actualMAC;
	}

	/**
	 * <p>
	 * Encrypt the given data using ciphering mechanism explained the OPST.
	 * </p>
	 * @param dataENC - data to be ciphered
	 * @param isEncrypt - to encrypt or decrypt
	 * @return byte[] - Ciphered Data
	 * @throws CipurseException
	 */
	private byte[] generateCipher(byte[] dataENC, boolean isEncrypt)
			throws CipurseException {
		/*
		 * hx-1 := ki , hx := AES( key = hx-1 ; q) XOR q, Cx := AES( key = hx ;
		 * Dx ), hx+1 := AES( key = hx ; q ) XOR q, Cx+1 := AES( key = hx+1 ;
		 * Dx+1 ), ... hy := AES( key = hy-1 ; q ) XOR q, Cy := AES( key = hy ;
		 * Dy ), ki+1 := hy
		 */

		byte[] blockDx = new byte[CipurseConstant.AES_BLOCK_LENGTH];
		byte[] ciphered = new byte[dataENC.length];

		frameKeyiPlus1 = frameKeyi;
		for (int i = 0; i < dataENC.length; i += CipurseConstant.AES_BLOCK_LENGTH) {
			byte[] hx = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,CipurseConstant.qConstant,null, frameKeyiPlus1);
			hx = xor(hx, frameKeyiPlus1);

			System.arraycopy(dataENC, i, blockDx, 0, CipurseConstant.AES_BLOCK_LENGTH);
			byte[] temp = null;
			if (isEncrypt) {
				temp = symCrypto.symCryptoEncryption(ALGO_TYPE.AES_128,hx,null, blockDx);
			} else {
				temp = symCrypto.symCryptoDecryption(ALGO_TYPE.AES_128,hx, null,blockDx);
			}
			System.arraycopy(temp, 0, ciphered, i, CipurseConstant.AES_BLOCK_LENGTH);
			System.arraycopy(hx, 0, frameKeyiPlus1, 0, CipurseConstant.AES_BLOCK_LENGTH);
		}

		System.arraycopy(frameKeyiPlus1, 0, frameKeyi, 0, CipurseConstant.AES_BLOCK_LENGTH);
		return ciphered;
	}

	/**
	 * <p>
	 * Calculate the MIC on given data
	 * </p>
	 * @param dataForMIC - MIC data
	 * @return byte[] - MIC value
	 */
	private byte[] computeMIC(byte[] dataForMIC) {
		int MIC_LENGH = 4;
		byte[] mic = new byte[4];

		byte[] paddedDataForMIC = null;

		// check if length of the input array is multiple of 4
		// else pad it with 00
		if ((dataForMIC.length % MIC_LENGH) != 0) {
			int toBePadded = MIC_LENGH - (dataForMIC.length % MIC_LENGH);
			paddedDataForMIC = new byte[dataForMIC.length + toBePadded];
			// remaining bytes are already zeros
			System.arraycopy(dataForMIC, 0, paddedDataForMIC, 0, dataForMIC.length);
		} else {
			paddedDataForMIC = new byte[dataForMIC.length];
			System.arraycopy(dataForMIC, 0, paddedDataForMIC, 0, paddedDataForMIC.length);
		}

		long crc1 = computeCRC(paddedDataForMIC);

		// swap bytes in paddedDataForMIC
		for (int i = 0; i <= (paddedDataForMIC.length - 4); i += 4) {
			byte temp1 = paddedDataForMIC[i];
			byte temp2 = paddedDataForMIC[i + 1];
			paddedDataForMIC[i] = paddedDataForMIC[i + 2];
			paddedDataForMIC[i + 1] = paddedDataForMIC[i + 3];
			paddedDataForMIC[i + 2] = temp1;
			paddedDataForMIC[i + 3] = temp2;
		}

		long crc2 = computeCRC(paddedDataForMIC);

		mic[0] = (byte) ((crc2 >> 8) & 0x00ff);
		mic[1] = (byte) (crc2 & 0x00ff);
		mic[2] = (byte) ((crc1 >> 8) & 0x00ff);
		mic[3] = (byte) (crc1 & 0x00ff);

		return mic;
	}

	/**
	 * <p>
	 * Calculate the CRC on given data
	 * </p>
	 * @param inputData - CRC data
	 * @return long - CRC value
	 */
	private long computeCRC(byte[] inputData) {

		long initialCRC = 0x6363;
		long ch = 0;

		for (int i = 0; i < inputData.length; i++) {
			ch = (short) (inputData[i] & 0xFF);
			ch = (short) (ch ^ (short) ((initialCRC) & 0xFF));
			ch = (short) ((ch ^ (ch << 4)) & 0xFF);

			long first = (long) ((initialCRC >> 8) & 0x0FFFF);
			long second = (long) ((ch << 8) & 0x0FFFF);
			long third = (long) ((ch << 3) & 0x0FFFF);
			long four = (long) ((ch >> 4) & 0x0FFFF);

			initialCRC = (long) ((first ^ second ^ third ^ four) & 0xFFFF);
		}
		return initialCRC;
	}

	private byte[] getLeDashCommand(byte[] smCommand, eCaseType orgCaseType,
			byte SMI) {
		byte[] smPlainCommand = null;

		if ((orgCaseType == eCaseType.CASE_1)
				|| (orgCaseType == eCaseType.CASE_3)) {
			if ((SMI & CipurseConstant.BITMAP_SMI_FOR_RESPONSE) != CipurseConstant.SM_COMMAND_RESPONSE_PLAIN) {
				smPlainCommand = getPlainCommand(smCommand);
			} else {
				smPlainCommand = smCommand;
			}
		} else {
			smPlainCommand = getPlainCommand(smCommand);
		}

		return smPlainCommand;
	}

	/**
	 * Method to get the plain command data
	 * @param smCommand byte array of the command data
	 * @return plain command data as byte array.
	 */
	private byte[] getPlainCommand(byte[] smCommand){
		byte[] smPlainCommand = null;
		smPlainCommand = new byte[smCommand.length + 1];
		System.arraycopy(smCommand, 0, smPlainCommand, 0, smCommand.length);
		smPlainCommand[smPlainCommand.length - 1] = CipurseConstant.LE_DASH;
		return smPlainCommand;
	}
	/**
	 * <p>
	 * Byte wise XOR the given two arrays
	 * </p>
	 * @param firstArray - Array One
	 * @param secondArray - Array Two
	 * @return byte[] - XORed byte array
	 * @throws CipurseException - Invalid parameters
	 */
	private byte[] xor(byte[] firstArray, byte[] secondArray) throws CipurseException {
		if (firstArray.length != secondArray.length) {
			throw new CipurseException(CipurseConstant.CTL_INVALID_DATA_LENGTH);
		}

		byte[] resultArray = new byte[firstArray.length];
		for (int i = 0; i < firstArray.length; i++) {
			resultArray[i] = (byte) (firstArray[i] ^ secondArray[i]);
		}
		return resultArray;

	}

	/**
	 * <p>
	 * Modify the given command as per OSPT SM-Command
	 * </p>
	 * @param command - Command to be modified
	 * @param SMI - SM Indicator
	 * @return byte[] - Modified Command
	 * @throws CipurseException - if APDU case in invalid
	 */
	private byte[] getOSPTModifiedCommand(byte[] command, byte SMI)
			throws CipurseException {

		eCaseType caseType = Utils.getCaseType(command);

		byte[] osptModifiedCmd = null;
		byte[] commandHeader = new byte[4];

		if (command.length >= CipurseConstant.OFFSET_LC) {
			System.arraycopy(command, 0, commandHeader, 0, 4);
		}

		switch (caseType) {
		case CASE_1:
			osptModifiedCmd = new byte[commandHeader.length + 2];
			System.arraycopy(commandHeader, 0, osptModifiedCmd, 0,
					commandHeader.length);
			osptModifiedCmd[CipurseConstant.OFFSET_LC] = 0x01; // Lc
			osptModifiedCmd[CipurseConstant.OFFSET_CMD_DATA] = SMI; // Data
			break;

		case CASE_2:
			osptModifiedCmd = new byte[commandHeader.length + 3];
			System.arraycopy(commandHeader, 0, osptModifiedCmd, 0,
					commandHeader.length);
			osptModifiedCmd[CipurseConstant.OFFSET_LC] = 0x02; // Lc
			osptModifiedCmd[CipurseConstant.OFFSET_CMD_DATA] = SMI; // Data
			osptModifiedCmd[CipurseConstant.OFFSET_CMD_DATA + 1] = command[CipurseConstant.OFFSET_LC]; // Le
			break;

		case CASE_3:
			osptModifiedCmd = new byte[(short)(commandHeader.length + 2
					+ (short)((command[CipurseConstant.OFFSET_LC] & 0x00FF)))];
			System.arraycopy(commandHeader, 0, osptModifiedCmd, 0,
					commandHeader.length);
			osptModifiedCmd[CipurseConstant.OFFSET_LC] = (byte)((int)(command[CipurseConstant.OFFSET_LC]) + 0x01); // Lc
			osptModifiedCmd[CipurseConstant.OFFSET_CMD_DATA] = SMI; // Data
			System.arraycopy(command, CipurseConstant.OFFSET_CMD_DATA,
					osptModifiedCmd, CipurseConstant.OFFSET_CMD_DATA + 1,
					(short)((command[CipurseConstant.OFFSET_LC] & 0x00FF)));
			break;

		case CASE_4:
			osptModifiedCmd = new byte[commandHeader.length + 3
					+ (short)((command[CipurseConstant.OFFSET_LC]&0x00FF))];
			System.arraycopy(commandHeader, 0, osptModifiedCmd, 0,
					commandHeader.length);
			osptModifiedCmd[CipurseConstant.OFFSET_LC] = (byte) (command[4] + 0x02); // Lc
			osptModifiedCmd[CipurseConstant.OFFSET_CMD_DATA] = SMI; // Data
			System.arraycopy(command, CipurseConstant.OFFSET_CMD_DATA,
					osptModifiedCmd, CipurseConstant.OFFSET_CMD_DATA + 1,
					(short)((command[CipurseConstant.OFFSET_LC]&0x00FF)));
			osptModifiedCmd[osptModifiedCmd.length - 1] = command[command.length - 1]; // Le
			break;

		default:
			throw new CipurseException(CipurseConstant.CTL_SM_INVALID_LENGTH);
		}

		osptModifiedCmd[0] = (byte) (osptModifiedCmd[0] | CipurseConstant.SM_BIT);
		return osptModifiedCmd;
	}

	/**
	 * <p>
	 * Compute Non Linear Mapping(NLM) of two 48 bit integers as specified in
	 * OSPT Crypto Document Section 7.1.
	 * </p>
	 * @param x - 48 bit integer 1 as byte array
	 * @param y - 48 bit integer 2 as byte array
	 * @return byte[] - NLM of x and y as a byte array
	 */
	private byte[] computeNLM(byte[] x, byte[] y) {
		int shiftBitsBy = 40;
		int i;
		long x1 = 0, y1 = 0;

		for (i = 0; i < x.length; i++) {
			x1 |= ((long) (x[i] & 0x00FF) << (shiftBitsBy - (i * 8)));
		}

		for (i = 0; i < y.length; i++) {
			y1 |= ((long) (y[i] & 0x00FF) << (shiftBitsBy - (i * 8)));
		}

		long nlm = computeNLM(x1, y1);
		byte[] retNLM = new byte[6];
		for (i = 0; i < retNLM.length; i++) {
			retNLM[i] = (byte) (nlm >> (shiftBitsBy - (i * 8)));
		}

		return retNLM;
	}

	/**
	 * <p>
	 * Compute Non Linear Mapping(NLM) of two 48 bit integers as specified in
	 * OSPT Crypto Document Section 7.1.
	 * </p>
	 * @param x - 48 bit integer 1
	 * @param y - 48 bit integer 2
	 * @return long - NLM of x and y
	 */
	private long computeNLM(long x, long y) {
		int i;
		long nlm = 0;
		final long constPolynomial = 0x35b088cce172L;

		for (i = 0; i < 48; i++) {

			nlm = shiftRight(nlm);
			if ((nlm & 1) == 1) {
				nlm = nlm ^ constPolynomial;
			}

			y = shiftRight(y);
			if ((y & 1) == 1) {
				nlm = nlm ^ x;
			}
		}

		return nlm;
	}

	/**
	 * <p>
	 * Shift right the long value by one bit. Out of 64 bits of long only 48 LS
	 * bits are considered remaining bits to be ignored.
	 * </p>
	 * @param ui48Bit - 48 bit integer
	 * @return long - shifted 48 bit integer
	 */
	private long shiftRight(long ui48Bit) {
		ui48Bit = ui48Bit << 1;
		if ((ui48Bit & 0x0001000000000000L) != 0) {
			ui48Bit = ui48Bit | 1;
			ui48Bit = ui48Bit & 0x0000ffffffffffffL;
		}
		return ui48Bit;
	}

	/**
	 * <p>
	 * OSPT specific padding of an array. Given array is pre-pended with zeros
	 * </p>
	 *
	 * @param x - array to be padded
	 * @return y - padded array
	 * @throws CipurseException - Invalid parameters
	 */
	private byte[] pad(byte[] x) throws CipurseException {
		byte[] y = new byte[CipurseConstant.AES_BLOCK_LENGTH];
		System.arraycopy(x, 0, y, (y.length - x.length), x.length);

		return y;
	}

	/**
	 * <p>
	 * OSPT specific padding of an array. Given array is doubled and pre-pended
	 * with zeros
	 * </p>
	 * @param x - array to be padded
	 * @return y - padded array
	 * @throws CipurseException - Invalid parameters
	 */
	private byte[] pad2(byte[] x) throws CipurseException {
		byte[] y = new byte[CipurseConstant.AES_BLOCK_LENGTH];
		System.arraycopy(x, 0, y, (CipurseConstant.AES_BLOCK_LENGTH - x.length),
				x.length);
		System.arraycopy(x, 0, y,
				(CipurseConstant.AES_BLOCK_LENGTH - (x.length * 2)), x.length);

		return y;
	}

	/**
	 * <p>
	 * Extract least significant N bytes from x
	 * </p>
	 * @param x - input array
	 * @param N - Number of bytes to extracted
	 * @return byte[] - extracted array
	 * @throws CipurseException - Invalid parameters
	 */
	private byte[] extFunction(byte[] x, int N) throws CipurseException {
		if (x.length < N) {
			throw new CipurseException(CipurseConstant.CTL_INVALID_DATA_LENGTH);
		}

		byte[] y = new byte[N];
		System.arraycopy(x, (x.length - N), y, 0, N);
		return y;
	}

}
