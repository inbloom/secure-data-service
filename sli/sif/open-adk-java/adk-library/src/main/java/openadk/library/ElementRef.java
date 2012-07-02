//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.IOException;
import java.io.Serializable;

import openadk.library.impl.ElementDefImpl;


/**
 * Represents a relative reference to a SIF Element
 * @author Andrew Elmhorst
 * @version 2.1
 *
 */
public class ElementRef implements Serializable 
{
	/** The field being referenced, or null if it cannot be parsed **/
	private transient ElementDef fField;
	
	/** The XPath  */
	private String fXPath;
	
	
	/**
	 * @param root
	 * @param xPath
	 * @param version
	 */
	public ElementRef( ElementDef root,  String xPath, SIFVersion version )
	{
		fField = ADK.DTD().lookupElementDefBySQP( root, xPath );
		fXPath = xPath;
	}
	
	/**
	 * @param root
	 * @param xPath
	 * @param version
	 */
	public ElementRef( ElementDef root,  ElementDef referencedField, SIFVersion version )
	{
		fField = referencedField;
		fXPath = referencedField.getSQPPath( version );
	}


	/**
	 * Returns the referenced field or null if it cannot be resolved
	 * @return The referenced field or null if it cannot be resolved
	 */
	public ElementDef getField() {
		return fField;
	}
	
	/**
	 * @return the XPath representation of this ElementRef
	 */
	public String getXPath(){
		return fXPath;
	}

	private void writeObject( java.io.ObjectOutputStream out )
		throws IOException
	{
		out.defaultWriteObject();

		ElementDefImpl.writeObject(fField,out);
	}

	private void readObject( java.io.ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		fField=ElementDefImpl.readObject(in);
	}
}
