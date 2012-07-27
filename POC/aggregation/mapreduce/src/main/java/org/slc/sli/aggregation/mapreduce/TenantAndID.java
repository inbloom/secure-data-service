package org.slc.sli.aggregation.mapreduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * Writable for holding both a tenant and an id
 * 
 * @author nbrown
 *
 */
public class TenantAndID implements WritableComparable<TenantAndID> {
    private String id;
    private String tenant;
    
    public TenantAndID(String id, String tenant) {
        super();
        this.id = id;
        this.tenant = tenant;
    }
    
    public TenantAndID() {
        this(null, null);
    }

    public String getId() {
        return id;
    }

    public String getTenant() {
        return tenant;
    }

    @Override
    public void readFields(DataInput data) throws IOException {
        id = data.readLine();
        tenant = data.readLine();
        
    }
    
    @Override
    public void write(DataOutput data) throws IOException {
        data.writeBytes(getId() + "\n" + getTenant());
    }

    @Override
    public String toString() {
        return "TenantAndID [id=" + id + ", tenant=" + tenant + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TenantAndID other = (TenantAndID) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (tenant == null) {
            if (other.tenant != null)
                return false;
        } else if (!tenant.equals(other.tenant))
            return false;
        return true;
    }

    @Override
    public int compareTo(TenantAndID other) {
        return this.id.compareTo(other.id);
    }
    
    
}
