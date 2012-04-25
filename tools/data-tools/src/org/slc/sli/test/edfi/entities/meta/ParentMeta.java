/**
 *
 */
package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lchen
 *
 */
public class ParentMeta {

    public final String id;
    public final String simpleId;
    public boolean isMale;


    public ParentMeta(String id,boolean isMale) {

        this.id = id;
        this.isMale = isMale;
        this.simpleId = id;

    }


    @Override
    public String toString() {
        return "ParentMeta [id=" + id + ", + simpleId=" + simpleId + "]";
    }
}


