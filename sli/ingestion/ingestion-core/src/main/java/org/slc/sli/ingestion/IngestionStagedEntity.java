package org.slc.sli.ingestion;

import java.io.Serializable;
import java.util.HashSet;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IngestionStagedEntity other = (IngestionStagedEntity) obj;
        if (collectionNameAsStaged == null) {
            if (other.collectionNameAsStaged != null)
                return false;
        } else if (!collectionNameAsStaged.equals(other.collectionNameAsStaged))
            return false;
        if (edfiEntity != other.edfiEntity)
            return false;
        return true;
    }

}
