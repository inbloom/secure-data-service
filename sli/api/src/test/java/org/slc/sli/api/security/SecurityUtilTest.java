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


package org.slc.sli.api.security;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.enums.Right;

/**
 * Tests sudo runs
 * Validates that while running the process has elevated privilege
 * and that once done, original privileges are restored
 *
 * @author dkornishev
 *
 */
public class SecurityUtilTest {

    @Before
    public void init() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testSudo() {
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        SecurityUtil.sudoRun(new SecurityTask<String>() {
            @Override
            public String execute() {
                Assert.assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Right.FULL_ACCESS));
                return "";
            }
        });

        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
