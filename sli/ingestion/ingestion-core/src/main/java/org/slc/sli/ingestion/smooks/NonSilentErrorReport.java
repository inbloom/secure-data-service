package org.slc.sli.ingestion.smooks;

import org.milyn.event.ExecutionEvent;
import org.milyn.event.ExecutionEventListener;

/**
 * Non-silent error report class reports elements/attributes that were ignored while processing data.
 *
 * @author okrook
 *
 */
public class NonSilentErrorReport implements ExecutionEventListener {

    @Override
    public void onEvent(ExecutionEvent event) {
        // TODO Auto-generated method stub

    }

}
