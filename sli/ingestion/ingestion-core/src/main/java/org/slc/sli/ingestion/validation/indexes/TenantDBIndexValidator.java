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
package org.slc.sli.ingestion.validation.indexes;

import java.util.List;
import java.util.Set;

import com.mongodb.DB;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.IndexFileParser;
import org.slc.sli.ingestion.util.MongoIndex;


/**
 * @author tke
 *
 */
public class TenantDBIndexValidator extends DbIndexValidator {

    private static final String INDEX_FILE = "tenantDB_indexes.txt";

    @Autowired
    IndexFileParser indexTxtFileParser;

    @Autowired
    private TenantDA tenantDA;

    @Override
    protected Set<MongoIndex> loadExpectedIndexes() {
        return indexTxtFileParser.parse(INDEX_FILE);
    }

    @Override
    public boolean isValid(DB db, AbstractMessageReport report, AbstractReportStats reportStats) {
        List<String> tenantDbs = tenantDA.getAllTenantDbs();

        boolean isValid = true;

        for (String tenantDb : tenantDbs) {
            isValid &= super.isValid(db.getSisterDB(tenantDb), report, reportStats);
        }

        return isValid;
    }
}
