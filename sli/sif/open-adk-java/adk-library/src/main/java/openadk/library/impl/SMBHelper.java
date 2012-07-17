//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;
import openadk.library.infra.*;

/**
 *  Methods to assist the TrackQueryResults class in invoking SMB.<p>
 *
 *  SMBHelper is used primarily to protect the integrity of the public API.
 *  TrackQueryResults is in the public com.edustructures.sifworks package, but
 *  other classes used internally by it for Selective Message Blocking (namely
 *  MessageDispatcher) are in the private .impl package where their members are
 *  protected from agents. By delegating SMB to this class, it can perform
 *  behind-the-scenes tasks in conjunction with MessageDispatcher. Doing those
 *  tasks directly from TrackQueryResults would require introducing public
 *  methods into associated classes that would expose underlying protected
 *  members.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class SMBHelper
{
	/**
	 *  The zone that originated the SIF_Event passed to TrackQueryResults
	 */
	public ZoneImpl fZone;

	/**
	 *  The Event passed to TrackQueryResults
	 */
	public Event fEvent;

	protected SIFMessageInfo fInfo;


	/**
	 *  Constructor
	 */
    public SMBHelper( TrackQueryResults tqr, Event event, MessageInfo info )
	{
		fZone = (ZoneImpl)event.getZone();
		fEvent = event;
		fInfo = (SIFMessageInfo)info;
    }

	/**
	 *  Invoke Selective Message Blocking.<p>
	 */
	public void invokeSMB()
		throws ADKException
	{
		fZone.log.debug( "Invoking SMB on event " + fInfo.getMsgId() );

		//  Inform the EvDisp thread associated with this Event that it should
		//  return an Intermediate SIF_Ack to the ZIS, which will invoke SMB.
		MessageDispatcher disp = fZone.getFDispatcher();
		MessageDispatcher.EvDisp evDisp = (MessageDispatcher.EvDisp)disp.fEvDispCache.get(fEvent);
		if( evDisp == null ) {
			ADKUtils._throw( new InternalError("Internal state error: No EvDisp thread for Event, cannot invoke SMB"),fZone.log );
		}

		evDisp.notifyAckCode(2);

		evDisp._smb = this;
	}

	/**
	 *  End Selective Message Blocking.<p>
	 */
	public void endSMB()
		throws ADKException
	{
		fZone.log.debug( "Ending SMB on event " + fInfo.getMsgId() );

		//  Send final SIF_Ack for the event...
		SIF_Ack ack = new SIF_Ack( fZone.getHighestEffectiveZISVersion() );
		ack.setSIF_OriginalMsgId( fInfo.getMsgId() );
		ack.setSIF_OriginalSourceId( fInfo.getSourceId() );
		ack.setSIF_Status( new SIF_Status( SIFStatusCodes.FINAL_ACK_3 ) );

		ack.setSIFVersion( fInfo.getSIFVersion() );
		fZone.getFDispatcher().send( ack );
	}
}
