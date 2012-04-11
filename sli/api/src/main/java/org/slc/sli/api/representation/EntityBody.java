package org.slc.sli.api.representation;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Contents of an entity body
 *
 * @author nbrown
 *
 */
public class EntityBody extends HashMap<String, Object> {

    private static final long serialVersionUID = -301785504415342449L;

    public EntityBody() {
        super();
    }

    public EntityBody(Map<? extends String, ? extends Object> m) {
        super(m);
    }

    @SuppressWarnings("unchecked")
    public List<String> getId(String idKey) {
        List<String> idList = new ArrayList<String>();
        Object value = this.get(idKey);
        if (value instanceof String) {
            idList.add((String) value);
        } else if (value instanceof List<?>) {
            for (String id : (List<String>) value) {
                idList.add(id);
            }
        }
        return idList;
    }
}
