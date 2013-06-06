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

package org.slc.sli.bulk.extract.pub;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;

/**
 * Factory class that returns an instance of PublicDataExtractor.
 * @author ablum
 */
public class PublicDataFactory {

    /**
     * Builds a DirectPublicDataExtract instance.
     * @param extractor the extractor
     * @return the new PublicDataExtractor instance
     */
    public PublicDataExtractor buildDirectPublicDataExtract(EntityExtractor extractor) {
        return new DirectPublicDataExtractor(extractor);
    }

    /**
     * Builds a UnfilteredPublicDataExtractor instance.
     *
     * @param extractor the extractor
     *
     * @return the new UnfilteredPublicDataExtractor instance
     */
    public PublicDataExtractor buildUnfilteredPublicDataExtractor(EntityExtractor extractor) {
        return new UnfilteredPublicDataExtractor(extractor);
    }

    /**
     * Builds an DirectAndIndependentPublicDataExtractor instance.
     * @param extractor the extractor
     *
     * @return an DirectAndIndependentPublicDataExtractor instance
     */
    public PublicDataExtractor buildIndependentPublicDataExtractor(EntityExtractor extractor) {
        return new DirectAndIndependentPublicDataExtractor(extractor);
    }

    /**
     * Build all instances of a PublicDataExtractor.
     * @param extractor the extractor
     * @return all PublicDataExtractor instances
     */
    public List<PublicDataExtractor> buildAllPublicDataExtracts(EntityExtractor extractor) {
        List<PublicDataExtractor> list = new ArrayList<PublicDataExtractor>();
        list.add(buildDirectPublicDataExtract(extractor));
        list.add(buildUnfilteredPublicDataExtractor(extractor));
        list.add(buildIndependentPublicDataExtractor(extractor));
        return list;

    }

}
