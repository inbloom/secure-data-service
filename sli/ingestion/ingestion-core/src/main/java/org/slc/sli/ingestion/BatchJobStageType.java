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


package org.slc.sli.ingestion;

/**
 * Types of stages
 *
 * @author bsuzuki
 *
 */
public enum BatchJobStageType {

    LANDING_ZONE_PROCESSOR("LandingZoneProcessor"),
    ZIP_FILE_PROCESSOR("ZipFileProcessor"),
    ZIP_FILE_SPLITTER("ZipFileSplitter"),
    CONTROL_FILE_PREPROCESSOR("ControlFilePreProcessor"),
    CONTROL_FILE_PROCESSOR("ControlFileProcessor"),
    PURGE_PROCESSOR("PurgeProcessor"),
    EDFI_PROCESSOR("EdFiProcessor"),
    WORKNOTE_SPLITTER("WorkNoteSplitter"),
    TRANSFORMATION_PROCESSOR("TransformationProcessor"),
    PERSISTENCE_PROCESSOR("PersistenceProcessor"),
    JOB_REPORTING_PROCESSOR("JobReportingProcessor");

    private final String name;

    BatchJobStageType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
