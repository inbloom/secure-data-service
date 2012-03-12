package org.slc.sli.manager.component.impl;

import java.lang.reflect.Method;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.PopulationManager;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.DashboardException;
import org.slc.sli.util.SecurityUtil;

public class CustomizationAssemblyFactoryImpl implements CustomizationAssemblyFactory {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigManager configManager;
    private PopulationManager populationManager;
    
    public void setConfigManager(ConfigManager configManager)
    {
        this.configManager = configManager;
    }
    
    public void setPopulationManager(PopulationManager populationManager)
    {
        this.populationManager = populationManager;
    }
    
    private String getTokenId()
    {
        return SecurityUtil.getToken();
    }
    
    private String getUsername()
    {
        return SecurityUtil.getUsername();
    }
    
    private ViewConfig getConfig(String componentId)
    {
        return configManager.getConfig(getUsername(), componentId);
    }
    
    private String getGetterName(ViewConfig config)
    {
        String value = config.getValue();
        return "get" + StringUtils.capitalize(value);
    }
    
    @Override
    public GenericEntity getDataComponent(String componentId, Object entityKey) {
        // TODO: must handle custom layout level configs? can we get away with one panel config?
        ViewConfig config = getConfig(componentId);
        if (config == null)
        {
            throw new DashboardException(
                    "Unable to find config for " + componentId + " and entity id " + entityKey + ", config " + componentId);
        }
        try {
            Method m = 
                PopulationManager.class.getMethod(getGetterName(config), new Class[]{String.class, Object.class, ViewConfig.class});
            return (GenericEntity) m.invoke(populationManager, getTokenId(), entityKey, config);
        } catch (NoSuchMethodException nsme)
        {
            logger.error(
                    "Invalid data component specified for " + componentId + " and entity id " + entityKey + ", config " + componentId, nsme);
        
        } catch (Throwable t) {
            logger.error(
                    "Unable to invoke population manager for " + componentId + " and entity id " + entityKey + ", config " + componentId, t);
        }
        return null;
    }

    @Override
    public GenericEntity getDislplayComponent(String componentId, Object params) {
        // TODO Auto-generated method stub
        return null;
    }
}
