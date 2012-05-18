package org.slc.sli.ingestion.smooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.milyn.delivery.sax.SAXElement;
import org.milyn.event.ExecutionEvent;
import org.milyn.event.ExecutionEventListener;
import org.milyn.event.types.ElementPresentEvent;
import org.milyn.event.types.ResourceTargetingEvent;

import org.slc.sli.ingestion.validation.ErrorReport;


/**
 * Non-silent error report class reports elements/attributes that were ignored while processing data.
 *
 * @author okrook
 *
 */
public class NonSilentErrorReport implements ExecutionEventListener {

    private List<String> eventsPresent = new ArrayList<String>();
    private Set<String> eventsResourceTarget = new HashSet<String>();
    private Map<String, List<String>> attributesPresent = new HashMap<String, List<String>>();
    private Map<String, List<String>> attributesProcessed = new HashMap<String, List<String>>();

    @Override
    public void onEvent(ExecutionEvent event) {

            processEvent(event);

    }

    protected void processEvent(ExecutionEvent event) {

        if (event instanceof ElementPresentEvent) {

            ElementPresentEvent newEvent = (ElementPresentEvent) event;
            SAXElement element = (SAXElement) newEvent.getElement();
            String elementName = element.getName().toString();
            eventsPresent.add(elementName);
            int size = element.getAttributes().getLength();

            List<String> atts = new ArrayList<String>();
            for (int pos = 0; pos < size; pos++) {
                atts.add(element.getAttributes().getLocalName(pos));
            }
            if (!atts.isEmpty()) {
                attributesPresent.put(elementName, atts);
            }

        } else if (event instanceof ResourceTargetingEvent) {

            ResourceTargetingEvent newEvent = (ResourceTargetingEvent) event;
            String targetAttribute = newEvent.getResourceConfig().getTargetAttribute();
            SAXElement element = (SAXElement) newEvent.getElement();
            SAXElement parent = element.getParent();
            String elementName = element.getName().toString();

            if (targetAttribute != null) {
                List<String> currentAtt = attributesProcessed.get(element);
                if (currentAtt != null) {
                    currentAtt.add(targetAttribute);
                } else {
                    currentAtt = new ArrayList<String>();
                    currentAtt.add(targetAttribute);
                }
                attributesProcessed.put(elementName, currentAtt);
            }
            eventsResourceTarget.add(elementName);

            if (parent != null) {
                eventsResourceTarget.add(parent.getName().toString());
            }
        }
    }

    public void reportSilentErrors(ErrorReport errorReport) {

        List<String> unProcessedElements = generateIgnoredElementsCollection();

        for (String element : unProcessedElements) {
            errorReport.warning("Element " + element + " has not been processed", this);
        }

        Map<String, List<String>> unProcessedAttributes = generateIgnoredAttributesCollection();

        for (Entry<String, List<String>> entry : unProcessedAttributes.entrySet()) {

            errorReport.warning("Attribute(s) " + entry.getValue() + " for element " + entry.getKey()
                    + " have not been processed", this);
        }
    }

    private List<String> generateIgnoredElementsCollection() {
        List<String> unProcessedElements = eventsPresent;

        for (String element : eventsResourceTarget) {
            unProcessedElements.remove(element);
        }

        return unProcessedElements;
    }

    private Map<String, List<String>> generateIgnoredAttributesCollection() {
        Map<String, List<String>> ignoredAttributes = attributesPresent;

        for (Entry<String, List<String>> entry : attributesProcessed.entrySet()) {
            List<String> atts = attributesPresent.get(entry.getKey());
            List<String> attProcessed = entry.getValue();

            for (String attribute : attProcessed) {
                atts.remove(attribute);
            }
            ignoredAttributes.put(entry.getKey().toString(), atts);
        }

        return ignoredAttributes;
    }

    public Set<String> getEventsResourceTarget() {
        return eventsResourceTarget;
    }

    public List<String> getEventsPresent() {
        return eventsPresent;
    }

    public Map<String, List<String>> getAttributesPresent() {
        return attributesPresent;
    }

    public Map<String, List<String>> getAttributesProcessed() {
        return attributesProcessed;
    }

}
