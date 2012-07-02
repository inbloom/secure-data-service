//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import openadk.library.SIFFormatter;
import openadk.library.SIFSimpleType;
import openadk.library.SIFTypeConverter;
import openadk.library.ValueBuilder;

/**
 * An interface definition of a class that can be used to get or set values
 * for fields during a Mapping operation. 
 * 
 * @author Andrew Elmhorst
 *
 * @version 2.0
 */
public interface FieldAdaptor {
	/**
	 * Returns whether the field being requested for mapping has a value that can
	 * be mapped to SIF
	 * @param fieldName The field name being mapped to SIF
	 * @return <code>True</code> if there is a value for the specified field that
	 * should be mapped to SIF
	 */
	boolean hasField(String fieldName);
	
	/**
	 * Sets a value that has been retrieved from a SIF Element in an inbound field
	 * mapping operation.
	 * @param fieldName The field name that is mapped to a SIFElement
	 * @param value The value of the SIF element
	 * @param mapping The FieldMappings that will be used to set this value or null
	 */
	void setSIFValue(String fieldName, SIFSimpleType value, FieldMapping mapping );
	
	/**
	 * Gets a value from the underlying data store to be used in an outbound
	 * field mapping operation 
	 * @param fieldName The field name that is mapped to a SIFElement
	 * @param typeConverter The converter class for the requested SIF data type
	 * @param mapping The FieldMapping this value was generated from or null
	 * @return The value to set to the SIF element. This value must contain the
	 * SIFSimpleType subclass represented by the SIFTypeConverter passed in to the 
	 * method.
	 */
	SIFSimpleType getSIFValue( String fieldName, SIFTypeConverter typeConverter, FieldMapping mapping ); 
	
	/**
	 * Returns the underlying value being stored for the field.<p>
	 * 
	 * This method is not called during Mappings operations. It may be called by
	 * classes such as the {@link openadk.library.ValueBuilder} class
	 * 
	 * @param key The field name to retrieve
	 * @return The underlying data object representing the field.
	 */
	Object getValue( String key );
	
}
