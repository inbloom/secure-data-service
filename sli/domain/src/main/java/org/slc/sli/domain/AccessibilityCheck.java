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

/*
 * This interface is for services or repositories that supply an accessibility check on an object.
 * It is useful so that a reference to an implementing class can be given to a long-running or
 * multi-step database operation (e.g. recursive delete, update) that needs to apply the check
 * at multiple points during the processing of such an operation.
 */

package org.slc.sli.domain;

public interface AccessibilityCheck {
	public boolean accessibilityCheck(String id);
}
