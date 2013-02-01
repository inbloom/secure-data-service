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

package org.slc.sli.modeling.tools.xmigen;

/**
 * An RuntimeException that occurs during the XMI generation process.
 * 
 * @author kmyers
 *
 */
public class XmiGenRuntimeException extends RuntimeException {

    public XmiGenRuntimeException(Throwable cause) {
        super(cause);
    }

    public XmiGenRuntimeException(String message) {
        super(message);
    }

    public XmiGenRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
