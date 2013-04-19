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
package org.slc.sli.bulk.extract.files;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * Support multiple output stream.
 * @author tke
 *
 */
public class MultiOutputStream extends OutputStream {

    private List<OutputStream> outputStreams = new ArrayList<OutputStream>();

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(byte[] data) throws IOException {
        for(OutputStream stream : outputStreams){
            stream.write(data);
        }
    }

    @Override
    public void write(byte [] buffer, int offset, int len) throws IOException{
        for(OutputStream stream : outputStreams) {
            stream.write(buffer, offset, len);
        }
    }

    @Override
    public void close() throws IOException{
        for(OutputStream stream : outputStreams) {
            IOUtils.closeQuietly(stream);
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        for(OutputStream stream : outputStreams){
            stream.write(b);
        }
    }

    /**
     * add a new outputStream.
     * @param outputStream outputStream to be added.
     */
    public void addStream(OutputStream outputStream) {
        outputStreams.add(outputStream);
    }

    @Override
    public void flush() throws IOException {
        for(OutputStream stream : outputStreams) {
            stream.flush();
        }
    }

}
