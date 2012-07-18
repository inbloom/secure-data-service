//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.policy;

import openadk.library.Agent;
import openadk.library.Zone;
import openadk.library.tools.cfg.AgentConfig;
import openadk.util.XMLUtils;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.Element;


/**
 * Represents the default policy engine used by the ADK. Policy is controlled
 * by entries in the agent's XML configuration file or default policy.<p>
 * 
 * <p>Policy entries can be set at the Agent, Template, or Zone level in the agent's
 * configuration file. If the {@link AgentConfig} class is not used to configure the
 * agent, this class provides default policies prescribed by the ADK.</p> 
 * 
 * <p>Here is an example of setting request policy using the agent configuration
 * file. The <code>&lt;Policy&gt;</code> element can be placed using a zone, template
 * or agent node in the config file.
 * <pre>
 * &lt;policy&gt;
 * &lt;!-- Set the policies for requesting data--&gt;
 * &lt;requestpolicy&gt;
 * 	&lt;object name="*"&gt;
 * 	  &lt;!-- For all objects, request data using SIF 1.5r1--&gt;
 * 	  &lt;version&gt;1.5r1&lt;/version&gt;
 * 	&lt;/object&gt;
 * 	&lt;object name="Assessment"&gt;
 * 	  &lt;!-- Override the default policy for the Assessment object and request it
 * 		using 2.* and using a specific source id --&gt;
 * 	  &lt;version&gt;2.*&lt;/version&gt;
 * 	  &lt;sourceId&gt;AssessmentAgent&lt;/sourceId&gt;
 * 	&lt;/object&gt;
 *   &lt;/requestpolicy&gt;
 * &lt;/policy&gt;
 * </pre>
 * </p>
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.1
 */
public class ADKDefaultPolicy extends PolicyFactory {
	
	private AgentConfig fConfig;
	public ADKDefaultPolicy( Agent agent ){
		if( agent != null ){
			Object source = agent.getConfigurationSource();
			if( source != null && source instanceof AgentConfig ){
				fConfig = (AgentConfig)source;
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.policy.PolicyFactory#getRequestPolicy(com.edustructures.sifworks.Zone, com.edustructures.sifworks.ElementDef)
	 */
	@Override
	public ObjectRequestPolicy getRequestPolicy( Zone zone, String objectName) {
		
		Element policyElement = getPolicyElement( zone.getZoneId(), "requestPolicy", objectName );
		if( policyElement != null ){
			ObjectRequestPolicy policy = new ObjectRequestPolicy( objectName );
			policy.setRequestSourceId(  XMLUtils.getElementTextValue( policyElement, "sourceId" ) );
			policy.setRequestVersions(  XMLUtils.getElementTextValue( policyElement, "version" ) );
			return policy;
		}
		
		return null;
	}
	
	
	/**
	 * Look for the specified policy element, using an XPath traversal of zone, template, and agent nodes
	 * @param zoneId The zoneId to retrieve policy for
	 * @param policySubPath The path of the policy to retrieve, such as "requestPolicy" or "eventPolicy/transmitPolicy"
	 * @param objectName The object type to retrieve policy for
	 * @return
	 */
	private Element getPolicyElement( String zoneId, String policySubPath, String objectName ){
		if( fConfig != null ){
			JXPathContext xpathContext = JXPathContext.newContext( fConfig.getDocument().getDocumentElement() );
			xpathContext.setLenient( true );
			
			// Look first, for policy located under the particular zone node
			Pointer currentPointer = xpathContext.getPointer( "zone[@id='" + zoneId + "']" );
			if( currentPointer != null && currentPointer.getNode() != null ){
				Element policyNode = findPolicy(xpathContext, currentPointer, policySubPath, objectName );
				if( policyNode != null ){
					return policyNode;
				}
				// No policy for this object found at the zone level, search for policy under the
				// template
				Element zoneNode = (Element)currentPointer.getNode();
				String templateId = zoneNode.getAttribute( "template" );
				if( templateId != null ){
					currentPointer = xpathContext.getPointer( "template[@id='" + templateId  + "']" );
					policyNode = findPolicy(xpathContext, currentPointer, policySubPath, objectName);
					if( policyNode != null ){
						return policyNode;
					}
				}
			}
						
			// Finally, look at the root agent element
			currentPointer = xpathContext.getContextPointer();
			return findPolicy(xpathContext, currentPointer, policySubPath, objectName );
			
		}
		return null;
	}


	/**
	 * Looks for a policy node at the specified contextual location for the specified object.
	 * 
	 * <p>If a policy node that contains the object name is not found, a search is also done
	 * for the default policy for all objects, with a special name ('*').
	 * @param xpathContext The XPath context around the agent configuration XML document
	 * @param currentPointer A pointer to a particular node in the document to start searching from,
	 * such as a <code>&lt;zone&gt;</code>, <code>&lt;template&gt;</code>, or <code>&lt;agent&gt;</code>  node.
	 * @param policyPath The subPath, such as "requestPolicy"
	 * @param objectName
	 * @return
	 */
	private Element findPolicy(JXPathContext xpathContext, Pointer currentPointer, String policyPath, String objectName) {
		
		if( currentPointer != null && currentPointer.getNode() != null ){
			String objectPolicyPath="policy/" + policyPath +  "/object[contains(@name,'" + objectName + "')]";
			JXPathContext zoneContext = xpathContext.getRelativeContext( currentPointer );
			Element zonePolicy = (Element)zoneContext.selectSingleNode( objectPolicyPath );
			if( zonePolicy != null ){
				return zonePolicy;
			}
			
			// Look for default policy for all objects at the zone level
			zonePolicy = (Element)zoneContext.selectSingleNode( "policy/" + policyPath +  "/object[@name='*']" );
			if( zonePolicy != null ){
				return zonePolicy;
			}
		}
		return null;
	}
	
	

}
