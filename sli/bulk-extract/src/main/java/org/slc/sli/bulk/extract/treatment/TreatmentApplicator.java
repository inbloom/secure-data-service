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

import java.util.List;

import org.slc.sli.domain.Entity;

/**
 * treatment applicator that applies all available treatments.
 * @author ablum
 *
 */
public class TreatmentApplicator implements Treatment {
    List<Treatment> treatments;

    @Override
    public Entity apply(Entity entity) {
        Entity treated = entity;
        for (Treatment treatment : treatments) {
            treated = treatment.apply(treated);
        }
        return treated;
    }

    /**
     * get treatments.
     * @return list<Treatment>
     */
    public List<Treatment> getTreatments() {
        return treatments;
    }

    /**
     * set treatments.
     * @param treatments treatments
     */
    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

}
