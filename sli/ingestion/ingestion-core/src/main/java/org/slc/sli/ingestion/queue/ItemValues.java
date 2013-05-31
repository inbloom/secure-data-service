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


package org.slc.sli.ingestion.queue;

/**
 *
 * Known values that a worker item can specify. This class exists for convenience and minimal
 * type-safety, nothing is enforcing these keypairs.
 * Using a class of string literals, since it is more concise than Java enum.
 *
 * @author smelody
 *
 */
public final class ItemValues {

    private ItemValues() { }

    public static final String UNCLAIMED = "unclaimed";
    public static final String WORKING = "working";
}
