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


package org.slc.sli.ingestion.landingzone;

import java.io.Serializable;

/**
 * Describes a file and the Landing Zone it is in.
 *
 * @author okrook
 *
 * @param <T> Type that holds file information
 */
public class FileDescriptor<T> implements Serializable {

    private static final long serialVersionUID = -2800997090364423334L;

    private T fileItem;
    private String parentZipFileOrDirectory;

    public FileDescriptor(T fileItem, String parentZipFileOrDirectory) {
        this.fileItem = fileItem;
        this.parentZipFileOrDirectory = parentZipFileOrDirectory;
    }

    public T getFileItem() {
        return fileItem;
    }

    public String getParentZipFileOrDirectory() {
        return parentZipFileOrDirectory;
    }
}
