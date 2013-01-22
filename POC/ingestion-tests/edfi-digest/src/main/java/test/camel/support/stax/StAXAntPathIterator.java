package test.camel.support.stax;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.camel.core.xml.scan.AntPathMatcher;

public abstract class StAXAntPathIterator<T> implements Iterator<T>, Closeable {
	protected static final XMLOutputFactory OUTPUT_FACTORY = XMLOutputFactory
			.newInstance();
	protected static final XMLEventFactory EVENT_FACTORY = XMLEventFactory
			.newInstance();
	protected static final Characters NEW_LINE_EVENT = EVENT_FACTORY
			.createCharacters("\n");

	private final XMLEventReader reader;
	private final String antPath;
	private final int group;
	private T snippet;

	private AntPathMatcher antPathMatcher = new AntPathMatcher();
	private Stack<StartElement> parents = new Stack<StartElement>();

	StAXAntPathIterator(XMLEventReader reader, String antPath, int group) {
		this.reader = reader;
		this.antPath = antPath;
		this.group = group;
	}

	public void close() throws IOException {
		try {
			reader.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	public boolean hasNext() {
		if (snippet == null) {
			snippet = getSnippet();
		}

		return snippet != null;
	}

	public T next() {
		T next = null;

		if (snippet == null) {
			next = getSnippet();
		} else {
			next = snippet;
			snippet = null;
		}

		return next;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	private T getSnippet() {

		try {
			return grabContent();
		} catch (XMLStreamException e) {
			return null;
		}
	}

	protected XMLEventReader getReader() {
		return reader;
	}

	protected abstract T grabContent() throws XMLStreamException;

	protected boolean retrieveContent(ContentRetriever retriever) throws XMLStreamException {
		int count = groupBy();

		while (reader.hasNext()) {
			XMLEvent tmp = reader.peek();

			if (tmp.isStartElement() && isTargeted(tmp)) {
				if (count-- > 0) {
					retriever.retrieve();
				} else {
					break;
				}
			} else {
				if (tmp.isStartElement()) {
					parents.add(tmp.asStartElement());
				} else if (tmp.isEndElement()) {
					parents.pop();
				}

				reader.nextEvent();
			}
		}

		return count != groupBy();
	}

	protected final boolean isTargeted(XMLEvent event) {
		return antPathMatcher.match(antPath, getCurrentXPath(event));
	}

	private final String getCurrentXPath(XMLEvent event) {
		StringBuffer sb = new StringBuffer();

		for (StartElement start : this.parents) {
			sb.append('/').append(start.getName().getLocalPart());
		}

		sb.append('/').append(event.asStartElement().getName().getLocalPart());

		return sb.toString();
	}

	protected void writeContent(final XMLEventWriter writer) throws XMLStreamException {

		iterateContent(new ContentIterator() {

			@Override
			public void visit(XMLEvent xmlEvent) throws XMLStreamException {
				writer.add(xmlEvent);
			}
		});
	}

	protected int groupBy() {
		return group;
	}

	protected void iterateContent(ContentIterator contentIterator) throws XMLStreamException {
		Stack<StartElement> parents = new Stack<StartElement>();

		do {
			XMLEvent xmlEvent = reader.nextEvent();

			if (xmlEvent.isStartElement()) {
				parents.add(xmlEvent.asStartElement());
			} else if (xmlEvent.isEndElement()) {
				parents.pop();
			}

			if (!(xmlEvent.isCharacters() && xmlEvent.asCharacters()
					.isWhiteSpace())) {
				contentIterator.visit(xmlEvent);
			}
		} while (parents.size() > 0 && reader.hasNext());

		contentIterator.visit(NEW_LINE_EVENT);
	}

	protected static void closeQuietly(XMLEventWriter writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (XMLStreamException e) {
				writer = null;
			}
		}
	}
}