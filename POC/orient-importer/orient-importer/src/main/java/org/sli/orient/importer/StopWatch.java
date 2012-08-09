package org.sli.orient.importer;

public class StopWatch {
    
    private long startTime;
    private long endTime;
    
    public StopWatch() {
        this.start();
    }
    
    public void start() {
        endTime = 0;
        startTime = System.currentTimeMillis();
    }
    
    public void stop() {
        endTime = System.currentTimeMillis() - startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }

    public String inSeconds() {
        return ((float) endTime / 1000) + " s";
    }
    
    public String toString() {
        return endTime + " ms";
    }

}
