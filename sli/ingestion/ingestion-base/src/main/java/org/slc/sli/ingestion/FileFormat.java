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


package org.slc.sli.ingestion;

/**
 * File Format enumerator.
 *
 * @author okrook
 *
 */
public enum FileFormat {

    EDFI_XML("edfi-xml", "xml"),
    CSV("csv", "csv"),
    NEUTRALRECORD("neutralrecord", "tmp"),
    CONTROL_FILE("control-file", "ctl"),
    ZIP_FILE("zip-file", "zip");

    private final String code;
    private final String extension;

    FileFormat(String code, String extension) {
        this.code = code;
        this.extension = extension;
    }

    public String getCode() {
        return code;
    }

    public String getExtension() {
        return extension;
    }

    public static FileFormat findByCode(String code) {
        for (FileFormat ff : FileFormat.values()) {
            if (ff.getCode().toLowerCase().equals(code)) {
                return ff;
            }
        }
        return null;
    }

}
