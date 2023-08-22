package org.wlpiaoyi.framework.ee.activiti.engine.repository;

import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.SneakyThrows;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.wlpiaoyi.framework.ee.activiti.utils.ActivitiUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;

import java.util.List;
import java.util.Random;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 18:44
 * {@code @version:}:       1.0
 */
public class ModelServiceTest {
    static ProcessEngine processEngine;

    static {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        //&nullCatalogMeansCurrent=true没有这个就不能自动创建表
        dataSourceBuilder.url("jdbc:mysql://127.0.0.1:3306/activti?" +
                "useUnicode=true" +
                "&characterEncoding=utf-8" +
                "&useSSL=true" +
                "&serverTimezone=GMT%2B8");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("00000000");
        processEngine = ActivitiUtils.buildProcessEngine(dataSourceBuilder.build());
    }
    ModelService modelService;
    @Before
    public void setUp() throws Exception {
        modelService = new ModelService(processEngine);
    }

    @Test
    public void initDb(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        //&nullCatalogMeansCurrent=true没有这个就不能自动创建表
        dataSourceBuilder.url("jdbc:mysql://127.0.0.1:3306/activti?" +
                "useUnicode=true" +
                "&characterEncoding=utf-8" +
                "&useSSL=true" +
                "&serverTimezone=GMT%2B8" +
                "&nullCatalogMeansCurrent=true");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("00000000");
        ActivitiUtils.buildProcessEngineForInitSql(dataSourceBuilder.build());
        /*
        ALTER TABLE `act_re_model`
        CHANGE COLUMN `META_INFO_` `META_INFO_` BLOB NULL ;
        */

    }



    @SneakyThrows
    @Test
    public void saveModel(){
        int index = new Random().nextInt() % 100000;
        Model model = modelService.getExecService().newModel();
        model.setCategory("wl_category_" + index);
        model.setName("wl_name_" + index);
        model.setKey("wl_key_" + index);
        model.setTenantId("wl_tenantid_" + index);
        model.setVersion(1);
        model.setMetaInfo(DataUtils.readFile(DataUtils.USER_DIR + "\\src\\test\\resources\\bpmn\\diagram.bpmn"));
        modelService.save(model);
        model = this.modelService.getOneByKey("wl_key_" + index);
        Assert.assertTrue("saveModel", model != null);
    }

    @SneakyThrows
    @Test
    public void deleteModel(){
        Model model = modelService.getExecService().newModel();
        model.setCategory("wl_category_d1");
        model.setName("wl_name_d1");
        model.setKey("wl_key_d1");
        model.setTenantId("wl_tenantid_d1");
        model.setVersion(1);
        model.setMetaInfo(DataUtils.readFile(DataUtils.USER_DIR + "\\src\\test\\resources\\bpmn\\diagram.bpmn"));
        modelService.save(model);
        model = this.modelService.getOneByKey("wl_key_d1");
        this.modelService.delete(model.getId());
        model = this.modelService.getOneByKey("wl_key_d1");
        Assert.assertTrue("deleteModel", model == null);
    }

    @Test
    public void queryModel(){
        ModelQuery query = this.modelService.createQuery();
        query.modelNameLike("%wl%");
        long count = query.count();
        Assert.assertTrue("deleteModel count", count > 0);
        List<Model> models = query.listPage(0, 10);
        Assert.assertTrue("deleteModel listPage",  models.size() > 0);
        Model model = this.modelService.getOneByKey(models.get(0).getKey());
        Assert.assertTrue("deleteModel oneKey",  model != null);
    }

    @After
    public void tearDown() throws Exception {

    }
}
