package org.slc.sli.manager.component.impl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.ModelAndViewConfig;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.Manager;
import org.slc.sli.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.util.DashboardException;
import org.slc.sli.util.SecurityUtil;

/**
 * Implementation of the CustomizationAssemblyFactory
 * @author agrebneva
 *
 */
public class CustomizationAssemblyFactoryImpl implements CustomizationAssemblyFactory, ApplicationContextAware {
    private static final Class<?>[] ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE = 
            new Class[]{String.class, Object.class, Config.Data.class};
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationContext applicationContext;
    private ConfigManager configManager;
    private Map<String, InvokableSet> entityReferenceToManagerMethodMap;

    
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    protected String getTokenId() {
        return SecurityUtil.getToken();
    }
    
    protected String getUsername() {
        return SecurityUtil.getUsername();
    }
    
    protected Config getConfig(String componentId) {
        return configManager.getComponentConfig(getUsername(), componentId);
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
            if (dataConfig != null && !model.hasDataForAlias(dataConfig.getAlias())) {
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
    
    
    /**
     * Internal convenience class for method caching
     * @author agrebneva
     *
     */
    private class InvokableSet {
        Manager manager;
        Method method;
        InvokableSet(Manager manager, Method method) {
            this.manager = manager;
            this.method = method;
        }
        public Manager getManager() {
            return manager;
        }
        public Method getMethod() {
            return method;
        }
    }
    
    private void populateEntityReferenceToManagerMethodMap() {
        Map<String, InvokableSet> entityReferenceToManagerMethodMap = new HashMap<String, InvokableSet>();
        Manager.EntityMapping entityMapping;
        for (Manager manager : applicationContext.getBeansOfType(Manager.class).values())
        {
            logger.info(manager.getClass().getCanonicalName());
            for (Method m : manager.getClass().getMethods()) {
                entityMapping = m.getAnnotation(Manager.EntityMapping.class);
                if (entityMapping != null) {
                    if (entityReferenceToManagerMethodMap.containsKey(entityMapping.value()))
                    {
                        throw new DashboardException("Duplicate entity mapping references found for " + entityMapping.value() + ". Fix!!!");
                    }
                    if (!Arrays.equals(ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE, m.getParameterTypes())) {
                        throw new DashboardException(
                                "Wrong signature for the method for " + entityMapping.value() + ". Expected is " 
                                + ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE + "!!!");
                    }
                    entityReferenceToManagerMethodMap.put(entityMapping.value(), new InvokableSet(manager, m));
                }
            }
        }
        this.entityReferenceToManagerMethodMap = Collections.unmodifiableMap(entityReferenceToManagerMethodMap);
    }
    
    protected InvokableSet getInvokableSet(String entityRef) {
        return this.entityReferenceToManagerMethodMap.get(entityRef);
    }
    
    /**
     * For UTs
     * @param entityRef
     * @return
     */
    public boolean hasCachedEntityMapperReference(String entityRef) {
        return this.entityReferenceToManagerMethodMap.containsKey(entityRef);
    }
    
    /**
     * Get data for the declared entity reference
     * @param componentId - component to get data for
     * @param entityKey - entity key for the component
     * @param config - data config for the component
     * @return entity
     */
    protected GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
        InvokableSet set = this.getInvokableSet(config.getEntityRef());
        if (set == null) {
            throw new DashboardException("No entity mapping references found for " + config.getEntityRef() + ". Fix!!!");
        }
        try {
            return (GenericEntity) set.getMethod().invoke(set.getManager(), getTokenId(), entityKey, config);
        } catch (Throwable t) {
            logger.error("Unable to invoke population manager for " + componentId + " and entity id " + entityKey
                    + ", config " + componentId, t);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
       populateEntityReferenceToManagerMethodMap();
    }
}
