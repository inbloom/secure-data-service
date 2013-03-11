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
package org.slc.sli.api.migration;

import java.util.HashMap;

/**
 * Represents a rule that is chained to resolve a field.
 * Used in migration strategy.
 */
public final class TransformationRule implements Comparable<TransformationRule> {

    private Integer rank;
    private String collection;
    private String field;

    private TransformationRule(Integer rank, HashMap<String, String> rule) {
        this.rank = rank;
        this.collection = rule.get("collection");
        this.field = rule.get("field");
    }

    public static TransformationRule init(Integer rank, HashMap<String, String> rule) {

        return new TransformationRule(rank, rule);
    }


    public int getRank() {
        return this.rank;
    }

    public String getField() {
        return this.field;
    }

    public String getCollection() {
        return this.collection;
    }

    @Override
    public int compareTo(TransformationRule transformationRule) {
        if (this.getRank() < transformationRule.getRank()) {
            return -1;
        } else if (this.getRank() > transformationRule.getRank()) {
            return 1;
        } else {
            return 0;
        }
    }

}
