package org.slc.sli.dal.adapter.transform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 9/12/12
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class DatabaseTransformStore {

    @Autowired
    private ApplicationContext context;

    private Collection<DatabaseTransform> transformList;

    @PostConstruct
    private void init() {
        transformList = context.getBeansOfType(DatabaseTransform.class).values();
    }

    public List<Transform> getTransform(String type, int fromVersion, int toVersion) {
        List<Transform> list = new ArrayList<Transform>();

        for (Transform transform : transformList) {
            if (transform.isTransformable(type, fromVersion, toVersion)) {
                list.add(transform);
            }
        }

        return list;
    }
}
