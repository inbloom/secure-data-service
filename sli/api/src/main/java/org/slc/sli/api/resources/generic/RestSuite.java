package org.slc.sli.api.resources.generic;

import org.springframework.stereotype.Component;

/**
 * @author jstokes
 */
public class RestSuite {
    public static final RestSuite DEFAULT_ONE_PART =
//            new RestSuite(new GetAllAction(), new NopPutAction(), new NopDeleteAction(), new DefaultPostAction());
            new RestSuite(new GetAllAction(), null, null, null);

    private final GetAction getAction;
    private final PutAction putAction;
    private final DeleteAction deleteAction;
    private final PostAction postAction;

    public RestSuite(final GetAction getAction,
                     final PutAction putAction,
                     final DeleteAction deleteAction,
                     final PostAction postAction) {
        this.getAction = getAction;
        this.putAction = putAction;
        this.deleteAction = deleteAction;
        this.postAction = postAction;
    }

    public GetAction getGetAction() {
        return getAction;
    }

    public PutAction getPutAction() {
        return putAction;
    }

    public DeleteAction getDeleteAction() {
        return deleteAction;
    }

    public PostAction getPostAction() {
        return postAction;
    }
}
