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


package org.slc.sli.ingestion.dal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * An implementation of PathMatchingResourcePatternResolver used to resolve the directory structure that holds index information
 * @author ablum
 *
 */

public class IndexResourcePatternResolver extends PathMatchingResourcePatternResolver {

    public List<MongoIndexConfig> findAllIndexes(String directoryPattern) {

        List<MongoIndexConfig> indexConfigs = new ArrayList<MongoIndexConfig>();

        try {
            Resource[] collectionDirectories = findPathMatchingResources(directoryPattern);

            for (Resource collectionDirectory : collectionDirectories) {
                InputStream inputStream = collectionDirectory.getInputStream();
                indexConfigs.add(MongoIndexConfig.parse(inputStream));
                inputStream.close();
            }

        } catch (IOException e) {
            error("Path to index directory does not exist: " + directoryPattern);
        }

        return indexConfigs;
    }
}
