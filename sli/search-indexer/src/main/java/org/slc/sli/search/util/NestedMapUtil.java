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
package org.slc.sli.search.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Operations for nested maps
 *
 */
public class NestedMapUtil {
    private static final String _ARRAY_ELEM = "$";

    /**
     * Move field specified from node from to node to
     * @param fieldChainFrom - path to the nested field to move from
     * @param fieldChainTo - new path
     * @param entity - nested map
     */
    public static void rename(DotPath fieldChainFrom, DotPath fieldChainTo, Map<String, Object> entity) {
        put(fieldChainTo, remove(fieldChainFrom, entity), entity);
    }
    /**
     * Remove field specified by its pathname
     * @param fieldChain - path to the nested field
     * @param entity - nested map
     * @return - removed object if found
     */
    public static Object remove(DotPath fieldChain, Map<String, Object> entity) {
        return findRecursively(fieldChain, entity, true, 0);
    }

    /**
     * Remove field specified by its pathname
     * @param fieldChain - path to the nested field
     * @param entity - nested map
     * @return - removed object if found
     */
    public static Object get(DotPath fieldChain, Object entity) {
        return findRecursively(fieldChain, entity, false, 0);
    }

    /**
     * Put field specified by a pathname chain into a map
     * @param fieldChain - path to the nested field
     * @param value - what to add
     * @param entity - where to add
     * @return previous value or null
     */
    public static Object put(DotPath fieldChain, Object value, Map<String, Object> entity) {
        return insertRecursively(fieldChain, value, entity, 0);
    }

    /**
     * Merge entities
     * @param fieldChain - path to the nested field
     * @param value - what to add
     * @param entity - where to add
     * @return true if entityTo changes, false otherwise
     */
    public static boolean merge(Map<String, Object> entityTo, Map<String, Object> entityFrom) {
        Map<String, Object> map = toFlatMap(entityFrom);
        boolean changed = false;
        for (Map.Entry<String, Object> entry: map.entrySet()) {
            changed |= !isSame(put(new DotPath(entry.getKey()), entry.getValue(), entityTo), entry.getValue()) ;
        }
        return changed;
    }

    private static boolean isSame(Object o1, Object o2) {
        return (o1 == null && o2 == null || o1 != null && o1.equals(o2));
    }

    /**
     * Filter out entries unless the key is in the list
     * @param nodeNames - list f keys to preserve
     * @param entity - entity to filter out from
     */
    public static void filterExcept(List<String> nodeNames, Map<String, Object> entity) {
        filterExceptRecursively(nodeNames, entity, 0);
    }

    public static Map<String, Object> toFlatMap(Map<String, Object> entity) {
        Map<String , Object> flatMap = new HashMap<String, Object>();
        toFlatMapRecursively(DotPath.EMPTY, entity, flatMap, 0);
        return flatMap;
    }

    @SuppressWarnings("unchecked")
    private static Object findRecursively(DotPath fieldChainOrig, Object entity, boolean delete, int count) {
        if (fieldChainOrig.isEmpty()) {
            return entity;
        }
        DotPath fieldChain = fieldChainOrig.clone();
        if (count > 10) {
            throw new IllegalArgumentException("Recursion too deep");
        }
        if (entity instanceof Map) {
            String field = fieldChain.remove(0);
            Map<String, Object> map = (Map<String, Object>) entity;
            if (fieldChain.isEmpty()) {
                return (delete) ? map.remove(field) : map.get(field);
            }
            Object tempEntity = findRecursively(fieldChain, map.get(field), delete, count + 1);
            Object obj = map.get(field);
            if (delete && obj != null && obj instanceof Map) {
                Map<String, Object> sub = (Map<String, Object>)obj;
                if (sub.isEmpty()) {
                    map.remove(field);
                }
            }
            return tempEntity;
        } else if (entity instanceof List) {
            List<Object> arr = (List<Object>)entity;
            if (!arr.isEmpty()) {
                String field = fieldChain.get(0);
                if (_ARRAY_ELEM.equals(field)) {
                    fieldChain.remove(0);
                    return findRecursively(fieldChain, arr.get(0), delete, count + 1);
                } else {
                    List<Object> filteredArray = new ArrayList<Object>();
                    for (Object o : arr) {
                        filteredArray.add(findRecursively(fieldChain, o, delete, count + 1));
                    }
                    return filteredArray;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object insertRecursively(DotPath fieldChainOrig, Object value, Object entity, int count) {
        if (fieldChainOrig.isEmpty()) {
            return null;
        }
        if (count > 10) {
            throw new IllegalArgumentException("Recursion too deep");
        }
        DotPath fieldChain = fieldChainOrig.clone();
        String field = fieldChain.remove(0);
        if (entity instanceof Map) {
            Map<String, Object> map = (Map<String, Object>)entity;
            if (fieldChain.isEmpty()) {
                return map.put(field, value);
            } else {
                Object tmp = map.get(field);
                if (tmp == null) {
                    tmp = new HashMap<String, Object>();
                    map.put(field, tmp);
                }
                if (tmp instanceof Map) {
                    return insertRecursively(fieldChain, value, tmp, count + 1);
                }
            }
        }
        throw new IllegalArgumentException("Unable to rename a value: node exists and not a map");
    }

    @SuppressWarnings("unchecked")
    private static void filterExceptRecursively(List<String> nodeNames, Object entity, int count) {
        if (entity == null) {
            return;
        }
        if (count > 10) {
            throw new IllegalArgumentException("Recursion too deep");
        }
        if (entity instanceof Map) {
            Map<String, Object> map = (Map<String, Object>)entity;
            Set<String> toDelete = new HashSet<String>(map.keySet());
            toDelete.removeAll(nodeNames);
            for (String s : toDelete) {
                map.remove(s);
            }
            Object tmp;
            for (String key: map.keySet()) {
                tmp = map.get(key);
                if (tmp != null) {
                    filterExceptRecursively(nodeNames, tmp, count + 1);
                }
            }
        } else if (entity instanceof List) {
            List<Object> arrayElems = (List<Object>)entity;
            for (Object o : arrayElems) {
                if (o instanceof Map) {
                    filterExceptRecursively(nodeNames, o, count);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void toFlatMapRecursively(DotPath fieldChainOrig, Object entity, Map<String, Object> flatMap, int count) {
        if (entity == null) {
            return;
        }
        if (count > 10) {
            throw new IllegalArgumentException("Recursion too deep");
        }
        DotPath nodeNames = fieldChainOrig.clone();
        if (entity instanceof Map) {
            Map<String, Object> map = (Map<String, Object>)entity;
            Object tmp;
            int lastElemIndex = nodeNames.size();
            for (String key: map.keySet()) {
                tmp = map.get(key);
                if (tmp != null) {
                    nodeNames.add(key);
                    toFlatMapRecursively(nodeNames, tmp, flatMap, count + 1);
                    nodeNames.remove(lastElemIndex);
                }
            }
        } else {
            flatMap.put(nodeNames.toString(), entity);
        }
    }

}
