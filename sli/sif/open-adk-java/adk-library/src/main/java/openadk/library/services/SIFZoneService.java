//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services;

import java.io.IOException;
import java.lang.reflect.Method;

import openadk.library.ADKException;
import openadk.library.Condition;
import openadk.library.ConditionGroup;
import openadk.library.DataObjectOutputStream;
import openadk.library.ElementDef;
import openadk.library.MessageInfo;
import openadk.library.Provisioner;
import openadk.library.Publisher;
import openadk.library.Query;
import openadk.library.SIFElement;
import openadk.library.SIFMessageInfo;
import openadk.library.SIFParser;
import openadk.library.Zone;
import openadk.library.impl.DataObjectOutputStreamImpl;
import openadk.library.infra.SIF_Body;
import openadk.library.infra.SIF_ServiceInput;
import openadk.library.services.impl.ServiceObjectOutputStreamImpl;
import openadk.library.services.impl.ServiceOutputFileStream;
import openadk.util.ADKStringUtils;


/**
 * Publishs SIF Services 
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.3
 */
public class SIFZoneService implements Publisher {

	private ElementDef fServiceDef;

	/**
	 * Creates in instance of a SIFZoneService
	 *
	 */
	protected SIFZoneService(ElementDef serviceDef) {
		fServiceDef = serviceDef;
	}

	/**
	 * Gets the Service Definition
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
		provisioningSource.setPublisher(this, fServiceDef, null);
	}

	public void onRequest(ServiceOutputFileStream out, SIF_ServiceInput query, Zone zone,
			MessageInfo info) throws ADKException {

				String methodName = query.getSIF_Operation();
				SIF_Body body = query.getSIF_Body();
//				String requestPayload = body.toString();
				String requestPayload = "";
//				requestPayload = ADKStringUtils.unencodeXML(requestPayload);
				String serviceMsgId = query.getMsgId();
				((SIFMessageInfo)info).setSIFServiceMsgId(serviceMsgId);
				try {
//					SIFElement payload = SIFParser.newInstance().parse(
//							requestPayload, zone);
					
					SIFElement payload = null;
					for (SIFElement element : body.getChildList()) {
						payload = element;
						break;
					}
					
					Method proxyMethod = ServiceUtils.getMethod(this, "on"
							+ methodName, zone);
					try {
						ServiceObjectOutputStreamImpl serviceOutput = new ServiceObjectOutputStreamImpl(
								 out, fServiceDef, methodName );
						proxyMethod.invoke(this, new Object[] { payload,
								serviceOutput, zone, info });
					} catch (Exception iae) {
						// TODO: Fix up error handling when SIF Services error
						// codes are defined
						throw new ADKException("Unable to process event: "
								+ iae, zone, iae);
					}
					return;

				} 
				catch (Exception ioe) {
					throw new ADKException(ioe.getMessage(), zone, ioe);
				}

	}

	public void onRequest(DataObjectOutputStream out, Query query, Zone zone,
			MessageInfo info) throws ADKException {

		ConditionGroup[] conditionGroups = query.getConditions();
		// Deserialize the request object
		if (conditionGroups != null && conditionGroups.length == 1) {
			Condition[] conditions = conditionGroups[0].getConditions();
			if (conditions != null && conditions.length == 3) {
				String methodName = conditions[0].getValue();
				String requestPayload = conditions[1].getValue();
				requestPayload = ADKStringUtils.unencodeXML(requestPayload);
				String serviceMsgId = conditions[2].getValue();
				((SIFMessageInfo)info).setSIFServiceMsgId(serviceMsgId);
				try {
					SIFElement payload = SIFParser.newInstance().parse(
							requestPayload, zone);
					Method proxyMethod = ServiceUtils.getMethod(this, "on"
							+ methodName, zone);
					try {
						ServiceObjectOutputStreamImpl serviceOutput = new ServiceObjectOutputStreamImpl(
								(DataObjectOutputStreamImpl) out, fServiceDef, methodName );
						proxyMethod.invoke(this, new Object[] { payload,
								serviceOutput, zone, info });
					} catch (Exception iae) {
						// TODO: Fix up error handling when SIF Services error
						// codes are defined
						throw new ADKException("Unable to process event: "
								+ iae, zone, iae);
					}
					return;

				} catch (IOException ioe) {
					throw new ADKException(ioe.getMessage(), zone, ioe);
				}

			}

		}
		throw new ADKException("Suitable ServiceRequest parameter not found",
				zone);

	}
}
