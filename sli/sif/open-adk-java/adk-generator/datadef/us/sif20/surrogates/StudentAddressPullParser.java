package openadk.library.impl.surrogates;

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
import openadk.library.SIFElement;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;
import openadk.library.SIFFormatter;
import openadk.library.SIFParser;
import openadk.library.SIFVersion;
import openadk.library.Zone;
import openadk.library.common.XMLData;
import openadk.library.impl.SIFPullParser;
import openadk.util.XMLNodeReader;
import openadk.util.XMLStreamDocumentBuilder;

public class StudentAddressPullParser extends SIFPullParser {
	public SIFElement parseOneElementFromStream(XMLStreamReader reader, SIFVersion version, DTD dtd, Zone zone, int flags) throws ADKParsingException, SIFException {

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

						if (foundDef.isField()) {
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
					if ( currentElement.getParent() == null ) {
						return currentElement;
					} else  {
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
	
}
