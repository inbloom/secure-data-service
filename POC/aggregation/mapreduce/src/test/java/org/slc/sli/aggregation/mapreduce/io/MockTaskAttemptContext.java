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

package org.slc.sli.aggregation.mapreduce.io;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.security.Credentials;

/**
 * MockTaskAttemptContext - mock for a mongo hadoop task context instance.
 */
public class MockTaskAttemptContext implements TaskAttemptContext {

    protected Configuration conf = new Configuration();

    public MockTaskAttemptContext() throws IOException {
        conf.set("mongo.output.uri", "mongodb://test.server:27017/test.collection");
    }

    @Override
    public Configuration getConfiguration() {
        return conf;
    }

    @Override
    public Credentials getCredentials() {
        return null;
    }

    @Override
    public JobID getJobID() {
        return new JobID();
    }

    @Override
    public int getNumReduceTasks() {
        return 0;
    }

    @Override
    public Path getWorkingDirectory() throws IOException {
        return null;
    }

    @Override
    public Class<?> getOutputKeyClass() {
        return null;
    }

    @Override
    public Class<?> getOutputValueClass() {
        return null;
    }

    @Override
    public Class<?> getMapOutputKeyClass() {
        return null;
    }

    @Override
    public Class<?> getMapOutputValueClass() {
        return null;
    }

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public boolean userClassesTakesPrecedence() {
        return false;
    }

    @Override
    public Class<? extends InputFormat<?, ?>> getInputFormatClass() throws ClassNotFoundException {
        return null;
    }

    @Override
    public Class<? extends Mapper<?, ?, ?, ?>> getMapperClass() throws ClassNotFoundException {
        return null;
    }

    @Override
    public Class<? extends Reducer<?, ?, ?, ?>> getCombinerClass() throws ClassNotFoundException {
        return null;
    }

    @Override
    public Class<? extends Reducer<?, ?, ?, ?>> getReducerClass() throws ClassNotFoundException {
        return null;
    }

    @Override
    public Class<? extends OutputFormat<?, ?>> getOutputFormatClass() throws ClassNotFoundException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getPartitionerClass
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getPartitionerClass()
     */
    @Override
    public Class<? extends Partitioner<?, ?>> getPartitionerClass() throws ClassNotFoundException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getSortComparator
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getSortComparator()
     */
    @Override
    public RawComparator<?> getSortComparator() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getJar
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getJar()
     */
    @Override
    public String getJar() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getGroupingComparator
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getGroupingComparator()
     */
    @Override
    public RawComparator<?> getGroupingComparator() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getJobSetupCleanupNeeded
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getJobSetupCleanupNeeded()
     */
    @Override
    public boolean getJobSetupCleanupNeeded() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * getProfileEnabled
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getProfileEnabled()
     */
    @Override
    public boolean getProfileEnabled() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * getProfileParams
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getProfileParams()
     */
    @Override
    public String getProfileParams() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getUser
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getUser()
     */
    @Override
    public String getUser() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getSymlink
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getSymlink()
     */
    @Override
    public boolean getSymlink() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * getArchiveClassPaths
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getArchiveClassPaths()
     */
    @Override
    public Path[] getArchiveClassPaths() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getCacheArchives
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getCacheArchives()
     */
    @Override
    public URI[] getCacheArchives() throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getCacheFiles
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getCacheFiles()
     */
    @Override
    public URI[] getCacheFiles() throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getLocalCacheArchives
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getLocalCacheArchives()
     */
    @Override
    public Path[] getLocalCacheArchives() throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getLocalCacheFiles
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getLocalCacheFiles()
     */
    @Override
    public Path[] getLocalCacheFiles() throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getFileClassPaths
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getFileClassPaths()
     */
    @Override
    public Path[] getFileClassPaths() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getArchiveTimestamps
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getArchiveTimestamps()
     */
    @Override
    public String[] getArchiveTimestamps() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getFileTimestamps
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getFileTimestamps()
     */
    @Override
    public String[] getFileTimestamps() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * getMaxMapAttempts
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getMaxMapAttempts()
     */
    @Override
    public int getMaxMapAttempts() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * getMaxReduceAttempts
     *
     * @see org.apache.hadoop.mapreduce.JobContext#getMaxReduceAttempts()
     */
    @Override
    public int getMaxReduceAttempts() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * progress
     *
     * @see org.apache.hadoop.util.Progressable#progress()
     */
    @Override
    public void progress() {
    }

    /*
     * (non-Javadoc)
     *
     * getTaskAttemptID
     *
     * @see org.apache.hadoop.mapreduce.TaskAttemptContext#getTaskAttemptID()
     */
    @Override
    public TaskAttemptID getTaskAttemptID() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * setStatus
     *
     * @see org.apache.hadoop.mapreduce.TaskAttemptContext#setStatus(java.lang.String)
     */
    @Override
    public void setStatus(String msg) {
    }

    /*
     * (non-Javadoc)
     *
     * getStatus
     *
     * @see org.apache.hadoop.mapreduce.TaskAttemptContext#getStatus()
     */
    @Override
    public String getStatus() {
        return null;
    }
}
