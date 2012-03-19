package org.slc.sli.view.modifier;

import org.slc.sli.config.ViewConfig;

/**
 * @author jstokes
 */
public interface ViewModifier {

    public ViewConfig modify(ViewConfig view);

}
