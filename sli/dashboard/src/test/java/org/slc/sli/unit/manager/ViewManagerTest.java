package org.slc.sli.unit.manager;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.manager.ViewManager;

public class ViewManagerTest {

    
    @Test
    public void testGetApplicableViewConfigs() {
        ViewManager viewMan = new ViewManager();
        viewMan.setViewConfigs(new LinkedList<ViewConfig>());
        List<ViewConfig> result = viewMan.getApplicableViewConfigs(new LinkedList<String>(), "fakeToken");
        assert(result.isEmpty());
    }
    
    
    
}
