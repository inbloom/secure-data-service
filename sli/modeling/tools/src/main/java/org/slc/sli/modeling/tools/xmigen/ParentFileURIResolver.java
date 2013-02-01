/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.modeling.tools.xmigen;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 * {@link URIResolver} implementation that resolves <code>schemaLocation</code> using a parent
 * {@link File}.
 */
public final class ParentFileURIResolver implements URIResolver {

    private final File parentFile;

    public ParentFileURIResolver(final File parentFile) {
        if (parentFile == null) {
            throw new IllegalArgumentException("parentFile");
        }
        this.parentFile = parentFile;
    }

    @Override
    public InputSource resolveEntity(final String targetNamespace, final String schemaLocation, final String baseUri) {
        try {
            final File file = new File(parentFile, schemaLocation);
            final InputStream byteStream = new BufferedInputStream(new FileInputStream(file));
            return new InputSource(byteStream);
        } catch (final FileNotFoundException e) {
            throw new XmiGenRuntimeException(e);
        }
    }
}
