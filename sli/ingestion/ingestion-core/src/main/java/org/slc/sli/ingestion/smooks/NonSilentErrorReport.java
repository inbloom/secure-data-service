package org.slc.sli.ingestion.smooks;

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
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;

import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
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
    private Set<String> filteredAttributes;
    private MessageSource messageSource;
    private String messagePrefix;
    private Set<String> warningMessages = new HashSet<String>();
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

    public NonSilentErrorReport(Set<String> filteredAttributes, MessageSource messageSource, ErrorReport errorReport) {
        this.filteredAttributes = filteredAttributes;
        this.messageSource = messageSource;
        this.errorReport = errorReport;
    }

    public NonSilentErrorReport(Set<String> filteredAttributes, MessageSource messageSource, ErrorReport errorReport, String messagePrefix) {
        this(filteredAttributes, messageSource, errorReport);

        this.messagePrefix = messagePrefix;
    }

    @Override
    public void onEvent(ExecutionEvent event) {

        if (event instanceof ElementPresentEvent) {

            ElementPresentEvent presentEvent = (ElementPresentEvent) event;

            SAXElement element = (SAXElement) presentEvent.getElement();

            int sequence = 0;

            ElementState last = getLastElementState();

            reportIgnoredAttributes();

            while (last != null && !last.element.equals(element.getParent())) {
                reportIgnoredElements();

                ElementState oldLast = processedElements.pop();

                if (oldLast.element.getName().equals(element.getName())) {
                    sequence = oldLast.sequence + 1;
                }

                last = getLastElementState();
            }

            processedElements.add(new ElementState(element, sequence));

        } else if (event instanceof ResourceTargetingEvent) {

            ResourceTargetingEvent targetingEvent = (ResourceTargetingEvent) event;

            ElementState last = getLastElementState();

            if (last != null) {
                String targetAttribute = targetingEvent.getResourceConfig().getTargetAttribute();
                if (targetAttribute != null) {
                    last.targetedAttributes.add(targetAttribute);
                }

                markAsProcessed(last);
            }
        } else if (event instanceof FilterLifecycleEvent) {

            FilterLifecycleEvent lifecycleEvent = (FilterLifecycleEvent) event;

            if (lifecycleEvent.getEventType() == EventType.FINISHED) {
                while (processedElements.size() > 0) {
                    reportIgnoredElements();
                    processedElements.pop();
                }
                for (String message : warningMessages) {
                    errorReport.warning(message, this);
                }
            }
        }
    }

    private void markAsProcessed(ElementState element) {
        if (element.isTargeted) {
            return;
        }

        element.isTargeted = true;

        int elementPos = processedElements.lastIndexOf(element);

        if (elementPos > 0) {
            markAsProcessed(processedElements.elementAt(elementPos - 1));
        }
    }

    private ElementState getLastElementState() {
        if (processedElements.size() > 0) {
            return processedElements.peek();
        } else {
            return null;
        }
    }

    private void reportIgnoredElements() {
        ElementState last = getLastElementState();

        if (last == null) {
            return;
        }

        if (!last.isTargeted) {
            String message = MessageSourceHelper.getMessage(messageSource, "NON_SLI_ELEMENT", getXPath(processedElements), this);

            if (StringUtils.hasText(messagePrefix)) {
                message = messagePrefix + "\t" + message;
            }
            warningMessages.add(message);
            //errorReport.warning(message, this);
        }
    }

    private void reportIgnoredAttributes() {
        ElementState last = getLastElementState();

        if (last == null) {
            return;
        }

        Set<String> ignoredAttributes = getIgnoredAttributes(last);

        String xPath = getXPath(processedElements);

        for (String attribute : ignoredAttributes) {
            String message = MessageSourceHelper.getMessage(messageSource, "NON_SLI_ATTRIBUTE", xPath, attribute, this);

            if (StringUtils.hasText(messagePrefix)) {
                message = messagePrefix + "\t" + message;
            }
            warningMessages.add(message);
            //errorReport.warning(message, this);
        }
    }

    private Set<String> getIgnoredAttributes(ElementState element) {
        Set<String> ignoredAttributes = new HashSet<String>();

        Attributes attributes = element.element.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            String attribute = attributes.getLocalName(i);

            if (!attribute.isEmpty()
                    && !filteredAttributes.contains(attribute)
                    && !element.targetedAttributes.contains(attribute)) {
                ignoredAttributes.add(attributes.getQName(i));
            }
        }

        return ignoredAttributes;
    }

    private static String getXPath(Stack<ElementState> elements) {
        StringBuffer sb = new StringBuffer();

        for (ElementState es : elements) {
            sb.append(String.format("/%s", es.element.getName().getLocalPart()));
        }

        return sb.toString();
    }
}
