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


package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.RangedWorkNote;

/**
 * @author dduran
 */
public interface TransformationStrategy {
    
    /**
     * Perform a specific transformation on the batch job with provided id.
     * 
     * @param job id of the current job
     */
    void perform(Job job);
    
    /**
     * Perform a specific transformation on the job with the provided id using the WorkNote to guide
     * what collection (and range) should be operated on.
     * 
     * @param job id of the current job
     * @param workNote work handed out by maestro to be performed by pit
     */
    void perform(Job job, RangedWorkNote workNote);
    
}
