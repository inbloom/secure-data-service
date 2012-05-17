package org.slc.sli.ingestion.smooks;

import java.util.ArrayList;
import java.util.List;

import org.milyn.delivery.sax.SAXElement;
import org.milyn.event.ElementProcessingEvent;
import org.milyn.event.ExecutionEvent;
import org.milyn.event.ExecutionEventListener;
import org.milyn.event.types.ElementPresentEvent;
import org.milyn.event.types.ElementVisitEvent;
import org.milyn.event.types.ResourceTargetingEvent;

/**
 * Non-silent error report class reports elements/attributes that were ignored while processing data.
 *
 * @author okrook
 *
 */
public class NonSilentErrorReport implements ExecutionEventListener {

    private List<String> eventsPresent = new ArrayList<String>();
    private List<String> eventsVisited = new ArrayList<String>();
    private List<String> eventsResourceTarget = new ArrayList<String>();
    private List<String> eventsProcessed = new ArrayList<String>();


    @Override
    public void onEvent(ExecutionEvent event) {
        try {
            if (!ignoreEvent(event)) {
                event.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected boolean ignoreEvent(ExecutionEvent event) {

        if (event instanceof ElementPresentEvent) {
            ElementPresentEvent newEvent = (ElementPresentEvent) event;
            SAXElement element = (SAXElement) newEvent.getElement();
            eventsPresent.add(newEvent.getDepth() + "-" + element.getName().toString());
            return true;
        }

        if (event instanceof ElementVisitEvent) {
            ElementVisitEvent newEvent = (ElementVisitEvent) event;
            SAXElement element = (SAXElement) newEvent.getElement();
            eventsVisited.add(newEvent.getSequence().name() + "-" + element.getName().toString());
        }

        if (event instanceof ResourceTargetingEvent) {
            ResourceTargetingEvent newEvent = (ResourceTargetingEvent) event;

            SAXElement element = (SAXElement) newEvent.getElement();
            eventsResourceTarget.add(newEvent.getSequence().name() + "-" + element.getName().toString());
        }

        if (event instanceof ElementProcessingEvent) {
            ElementProcessingEvent newEvent = (ElementProcessingEvent) event;
            SAXElement element = (SAXElement) newEvent.getElement();
            eventsProcessed.add(newEvent.getDepth() + "-" + element.getName().toString());
        }

        return true;
    }

    public List<String> getEventsPresent() {
        return eventsPresent;
    }

    public List<String> getEventsVisited() {
        return eventsVisited;
    }

    public List<String> getEventsResourceTarget() {
        return eventsResourceTarget;
    }
    public List<String> getEventsProcessed() {
        return eventsProcessed;
    }



}
