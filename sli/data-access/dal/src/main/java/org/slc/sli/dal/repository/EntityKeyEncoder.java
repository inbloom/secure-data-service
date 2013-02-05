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

package org.slc.sli.dal.repository;

import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slc.sli.domain.Entity;

/**
 * Encodes keys of entity per config. See DE1782
 * 
 * Encodings are represented as a comma-separated string of colon-separated pairs,
 * the first in the pair is the regexp, the second is the replacement
 * 
 * @author syau
 *
 */
public class EntityKeyEncoder {

    // to represent encodings 
    public class Encoding {
        Pattern pattern;
        String replacement;
        public Encoding(String p, String r) {
            pattern = Pattern.compile(p);
            replacement = r;
        }
    }

    // list of encodings. The encoding is applied to each key searially.
    private List<Encoding> encodings;

    @Value("${sli.mongodb.keyencoding}")
    public void setKeyEncoding(String keyEncoding) {
        String[] e = keyEncoding.split(",");
        encodings = new ArrayList<Encoding> (e.length);
        for (int i = 0; i < e.length; i++) {
            String encodingItem = e[i];
            if (encodingItem.contains(":")) {
                String[] itemPair = encodingItem.split(":");
                encodings.add(new Encoding(itemPair[0], itemPair[1]));
            } else {
                // Error out?
                throw new RuntimeException ("EntityKeyEncoder: Malformed encoding: " + keyEncoding);
            }
        }
    }
    
    /**
     * Encodes the keys in the entity's body recursively
     */
    public void encodeEntityKey(Entity entity) {
        Map<String, Object> body = entity.getBody();
        if (body != null) {
            encodeKey(body);
        }
    }

    @SuppressWarnings("unchecked")
    private void encodeKey(Map map) {
        Set keys = new HashSet(map.keySet()); // cloning keyset to void concurrent modification
        // for each key
        for (Object o : keys) {
            if (!(o instanceof String))  {
                continue;
            }
            String key = (String) o;
            // apply encodings serially
            String newKey = key;
            for (Encoding encoding : encodings) {
                Matcher matcher = encoding.pattern.matcher(newKey);
                if (matcher.find()) {
                    newKey = matcher.replaceAll(encoding.replacement);
                }
            }
            // replace keys in the map with encoded ones
            if (!key.equals(newKey)) {
                map.put(newKey, map.get(key));
                map.remove(key);
            }
        }
        // recurse
        for (Object value : map.values()) {
            if (value instanceof Map) {
                encodeKey((Map) value);
            } else if (value instanceof Collection) {
                Collection c = (Collection) value;
                for (Object o : c) {
                    if (o instanceof Map) {
                        encodeKey((Map) o);
                    }
                }
            }
        }
    }
}
