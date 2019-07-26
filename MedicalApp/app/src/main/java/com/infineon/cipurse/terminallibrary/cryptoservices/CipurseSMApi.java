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

import com.infineon.cipurse.terminallibrary.ByteArray;
import com.infineon.cipurse.terminallibrary.CipurseCommandsConstants;
import com.infineon.cipurse.terminallibrary.CipurseConstant;
import com.infineon.cipurse.terminallibrary.CipurseException;
import com.infineon.cipurse.terminallibrary.framework.CommandBuilder;
import com.infineon.cipurse.terminallibrary.framework.ICommandBuilder;
import com.infineon.cipurse.terminallibrary.framework.Utils;
import com.infineon.cipurse.terminallibrary.framework.Utils.eCaseType;
import com.infineon.cipurse.terminallibrary.model.KeyDiversificationInfo;
import com.infineon.cipurse.terminallibrary.pal.ICommunicationHandler;
import com.infineon.cipurse.terminallibrary.pal.ILogger;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.SMLevel;
import com.infineon.cipurse.terminallibrary.presentation.ISessionManager.SMTYPE;
import com.infineon.cipurse.terminallibrary.presentation.SessionManager;


/**
* Defines CIPURSE(TM) generic Secure Messaging Service.
*
* @since 1.0.0
* @version 1.0.1
*/
public class CipurseSMApi implements ICipurseSMApi, CipurseCommandsConstants{




	/**
	 * Flag for Secure messaging. Set this flag to 'TRUE' for sending the commands in secure mode.
	 */
	protected boolean secureMessagingFlag = false;

	/**
	 * Secure messaging indicator.
	 */
	protected byte secureMessagingIndicator=0;

	private ICommunicationHandler commsChannel;

	private ILogger logger;

	private ICommandBuilder commandBuilder;

	private ICipurseSAMCryptoApi smSAMCryptoApi;

	private SessionManager sessionManager;

	/**
	 * @param sessionManager SessionManager Handler
	 */
	public CipurseSMApi(SessionManager sessionManager){
		this.sessionManager = sessionManager;
	}


	protected void configureSMApi(ICommunicationHandler mCBPHandler,ILogger logger) throws CipurseException {
		this.commsChannel = mCBPHandler;
		this.commandBuilder = new CommandBuilder();
	}

	@Override
	public boolean setUpSecureChannel(byte keyNum, KeyDiversificationInfo keyValue) throws CipurseException {
			if(secureMessagingFlag && sessionManager.getSMIValue() != SMLevel.eSM_NO_SECMSG)
				return setUpSecureChannelWithSM(keyNum, keyValue);

			byte[] challengeCmd = commandBuilder.buildGetChallenge();
			byte[] getChallengeResponse = transmit(challengeCmd);

			byte[] mutualAuthData = smSAMCryptoApi.authenticateSAM(keyValue, (byte)0xFF, (byte)0xFF, keyNum, new ByteArray(getChallengeResponse));
			// Transmit the command

			byte[] response = transmit(commandBuilder.buildMutualAuthentication(keyNum, new ByteArray(mutualAuthData)).getBytes());

			boolean success = smSAMCryptoApi.authenticateCBP(new ByteArray(response));
			if(success)
				secureMessagingFlag=true;
			else
				secureMessagingFlag=false;
			return success;
	}

	protected boolean setUpSecureChannelWithSM(byte keyNum, KeyDiversificationInfo keyValue) throws CipurseException {

		byte smiForAuth = Utils.updateSMIforLePresence(sessionManager.getSMIValue().getSMLevel(), true);
		byte[] challengeCmd = commandBuilder.buildGetChallenge();
		byte[] getaChallengeResponse = transmit(challengeCmd, SM_OPTIONS.NO_UNWRAP_RSP);

		byte[] mutualAuthData = smSAMCryptoApi.authenticateSAM(keyValue, smiForAuth, smiForAuth, keyNum, new ByteArray(getaChallengeResponse));
		mutualAuthHeader[CipurseConstant.OFFSET_P2] = keyNum;
		byte[] mutualAuthCmd = buildMutualAuthCmd(mutualAuthHeader, smiForAuth, mutualAuthData);
		// Transmit the command
		byte[] response = transmit(mutualAuthCmd,SM_OPTIONS.NO_WRAP_AND_UNWRAP);

		return smSAMCryptoApi.authenticateCBP(new ByteArray(response));
	}


	protected byte[] buildMutualAuthCmd(byte[] mutualAuthHeader, short smi, byte[] mutualAuthCmdData) throws CipurseException
	{
		byte[] mutualAuthSMheader = Arrays.copyOf(mutualAuthHeader, mutualAuthHeader.length);
		mutualAuthSMheader[0] = 0x04;
		byte[] smCmdData=new byte[]{(byte) smi};

		int offset=0;

		switch (smi & CipurseConstant.BITMAP_SMI_COMMAND) {
		case CipurseConstant.SM_COMMAND_MACED:
			smCmdData=new byte[46 + 2];
			smCmdData[offset ++]=(byte) smi;
			offset=Utils.arrayCopy(mutualAuthCmdData, 0, smCmdData, offset, 38);
			smCmdData[offset ++]=(byte) 0x10;
			offset=Utils.arrayCopy(mutualAuthCmdData, 38, smCmdData, offset, 8);
			break;

		case CipurseConstant.SM_COMMAND_ENCED:
			smCmdData=new byte[48 + 2];
			smCmdData[offset ++]=(byte) smi;
			offset=Utils.arrayCopy(mutualAuthCmdData, 0, smCmdData, offset, 48);
			smCmdData[offset ++]=(byte) 0x10;
			break;

		case CipurseConstant.SM_COMMAND_RESPONSE_PLAIN:
			smCmdData=new byte[38 + 2];
			smCmdData[offset ++]=(byte) smi;
			offset=Utils.arrayCopy(mutualAuthCmdData, 0, smCmdData, offset, 38);
			smCmdData[offset ++]=(byte) 0x10;
			break;

		default:
			throw new CipurseException(CipurseConstant.CTL_CONDITIONS_OF_USE_NOT_SATISFIED);

		}

		byte[] smCommand=Utils.concat(mutualAuthSMheader, smCmdData);
		smCommand[APDU_OFFSET_P3]=(byte) smCmdData.length;
		return CipurseCryptoUtils.getLeDashCommand(smCommand, eCaseType.CASE_4, (byte) smi);
	}

	@Override
	public byte[] transmit(byte[] command) throws CipurseException {
		if(secureMessagingFlag)
			return transmit(command, SM_OPTIONS.WRAP_AND_UNWRAP);
		else
			return transmit(command, SM_OPTIONS.NO_WRAP_AND_UNWRAP);
	}

	@Override
    public byte[] transmit(byte[] command, SM_OPTIONS smOption) throws CipurseException {

		logger.log(ILogger.COMMAND_MESSAGE, "CBP:","",command);

		SMLevel smi = sessionManager.getSMIValue();
		if(secureMessagingFlag && smi==SMLevel.eSM_NO_SECMSG)
			resetSM();

        byte[] finalCmd=command;
        if( (smOption == SM_OPTIONS.WRAP_AND_UNWRAP || smOption == SM_OPTIONS.NO_UNWRAP_RSP)
        		&& secureMessagingFlag)
        {
        	finalCmd=CipurseCryptoUtils.wrapCommand(smSAMCryptoApi,new ByteArray(command), smi.getSMLevel());
            logger.log(ILogger.WRAPPED_COMMAND_MESSAGE, "CBP:","",finalCmd);
        }

        byte[] response = commsChannel.transReceive(finalCmd);

        byte[] finalResponse = response;
        if( (smOption == SM_OPTIONS.NO_WRAP_CMD || smOption == SM_OPTIONS.WRAP_AND_UNWRAP)
        		&& secureMessagingFlag)
        {
         	if((((smi.getSMLevel() & CipurseConstant.BITMAP_SMI_RESPONSE) == CipurseConstant.SM_COMMAND_RESPONSE_PLAIN) && (response.length > 1))
                            || (response.length > 2))
            {
          		logger.log(ILogger.WRAPPED_RESPONSE_MESSAGE,"CBP:","", response);
                finalResponse = CipurseCryptoUtils.unWrapResponse(smSAMCryptoApi,new ByteArray(response), smi.getSMLevel());
            }
        }

        logger.log( ILogger.RESPONSE_MESSAGE,"CBP:","", finalResponse);

        return finalResponse;
    }

	@Override
	public boolean resetSM() throws CipurseException {
		secureMessagingFlag = false;
		if(smSAMCryptoApi!=null)
			smSAMCryptoApi.endSession();
		return true;
	}


	@Override
	public ICipurseSMApi initializeSMAPI(
			ICipurseSAMCryptoApi cryptoApi, SMTYPE smType,
			ICommunicationHandler mCBPHandler,
			ILogger ppsLoggerHandle) throws CipurseException {

			this.smSAMCryptoApi =cryptoApi;
			this.logger = ppsLoggerHandle;
			configureSMApi(mCBPHandler,ppsLoggerHandle);
			return this;
	}


	public ICipurseSAMCryptoApi getCryptoAPI(){
		return smSAMCryptoApi;
	}

}
