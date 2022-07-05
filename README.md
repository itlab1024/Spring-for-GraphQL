# 什么是GraphQL
[官方](https://graphql.org/)的描述是：GraphQL是API的查询语言，也是用现有数据完成这些查询的运行时。GraphQL对API中的数据进行了完整易懂的描述，使客户能够确切地询问他们需要什么，仅此而已，使API随着时间的推移更容易发展，并启用强大的开发人员工具。

具体什么意思呢？举个例子来说明。比如我们以软件常用功能"我的"的界面来说明，假如他只有一个接口/users/{id},返回的信息结构可能如下：
```json
{
	"id": 1,
	"name": "GraphQL小白",
	"pic": "http://xxx.img",
	"address": ["昌平区", "海淀区"]
}
```

考虑这种情况，这个接口同时被Web端和移动端使用。但是移动端鉴于设计或者流量考虑，并不显示address信息，那么就没有必要返回该字段。GraphQL就能够解决这样的问题。减少数据的冗余。



# GraphQL是如何做到的？

主要分为三个步骤

1. 描述数据

   使用结构化数据schema描述数据的样子。比如：

   ```
   type Project {
     name: String
     tagline: String
     contributors: [User]
   }
   ```

2. 通过schema设置自己需要的结构属性，用于提交请求，比如

   ```graphql
   {
     project(name: "GraphQL") {
       tagline
     }
   }
   ```

3. 获取结果

   ```json
   {
     "project": {
       "tagline": "A query language for APIs"
     }
   }
   ```

 name和contributors字段没有设置，就不会返回。

![官网描述](https://raw.githubusercontent.com/itlab1024/picgo-images/main/202207051244984.png)



# GraphQL语言支持

官方提供了多种语言支持

![GraphQL语言支持](https://raw.githubusercontent.com/itlab1024/picgo-images/main/202207051246987.png)

# GraphQL Java的使用

我先来看下原生的GraphQL Java实现如何使用。

[官方文档](https://www.graphql-java.com/documentation/getting-started/)

```java
public static void main(String[] args) {
  String schema = "type Query{hello: String, name: String}";

  SchemaParser schemaParser = new SchemaParser();
  TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

  RuntimeWiring runtimeWiring = newRuntimeWiring()
    .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world"))
          .dataFetcher("name", new StaticDataFetcher("graphQL")))
    .build();

  SchemaGenerator schemaGenerator = new SchemaGenerator();
  GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

  GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
  ExecutionResult executionResult = build.execute("{hello}");

  System.out.println(executionResult.getData().toString());
}
```

说明：

首先定义了一个schema，有两个字段，hello和name。

```
String schema = "type Query{hello: String, name: String}";
```

RuntimeWiring构造了数据，设置了hello和name两个字段。

然后通过GraphQL的execute方法，传递要获取的结构，比如上图中是`{hello}`，则结果会打印`{hello=world}`如果传递的是`{name}`则会输出`{name=graphQL}`。

本文的重点是要讲`Spring for GraphQL`,`GraphQL-Java`原生库的使用简单介绍下。

接下来来再简单介绍下`Spring Boot`如何使用GraphQL。

# Spring For GraphQL

Spring-for-graphql是Spring推出的graphql的Spring实现。官方地址是：https://spring.io/projects/spring-graphql，目前版本是1.0.0

![官网地址](https://raw.githubusercontent.com/itlab1024/picgo-images/main/202207051407404.png)

## 要求

以下是最低版本要求：

- JDK8
- Spring Framework 5.3
- GraphQL Java 18
- Spring Data 2021.1.0 or later for QueryDSL or Query by Example

## 服务传输

Spring for GraphQL支持服务器通过HTTP、WebSocket和RSocket处理GraphQL请求。

我首先学习下HTTP方式。

## HTTP

在http这种模式下，spring-for-graphql支持Spring MVC以及Spring WebFlux两种方式。

## 开始实现

创建项目引入相关依赖

pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.1</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.itlab1024</groupId>
    <artifactId>spring-for-graphql</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-for-graphql</name>
    <description>spring-for-graphql</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-graphql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-core</artifactId>
        </dependency>
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-apt</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webflux</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.graphql</groupId>
            <artifactId>spring-graphql-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <!--该插件可以生成querysdl需要的查询对象，执行mvn compile即可-->
            <plugin>
                <groupId>com.mysema.maven</groupId>
                <artifactId>apt-maven-plugin</artifactId>
                <version>1.1.3</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>process</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>target/generated-sources</outputDirectory>
                            <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>

    </build>

</project>

```



application.yaml配置数据库

```yaml
spring:
  graphql:
    schema:
      printer:
        enabled: true
  datasource:
    url: jdbc:mysql://localhost:3306/spring-for-graphql
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: qwe!@#123
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create
```



创建两个数据库Entity类。

```java
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

//-------------

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
```

创建Repository接口，用于请求数据库， 特别要注意的是实现了QuerydslPredicateExecutor。

```java
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


//-----
package com.itlab1024.graphql.repository;

import com.itlab1024.graphql.entity.Book;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.graphql.data.GraphQlRepository;

@GraphQlRepository
public interface BookRepository extends
        CrudRepository<Book, Long>, QuerydslPredicateExecutor<Book> {
}

```



在resource中创建/graphql/schema.graphqls文件，并配置书籍作者结构体信息。

```tex
type Query {
    bookById(id: ID): Book
}

type Book {
    id: ID
    name: String
    pageCount: Int
    author: Author
}

type Author {
    id: ID
    firstName: String
    lastName: String
}
```



## 安装插件

该插件对于编写schema.graphqls文件很方便。

![image-20220705133504710](https://raw.githubusercontent.com/itlab1024/picgo-images/main/202207051335811.png)



## 测试

启动项目，向数据库中的两张表中增加两条数据。

Book：

| id   | name        | page\_count | author\_id |
| :--- | :---------- | :---------- | :--------- |
| 1    | Spring 入坑 | 1           | 1          |

Author：

| id   | first\_name | last\_name |
| :--- | :---------- | :--------- |
| 1    | itlab       | 1024       |

使用postman测试：

![image-20220705161038297](https://raw.githubusercontent.com/itlab1024/picgo-images/main/202207051610704.png)

返回的结果是：

```json
{
    "data": {
        "bookById": {
            "id": "1",
            "name": "Spring 入坑",
            "pageCount": 1,
            "author": {
                "firstName": "itlab",
                "lastName": "1024"
            }
        }
    }
}
```



如果我不想返回author信息，只需要修改请求的参数为

```tex
{
  bookById(id: 1){
    id
    name
    pageCount
  }
}
```

使用postman请求

![image-20220705161213566](https://raw.githubusercontent.com/itlab1024/picgo-images/main/202207051612643.png)

返回结果是

```json
{
    "data": {
        "bookById": {
            "id": "1",
            "name": "Spring 入坑",
            "pageCount": 1
        }
    }
}
```

可以看到Author的数据没有返回。

## 补充

官方文档主要说了原理，我跟着文档实现了上述功能，在尝试过程中也遇到了问题，列出来，希望可以帮助到你。避免踩坑。

* DSL问题

  pom中要引入如下插件。

  ```xml
  <!--该插件可以生成querysdl需要的查询对象，执行mvn compile即可-->
  <plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
      <execution>
        <goals>
          <goal>process</goal>
        </goals>
        <configuration>
          <outputDirectory>target/generated-sources</outputDirectory>
          <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
        </configuration>
      </execution>
    </executions>
  </plugin>
  ```

  该插件是DSL生成Java类使用的，我只创建了Book和Author类。该插件在编译的时候会创建QBook和QAuthor类。

  ```java
  //
  // Source code recreated from a .class file by IntelliJ IDEA
  // (powered by FernFlower decompiler)
  //
  
  package com.itlab1024.graphql.entity;
  
  import com.querydsl.core.types.Path;
  import com.querydsl.core.types.PathMetadata;
  import com.querydsl.core.types.PathMetadataFactory;
  import com.querydsl.core.types.dsl.EntityPathBase;
  import com.querydsl.core.types.dsl.NumberPath;
  import com.querydsl.core.types.dsl.StringPath;
  
  public class QAuthor extends EntityPathBase<Author> {
      private static final long serialVersionUID = -1194475303L;
      public static final QAuthor author = new QAuthor("author");
      public final StringPath firstName = this.createString("firstName");
      public final NumberPath<Long> id = this.createNumber("id", Long.class);
      public final StringPath lastName = this.createString("lastName");
  
      public QAuthor(String variable) {
          super(Author.class, PathMetadataFactory.forVariable(variable));
      }
  
      public QAuthor(Path<? extends Author> path) {
          super(path.getType(), path.getMetadata());
      }
  
      public QAuthor(PathMetadata metadata) {
          super(Author.class, metadata);
      }
  }
  // -----
  
  //
  // Source code recreated from a .class file by IntelliJ IDEA
  // (powered by FernFlower decompiler)
  //
  
  package com.itlab1024.graphql.entity;
  
  import com.querydsl.core.types.Path;
  import com.querydsl.core.types.PathMetadata;
  import com.querydsl.core.types.PathMetadataFactory;
  import com.querydsl.core.types.dsl.EntityPathBase;
  import com.querydsl.core.types.dsl.NumberPath;
  import com.querydsl.core.types.dsl.PathInits;
  import com.querydsl.core.types.dsl.StringPath;
  
  public class QBook extends EntityPathBase<Book> {
      private static final long serialVersionUID = -345352777L;
      private static final PathInits INITS;
      public static final QBook book;
      public final QAuthor author;
      public final NumberPath<Long> id;
      public final StringPath name;
      public final StringPath pageCount;
  
      public QBook(String variable) {
          this(Book.class, PathMetadataFactory.forVariable(variable), INITS);
      }
  
      public QBook(Path<? extends Book> path) {
          this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
      }
  
      public QBook(PathMetadata metadata) {
          this(metadata, PathInits.getFor(metadata, INITS));
      }
  
      public QBook(PathMetadata metadata, PathInits inits) {
          this(Book.class, metadata, inits);
      }
  
      public QBook(Class<? extends Book> type, PathMetadata metadata, PathInits inits) {
          super(type, metadata, inits);
          this.id = this.createNumber("id", Long.class);
          this.name = this.createString("name");
          this.pageCount = this.createString("pageCount");
          this.author = inits.isInitialized("author") ? new QAuthor(this.forProperty("author")) : null;
      }
  
      static {
          INITS = PathInits.DIRECT2;
          book = new QBook("book");
      }
  }
  ```

在target下可以找到该类

![image-20220705162332963](https://raw.githubusercontent.com/itlab1024/picgo-images/main/202207051623073.png)

这就实现了动态获取数据的功能。

>  至此，一个简单的Spring-for-graphql功能已经实现。
>
> 提交分支v1.0.0
