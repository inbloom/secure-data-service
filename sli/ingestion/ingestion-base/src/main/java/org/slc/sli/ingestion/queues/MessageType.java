package org.slc.sli.ingestion.queues;

/**
 * @author jtully
 * Message type enumerator.
 */
public enum MessageType {
    BATCH_REQUEST,
    PURGE,
    CONTROL_FILE_PROCESSED, //Flag is set after ControlFileProcessor completes its work
    XML_FILE_PROCESSED, //Flag is set after XmlFileProcessor completes its work
    PERSIST_REQUEST,
    ERROR,
    DONE,
    MERGE_REQUEST,
    DATA_TRANSFORMATION,
    DATA_MODEL_TRANSFORMATION;
}
