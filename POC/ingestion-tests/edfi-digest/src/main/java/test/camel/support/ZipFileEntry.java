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


package test.camel.support;

import java.io.Serializable;

/**
 * @author okrook
 *
 */
public class ZipFileEntry implements Serializable {
    private static final long serialVersionUID = 2644940694123892337L;

    private String zipFilePath;
    private String fileEntry;

    public ZipFileEntry() {
    }

    public ZipFileEntry(String zipFilePath, String fileEntry) {
        this.zipFilePath = zipFilePath;
        this.fileEntry = fileEntry;
    }

    public String getZipFilePath() {
        return zipFilePath;
    }

    public void setZipFilePath(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    public String getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(String fileEntry) {
        this.fileEntry = fileEntry;
    }
}
