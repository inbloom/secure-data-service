package test.camel.support.stax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final JAXBContext jaxb;
    private final Unmarshaller unmarshaller;
    private LRUCache<String, Class<?>> classCache = new LRUCache<String, Class<?>>(1024);

    public StAXAntPathJAXBIterator(XMLEventReader reader, String antPath, int group) {
        super(reader, antPath, group);

        try {
            jaxb = JAXBContext.newInstance(packageName);
            unmarshaller = jaxb.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException();
        }
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

                        answer = unmarshaller.unmarshal(getReader(), getClassByTagName(tagName));
                        if (answer != null && answer.getClass() == JAXBElement.class) {
                            JAXBElement<?> jbe = (JAXBElement<?>) answer;
                            answer = jbe.getValue();
                        }
                    } catch (JAXBException e) {
                        throw new RuntimeCamelException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeCamelException(e);
                    }

                    entities.add(answer);
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

    private Class<?> getClassByTagName(String tagName) throws ClassNotFoundException {
        if (classCache.containsKey(tagName)) {
            return classCache.get(tagName);
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Class<?> clazz = cl.loadClass(packageName + "." + tagName);

        synchronized (classCache) {
            if (!classCache.containsKey(tagName)) {
                classCache.put(tagName, clazz);
            }
        }

        return clazz;
    }
}
