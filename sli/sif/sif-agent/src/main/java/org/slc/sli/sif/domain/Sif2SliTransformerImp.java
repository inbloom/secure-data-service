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

package org.slc.sli.sif.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import openadk.library.datamodel.SEAInfo;
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.sifentity.LEAInfoEntity;
import org.slc.sli.sif.domain.sifentity.SEAInfoEntity;
import org.slc.sli.sif.domain.sifentity.SchoolInfoEntity;
import org.slc.sli.sif.domain.slientity.LEAEntity;
import org.slc.sli.sif.domain.slientity.SEAEntity;
import org.slc.sli.sif.domain.slientity.SchoolEntity;

/**
 * Transformer for mapping entities from SIF domain to SLI domain.
 *
 * @author slee
 *
 */
@Component
public class Sif2SliTransformerImp implements Sif2SliTransformer
{
    protected static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private Mapper dozerMapper;

    @Override
    public Map<String, Object> transform(SchoolInfo schoolInfo) {
        try
        {
            SchoolEntity e = this.dozerMapper.map(new SchoolInfoEntity(schoolInfo), SchoolEntity.class);
            Map<String, Object> body = mapper.readValue(e.json(), new TypeReference<Map<String, Object>>(){});
            clearNullValueKeys (body);
            return body;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> transform(LEAInfo leaInfo) {
        try {
            LEAEntity e = this.dozerMapper.map(new LEAInfoEntity(leaInfo), LEAEntity.class);
            Map<String, Object> body = mapper.readValue(e.json(), new TypeReference<Map<String, Object>>(){});
            clearNullValueKeys (body);
            return body;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> transform(SEAInfo seaInfo) {
        try {
            SEAEntity e = this.dozerMapper.map(new SEAInfoEntity(seaInfo), SEAEntity.class);
            Map<String, Object> body = mapper.readValue(e.json(), new TypeReference<Map<String, Object>>(){});
            clearNullValueKeys (body);
            return body;
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<String, Object>();
    }

    //============ private helper

    // removes all keys from this map that has a null value. If some values are maps,
    // do it recursively
    private static void clearNullValueKeys(Map m) {
        Set keySet = m.keySet();
        Set keysToRemove = new HashSet();
        for (Object k : keySet) {
            if (isNullValue(m.get(k))) {
                keysToRemove.add(k);
            }
        }
        for (Object k : keysToRemove) {
            m.remove(k);
        }
    }
    private static void clearNullValueFromList(List l) {
        ListIterator it = l.listIterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (isNullValue(o)) {
                it.remove();
            }
        }
    }
    private static boolean isNullValue (Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof Map) {
            clearNullValueKeys((Map) o);
            return ((Map)o).isEmpty();
        } else if (o instanceof List) {
            clearNullValueFromList((List) o);
            return ((List)o).isEmpty();
        }
        return false;
    }
}

