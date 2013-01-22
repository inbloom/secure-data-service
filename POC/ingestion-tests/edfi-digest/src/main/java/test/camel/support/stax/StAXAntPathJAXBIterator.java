package test.camel.support.stax;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.util.LRUCache;

public class StAXAntPathJAXBIterator extends StAXAntPathIterator<List<?>> {
    private final String packageName = "org.ed_fi._0100";
    private LRUCache<String, JAXBContext> classCache = new LRUCache<String, JAXBContext>(256);

    public StAXAntPathJAXBIterator(XMLEventReader reader, String antPath, int group) {
        super(reader, antPath, group);
    }

    protected List<?> grabContent() throws XMLStreamException {
        final List<Object> entities = new ArrayList<Object>();

        try {
            boolean retrieved = retrieveContent(new ContentRetriever() {

                @Override
                public void retrieve() throws XMLStreamException {
                    Object answer;
                    try {
                        String tagName = getReader().peek().asStartElement().getName().getLocalPart();

                        Map.Entry<Unmarshaller, Class<?>> entry = getUnmarshallerByTagName(tagName);

                        answer = entry.getKey().unmarshal(getReader(), entry.getValue());
                        if (answer != null && answer.getClass() == JAXBElement.class) {
                            JAXBElement<?> jbe = (JAXBElement<?>) answer;
                            answer = jbe.getValue();
                        }
                    } catch (JAXBException e) {
                        throw new RuntimeCamelException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeCamelException(e);
                    }

                    answer = null;
                    //entities.add(answer);
                }
            });

            if (!retrieved) {
                throw new IOException("No more elements");
            }

            return entities;
        } catch (IOException e) {
            return null;
        }
    }

    private Map.Entry<Unmarshaller, Class<?>> getUnmarshallerByTagName(String tagName) throws ClassNotFoundException {
        JAXBContext jaxb;

        if (classCache.containsKey(tagName)) {
            jaxb = classCache.get(tagName);
        } else {

            try {
                jaxb = JAXBContext.newInstance(packageName);
            } catch (JAXBException e) {
                throw new RuntimeCamelException(e);
            }

            synchronized (classCache) {
                if (!classCache.containsKey(tagName)) {
                    classCache.put(tagName, jaxb);
                }
            }
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Class<?> clazz = cl.loadClass(packageName + "." + tagName);

        Map.Entry<Unmarshaller, Class<?>> entry = null;

        try {
            entry = new AbstractMap.SimpleEntry<Unmarshaller, Class<?>>(jaxb.createUnmarshaller(), clazz);
        } catch (JAXBException e) {
            throw new RuntimeCamelException(e);
        }

        return entry;
    }
}
