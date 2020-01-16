package com.iversonx.struts_springmvc.extend;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;

import java.util.*;

/**
 * @author Lijie
 * @version 1.0
 * @date 2020/1/16 17:11
 */
public class ActionConfigManager {
    private Map<String, PackageConfig> packageConfig;
    /**
     * 存储相同action的配置
     * <p>
     *     key: Action的className
     * </p>
     */
    private Map<String, Set<ActionConfig>> actionConfigWithClass = new HashMap<>();

    private Map<String, ActionConfig> actionConfigWithClassAndMethod = new HashMap<>();

    public void setPackageConfig(Map<String, PackageConfig> packageConfig) {
        this.packageConfig = packageConfig;
        initMap();
    }

    private void initMap() {
        for(Map.Entry<String, PackageConfig> entry : this.packageConfig.entrySet()) {
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

    public Map<String, PackageConfig> getPackageConfig() {
        return packageConfig;
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
        PackageConfig config = this.packageConfig.get(packageName);
        if(config != null) {
            return config.getNamespace();
        }
        return null;
    }
}

