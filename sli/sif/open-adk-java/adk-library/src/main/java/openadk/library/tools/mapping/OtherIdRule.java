//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.io.*;

import openadk.library.*;
import openadk.library.common.CommonDTD;
import openadk.library.common.OtherId;
import openadk.library.common.OtherIdList;
import openadk.library.tools.xpath.SIFXPathContext;
import openadk.util.XMLUtils;

import org.apache.commons.jxpath.CompiledExpression;
import org.w3c.dom.*;

/**
 *  A Rule class to evaluate &lt;OtherId&gt; queries as defined by the OtherIdMapping class.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class OtherIdRule extends Rule implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8068524510995663496L;

	protected OtherIdMapping fMapping;

	private CompiledExpression fExpression;
	protected transient Node fNode;

    public OtherIdRule( OtherIdMapping mapping )
	{
		this( mapping, null );
    }

	public OtherIdRule( OtherIdMapping mapping, Node node )
	{
		fMapping = mapping;
		fNode = node;
	}

	/**
	 *  Produces a duplicate of this Rule object<p>
	 *
	 *  @return A "deep copy" of this Rule object
	 */
	public Rule copy( FieldMapping newParent )
		throws ADKMappingException
	{
		OtherIdRule m = new OtherIdRule( fMapping == null ? null : fMapping.copy() );
		if( fNode != null && newParent.fNode != null ) {
			m.fNode = newParent.fNode.getOwnerDocument().importNode( fNode, false );
		}
		
		return m;
	}

	/**
	 *  Evaluates this rule against a SIFDataObject and returns the text value
	 *  of the <code>&lt;OtherId&gt;</code> element that satisfied the query. If
	 *  the OtherIdMapping passed to the constructor included a <i>prefix</i>
	 *  attribute, the returned value will exclude the prefix string.
	 *
	 *  @param data The SIFDataObject the rule is evaluated against
	 *  @return The value of the <code>&lt;OtherId&gt;</code> element that
	 *      satisfied the query (excluding the prefix string if applicable), or
	 *      null if no match found
	 */
	private SIFSimpleType evaluate( SIFDataObject data, SIFVersion version )
		throws ADKSchemaException
	{
		if( data != null )
		{
			//
			//  Search all of the OtherId children for one that matches the type
			//  and optionally the prefix specified by the fMapping
			//
			OtherIdList otherIdList = (OtherIdList)data.getChild( CommonDTD.OTHERIDLIST );
			if( otherIdList != null ){
				for( OtherId otherId : otherIdList )
				{
					//  Compare the Type attribute
					String typ = otherId.getType();
					if( typ == null || !typ.equals( fMapping.getType() ) ){
						continue;
					}

					//  Optionally compare the prefix and if its a match return
					//  all text after the prefix string
					String prefix = fMapping.getPrefix();
					if( prefix != null )
					{
						String val = otherId.getValue();
						if( val != null && val.startsWith( prefix ) ) {
							return new SIFString( val.substring( prefix.length() ) );
						}
					}
					else {
						return otherId.getSIFValue();
					}
				}
			}
		}
		return null;
	}

	/**
	 *  Render this OtherIdRule as an XML DOM Node
	 */
	public void toXML( Node parent )
	{
		if( fNode == null )
		{
			Node n = parent.getOwnerDocument().createElement( "OtherId" );
			if( fMapping.getType() != null )
	    		XMLUtils.setAttribute( n, "type", fMapping.getType() );
		    if( fMapping.getPrefix() != null )
			    XMLUtils.setAttribute( n, "prefix", fMapping.getPrefix() );

		    parent.appendChild( n );
		}
		else
		{
			parent.appendChild( fNode );
		}
	}

	/**
	 *  Return the string representation of this OtherIdRule as XML text
	 */
	public String toString()
	{
		StringBuffer b = new StringBuffer();
		b.append("<OtherId type='");
		if( fMapping.getType() != null )
			b.append( fMapping.getType() );
		b.append("'");
		if( fMapping.getPrefix() != null ) {
			b.append( " prefix='" );
			b.append( fMapping.getPrefix() );
			b.append( "'");
		}

		b.append("/>");

		return b.toString();
	}

	@Override
	public SIFSimpleType evaluate(SIFXPathContext xpathContext, SIFVersion version) throws ADKSchemaException {
			
		SIFDataObject sdo = (SIFDataObject)xpathContext.getContextBean();
		return evaluate( sdo, version );
//			// TODO: Test and optimize this
//			if( fExpression == null ){
//				String expression = "substring-after( OtherIdList/OtherId[@Type='" + fMapping.getType() + "'][starts-with( ., '" + fMapping.getPrefix() +  "' )], '" + fMapping.getPrefix() + "' )";
//				fExpression = xpathContext.compile( expression );
//			}
//			
//			String value = (String)fExpression.getValue( xpathContext );
//			if( value == null ){
//				return null;
//			}
//			return new SIFString( value );
	}
}
