//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Wraps an XMLStreamReader and reads only the current element and it's children
 * @author Andrew Elmhorst
 * @version 2.1
 *
 */
public class XMLNodeReader implements XMLStreamReader {

	
	/**
	 * The underlying reader that is being read from
	 */
	private XMLStreamReader fReader;
	
	
	/**
	 * The current element depth in the source document, relative to the 
	 * start position. For each child start element, this will be incremented and then
	 * decremented for each end element. When it reaches zero, reading of the 
	 * node is complete
	 * 
	 * The special value of -99 means that next() has not yet been called;
	 */
	private int fDepth = -99;
	
	
	/**
	 * Creates a new instance of an XMLNodeReader
	 * @param wrappedReader An XMLStreamReader position on an XML Start Tag
	 * @throws XMLStreamException If the XML Reader is not position on a start tag
	 */
	public XMLNodeReader( XMLStreamReader wrappedReader )
		throws XMLStreamException
	{
		if(wrappedReader.getEventType() != XMLStreamReader.START_ELEMENT ){
			throw new XMLStreamException( "XMLStreamReader must be position on the Start Element." );
		}
		
		fReader = wrappedReader;
		
		
	}
	
	
	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#close()
	 */
	public void close() throws XMLStreamException {
		// Do nothing

	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getAttributeCount()
	 */
	public int getAttributeCount() {
		return fReader.getAttributeCount();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getAttributeLocalName(int)
	 */
	public String getAttributeLocalName(int arg0) {
		return fReader.getAttributeLocalName( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getAttributeName(int)
	 */
	public QName getAttributeName(int arg0) {
		return fReader.getAttributeName( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getAttributeNamespace(int)
	 */
	public String getAttributeNamespace(int arg0) {
		return fReader.getAttributeNamespace( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getAttributePrefix(int)
	 */
	public String getAttributePrefix(int arg0) {
		return fReader.getAttributePrefix( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getAttributeType(int)
	 */
	public String getAttributeType(int arg0) {
		return fReader.getAttributeType( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getAttributeValue(int)
	 */
	public String getAttributeValue(int arg0) {
		return fReader.getAttributeValue( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getAttributeValue(java.lang.String, java.lang.String)
	 */
	public String getAttributeValue(String arg0, String arg1) {
		return fReader.getAttributeValue( arg0, arg1 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getCharacterEncodingScheme()
	 */
	public String getCharacterEncodingScheme() {
		return fReader.getCharacterEncodingScheme();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getElementText()
	 */
	public String getElementText() throws XMLStreamException {
		return fReader.getElementText();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getEncoding()
	 */
	public String getEncoding() {
		return fReader.getEncoding();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getEventType()
	 */
	public int getEventType() {
		return fReader.getEventType();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getLocalName()
	 */
	public String getLocalName() {
		return fReader.getLocalName();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getLocation()
	 */
	public Location getLocation() {
		return fReader.getLocation();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getName()
	 */
	public QName getName() {
		return fReader.getName();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceContext()
	 */
	public NamespaceContext getNamespaceContext() {
		return fReader.getNamespaceContext();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceCount()
	 */
	public int getNamespaceCount() {
		return fReader.getNamespaceCount();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getNamespacePrefix(int)
	 */
	public String getNamespacePrefix(int arg0) {
		return fReader.getNamespacePrefix( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceURI()
	 */
	public String getNamespaceURI() {
		return fReader.getNamespaceURI();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceURI(java.lang.String)
	 */
	public String getNamespaceURI(String arg0) {
		return fReader.getNamespaceURI( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceURI(int)
	 */
	public String getNamespaceURI(int arg0) {
		return fReader.getNamespaceURI( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getPIData()
	 */
	public String getPIData() {
		return fReader.getPIData();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getPITarget()
	 */
	public String getPITarget() {
		return fReader.getPITarget();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getPrefix()
	 */
	public String getPrefix() {
		return fReader.getPrefix();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getProperty(java.lang.String)
	 */
	public Object getProperty(String arg0) throws IllegalArgumentException {
		return fReader.getProperty( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getText()
	 */
	public String getText() {
		return fReader.getText();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getTextCharacters()
	 */
	public char[] getTextCharacters() {
		return fReader.getTextCharacters();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getTextCharacters(int, char[], int, int)
	 */
	public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3)
			throws XMLStreamException {
		return fReader.getTextCharacters( arg0, arg1, arg2, arg3 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getTextLength()
	 */
	public int getTextLength() {
		return fReader.getTextLength();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getTextStart()
	 */
	public int getTextStart() {
		return fReader.getTextStart();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getVersion()
	 */
	public String getVersion() {
		return fReader.getVersion();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#hasName()
	 */
	public boolean hasName() {
		return fReader.hasName();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#hasNext()
	 */
	public boolean hasNext() throws XMLStreamException {
		return fReader.hasNext() && (fDepth > 0 || fDepth == -99 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#hasText()
	 */
	public boolean hasText() {
		return fReader.hasText();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#isAttributeSpecified(int)
	 */
	public boolean isAttributeSpecified(int arg0) {
		return fReader.isAttributeSpecified( arg0 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#isCharacters()
	 */
	public boolean isCharacters() {
			return fReader.isCharacters();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#isEndElement()
	 */
	public boolean isEndElement() {
		return fReader.isEndElement();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#isStandalone()
	 */
	public boolean isStandalone() {
		return fReader.isStandalone();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#isStartElement()
	 */
	public boolean isStartElement() {
		return fReader.isStartElement();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#isWhiteSpace()
	 */
	public boolean isWhiteSpace() {
		return fReader.isWhiteSpace();
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#next()
	 */
	public int next() throws XMLStreamException {
		if( fDepth == -99 ){
			fDepth = 1;
			return XMLStreamReader.START_ELEMENT;
		}
		int eventType = fReader.next();
		return evaluateNext( eventType );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#nextTag()
	 */
	public int nextTag() throws XMLStreamException {
		
		if( fDepth == -99 ){
			fDepth = 1;
			return XMLStreamReader.START_ELEMENT;
		}
		
		int eventType = fReader.nextTag();
		return evaluateNext( eventType );
	}
	
	
	/**
	 * Evaluates the event type returned by the underlying reader.
	 * @param nextEventType
	 * @return 
	 * @throws XMLStreamException 
	 */
	private int evaluateNext( int nextEventType ) throws XMLStreamException{
		switch (nextEventType ){
			case XMLStreamReader.START_ELEMENT:
				fDepth++;
				break;
			case XMLStreamReader.END_ELEMENT:
				fDepth--;
				break;
		}
		
		if( fDepth < 1){
			// Advance the cursor to the next start element, but return that we're done
			fReader.nextTag();
			return XMLStreamReader.END_DOCUMENT;
		}
		return nextEventType;
	}
	

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#require(int, java.lang.String, java.lang.String)
	 */
	public void require(int arg0, String arg1, String arg2)
			throws XMLStreamException {
		fReader.require( arg0, arg1, arg2 );
	}

	/* (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#standaloneSet()
	 */
	public boolean standaloneSet() {
		return fReader.standaloneSet();
	}

}
