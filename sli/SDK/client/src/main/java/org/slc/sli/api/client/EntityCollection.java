package org.slc.sli.api.client;

import java.util.LinkedList;

/**
 * A collection of Entity instances. This collection supports heterogeneous collections.
 * The API, however, will only return homogeneous instances.
 *
 * @author asaarela
 */

public class EntityCollection extends LinkedList<Entity> {

    // Serializable
    private static final long serialVersionUID = -4836656021586038348L;
}
