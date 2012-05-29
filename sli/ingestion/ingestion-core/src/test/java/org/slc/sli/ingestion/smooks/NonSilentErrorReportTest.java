package org.slc.sli.ingestion.smooks;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.delivery.sax.SAXElement;
import org.milyn.event.types.ElementPresentEvent;
import org.milyn.event.types.FilterLifecycleEvent;
import org.milyn.event.types.FilterLifecycleEvent.EventType;
import org.milyn.event.types.ResourceTargetingEvent;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.xml.sax.Attributes;

import org.slc.sli.ingestion.FaultsReport;

/**
 *
 * @author npandey
 *
 */
public class NonSilentErrorReportTest {

    NonSilentErrorReport nonSilentErrorReport;

    FaultsReport errorReport;
    MessageSource messageSource;
    HashSet<String> warningMessages;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Set<String> filteredAttributes = new HashSet<String>();
        filteredAttributes.add("refResolved");
        messageSource = Mockito.mock(MessageSource.class);
        errorReport = new FaultsReport();
        nonSilentErrorReport = new NonSilentErrorReport(filteredAttributes, messageSource, errorReport);
        warningMessages = (HashSet<String>) Mockito.spy(PrivateAccessor.getField(nonSilentErrorReport, "warningMessages"));
        PrivateAccessor.setField(nonSilentErrorReport, "warningMessages", warningMessages);
    }

    @Test
    public void testLifeCycleEventOnly() {
        FilterLifecycleEvent event = Mockito.mock(FilterLifecycleEvent.class);
        Mockito.when(event.getEventType()).thenReturn(EventType.FINISHED);

        nonSilentErrorReport.onEvent(event);

        Assert.assertEquals(0, errorReport.getFaults().size());

    }

    @Test
    public void testInvalidEvents() {

        ResourceTargetingEvent event = Mockito.mock(ResourceTargetingEvent.class);
        SmooksResourceConfiguration resourceConfig = Mockito.mock(SmooksResourceConfiguration.class);

        Mockito.when(event.getResourceConfig()).thenReturn(resourceConfig);
        Mockito.when(resourceConfig.getTargetAttribute()).thenReturn("att");

        nonSilentErrorReport.onEvent(event);

        FilterLifecycleEvent lifecycleEvent = Mockito.mock(FilterLifecycleEvent.class);
        Mockito.when(lifecycleEvent.getEventType()).thenReturn(EventType.FINISHED);

        nonSilentErrorReport.onEvent(lifecycleEvent);

        Assert.assertEquals(0, errorReport.getFaults().size());
    }

    @Test
    public void testValidTargetedEvent() {
        ElementPresentEvent elementPresentEvent = Mockito.mock(ElementPresentEvent.class);

        SAXElement element = Mockito.mock(SAXElement.class);
        Mockito.when(element.getParent()).thenReturn(null);
        Mockito.when(element.getName()).thenReturn(new QName("TestElement"));
        Mockito.when(elementPresentEvent.getElement()).thenReturn(element);

        Attributes attributes = Mockito.mock(Attributes.class);
        Mockito.when(attributes.getLength()).thenReturn(2);
        Mockito.when(element.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getLocalName(0)).thenReturn("dummyAtt");
        Mockito.when(attributes.getLocalName(1)).thenReturn("refResolved");

        nonSilentErrorReport.onEvent(elementPresentEvent);

        ResourceTargetingEvent resourceTargetingEvent = Mockito.mock(ResourceTargetingEvent.class);
        SmooksResourceConfiguration resourceConfig = Mockito.mock(SmooksResourceConfiguration.class);

        Mockito.when(resourceTargetingEvent.getResourceConfig()).thenReturn(resourceConfig);
        Mockito.when(resourceConfig.getTargetAttribute()).thenReturn(null);

        nonSilentErrorReport.onEvent(resourceTargetingEvent);

        ElementPresentEvent secondEvent = Mockito.mock(ElementPresentEvent.class);
        SAXElement secondElement = Mockito.mock(SAXElement.class);
        Mockito.when(secondElement.getParent()).thenReturn(null);
        Mockito.when(secondElement.getName()).thenReturn(new QName("ChildElement"));
        Mockito.when(secondEvent.getElement()).thenReturn(secondElement);

        nonSilentErrorReport.onEvent(secondEvent);

        FilterLifecycleEvent lifecycleEvent = Mockito.mock(FilterLifecycleEvent.class);
        Mockito.when(lifecycleEvent.getEventType()).thenReturn(EventType.FINISHED);

        nonSilentErrorReport.onEvent(lifecycleEvent);

        Mockito.verify(warningMessages, Mockito.times(2)).add(null);
    }

    @Test
    public void testValidNonTargetedEvent() {
        ElementPresentEvent elementPresentEvent = Mockito.mock(ElementPresentEvent.class);

        SAXElement element = Mockito.mock(SAXElement.class);
        Mockito.when(element.getParent()).thenReturn(null);
        Mockito.when(element.getName()).thenReturn(new QName("TestElement"));
        Mockito.when(elementPresentEvent.getElement()).thenReturn(element);

        Attributes attributes = Mockito.mock(Attributes.class);
        Mockito.when(attributes.getLength()).thenReturn(2);
        Mockito.when(element.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getLocalName(0)).thenReturn("dummyAtt");
        Mockito.when(attributes.getLocalName(1)).thenReturn("refResolved");

        nonSilentErrorReport.onEvent(elementPresentEvent);

        ElementPresentEvent secondEvent = Mockito.mock(ElementPresentEvent.class);
        SAXElement secondElement = Mockito.mock(SAXElement.class);
        Mockito.when(secondElement.getParent()).thenReturn(null);
        Mockito.when(secondElement.getName()).thenReturn(new QName("ChildElement"));
        Mockito.when(secondEvent.getElement()).thenReturn(secondElement);

        nonSilentErrorReport.onEvent(secondEvent);

        FilterLifecycleEvent lifecycleEvent = Mockito.mock(FilterLifecycleEvent.class);
        Mockito.when(lifecycleEvent.getEventType()).thenReturn(EventType.FINISHED);

        nonSilentErrorReport.onEvent(lifecycleEvent);

        Mockito.verify(warningMessages, Mockito.times(3)).add(null);
    }
}
