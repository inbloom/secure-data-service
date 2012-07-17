//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.policy;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.Agent;
import openadk.library.SIFDTD;
import openadk.library.SIFMessagePayload;
import openadk.library.SIFVersion;
import openadk.library.Zone;
import openadk.library.impl.ObjectFactory;
import openadk.library.impl.ObjectFactory.ADKFactoryType;
import openadk.library.infra.InfraDTD;
import openadk.library.infra.SIF_Query;
import openadk.library.infra.SIF_Request;
import openadk.library.infra.SIF_Version;


/**
 * @author Andy
 *
 */
public class PolicyManagerImpl extends PolicyManager {

	private final PolicyFactory fPolicyFactory;
	
	public PolicyManagerImpl( Agent agentInstance )
		throws ADKException
	{
		fPolicyFactory = (PolicyFactory)ObjectFactory.getInstance().createInstance( ADKFactoryType.POLICY_FACTORY, agentInstance );
	}
	
	
	
	@Override
	public void applyOutboundPolicy(SIFMessagePayload msg, Zone zone ) throws ADKException {
		
		byte pload = ADK.DTD().getElementType(msg.getElementDef().name());
		switch( pload ){
		case SIFDTD.MSGTYP_REQUEST:
			setRequestPolicy((SIF_Request)msg, zone );
		}
		
	}
	
	
	private void setRequestPolicy( SIF_Request request, Zone zone )
	{
		SIF_Query query = request.getSIF_Query();
		if( query == null ) {
			// SIF_ExtendedQuery and SIF_Example are not supported by ADK Policy yet
			return;
		}
		
		//
		// Object Request Policy
		//
		// Determine if there is policy in effect for this Query
		//
		String objectName = query.getSIF_QueryObject().getObjectName();
		ObjectRequestPolicy requestPolicy = fPolicyFactory.getRequestPolicy( zone, objectName );
		if( requestPolicy != null ){
			
			//
			// SIF_Request/SIF_Version policy
			//
			String requestVersions = requestPolicy.getRequestVersions();
			if( requestVersions != null ){
				if( (ADK.debug & ADK.DBG_POLICY) > 0 ){
					zone.getLog().info( "POLICY: Setting SIF_Request/SIF_Version to " + requestVersions );
				}
				// Clear the list of SIF Versions
				for( SIF_Version existingVersion : request.getSIF_Versions() ){
					request.removeChild( existingVersion );
				}
				
				// The version will be a comma-delimited list. Set each of these
				// as SIF_Version elements, but also try to derive the most logical
				// version element to set the SIF Message/@Version attribute to
				// NOTE: Someone could theoretically set versions incorrectly, such
				// as "1.1,1.5r1". Multiple SIF_Version elements are not supported in
				// SIF 1.x, but we won't bother with validating incorrect settings. Policy
				// is power in the configurator's hands to use or abuse.

				String[] versions = requestVersions.split( "," );
				String lowestVersion = versions[0];
				for( String version : versions ){
					version = version.trim();
					request.addSIF_Version( new SIF_Version( version ) );
					if( lowestVersion.compareTo( version ) > 0 ){
						lowestVersion = version;
					}
				}
				
				// Determine how the SIF_Message/@Version should be set to
				//  * If the policy is set to a single version, use it 
				//  * If a list, use the lowest
				//  * If *, ignore
				//  * if [major].*, use the lowest version supported
				if( lowestVersion.length() > 0  ){
					SIFVersion newMsgVersion = null;
					if( lowestVersion.endsWith( "*" ) ){
						try
						{
							// 2.*, requests go out with a message version of 2.0r1
							int major = Integer.parseInt( lowestVersion.substring( 0, 1 ) );
							newMsgVersion = SIFVersion.getEarliest( major );
							
						} catch( IllegalArgumentException iae ){
							zone.getLog().warn( 
									"POLICY: Error parsing ObjectRequestPolicy version '" + 
									requestVersions + "' : " + 
									iae.getMessage(), iae );
						}
						
					} else {
						try
						{
							newMsgVersion = SIFVersion.parse( lowestVersion );
						} catch( IllegalArgumentException iae ){
							zone.getLog().warn( 
									"POLICY: Error parsing ObjectRequestPolicy version '" + 
									requestVersions + "' : " + 
									iae.getMessage(), iae );
						}
					}
					if( newMsgVersion != null ){
						if( (ADK.debug & ADK.DBG_POLICY) > 0 ){
							zone.getLog().info( "POLICY: Setting SIF_Messaage/@Version to " + newMsgVersion );
						}
						request.setSIFVersion( newMsgVersion );
					}
				}
			}
			
			//
			// SIF_DestinationID policy
			//
			String requestSourceId = requestPolicy.getRequestSourceId() ;
			if( requestSourceId != null ){
				if( (ADK.debug & ADK.DBG_POLICY) > 0 ){
					zone.getLog().info( "POLICY: Setting SIF_Request SIF_DestinationID to " + requestPolicy.getRequestSourceId() );
				}
				request.getSIF_Header().setSIF_DestinationId( requestSourceId );
			}
		}
	}


	
}
