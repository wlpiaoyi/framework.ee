package org.wlpiaoyi.framework.ee.file.manager.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.core.ResolvableType;
import org.springframework.transaction.annotation.Transactional;
import org.wlpiaoyi.framework.ee.file.manager.domain.entity.BaseEntity;
import org.wlpiaoyi.framework.ee.file.manager.service.IBaseService;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/16 12:26
 * {@code @version:}:       1.0
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements IBaseService<T> {

    protected Class<T> currentMapperClass() {
        return (Class)this.getResolvableType().as(BaseServiceImpl.class).getGeneric(new int[]{0}).getType();
    }

    protected Class<T> currentModelClass() {
        return (Class)this.getResolvableType().as(BaseServiceImpl.class).getGeneric(new int[]{1}).getType();
    }

    protected ResolvableType getResolvableType() {
        return ResolvableType.forClass(ClassUtils.getUserClass(this.getClass()));
    }

    public boolean save(T entity) {
        this.resolveEntity(entity);
        return super.save(entity);
    }

    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        entityList.forEach(this::resolveEntity);
        return super.saveBatch(entityList, batchSize);
    }

    public boolean updateById(T entity) {
        this.resolveEntity(entity);
        return super.updateById(entity);
    }

    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        entityList.forEach(this::resolveEntity);
        return super.updateBatchById(entityList, batchSize);
    }

    public boolean saveOrUpdate(T entity) {
        this.resolveEntity(entity);
        return entity.getId() == null ? this.save(entity) : this.updateById(entity);
    }

    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        entityList.forEach(this::resolveEntity);
        return super.saveOrUpdateBatch(entityList, batchSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLogic(List<Long> ids) {

        List<T> list = new ArrayList();
        ids.forEach((id) -> {
            T entity;
            try {
                entity = this.currentModelClass().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            entity.setIsDeleted(1);
            entity.setUpdateTime(new Date());
            entity.setId(id);
            list.add(entity);
        });
        return super.updateBatchById(list) && super.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(List<Long> ids, Integer status) {

        List<T> list = new ArrayList();
        ids.forEach((id) -> {
            T entity;
            try {
                entity = this.currentModelClass().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            entity.setUpdateTime(new Date());
            entity.setId(id);
            entity.setStatus(status);
            list.add(entity);
        });
        return super.updateBatchById(list);
    }

    private void resolveEntity(T entity) {
        try {
            if(ValueUtils.isBlank(entity.getId())){
                entity.setId(IdUtils.nextId());
            }

        } catch (Throwable var8) {
            throw var8;
        }
    }

}
