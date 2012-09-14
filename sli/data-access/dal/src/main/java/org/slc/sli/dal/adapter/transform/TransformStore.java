package org.slc.sli.dal.adapter.transform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides transformation plugins
 *
 * @author srupasinghe
 */
public class TransformStore<T extends Transform> {

    private Class<T> type;

    @Autowired
    private ApplicationContext context;

    private Collection<T> transformList;

    public TransformStore(Class<T> type) {
        this.type = type;
    }

    @PostConstruct
    private void init() {
        transformList = context.getBeansOfType(type).values();
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

    public List<Transform> getTransform(String type) {
        List<Transform> list = new ArrayList<Transform>();

        for (Transform transform : transformList) {
            if (transform.isTransformable(type)) {
                list.add(transform);
            }
        }

        return list;
    }
}
