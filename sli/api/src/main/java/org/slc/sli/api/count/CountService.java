/*
 * Copyright 2012-2014 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.count;

/**
 * Service that is used to return counts of entities that are custom to
 * specific use cases. At this time, the use case for these counts are
 * in the Data Browser tables and counts next to the links
 * 
 * @author mbrush
 *
 */

public interface CountService {
	
	public EducationOrganizationCount find();
	public EducationOrganizationCount findOne(String edOrgId);

}
