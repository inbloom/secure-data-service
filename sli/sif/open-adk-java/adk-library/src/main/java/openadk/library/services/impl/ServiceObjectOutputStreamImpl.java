//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import openadk.library.*;
import openadk.library.common.YesNo;
import openadk.library.impl.DataObjectOutputStreamImpl;
import openadk.library.infra.SIF_Error;
import openadk.library.services.ServiceObjectOutputStream;
import openadk.library.services.ServiceOutputInfo;


/**
 * Implementation of the information needed to open a SIFServiceResponseSender class to
 * send a deferred SIF Zone Service method response.
 * 
 * @author Andy
 *
 */
public class ServiceObjectOutputStreamImpl implements ServiceObjectOutputStream {

	private  ServiceOutputFileStream serviceOutputFileStream = null;
	private  DataObjectOutputStreamImpl fOut = null;
	private final ElementDef fServiceDef;
	private Class fServiceWrapperElementClass;
	private String fMethodName;

	public ServiceObjectOutputStreamImpl(ServiceOutputFileStream output,
			ElementDef serviceDef, String methodName ) throws ADKException {
		serviceOutputFileStream = output;
		try {
			fServiceWrapperElementClass = Class.forName(serviceDef
					.getFQClassName());
		} catch (ClassNotFoundException e) {
			throw new ADKException(
					"Unable to load class to represent elementDef: '"
							+ serviceDef.getFQClassName() + "'", null);
		}
		fServiceDef = serviceDef;
	}
	
	public ServiceObjectOutputStreamImpl(DataObjectOutputStreamImpl output,
			ElementDef serviceDef, String methodName ) throws ADKException {
		fOut = output;
		try {
			fServiceWrapperElementClass = Class.forName(serviceDef
					.getFQClassName());
		} catch (ClassNotFoundException e) {
			throw new ADKException(
					"Unable to load class to represent elementDef: '"
							+ serviceDef.getFQClassName() + "'", null);
		}
		fServiceDef = serviceDef;
	}

	public ServiceOutputInfo deferResponse(MessageInfo mi) throws ADKException {
		if (fOut != null)
			fOut.deferResponse();
		else if (serviceOutputFileStream != null)
			serviceOutputFileStream.deferResponse();
		
		return new ServiceOutputInfoImpl( fServiceDef.name(), fMethodName,  mi);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.services.ServiceObjectOutputStream#write(com.edustructures.sifworks.SIFElement)
	 */
	public void write(SIFElement data) throws ADKException {

		try {
			SIFDataObject serviceObject = (SIFDataObject) fServiceWrapperElementClass
					.newInstance();
/*			serviceObject.addChild(data);
			if (fOut != null)
				fOut.write(serviceObject);
			else 
*/				
			if (serviceOutputFileStream != null) 
				serviceOutputFileStream.write(data);
			
			// Remove the object from the wrapper element (required if the
			// object is going
			// to be published again using a different 'Parent' )
//			serviceObject.removeChild(data);
		} catch (Exception iae) {
			throw new ADKException(iae.getMessage(), null, iae);
		}

	}

	public void close() throws IOException {
		if (fOut != null)
			fOut.close();
		else if (serviceOutputFileStream != null)
			serviceOutputFileStream.close();
	}

	public void commit() throws ADKException {
		if (fOut != null)
			fOut.commit();
		else if (serviceOutputFileStream != null)
			serviceOutputFileStream.commit();
	}

	public void setError(SIF_Error error) throws ADKException {
		if (fOut != null)
			fOut.setError(error);
		else if (serviceOutputFileStream != null)
			serviceOutputFileStream.setError(error);
	}

	public YesNo getSIF_MorePackets() {
		YesNo retval = null;
		if (fOut != null)
			retval = fOut.getSIF_MorePackets();
		else if (serviceOutputFileStream != null)
			retval = serviceOutputFileStream.getSIF_MorePackets();
		return retval;
	}

	public int getSIF_PacketNumber() {
		int retval = 0;
		if (fOut != null)
			retval = fOut.getSIF_PacketNumber();
		else if (serviceOutputFileStream != null)
			retval = serviceOutputFileStream.getSIF_PacketNumber();

		return retval;
	}

	public void setSIF_MorePackets(YesNo morePacketsValue) {
		fOut.setSIF_MorePackets(morePacketsValue);
	}

	public void setSIF_PacketNumber(int packetNumber) {
		if (fOut != null)
			fOut.setSIF_PacketNumber(packetNumber);
		else if (serviceOutputFileStream != null)
			serviceOutputFileStream.setSIF_PacketNumber(packetNumber);
	}
	
	public void writeBuffer(ByteArrayOutputStream buffer) throws IOException {
		serviceOutputFileStream.writeBuffer(buffer);
	}

}
