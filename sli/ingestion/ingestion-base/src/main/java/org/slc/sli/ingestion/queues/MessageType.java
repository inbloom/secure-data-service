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


package org.slc.sli.ingestion.queues;

/**
 * @author jtully
 * Message type enumerator.
 */
public enum MessageType {
    BATCH_REQUEST,
    PURGE,
    CONTROL_FILE_PROCESSED, //Flag is set after ControlFileProcessor completes its work
    PERSIST_REQUEST,
    ERROR,
    DONE,
    MERGE_REQUEST,
    DATA_TRANSFORMATION,
    DATA_MODEL_TRANSFORMATION,
    DATA_STAGED,
    XML_FILE_SPLIT; // XML file has been split
}
