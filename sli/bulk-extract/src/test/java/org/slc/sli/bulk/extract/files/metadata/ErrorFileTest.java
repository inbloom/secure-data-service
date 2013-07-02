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
package org.slc.sli.bulk.extract.files.metadata;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.domain.Entity;

public class ErrorFileTest {
    
    @Test
    public void testErrorFile() throws IOException {
        File parentDir = new File("./");
        parentDir.deleteOnExit();
        
        ErrorFile error = new ErrorFile(parentDir);
        
        
        for (int i=0; i < 3; i++) {
            Entity entity = Mockito.mock(Entity.class);
            Mockito.when(entity.getType()).thenReturn("TYPE" + i);
            error.logEntityError(entity);
        }
        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("TYPE0");
        error.logEntityError(entity);
        
        File result = error.getFile();
        assertNotNull(result);
        
        String errorString = FileUtils.readFileToString(result);
        
        assertTrue(errorString.contains("2 errors occurred for entity type TYPE0\n"));
        assertTrue(errorString.contains("1 errors occurred for entity type TYPE1\n"));
        assertTrue(errorString.contains("1 errors occurred for entity type TYPE2\n"));
    }
}
