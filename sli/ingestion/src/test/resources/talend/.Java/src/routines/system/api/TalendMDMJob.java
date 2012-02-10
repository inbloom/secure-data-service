/*******************************************************************************
 *  Copyright (c) 2011 Talend Inc. - www.talend.com
 *  All rights reserved.
 *
 *  This program and the accompanying materials are made available
 *  under the terms of the Apache License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 ******************************************************************************/
package routines.system.api;


/**
 * A JOB interface for Jobs that are using tMDMTrigger Components
 */
public interface TalendMDMJob extends TalendJob {


	/**
	 * @return
	 */
	public org.dom4j.Document getMDMOutputMessage();

	/**
	 * @param message
	 */
	public void setMDMInputMessage(org.dom4j.Document message);

	/**
	 * @param message
	 * @throws DocumentException 
	 */
	public void setMDMInputMessage(String message) throws org.dom4j.DocumentException;

}
