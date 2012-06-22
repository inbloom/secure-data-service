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


package org.slc.sli.ingestion.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author ifaybyshev
 *
 */
public class StageSet {

    private List<Stage> chunks;
    private String stageName;

    public StageSet() {
        this.chunks = new LinkedList<Stage>();
        this.stageName = "";
    }

    public StageSet(Stage s) {
        this.chunks = new LinkedList<Stage>();
        this.stageName = s.getStageName();
        this.chunks.add(s);
    }

    public void addStage(Stage s) {
        if (this.stageName.equals("")) {
            this.stageName = s.getStageName();
        }
        this.chunks.add(s);
    }

    public List<Stage> getChunks() {
        return chunks;
    }

    public void setChunks(List<Stage> chunks) {
        this.chunks = chunks;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
}
