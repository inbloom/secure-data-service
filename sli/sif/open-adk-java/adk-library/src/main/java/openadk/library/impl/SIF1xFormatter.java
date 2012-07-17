//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import openadk.library.*;


/**
 * Represents a SIFFormatter than can be used to format data to and from SIF 1.x
 * datatypes
 *
 * @author Andrew Elmhorst
 * @version 2.0
 *
 */
public class SIF1xFormatter extends SIFFormatter {

	/**
	 * SimpleDateFormat is not a thread-safe object, so we store one copy in
	 * each thread.
	 */
	private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>();

	/**
	 * SimpleDateFormat is not a thread-safe object, so we store one copy in
	 * each thread.
	 */
	private static final ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>();


	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.SIFFormatter#toDateString(java.util.Calendar)
	 */
	public String toDateString(Calendar date) {
		if (date == null) {
			return "";
		}
		return getDateFormat().format(date.getTime());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.SIFFormatter#toString(java.lang.Boolean)
	 */
	public String toString(Boolean boolValue) {
		if (boolValue == null) {
			return "";
		}
		if( boolValue.booleanValue() ){
			return "Yes";
		} else {
			return "No";
		}
	}

	@Override
	public String toTimeString(Calendar time) {
		if (time == null) {
			return null;
		}
		return getTimeFormat().format(time.getTime());
	}

	@Override
	public Calendar toTime(String xmlValue) {
		if (xmlValue == null ) {
			return null;
		}
		xmlValue = xmlValue.trim();
		if( xmlValue.length() == 0 ){
			return null;
		}

		try {

			Date parsedDate = getTimeFormat().parse( xmlValue);
			Calendar retValue = Calendar.getInstance();
			retValue.clear();
			retValue.setTime(parsedDate);
			return retValue;
		} catch (ParseException parseEx) {
			throw new NumberFormatException(
					"Error parsing SIF 1.x formatted Date:'" + xmlValue + "'. " + parseEx.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.SIFFormatter#toDate(java.lang.String)
	 */
	public Calendar toDate(String value) {
		if (value == null ) {
			return null;
		}
		value = value.trim();
		if( value.length() == 0 ){
			return null;
		}

		try {

			Date parsedDate = getDateFormat().parse(value);
			Calendar retValue = Calendar.getInstance();
			retValue.clear();
			retValue.setTime(parsedDate);
			return retValue;
		} catch (ParseException parseEx) {
			throw new NumberFormatException(
					"Error parsing SIF 1.x formatted Date:'" + value + "'. " + parseEx.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.edustructures.sifworks.SIFFormatter#toBoolean(java.lang.String)
	 */
	public Boolean toBoolean(String value) {
		if (value == null ) {
			return null;
		}
		value = value.trim();
		if( value.length() == 0 ){
			return null;
		}
		if( value.equalsIgnoreCase( "yes" ) ){
			return true;
		} else if( value.equalsIgnoreCase( "no" ) ){
			return false;
		} else if( value.equalsIgnoreCase( "true" ) ){
			return true;
		} else if( value.equalsIgnoreCase( "false" ) ){
			return false;
		}

		throw new IllegalArgumentException( "Value '" + value + "' cannot be parsed into a Boolean" );

	}

	/**
	 * Returns true if the formatter supports writing the xsi:nil attribute. If
	 * the return value is false, SIFWriter will write an empty string
	 *
	 * @return true if the formatter supports writing the xsi:nil attribute
	 */
	public boolean supportsNamespaces() {
		return false;
	}

	/**
	 * SimpleDateFormat is not thread-safe, so we use a ThreadLocal to store one
	 * copy on each thread. Call this method to get a copy of the
	 * SimpleDateFormat used to turn java.util.Date into a String formatted for
	 * SIF.
	 *
	 * @return A SimpleDateFormat that can turn a java.util.Date into an 8
	 *         character date in the SIF format.
	 */
	private synchronized SimpleDateFormat getDateFormat() {
		SimpleDateFormat retval = (SimpleDateFormat) dateFormat.get();
		if (retval == null) {
			retval = new SimpleDateFormat("yyyyMMdd");
			retval.setLenient( false );
			dateFormat.set(retval);
		}
		return retval;
	}

	/**
	 * SimpleDateFormat is not thread-safe, so we use a ThreadLocal to store one
	 * copy on each thread. Call this method to get a copy of the
	 * SimpleDateFormat used to turn java.util.Date into a String formatted for
	 * SIF.
	 *
	 * @return A SimpleDateFormat that can turn a java.util.Date into an 8
	 *         character date in the SIF format.
	 */
	private synchronized SimpleDateFormat getTimeFormat() {
		SimpleDateFormat retval = (SimpleDateFormat) timeFormat.get();
		if (retval == null) {
			retval = new SimpleDateFormat( "HH:mm:ss" );
			retval.setLenient( false );
			timeFormat.set(retval);
		}
		return retval;
	}

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
	 */
	@SuppressWarnings("unchecked")
	public SIFElement addChild(SIFElement contentParent, SIFElement content,
			SIFVersion version)
	{
		contentParent.restoreImplementationDef( content );
		return getContainer( contentParent, content.getElementDef(), version ).addChild( content );
	}

	@Override
	public SimpleField setField(SIFElement contentParent, ElementDef fieldDef, SIFSimpleType data, SIFVersion version) {
		return getContainer( contentParent, fieldDef, version ).setField( fieldDef, data );
	}

	private SIFElement getContainer( SIFElement contentParent, ElementDef childDef, SIFVersion version )
	{

		ElementDef elementParentDef = childDef.getParent();
		if( elementParentDef != null && elementParentDef != contentParent.getElementDef() ){
			// The element does not appear to belong to this parent. Attempt to look
			// for a container element that might be missing in between the two
			// If the parent of this element were collapsed in a previous version
			// of SIF, check for or re-add the parent element and add this new
			// child instead.
			//
			// For example, a child could be an Email element from the
			// common package that's being added to StudentPersonal. In this
			// case,
			// we need to actually find or create an instance of the new
			// EmailList
			// container element and add the child to it, instead of to "this"
			String tag = elementParentDef.tag( ADK.getSIFVersion() );
			ElementDef missingLink = ADK.DTD().lookupElementDef( contentParent.getElementDef(), tag );
			if( missingLink != null && missingLink.isCollapsed( version ) ){
				SIFElement container = contentParent.getChild( missingLink );
				if (container == null) {
					try {
						container = SIFElement.create(contentParent, missingLink );
					} catch (ADKSchemaException adkse) {
						throw new IllegalArgumentException(adkse.getMessage(),
								adkse);
					}
				}
				addChild(contentParent, container, version);
				return container;
			}
		}

		return contentParent;
	}



	/**
	 * Gets the content from the SIFElement for the specified version of SIF.
	 * Only elements that apply to the requested version of SIF will be
	 * returned. Attributes are not returned.
	 *
	 * @param element
	 *            The element to retrieve content from
	 * @param version
	 * @return
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
			if( def.isSupported( version ) )
			{
				if (def.isCollapsed(version)) {
					List<? extends Element> subElements = getContent( val, version );
					// FIXUP the ElementDef for this version of SIF.
					// for example, StudentPersonal/EmailList/Email needs it's
					// ElementDef set to "StudentPersonal_Email"
					for (Element e : subElements) {
						ElementDef subElementDef = e.getElementDef();
						if (version.compareTo(subElementDef.getEarliestVersion()) >= 0) {
							String tag = subElementDef.tag( ADK.getSIFVersion() );
							ElementDef restoredDef = ADK.DTD().lookupElementDef( element.getElementDef(), tag );
							if (restoredDef != null) {
								e.setElementDef(restoredDef);
							}
							returnValue.add(e);
						}
					}

				} else {
					returnValue.add(val);
				}
			}
		}

		Collections.sort(returnValue, ElementSorter.getInstance(version));
		return returnValue;
	}


	/**
	 * Returns a string representation of the specified date. Since SIF 1.5 does
	 * not support the datetime datatype, this field formats the date as a SIF
	 * Date ("yyyyMMdd")
	 *
	 * @see openadk.library.SIFFormatter#toDateTimeString(java.util.Calendar)
	 */
	@Override
	public String toDateTimeString(Calendar date) {
		return toDateString(date);
	}

	@Override
	public String toString(BigDecimal decimalValue) {
		if( decimalValue == null ){
			return null;
		}
		return decimalValue.toPlainString();
	}

	@Override
	public BigDecimal toDecimal(String decimalValue) {
		if (decimalValue == null ) {
			return null;
		}
		decimalValue = decimalValue.trim();
		if( decimalValue.length() == 0 ){
			return null;
		}
		// SIF 1.5 allows some values to contain a percentage symbol. These can
		// be converted to decimal values after removing the percentage sign
		if( decimalValue.endsWith( "%" ) ){
			decimalValue = decimalValue.substring( 0, decimalValue.length() - 1 );
		}
		return new BigDecimal(decimalValue);
	}

	@Override
	public Calendar toDateTime(String xmlValue){
		return toDate( xmlValue );
	}

	@Override
	public Duration toDuration(String xmlValue) {
		if (xmlValue == null ) {
			return null;
		}xmlValue.trim();
		if( xmlValue.length() == 0 ){
			return null;
		}

		// TODO: This method probably needs to become more optimized
		// by storing the DatatypeFactory globally or re-using the one from
		// the SIF2xFormatter....
		DatatypeFactory factory = null;
		try
		{
			factory = DatatypeFactory.newInstance();
		}
		catch( DatatypeConfigurationException dce ){
			throw new RuntimeException( dce.getMessage(), dce );
		}

		Calendar time = toTime( xmlValue );
		if( time != null ){
			return factory.newDurationDayTime( true, 0, time.get( Calendar.HOUR ), time.get( Calendar.MINUTE ), time.get( Calendar.SECOND ) );
		}
		return null;

	}

	@Override
	public String toString(Duration d) {
		// returns a string formatted as "HH:MM:SS"
		StringBuilder sb = new StringBuilder();
		sb.append( d.getHours() );
		sb.append( ':' );
		sb.append( d.getMinutes() );
		sb.append( ':' );
		sb.append( d.getSeconds() );
		return sb.toString();

	}

	/**
	 * Converts a Java <c>int</c> value to a SIF int value
	 *
	 * @param intValue
	 * @return The int formatted as a string, using SIF formatting requirements
	 */
	public String toString(Integer intValue) {
		if( intValue == null ){
			return "";
		}
		return String.valueOf( intValue );
	}
}
