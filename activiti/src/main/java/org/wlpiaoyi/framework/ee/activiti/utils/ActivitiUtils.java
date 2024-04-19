package org.wlpiaoyi.framework.ee.activiti.utils;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;

import jakarta.sql.DataSource;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 18:10
 * {@code @version:}:       1.0
 */
public class ActivitiUtils {

    /**
     * Get process engine configuration
     * @param dataSource
     * @return
     */
    public static StandaloneProcessEngineConfiguration processEngineConfiguration(DataSource dataSource) {
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setAsyncExecutorActivate(false);
        return configuration;
    }

    /**
     * Get process engine
     * @param dataSource
     */
    public static ProcessEngine buildProcessEngine(DataSource dataSource){
        ProcessEngineConfiguration engineConfiguration = processEngineConfiguration(dataSource);
        engineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
        ProcessEngine processEngine = engineConfiguration.buildProcessEngine();
        return processEngine;
    }

    /**
     * Get process engine
     * DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
     * dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
     * //&nullCatalogMeansCurrent=true没有这个就不能自动创建表
     * dataSourceBuilder.url("jdbc:mysql://127.0.0.1:3306/activti?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true");
     * dataSourceBuilder.username("root");
     * dataSourceBuilder.password("00000000");
     * @param dataSource
     */
    public static ProcessEngine buildProcessEngineForInitSql(DataSource dataSource){
        ProcessEngineConfiguration engineConfiguration = processEngineConfiguration(dataSource);
        engineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP);
        ProcessEngine processEngine = engineConfiguration.buildProcessEngine();
        return processEngine;
    }
}
