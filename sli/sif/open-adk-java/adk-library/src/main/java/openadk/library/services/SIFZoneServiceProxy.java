//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.DataObjectInputStream;
import openadk.library.ElementDef;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.Provisioner;
import openadk.library.Query;
import openadk.library.QueryResults;
import openadk.library.SIFElement;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;
import openadk.library.SIFMessageInfo;
import openadk.library.SIFWriter;
import openadk.library.Subscriber;
import openadk.library.Zone;
import openadk.library.infra.SIF_Element;
import openadk.library.infra.SIF_Error;
import openadk.library.services.impl.ServiceObjectInputStreamImpl;
import openadk.util.ADKStringUtils;


/**
 * Abstract Proxy class to a zone service.
 *
 * @author Andrew Elmhorst
 * @version ADK 2.3
 *
 */
public abstract class SIFZoneServiceProxy implements QueryResults, Subscriber {

	private ElementDef fServiceDef;

	/**
	 * Creates in instance of SIFZoneServiceProxy
	 *
	 * @param ElementDef
	 */
	protected SIFZoneServiceProxy(ElementDef serviceDef) {
		fServiceDef = serviceDef;
	}

	/**
	 * Gets the service definition
	 *
	 * @return ElementDef
	 */
	public ElementDef getServiceDefinition() {
		return fServiceDef;
	}

	/**
	 * Provision the service
	 * @param Provisioner
	 */
	public void provision(Provisioner provisioningSource) throws ADKException {
		provisioningSource.setSubscriber(this, fServiceDef, null);
		provisioningSource.setQueryResults(this, fServiceDef, null);
	}

	/**
	 * Invoke the service 
	 * 
	 * @param Zone
	 * @param ServiceRequestInfo
	 * @param SIFElement payload
	 * 
	 * @return SIF_MsgID
	 */
	protected String invokeService(Zone zone, ServiceRequestInfo requestInfo,
			SIFElement payload) throws ADKException {

		// Assign a SIF_ServiceMsgId value
		requestInfo.setSIFServiceMsgId(ADK.makeGUID());
		
		// JEN - use query if use old message types
		// Serialize the request object
/*		Query query = new Query(fServiceDef);
		StringWriter sw = new StringWriter();
		SIFWriter writer = new SIFWriter(sw);
		writer.suppressNamespace(true);
		writer.write(payload);
		writer.flush();
		writer.close();
		String xml = sw.toString();
		query.addCondition("Method", "EQ", requestInfo.getMethodName());
		query.addCondition("Payload", "EQ", ADKStringUtils.encodeXML(xml));
		query.addCondition("ServiceMsgId", "EQ", requestInfo.getSIFServiceMsgId());
*/		

		// Note: When SIF Zone Service messages are used, the method name
		// and service name are header elements. However, we need to track that
		// information locally
//		query.setUserData(requestInfo);


		// JEN added String return to match ETF
		// Support for directed service requests
		String msgId = null;
		if( requestInfo.getDestinationId() != null ){
			msgId=zone.invokeService(zone, requestInfo, payload);
		} 
		
		//	Set SIF_MsgId so the caller doesn't need to worry about it
		requestInfo.setSIFMsgId(msgId);
		return msgId;
	}

	public void onQueryPending(MessageInfo info, Zone zone) throws ADKException {
		// Do nothing for now...

	}

	public void onQueryResults(DataObjectInputStream data, SIF_Error error,
			Zone zone, MessageInfo info) throws ADKException {

		SIFMessageInfo smi = (SIFMessageInfo) info;
/*		ServiceRequestInfo sri = (ServiceRequestInfo) smi.getSIFRequestInfo()
				.getUserData();
		if (sri == null) {
			// TODO: In "Real" SIF Zone Services, the service and method names
			// are always going to be defined
			// in the message header. For this implementation, just skip it if
			// we can't derive the method
			// name from our saved state
			System.out
					.println("No saved state for SIF_Response with RequestMsgId of: "
							+ smi.getSIFRequestMsgId());
			return;
		}
		
		smi.setSIFServiceMsgId(sri.getSIFServiceMsgId());
*/		
		String methodName = (String) smi.getObjects().get("SIF_Operation");
		Method proxyMethod = ServiceUtils.getMethod(this, "on"
				+ methodName + "Response", zone);

		// TODO: Service methods can return either arrays, or a single object.
		// We need to tailor
		// this method to use a ServiceObjectInputStream instance for arrays and
		// to read just the single
		// object in other cases. Perhaps it could derive which method to use
		// from reflection data on the
		// method it finds in the client.
		ServiceObjectInputStream<SIFElement> sois;
		if (error != null) {
			sois = new ServiceObjectInputStreamImpl<SIFElement>(zone, info,
					error);
		} else {
			sois = new ServiceObjectInputStreamImpl<SIFElement>(zone, info,
					data);
		}

		try {
			proxyMethod.invoke(this, new Object[] { sois, error, zone, info });
		} catch (IllegalAccessException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		} catch (IllegalArgumentException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		} catch (InvocationTargetException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		}
	}

	public void onServiceEvent(DataObjectInputStream data, SIF_Error error,
			Zone zone, MessageInfo info) throws ADKException {

		SIFMessageInfo smi = (SIFMessageInfo) info;
		String methodName = (String) smi.getObjects().get("SIF_Operation");
		Method proxyMethod = ServiceUtils.getMethod(this, "on"
				+ methodName , zone);

		// TODO: Service methods can return either arrays, or a single object.
		// We need to tailor
		// this method to use a ServiceObjectInputStream instance for arrays and
		// to read just the single
		// object in other cases. Perhaps it could derive which method to use
		// from reflection data on the
		// method it finds in the client.
		ServiceObjectInputStream<SIFElement> sois;
		if (error != null) {
			sois = new ServiceObjectInputStreamImpl<SIFElement>(zone, info,
					error);
		} else {
			sois = new ServiceObjectInputStreamImpl<SIFElement>(zone, info,
					data);
		}

		try {
			proxyMethod.invoke(this, new Object[] { sois, zone, info });
		} 
		catch (IllegalAccessException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		} 
		catch (IllegalArgumentException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		} 
		catch (InvocationTargetException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		}
	}
	
	public void onEvent(Event event, Zone zone, MessageInfo info)
			throws ADKException {
		SIFElement serviceElement = event.getData().readDataObject();
		if (serviceElement == null) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new SIFException(SIFErrorCategory.GENERIC,
					SIFErrorCodes.GENERIC_GENERIC_ERROR_1,
					"The Event payload element cannot be empty.", zone);
		}
		List<SIFElement> children = serviceElement.getChildList();
		if (children.size() == 0) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new SIFException(SIFErrorCategory.GENERIC,
					SIFErrorCodes.GENERIC_GENERIC_ERROR_1, "The <"
							+ serviceElement.getElementDef().name()
							+ "> element cannot be empty.", zone);
		}
		SIFElement eventElement = children.get(0);
		String methodName = "on" + eventElement.getElementDef().name();
		Method proxyMethod = ServiceUtils.getMethod(this, methodName, zone);
		SIFElement eventPayload = eventElement.getChildList().get(0);
		try {
			proxyMethod.invoke(this, new Object[] { eventPayload, zone, info });
		} catch (IllegalAccessException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		} catch (IllegalArgumentException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		} catch (InvocationTargetException iae) {
			// TODO: Fix up error handling when SIF Services error codes are
			// defined
			throw new ADKException("Unable to process event: " + iae, zone, iae);
		}

	}

}
