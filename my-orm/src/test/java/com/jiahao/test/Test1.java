package com.jiahao.test;

import com.jiahao.annotation.Table;
import com.jiahao.domain.Course;
import com.jiahao.orm.HaoBatis;
import com.jiahao.orm.Query;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Test1 {
    @Test
    public void testInsertSql(){
        HaoBatis<Course> haoBatis = new HaoBatis<Course>();
        Course course = new Course();
        course.setName("大学语文");
        course.setStartDate(new Date());
        course.setEndDate(new Date());
        int add = haoBatis.add(course);
    }
    @Test
    public void testDelete(){
        HaoBatis<Course> haoBatis = new HaoBatis<>(Course.class);
        int id = haoBatis.delete(13);
        System.out.println(id);
    }
    @Test
    public void testUpdate(){
        HaoBatis<Course> haoBatis = new HaoBatis<>(Course.class);
        Course course = new Course();
        course.setId(13);
        course.setName("JAVA程序设计");
        course.setStartDate(new Date());
        course.setEndDate(new Date());
        haoBatis.update(course);
    }
    @Test
    public void testSelectList(){
        HaoBatis<Course> haoBatis = new HaoBatis<>(Course.class);
        Course course = new Course();
        System.out.println( haoBatis.selectList(course));
    }
    @Test
    public void testGetList(){
        HaoBatis<Course> haoBatis = new HaoBatis<>(Course.class);
        Course course = new Course();
        System.out.println( haoBatis.getList());
    }
    @Test
    public void testSelectById(){
        HaoBatis<Course> haoBatis = new HaoBatis<>(Course.class);
        Course course = new Course();
        System.out.println( haoBatis.selectById(14));
    }
    @Test
    public void testSelectByQuery() throws ParseException {
        HaoBatis<Course> haoBatis = new HaoBatis<>(Course.class);
        Query<Course> query = new Query<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = format.parse("2021-2-27");
        Date endTime = format.parse("2021-3-1");

        //query.start("startTime",startTime);
        //query.end("startTime",endTime);
        query.like("name","大学%").doPage(2,2);
        List<Course> courses = haoBatis.selectByQuery(query);
        System.out.println(courses);
    }
}
