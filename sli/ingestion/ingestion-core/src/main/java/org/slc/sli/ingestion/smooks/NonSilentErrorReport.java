package org.slc.sli.ingestion.smooks;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.milyn.delivery.sax.SAXElement;
import org.milyn.event.ExecutionEvent;
import org.milyn.event.ExecutionEventListener;
import org.milyn.event.types.ElementPresentEvent;
import org.milyn.event.types.FilterLifecycleEvent;
import org.milyn.event.types.FilterLifecycleEvent.EventType;
import org.milyn.event.types.ResourceTargetingEvent;
import org.xml.sax.Attributes;

import org.slc.sli.ingestion.validation.ErrorReport;


/**
 * Non-silent error report class reports elements/attributes that were ignored while processing data.
 *
 * @author okrook
 *
 */
public class NonSilentErrorReport implements ExecutionEventListener {

    private ErrorReport errorReport;
    private Stack<ElementState> processedElements = new Stack<ElementState>();

    private final class ElementState {
        SAXElement element;
        Set<String> targetedAttributes;
        int sequence;
        boolean isTargeted;

        ElementState(SAXElement element, int sequence) {
            this.element = element;
            this.targetedAttributes = new HashSet<String>();
            this.sequence = sequence;
            this.isTargeted = false;
        }
    }

    public NonSilentErrorReport(ErrorReport errorReport) {
        this.errorReport = errorReport;
    }

    @Override
    public void onEvent(ExecutionEvent event) {

        if (event instanceof ElementPresentEvent) {

            ElementPresentEvent presentEvent = (ElementPresentEvent) event;

            SAXElement element = (SAXElement) presentEvent.getElement();

            int sequence = 0;

            ElementState last = getLastElementState();
            while (last != null && !last.element.equals(element.getParent())) {
                reportErrors();

                processedElements.pop();

                last = getLastElementState();
            }

            if (last != null && last.element.getName().equals(element.getName())) {
                reportErrors();

                sequence = last.sequence + 1;
            }

            processedElements.add(new ElementState(element, sequence));

        } else if (event instanceof ResourceTargetingEvent) {

            ResourceTargetingEvent targetingEvent = (ResourceTargetingEvent) event;
            SAXElement element = (SAXElement) targetingEvent.getElement();

            ElementState last = getLastElementState();

            if (last != null) {
                if (!last.element.equals(element)) {
                    PrintStream ps = System.out;
                    ps.printf("Unexpected element - expected: %s, actiual: %s", last.element.getName().getLocalPart(), element.getName().getLocalPart());
                    ps.println();
                }

                String targetAttribute = targetingEvent.getResourceConfig().getTargetAttribute();
                if (targetAttribute != null) {
                    last.targetedAttributes.add(targetAttribute);
                }

                last.isTargeted = true;
            }
        } else if (event instanceof FilterLifecycleEvent) {

            FilterLifecycleEvent lifecycleEvent = (FilterLifecycleEvent) event;

            if (lifecycleEvent.getEventType() == EventType.FINISHED) {
                reportErrors();
            }
        }
    }

    private ElementState getLastElementState() {
        if (processedElements.size() > 0) {
            return processedElements.peek();
        } else {
            return null;
        }
    }

    private void reportErrors() {
        ElementState last = getLastElementState();

        if (last == null) {
            return;
        }

        if (!last.isTargeted) {
            String message = String.format("[%s]: element was not processed", getXPath(processedElements));
            errorReport.warning(message, this);
            PrintStream ps = System.out;
            ps.println(message);
        } else {
            Set<String> ignoredAttributes = getIgnoredAttributes(last);

            String xPath = getXPath(processedElements);

            PrintStream ps = System.out;
            for (String attribute : ignoredAttributes) {
                String message = String.format("[%s]: [%s] attribute was not processed", xPath, attribute);
                errorReport.warning(message, this);
                ps.println(message);
            }
        }
    }

    private static Set<String> getIgnoredAttributes(ElementState element) {
        Set<String> ignoredAttributes = new HashSet<String>();

        Attributes attributes = element.element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            String attribute = attributes.getLocalName(i);

            if (!element.targetedAttributes.contains(attribute)) {
                ignoredAttributes.add(attribute);
            }
        }


        return ignoredAttributes;
    }

    private static String getXPath(Stack<ElementState> elements) {
        StringBuffer sb = new StringBuffer();

        for (ElementState es : elements) {
            sb.append(String.format("/%s[%d]", es.element.getName().getLocalPart(), es.sequence));
        }

        return sb.toString();
    }

}
