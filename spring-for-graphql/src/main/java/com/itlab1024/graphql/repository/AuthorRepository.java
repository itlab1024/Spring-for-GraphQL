package com.itlab1024.graphql.repository;

import com.itlab1024.graphql.entity.Author;
import com.itlab1024.graphql.entity.Book;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.graphql.data.GraphQlRepository;

@GraphQlRepository
public interface AuthorRepository extends
        CrudRepository<Author, Long>, QuerydslPredicateExecutor<Author> {
}
