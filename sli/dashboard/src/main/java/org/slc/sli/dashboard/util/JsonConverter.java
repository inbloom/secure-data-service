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


package org.slc.sli.dashboard.util;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Json converter util
 *
 * @author dwu
 *
 */
public class JsonConverter {

    private static final Gson GSON = new GsonBuilder().create();

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }
    /**
     * TODO: switch to something more efficient than creating deserializer everytime and use jackson
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return json == null ? null : GSON.fromJson(json, clazz);
    }
    /**
     * TODO: switch to something more efficient than creating deserializer everytime and use jackson
     * @param reader
     * @param clazz
     * @return
     */
    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }
}
