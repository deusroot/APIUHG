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
package com.infineon.cipurse.terminallibrary.presentation;

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.cryptoservices.CipurseSAMCryptoHWApi;
import com.infineon.cipurse.terminallibrary.cryptoservices.CipurseSAMCryptoSWApi;
import com.infineon.cipurse.terminallibrary.cryptoservices.CipurseSMApi;
import com.infineon.cipurse.terminallibrary.cryptoservices.ICipurseSAMCryptoApi;
import com.infineon.cipurse.terminallibrary.cryptoservices.ICipurseSMApi;
import com.infineon.cipurse.terminallibrary.framework.CommandBuilder;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.pal.ICommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.pal.ISymCrypto;


/**
* Class implementing SessionManager.
*
* @since 1.0.0
* @version 1.0.1
*/
public class SessionManager implements ISessionManager {

	private CRYPTO_MODE cryptoMode;
	private ICommunicationHandler commCBPHandler;
	private ICommunicationHandler commHWHandler;
	private ILogger logger;
	private ISymCrypto symCrypto;
	private ICipurseSMApi cipurseSMApi;
	private ISessionManager sessionMgr;

	private ICipurseSAMCryptoApi mSWSAMCryptoApi;
	private ICipurseSAMCryptoApi mHWSAMCryptoApi;
	private SMTYPE smType;
	private CRYPTO_MODE currentSAM_mode;
	private SMLevel smLevel;
	private CommandBuilder commandBuilder;

	/**
	 * Default Constructor for SessionManager
	 */
	public SessionManager() {
		cipurseSMApi = new CipurseSMApi(this);
		commandBuilder = new CommandBuilder();
		this.mHWSAMCryptoApi = new CipurseSAMCryptoHWApi();
		this.mSWSAMCryptoApi = new CipurseSAMCryptoSWApi();
	}


	@Override
	public boolean switchToSWSAM() throws CipurseException {

		if(cryptoMode == CRYPTO_MODE.HYBRID && currentSAM_mode == CRYPTO_MODE.HW_SAM)
		{
			SMLevel tempSMI = smLevel;
			ByteArray sessionKey = getCipurseSMApi().getCryptoAPI().getSessionKey();
			mSWSAMCryptoApi.initializeCryptoAPI(cipurseSMApi, cryptoMode, null, logger, symCrypto);
			cipurseSMApi = cipurseSMApi.initializeSMAPI(mSWSAMCryptoApi, smType, commCBPHandler, logger);
			currentSAM_mode = CRYPTO_MODE.SW_SAM;
			getCipurseSMApi().getCryptoAPI().setSessionKey(sessionKey);
			this.smLevel = tempSMI;
			return true;
		}
		return false;
	}

	@Override
	public boolean restoreHybridMode() throws CipurseException {
		if(cryptoMode == CRYPTO_MODE.HYBRID && currentSAM_mode == CRYPTO_MODE.SW_SAM)
		{
			cipurseSMApi.resetSM();
			mHWSAMCryptoApi.initializeCryptoAPI(cipurseSMApi,cryptoMode,commHWHandler, logger, symCrypto);
			cipurseSMApi = cipurseSMApi.initializeSMAPI(mHWSAMCryptoApi, smType,commCBPHandler, logger);
			currentSAM_mode = CRYPTO_MODE.HW_SAM;
			return true;
		}
		return false;

	}

	@Override
	public ByteArray authorizeHWSAM(ByteArray ppsAIDValue, ByteArray ppsPassword) throws CipurseException {

		logger.log("\"authorizeHWSAM\" function parameters :\n"
						+ String.format("ppsAIDValue: %s%nppsPassword: %s%n",
								ppsAIDValue, ppsPassword));
		if(cryptoMode == CRYPTO_MODE.SW_SAM){
			throw new CipurseException(CipurseConstant.CTL_OPERATION_NOT_ALLOWED);
		}
		ByteArray selectCmd = commandBuilder.buildSelectFileByAID((byte)0x00, ppsAIDValue, (short)-1);
		logger.log(ILogger.COMMAND_MESSAGE,"SAM:","", selectCmd.getBytes());
		byte[] resp = commHWHandler.transReceive(selectCmd.getBytes());
		logger.log( ILogger.RESPONSE_MESSAGE,"SAM:","", resp);
		int sw=Utils.getShort(resp, resp.length - 2);
		if(sw!= 0x9000 )
			return new ByteArray(resp);

		ByteArray verifySamPswdCmd = commandBuilder.buildVerifySAMPassword(ppsPassword);
		logger.log(ILogger.COMMAND_MESSAGE,"SAM:","", verifySamPswdCmd.getBytes());
		resp = commHWHandler.transReceive(verifySamPswdCmd.getBytes());
		logger.log(ILogger.RESPONSE_MESSAGE,"SAM:","", resp);

		return new ByteArray(resp);
	}

	@Override
	public void setSMIValue(SMLevel smi) {
		logger.log("Set SMI value\nSM Level :  0x" + String.format("%02x", smi.getSMLevel()) );
		this.smLevel = smi;
	}

	@Override
	public void setVersionInfo(CIPURSEVersion PeCIPURSEVersion,
			CIPURSESAMVersion PeCIPURSESAMVersion) throws CipurseException {
		logger.log("\"setVersionInfo\" function parameters :\n"
				+ String.format("CIPURSE Version: %s%nCIPURSE SAM Version: %s%n",
						PeCIPURSEVersion, PeCIPURSESAMVersion));
		if(PeCIPURSEVersion != CIPURSEVersion.eVERSION2R2)
			throw new CipurseException(CipurseConstant.CTL_OPERATION_NOT_ALLOWED);
		if(PeCIPURSESAMVersion != CIPURSESAMVersion.eVERSION2)
			throw new CipurseException(CipurseConstant.CTL_OPERATION_NOT_ALLOWED);
	}

	@Override
	public void setSessionContext(ISessionManager sessionMgr) {
		this.sessionMgr = sessionMgr ;

	}

	@Override
	public ISessionManager initializeSession(
			CRYPTO_MODE peCryptoMode,SMTYPE smModeType,
			ICommunicationHandler psCBPCommsHandle,
			ICommunicationHandler psHWSAMCommsHandle, ILogger psLoggerHandle,
			ISymCrypto psAESHandle ) throws CipurseException {

		if(smModeType!=SMTYPE.CIPURSE)
			throw new CipurseException(CipurseConstant.CTL_FUNCTION_NOT_SUPPORTED);
		if(null == psLoggerHandle)
			throw new CipurseException(CipurseConstant.CTL_NULL_PTR);
		this.cryptoMode = peCryptoMode;
		this.smType = smModeType;
		this.commCBPHandler = psCBPCommsHandle;
		this.commHWHandler = psHWSAMCommsHandle;
		this.logger = psLoggerHandle;
		this.symCrypto = psAESHandle;
		configureSessionManager();
		sessionMgr =this;
		smLevel=SMLevel.eSM_NO_SECMSG;
		return sessionMgr;
	}

	private void configureSessionManager() throws CipurseException {

		switch (cryptoMode) {
		case SW_SAM:
			mSWSAMCryptoApi.initializeCryptoAPI(cipurseSMApi, cryptoMode, null, logger, symCrypto);
			cipurseSMApi = cipurseSMApi.initializeSMAPI(mSWSAMCryptoApi, smType,commCBPHandler, logger);
			break;
		case HW_SAM:
			mHWSAMCryptoApi.initializeCryptoAPI(cipurseSMApi, cryptoMode, commHWHandler, logger, symCrypto);
			cipurseSMApi = cipurseSMApi.initializeSMAPI(mHWSAMCryptoApi, smType,commCBPHandler, logger);
			break;
		case HYBRID:
			mHWSAMCryptoApi.initializeCryptoAPI(cipurseSMApi, cryptoMode, commHWHandler, logger, symCrypto);
			cipurseSMApi = cipurseSMApi.initializeSMAPI(mHWSAMCryptoApi, smType, commCBPHandler, logger);
			currentSAM_mode = CRYPTO_MODE.SW_SAM;
			break;
		default:
			break;
		}

	}


	@Override
	public ICipurseSMApi getCipurseSMApi() {
		return cipurseSMApi;
	}

	@Override
	public boolean resetSM() throws CipurseException {

		return cipurseSMApi.resetSM();
	}

	@Override
	public SMLevel getSMIValue() {
		return this.smLevel;
	}

	@Override
	public ILogger getLogger() {
		return logger;
	}

	@Override
	public CRYPTO_MODE getCurrentCryptoMode() {
		return this.cryptoMode;
	}


	@Override
	public ICommunicationHandler getSAMCommsHandler() {
		return commHWHandler;
	}


	@Override
	public ICommunicationHandler getCBPCommsHandler() {
		return commCBPHandler;
	}

}
