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

package org.slc.sli.common.util.tenantdb;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that provides thread-local context for non-local jump use cases.
 */
public class TenantContext {

    private static ThreadLocal<String> threadLocalTenantId = new ThreadLocal<String>();

    private static ThreadLocal<String> threadLocalJobId = new ThreadLocal<String>();

    private static ThreadLocal<HashMap<String, String>> threadLocalBatchProperties = new ThreadLocal<HashMap<String, String>>();

    private static ThreadLocal<Boolean> threadLocalIsSystemCall = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };


    /**
     * Get the tenant id local to this thread.
     *
     * @return tenant id.
     */
    public static String getTenantId() {
        return threadLocalTenantId.get();
    }

    /**
     * Set the tenant id local to this thread.
     *
     * @param tenantId
     */
    public static void setTenantId(String tenantId) {
        threadLocalTenantId.set(tenantId);
    }

    /**
     * Set the job id local to this thread.
     *
     * @param jobId
     */
    public static void setJobId(String jobId) {
        threadLocalJobId.set(jobId);
    }

    /**
     * Set the batch job properties local to this thread.
     *
     * @param batchProperties
     */
    public static void setBatchProperties(Map<String, String> batchProperties) {
        threadLocalBatchProperties.set((HashMap<String, String>) batchProperties);
    }

    /**
     * Get the job id local to this thread.
     *
     * @return job id.
     */
    public static String getJobId() {
        return threadLocalJobId.get();
    }

    public static String getBatchProperty(String key) {
        Map<String, String> props = threadLocalBatchProperties.get();
        if (null == props) {
            return null;
        }
        return props.get(key);
    }

    public static Map<String, String> getBatchProperties() {
        return threadLocalBatchProperties.get();
    }

    public static boolean isSystemCall() {
        return threadLocalIsSystemCall.get();
    }

    public static void setIsSystemCall(boolean isSystemCall) {
        threadLocalIsSystemCall.set(isSystemCall);
    }

    /**
     * Remove all the ThreadLocals used by TenantContext for the current thread
     */
    public static void cleanup() {
        threadLocalTenantId.remove();
        threadLocalJobId.remove();
        threadLocalBatchProperties.remove();
        threadLocalIsSystemCall.remove();
    }

}
