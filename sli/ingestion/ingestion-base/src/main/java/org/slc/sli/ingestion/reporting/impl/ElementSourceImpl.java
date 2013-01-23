/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.reporting.impl;

import java.text.MessageFormat;

import org.slc.sli.ingestion.reporting.ElementSource;

/**
 * Source implementation for XML based Elements.
 *
 */
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
