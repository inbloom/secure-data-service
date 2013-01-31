package test.camel.support.stax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StAXAntPathMapIterator extends StAXAntPathIterator<List<?>> {

    public StAXAntPathMapIterator() {
    }

    protected List<?> grabContent() throws XMLStreamException {
        final List<Map<String, Object>> content = new ArrayList<Map<String, Object>>();

        try {
            boolean retrieved = retrieveContent(new ContentRetriever() {
                @Override
                public void retrieve() throws XMLStreamException {
                    final Stack<Map<String, Object>> dataStack = new Stack<Map<String, Object>>();

                    iterateContent(new ContentIterator() {

                        @SuppressWarnings("unchecked")
                        @Override
                        public void visit(XMLEvent xmlEvent) throws XMLStreamException {
                            if (xmlEvent.isStartElement()) {
                                Map<String, Object> data = new HashMap<String, Object>();

                                String tag = xmlEvent.asStartElement().getName().getLocalPart();

                                if (dataStack.size() == 0) {
                                    content.add(data);

                                    data.put("entityType", tag);
                                    data.put("line", xmlEvent.getLocation().getLineNumber());
                                    data.put("column", xmlEvent.getLocation().getColumnNumber());
                                }

                                collectAttributes(xmlEvent.asStartElement(), data);

                                if (dataStack.size() != 0) {
                                    Map<String, Object> currentObject = dataStack.peek();

                                    if (currentObject.containsKey(tag)) {

                                        List<Object> collection;
                                        Object elementObj = currentObject.get(tag);

                                        if (elementObj instanceof List) {
                                            collection = (List<Object>) elementObj;
                                            collection.add(data);
                                        } else {
                                            collection = new ArrayList<Object>();
                                            collection.add(elementObj);
                                            collection.add(data);

                                            currentObject.put(tag, collection);
                                        }
                                    } else {
                                        currentObject.put(tag, data);
                                    }
                                }

                                dataStack.push(data);
                            } else if (xmlEvent.isCharacters()) {
                                if (dataStack.size() != 0) {
                                    collectEventInfo(xmlEvent.asCharacters(), dataStack.peek());
                                }
                            } else if (xmlEvent.isEndElement()) {
                                dataStack.pop();
                            }
                        }
                    });
                }

                private void collectAttributes(StartElement event, Map<String, Object> data) {
                    @SuppressWarnings("unchecked")
                    Iterator<Attribute> attrs = event.getAttributes();

                    while (attrs.hasNext()) {
                        Attribute attr = attrs.next();

                        data.put(attr.getName().getLocalPart(), attr.getValue());
                    }
                }

                private void collectEventInfo(Characters characters, Map<String, Object> data) {
                    if (characters.isIgnorableWhiteSpace() || characters.isWhiteSpace()) {
                        return;
                    }

                    String text = characters.getData();

                    if (data.containsKey("_value")) {
                        String oldText = (String) data.get("_value");
                        text = oldText + text;
                    }

                    data.put("_value", text);
                }
            });

            if (!retrieved) {
                throw new IOException("No more elements");
            }

            return content;
        } catch (IOException e) {
            return null;
        }
    }
}
