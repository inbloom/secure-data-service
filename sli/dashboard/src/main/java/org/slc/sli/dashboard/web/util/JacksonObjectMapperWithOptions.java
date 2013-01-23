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


package org.slc.sli.dashboard.web.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

/**
 * A wrapper for jackson ObjectMapper with some useful features enabled
 * @author agrebneva
 *
 */
public class JacksonObjectMapperWithOptions extends ObjectMapper {

    public JacksonObjectMapperWithOptions() {
        super();
        this.getJsonFactory().setCharacterEscapes(new HTMLCharacterEscapes());
        configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
        configure(MapperFeature.AUTO_DETECT_FIELDS, true);
        setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
    }
}
