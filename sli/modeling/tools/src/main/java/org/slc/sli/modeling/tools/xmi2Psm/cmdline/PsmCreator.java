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


package org.slc.sli.modeling.tools.xmi2Psm.cmdline;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmConfigWriter;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.psm.PsmResource;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for generating an initial definition of the platform specific model.
 *
 * The classes in the UML model become documents. These can be pruned manually.
 */
public final class PsmCreator {

    public static void main(final String[] args) {
        try {
            final Model model = XmiReader.readModel("SLI.xmi");
            final PsmConfig<Type> psm = convert(model);
            PsmConfigWriter.writeConfig(psm, model, "documents.xml");
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private PsmCreator() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }

    private static final PsmConfig<Type> convert(final Model model) {
        final List<PsmDocument<Type>> resources = new LinkedList<PsmDocument<Type>>();
        for (final NamespaceOwnedElement ownedElement : model.getOwnedElements()) {
            if (ownedElement instanceof ClassType) {
                final ClassType classType = (ClassType) ownedElement;
                resources.add(convertClassType(classType));
            }
        }
        Collections.sort(resources, PsmDocumentComparator.SINGLETON);
        return new PsmConfig<Type>(resources);
    }

    /**
     * As a first approximation we convert all the available classes to documents.
     */
    private static final PsmDocument<Type> convertClassType(final ClassType classType) {
        final String camelName = camelCase(classType.getName());
        final PsmCollection collection = new PsmCollection(camelName);
        final PsmResource resource = new PsmResource(camelName.concat("s"));
        return new PsmDocument<Type>(classType, resource, collection);
    }

    private static final String camelCase(final String name) {
        return name.substring(0, 1).toLowerCase().concat(name.substring(1));
    }
}
