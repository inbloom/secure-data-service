package org.slc.sli.ingestion.model.da;

/**
 * 
 * @author ldalgado
 *
 */
public class JobLogStatus {

    private boolean success; 
    private String message;  
    private Object result;
    
    public JobLogStatus(boolean success, String message, Object result) {
        super();
        this.success = success;
        this.message = message;
        this.result = result;
    }
    
    //mongoTemplate requires this constructor.
    public JobLogStatus() {
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Object getResult() {
        return result;
    }
    public void setResult(Object result) {
        this.result = result;
    }   
}
