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
