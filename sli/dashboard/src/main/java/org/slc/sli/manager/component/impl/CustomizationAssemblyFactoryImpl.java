package org.slc.sli.manager.component.impl;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.PopulationManager;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.DashboardException;
import org.slc.sli.util.SecurityUtil;

/**
 * Implementation of the CustomizationAssemblyFactory
 * @author agrebneva
 *
 */
public class CustomizationAssemblyFactoryImpl implements CustomizationAssemblyFactory {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigManager configManager;
    private PopulationManager populationManager;
    
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    public void setPopulationManager(PopulationManager populationManager) {
        this.populationManager = populationManager;
    }
    
    private String getTokenId() {
        return SecurityUtil.getToken();
    }
    
    private String getUsername() {
        return SecurityUtil.getUsername();
    }
    
    private Config getConfig(String componentId) {
        return configManager.getComponentConfig(getUsername(), componentId);
    }
    
    private String getGetterName(Config.Data config) {
        String value = config.getEntityRef();
        return "get" + StringUtils.capitalize(value);
    }
    
    private GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
        try {
            Method m = 
                PopulationManager.class.getMethod(getGetterName(config), new Class[]{String.class, Object.class, Config.Data.class});
            return (GenericEntity) m.invoke(populationManager, getTokenId(), entityKey, config);
        } catch (NoSuchMethodException nsme) {
            logger.error(
                    "Invalid data component specified for " + componentId + " and entity id " + entityKey + ", config " + componentId, nsme);
        } catch (Throwable t) {
            logger.error("Unable to invoke population manager for " + componentId + " and entity id " + entityKey
                    + ", config " + componentId, t);
        }
        return null;
    }
    
    /**
     * Check declared condition against the entity
     * @param config - config for the component
     * @param entity - entity for the component
     * @return true if condition passes and false otherwise
     */
    private boolean checkCondition(Config config, GenericEntity entity) {
        if (config != null && config.getCondition() != null) {
            if (entity == null) {
                throw new DashboardException("Entity is null for a conditional item.");
            }
            Config.Condition condition = config.getCondition();
            String[] tokens = condition.getField().split(".");
            tokens = (tokens.length == 0) ? new String[]{condition.getField()} : tokens;
            Object childEntity = entity;
            for (String token : tokens) {
                if (childEntity == null || !(childEntity instanceof GenericEntity)) {
                    return false;
                }
                childEntity = ((GenericEntity) childEntity).get(token);
            }
            Object[] values = condition.getValue();
            // if null and value is null, it's allowed, otherwise it's not
            if (childEntity == null) {
                return values.length == 0;
            }
            if (childEntity instanceof Number) {
                Number childNumber = (Number) childEntity;
                for (Object n : values) {
                    if (childNumber.equals((Number) n))
                        return true;
                }
            } else if (childEntity instanceof String) {
                String childString = (String) childEntity;
                for (Object n : values) {
                    if (childString.equalsIgnoreCase((String) n))
                        return true;
                }
            } else
                throw new DashboardException("Unsupported data type for condition. Only allow string and numbers");
            return false;
        }
        return true;
    }
    
    /**
     * Traverse the config tree and populate the necessary entity and config objects
     * @param model - model to populate
     * @param componentId - current component to explore
     * @param entityKey - entityKey
     * @param parentEntity - parent entity
     * @param depth - depth of the recursion
     */
    private void populateModelRecursively(
        ModelAndViewConfig model, String componentId, Object entityKey, Config.Item parentToComponentConfigRef, 
        GenericEntity parentEntity, int depth
    ) {
        if (depth > 3) {
            throw new DashboardException("The items hierarchy is too deep - only allow 3 elements");
        }
        Config config = parentToComponentConfigRef;
        GenericEntity entity = parentEntity;
        if (parentToComponentConfigRef == null || parentToComponentConfigRef.getType().hasOwnConfig()) {
            config = getConfig(componentId);
            if (config == null) {
                throw new DashboardException(
                        "Unable to find config for " + componentId + " and entity id " + entityKey + ", config " + componentId);
            } 
            Config.Data dataConfig = config.getData();
            if (dataConfig != null) {
                entity = getDataComponent(componentId, entityKey, dataConfig);
                model.addData(dataConfig.getAlias(), entity);
            }
            if (!checkCondition(config, entity))
                return;
        }
        model.addComponentViewConfigMap(componentId, config);
        if (config.getItems() != null) {
            depth++;
            for (Config.Item item : config.getItems()) {
                if (checkCondition(item, entity))
                    populateModelRecursively(model, item.getId(), entityKey, item, entity, depth);
            }
        }
    }

    @Override
    public ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey) {
        
        ModelAndViewConfig modelAndViewConfig = new ModelAndViewConfig();
        populateModelRecursively(modelAndViewConfig, componentId, entityKey, null, null, 0);
        return modelAndViewConfig;
    }
}
