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


package org.slc.sli.repository;

import java.io.Serializable;

/**
 * Provides an interface for giving the mock repository information about what field an entity uses
 * for its ID.
 *
 * @author Sean Melody <smelody@wgen.net>
 *
 * @param <T>
 *            The entity type
 * @param <ID>
 *            The ID type
 */
public interface MockIDProvider<T, ID extends Serializable> {

    public ID getIDForEntity(T entity);

}
