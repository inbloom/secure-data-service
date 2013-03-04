package org.slc.sli.ingestion.streaming;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

@Component
public class StreamingLoader {
    public static final Logger LOG = LoggerFactory.getLogger(StreamingLoader.class);

    @Resource(name = "mongoEntityRepository")
    private Repository<Entity> repo;

    @Resource
    private TypeProvider tp;

    @Resource
    private VersionAwareAdapter va;

    @Resource
    private ReferenceValidator ref;

    public void process(Reader reader) throws XMLStreamException {
        XMLEventReader r = XMLInputFactory.newInstance().createXMLEventReader(reader);

        XMLEvent root;
        while ((root = r.nextEvent()).getEventType() != XMLEvent.START_ELEMENT) {
            ;
        }
        String rootNodeName = extractName(root);

        StopWatch sw = new StopWatch();
        while (true) {
            XMLEvent e = r.nextEvent();

            if (e.getEventType() == XMLEvent.END_ELEMENT && extractName(e).equals(rootNodeName)) {
                break;
            } else if (e.getEventType() == XMLEvent.START_ELEMENT && tp.isComplexType(extractName(e))) {
                Map<String, Object> entity = parseMap(r, extractName(e));
                sw.start();
                repo.create(extractName(e), va.adapt(entity));
                sw.stop();
                LOG.debug("Saved entity: {}",extractName(e));
            }
        }

        LOG.info("Total mongo time:" + sw.getTotalTimeMillis());
    }

    @SuppressWarnings({ "unchecked", "serial" })
    private Map<String, Object> parseMap(XMLEventReader r, String nodeName) throws XMLStreamException {
        Map<String, Object> result = new HashMap<String, Object>() {

            @Override
            public Object put(String key, Object value) {
                Object result;
                if(this.containsKey(key)) {
                    Object stored = this.get(key);
                    if(List.class.isAssignableFrom(stored.getClass())) {
                        List<Object> storage = (List<Object>) stored;
                        storage.add(value);
                        result = storage;
                    }
                    else
                    {
                        result = super.put(key, new ArrayList<Object>(Arrays.asList(stored,value)));
                    }
                }
                else {
                    result = super.put(key, value);
                }
                return result;
            }

        };

        while (true) {
            XMLEvent e = r.nextEvent();

            if (e.getEventType() == XMLEvent.START_ELEMENT) {
                String elementName = extractName(e);
                Iterator<Attribute> it = e.asStartElement().getAttributes();

                while (it.hasNext()) {
                    Attribute a = it.next();
                    result.put(a.getName().getLocalPart(), a.getValue());
                }

                LOG.debug("Processing: {}", elementName);
                if (tp.isComplexType(elementName)) {
                    result.put(elementName, parseMap(r, elementName));
                } else {
                    String value = r.getElementText();
                    result.put(elementName, tp.convertType(elementName, value));

                    if (tp.isReference(elementName)) {
                        ref.addForValidation(elementName, value);
                    }
                }
            }

            if (e.getEventType() == XMLEvent.END_ELEMENT && extractName(e).equals(nodeName)) {
                break;
            }
        }

        return result;
    }

    private String extractName(XMLEvent e) {
        String result = "";
        if (e.getEventType() == XMLEvent.END_ELEMENT) {
            result = lowerCaseFirst(e.asEndElement().getName().getLocalPart());
        } else if (e.getEventType() == XMLEvent.START_ELEMENT) {
            result = lowerCaseFirst(e.asStartElement().getName().getLocalPart());
        }

        return result;
    }

    private String lowerCaseFirst(String string) {
        return Character.toLowerCase(string.charAt(0)) + (string.length() > 1 ? string.substring(1) : "");
    }

}
