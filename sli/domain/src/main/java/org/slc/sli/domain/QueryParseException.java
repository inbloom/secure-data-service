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


package org.slc.sli.domain;

/**
 * Exception indicating a http query request string could not be parsed
 * to spring data query object.
 * 
 * @author dong liu <dliu@wgen.net>
 */

public class QueryParseException extends RuntimeException {
    
    private static final long serialVersionUID = 3777997711710333578L;
    private String queryString;
    
    public QueryParseException(String message, String queryString) {
        super(message);
        this.queryString = queryString;
    }

    public QueryParseException(String message, String queryString, Exception e) {
        super(message, e);
        this.queryString = queryString;
    }
    
    public String getQueryString() {
        return this.queryString;
    }
}
