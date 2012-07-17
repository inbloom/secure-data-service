//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services.impl;

import openadk.library.*;
import openadk.library.services.ServiceOutputInfo;


/**
 * @author Andrew Elmhorst
 * @version ADK 2.3
 */
public class ServiceOutputInfoImpl implements ServiceOutputInfo {

	private int fMaxBufferSize;
	private String fSIFRequestMsgId;
	private String fSIFRequestSourceId;
	private SIFVersion fSIFVersion;
	private String fService;
	private String fOperation;

	ServiceOutputInfoImpl( String service, String operation , MessageInfo mi) {
		fService = service;
		if (mi instanceof SIFMessageInfo) {
			fOperation = (String) ((SIFMessageInfo)mi).getObjects().get("SIF_Operation"); // JEN todo BIG HACK
			fSIFRequestMsgId =  (String) ((SIFMessageInfo)mi).getObjects().get("SIF_ServiceMsgId");
		}
		SIFMessageInfo smi = (SIFMessageInfo) mi;
		fMaxBufferSize = smi.getMaxBufferSize();
		fSIFRequestSourceId = smi.getSourceId();
		fSIFVersion = smi.getLatestSIFRequestVersion();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.services.DeferredResponseInfo#getSIFMaxBufferSize()
	 */
	public int getSIFMaxBufferSize() {
		return fMaxBufferSize;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.services.DeferredResponseInfo#getSIFRequestMsgId()
	 */
	public String getSIFRequestMsgId() {
		return fSIFRequestMsgId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.services.DeferredResponseInfo#getSIFRequestSourceId()
	 */
	public String getSIFRequestSourceId() {
		return fSIFRequestSourceId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.services.DeferredResponseInfo#getSIFVersion()
	 */
	public SIFVersion getSIFVersion() {
		return fSIFVersion;
	}

	public String getOperation() {
		return fOperation;
	}

	public String getService() {
		return fService;
	}


}
