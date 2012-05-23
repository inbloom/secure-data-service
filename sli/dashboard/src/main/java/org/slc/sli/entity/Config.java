package org.slc.sli.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.slc.sli.util.DashboardException;
import org.slc.sli.web.util.NoBadChars;

/**
 * Main config object for dashboard components
 * Immutable
 *
 * @author agrebneva
 *
 */
public class Config implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Type of components
     *
     * @author agrebneva
     *
     */
    public enum Type {
        LAYOUT(true), PANEL(true), GRID(true), TAB(false), WIDGET(true), FIELD(false), EXPAND(false);

        private boolean hasOwnConfig;

        private Type(boolean hasOwnConfig) {
            this.hasOwnConfig = hasOwnConfig;
        }

        public boolean hasOwnConfig() {
            return hasOwnConfig;
        }

        public boolean isLayoutItem() {
            return this == LAYOUT;
        }
    }

    /**
     * Subcomponent of the config
     *
     * @author agrebneva
     *
     */
    public static class Item extends Config {
        private static final long serialVersionUID = 1L;
        @Pattern(regexp = "[a-zA-Z0-9 \\-/\\+()\"':]{0,150}")
        protected String description;
        // Field is a json hierarchy with nodes delimited by period and can use optional single quote
        @Pattern(regexp = "[a-zA-Z0-9 \\.\\-_\\\\]{0,100}")
        protected String field;
        @Pattern(regexp = "[a-zA-Z0-9 -/\\+()\"':]{0,150}")
        protected String value;
        @Pattern(regexp = "[a-zA-Z0-9]{0,20}")
        protected String width;
        @Pattern(regexp = "[a-zA-Z0-9]{0,20}")
        protected String datatype;
        @Pattern(regexp = "[a-zA-Z0-9]{0,20}")
        protected String color;
        @Pattern(regexp = "[a-zA-Z0-9.-]{0,40}")
        protected String style;
        @Pattern(regexp = "[a-zA-Z0-9 \\.\\-]{0,30}")
        protected String formatter;
        @Pattern(regexp = "[a-zA-Z0-9 \\.\\-]{0,30}")
        protected String sorter;
        @Pattern(regexp = "[a-zA-Z0-9 \\.\\-]")
        protected String align;

        @NoBadChars
        @Size(max = 30)
        protected Map<String, Object> params;

        public String getDescription() {
            return description;
        }

        public String getField() {
            return field;
        }

        public String getValue() {
            return value;
        }

        public String getWidth() {
            return width;
        }

        public String getColor() {
            return color;
        }

        public String getStyle() {
            return style;
        }

        public String getFormatter() {
            return formatter;
        }

        public String getSorter() {
            return sorter;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public String getDatatype() {
            return datatype;
        }

        public String getAlign() {
            return align;
        }

        public void setAlign(String align) {
            this.align = align;
        }

        @Override
        public Config cloneWithItems(Config.Item[] items) {
            Item item;
            try {
                item = (Item) this.clone();
            } catch (CloneNotSupportedException e) {
                throw new DashboardException("Unable to clone items", e);
            }
            item.items = items;
            return item;
        }

        public Item cloneWithName(String name) {
            Item item;
            try {
                item = (Item) this.clone();
            } catch (CloneNotSupportedException e) {
                throw new DashboardException("Unable to clone items", e);
            }
            item.name = name;
            return item;
        }

        public Item cloneWithParams(String name, String field) {
            Item item;
            try {
                item = (Item) this.clone();
            } catch (CloneNotSupportedException e) {
                throw new DashboardException("Unable to clone items", e);
            }
            item.name = name;
            item.field = field;
            return item;
        }

        @Override
        public String toString() {
            return "ViewItem [width=" + width + ", type=" + datatype + ", color=" + color + ", style=" + style
                    + ", formatter=" + formatter + ", params=" + params + "]";
        }
    }

    /**
     * Data component of the config
     *
     * @author agrebneva
     *
     */
    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        @Pattern(regexp = "[a-zA-Z0-9]{0,50}")
        protected String entity;
        @Pattern(regexp = "[a-zA-Z0-9 ]{0,50}")
        protected String cacheKey;
        @Size(max = 30)
        @NoBadChars
        protected Map<String, Object> params;
        protected boolean lazy;

        public Data() {
        }

        public Data(String entity, String cacheKey, boolean lazy, Map<String, Object> params) {
            this.entity = entity;
            this.cacheKey = cacheKey;
            this.params = params;
            this.lazy = lazy;
        }

        public String getEntityRef() {
            return entity;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public boolean isLazy() {
            return lazy;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((cacheKey == null) ? 0 : cacheKey.hashCode());
            result = prime * result + ((entity == null) ? 0 : entity.hashCode());
            result = prime * result + (lazy ? 1231 : 1237);
            if (params != null) {
                for (Object value : params.values()) {
                      result = prime * result + ((value == null) ? 0 : value.hashCode());
                  }
            }
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
            Data other = (Data) obj;
            if (cacheKey == null) {
                if (other.cacheKey != null) {
                    return false;
                }
            } else if (!cacheKey.equals(other.cacheKey)) {
                return false;
            }
            if (entity == null) {
                if (other.entity != null) {
                    return false;
                }
            } else if (!entity.equals(other.entity)) {
                return false;
            }
            if (lazy != other.lazy) {
                return false;
            }
            if (params == null) {
                if (other.params != null) {
                    return false;
                }
            } else if (!params.equals(other.params)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Data-related condition on the item
     *
     * @author agrebneva
     *
     */
    public static class Condition implements Serializable {
        private static final long serialVersionUID = 1L;
        @Pattern(regexp = "[a-zA-Z0-9 \\.\\-]{0,30}")
        protected String field;
        protected Object[] value;

        public String getField() {
            return field;
        }

        public Object[] getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Condition [field=" + field + ", value=" + Arrays.toString(value) + "]";
        }
    }

    @Pattern(regexp = "[a-zA-Z0-9]{1,30}")
    protected String id;
    /**
     * if id of the parent is different from the id - in case when many similar panels share the driver
     */
    @Pattern(regexp = "[a-zA-Z0-9]{0,30}")
    protected String parentId;
    @Pattern(regexp = "[a-zA-Z0-9 \\-/\\+()\"':\\.%]{0,150}")
    protected String name;

    protected Type type = Type.FIELD;
    @Valid
    protected Condition condition;
    @Valid
    protected Data data;
    @Valid
    protected Item[] items;
    @Pattern(regexp = "[a-zA-Z0-9 \\.\\-]{0,30}")
    protected String root;

    public Config(String id, String parentId, String name, Type type, Condition condition, Data data, Item[] items, String root) {
        super();
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.data = data;
        this.items = items;
        this.root = root;
    }

    public Config() {
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId == null ? id : parentId;
    }

    public String getName() {
        return name;
    }

    public String getRoot() {
        return root;
    }

    public Type getType() {
        return type;
    }

    public Condition getCondition() {
        return condition;
    }

    public Data getData() {
        return data;
    }

    public Item[] getItems() {
        return items;
    }

    public Config cloneWithItems(Item[] items) {
        return new Config(id, parentId, name, type, condition, data, items, root);
    }

    /**
     * use this method if Config object is required to have a duplicate copy. Config object should
     * be immutable in order to avoid confusions.
     * It creates a cloned (deep copy) Config object except Config.Data.entity and Config.Data.param
     * (these values are overwritten by Config object an input param)
     *
     * @param customConfig
     *            Config.Data.entity and Config.Data.param are used to overwrite to a cloned Config
     *            object
     * @return cloned Config object merged with customConfig
     */
    public Config overWrite(Config customConfig) {
        // parent id for overwrite should be the same as id of the driver
        Config config = new Config(this.id, this.id, customConfig.name, this.type, this.condition, new Data(this.data.entity,
                customConfig.data.cacheKey, this.data.lazy, customConfig.data.params == null ? null
                        : Collections.unmodifiableMap(new HashMap<String, Object>(customConfig.data.params))),
                (customConfig.items == null) ? this.items : customConfig.items, this.root);
        return config;
    }
}