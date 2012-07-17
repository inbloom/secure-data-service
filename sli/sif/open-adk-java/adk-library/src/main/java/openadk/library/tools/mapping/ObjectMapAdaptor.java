//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.util.Map;
import java.util.Set;

import openadk.library.*;

import org.apache.commons.jxpath.Variables;


/**
 *
 * 	A FieldAdaptor implementation that contains field values to assign to the
 *  supplied SIFDataObject, where each entry in the map is keyed by the
 *  local application-defined name of a field and the value is the native
 *  Java datatype of the corresponding element or attribute of the
 *  SIFDataObject, such as a Calender, String, Integer, Boolean, etc.<p>
 *
 *    To use this class,<p>
 *
 *  <ol>
 *      <li>
 *          Create an instance and optionally populate the Map with known field
 *          values that will not be subject to the mapping process. If pre-loading
 *          the Map, the key of each entry should be the local
 *          application-defined field name and the value should be the native data type
 *          value of that field. Any field added to the Map before calling
 *          this method will not be subject to mapping rules, unless the
 *          {@link openadk.library.tools.mapping.ObjectMapAdaptor#setOverwriteValues(boolean)}
 *           property is set to <code>True</code>.
 *      </li>
 *      <li>
 *          Use this class instance with the {@link openadk.library.tools.mapping.Mappings}
 *          class,by calling the appropriate <code>map</code> method and passing the SIFDataObject
 *          instance to retrieve field values from for insertion into the
 *          Map. The method first looks up the ObjectMapping instance
 *          corresponding to the SIF Data Object type. If no ObjectMapping
 *          has been defined for the object type, no action is taken and the
 *          method returns successfully without exception. Otherwise, all
 *          field rules defined by the ObjectMapping are evaluated in order.
 *          If a rule evaluates successfully, the corresponding element or
 *          attribute value will be inserted into the HashMap. A rule will
 *          not be evaluated if the associated field already exists in the
 *          Map, unless the
 *          {@link openadk.library.tools.mapping.ObjectMapAdaptor#setOverwriteValues(boolean)}
 *          property is set to <code>True</code>.
 *      </li>
 *  </ol>
 *
 *   @see openadk.library.tools.mapping.Mappings
 *
 * @author Andrew Elmhorst
 *
 * @version 2.0
 *
 */
public class ObjectMapAdaptor
	implements FieldAdaptor, Variables
{

	protected Map fMap;
	private boolean fOverwriteValues = false;

	/**
	 * Creates an instance of ObjectMapAdaptor that uses the specified Map
	 * @param map The <code>Map</code> to use for SIF Data mapping operations
	 */
	public ObjectMapAdaptor( Map map )
	{
		fMap = map;
	}

	/**
	 * Converts a SIF datatype value to the java native type to be stored
	 * in the Map. The default implementation of this class stores the
	 * native Java value, but subclasses can override this method to convert
	 * the value to the form they want to use.
	 * @param value The SIF value to stored in the Map
	 * @return The converted value stored in the Map
	 */
	protected Object toMapValue( SIFSimpleType value )
	{
		return value.getValue();
	}

	/**
	 * Converts the object stored in the Map to the SIF data type. The data
	 * type required by SIF is available by examining the {@link SIFTypeConverter}
	 * passed in to the method.
	 * @param fieldName TODO
	 * @param mapValue The value that was retrieved from the Map
	 * @param typeConverter A representation of the desired SIF datatype
	 * @return The converted SIF value
	 */
	protected SIFSimpleType fromMapValue( String fieldName, Object mapValue, SIFTypeConverter typeConverter )
	{
		return typeConverter.getSIFSimpleType( mapValue );
	}

	/**
	 * @see openadk.library.tools.mapping.FieldAdaptor#getValue(java.lang.String)
	 */
	public Object getValue(String fieldName ) {
		return fMap.get( fieldName );
	}


	/**
	 * @see openadk.library.tools.mapping.FieldAdaptor#hasField(java.lang.String)
	 */
	public boolean hasField(String fieldName) {
		return fMap.containsKey( fieldName );
	}

	/**
	 * @see openadk.library.tools.mapping.FieldAdaptor#setSIFValue(java.lang.String, openadk.library.SIFSimpleType, openadk.library.tools.mapping.FieldMapping)
	 */
	public void setSIFValue(String fieldName, SIFSimpleType resultingValue, FieldMapping mapping ) {
		if( fOverwriteValues || !fMap.containsKey( fieldName ) ){
			Object mapValue = toMapValue( resultingValue );
			fMap.put( fieldName, mapValue );
		}
	}



	/**
	 * @see openadk.library.tools.mapping.FieldAdaptor#getSIFValue(java.lang.String, openadk.library.SIFTypeConverter, openadk.library.tools.mapping.FieldMapping)
	 */
	public SIFSimpleType getSIFValue( String fieldName, SIFTypeConverter typeConverter, FieldMapping mapping )
	{
		if( fMap.containsKey( fieldName ) ){
			Object value = fMap.get( fieldName );
			return fromMapValue( fieldName, value, typeConverter );
		} else {
			// No value in the Map. Return null
			return null;
		}
	}

	/**
	 * Gets the <code>Map</code> being used for SIF data mapping operations
	 * @return The <code>Map</code> being used for SIF data mapping operations
	 */
	public Map getMap()
	{
		return fMap;
	}

	/**
	 * Sets the <code>Map</code> being used for SIF data mapping operations
	 * @param map The <code>Map</code> to be used for SIF data mapping operations
	 */
	public void setMap( Map map )
	{
		fMap = map;
	}

	/**
	 * Gets the keyset of the underlying Map
	 * @return the keyset of the underlying Map
	 */
	public Set keySet()
	{
		return fMap.keySet();
	}

	/**
	 * This setting influences inbound mapping operations. If set to <code>True</code>,
	 * data coming from SIF can overwrite existing values in the Map. The
	 * default value is <code>False</code>
	 * @param overwriteValues <code>True</code> to overwrite existing values in the
	 * Map with data from SIF
	 */
	public void setOverwriteValues(boolean overwriteValues) {
		fOverwriteValues = overwriteValues;
	}

	/**
	 * Returns whether this class will overwrite existing values in the Map
	 * during inbound mapping operations of data coming from SIF.
	 * @return True if this class should overwrite existing values in the Map
	 */
	public boolean getOverwriteValues() {
		return fOverwriteValues;
	}


	/**
	 * From the Variables interface. Always returns true because
	 * this class allows variables with any name to be tried
	 * @param varName
	 * @return true
	 */
	public boolean isDeclaredVariable(String varName) {
		// Always return true because this class doesn't care if the
		// variable has been declared or not. Allow it to be tried
		return true;
	}

	/**
	 * Get the specified field from the Map for a JXPath variable
	 *
	 * @param varName The name of the field
	 * @return The object or an empty string if the field does not exist
	 */
	public Object getVariable(String varName) {
		if( fMap.containsKey( varName ) ){
			return fMap.get( varName );
		}
		return "";
	}

	/**
	 * Declares a field for the JXPath variable and assigns the value
	 * @param varName The name of the field
	 * @param value The value to assign to the field
	 */
	public void declareVariable(String varName, Object value) {
		fMap.put( varName, value );

	}

	/**
	 * Removes the underlying variable from the Map for JXPath support
	 * @param varName
	 */
	public void undeclareVariable(String varName) {
		fMap.remove( varName );

	}

}
