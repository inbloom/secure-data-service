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

package org.slc.sli.modeling.docgen;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 11/21/12
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class DocumentGeneratorRuntimeException extends RuntimeException {
    public DocumentGeneratorRuntimeException(Throwable cause) {
        super(cause);
    }

    public DocumentGeneratorRuntimeException(String message) {
        super(message);
    }

    public DocumentGeneratorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
