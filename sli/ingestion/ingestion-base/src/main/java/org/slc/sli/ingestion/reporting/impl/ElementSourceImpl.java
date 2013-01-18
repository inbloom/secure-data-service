package org.slc.sli.ingestion.reporting.impl;

import java.text.MessageFormat;

import org.slc.sli.ingestion.reporting.ElementSource;

public class ElementSourceImpl extends JobSource implements ElementSource
{
    private final ElementSource source;

    public ElementSourceImpl(ElementSource source)
    {
        super(source.getResourceId());
        this.source = source;
    }

    @Override
    public int getVisitBeforeLineNumber()
    {
        return source.getVisitBeforeLineNumber();
    }

    @Override
    public int getVisitBeforeColumnNumber()
    {
        return source.getVisitBeforeColumnNumber();
    }

    @Override
    public String getElementType()
    {
        return source.getElementType();
    }

    @Override
    public String getUserFriendlyMessage() {
        Object[] arguments = { getElementType(), 
                new Integer(getVisitBeforeLineNumber()), 
                new Integer(getVisitBeforeColumnNumber())};

        return MessageFormat.format("Element:" + "line-{1}," + "column-{2}", arguments);
    }

}
