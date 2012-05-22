/**
 *
 */
package org.slc.sli.ingestion.smooks;

import java.lang.instrument.UnmodifiableClassException;

import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.instrument.classloading.LoadTimeWeaver;

/**
 * @author okrook
 *
 */
public class ElementVisitEventReplacer implements LoadTimeWeaverAware {

    private LoadTimeWeaver loadTimeWeaver;

    /* (non-Javadoc)
     * @see org.springframework.context.weaving.LoadTimeWeaverAware#setLoadTimeWeaver(org.springframework.instrument.classloading.LoadTimeWeaver)
     */
    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    public void replaceEvent() throws UnmodifiableClassException {
        loadTimeWeaver.addTransformer(new ElementVisitEventTransformer());
    }
}
