package com.jiahao.domain;

import com.jiahao.annotation.Column;
import com.jiahao.annotation.Id;
import com.jiahao.annotation.Table;

import java.util.Date;

/**
 * 课程实体类
 * @author JiaHao
 */
@Table(name = "course")
public class Course {
    @Id(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "startTime",type = "date")
    private Date startDate;
    @Column(name = "endTime",type = "date")
    private Date endDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
