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

import org.slc.sli.ingestion.landingzone.ControlFile;
/**
 * @author tke
 *
 */
public class ControlFileWorkNote extends WorkNote {
    private static final long serialVersionUID = 7526472295622776147L;
    private ControlFile controlFile;

    public ControlFileWorkNote(ControlFile cf, String batchJobId, String tenantId) {
        super(batchJobId, tenantId);
        this.controlFile = cf;
    }

    /**
     * @return the controlFile
     */
    public ControlFile getControlFile() {
        return controlFile;
    }

    /**
     * @param controlFile the controlFile to set
     */
    public void setControlFile(ControlFile controlFile) {
        this.controlFile = controlFile;
    }


}
