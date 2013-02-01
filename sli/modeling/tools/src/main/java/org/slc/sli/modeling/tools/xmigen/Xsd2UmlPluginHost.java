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


package org.slc.sli.modeling.tools.xmigen;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;

/**
 * Provides information required by an {@link Xsd2UmlPlugin}.
 */
public interface Xsd2UmlPluginHost {

    /**
     * Ensures that there is a tag definition for the specified name and obtains the identifier.
     *
     * @param name
     *            The name of the tag definition.
     * @return The identifier of the tag definition.
     */
    Identifier ensureTagDefinitionId(final String name);

    /**
     * Returns the tag definition for the specified identifier.
     *
     * @param id
     *            The identifier of the tag definition.
     * @return The tag definition corresponding to the identifier.
     */
    TagDefinition getTagDefinition(Identifier id);

    /**
     * Returns the {@link Type} for the specified {@link Identifier}.
     *
     * @param typeId
     *            The identifier of the type.
     * @return The type corresponding to the identifier.
     */
    Type getType(Identifier typeId);

    /**
     * Get the underlying plugin hosted by this instance.
     *
     * @return Xsd2UmlHostedPlugin plugin
     */
    Xsd2UmlHostedPlugin getPlugin();
}
