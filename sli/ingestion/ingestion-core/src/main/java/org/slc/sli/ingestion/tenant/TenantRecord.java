/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.ingestion.tenant;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Container class for Tenant data entries
 *
 * @author jtully
 */
public class TenantRecord {

    private List<LandingZoneRecord> landingZone;
    private String tenantId;
    private String dbName;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public List<LandingZoneRecord> getLandingZone() {
        return landingZone;
    }

    public void setLandingZone(List<LandingZoneRecord> landingZone) {
        this.landingZone = landingZone;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * Read in a TenantRecord object from a JSON InputStream
     *
     * @param inputStream
     *            , JSON formatted InputStream
     * @return TenantRecord object
     * @throws IOException
     */
    public static TenantRecord parse(InputStream inputStream) throws IOException {
        return MAPPER.readValue(inputStream, TenantRecord.class);
    }

    /**
     * Read in a TenantRecord object from a JSON String
     *
     * @param input
     *            , JSON formatted String
     * @return TenantRecord object
     * @throws IOException
     */
    public static TenantRecord parse(String input) throws IOException {
        return MAPPER.readValue(input, TenantRecord.class);
    }

    /**
     * Output the object as a JSON String
     */
    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (java.io.IOException e) {
            return super.toString();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((landingZone == null) ? 0 : landingZone.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        return toString().equals(o.toString());
    }
}
