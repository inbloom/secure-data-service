package org.slc.sli.ingestion.reporting.impl;

import java.text.MessageFormat;

import org.slc.sli.ingestion.reporting.ElementSource;

public class ElementSourceImpl extends JobSource implements ElementSource
{
    private final int visitBeforeLineNumber;
    private final int visitBeforeColumnNumber;
    private final String elementType;

    public ElementSourceImpl(ElementSource source)
    {
        super(source.getResourceId());
        this.visitBeforeLineNumber = source.getVisitBeforeLineNumber();
        this.visitBeforeColumnNumber = source.getVisitBeforeColumnNumber();
        this.elementType = source.getElementType();
    }

    @Override
    public int getVisitBeforeLineNumber()
    {
        return visitBeforeLineNumber;
    }

    @Override
    public int getVisitBeforeColumnNumber()
    {
        return visitBeforeColumnNumber;
    }

    @Override
    public String getElementType()
    {
        return elementType;
    }

    @Override
    public String getUserFriendlyMessage() {
        Object[] arguments = { elementType == null ? "Element" : elementType, 
                new Integer(visitBeforeLineNumber), 
                new Integer(visitBeforeColumnNumber)};

        return MessageFormat.format("{0}:" + "line-{1}," + "column-{2}", arguments);
    }

}
