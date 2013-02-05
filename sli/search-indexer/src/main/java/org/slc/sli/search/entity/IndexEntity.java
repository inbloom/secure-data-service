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
package org.slc.sli.search.entity;

import java.util.Map;

/**
 * Indexable Unit
 *
 */
public class IndexEntity {
    public enum Action {
        UPDATE("index"), // update will be get-reindex, so action remains as index
        QUICK_UPDATE("update"),
        INDEX("index"),
        DELETE("delete");

        private final String type;
        Action(String type) {this.type = type;}
        public String getType() {
            return type;
        }
    }
    private final Action action;
    private final String index;
    private final String type;
    private final String id;
    private final Map<String, Object> body;

    public IndexEntity(Action action, String index, String type, String id, Map<String, Object> body) {
        this.action = action;
        this.index = index;
        this.type = type;
        this.id = id;
        this.body = body;
    }

    public IndexEntity(String index, String type, String id, Map<String, Object> body) {
        this(Action.INDEX, index, type, id, body);
    }

    public IndexEntity(String index, String type, String id) {
        this(Action.INDEX, index, type, id, null);
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public String getActionValue() {
        return action.getType();
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return action.getType() + ": {index:" + index + ", type:" + type + ", id:" + id + "}";
    }
}
