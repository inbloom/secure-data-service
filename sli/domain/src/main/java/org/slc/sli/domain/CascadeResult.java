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

    public enum Status {
        SUCCESS,
        MAX_OBJECTS_EXCEEDED,
        ACCESS_DENIED,
        DATABASE_ERROR,
        CHILD_DATA_EXISTS,
        MAX_DEPTH_EXCEEDED
    }

    private int nObjects;		    // Number of objects affected
    private int	   depth;			// Furthest depth examined
    private Status status;			// Current/overall status of the operation

    private String message;          // Status message e.g. error text - null on SUCCESS

    // Used to identify the error object for ACCESS_DENIED and CHILD_DATA_EXISTS status
    private String objectType;
    private String objectId;

    public CascadeResult() {
        nObjects = 0;
        depth = 0;
        status = Status.SUCCESS; // Until further notice
        message = null;
        objectType = null;
        objectId = null;
    }

    public CascadeResult(int nObjects, int depth, Status status, String message, String objectId, String objectType) {
        this.nObjects = nObjects;
        this.depth = depth;
        this.status = status;
        this.message = message;
        this.objectId = objectId;
        this.objectType = objectType;
    }

    public CascadeResult setFields(int nObjects, int depth, Status status, String message, String objectId, String objectType) {
        this.nObjects = nObjects;
        this.depth = depth;
        this.status = status;
        this.message = message;
        this.objectId = objectId;
        this.objectType = objectType;
        return this;
    }

    // Return true if the operation is a success
	public boolean success() {
		return status == Status.SUCCESS;
	}

    public int getnObjects() {
        return nObjects;
    }

    public void setnObjects(int nObjects) {
        this.nObjects = nObjects;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void incrementDepth() {
        depth++;
    }

    public int getDepth() {
        return depth;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
