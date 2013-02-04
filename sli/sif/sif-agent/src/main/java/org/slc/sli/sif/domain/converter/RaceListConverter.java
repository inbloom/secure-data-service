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

package org.slc.sli.sif.domain.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openadk.library.common.RaceList;
import openadk.library.common.RaceType;
import openadk.library.common.Race;

import org.springframework.stereotype.Component;

/**
 * A custom converter to convert SIF RaceList to SLI Race list.
 *
 * @author syau
 *
 */
@Component
public class RaceListConverter {

    private static final Map<RaceType, String> RACE_TYPE_MAP = new HashMap<RaceType, String>();
    static {
        RACE_TYPE_MAP.put(RaceType.AFRICAN_AMERICAN, "Black - African American");
        RACE_TYPE_MAP.put(RaceType.AMERICAN_INDIAN, "American Indian - Alaskan Native");
        RACE_TYPE_MAP.put(RaceType.ASIAN, "Asian");
        RACE_TYPE_MAP.put(RaceType.PACISLANDER, "Native Hawaiian - Pacific Islander");
        RACE_TYPE_MAP.put(RaceType.WHITE, "White");
    }

    public List<String> convert(RaceList raceList) {
        if (raceList == null) {
            return null;
        }

        return toSliRaceList(raceList.getRaces());
    }

    private List<String> toSliRaceList(Race[] races) {
        List<String> list = new ArrayList<String>(races.length);
        for (Race race : races) {
            RaceType rt = RaceType.wrap(race.getCode());
            if (RACE_TYPE_MAP.containsKey(rt)) {
                list.add(RACE_TYPE_MAP.get(rt));
            }
        }
        return list;
    }

}
