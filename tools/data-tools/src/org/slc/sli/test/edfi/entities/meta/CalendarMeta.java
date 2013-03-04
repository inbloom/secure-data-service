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


package org.slc.sli.test.edfi.entities.meta;

import org.slc.sli.test.generators.CalendarDateGenerator;

public class CalendarMeta {
     
    public final String id;
    
    public String sessionId;
    
    public String gradingPeriodId;
    
    public String date;
    
    public CalendarMeta (String id, SessionMeta sessionMeta) {
        this.id = id;
        this.sessionId = sessionMeta.id;    
        this.date = CalendarDateGenerator.generatDate();
    }
    
    
    public CalendarMeta (String id, GradingPeriodMeta gradingPeriodMeta) {
        this.id = id;
        this.gradingPeriodId = gradingPeriodMeta.id;
        this.date = CalendarDateGenerator.generatDate();
    }
    
    @Override
    public String toString() {
        return "CalendarMeta [id=" + id + ", sessionId=" + sessionId + "]";
    }

}
