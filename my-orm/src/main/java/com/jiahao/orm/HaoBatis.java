package com.jiahao.orm;

import com.jiahao.annotation.Column;
import com.jiahao.annotation.Id;
import com.jiahao.annotation.Table;
import com.jiahao.exception.MyException;
import com.jiahao.util.JdbcUtil;

import java.io.File;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义orm主要代码
 * @author JiaHao
 */

public class HaoBatis<T> implements IHaoBatis<T> {
    Class <T> entityClass;
    public HaoBatis() {

    }

    public HaoBatis(Class <T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 添加的方法
     * @param element
     * @return id
     */
     @Override
     public int add(T element){
         //拼接sql
         Map map = getInsertSql(element);
         //获取值
         return JdbcUtil.excuteUpdate((String) map.get("sql"),(Object[]) map.get("params"));
     }

    /**
     * 根据id插入，需要传入class
     * 返回0失败，返回1成功
     * @param id
     * @return
     */
     @Override
     public int delete(int id){
         System.out.println(entityClass.getSimpleName());

         //拼接sql
         String sql = getDeleteSql();
         //参数

         //执行sql
         Object[] params = new Object[1];
         params[0] = id;
         return JdbcUtil.excuteUpdate(sql,params);
     }

    /**
     * 根据id更新的方法
     *
     * @param element
     * @return
     */
    @Override
    public int update(T element) {
        //拼接sql语句
        String sql = getUpdateSql(element);
        //获取值
        Object [] params = getUpdateParams(element);
        return JdbcUtil.excuteUpdate(sql , params);
    }

    /**
     * 根据条件查询的方法
     * 0默认不搜索
     * @param element
     * @return
     */
    @Override
    public List<T> selectList(T element) {
        //拼接sql语句
        String sql = getSelectSql(element);
        //获取参数
        Object [] params = getSelectParams(element);

        ResultSet resultSet = JdbcUtil.excuteSelect(sql, params);
        //拼接结果集
        return getResult(resultSet);
    }

    /**
     * 获取所有的方法
     *
     * @return
     */
    @Override
    public List<T> getList() {
        return selectList(null);
    }

    /**
     * 根据id查询的方法
     *
     * @param id
     * @return
     */
    @Override
    public T selectById(int id) {
        if (entityClass == null){
            throw new MyException("请传入实体类class");
        }
        //构建查询对象
        try {
            T queryElement = entityClass.newInstance();
            //存id条件
            Field[] fields = entityClass.getDeclaredFields();
            //存是否有id属性
            int count = 0;
            for (Field field : fields){
                if (field.getAnnotation(Id.class) != null){
                    field.setAccessible(true);
                    //获得id属性，赋值
                    field.set(queryElement,id);
                    count ++;
                }
            }
            if (count == 0){
                //没有id属性
                throw new MyException("请检查实体类有没有@Id注解");
            }
            //执行查询
            List<T> list = selectList(queryElement);
            if (list.size() == 0){
                //结果为空
                return null;
            }else {
                return list.get(0);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据条件查询
     *
     * @param query
     * @return
     */
    @Override
    public List<T> selectByQuery(Query<T> query) {
        //拼接sql
        StringBuilder builder = new StringBuilder();
        //参数
        List<Object> list = new ArrayList<>();
        builder.append("SELECT * FROM ");
        //获取表
        if (entityClass == null){
            throw new MyException("没有传入实体类类型");
        }
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null){
            throw new MyException("实体类上没有@Table");
        }
        builder.append(table.name());
        int count = 0;
        builder.append(" WHERE ");
        //遍历条件集
        Map<String, Object> equalFields = query.getEqualFields();
        if (equalFields != null){
            for (String key : equalFields.keySet()){
                builder.append(key);
                builder.append(" = ? AND ");
                count ++;
                list.add(equalFields.get(key));
            }
        }
        Map<String, Object> startFields = query.getStartFields();
        if (startFields != null){
            for (String key : startFields.keySet()){
                builder.append(key);
                builder.append(" > ? AND ");
                count ++;
                list.add(startFields.get(key));
            }
        }
        Map<String, Object> endFields = query.getEndFields();
        if (endFields != null){
            for (String key : endFields.keySet()){
                builder.append(key);
                builder.append(" < ? AND ");
                count ++;
                list.add(endFields.get(key));
            }
        }
        Map<String, Object> likeFields = query.getLikeFields();
        if (likeFields != null){
            for (String key : likeFields.keySet()){
                builder.append(key);
                builder.append(" LIKE ? AND ");
                count ++;
                list.add(likeFields.get(key));
            }
        }
        List<String> sortFields = query.getSortFields();

        if (count == 0){
            //删除where
            builder.delete(builder.length() - 6,builder.length());
        }else {
            //有条件，去掉and
            builder.delete(builder.length() - 4,builder.length());
        }
        if (sortFields != null){
            for (String key : sortFields){
                builder.append(" ORDER BY ");
                builder.append(key);
                builder.append(",");
            }
            //去掉，
            builder.deleteCharAt(builder.length() - 1);
        }
        //添加分页
        if (query.getPage() != null && query.getSize() != null){
            //设置分页
            builder.append(" LIMIT ");
            builder.append(query.getPage());
            builder.append(",");
            builder.append(query.getSize());
        }
        //以获得sql 和 参数列表
        ResultSet resultSet = JdbcUtil.excuteSelect(builder.toString(), list.toArray());
        return getResult(resultSet);
    }

    private List<T> getResult(ResultSet resultSet) {
        List <T> result = new ArrayList<>();
        try {
            while (resultSet.next()){
                //创建对象
                Constructor<T> constructor = entityClass.getConstructor();
                T t = constructor.newInstance();
                //添加属性
                Field[] fields = entityClass.getDeclaredFields();
                Id id;
                Column column;
                for (Field field : fields){
                    field.setAccessible(true);
                    if ( (id = field.getAnnotation(Id.class)) != null ){
                        //id
                        field.set(t,resultSet.getObject(id.name()));
                    }
                    if ((column =  field.getAnnotation(Column.class)) != null ){
                        //列
                        field.set(t,resultSet.getObject(column.name()));
                    }
                }
                result.add(t);
            }
        }catch (SQLException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }finally {
            JdbcUtil.release();
        }

        return result;
    }

    private Object[] getSelectParams(T element) {
        //遍历参数
        Field[] fields = entityClass.getDeclaredFields();
        Object [] params = new Object[fields.length];
        int count = 0;
        for (int i = 0 ; i < fields.length ; i++){
            fields[i].setAccessible(true);
            Object o = null;
            try {
                o = fields[i].get(element);
                if (o != null && !o.equals(0)){
                    //参数不为空，添加
                    params[i - count] = fields[i].get(element);
                }else {
                    count ++;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return params;
    }

    /**
     * 拼接查询语句的方法
     *
     * @param element
     * @return
     */
    private String getSelectSql(T element) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ");
        //获取表名
        if (entityClass == null){
            entityClass = (Class<T>) element.getClass();
        }
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null){
            //没有table注解
            throw new MyException("实体类缺少@Table注解");
        }
        builder.append(table.name());
        builder.append(" where ");
        //遍历参数
        Field[] fields = entityClass.getDeclaredFields();
        int count = 0;
        for (Field field : fields){
            field.setAccessible(true);
            Object o = null;
            try {
                o = field.get(element);
                if (o != null && !o.equals(0)){
                    //参数不为空，添加
                    //如果参数为id
                    Id id ;
                    if ((id = field.getAnnotation(Id.class))!= null){
                        builder.append(id.name());
                        builder.append(" = ?,");
                        count ++;
                        continue;
                    }
                    Column column;
                    if ((column = field.getAnnotation(Column.class)) != null){
                        builder.append(column. name());
                        builder.append(" = ?,");
                        count ++;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //去掉最后一个,
        if (count != 0){
            builder.deleteCharAt(builder.length() - 1);
        }else {
            //没条件 去掉where
            builder.delete(builder.length() - 6 , builder.length());
        }

        return builder.toString();
    }

    private Object[] getUpdateParams(T element) {
        Field[] fields = entityClass.getDeclaredFields();
        int count = 0;
        Object [] params = new Object[fields.length];
        Field idFiled = null;
        //循环遍历出参数
        for (int i = 0 ; i < fields.length ;i++){
            //如果是id
            if (fields[i].getAnnotation(Id.class) != null){
                count ++;
                idFiled = fields[i];
            }else {
                //不是id
                try {
                    fields[i].setAccessible(true);
                    params[i - count] = fields[i].get(element);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Object id = null;
        try {
            assert idFiled != null;
            idFiled.setAccessible(true);
            id = idFiled.get(element);
            if (id == null){
                //没有值
                throw new MyException("id为空，更新失败");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        params[params.length - 1] = id;
        return params;
    }


    private String getUpdateSql(T element) {
        StringBuilder builder = new StringBuilder();
        //获取class
        if (entityClass == null){
            //获取
            entityClass = (Class<T>) element.getClass();
        }
        //拼接语句
        builder.append("UPDATE ");
        builder.append(entityClass.getAnnotation(Table.class).name());
        builder.append(" SET ");
        //获得所有字段
        Field [] fields = entityClass.getDeclaredFields();
        Field idField = null;
        for (Field field : fields){
            //如果是id
            if (field.getAnnotation(Id.class) != null){
                idField = field;
            }else {
                //不是id
                Column column = field.getAnnotation(Column.class);
                if (column != null){
                    builder.append(field.getAnnotation(Column.class).name());
                    builder.append(" = ?,");
                }
            }
        }
        //去掉最后一个，
        builder.deleteCharAt(builder.length() - 1);
        //where语句
        if (idField == null){
            throw  new MyException("没有id属性，请检查注解@Id" );
        }else {
            builder.append(" where ");
            builder.append(idField.getAnnotation(Id.class).name());
            builder.append(" = ?");
        }


        return builder.toString();
    }


    /**
     * 获取删除的sql语句
     * @return
     */
    private String getDeleteSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ");
        //获得表名
        if (entityClass == null){
            //没传入class
            throw new  MyException("请传入实体类类型");
        }else {
            Table table = entityClass.getAnnotation(Table.class);
            builder.append(table.name());
            builder.append(" ");
        }
        //拼接where语句
        builder.append("WHERE ");
        //获得id字段
        Field idField = getIdField(entityClass.getDeclaredFields());
        //拼接
        builder.append(idField.getAnnotation(Id.class).name());
        builder.append(" = ?");
        return builder.toString();
    }

    private Map getInsertSql(T element)   {
         StringBuilder builder = new StringBuilder();
        Class<?> clazz = element.getClass();
        //获取所有属性
        Field[] fields = clazz.getDeclaredFields();
        Object [] params ;
         builder.append("insert into ");
         //获取表名
        Table table = clazz.getAnnotation(Table.class);
        builder.append(table.name()).append(" ");
        Field idField = null;
        //获取id是否为自增
        idField = getIdField(fields);
        if(idField == null){
                throw new MyException("未设置id");
        }
        Id id = idField.getAnnotation(Id.class);
        //id字段为int类型
            if (id.increment() == 0){
                //非自增
                params = new Object[fields.length];
                builder.append("VALUES (");
                //循环遍历添加？
                for (int i= 0 ; i< fields.length ; i++){
                    try {
                        fields[i].setAccessible(true);
                        params[i] = fields[i].get(element);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    builder.append("?,");
                }
                //去掉最后一个,
                builder.deleteCharAt(builder.length() - 1);
                //添加）
                builder.append(")");
            }else {
                //自增
                params = new Object[fields.length - 1];
                //遍历添加列名
                builder.append("(");
                for (Field field : fields){
                    if (! field.equals(idField)){
                        //不是id
                        builder.append(field.getAnnotation(Column.class).name()).append(",");
                    }
                }
                //去掉最后一个,
                builder.deleteCharAt(builder.length() - 1);
                //添加）
                builder.append(") ");
                //添加value
                builder.append("VALUES (");
                int count = 0;
                //循环遍历添加？
                for (int i= 0 ; i< fields.length ; i++){
                    if (!fields[i].equals(idField)){
                        fields[i].setAccessible(true);
                        //不是id
                        builder.append("?,");
                        try {
                            params[i - count] = fields[i].get(element);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else {
                        //是id
                        count ++;
                    }
                }
                //去掉最后一个,
                builder.deleteCharAt(builder.length() - 1);
                //添加）
                builder.append(")");
            }
        System.out.println(builder);
        Map map = new HashMap(2);
        map.put("sql",builder.toString());
        map.put("params",params);

        return map;
    }

    /**
     * 获得id
     * @param fields
     * @return
     */
    private Field getIdField(Field[] fields) {
        Field idField  = null;
        for (Field field :fields){
            if (field.getAnnotation(Id.class) != null){
                idField = field;
            }
        }
        if (idField == null){
            throw new  MyException("没有@Id");
        }
        return idField;
    }
}
