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
 * This class represents the result of a step in a database operation that
 * cascades recursively across referring objects.  It is meant to act both
 * as an accumulator (e.g. count of objects, errors encountered, etc.) as
 * well as the composition of the final result of the operation.
 */
package org.slc.sli.domain;

public class CascadeResult {

	public	int 	nObjects;		// Number of objects affected
	public	int		depth;			// Depth we are at
	public	int		status;			// Current/overall status of the operation

	public String errorMessage;

	public static final int   SUCCESS 			 = 0x0;
	public static final int	MAX_OBJECTS_EXCEEDED = 0x1;
    public static final int ACCESS_DENIED        = 0x2;
    public static final int DATABASE_ERROR       = 0x3;

	public CascadeResult() {
		nObjects = 0;
		depth = 0;
		status = SUCCESS; // Until further notice
	}
	// Return true if the operation is a success
	public boolean success() {
		return status == SUCCESS;
	}

}
