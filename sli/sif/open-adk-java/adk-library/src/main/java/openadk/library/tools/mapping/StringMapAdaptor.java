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
 *  local application-defined name of a field and the value is the text
 *  value to assign to the corresponding element or attribute of the
 *  SIFDataObject, in the SIF 1.5r1 text format.<p>
 *
 *   This class is most useful for agents that were written using the 1.x version
 *   of the ADK and offers compatibility with the Mappings implementation used in
 *   that release. Values mapped to and from the Map used in this class will match
 *   the textual representation of those values in the 1.x version of the ADK. For example,
 *   a SIF date field that is mapped to this class will map to and from the SIF 1.5 format
 *   for dates, which was yyyyMMdd.<p>
 *
 *   The Data-to-Text formatting is controlled by the {@link openadk.library.ADK#getTextFormatter()}
 *   property, which defaults to SIF 1.5 formatting. If you wish to supply your own text formatter,
 *   you can call the constructor overload that accepts a SIFFormatter instance.
 *
 *
 *    To use this class,<p>
 *
 *  <ol>
 *      <li>
 *          Create an instance and optionally populate the Map with known field
 *          values that will not be subject to the mapping process. If pre-loading
 *          the Map, the key of each entry should be the local
 *          application-defined field name and the value should be the string
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
 *   @see openadk.library.ADK#setTextFormatter(SIFFormatter)
 *   @see openadk.library.ADK#getTextFormatter()
 *   @see openadk.library.tools.mapping.Mappings
 *
 * @author Andrew Elmhorst
 *
 * @version 2.0
 *
 */
public class StringMapAdaptor extends ObjectMapAdaptor {

	private SIFFormatter fDataFormatter;
	private boolean fLooseDataTypeParsing = true;

	/**
	 * Creates an instance of StringMapAdaptor that uses the specified Map
	 * @param dataMap The <code>Map</code> to use for SIF Data mapping operations
	 */
	public StringMapAdaptor( Map dataMap )
	{
		this( dataMap, ADK.getTextFormatter() );
	}

	/**
	 * Creates an instance of StringMapAdaptor that uses the specified map and
	 * specified SIFFormatter instance for deriving String values from SIF datatypes
	 * @param dataMap The <code>Map</code> to use for SIF Data mapping operations
	 * @param formatter The formatter to use for converting SIF native datatypes to
	 * text and back again.
	 */
	public StringMapAdaptor(Map dataMap, SIFFormatter formatter )
	{
		super( dataMap );
		fDataFormatter = formatter;
	}

	/**
	 * @see openadk.library.tools.mapping.ObjectMapAdaptor#toMapValue(openadk.library.SIFSimpleType)
	 */
	@Override
	protected Object toMapValue( SIFSimpleType value )
	{
		// Store items in the map using a string value
		return value.toString( fDataFormatter );
	}

	/**
	 * @see openadk.library.tools.mapping.ObjectMapAdaptor#fromMapValue(String, java.lang.Object, openadk.library.SIFTypeConverter)
	 */
	@Override
	protected SIFSimpleType fromMapValue( String fieldName, Object mapValue, SIFTypeConverter typeConverter )
	{
		// Items are stored in the map as string values. Convert them using the typeConverter's parse method
		if( mapValue == null ){
			return null;
		}
		try {
			return typeConverter.parse( fDataFormatter, (String)mapValue );
		}
		catch( ADKParsingException adkpe ) {
			if( fLooseDataTypeParsing  ){
				// TT 2998 Add support for ignoring type parse exceptions
				// on Outbound data. If it fails to parse, ignore
				if( ( ADK.debug & ADK.DBG_MESSAGING_DETAILED )  > 0 ){
					ADK.getLog().warn(
							"Unable to parse value for outbound mapping for field " +
							fieldName +
							 ". Ignored error: " + adkpe.getMessage() , adkpe );
				}
				return null;
			}
			else
			{
				throw new NumberFormatException( adkpe.getMessage() );
			}

		}
	}

	/**
	 * Sets whether or not data type conversion from string to SIF Types throws an Exception or not
	 * @param fLooseDataTypeParsing TRUE if the ADK should suppress exceptions for data type parsing errors
	 */
	public void setLooseDataTypeParsing(boolean fLooseDataTypeParsing) {
		this.fLooseDataTypeParsing = fLooseDataTypeParsing;
	}

	/**
	 * Sets whether or not data type conversion from string to SIF Types throws an Exception or not
	 * @return TRUE if the ADK suppresses exceptions for data type parsing errors
	 */
	public boolean getLooseDataTypeParsing() {
		return fLooseDataTypeParsing;
	}
}
