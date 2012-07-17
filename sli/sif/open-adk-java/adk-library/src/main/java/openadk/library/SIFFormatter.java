//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import openadk.library.impl.ElementSorter;
import openadk.library.impl.SIFIOFormatter;


/**
 * A class that converts native datatypes supported by SIF to their textual
 * representation.
 *
 * @author Andrew Elmhrost
 * @version 2.0
 */
public abstract class SIFFormatter {

	/**
	 * Converts a Java <c>Calendar</c> value to a SIF data value
	 *
	 * @param date
	 * @return The date formatted as a string, using SIF formatting requirements
	 */
	public abstract String toDateString(Calendar date);

	/**
	 * Converts a Java <c>Calendar</c> value to a SIF datetime value
	 *
	 * @param date
	 * @return The datetime formatted as a string, using SIF formatting
	 *         requirements
	 */
	public abstract String toDateTimeString(Calendar date);

	/**
	 * Converts a Java <c>Calendar</c> value to a SIF time value
	 *
	 * @param time
	 * @return The time formatted as a string, using SIF formatting requirements
	 */
	public abstract String toTimeString(Calendar time);

	/**
	 * Converts a Java <c>BigDecimal</c> value to a SIF decimal value
	 *
	 * @param decimalValue
	 * @return The BigDecimal formatted as a string, using SIF formatting
	 *         requirements
	 */
	public abstract String toString(BigDecimal decimalValue);

	/**
	 * Converts a Java <c>Boolean</c> value to a SIF boolean value
	 *
	 * @param boolValue
	 * @return The boolean formatted as a string, using SIF formatting
	 *         requirements
	 */
	public abstract String toString(Boolean boolValue);

	/**
	 * Converts an a SIF XML date value to a Java <c>Calendar</c> value
	 *
	 * @param dateValue
	 * @return A Calendar that has been parsed from the SIF String
	 *         representation
	 * @throws IllegalArgumentException
	 *             If the value cannot be parsed
	 */
	public abstract Calendar toDate(String dateValue);

	/**
	 * Parses a datetime value from the provided string. NOTE: SIF 1.x does not
	 * support datetime values.
	 *
	 * @param xmlValue
	 * @return A Calendar that has been parsed from the SIF String
	 *         representation
	 * @throws IllegalArgumentException
	 *             If the date cannot be parsed
	 */
	public abstract Calendar toDateTime(String xmlValue);

	/**
	 * Parses a time value from the provided string. NOTE: SIF 1.x does not
	 * support time values.
	 *
	 * @param xmlValue
	 * @return A Calendar that has been parsed from the SIF String
	 *         representation
	 * @throws IllegalArgumentException
	 *             If the value cannot be parsed
	 */
	public abstract Calendar toTime(String xmlValue);

	/**
	 * Converts an a SIF XML decimal value to a Java <c>BigDecimal</c> value
	 *
	 * @param decimalValue
	 * @return A Decimal that has been parsed from the SIF String representation
	 * @throws IllegalArgumentException
	 *             If the value cannot be parsed
	 */
	public abstract BigDecimal toDecimal(String decimalValue);


	/**
	 * Converts an a SIF XML duration value to a Java <c>Duration</c> value
	 *
	 * @param xmlValue
	 *            A Duration formatted as a String or NULL
	 * @return An XML Duration that has been parsed from the SIF String
	 *         representation
	 * @throws IllegalArgumentException
	 *             If the value cannot be parsed
	 */
	public abstract Duration toDuration(String xmlValue);

	/**
	 * Converts a Java <c>Duration</c> value to a SIF duration value
	 *
	 * @param d
	 * @return The Duration formatted as a string, using SIF formatting
	 *         requirements
	 * @throws IllegalArgumentException
	 *             If the duration cannot be parsed
	 * @throws UnsupportedOperationException
	 *             in SIF 1.1, since Durations are not used
	 */
	public abstract String toString(Duration d);

	/**
	 * Converts an a SIF XML boolean value to a Java <c>Boolean</c> value
	 *
	 * @param inValue
	 *            A boolean formatted as a String or NULL
	 * @return A Boolean that has been parsed from the String representation
	 * @throws IllegalArgumentException
	 *             If the value cannot be parsed
	 */
	public abstract Boolean toBoolean(String inValue);

	/**
	 * @return The Content-Type HttpHeader that should be used
	 */
	public String getContentType() {
		return SIFIOFormatter.CONTENT_TYPE;
	}

	/**
	 * @return the Charset used by SIF to encode XML to a binary transport
	 */
	public Charset getCharset() {
		return SIFIOFormatter.CHARSET;
	}

	/**
	 * Returns true if the formatter supports XML Namespaces. If this value
	 * returns true, XML Namespaces will be declared when writing SIF Elements,
	 * and the xsi:nil attribute will be enabled
	 *
	 * @return true if the formatter supports namespaces
	 */
	public abstract boolean supportsNamespaces();

	/**
	 * Adds a SIFElement parsed from a specific version of SIF to the parent.
	 * The formatter instance may use version-specific rules to ensure that the
	 * hierarchy is properly maintained when the source of the content is from
	 * this version of SIF
	 *
	 * @param contentParent
	 *            The element to add content to
	 * @param content
	 *            The element to add
	 * @param version
	 *            The version of SIF that the SIFElement is being constructed
	 *            from
	 * @return Returns the child that has been added to the parent
	 */
	public SIFElement addChild(SIFElement contentParent, SIFElement content,
			SIFVersion version) {
		return contentParent.addChild(content);
	}

	/**
	 * Adds a SimpleField parsed from a specific version of SIF to the parent.
	 *
	 * @param contentParent
	 *            The element to add content to
	 * @param fieldDef
	 *            The metadata definition of the field to set
	 * @param data
	 * 			The value to set to the field
	 * @param version
	 *            The version of SIF that the SIFElement is being constructed
	 *            from
	 */
	public SimpleField setField(
			SIFElement contentParent,
			ElementDef fieldDef,
			SIFSimpleType data,
			SIFVersion version ) {
		return contentParent.setField( fieldDef, data );
	}

	/**
	 * Gets the content from the SIFElement for the specified version of SIF.
	 * Only elements that apply to the requested version of SIF will be
	 * returned.
	 *
	 * @param element
	 *            The element to retrieve content from
	 * @param version
	 * @return The content of the specified parent
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getContent(SIFElement element, SIFVersion version) {

		List<Element> returnValue = new ArrayList<Element>();
		List<SimpleField> fields = element.getFields();
		for (SimpleField val : fields) {
			ElementDef def = val.getElementDef();
			if ( def.isSupported( version ) &&
					!def.isAttribute( version ) &&
					def.isField() ){
				returnValue.add(val);
			}
		}

		List<? extends SIFElement> children = element.getChildList();
		for (SIFElement val : children) {
			ElementDef def = val.getElementDef();
			if( def.isSupported( version )  )
			{
				returnValue.add(val);
			}
		}

		Collections.sort( returnValue, ElementSorter.getInstance(version) );
		return returnValue;
	}

	/**
	 * Gets the fields from the SIFElement for the specified version of SIF.
	 * Only the fields that apply to the requested version of SIF will be
	 * returned.
	 *
	 * @param element
	 * @param version
	 * @return Returns the fields of the SIFElement
	 */
	@SuppressWarnings("unchecked")
	public List<SimpleField> getFields(SIFElement element, SIFVersion version) {
		List<SimpleField> fields = element.getFields();
		// remove any fields that do not belong in this version of SIF
		for( int a = fields.size() - 1; a > -1 ; a-- ){
			SimpleField field = fields.get( a );
			ElementDef def = field.getElementDef();
			if( version.compareTo(def.getEarliestVersion()) < 0 ||
					version.compareTo(def.getLatestVersion()) > 0  ){
				fields.remove( a );
			}
		}
		Collections.sort(fields, ElementSorter.getInstance(version));
		return fields;
	}

	public abstract String toString( Integer value );

	/**
	 * Converts an a SIF XML int value to a Java <c>int</c> value
	 *
	 * @param value
	 * @return An Integer that has been parsed from the SIF String
	 *         representation
	 * @throws IllegalArgumentException
	 *             If the value cannot be parsed
	 */
	public Integer toInteger(String value) {
		if( value == null ){
			return null;
		}
		value = value.trim();
		if( value.length() == 0 ){
			return null;
		}
		return Integer.parseInt( value  );
	}



	/**
	 * Converts an a SIF XML float value to a Java <c>Float</c> value
	 *
	 * @param floatValue
	 * @return A float that has been parsed from the SIF String
	 *         representation
	 * @throws IllegalArgumentException
	 *             If the value cannot be parsed
	 */
	public Float toFloat(String floatValue) {
		if (floatValue == null ) {
			return null;
		}
		floatValue = floatValue.trim();
		if( floatValue.length() == 0 ){
			return null;
		}
		if( floatValue.indexOf( 'N' ) > -1 ){
			// Parse for INF, -INF and NaN
			if( floatValue.equalsIgnoreCase( "INF" ) ){
				return Float.POSITIVE_INFINITY;
			} else if( floatValue.equalsIgnoreCase( "-INF" ) ){
				return Float.NEGATIVE_INFINITY;
			} else if( floatValue.equalsIgnoreCase( "NaN" ) ){
				return Float.NaN;
			}
		}
		return Float.parseFloat( floatValue);
	}

	public Long toLong(String longValue) {
		if (longValue == null ) {
			return null;
		}
		longValue = longValue.trim();
		if( longValue.length() == 0 ){
			return null;
		}
		return Long.parseLong( longValue );
	}

	public String toString(Long longValue) {
		if (longValue == null) {
			return null;
		}
		return longValue.toString();
	}

	public String toString(Float floatValue) {
		if (floatValue == null) {
			return null;
		}
		if( floatValue == Float.POSITIVE_INFINITY ){
			return "INF";
		}else if( floatValue == Float.NEGATIVE_INFINITY ){
			return "-INF";
		} else if( floatValue == Float.NaN ){
			return "NaN";
		}

		return floatValue.toString();
	}

	/**
	 * Converts a UUID to a string using the SIF format
	 *
	 * @param guid
	 *            the UUID to convert
	 * @return the UUID formatted as a SIFRefId
	 */
	public static String UUIDToRefID(UUID guid) {
		String value = guid.toString();
		return value.replace("-", "").toUpperCase();
	}

	/**
	 * Converts a SIF RefId to a Java UUID
	 *
	 * @param sifRefId
	 *            the string formatted as a SIF refId
	 * @return a UUID
	 * @throws IllegalArgumentException
	 *             if sifRefid does not conform to the string representation of
	 *             a SIF RefId
	 */
	public static UUID RefIDToUUID(String sifRefId) {
		if (sifRefId == null) {
			throw new IllegalArgumentException("Argument cannot be null");
		}
		if (sifRefId.length() != 32) {
			throw new IllegalArgumentException(
					"RefId is not in proper format. Expected 32 characters, was "
							+ sifRefId.length());
		}
		return UUID.fromString(addGuidDashesToRefID(sifRefId));
	}

	private static String addGuidDashesToRefID(String refID) {
		// TODO: There's got to be a better way to do this.
		StringBuilder a_Builder = new StringBuilder(refID);
		if (refID.length() > 23) {
			a_Builder.insert(8, '-');
			a_Builder.insert(13, '-');
			a_Builder.insert(18, '-');
			a_Builder.insert(23, '-');
		}

		return a_Builder.toString();
	}

	public synchronized static DatatypeFactory getDataTypeFactory() {
		if (sXmlDataTypeFactory == null) {
			try {
				sXmlDataTypeFactory = DatatypeFactory.newInstance();
			} catch (DatatypeConfigurationException dce) {
				ADK.getLog().error("Unable to load XMLDatatypeFactory: " + dce,
						dce);
			}
		}
		return sXmlDataTypeFactory;
	}

	private static DatatypeFactory sXmlDataTypeFactory;

}
