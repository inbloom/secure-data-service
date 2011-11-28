package org.slc.sli.ingestion;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class BatchJob {

    public enum State {
        INITIAL, REJECTED, PROCESSING, ABORTED, COMPLETED;
    }

    private UUID id;
    private Date createDate;
    private List<File> files;
    private Properties configProperties;
    private State state;

    public BatchJob() {
        this.id = UUID.randomUUID();
        this.createDate = new Date();
        this.configProperties = new Properties();
        this.files = new ArrayList<File>();
        this.state = State.INITIAL;
    }

    /**
     * @return the jobId
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return the files
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    /**
     * @return the configProperties
     */
    public Properties getConfigProperties() {
        return configProperties;
    }

    /**
     * @param configProperties the configProperties to set
     */
    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }
}

