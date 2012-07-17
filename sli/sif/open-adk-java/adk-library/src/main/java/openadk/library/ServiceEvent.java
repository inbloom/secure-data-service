//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

public class ServiceEvent {
	
	/**
	 *  The data that has changed as described by the action
	 */
	protected DataObjectInputStream data;

	/**
	 *  Identifies the type of SIF Data Object contained in the event
	 */
	protected ElementDef objType;

	/**
	 *  The zone from which this event originated
	 */
	protected Zone zone;
	
	/**
	 * The SIF Contexts to which this event applies 
	 */
	private SIFContext[] contexts = new SIFContext[] { SIFContext.DEFAULT };
	
	/*
	 * Fields
	 */
	private String service = "";
	private String operation = "";
	private String serviceMsgId = "";
	
	/*
	 * Constructor
	 */
	public ServiceEvent () {
		
	}

	public DataObjectInputStream getData() {
		return data;
	}

	public void setData(DataObjectInputStream data) {
		this.data = data;
	}

	public ElementDef getObjType() {
		return objType;
	}

	public void setObjType(ElementDef objType) {
		this.objType = objType;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public SIFContext[] getContexts() {
		return contexts;
	}

	public void setContexts(SIFContext[] contexts) {
		this.contexts = contexts;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getServiceMsgId() {
		return serviceMsgId;
	}

	public void setServiceMsgId(String serviceMsgId) {
		this.serviceMsgId = serviceMsgId;
	}
	
	
}
