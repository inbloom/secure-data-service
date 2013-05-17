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


package org.slc.sli.ingestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Describes an entity that has been staged by ingestion.
 *
 * @author dduran
 *
 */
public class IngestionStagedEntity implements Serializable {

    private static final long serialVersionUID = 2248791042291917912L;

    private final String collectionNameAsStaged;

    private final EdfiEntity edfiEntity;

    public IngestionStagedEntity(String parsedCollectionName, EdfiEntity edfiEntity) {
        this.collectionNameAsStaged = parsedCollectionName;
        this.edfiEntity = edfiEntity;
    }

    /**
     * The name of the collection as it has been parsed/staged by ingestion.
     * It may or may not be equal to the corresponding EdFi entity name.
     *
     * @return
     */
    public String getCollectionNameAsStaged() {
        return collectionNameAsStaged;
    }

    /**
     * The corresponding Edfi entity.
     *
     * @return
     */
    public EdfiEntity getEdfiEntity() {
        return edfiEntity;
    }

    @Override
    public String toString() {
        return "{ collectionNameAsStaged: \"" + collectionNameAsStaged + "\", edfiEntity: \"" + edfiEntity
                + "\"}";
    }

    /**
     * Removes entities which have parent entities in the same set
     *
     * @param impure
     *            set of edfi entities to purify
     * @return purifed set which only contains entities which have no dependencies in the provided
     *         set
     */
    public static Set<IngestionStagedEntity> cleanse(Set<IngestionStagedEntity> impure) {
        Set<IngestionStagedEntity> pure = new HashSet<IngestionStagedEntity>(impure);

        for (IngestionStagedEntity outer : impure) {
            if (outer.getEdfiEntity() != null) {
                for (IngestionStagedEntity inner : impure) {
                    if (outer != inner && inner.getEdfiEntity() != null
                            && outer.getEdfiEntity().getNeededEntities().contains(inner.getEdfiEntity())) {
                        pure.remove(outer);
                        break;
                    }
                }
            }
        }

        return pure;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((collectionNameAsStaged == null) ? 0 : collectionNameAsStaged.hashCode());
        result = prime * result + ((edfiEntity == null) ? 0 : edfiEntity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IngestionStagedEntity other = (IngestionStagedEntity) obj;
        if (collectionNameAsStaged == null) {
            if (other.collectionNameAsStaged != null) {
                return false;
            }
        } else if (!collectionNameAsStaged.equals(other.collectionNameAsStaged)) {
            return false;
        }
        if (edfiEntity != other.edfiEntity) {
            return false;
        }
        return true;
    }

    public static List<String> toEntityNames(Set<IngestionStagedEntity> stagedEntities) {
        List<String> name = new ArrayList<String>();

        for (IngestionStagedEntity entity : stagedEntities) {
            name.add(entity.getCollectionNameAsStaged());
        }

        return name;
    }

    public static IngestionStagedEntity createFromRecordType(String recordType) {
        IngestionStagedEntity ingestionStagedEntity = null;
        EdfiEntity stagedEdfiEntity = EdfiEntity.fromEntityName(recordType);
        if (stagedEdfiEntity != null) {

            ingestionStagedEntity = new IngestionStagedEntity(recordType,
                    stagedEdfiEntity);
        }

        return ingestionStagedEntity;
    }

}
