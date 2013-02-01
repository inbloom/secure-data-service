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

package org.slc.sli.domain;

import java.io.Serializable;

/**
 * A value in an aggregate
 *
 * @author nbrown
 * @param <T>
 *
 */
public class CalculatedDatum<T> implements Serializable {
    private static final long serialVersionUID = -2950228193252067690L;
    private final String type;
    private final String window;
    private final String name;
    private final String method;
    private final T value;

    public CalculatedDatum(String type, String window, String name, String method, T value) {
        super();
        this.type = type;
        this.window = window;
        this.name = name;
        this.method = method;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getWindow() {
        return window;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CalculatedDatum [type=" + type + ", window=" + window + ", name=" + name + ", methodology=" + method
                + ", value=" + value + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((window == null) ? 0 : window.hashCode());
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
        CalculatedDatum<?> other = (CalculatedDatum<?>) obj;
        if (method == null) {
            if (other.method != null) {
                return false;
            }
        } else if (!method.equals(other.method)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        if (window == null) {
            if (other.window != null) {
                return false;
            }
        } else if (!window.equals(other.window)) {
            return false;
        }
        return true;
    }

}
