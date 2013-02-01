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

import org.apache.ws.commons.schema.XmlSchema;
import org.slc.sli.modeling.uml.Model;

/**
 * A utility class that can transform an XML schema to UML.
 * 
 * @author kmyers
 *
 */
public final class Xsd2Uml {
    public static final Model transform(final String name, final XmlSchema schema, final Xsd2UmlHostedPlugin plugin) {
        final Model model01 = Xsd2UmlConvert.extract(name, schema, plugin);
        final Model model02 = Xsd2UmlLinker.link(model01, plugin);
        final Model model03 = Xsd2UmlTweaker.tweak(model02, plugin);
        return model03;
    }
}
