package org.slc.sli.domain;

/**
 * A value in an aggregate
 *
 * @author nbrown
 *
 */
public class AggregateDatum {
    private final String type;
    private final String window;
    private final String name;
    private final String methodology;
    private final Object value;

    public AggregateDatum(String type, String window, String name, String methodology, Object value) {
        super();
        this.type = type;
        this.window = window;
        this.name = name;
        this.methodology = methodology;
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

    public String getMethodology() {
        return methodology;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "AggregateDatum [type=" + type + ", window=" + window + ", name=" + name + ", methodology="
                + methodology + ", value=" + value + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((methodology == null) ? 0 : methodology.hashCode());
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
        AggregateDatum other = (AggregateDatum) obj;
        if (methodology == null) {
            if (other.methodology != null) {
                return false;
            }
        } else if (!methodology.equals(other.methodology)) {
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
