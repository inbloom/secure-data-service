package org.slc.sli.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Lifecycle manager which coordinates lifecycle state transitions and any related caching for performance reasons
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 */
public class LifecycleManager {
    
    // Logging
    private static Logger log = LoggerFactory.getLogger(LifecycleManager.class);
    
    
    // Constants
    public final static String CACHE_DESCRIPTOR_SEPARATOR = "|";
    public final static String CACHE_DESCRIPTOR_LIST_SEPARATOR = ",";
    public static final long LOG_DISPLAY_INTERVAL = 1000;
    
    
    // Attributes
    private Map<String, String> wipCache;
    private Map<String, String> resolvedCache;
    private Map<String, String> notificationCache;
    private Map<String, String> activeCache;
    
    
    // Constructors
    public LifecycleManager() {  
        super();
        this.init();
    }
    
    
    // Methods
    public void init() {
        wipCache = new HashMap<String, String>();
        resolvedCache = new HashMap<String, String>();
        notificationCache = new HashMap<String, String>();
        activeCache = new HashMap<String, String>();
    }
    
    public synchronized void prepare(ModelEntity entity) {
        
        // Replace Dependency Attributes with corresponding Dependency IDs
        String[] dependencies = entity.getLifecycle().getDependencies();   
        if (dependencies != null) {
            for (int index = 0; index < dependencies.length; index++) {
                String dependencyAttribute = dependencies[index];
                String dependencyId = (String)entity.getBody().get(dependencyAttribute);
                dependencies[index] = dependencyId;
            }
        }
        
    }
    
    public synchronized boolean install(ModelEntity entity) {
        boolean alreadyExists = false;
        
        // Prepare Entity Dependencies
        this.prepare(entity);
        
        String[] remainingParts = null;
        String remainingPartsString = "";
        String[] remainingDependencies = null;
        String remainingDependenciesString = "";
        boolean wipCacheUpdated = false;
        boolean resolvedCacheUpdated = false;
        boolean notificationCacheUpdated = false;
        
        // Lookup WIP Cache Descriptor
        String cacheDescriptor = (String)wipCache.get(entity.getId().toString());
        
        // Extract Parts and Dependencies
        if (cacheDescriptor != null) {  
            
            // Entity Already Exists
            alreadyExists = true;
            
            StringTokenizer tokenizer = new StringTokenizer(cacheDescriptor, CACHE_DESCRIPTOR_SEPARATOR);
            if (tokenizer.hasMoreTokens()) {
                String parts = tokenizer.nextToken();
                remainingParts = this.parseArray(parts);
            }
            if (tokenizer.hasMoreTokens()) {
                String dependencies = tokenizer.nextToken();
                remainingDependencies = this.parseArray(dependencies);
            }
        } else {
            remainingParts = entity.getLifecycle().getParts();
            remainingDependencies = entity.getLifecycle().getDependencies();            
        }
        
        // Check Parts
        if (remainingParts != null) {
            String currentPart = entity.getLifecycle().getCurrentPart();
            for (int index = 0; index < remainingParts.length; index++) {
                String part = remainingParts[index];
                if ((part != null) && (part.equals(currentPart))) {
                    remainingParts[index] = null;
                }
            }
            remainingPartsString = this.arrayToString(remainingParts);
        }
        
        // Check Dependencies
        if (remainingDependencies != null) {
            for (int index = 0; index < remainingDependencies.length; index++) {
                String dependencyId = remainingDependencies[index];
                
                // Lookup Resolved Dependency
                boolean isDependencyResolved = resolvedCache.containsKey(dependencyId);
                boolean isDependencyActive = activeCache.containsKey(dependencyId);
                
                // Remove Dependency if Resolved
                if (isDependencyResolved || isDependencyActive) {
                    remainingDependencies[index] = null;
                }            
            }
            remainingDependenciesString = this.arrayToString(remainingDependencies);
        }
        
        // Determine If Installed
        if (remainingPartsString.length() <= 0) {

            // Determine If Resolved
            if (remainingDependenciesString.length() <= 0) {
                
                // Transition To Resolved
                entity.getLifecycle().setState(ModelLifecycleState.RESOLVED);
                Timestamp resolvedOn = new Timestamp(Calendar.getInstance().getTimeInMillis());
                entity.getLifecycle().setResolvedOn(resolvedOn.toString());
            } else {
                
                // Transition To Installed
                entity.getLifecycle().setState(ModelLifecycleState.INSTALLED);
                entity.getLifecycle().setCurrentPart("");
                entity.getLifecycle().setParts(new String[0]);
                Timestamp installedOn = new Timestamp(Calendar.getInstance().getTimeInMillis());
                entity.getLifecycle().setInstalledOn(installedOn.toString());
            }
        }
                        
        // Update Cache Descriptor
        cacheDescriptor = createCacheDescriptor(remainingParts, remainingDependencies);
        
        // Update Caches
        if (entity.getLifecycle().getState() == ModelLifecycleState.RESOLVED) {
            
            // Lifecycle State Transition: RESOLVED
            
            // Remove Entity from WIP Cache
            Object mapping = wipCache.remove(entity.getId().toString());
            wipCacheUpdated = (mapping != null);
            
            // Insert Entity into Resolved Cache if Core Entity
            if (entity.getLifecycle().isCore()) {
                resolvedCache.put(entity.getId().toString(), cacheDescriptor);
                resolvedCacheUpdated = true;
            }
            
            // Update WIP Cache To Remove Dependencies On Resolved Entity
            String notificationDescriptor = (String)notificationCache.get(entity.getId().toString());
            if (notificationDescriptor != null) {
                String[] notificationArray = parseArray(notificationDescriptor);
                
                // Process Dependents In Notification Graph
                for (int index = 0; index < notificationArray.length; index++) {
                    
                    // Lookup Dependent WIP Cache Descriptor
                    String dependentDescriptor = (String)wipCache.get(notificationArray[index]);
                    if (dependentDescriptor != null) {
                        
                        // Remove Notification Id
                        notificationArray = this.removeItem(notificationArray, notificationArray[index]);
                        
                        // Update Notification Descriptor
                        notificationDescriptor = this.arrayToString(notificationArray);
                        
                        // Update Entity Dependent in WIP Cache
                        wipCache.put(notificationArray[index], notificationDescriptor);
                        wipCacheUpdated = true;
                    }
                }
            }
        } else {
            
            // Lifecycle State Transition: WIP
            
            // Insert Entity into WIP Cache
            wipCache.put(entity.getId().toString(), cacheDescriptor);
            wipCacheUpdated = true;
            
            // Update Notification Cache With Remaining Dependencies
            for (int index = 0; index < remainingDependencies.length; index++) {
                if (remainingDependencies[index] != null) {
                    
                    // Lookup Notification Descriptor
                    String notificationDescriptor = (String)notificationCache.get(remainingDependencies[index]);
                    String[] notificationArray = {};
                    if (notificationDescriptor != null) {
                        notificationArray = parseArray(notificationDescriptor);
                    }
                    
                    // Add Notification Id
                    notificationArray = this.addItem(notificationArray, entity.getId().toString());
                    
                    // Update Notification Descriptor
                    notificationDescriptor = this.arrayToString(notificationArray);
                    
                    // Update Entity Dependent in Notification Cache
                    notificationCache.put(remainingDependencies[index], notificationDescriptor);
                    notificationCacheUpdated = true;
                }
            }
        }

        // Display Cache Information
        this.displayCacheInfo(wipCacheUpdated, resolvedCacheUpdated, notificationCacheUpdated);
                
        return alreadyExists;
    }
    
    private boolean containsItem(String[] array, String item) {
        for (int index = 0; index < array.length; index++) {
            if (array[index].equals(item)) {
                return true;
            }
        }
        return false;
    }
    
    private String[] addItem(String[] array, String item) {
        String[] newArray = array;

        if (!this.containsItem(array, item)) {
            List<String> list = new ArrayList<String>(Arrays.asList(array));
            list.add(item);
            newArray = list.toArray(new String[list.size()]);
        }
        
        return newArray;
    }
    
    private String[] removeItem(String[] array, String item) {
        List<String> list = new ArrayList<String>(Arrays.asList(array));
        list.remove(item);
        String[] newArray = list.toArray(new String[list.size()]);
        
        return newArray;
    }
    
    private String createCacheDescriptor(String[] parts, String[] dependencies) {
        String cacheDescriptor = "";
        
        cacheDescriptor += this.arrayToString(parts);
        cacheDescriptor += CACHE_DESCRIPTOR_SEPARATOR;
        cacheDescriptor += this.arrayToString(dependencies);
        
        if (cacheDescriptor.equals(CACHE_DESCRIPTOR_SEPARATOR)) {
            cacheDescriptor = "";
        }
        
        return cacheDescriptor;
    }
    
    private String[] parseArray(String arrayString) {
        String[] array = {};
        
        List<String> list = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(arrayString, CACHE_DESCRIPTOR_LIST_SEPARATOR);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            list.add(token);
        }
        
        array = list.toArray(new String[list.size()]);
        
        return array;
    }
    
    private String arrayToString(String[] array) {
        String arrayString = "";
        
        if (array != null) {
            for (int index = 0; index < array.length; index++) {
                String arrayItem = array[index];
                if (arrayItem != null) {
                    if (arrayString.length() > 0) {
                        arrayString += CACHE_DESCRIPTOR_LIST_SEPARATOR;
                    }
                    arrayString += arrayItem;
                }
            }
        }
        
        return arrayString;
    }
    
    private void displayCacheInfo(boolean wipCacheUpdated, boolean resolvedCacheUpdated, boolean notificationCacheUpdated) {
        
        if (wipCacheUpdated) {
            if (wipCache.size() <= 0) {
                log.info("WIP Cache Size: Empty");
            } else if ((wipCache.size() % LOG_DISPLAY_INTERVAL) == 0) {
                log.info("WIP Cache Size: " + wipCache.size());
            }
        }
        if (resolvedCacheUpdated) {
            if (resolvedCache.size() <= 0) {
                log.info("Resolved Cache Size: Empty");
            } else if ((resolvedCache.size() % LOG_DISPLAY_INTERVAL) == 0) {
                log.info("Resolved Cache Size: " + resolvedCache.size());
            }
        }
        if (notificationCacheUpdated) {
            if (notificationCache.size() <= 0) {
                log.info("Notification Cache Size: Empty");
            } else if ((notificationCache.size() % LOG_DISPLAY_INTERVAL) == 0) {
                log.info("Notification Cache Size: " + notificationCache.size());
            }
        }
    }
    
}
