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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.util.LRUCache;

public class StAXAntPathJAXBIterator extends StAXAntPathIterator<List<?>> {
    private static LRUCache<String, JAXBContext> classCache = new LRUCache<String, JAXBContext>(256);

    private String packageName;

    public StAXAntPathJAXBIterator() {
    }

    protected List<?> grabContent() throws XMLStreamException {
        final List<EdFiEntity> entities = new ArrayList<EdFiEntity>();

        try {
            boolean retrieved = retrieveContent(new ContentRetriever() {

                @Override
                public void retrieve() throws XMLStreamException {
                    EdFiEntity entity;
                    try {
                        XMLEvent currentEvent = getReader().peek();
                        String tagName = currentEvent.asStartElement().getName().getLocalPart();

                        Map.Entry<Unmarshaller, Class<?>> entry = getUnmarshallerByTagName(tagName);

                        getReader().peek().getLocation();

                        Object answer = entry.getKey().unmarshal(getReader(), entry.getValue());
                        if (answer != null && answer.getClass() == JAXBElement.class) {
                            JAXBElement<?> jbe = (JAXBElement<?>) answer;
                            answer = jbe.getValue();
                        }

                        entity = new EdFiEntity(currentEvent.getLocation(), answer);
                    } catch (JAXBException e) {
                        throw new RuntimeCamelException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeCamelException(e);
                    }

                    entities.add(entity);
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

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
