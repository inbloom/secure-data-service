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

package org.slc.sli.ingestion.transformation;

import java.util.List;

import org.mockito.ArgumentMatcher;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Util class for NeutralQuery match
 *
 * @author slee
 *
 */
public class IsCorrectNeutralQuery extends ArgumentMatcher<NeutralQuery> {
    private NeutralQuery query;

    public IsCorrectNeutralQuery(NeutralQuery query) {
        this.query = query;
    }

    @Override
    public boolean matches(Object argument) {
        if (query != null) {
            return matches((NeutralQuery) argument);
        }
        return false;
    }

    private boolean matches(NeutralQuery arg) {
        List<NeutralCriteria> queryCriteria = query.getCriteria();
        List<NeutralCriteria> argCriteria = arg.getCriteria();

        if (queryCriteria.size() != argCriteria.size()) {
            return false;
        }

        for (int i = 0; i < queryCriteria.size(); ++i) {
            if (!queryCriteria.get(i).equals(argCriteria.get(i))) {
                return false;
            }
        }

        return true;
    }

}
