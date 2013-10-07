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
package org.slc.sli.bulk.extract.treatment;

import java.util.Map;

import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.migration.strategy.impl.AttendanceStrategyHelper;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Treat attendance entity.
 * @author ablum
 *
 */
public class AttendanceTreatment implements Treatment {
    
    private static final Logger LOG = LoggerFactory.getLogger(AttendanceTreatment.class);

    @Override
    public Entity apply(Entity entity) {
        // Don't apply the treatment if it already has been applied.
        // It can get treated multiple times for different LEAs and Apps.
        if (entity.getBody().containsKey("schoolYearAttendance")) {
            LOG.debug("Treatment has already been applied to attendance entity: {}", new Object[] { entity.getEntityId() });
            entity.getBody().remove(ParameterConstants.SCHOOL_YEAR);
            return entity;
        }
        Map<String,Object> treated = AttendanceStrategyHelper.wrap(entity.getBody());
        entity.getBody().clear();
        entity.getBody().putAll(treated);
        return entity;
    }

}
