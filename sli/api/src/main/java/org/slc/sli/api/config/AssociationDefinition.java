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


package org.slc.sli.api.config;

import org.slc.sli.api.service.AssociationService;

/**
 * Definition of an association resource
 *
 * @author nbrown
 *
 */
public final class AssociationDefinition extends EntityDefinition {

    private final EntityDefinition sourceEntity;
    private final EntityDefinition targetEntity;
    private final String relNameFromSource;
    private final String relNameFromTarget;
    private final String sourceLink;
    private final String targetLink;
    private final String sourceKey;
    private final String targetKey;
    private final String sourceHopLink;
    private final String targetHopLink;

    protected AssociationDefinition(String type, String resourceName, String collectionName,
            AssociationService service, DefinitionFactory.EntityInfo source, DefinitionFactory.EntityInfo target) {
        super(type, resourceName, collectionName, service, false);
        this.sourceEntity = source.getDefn();
        this.targetEntity = target.getDefn();
        this.relNameFromSource = source.getLinkToAssociation();
        this.relNameFromTarget = target.getLinkToAssociation();
        this.sourceLink = source.getLinkName();
        this.targetLink = target.getLinkName();
        this.sourceHopLink = source.getHopLinkName();
        this.targetHopLink = target.getHopLinkName();
        this.sourceKey = source.getKey();
        this.targetKey = target.getKey();
    }

    /**
     * The source of the association
     *
     * @return
     */
    public EntityDefinition getSourceEntity() {
        return sourceEntity;
    }

    /**
     * The target of the association
     *
     * @return
     */
    public EntityDefinition getTargetEntity() {
        return targetEntity;
    }

    /**
     * Gets the name of the relationship as its called when coming from the source
     *
     * @return
     */
    public String getRelNameFromSource() {
        return relNameFromSource;
    }

    /**
     * Gets the name of the relationship as its called when coming from the target
     *
     * @return
     */
    public String getRelNameFromTarget() {
        return relNameFromTarget;
    }

    /**
     * The label for the link to the source
     *
     * @return
     */
    public String getSourceLink() {
        return sourceLink;
    }

    /**
     * The label for the link from the target directly to the source
     *
     * @return
     */
    public String getHoppedTargetLink() {
        return targetHopLink;
    }

    /**
     * The label for the link from the target directly to the source
     *
     * @return
     */
    public String getHoppedSourceLink() {
        return sourceHopLink;
    }

    /**
     * The label for the link to the target
     *
     * @return
     */
    public String getTargetLink() {
        return targetLink;
    }

    /**
     * The key for the target
     *
     * @return
     */
    public String getSourceKey() {
        return sourceKey;
    }

    /**
     * The key for the source
     *
     * @return
     */
    public String getTargetKey() {
        return targetKey;
    }

    @Override
    public AssociationService getService() {
        return (AssociationService) super.getService();
    }

}
