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


package org.slc.sli.ingestion.transformation.normalization;

/**
 * An exception class dedicated to references that could not be resolved
 * to an ID.
 *
 * @author kmyers
 *
 */
public class IdResolutionException extends Exception {

    private static final long serialVersionUID = 1L;
    private String key;
    private String value;

    public IdResolutionException(String errorMessage, String key, String value) {
        super(errorMessage);
        this.key = key;
        this.value = value;
    }

    public IdResolutionException(String errorMessage, String key, String value, Throwable wrapped) {
        super(errorMessage, wrapped);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
