package com.itlab1024.graphql.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 作者实体
 */
@Data
@Entity
public class Author {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
}
