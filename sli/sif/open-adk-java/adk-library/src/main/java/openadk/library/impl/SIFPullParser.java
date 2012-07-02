//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import openadk.library.ADK;
import openadk.library.ADKParsingException;
import openadk.library.ADKTypeParseException;
import openadk.library.DTD;
import openadk.library.Element;
import openadk.library.ElementDef;
import openadk.library.ElementVersionInfo;
import openadk.library.SIFDTD;
import openadk.library.SIFElement;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;
import openadk.library.SIFFormatter;
import openadk.library.SIFParser;
import openadk.library.SIFSimpleType;
import openadk.library.SIFString;
import openadk.library.SIFTypeConverter;
import openadk.library.SIFTypeConverters;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.SimpleField;
import openadk.library.Zone;
import openadk.library.common.CommonDTD;
import openadk.library.common.SIF_ExtendedElement;
import openadk.library.common.XMLData;
import openadk.library.datamodel.DatamodelDTD;
import openadk.library.impl.surrogates.RenderSurrogate;
import openadk.library.infra.SIF_Query;
import openadk.library.infra.SIF_QueryObject;
import openadk.library.infra.SIF_Request;
import openadk.util.XMLNodeReader;
import openadk.util.XMLStreamDocumentBuilder;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * 
 * A SIF Parser implementation that uses StAX-based parsing, using the SUN
 * StAXParser
 * 
 * @author Andrew Elmhorst
 * 
 */
public class SIFPullParser extends SIFParser {

	private SIFElement fParsed;

	/**
	 * The constructor is made protected so that access can only be done using
	 * the factory method.
	 */
	public SIFPullParser() {
	}

	private static XMLInputFactory sFactory = createXmlInputFactory();

	/**
	 * Creates an instance of an XMLInputFactory for use with this parser
	 * 
	 * @return
	 */
	private static XMLInputFactory createXmlInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		
		// Not all XMLInputFactory service providers support all properties, so that will now be checked before attempting to set them
		for (String propertyToEnable : new String[] {"reuse-instance", XMLInputFactory.IS_COALESCING}) {
			if (factory.isPropertySupported(propertyToEnable)) {
				try {
					factory.setProperty(propertyToEnable, true);
				} catch (IllegalArgumentException iae) {
					iae.printStackTrace();
				}
			}
		}
		
		return factory;
	}

	/**
	 * Closes the XMLStreamReader and translates any exceptions to an
	 * ADKException
	 * 
	 * @param reader
	 * @param zone
	 * @throws ADKParsingException
	 */
	private void closeXmlReader(XMLStreamReader reader, Zone zone) throws ADKParsingException {
		try {
			reader.close();
		} catch (XMLStreamException xse) {
			throw new ADKParsingException(xse.getLocalizedMessage(), zone, xse);
		}
	}

	/**
	 * Creates an XMLStreamReader instance that reads from the specified Reader
	 * 
	 * @param msg
	 * @return
	 * @throws ADKParsingException
	 */
	private XMLStreamReader getXmlReader(Reader reader) throws ADKParsingException {
		try {
			synchronized (sFactory) {
				return sFactory.createXMLStreamReader(reader);
			}
		} catch (XMLStreamException xse) {
			throw new ADKParsingException(xse.getLocalizedMessage(), null, xse);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edustructures.sifworks.SIFParser#parse(java.lang.String )
	 */
	@Override
	public SIFElement parse(String msg) throws ADKParsingException, SIFException

	{
		return parse(msg, null, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edustructures.sifworks.SIFParser#parse(java.lang.String,
	 * com.edustructures.sifworks.Zone)
	 */
	@Override
	public SIFElement parse(String msg, Zone zone) throws ADKParsingException, SIFException

	{
		return parse(msg, zone, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edustructures.sifworks.SIFParser#parse(java.lang.String,
	 * com.edustructures.sifworks.Zone, int)
	 */
	@Override
	public SIFElement parse(String msg, Zone zone, int flags) throws ADKParsingException, SIFException {
		return parse(msg, zone, flags, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edustructures.sifworks.SIFParser#parse(java.lang.String,
	 * com.edustructures.sifworks.Zone, int,
	 * com.edustructures.sifworks.SIFVersion)
	 */
	@Override
	public SIFElement parse(String msg, Zone zone, int flags, SIFVersion version) throws ADKParsingException, SIFException {
		StringReader sr = new StringReader(msg);
		XMLStreamReader reader = getXmlReader(sr);
		SIFElement parsed = parse(reader, zone, flags, version);
		closeXmlReader(reader, zone);
		sr.close();
		return parsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edustructures.sifworks.SIFParser#parse(java.io.Reader,
	 * com.edustructures.sifworks.Zone)
	 */
	@Override
	public SIFElement parse(Reader msg, Zone zone) throws ADKParsingException, SIFException {
		XMLStreamReader reader = getXmlReader(msg);
		SIFElement parsed = parse(reader, zone, 0, null);
		closeXmlReader(reader, zone);
		return parsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edustructures.sifworks.SIFParser#parse(java.io.Reader,
	 * com.edustructures.sifworks.Zone, int)
	 */
	@Override
	public SIFElement parse(Reader msg, Zone zone, int flags) throws ADKParsingException, SIFException {
		XMLStreamReader reader = getXmlReader(msg);
		SIFElement parsed = parse(reader, zone, flags, null);
		closeXmlReader(reader, zone);
		return parsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edustructures.sifworks.SIFParser#parse(java.io.Reader,
	 * com.edustructures.sifworks.Zone, int,
	 * com.edustructures.sifworks.SIFVersion)
	 */
	@Override
	public SIFElement parse(Reader msg, Zone zone, int flags, SIFVersion version) throws ADKParsingException, SIFException {
		XMLStreamReader reader = getXmlReader(msg);
		SIFElement parsed = parse(reader, zone, flags, version);
		closeXmlReader(reader, zone);
		return parsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edustructures.sifworks.SIFParser#getParsed()
	 */
	@Override
	public SIFElement getParsed() {
		return fParsed;
	}

	private SIFElement parse(XMLStreamReader reader, Zone zone, int flags, SIFVersion version) throws ADKParsingException, SIFException {
		// Move to the first element in the XML document
		fParsed = null;
		if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
			try {
				reader.nextTag();
			} catch (XMLStreamException xse) {
				throw new ADKParsingException(xse.getLocalizedMessage(), zone, xse);
			}
		}
		if (reader.getLocalName().equals("SIF_Message")) {
			SIFElement element = readSIFMessageElement(reader, ADK.DTD(), zone, flags, version);
			return element.getChildList().get(0);
		} else {
			version = parseVersion(reader, ADK.DTD(), zone, flags, version);
			return parseElementStream(reader, version, ADK.DTD(), zone, flags);
		}
	}

	/**
	 * Reads a SIF_Message element, which sets the version and namespace scope
	 * for the rest of the xml parsing
	 * 
	 * @param reader
	 * @param dtd
	 * @param zone
	 * @param flags
	 * @param defaultVersion
	 * @return
	 */
	protected SIFElement readSIFMessageElement(XMLStreamReader reader, DTD dtd, Zone zone, int flags, SIFVersion defaultVersion) throws ADKParsingException, SIFException {

		SIFVersion version = parseVersion(reader, dtd, zone, flags, defaultVersion);

		SIF_Message message = new SIF_Message();
		// Note: Always reset fParsed to the current message
		// This allows a proper SIF_Ack to be returned in Pull mode, even if
		// the payload doesn't parse properly
		fParsed = message;

		// Set the namespace from our working version
		message.setXmlns(version.getXmlns());

		if (version.compareTo(SIFVersion.SIF11) >= 0) {
			// If we are at SifVersion 1.1 or greater, set the version attribute
			message.setVersionAttribute(version.toString());
		}
		try {
			reader.nextTag();
		} catch (XMLStreamException xse) {
			throw new ADKParsingException(xse.getLocalizedMessage(), zone, xse);
		}

		SIFElement element = parseElementStream(reader, version, dtd, zone, flags);

		// Do we need to bump message version - JEN
		if (element instanceof SIF_Request) {
			SIF_Request request = (SIF_Request) element;
			SIF_Query query = request.getSIF_Query();

			if (query != null) {
				SIF_QueryObject queryObject = query.getSIF_QueryObject();
				String queryObjectName = queryObject.getObjectName();
				ElementDef def = ADK.DTD().lookupElementDef(queryObjectName);

				if (def != null) {
					String earlistVersion = def.getEarliestVersion().toString();
					SIFVersion earlier = SIFVersion.parse(earlistVersion);
					SIFVersion base = SIFVersion.parse("2.0r1");

					if (((earlier.getMajor() == base.getMajor()) && (earlier.getMinor() > base.getMinor())) || (earlier.getMajor() > base.getMajor())) {
						message.setVersion(earlistVersion);
					}
				}
			}
		}

		message.addChild(element);
		return message;

	}

	private SIFVersion parseVersion(XMLStreamReader reader, DTD dtd, Zone zone, int flags, SIFVersion defaultVersion) throws ADKParsingException, SIFException {

		SIFVersion version = null;
		// TODO: For now, we are using a reader that doesn't support namespaces,
		// so we can't look up the version attribute by name (for now)
		String verAttr = reader.getAttributeValue(null, "Version");
		if (verAttr == null) {
			for (int a = 0; a < reader.getAttributeCount(); a++) {
				if (reader.getAttributeLocalName(a).equals("Version")) {
					verAttr = reader.getAttributeValue(a);
				}
			}
		}

		// Order of precedence:
		// 1) Version attribute of message
		// 2) The version passed in (if not null)
		// 3) The namespace version (if able to parse)
		// 4) The ADK SIF Version

		if (verAttr != null) {
			version = SIFVersion.parse(verAttr);

		} else if (defaultVersion != null) {
			version = defaultVersion;
		} else {
			String namespace = reader.getNamespaceURI();
			version = SIFVersion.parseXmlns(namespace);
			if (version == null) {
				version = ADK.getSIFVersion();
			}
		}

		// Do validation on the version
		if (!ADK.isSIFVersionSupported(version)) {
			throw new SIFException(SIFErrorCategory.GENERIC, SIFErrorCodes.GENERIC_VERSION_NOT_SUPPORTED_3, "SIF " + version.toString() + " not supported", reader.getNamespaceURI(), zone);
		} else if (zone != null && zone.getProperties().getStrictVersioning()) {
			if (version.compareTo(ADK.getSIFVersion()) != 0) {
				throw new SIFException(SIFErrorCategory.GENERIC, SIFErrorCodes.GENERIC_VERSION_NOT_SUPPORTED_3, "SIF " + version.toString() + " message support disabled by this agent", "This agent is running in strict SIF "
						+ ADK.getSIFVersion().toString() + " mode", zone);
			}
		}

		return version;
	}

	private SIFElement parseElementStream(XMLStreamReader reader, SIFVersion version, DTD dtd, Zone zone, int flags) throws ADKParsingException, SIFException {

		boolean legacyParse = version.compareTo(SIFVersion.SIF20) < 0;

		// The current SIFElement being parsed
		SIFElement currentElement = null;
		SIFFormatter formatter = ADK.DTD().getFormatter(version);

		String xmlName = "";

		try {
			int nodeType = reader.getEventType();
			while (reader.hasNext()) {
				if (nodeType == XMLStreamConstants.CHARACTERS) {
					if (reader.isWhiteSpace()) {
						// JEN Alert Message problem - here
						try {
							nodeType = reader.nextTag();
						} catch (Exception e) {
							String errorMessage = xmlName + " Tag Parse Exception:";

							ADK.getLog().warn(errorMessage, e);

							nodeType = reader.next();
						}
						continue;
					}
					if (currentElement.getElementDef().hasSimpleContent()) {
						// This is a SIFElement that allows a Text value to be
						// set to it.
						// The XMLStreamReader cursor will automatically be
						// advanced by this method to what should be the
						// END_ELEMENT
						setFieldValueFromElement(currentElement.getElementDef(), currentElement, reader, version, formatter, zone);
						nodeType = reader.getEventType();
						continue;
					}
				} else if (nodeType == XMLStreamConstants.START_ELEMENT) {
					if (reader.getLocalName().equals("SIF_Message")) {
						// Special case for embedded SIF_Message envelopes
						if ((flags & SIFParser.FLG_EXPECT_INNER_ENVELOPE) != 0) {
							SIFElement msgElement = readSIFMessageElement(reader, dtd, zone, 0, version);
							currentElement.addChild(msgElement);
							currentElement = msgElement;
						} else {
							throw new ADKParsingException("Unexpected SIF_Message encountered in parsing", zone);
						}
					} else {
						xmlName = reader.getLocalName();
						ElementDef foundDef = lookupElementDef(currentElement, xmlName, dtd, version, zone);
						if (foundDef == null) {
							if (legacyParse) {
								nodeType = parseLegacyXML(reader, version, zone, currentElement, formatter, xmlName);
								continue;
							} else if (currentElement != null && currentElement.getElementDef().name().equals("XMLData")) {
								// Parse this into a DOM and set on the XMLData
								// element
								XMLNodeReader nestedReader = new XMLNodeReader(reader);
								org.w3c.dom.Document doc = XMLStreamDocumentBuilder.build(nestedReader, false, true, null);
								((XMLData) currentElement).setXML(doc);
								nodeType = reader.nextTag();
								continue;
							} else {
								String _tag = currentElement != null ? currentElement.getElementDef().name() + "/" + xmlName : xmlName;
								throw new SIFException(SIFErrorCategory.XML_VALIDATION, SIFErrorCodes.XML_GENERIC_VALIDATION_3, "Unknown element or attribute", _tag + " is not a recognized element of SIF " + version.toString(), zone);
							}
						}

						if (legacyParse) {
							ElementVersionInfo evi = foundDef.getVersionInfo(version);
							if (evi != null) {
								RenderSurrogate rs = evi.getSurrogate();
								if (rs != null) {
									try {
										if (rs.readRaw(reader, version, currentElement, formatter)) {
											nodeType = reader.getEventType();
											continue;
										} else {
											throw new SIFException(SIFErrorCategory.XML_VALIDATION, SIFErrorCodes.XML_GENERIC_VALIDATION_3, "Unknown element or attribute", reader.getLocalName() + " was not able to be parsed by " + rs, zone);
										}
									} catch (ADKTypeParseException atpe) {
										handleTypeParseException("Unable to parse value: " + atpe.getMessage(), atpe, zone);

										nodeType = reader.getEventType();
										continue;
									}
								}
							}

						}
						
						if (reader.getLocalName().equals("SIF_ExtendedElement") || foundDef.equals(CommonDTD.SIF_EXTENDEDELEMENT)) {
							readSIFExtendedElement(reader, dtd, zone, currentElement, version, formatter);
							nodeType = reader.getEventType();
							continue;
						} else if (foundDef.isField()) {
							setFieldValueFromElement(foundDef, currentElement, reader, version, formatter, zone);
							nodeType = reader.nextTag();
							continue;
						} else {
							currentElement = readSIFElementFromElementNode(foundDef, reader, version, dtd, currentElement, formatter, zone);
							// TODO: Empty repeatable fields, such as Email,
							// that contain no data are not getting set to nil.
							// Should treat <Email Type="Primary"/> equivalent
							// to <Email Type="Primary" xsi:nil="true"/>
						}
					}

				} else if (nodeType == XMLStreamConstants.END_ELEMENT) {
					if (currentElement.getElementDef().hasSimpleContent() && currentElement.getSIFValue() == null && currentElement.getChildCount() < 1) {
						SIFTypeConverter<?> defConverter = currentElement.getElementDef().getTypeConverter();
						if (defConverter == null) {
							defConverter = SIFTypeConverters.STRING;
						}
						
						SIFSimpleType<?> val = null;
						SimpleField<?> sifAction = currentElement.getField("SIF_Action");			
						if (sifAction != null && "Delete".equals(sifAction.getTextValue())) {
							val = defConverter.getSIFSimpleType(null);
						} else {
							val = defConverter.getSIFSimpleType("");
						}
						currentElement.setField(currentElement.getElementDef(), val);
					}
					if (currentElement.getParent() != null) {
						currentElement = (SIFElement) currentElement.getParent();
						while (legacyParse && currentElement.getElementDef().isCollapsed(version)) {
							currentElement = (SIFElement) currentElement.getParent();
						}
					}
					if (reader.getLocalName().equals("SIF_Message")) {
						// We need to return here. If we let the reader keep
						// reading, and we are reading an embedded
						// SIF_Message, it will keep parsing the end tags and
						// not let the stack of SIFElement objects
						// properly unwind. We're done anyway.
						break;
					}

					// Advance to the nexttag (faster than calling next())
					if (!reader.isEndElement()) {
						nodeType = reader.nextTag();
						continue;
					}
				}

				// Advance the cursor
				nodeType = reader.next();

			}

		} catch (XMLStreamException xse) {
			throw new ADKParsingException(xse.getLocalizedMessage(), zone, xse);
		}

		if (currentElement == null) {
			return null;
		} else {
			// Now, unwind and pop off the top element parsed
			Element top = currentElement;
			Element current;
			while ((current = top.getParent()) != null) {
				top = current;
			}
			return (SIFElement) top;
		}

	}

	protected int parseLegacyXML(XMLStreamReader reader, SIFVersion version, Zone zone, SIFElement currentElement, SIFFormatter formatter, String xmlName) throws SIFException {
		int nodeType;
		boolean handled = false;

		// Determine if any surrogate formatters that are defined as children
		// of the current element can resolve it
		// NOTE: Until we fix this in the ADK, elements from the common package
		// loose their
		// metadata information that was originally defined.
		ElementDef currentDef = currentElement.getElementDef();
		List<ElementDef> children = currentDef.getChildren();
		if (children == null || children.size() == 0) {
			// try to get the actual element def
			// WARNING! this is somewhat of a hack until
			// we get support for what we need in the ADK

			try {
				Class<?> actualElement = Class.forName(currentDef.getFQClassName());
				SIFElement copy = (SIFElement) actualElement.newInstance();
				children = copy.getElementDef().getChildren();
			} catch (Exception cnfe) {
				throw new SIFException(SIFErrorCategory.XML_VALIDATION, SIFErrorCodes.XML_GENERIC_VALIDATION_3, "Unable to parse" + xmlName + "  " + version.toString() + cnfe.getMessage(), zone);

			}
		}

		for (ElementDef candidate : children) {
			if (candidate.getEarliestVersion().compareTo(version) > 0) {
				continue;
			}
			RenderSurrogate rs = candidate.getVersionInfo(version).getSurrogate();
			if (rs != null) {
				try {
					if (rs.readRaw(reader, version, currentElement, formatter)) {
						handled = true;
						break;
					}
				} catch (ADKTypeParseException e) {
					handleTypeParseException("Unable to parse element or attribute value: " + e.getMessage(), e, zone);
					handled = true;
					break;
				} catch (ADKParsingException e) {
					throw new SIFException(SIFErrorCategory.XML_VALIDATION, SIFErrorCodes.XML_GENERIC_VALIDATION_3, "unable to parse xml: " + e.getMessage() + version.toString(), zone);
				}
			}
		}

		if (!handled) {
			String _tag = currentElement != null ? currentElement.getElementDef().name() + "/" + xmlName : xmlName;
			throw new SIFException(SIFErrorCategory.XML_VALIDATION, SIFErrorCodes.XML_GENERIC_VALIDATION_3, "Unknown element or attribute", _tag + " is not a recognized element of SIF " + version.toString(), zone);

		}
		nodeType = reader.getEventType();
		return nodeType;
	}

	protected SIFElement readSIFElementFromElementNode(ElementDef def, XMLStreamReader reader, SIFVersion version, DTD dtd, SIFElement parent, SIFFormatter formatter, Zone zone) throws ADKParsingException, SIFException, XMLStreamException {
		SIFElement element = null;

		try {
			element = SIFElement.create(parent, def);
		} catch (Exception e) {
			throw new ADKParsingException("Could not create an instance of '" + def.getFQClassName() + "' to wrap a " + reader.getLocalName() + " object. " + e, zone, e);
		}
		if (fParsed == null) {
			fParsed = element;
		}

		element.setElementDef(def);
		element.setSIFVersion(version);
		if (parent != null) {
			element = formatter.addChild(parent, element, version);
		}

		// Set the attributes to fields of the SIFElement
		int attrCount = reader.getAttributeCount();
		for (int a = 0; a < attrCount; a++) {
			setFieldValueFromAttribute(element, a, reader, dtd, version, formatter, zone);
		}

		return element;
	}

	/**
	 * Looks up an ElementDef representing the current XML Element or Attribute
	 * 
	 * @param parent
	 * @param reader
	 * @param dtd
	 * @param version
	 * @param zone
	 * @return
	 * @throws SIFException
	 */
	protected ElementDef lookupElementDef(SIFElement parent, String xmlName, DTD dtd, SIFVersion version, Zone zone) {
		// Lookup the ElementDef metadata in the SifDtd object for the
		// version of SIF we are parsing. First try looking up a ElementDef
		// for a field or complex object that is a child of another element,
		// such as StudentPersonal_Name, SIF_Ack_SIF_Header, etc. If none
		// found then look for a root-level element such as StudentPersonal,
		// SIF_Ack, etc. If still nothing is found we don't know how to
		// parse this element -- it is neither a top-level object element
		// nor a child field element for this version of SIF.
		String elementName = xmlName;
		ElementDef def = null;
		if (parent != null) {
			def = dtd.lookupElementDef(parent.getElementDef(), elementName);
		}
		if (def == null) {
			def = dtd.lookupElementDef(elementName);
		}

		// Beginning with SIF 1.5 *any* object can have a SIF_ExtendedElements
		// child, so we need to check for that case since the Adk metadata
		// does not add SIF_ExtendedElements to all object types
		if (def == null && elementName.equals("SIF_ExtendedElements")) {
			def = CommonDTD.SIF_EXTENDEDELEMENTS;
		}

		// Beginning with SIF 2.0 *any* object can have a SIF_ExtendedElements
		// child, so we need to check for that case since the Adk metadata
		// does not add SIF_ExtendedElements to all object types
		if (def == null && elementName.equals("SIF_Metadata")) {
			def = DatamodelDTD.SIF_METADATA;
		}

		/*
		 * JEN - todo Big Service Hack
		 * 
		 * @see
		 * com.edustructures.sifworks.DTD#lookupAnyElementDef(java.lang.String)
		 */
		if (def == null) {
			// def = dtd.lookupAnyElementDef(elementName);
			for (String defKey : SIFDTD.sElementDefs.keySet()) {
				if (defKey.contains(elementName)) {
					def = SIFDTD.sElementDefs.get(defKey);
					ADK.getLog().info("SIFDTD lookupAnyElementDef returned: " + defKey + " for " + elementName);
					break;
				}
			}
		}

		// Note: def returned can be null.
		return def;
	}

	/**
	 * Sets the value of a SimpleField from the contents of an Xml Attribute
	 * 
	 * @param element
	 * @param attributeIndex
	 * @param reader
	 * @param dtd
	 * @param formatter
	 * @param zone
	 * @throws ADKParsingException
	 * @throws SIFException
	 */
	private void setFieldValueFromAttribute(SIFElement element, int attributeIndex, XMLStreamReader reader, DTD dtd, SIFVersion version, SIFFormatter formatter, Zone zone) throws ADKParsingException, SIFException {

		ElementDef elementDef = element.getElementDef();
		QName attrName = reader.getAttributeName(attributeIndex);
		ElementDef field = dtd.lookupElementDef(elementDef, attrName.getLocalPart());
		if (field == null && attrName.getPrefix() != null) {
			if (SIFWriter.NIL.equals(attrName.getLocalPart()) && SIFWriter.XSI_NAMESPACE.equals(attrName.getNamespaceURI())) {
				SIFTypeConverter<?> converter = elementDef.getTypeConverter();
				if (converter != null) {
					SIFSimpleType<?> sst = converter.getSIFSimpleType(null);
					element.setField(elementDef, sst);
				}
				return;
			} else if ("xmlns".equals(reader.getPrefix())) {
				return;
			} else {

				field = dtd.lookupElementDef(elementDef, attrName.getPrefix() + ":" + attrName.getLocalPart());
			}
		}

		if (field != null) {
			String strVal = reader.getAttributeValue(attributeIndex);
			SIFSimpleType<?> val = parseValue(field, strVal, version, formatter, zone);
			element.setField(field, val);
		}

		else {
			throw new SIFException(SIFErrorCategory.XML_VALIDATION, SIFErrorCodes.XML_GENERIC_VALIDATION_3, "Unknown element or attribute", attrName + " is not a recognized attribute of the " + elementDef.name() + " element (SIF "
					+ element.effectiveSIFVersion().toString() + ")", zone);
		}

	}

	/**
	 * Sets the value of SimpleField from the contents of an XML Element and
	 * advances the reader to the END_ELEMENT tag
	 * 
	 * @param def
	 * @param element
	 * @param reader
	 * @param formatter
	 * @throws XMLStreamException
	 * @throws ADKParsingException
	 */
	protected void setFieldValueFromElement(ElementDef def, SIFElement element, XMLStreamReader reader, SIFVersion version, SIFFormatter formatter, Zone zone) throws XMLStreamException, SIFException {
		int nodeType = reader.getEventType();
		while (nodeType == XMLStreamConstants.START_ELEMENT) {
			// Look for the xsi:nill attribute that signals a null value
			int attrCount = reader.getAttributeCount();
			if (attrCount > 0) {
				for (int i = 0; i < attrCount; i++) {
					QName attrName = reader.getAttributeName(i);
					if (SIFWriter.NIL.equals(attrName.getLocalPart()) && SIFWriter.XSI_NAMESPACE.equals(attrName.getNamespaceURI())) {
						SIFTypeConverter<?> defConverter = def.getTypeConverter();
						if (defConverter == null) {
							defConverter = SIFTypeConverters.STRING;
						}
						SIFSimpleType<?> val = defConverter.getSIFSimpleType(null);
						element.setField(def, val);
						reader.nextTag();
						return;
					}
				}
			}
			nodeType = reader.next();
		}
		if (nodeType == XMLStreamConstants.CHARACTERS || nodeType == XMLStreamConstants.CDATA) {
			int textNodeType = nodeType;
			StringBuffer buf = new StringBuffer();
			while (nodeType == textNodeType) {
				buf.append(reader.getText());
				nodeType = reader.next();
			}
			String elementText = buf.toString();
			SIFSimpleType<?> data = parseValue(def, elementText, version, formatter, zone);

			// JEN
			try {
				formatter.setField(element, def, data, version);
			} catch (NumberFormatException e) {
				String errorMessage = "Unable to parse element or attribute '" + def.name() + "'" + e.getMessage() + " (SIF " + version.toString() + ")";
				ADKTypeParseException ex = new ADKTypeParseException(errorMessage, zone, e);
				handleTypeParseException(errorMessage, ex, zone);
			}
		} else if (nodeType == XMLStreamConstants.END_ELEMENT) {
			// Treat empty field values the same as xsi:nill attribute to signal
			// a null value
			SIFTypeConverter<?> defConverter = def.getTypeConverter();
			if (defConverter == null) {
				defConverter = SIFTypeConverters.STRING;
			}
			SIFSimpleType<?> val = defConverter.getSIFSimpleType(null);
			element.setField(def, val);
		}
	}

	private SIFSimpleType<?> parseValue(ElementDef def, String value, SIFVersion version, SIFFormatter formatter, Zone zone) throws SIFException {
		try {
			SIFTypeConverter<?> converter = def.getTypeConverter();
			if (converter == null) {
				// TODO: Should we not allow this in "STRICT" mode?
				converter = SIFTypeConverters.STRING;
			}
			return converter.parse(formatter, value);
		} catch (ADKTypeParseException pe) {
			String errorMessage = "Unable to parse element or attribute '" + def.name() + "'" + pe.getMessage() + " (SIF " + version.toString() + ")";
			handleTypeParseException(errorMessage, pe, zone);
			return null;
		}
	}

	/**
	 * Evaluates the ADK StrictTypeParsing property to determine if a
	 * SIFException should be thrown for a failed parse
	 * 
	 * @param errorMessage
	 * @param pe
	 * @param zone
	 * @throws SIFException
	 */
	protected void handleTypeParseException(String errorMessage, ADKTypeParseException pe, Zone zone) throws SIFException {
		Logger log = ADK.getLog();
		if (zone != null) {
			log = zone.getLog();
			if (zone.getProperties().getStrictTypeParsing()) {
				throw new SIFException(SIFErrorCategory.XML_VALIDATION, SIFErrorCodes.XML_INVALID_VALUE_4, errorMessage, zone, pe);
			}
		}
		if ((ADK.debug & ADK.DBG_EXCEPTIONS) > 0) {
			log.warn(errorMessage, pe);
		}
	}
	
	
	//} else if (currentElement != null && currentElement.getElementDef().name().equals("SIF_ExtendedElement")) {
	//	XMLNodeReader nestedReader = new XMLNodeReader(reader);
	//	org.w3c.dom.Document doc = XMLStreamDocumentBuilder.build(nestedReader, false, true, null);
	//	((SIF_ExtendedElement) currentElement).setXML(doc);
	//	nodeType = reader.nextTag();
	//	continue;
	
	/**
	 * Reads a SIF_ExtendedElement element, handling arbitrary xml children and other special cases 
	 *
	 * @param reader
	 * @param dtd
	 * @param zone
	 * @param flags
	 * @param defaultVersion
	 * @return
	 * @throws XMLStreamException 
	 */
	protected SIFElement readSIFExtendedElement(XMLStreamReader reader, DTD dtd, Zone zone, SIFElement parent, SIFVersion version, SIFFormatter formatter) throws ADKParsingException, SIFException, XMLStreamException {
		SIF_ExtendedElement see = (SIF_ExtendedElement)readSIFElementFromElementNode(CommonDTD.SIF_EXTENDEDELEMENT, reader, version, dtd, parent, formatter, zone);
		XMLNodeReader nestedReader = new XMLNodeReader(reader);
		org.w3c.dom.Document doc = XMLStreamDocumentBuilder.build(nestedReader, true, true, null);
		see.setXML((org.w3c.dom.Element)doc.getFirstChild());
		
		return see;
	}
}
