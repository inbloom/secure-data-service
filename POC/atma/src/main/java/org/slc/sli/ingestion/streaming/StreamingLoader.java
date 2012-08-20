package org.slc.sli.ingestion.streaming;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.stereotype.Component;

@Component
public class StreamingLoader {

	@Resource(name = "mongoEntityRepository")
	private Repository<Entity> repo;

	@Resource
	private TypeProvider tp;

	public void process(Reader reader) throws XMLStreamException {
		XMLEventReader r = XMLInputFactory.newInstance().createXMLEventReader(reader);

		String rootNodeName = "InterchangeBulk";

		while (true) {
			XMLEvent e = r.nextEvent();

			if (e.getEventType() == XMLEvent.END_ELEMENT && e.asEndElement().getName().getLocalPart().equals(rootNodeName)) {
				break;
			} else if (e.getEventType() == XMLEvent.START_ELEMENT && tp.isComplexType(e.asStartElement().getName().getLocalPart())) {
				Map<String, Object> entity = parseMap(r, e.asStartElement().getName().getLocalPart());
				repo.create(e.asStartElement().getName().getLocalPart(), entity);
			}
		}
	}

	private Map<String, Object> parseMap(XMLEventReader r, String nodeName) throws XMLStreamException {
		Map<String, Object> result = new HashMap<String, Object>();

		while (true) {
			XMLEvent e = r.nextEvent();

			if (e.getEventType() == XMLEvent.START_ELEMENT) {
				String elementName = e.asStartElement().getName().getLocalPart();

				System.out.println("Processing: " + elementName);
				if (tp.isComplexType(elementName)) {
					result.put(elementName, parseMap(r, elementName));
				} else {
					result.put(elementName, tp.convertType(elementName, r.getElementText()));
				}
			}

			if (e.getEventType() == XMLEvent.END_ELEMENT && e.asEndElement().getName().getLocalPart().equals(nodeName)) {
				break;
			}
		}

		return result;
	}

}
