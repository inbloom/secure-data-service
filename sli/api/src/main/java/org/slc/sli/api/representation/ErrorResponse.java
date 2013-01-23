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


package org.slc.sli.api.representation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Representation of an error message in response to issues encountered servicing API requests.
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@XmlRootElement(name = "error")
public class ErrorResponse {
    @XmlElement(name = "code")
    @JsonProperty("code")
    int statusCode;

    @XmlElement(name = "type")
    @JsonProperty("type")
    String type;

    @XmlElement(name = "message")
    @JsonProperty("message")
    String message;

    public ErrorResponse(int statusCode, String type, String message) {
        this.statusCode = statusCode;
        this.type = type;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

}
