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

package org.slc.sli.modeling.waudit;


import org.junit.Test;

import java.io.File;

public class WadlAuditTest {

    @Test
    public void test() {
        String[] args = new String[]{
                "--outFolder",
                ".",
                "--wadlFile",
                "src/test/resources/SLI.wadl",
                "--xmiFile",
                "src/test/resources/SLI.xmi",
                "--documentFile",
                "src/test/resources/documents.xml",
                "--outFile",
                "ebase_wadl.wadl",
        };

        WadlAudit.main(args);

        File file = new File("ebase_wadl.wadl");
        file.delete();
    }


}
