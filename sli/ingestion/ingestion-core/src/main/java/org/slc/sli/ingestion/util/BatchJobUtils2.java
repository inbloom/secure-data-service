/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.ingestion.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class BatchJobUtils2
{

    private static InetAddress localhost;
    static {
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHostAddress() {
        return localhost.getHostAddress();
    }

    public static String getHostName() {
        return localhost.getHostName();
    }

    public static Date getCurrentTimeStamp() {
        return new Date();
    }

}
