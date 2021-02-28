package com.jiahao.orm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装查询条件
 * @author JiaHao
 */
public class Query<T> {
    /**
     * 精确匹配字段
     * map是字段名和值
     */
    private Map<String,Object> equalFields;
    /**
     * 模糊匹配字段
     */
    private Map<String,Object> likeFields;
    /**
     * 范围匹配字段
     */
    private Map<String,Object> startFields;
    private Map<String,Object> endFields;
    /**
     * 排序字段
     */
    private List<String> sortFields;
    /**
     * 分页条件
     */
    private Integer page;
    private Integer size;

    /**
     * 做分页的方法
     * @param page
     * @param size
     * @return
     */
    public Query<T> doPage(int page,int size){
        this.page = page;
        this.size = size;
        return this;
    }
    /**
     * 相等
     */
    public Query<T> equal(String field , Object value){
        if (equalFields == null){
            equalFields = new HashMap<>();
        }
        equalFields.put(field,value);
        return this;
    }
    /**
     * 范围 开始
     */
    public Query<T> start(String field , Object value){
        if (startFields == null){
            startFields = new HashMap<>();
        }
        startFields.put(field,value);
        return this;
    }
    /**
     * 范围 结束
     */
    public Query<T> end(String field , Object value){
        if (endFields == null){
            endFields = new HashMap<>();
        }
        endFields.put(field,value);
        return this;
    }
    /**
     * 范围 模糊
     */
    public Query<T> like(String field , Object value){
        if (likeFields == null){
            likeFields = new HashMap<>();
        }
        likeFields.put(field,value);
        return this;
    }
    /**
     * 范围 排序
     */
    public Query<T> sort(String field){
        if (sortFields == null){
            sortFields = new ArrayList<>();
        }
        sortFields.add(field);
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public Map<String, Object> getEqualFields() {
        return equalFields;
    }

    public Map<String, Object> getLikeFields() {
        return likeFields;
    }

    public Map<String, Object> getStartFields() {
        return startFields;
    }

    public Map<String, Object> getEndFields() {
        return endFields;
    }

    public List<String> getSortFields() {
        return sortFields;
    }
}
