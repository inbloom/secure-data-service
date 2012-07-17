//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import openadk.library.ElementDef;
import openadk.library.SIFElement;
import openadk.library.SIFVersion;
import openadk.library.ValueBuilder;
import openadk.library.tools.xpath.SIFXPathContext;

import org.apache.commons.jxpath.Variables;


/**
 * Encapsulates context operation about a set of mappings that is being done
 * to handle a specific operation, such as processing a SIF Message.<p> 
 * 
 * Using MappingsContext is more efficient than repeatedly calling <code>Mappings.mapInbound<code>
 * or <code>Mappings.mapOutbound</code>. To get an instance of MappingsContext, call 
 * {@link Mappings#selectInbound(ElementDef, SIFVersion, String, String)} or 
 * {@link Mappings#selectOutbound(ElementDef, SIFVersion, String, String)}
 * 
 * @author Andrew Elmhorst
 *
 */
public class MappingsContext {

	
	private SIFXPathContext fRootContext;
	private SIFVersion fSIFVersion;
	private ElementDef fElementDef;
	private MappingsDirection fDirection;
	private ValueBuilder fValueBuilder;
	private List<Mapping> fFieldMappings = new ArrayList<Mapping>();
	private Mappings fMappings;
	private ObjectMapping fObjectMappings;
	
	
	/**
	 * @param mappings
	 * @param direction
	 * @param version
	 * @param elementType
	 */
	private MappingsContext( 
			Mappings mappings, 
			MappingsDirection direction, 
			SIFVersion version, 
			ElementDef elementType )
	{
		fMappings = mappings;
		fDirection = direction;
		fSIFVersion = version;
		fElementDef = elementType;
	}
	
	/**
	 * Creates a MappingsContext instance to handle a set of mappings operations using
	 * the same parameters
	 * @param m The mappings instance to use
	 * @param direction The mappings direction
	 * @param version The version of SIF to use for evaluating rule filters on field mappings
	 * @param elementDef The ElementDef representing the object type being mapped
	 * @return A new MappingsContext, initialized to map using the specified parameters
	 */
	public static MappingsContext create(Mappings m, MappingsDirection direction, SIFVersion version, ElementDef elementDef) {
		// Get the rules associated with the element type
		MappingsContext mc = new MappingsContext( m, direction, version, elementDef );
		ObjectMapping om = m.getRules( elementDef.name(), true );
		mc.addRules( om );
		return mc;
	}

	private void addRules( ObjectMapping om ) {
		// Get the rules associated with the element type
		fObjectMappings = om;
		if( om != null )
		{
			for( Mapping fm: om.getAllRulesList( true ) ){
				// addRule( FieldMapping ) will automatically filter out
				// any rules that need to be filtered
				addRule( fm );
			}
		}
	}
	
	private synchronized SIFXPathContext getXPathContext( 
			SIFElement mappedElement, FieldAdaptor adaptor )
		throws ADKMappingException
	{
		if( !mappedElement.getElementDef().name().equals( fElementDef.name() ) ){
			throw new ADKMappingException( 
					"Unable to use object for mapping. MappingsContext expected an object of type '" + 
					fElementDef.name() + "' but was '" + mappedElement.getElementDef().name() + "'.", null );
		}
		
		if( fRootContext == null ){
			fRootContext = SIFXPathContext.newSIFContext( mappedElement, fSIFVersion );
			if( adaptor instanceof Variables ){
				fRootContext.setVariables( (Variables)adaptor );
			}
		}
		return SIFXPathContext.newSIFContext( fRootContext, mappedElement );
	}
	
	/**
	 * Perform a mapping operation on the specified SIFElement. The mapping operation
	 * will be either inbound or outbound, depending on whether this class was returned
	 * from {@link Mappings#selectInbound(ElementDef, SIFVersion, String, String)} or
	 * {@link Mappings#selectOutbound(ElementDef, SIFVersion, String, String)}
	 * @param mappedElement The SIFElement to perform the mappings operation on
	 * @param adaptor The FieldAdaptor to use for getting or setting data
	 * @throws ADKMappingException
	 */
	public void map(SIFElement mappedElement, FieldAdaptor adaptor )
		throws ADKMappingException
	{
		SIFXPathContext context = getXPathContext( mappedElement, adaptor );
		if( getDirection() == MappingsDirection.INBOUND ){
			fMappings.mapInbound( context, adaptor, mappedElement, fFieldMappings, fSIFVersion );
		} else if (getDirection() == MappingsDirection.OUTBOUND ){
			fMappings.mapOutbound( context, adaptor, mappedElement, fFieldMappings, fValueBuilder, fSIFVersion );
		}
	}
	
	/**
	 * Evaluates the filters defined for this FieldMapping. If any of the filters
	 * evaluate to false, the FieldMapping is not added
	 * 
	 * @param fieldMapping The FieldMapping to add
	 * @return True if the FieldMapping was added. Otherwise false
	 */
	boolean addRule( Mapping mapping )
	{
		MappingsFilter filt = mapping.getFilter();
		//	Filter out this rule?
		if( filt != null ) {
			if( !filt.evalDirection( getDirection() ) ||
				!filt.evalVersion( fSIFVersion ) )
			return false;
		}
		
		return fFieldMappings.add( mapping );
	}


	/**
	 * Gets the MappingsDirection being used for this Mappings Context
	 * @return A MappingsDirection value (INBOUND or OUTBOUND)
	 */
	public MappingsDirection getDirection() {
		return fDirection;
	}
	
	/**
	 * Sets the ValueBuilder instance to use for mapping operations. 
	 * @param functions A class implementing the ValueBuilder interface
	 */
	public void setValueBuilder( ValueBuilder functions )
	{
		fValueBuilder = functions;
	}

	
	/**
	 * Gets the ObjectMapping instance that this MappingsContext was initialized with
	 * @return The ObjectMapping being used for this MappingsContext
	 */
	public ObjectMapping getObjectMappings() {
		return fObjectMappings;
	}
	
	/**
	 * Returns an unmodifiable collection of the FieldMappings defined
	 * in this mapping context
	 * @return an unmodifiable collection of FieldMappings
	 */
	public Collection<Mapping> getFieldMappings(){
		return Collections.unmodifiableCollection( fFieldMappings );
	}
	
	
	/**
	 * Returns the ElementDef that this MappingsContext is initialized to
	 * map values for
	 * @return The ElementDef that this MappingsContext is mapping data for
	 */
	public ElementDef getObjectDef(){
		return fElementDef;
	}
	
	/**
	 * Returns the Mappings object that this context is using to perform
	 * Mapping operations.
	 * @return a Mappings instance
	 */
	public Mappings getMappings()
	{
		return fMappings;
	}
	


	
}
