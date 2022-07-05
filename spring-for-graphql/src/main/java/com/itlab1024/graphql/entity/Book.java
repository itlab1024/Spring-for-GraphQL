package com.itlab1024.graphql.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 书籍实体
 */
@Data
@Entity
public class Book {
    @Id
    private Long id;
    private String name;
    private String pageCount;
    @OneToOne()
    private Author author;
}
