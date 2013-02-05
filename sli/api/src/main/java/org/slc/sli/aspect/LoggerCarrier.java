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


package org.slc.sli.aspect;

import org.slc.sli.common.util.logging.SecurityEvent;

/**
 * Logging "super class" injected into all sli classes via aspectj
 * inter-type declaration.  Java doesn't have mixins :(
 *
 * @author dkornishev
 *
 */
public interface LoggerCarrier {
    public void debug(String msg);
    public void info(String msg);
    public void warn(String msg);
    public void debug(String msg, Object... params);
    public void info(String msg, Object... params);
    public void warn(String msg, Object... params);
    public void error(String msg, Throwable x);
    public void audit(SecurityEvent event);
    public void auditLog(SecurityEvent event);
}
