//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import openadk.library.*;
import openadk.library.tools.xpath.SIFXPathContext;
import openadk.util.XMLUtils;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 *  A Rule class to evaluate XPath-like queries as defined by the
 *  <code>SIFDTD.lookupByXPath</code> method
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class XPathRule extends Rule
{
	private String fDef = null;
	private ElementDef fTargetDef;
	
	/**
	 * For outbound mappings that contain an expression containing the '=' sign,
	 * this field contains the index of that sign for easy parsing
	 */
	private int fValueIndex = -1;
	private CompiledExpression fExpression;
	

	/**
	 *  Constructor
	 *  @param definition An XPath-like query (e.g. "@RefId",
	 *      "StudentAddress/Address[@Type='H','M']/Street/Line1") to evaluate
	 *      against the SIFDataObject passed to the <code>evaluate</code> method
	 */
    public XPathRule( String definition )
	{
		fDef = definition;
    }

	/**
	 *  Produces a duplicate of this Rule object<p>
	 * @param newParent The FieldMapping to copy this rule to
	 *
	 *  @return A "deep copy" of this Rule object
	 */
	public Rule copy( FieldMapping newParent )
	{
		XPathRule clone = new XPathRule( fDef );
		if( newParent.fNode != null ) {
			XMLUtils.setText( newParent.fNode, fDef );
		}
			
		return clone;
	}


	/**
	 *  Render this XPathRule as an XML DOM Node
	 * @param parent The XML node to write to
	 */
	public void toXML( org.w3c.dom.Node parent )
	{
		if( parent.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE ){
			XMLUtils.setText( parent, fDef );
		}
		else if( parent.getNodeType() == org.w3c.dom.Node.TEXT_NODE ) {
			parent.setNodeValue( fDef );
		}
	}

	/**
	 *  Render this Rule as an XML element
	 * @return The XPath expression this rule represents
	 */
	public String toString()
	{
		return fDef;
	}
	
	/**
	 * @return The XPath expression this rule represents
	 */
	public String getXPath()
	{
		return fDef;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getValueExpression()
	{
		if( fValueIndex == -1 ){
			return null;
		}
		return fDef.substring( fValueIndex );
	}
	
	public synchronized String getPathExpression()
	{
		if( fExpression == null ){
			compile();
		}
		if( fValueIndex == -1 ){
			return fDef;
		}
		return fDef.substring( 0, fValueIndex - 1 );
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.tools.mapping.Rule#evaluate(com.edustructures.sifworks.tools.xpath.SIFXPathContext, com.edustructures.sifworks.SIFVersion)
	 */
	@Override
	public synchronized SIFSimpleType evaluate(SIFXPathContext xpathContext, SIFVersion version) 
		throws ADKSchemaException 
	{
		SIFSimpleType retval = null;
		// TODO: This could be done in the constructor, but the ADK outbound mapping
		// syntax sometimes cannot be compiled because it uses proprietary syntax
		// Therefore, compile the expression the first time it is used for a mapping
		if( fExpression == null ) {
			compile();
		}
		
		Object value = fExpression.getValue( xpathContext );
		if( value == null ){
			return null;
		} else if( value instanceof Element )
		{
			return ((Element)value).getSIFValue();
		} else {
			return new SIFString(value.toString() );
		}
	}

	/**
	 * Builds out the path specified by this XPath rule and returns the final Element in the path
	 * @param context
	 * @param version
	 * @return The newly-created element. If the Element already exists, however, the return value will
	 * be null
	 */
	public synchronized NodePointer createPath( SIFXPathContext context, SIFVersion version )
	{
		if( fExpression == null ){
			compile();
		}
		String expressionAsString =fExpression.toString();
		// modified to support StudentAddressListSurrogate (next 4 lines)
		NodePointer np = (NodePointer) fExpression.getPointer(context, expressionAsString);
		// This is a bit of a hack due to unfamiliarity with the API. There may be a more appropriate way
		// to determine that the given expression needs the opportunity to create multiple matching tags.
		// but for now, we're just checking if adk:x is in there, and then another method down the chain
		// does the real evaluation.
		if (np != null && np.getValue() != null && !expressionAsString.contains("and adk:x()]")) {
		    return np;
		}
		return (NodePointer)fExpression.createPath( context );
	}
	

	private void compile() {
		// If there is a value assignment in the rule, chop it off
		String sqp = fDef;
		int lastEqualsSign = fDef.lastIndexOf( "=" );
		if( lastEqualsSign > -1 ){
			int lastBracket = fDef.lastIndexOf( "]" );
			if( lastBracket < lastEqualsSign ){
				sqp = fDef.substring( 0, lastEqualsSign );
				fValueIndex = lastEqualsSign + 1;
			}
		}
		String jXPath = SIFXPathContext.convertLegacyXPath( sqp );
		fExpression = SIFXPathContext.compile( jXPath );
	}

	/**
	 * Looks up the ElementDef that this XPathRule points to by XPath
	 * @param parent The parent object metadata object, representing the root of the path
	 * @return The ElementDef that this XPathRule points to
	 */
	public ElementDef lookupTargetDef( ElementDef parent ) {
		if( fTargetDef == null ){
			fTargetDef = ADK.DTD().lookupElementDefBySQP( parent, getPathExpression() ); 
		}
		 return fTargetDef;
	}
}
