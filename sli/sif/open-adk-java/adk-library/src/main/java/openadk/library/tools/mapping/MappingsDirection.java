//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

/**
 * Defines the MappingDirection flag, which indicates whether a Mappings operation
 * is being done inbound or outbound
 * 
 * @author Andrew Elmhorst
 *
 */
public enum MappingsDirection {
	
	/**
	 * 	Direction flag which indicates the 
	 * 	agent is not specifying whether the method is being called for an inbound
	 * 	or outbound operation.<p>
	 *
	 *	@see Mappings#mapInbound(com.edustructures.sifworks.SIFDataObject, FieldAdaptor)
	 *  @see Mappings#mapOutbound(FieldAdaptor,com.edustructures.sifworks.SIFDataObject)
	 *  @see MappingsContext#getDirection()
	 *
	 * 	@since ADK 1.5
	 */
	UNSPECIFIED,
	
	/**
	 * 	Direction flag passed to the <code>map</code> method to indicate the 
	 * 	agent is mapping values for an outbound message (for example, a SIF_Event 
	 * 	that is being reported to the zone or a SIF_Response being prepared.) 
	 * 	Currently, this flag is used only in conjunction with the <i>ValueSet</i> 
	 * 	attribute of the FieldMapping class. When a ValueSet is associated with 
	 * 	a FieldMapping rule and this flag is passed to the <code>map</code> 
	 * 	method, it will automatically lookup the ValueSet by ID and call 
	 * 	its <code>translate</code> function on the value produced from
	 * 	the mapping.<p>
	 *
	 *	@see Mappings#mapInbound(com.edustructures.sifworks.SIFDataObject, FieldAdaptor)
	 *  @see Mappings#mapOutbound(FieldAdaptor,com.edustructures.sifworks.SIFDataObject)
	 *  @see MappingsContext#getDirection()
	 *
	 * 	@since ADK 1.5
	 */
	OUTBOUND,
		
	/**
	 * 	Direction flag passed to the <code>map</code> method to indicate the 
	 * 	agent is mapping values for an inbound message (for example, a SIF_Event 
	 * 	that is received from the zone or a SIF_Response). Currently, this flag 
	 * 	is used only in conjunction with the <i>ValueSet</i> attribute of the 
	 * 	FieldMapping class. When a ValueSet is associated with a FieldMapping 
	 * 	rule and this flag is passed to the <code>map</code> method, it will 
	 * 	automatically lookup the ValueSet by ID and call its <code>translateReverse</code> 
	 * 	function on the value produced from the mapping.<p>
	 *
	 *	@see Mappings#mapInbound(com.edustructures.sifworks.SIFDataObject, FieldAdaptor)
	 *  @see Mappings#mapOutbound(FieldAdaptor,com.edustructures.sifworks.SIFDataObject)
	 *  @see MappingsContext#getDirection()
	 *
	 * 	@since ADK 1.5
	 */
	INBOUND
		


}
