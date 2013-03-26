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
package org.slc.sli.bulk.extract.File;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * 
 * @author npandey
 *
 */
public class DataExtractFile implements Closeable{
    
    private GzipCompressorOutputStream outputStream;
    private File file;
    
    private static final String FILE_EXTENSION = ".json.gz";
    
    public DataExtractFile(String parentDirName, String fileName)
            throws FileNotFoundException, IOException {
        
        File parentDir = new File(parentDirName + "/");
        if (parentDir.isDirectory()) {
            file = new File(parentDir, fileName + FILE_EXTENSION);
            file.createNewFile();
            if (file.canWrite()) {
                outputStream = new GzipCompressorOutputStream(new FileOutputStream(file));
            }
        }
    }
    
    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    @Override
    public void close() {
        IOUtils.closeQuietly(outputStream);
        
    }
    
    public File getFile() {
        return file;
    }

}
