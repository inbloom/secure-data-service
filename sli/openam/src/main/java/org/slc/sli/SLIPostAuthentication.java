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


package org.slc.sli;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.spi.AMPostAuthProcessInterface;
import com.sun.identity.authentication.spi.AuthenticationException;

public class SLIPostAuthentication implements AMPostAuthProcessInterface {
    
    @Override
    public void onLoginFailure(Map arg0, HttpServletRequest arg1, HttpServletResponse arg2) throws AuthenticationException {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onLoginSuccess(Map requestParams, HttpServletRequest request, HttpServletResponse response, SSOToken token) throws AuthenticationException {
        log(token.getTokenID());
        
        for (Object key : requestParams.keySet()) {
            log(key + " -> " + requestParams.get(key));
        }
        
        String sessionId = token.getTokenID().toString();
        
        response.setContentType("text/html");
        response.addHeader("X-sli-session", sessionId);
        response.setHeader("X-sli-session", sessionId);
        
        Cookie c = new Cookie("sliSessionId", sessionId);
        c.setPath("/");
        c.setDomain(".slidev.org");
        c.setMaxAge(5 * 60);
        
        response.addCookie(c);
        
        Cookie c2 = new Cookie("sliSessionId", sessionId);
        c2.setPath("/");
        c2.setDomain(".dk.com");
        c2.setMaxAge(5 * 60);
        response.addCookie(c2);
        
        Cookie c3 = new Cookie("fversion", "1:51");
        c.setPath("/");
        c.setDomain(".slidev.org");
        c.setMaxAge(5 * 60);
        response.addCookie(c3);
        
    }
    
    @Override
    public void onLogout(HttpServletRequest arg0, HttpServletResponse arg1, SSOToken arg2) throws AuthenticationException {
        // TODO Auto-generated method stub
        
    }
    
    private static void log(Object msg) {
        System.out.println(msg.toString());
    }
}
