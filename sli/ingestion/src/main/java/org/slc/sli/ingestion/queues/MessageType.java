package org.slc.sli.ingestion.queues;

/**
 * @author jtully
 * Message type enumerator.
 */
public enum MessageType {
    BATCH_REQUEST,
    BULK_TRANSFORM_REQUEST,
    PERSIST_REQUEST,
    ERROR,
    DONE,
    MERGE_REQUEST,
    DATA_TRANSFORMATION,
    DATA_MODEL_TRANSFORMATION;
}
