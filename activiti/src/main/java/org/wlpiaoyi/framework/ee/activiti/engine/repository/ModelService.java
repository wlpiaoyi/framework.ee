package org.wlpiaoyi.framework.ee.activiti.engine.repository;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.ibatis.cache.CacheException;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.exception.CatchException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模型管理
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/4/5 18:23
 * {@code @version:}:       1.0
 */

public class ModelService extends BaseService {


    public ModelService(ProcessEngine processEngine) {
        super(processEngine);
    }


    public ModelQuery createQuery(){
        return super.createQuery(ModelQuery.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String modelId){
        this.execService.deleteModel(modelId);
    }

    public Model getOne(String modelId){
        return this.execService.getModel(modelId);
    }


    public Model getOneByKey(String key){
        List<Model> models = this.createQuery().modelKey(key).list();
        if(ValueUtils.isBlank(models)){
            return null;
        }
        return models.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public Model save(Model model) throws CatchException {
        if(ValueUtils.isBlank(model.getCategory())){
            throw new CatchException(501, "模板分类不能为空");
        }
        if(ValueUtils.isBlank(model.getKey())){
            model.setKey(StringUtils.getUUID32());
        }
        if(ValueUtils.isBlank(model.getName())){
            throw new CatchException(501, "模板名称不能为空");
        }
        if(ValueUtils.isBlank(model.getMetaInfo())){
            throw new CatchException(501, "模板内容不能为空");
        }
        Model dbM = null;
        if (!ValueUtils.isBlank(model.getId())){
            dbM = this.getOne(model.getId());
        }else if (!ValueUtils.isBlank(model.getKey())){
            dbM = this.getOneByKey(model.getKey());
        }
        if(dbM != null){
            dbM.setMetaInfo(model.getMetaInfo());
            dbM.setVersion(model.getVersion());
            dbM.setName(model.getName());
            dbM.setCategory(model.getCategory());
            dbM.setDeploymentId(model.getDeploymentId());
            dbM.setTenantId(model.getTenantId());
        }
        RepositoryService repositoryService = this.processEngine.getRepositoryService();
        this.checkMetaInfo(model);
        repositoryService.saveModel(model);

        return model;
    }

    private static void checkMetaInfo(Model model) throws CatchException {

        String regex = "<bpmn2:definitions([\\w\\d\\/\\.\"\'\\-:\n\t\r =\\u4e00-\\u9fa5^>]*)>";
        // 匹配当前正则表达式
        Matcher matcher = Pattern.compile(regex).matcher(model.getMetaInfo());
        String bpmHead = null;
        int bpmHeaderStart = 0, bpmHeaderEnd = 0;
        if (matcher.find()) {
            bpmHead = matcher.group();
            bpmHeaderStart = matcher.start();
            bpmHeaderEnd = matcher.end();
        }
        if(ValueUtils.isBlank(bpmHead) || bpmHeaderStart < 0 || bpmHeaderEnd <= 0){
            throw new CatchException(501, "没有找到'xml header'");
        }
        regex = "id=\"([\\w\\d\\/\\.\\-:]*)\"";
        // 匹配当前正则表达式
        matcher = Pattern.compile(regex).matcher(bpmHead);
        // 判断是否可以找到匹配正则表达式的字符
        if (matcher.find()) {
            bpmHead = bpmHead.substring(0,matcher.start()) +
                    "id=\"" + model.getKey()+ "\"" +
                    bpmHead.substring(matcher.end(), bpmHead.length());
        }

        regex = "targetNamespace=\"([\\w\\d\\/\\.\\-:]*)\"";
        // 匹配当前正则表达式
        matcher = Pattern.compile(regex).matcher(bpmHead);
        // 判断是否可以找到匹配正则表达式的字符
        if (matcher.find()) {
            bpmHead = bpmHead.substring(0,matcher.start()) +
                    "targetNamespace=\"" + model.getCategory()+ "\"" +
                    bpmHead.substring(matcher.end(), bpmHead.length());
        }else{
            throw new CatchException(501, "没有找到targetNamespace");
        }

        model.setMetaInfo(
            model.getMetaInfo().substring(0,bpmHeaderStart) +
                    bpmHead +
                    model.getMetaInfo().substring(bpmHeaderEnd, model.getMetaInfo().length())
        );

        regex = "<bpmn2:process([\\w\\d\\/\\.\"\'\\-:\n\t\r =\\u4e00-\\u9fa5^>]*)>";
        // 匹配当前正则表达式
        matcher = Pattern.compile(regex).matcher(model.getMetaInfo());
        boolean hasMatherProcess = false;
        // 判断是否可以找到匹配正则表达式的字符
        if (matcher.find()) {
            hasMatherProcess = true;
            String replaceStr = matcher.group();
            String regex1 = "id=\"([\\w\\d\\/\\.\\-: =\\u4e00-\\u9fa5]*)\"";
            replaceStr = replaceStr.replaceAll(regex1, "id=\"" + model.getKey()+"\"");
            regex1 = "name=\"([\\w\\d\\/\\.\\-: =\\u4e00-\\u9fa5]*)\"";
            replaceStr = replaceStr.replaceAll(regex1, "name=\"" + model.getName()+"\"");
            regex1 = "activiti:versionTag=\"([\\d]*)\"";
            replaceStr = replaceStr.replaceAll(regex1, "activiti:versionTag=\"" + model.getVersion() +"\"");
            model.setMetaInfo(
                    model.getMetaInfo().substring(0,matcher.start()) +
                            replaceStr +
                            model.getMetaInfo().substring(matcher.end(), model.getMetaInfo().length())
            );
        }
        if(!hasMatherProcess){
            throw new CatchException(501, "xml没有找到'bpmn2:process'节点");
        }
    }

}
