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


package org.slc.sli.ingestion;

import java.io.Serializable;

/**
 * Fault that represents a warning or error in the system.
 *
 * @author okrook
 *
 */
public class Fault implements Serializable {

    private static final long serialVersionUID = 4237833853237555339L;

    protected String message;
    protected FaultType type;

    protected Fault(FaultType type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Fault createWarning(String message) {
        return new Fault(FaultType.TYPE_WARNING, message);
    }

    public static Fault createError(String message) {
        return new Fault(FaultType.TYPE_ERROR, message);
    }

    public String getMessage() {
        return message;
    }

    public boolean isWarning() {
        return this.type == FaultType.TYPE_WARNING;
    }

    public boolean isError() {
        return this.type == FaultType.TYPE_ERROR;
    }

    @Override
    public String toString() {
        return type.getName() + ": " + getMessage();
    }
}
