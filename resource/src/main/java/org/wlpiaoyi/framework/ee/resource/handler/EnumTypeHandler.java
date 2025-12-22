package org.wlpiaoyi.framework.ee.resource.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.wlpiaoyi.framework.ee.resource.domain.enums.BaseEnum;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis枚举类型处理器
 * <p>
 * 用于将实现了{@link BaseEnum}接口的枚举类型与数据库中的整型字段进行映射转换
 *
 * @param <T> 枚举类型，必须实现BaseEnum接口
 * @author wlpiaoyi
 * @version 2.0
 * @date 2025/12/19 11:02
 */
public class EnumTypeHandler<T extends BaseEnum> extends BaseTypeHandler<T> {

    /**
     * 枚举类型缓存，避免重复反射获取
     */
    private final Class<T> enumType;

    /**
     * 枚举值缓存：value -> enum instance
     * 使用ConcurrentHashMap确保线程安全
     */
    private final Map<Integer, T> valueCache = new ConcurrentHashMap<>();

    /**
     * 枚举名称缓存：name -> enum instance
     */
    private final Map<String, T> nameCache = new ConcurrentHashMap<>();

    // 私有无参构造（防止 MyBatis 报错，但不会被真正使用）
    @SuppressWarnings("unused")
//    public EnumTypeHandler() {
//        this.enumType = null;
//    }

    /**
     * 构造方法
     *
     * @param enumType 枚举类型Class对象，不能为null
     * @throws IllegalArgumentException 当enumType为null时抛出
     */
    public EnumTypeHandler(Class<T> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("枚举类型参数不能为null");
        }
        this.enumType = enumType;
        initializeCache(); // 初始化缓存
    }

    /**
     * 初始化枚举缓存
     * 在构造方法中调用，预加载所有枚举实例到缓存中
     */
    private void initializeCache() {
        T[] enumConstants = enumType.getEnumConstants();
        if (enumConstants == null || enumConstants.length == 0) {
            throw new IllegalArgumentException("枚举类型" + enumType.getName() + "没有定义枚举常量");
        }

        for (T enumConstant : enumConstants) {
            if (enumConstant.getValue() == null) {
                throw new IllegalStateException("枚举" + enumType.getName() + "的值不能为null");
            }
            Integer value;
            if(enumConstant.getValue() instanceof Integer){
                value = (Integer) enumConstant.getValue();
            }else if (enumConstant.getValue() instanceof Boolean){
                value = (Boolean) enumConstant.getValue() ? 1 : 0;
            }else throw new IllegalArgumentException("枚举类型" + enumType.getName() + "的值类型不能为" + enumConstant.getValue().getClass().getName());
            // 检查值是否重复
            if (valueCache.containsKey(value)) {
                throw new IllegalStateException("枚举" + enumType.getName() +
                        "中存在重复的值：" + value);
            }

            valueCache.put(value, enumConstant);
            nameCache.put(enumConstant.name(), enumConstant);
        }
    }

    /**
     * 根据整数值获取枚举实例
     * 使用缓存提高性能
     *
     * @param value 枚举的整数值
     * @return 对应的枚举实例，如果找不到则返回null
     */
    private T getByValue(int value) {
        return valueCache.get(value);
    }

    /**
     * 设置非空参数到PreparedStatement中
     * 将枚举的整数值设置到SQL参数中
     *
     * @param ps PreparedStatement对象
     * @param i 参数索引（从1开始）
     * @param parameter 枚举参数
     * @param jdbcType JDBC类型（可为null）
     * @throws SQLException SQL异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType)
            throws SQLException {
        // 直接使用枚举的getValue()方法获取整数值
        if(parameter.getValue() instanceof Integer){
            ps.setInt(i, (Integer) parameter.getValue());
        }else if (parameter.getValue() instanceof Boolean){
            ps.setBoolean(i, (Boolean) parameter.getValue());
        }else throw new IllegalArgumentException("枚举类型" + enumType.getName() + "的值类型不能为" + parameter.getValue().getClass().getName());
    }

    /**
     * 从ResultSet中获取可为空的枚举值（通过列名）
     *
     * @param rs ResultSet对象
     * @param columnName 列名
     * @return 枚举实例，如果数据库值为null则返回null
     * @throws SQLException SQL异常
     */
    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return resolveEnum(value);
    }

    /**
     * 从ResultSet中获取可为空的枚举值（通过列索引）
     *
     * @param rs ResultSet对象
     * @param columnIndex 列索引（从1开始）
     * @return 枚举实例，如果数据库值为null则返回null
     * @throws SQLException SQL异常
     */
    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return resolveEnum(value);
    }

    /**
     * 从CallableStatement中获取可为空的枚举值
     *
     * @param cs CallableStatement对象
     * @param columnIndex 列索引（从1开始）
     * @return 枚举实例，如果数据库值为null则返回null
     * @throws SQLException SQL异常
     */
    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return resolveEnum(value);
    }

    /**
     * 解析整数值到枚举实例
     * 统一处理枚举值的解析逻辑
     *
     * @param value 整数值
     * @return 对应的枚举实例
     * @throws IllegalArgumentException 如果找不到对应的枚举值
     */
    private T resolveEnum(int value) {
        T enumInstance = getByValue(value);
        if (enumInstance == null) {
            // 如果找不到对应的枚举值，可以记录日志或抛出更友好的异常
            throw new IllegalArgumentException(String.format(
                    "无法将值 %d 转换为枚举类型 %s，请检查枚举定义",
                    value, enumType.getSimpleName()
            ));
        }
        return enumInstance;
    }
//
//    /**
//     * 获取枚举类型
//     *
//     * @return 枚举类型Class对象
//     */
//    public Class<T> getEnumType() {
//        return enumType;
//    }
//
//    /**
//     * 清空缓存（主要用于测试）
//     */
//    public void clearCache() {
//        valueCache.clear();
//        nameCache.clear();
//        initializeCache(); // 重新初始化
//    }
//
//    /**
//     * 获取枚举值的缓存大小
//     *
//     * @return 缓存中枚举值的数量
//     */
//    public int getValueCacheSize() {
//        return valueCache.size();
//    }
//
//    /**
//     * 获取枚举名称的缓存大小
//     *
//     * @return 缓存中枚举名称的数量
//     */
//    public int getNameCacheSize() {
//        return nameCache.size();
//    }
}