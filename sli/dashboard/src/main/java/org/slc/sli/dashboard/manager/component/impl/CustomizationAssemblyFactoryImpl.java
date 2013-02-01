/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.manager.component.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.Config.Item;
import org.slc.sli.dashboard.entity.Config.Type;
import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.ModelAndViewConfig;
import org.slc.sli.dashboard.manager.ConfigManager;
import org.slc.sli.dashboard.manager.Manager;
import org.slc.sli.dashboard.manager.Manager.EntityMappingManager;
import org.slc.sli.dashboard.manager.UserEdOrgManager;
import org.slc.sli.dashboard.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.dashboard.util.DashboardException;
import org.slc.sli.dashboard.util.ExecutionTimeLogger.LogExecutionTime;
import org.slc.sli.dashboard.util.SecurityUtil;

/**
 * Implementation of the CustomizationAssemblyFactory
 * @author agrebneva
 *
 */
public class CustomizationAssemblyFactoryImpl implements CustomizationAssemblyFactory, ApplicationContextAware {
    public static final Class<?>[] ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE =
            new Class[]{String.class, Object.class, Config.Data.class};
    public static final String SUBSTITUTE_TOKEN_PATTERN = "\\$\\{([^}]+)\\}";
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizationAssemblyFactoryImpl.class);
    private ApplicationContext applicationContext;
    private ConfigManager configManager;
    private UserEdOrgManager userEdOrgManager;
    private Map<String, InvokableSet> entityReferenceToManagerMethodMap;

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void setUserEdOrgManager(UserEdOrgManager userEdOrgManager) {
        this.userEdOrgManager = userEdOrgManager;
    }

    protected String getTokenId() {
        return SecurityUtil.getToken();
    }

    protected Config getConfig(String componentId) {
        EdOrgKey edOrg = userEdOrgManager.getUserEdOrg(getTokenId());
        if (edOrg == null) {
            throw new DashboardException("No data is available for you to view. Please contact your IT administrator.");
        }
        return configManager.getComponentConfig(getTokenId(), edOrg, componentId);
    }

    @Override
    public Collection<Config> getWidgetConfigs() {
        return configManager.getWidgetConfigs(getTokenId(), userEdOrgManager.getUserEdOrg(getTokenId()));
    }

    /**
     * Check declared condition against the entity
     * @param config - config for the component
     * @param entity - entity for the component
     * @return true if condition passes and false otherwise
     */
    @SuppressWarnings("unchecked")
    public final boolean checkCondition(Config parentConfig, Config config, GenericEntity entity) {
        if (config != null && config.getCondition() != null) {

            if (entity == null) {
                return true;
            }

            Config.Condition condition = config.getCondition();
            Object[] values = condition.getValue();
            // for simplicity always treat as an array
            List<GenericEntity> listOfEntities = (parentConfig != null && parentConfig.getRoot() != null) ? entity.getList(parentConfig.getRoot()) : Arrays.asList(entity);
            Object childEntity;
            // condition is equivalent to exists in the list
            for (GenericEntity oneEntity : listOfEntities) {
                childEntity = getValue(oneEntity, condition.getField());
                // if null and value is null, it's allowed, otherwise it's not
                if (childEntity == null) {
                    return values.length == 0;
                }
                if (childEntity instanceof Number) {
                    double childNumber = ((Number) childEntity).doubleValue();
                    for (Object n : values) {
                        if (childNumber == ((Number) n).doubleValue()) {
                            return true;
                        }
                    }
                } else if (childEntity instanceof String) {
                    String childString = (String) childEntity;
                    for (Object n : values) {
                        if (childString.equalsIgnoreCase((String) n)) {
                            return true;
                        }
                    }
                } else {
                    throw new DashboardException("Unsupported data type for condition. Only allow string and numbers");
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Get value from the entity model map where sub-entities are identified by a dot
     * "data.history.id"
     * @param entity
     * @param dataField
     * @return
     */
    private Object getValue(GenericEntity entity, String dataField) {
        String[] pathTokens = dataField.split("\\.");
        pathTokens = (pathTokens.length == 0) ? new String[]{dataField} : pathTokens;
        Object childEntity = entity;
        for (String token : pathTokens) {
            if (childEntity == null || !(childEntity instanceof GenericEntity)) {
                return null;
            }
            childEntity = ((GenericEntity) childEntity).get(token);
        }
        return childEntity;
    }

    /**
     * Traverse the config tree and populate the necessary entity and config objects
     * @param model - model to populate
     * @param componentId - current component to explore
     * @param entityKey - entityKey
     * @param parentEntity - parent entity
     * @param depth - depth of the recursion
     */
    private Config populateModelRecursively(
        ModelAndViewConfig model, String componentId, Object entityKey, Config.Item parentToComponentConfigRef, Config panelConfig,
        GenericEntity parentEntity, int depth, boolean lazyOverride
    ) {
        if (depth > 5) {
            throw new DashboardException("The items hierarchy is too deep - only allow 5 elements");
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
                entity = model.getDataForAlias(dataConfig.getCacheKey());
                if ((!dataConfig.isLazy() || lazyOverride) && entity == null) {
                    entity = getDataComponent(componentId, entityKey, dataConfig);
                    model.addData(dataConfig.getCacheKey(), entity);
                }
            }
            if (!checkCondition(config, config, entity)) {
                return null;
            }
        }
        if (config.getItems() != null) {
            List<Config.Item> items = new ArrayList<Config.Item>();
            Config newConfig;
            Collection<Config.Item> expandedItems;
            Config.Item item;
            
            // get items, go through all of them and update config as need according to conditions and template substitutions
            for (Config.Item configItem : getUpdatedDynamicHeaderTemplate(config, entity)) {
                
                item = configItem;
                if (checkCondition(config, item, entity)) {
                    newConfig = populateModelRecursively(model, item.getId(), entityKey, item, config, entity, depth + 1, lazyOverride);
                    if (newConfig != null) {
                        item = (Item) item.cloneWithItems(newConfig.getItems());
                    }
                    // if needs expansion, expand and add all columns, otherwise add the item
                    expandedItems = getExpandedColumns(item, entity);
                    if (expandedItems != null) {
                        items.addAll(expandedItems);
                    } else {
                      items.add(item);
                    }
                    if (config.getType().isLayoutItem()) {
                        model.addLayoutItem(newConfig);
                    }
                }
            }
            config = config.cloneWithItems(items.toArray(new Config.Item[0]));
        }
        if (componentId != null) {
            model.addConfig(componentId, config);
        }
        return config;
    }

    /**
     * Replace tokens in headers with values from entity's internal metadata
     * @param config
     * @param entity
     */
    protected Config.Item[] getUpdatedDynamicHeaderTemplate(Config config, GenericEntity entity) {
        if (entity != null) {
            Pattern p = Pattern.compile(SUBSTITUTE_TOKEN_PATTERN);
            Matcher matcher;
            String name, value;
            Config.Item item;
            Collection<Config.Item> newItems = new ArrayList<Config.Item>();
            for (Config.Item configItem : config.getItems()) {
                item = configItem;
                name = item.getName();
                if (name != null) {
                    matcher = p.matcher(name);
                    while (matcher.find()) {
                        value = (String) getValue(entity, matcher.group(1));
                        if (value != null) {
                            name = name.replace(matcher.group(), value);
                        }
                    }
                    item = item.cloneWithName(name);
                }
                newItems.add(item);
            }
            return newItems.toArray(new Config.Item[0]);
        }
        return config.getItems();
    }

    /**
     * Dynamic column functionality which expands the columns by looking for an array in the entity that drives the expansion
     * Expand the columns if root attribute is present
     * @param config - config for field item
     * @param entity - entity for the component
     * @return expanded array
     */
    protected Collection<Config.Item> getExpandedColumns(Config.Item config, GenericEntity entity) {
        if (entity == null) {
            return null;
        }
        // if there is root for field, expand the columns
        if (config.getType() == Type.EXPAND && config.getRoot() != null) {
            @SuppressWarnings("unchecked")
            Collection<String> expandMapperList = entity.getList(config.getRoot());
            if (expandMapperList == null) {
                LOGGER.error("Expand map is not available in the entity for config " + config);
                return null;
            }
            List<Config.Item> expandedItems = new ArrayList<Config.Item>();
            for (String lookupName : expandMapperList) {
                expandedItems.add(config.cloneWithParams(lookupName, lookupName));
            }
            return expandedItems;
        }
        return null;
    }

    @Override
    public ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey) {
        return getModelAndViewConfig(componentId, entityKey, false);
    }

    @Override
    public ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey, boolean lazyOverride) {
        ModelAndViewConfig modelAndViewConfig = new ModelAndViewConfig();
        populateModelRecursively(modelAndViewConfig, componentId, entityKey, null, null, null, 0, lazyOverride);
        modelAndViewConfig.setWidgetConfig(getWidgetConfigs());
        return modelAndViewConfig;
    }


    /**
     * Internal convenience class for method caching
     * @author agrebneva
     *
     */
    private class InvokableSet {
        Object manager;
        Method method;
        InvokableSet(Object manager, Method method) {
            this.manager = manager;
            this.method = method;
        }
        public Object getManager() {
            return manager;
        }
        public Method getMethod() {
            return method;
        }
    }

    private void populateEntityReferenceToManagerMethodMap() {
        Map<String, InvokableSet> entityReferenceToManagerMethodMap = new HashMap<String, InvokableSet>();

        boolean foundInterface = false;
        for (Object manager : applicationContext.getBeansWithAnnotation(EntityMappingManager.class).values()) {
            LOGGER.info(manager.getClass().getCanonicalName());
            // managers can be advised (proxied) so original annotation are not seen on the method but
            // still available on the interface
            foundInterface = false;
            for (Class<?> type : manager.getClass().getInterfaces()) {
                if (type.isAnnotationPresent(EntityMappingManager.class)) {
                    foundInterface = true;
                    findEntityReferencesForType(entityReferenceToManagerMethodMap, type, manager);
                }
            }
            if (!foundInterface) {
                findEntityReferencesForType(entityReferenceToManagerMethodMap, manager.getClass(), manager);
            }
        }
        this.entityReferenceToManagerMethodMap = Collections.unmodifiableMap(entityReferenceToManagerMethodMap);
    }

    private final void findEntityReferencesForType(
            Map<String, InvokableSet> entityReferenceToManagerMethodMap, Class<?> type, Object instance) {
        Manager.EntityMapping entityMapping;
        for (Method m : type.getDeclaredMethods()) {
            entityMapping = m.getAnnotation(Manager.EntityMapping.class);
            if (entityMapping != null) {
                if (entityReferenceToManagerMethodMap.containsKey(entityMapping.value())) {
                    throw new DashboardException("Duplicate entity mapping references found for "
                            + entityMapping.value() + ". Fix!!!");
                }
                if (!Arrays.equals(ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE, m.getParameterTypes())) {
                    throw new DashboardException("Wrong signature for the method for "
                            + entityMapping.value() + ". Expected is "
                            + Arrays.asList(ENTITY_REFERENCE_METHOD_EXPECTED_SIGNATURE) + "!!!");
                }
                entityReferenceToManagerMethodMap.put(entityMapping.value(), new InvokableSet(instance, m));
            }
        }
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

    @Override
    public GenericEntity getDataComponent(String componentId, Object entityKey) {
        return getDataComponent(componentId, entityKey, getConfig(componentId).getData());
    }

    @LogExecutionTime
    protected GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
        if (config == null) {
            return null;
        }
        
        InvokableSet set = this.getInvokableSet(config.getEntityRef());
        if (set == null) {
            throw new DashboardException("No entity mapping references found for " + config.getEntityRef() + ". Fix!!!");
        }
        
        try {
            return (GenericEntity) set.getMethod().invoke(set.getManager(), getTokenId(), entityKey, config);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Unable to invoke population manager for " + componentId + " and entity id " + entityKey
                    + ", config " + componentId, e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Unable to invoke population manager for " + componentId + " and entity id " + entityKey
                    + ", config " + componentId, e);
        } catch (InvocationTargetException e) {
            LOGGER.error("Unable to invoke population manager for " + componentId + " and entity id " + entityKey
                    + ", config " + componentId, e);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext = applicationContext;
       populateEntityReferenceToManagerMethodMap();
    }
}
