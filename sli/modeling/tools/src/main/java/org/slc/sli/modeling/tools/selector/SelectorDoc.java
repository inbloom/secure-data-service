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

package org.slc.sli.modeling.tools.selector;

import java.io.FileNotFoundException;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * Command line utility to read an XMI File, and print out features (attributes,
 * associations) of each element type.
 *
 * @author wscott
 *
 */
public class SelectorDoc {

    public static void main(String[] args) {
        final Model model;
        final ModelIndex mi;
        try {
            model = XmiReader.readModel(args[0]);
        } catch (FileNotFoundException e) {
            return;
        }

        if (model != null) {
            mi = new DefaultModelIndex(model);
        } else {
            return;
        }

        System.out.println("=== ClassTypes ===");
        for(String classtype : mi.getClassTypes().keySet()) {
            System.out.println(classtype);
        }

    }

}
