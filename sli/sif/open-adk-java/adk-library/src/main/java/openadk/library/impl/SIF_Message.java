//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;
import openadk.library.common.*;


/**
 *  Used internally by the class framework to encapsulate a SIF message envelope.<p>
 *
 *  Agents should not construct SIF_Message objects directly.<p>
 *
 *  @author Eric Petersen
 *  @version SIF10r1
 *  @since SIF10r1
 */
public class SIF_Message extends SIFMessagePayload
{
	/**
	 *  Constructor
	 */
	public SIF_Message()
	{
		super(ADK.DTD().SIF_MESSAGE);
	}

	/**
	 *  Gets the value of the <code>Version</code> attribute.
<p>The SIF specification defines the meaning of this attribute as: "The version of SIF to which this message conforms"<p>
	 *
	 *  @return The <code>Version</code> attribute of this object.
	 *  @version 1.1
	 *  @since 1.0r1
	 */
	public String getVersion() {
		return  getFieldValue( SIFDTD.SIF_MESSAGE_VERSION );
	}

	/**
	 *  Sets the value of the <code>Version</code> attribute.
<p>The SIF specification defines the meaning of this attribute as: "The version of SIF to which this message conforms"<p>
	 *
	 *  @param value A <code>String</code> object
	 *  @version 1.1
	 *  @since 1.0r1
	 */
	public void setVersion( String value ) {
		setField( SIFDTD.SIF_MESSAGE_VERSION, value);
	}
}
