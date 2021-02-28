package com.jiahao.orm;

import com.jiahao.util.JdbcUtil;

import java.util.List;
import java.util.Map;

/**
 * 自定义orm框架接口
 * @author JiaHao
 */
public interface IHaoBatis<T> {
    /**
     * 添加的方法
     * @param element
     * @return id
     */
    int add(T element);

    /**
     * 根据id插入，需要传入class
     * 返回0失败，返回1成功
     * @param id
     * @return
     */
    int delete(int id);

    /**
     * 更新的方法
     * @param element
     * @return
     */
    int update(T element);

    /**
     * 根据条件查询的方法
     * @param element
     * @return
     */
    List<T> selectList(T element);

    /**
     * 获取所有的方法
     * @return
     */
    List<T> getList();

    /**
     * 根据id查询的方法
     * @param id
     * @return
     */
    T selectById(int id);

    /**
     * 根据条件查询
     * @param query
     * @return
     */
    List<T> selectByQuery(Query<T> query);

}
