package com.iversonx.struts_springmvc.support.processor;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActionConfigManager {
    private Map<String, PackageConfig> packageConfigMap;

    /**
     * 存储相同action的配置
     * <p>
     *     key: Action的className
     * </p>
     */
    private Map<String, Set<ActionConfig>> actionConfigWithClass = new HashMap<>();

    private Map<String, ActionConfig> actionConfigWithClassAndMethod = new HashMap<>();

    public void setPackageConfig(Map<String, PackageConfig> packageConfig) {
        this.packageConfigMap = packageConfig;
        initMap();
    }

    private void initMap() {
        for(Map.Entry<String, PackageConfig> entry : this.packageConfigMap.entrySet()) {
            Map<String, ActionConfig> actionConfigMap = entry.getValue().getActionConfigs();
            for(Map.Entry<String, ActionConfig> item : actionConfigMap.entrySet()) {
                ActionConfig actionConfig = item.getValue();
                String className = actionConfig.getClassName();
                Set<ActionConfig> actionConfigs = this.actionConfigWithClass.get(className);
                if(actionConfigs == null) {
                    actionConfigs = new HashSet<>();
                }
                actionConfigs.add(item.getValue());
                this.actionConfigWithClass.put(className, actionConfigs);

                String key = className + "#" + actionConfig.getMethodName();
                this.actionConfigWithClassAndMethod.put(key, actionConfig);
            }
        }
    }

    public Map<String, PackageConfig> getPackageConfigMap() {
        return packageConfigMap;
    }

    public Set<ActionConfig> getActionConfigsByClass(String className) {
        Set<ActionConfig> actionConfigs = this.actionConfigWithClass.get(className);
        if(actionConfigs != null) {
            return actionConfigs;
        } else {
            return new HashSet<>();
        }
    }

    public ActionConfig getActionConfigByClassAndMethod(String className, String method) {
        return actionConfigWithClassAndMethod.get(className + "#" + method);
    }

    public String getNamespace(String packageName) {
        PackageConfig config = this.packageConfigMap.get(packageName);
        if(config != null) {
            return config.getNamespace();
        }
        return null;
    }
}
