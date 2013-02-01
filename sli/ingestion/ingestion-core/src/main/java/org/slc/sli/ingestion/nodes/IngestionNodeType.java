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


package org.slc.sli.ingestion.nodes;

/**
 * We view ingestion as a symphony, with a maestro conducting work and worker nodes in the pit (orchestra).
 *
 * @author smelody
 *
 */
public final class IngestionNodeType {

    private IngestionNodeType() { }

    /** Maestro has the execution vision for the job */
    public static final String MAESTRO = "maestro";

    /** Life in the pit is execution of what the maestro tells you to do */
    public static final String PIT = "pit";

    public static final String STANDALONE = "standalone";
}
