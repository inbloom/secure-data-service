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


package org.slc.sli.modeling.tools.edfisli.cmdline;

import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.FileNotFoundException;
import java.util.Set;

/**
 * Command-line utility to determine where elements are used in the model.
 */
public final class WhereUsedCmdLine {

    private static final Logger LOG = LoggerFactory.getLogger(WhereUsedCmdLine.class);

    public static final String DEFAULT_INPUT_FILENAME = "SLI.xmi";
    public static final String DEFAULT_NAME = "percent";

    public WhereUsedCmdLine() {
        throw new UnsupportedOperationException();
    }

    public static void main(final String[] args) {

        String inputFilename = (args.length == 2) ? args[0] : DEFAULT_INPUT_FILENAME;
        String name = (args.length == 2) ? args[1] : DEFAULT_NAME;

        try {
            final Model model = XmiReader.readModel(inputFilename);
            final ModelIndex index = new DefaultModelIndex(model);

            @SuppressWarnings("deprecation")
            final Set<ModelElement> matches = index.lookupByName(new QName(name));
            for (final ModelElement match : matches) {
                LOG.info("name : " + name + " => " + match);
                showUsage(index, match.getId(), "  ");
            }
        } catch (final FileNotFoundException e) {
            throw new EdFiSLIRuntimeException(e);
        }
    }

    private static final void showUsage(final ModelIndex index, final Identifier id, final String indent) {
        final Set<ModelElement> usages = index.whereUsed(id);
        for (final ModelElement usage : usages) {
            if (usage instanceof ClassType) {
                final ClassType classType = (ClassType) usage;
                LOG.info(indent + "classType : " + classType.getName());
                showUsage(index, classType.getId(), indent.concat("  "));
            } else if (usage instanceof Attribute) {
                final Attribute attribute = (Attribute) usage;
                LOG.info(indent + "attribute : " + attribute.getName());
                showUsage(index, attribute.getId(), indent.concat("  "));
            } else {
                LOG.info(indent + "usage : " + usage.getClass());
            }
        }
    }
}
