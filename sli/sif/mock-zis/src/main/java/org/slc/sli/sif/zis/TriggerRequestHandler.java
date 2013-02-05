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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Http request handler acts as a trigger
 * for the mockZis to broadcast any POSTed messages.
 * 
 * @author jtully
 * 
 */
public class TriggerRequestHandler extends AbstractRequestHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(TriggerRequestHandler.class);
    
    @Autowired
    private MockZis mockZis;
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String xmlString = getRequestString(req);
        
        LOG.info("POST to Trigger with message: \n" + xmlString);
        
        mockZis.broadcastMessage(xmlString);
        
        writeResponseString(resp, mockZis.createAckString());
    }
    
}
