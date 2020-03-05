package com.iversonx.struts_springmvc.support.processor;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StrutsConfigManager {
    private Map<String, PackageConfig> packageConfigMap;

    /**
     * 存储相同action的配置
     * <p>
     *     key: Action的className
     * </p>
     */
    private Map<String, Set<ActionConfig>> actionConfigWithClass = new HashMap<>();

    private Map<String, ActionConfig> actionConfigWithURI = new HashMap<>();

    public void setPackageConfig(Map<String, PackageConfig> packageConfig) {
        this.packageConfigMap = packageConfig;
        initMap();
    }

    private void initMap() {
        for(Map.Entry<String, PackageConfig> entry : this.packageConfigMap.entrySet()) {
            PackageConfig packageConfig = entry.getValue();
            String namespace = packageConfig.getNamespace();
            Map<String, ActionConfig> actionConfigMap = packageConfig.getActionConfigs();
            for(Map.Entry<String, ActionConfig> item : actionConfigMap.entrySet()) {
                ActionConfig actionConfig = item.getValue();
                String className = actionConfig.getClassName();
                Set<ActionConfig> actionConfigs = this.actionConfigWithClass.get(className);
                if(actionConfigs == null) {
                    actionConfigs = new HashSet<>();
                }
                actionConfigs.add(item.getValue());
                this.actionConfigWithClass.put(className, actionConfigs);

                // 使用action配置中namespace + name 作为key，存储ActionConfig
                String key = namespace + actionConfig.getName();
                this.actionConfigWithURI.put(key, actionConfig);
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

    public ActionConfig getActionConfigByURI(String uri) {
        return actionConfigWithURI.get(uri);
    }

    public ResultConfig getResultConfigByURIReturnValue(String uri, String returnValue) {
        ActionConfig actionConfig = getActionConfigByURI(uri);
        Map<String, ResultConfig> resultConfigMap = actionConfig.getResults();
        return resultConfigMap.get(returnValue);
    }

    public String getNamespace(String packageName) {
        PackageConfig config = this.packageConfigMap.get(packageName);
        if(config != null) {
            return config.getNamespace();
        }
        return null;
    }
}
