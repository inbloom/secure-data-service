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

package org.slc.sli.sif.zis;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;

/**
 * Abstract request handler for mock-zis HTTP endpoints.
 * 
 * @author jtully
 * 
 */
public abstract class AbstractRequestHandler implements HttpRequestHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRequestHandler.class);
    
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
        if (request.getMethod().equals("POST")) {
            doPost(request, response);
        }
    }
    
    protected abstract void doPost(HttpServletRequest req, HttpServletResponse resp);
    
    protected String getRequestString(HttpServletRequest req) {
        String result = "";
        InputStream xml = null;
        try {
            xml = req.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(xml, writer, "UTF-8");
            result = writer.toString();
        } catch (IOException e) {
            LOG.error("Exception reading request: ", e);
        }
        return result;
    }
    
    protected void writeResponseString(HttpServletResponse resp, String respString) {
        PrintWriter out = null;
        try {
            resp.setContentType("text/xml");
            
            out = resp.getWriter();
            out.print(respString);
            out.flush();
            
        } catch (IOException e) {
            LOG.error("Exception writing response: ", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
