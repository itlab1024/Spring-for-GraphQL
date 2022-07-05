# 什么是GraphQL
[官方](https://graphql.org/)的描述是：GraphQL是API的查询语言，也是用现有数据完成这些查询的运行时。GraphQL对API中的数据进行了完整易懂的描述，使客户能够确切地询问他们需要什么，仅此而已，使API随着时间的推移更容易发展，并启用强大的开发人员工具。

具体什么意思呢？举个例子来说明。比如我们以软件常用功能"我的"的界面来说明，假如他只有一个接口/users/{id},返回的信息结构可能如下：
```json
{
	"id": 1,
	"name": "GraphQL小白",
	"pic": "http://xxx.img",
	"address": ["昌平区", "海淀区"],
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

# Spring Boot使用GraphQL

## 说明

本节通过Spring Boot创建一个GraphQL的服务。

GraphQL是一种从服务器检索数据的查询语言。在某种程度上，它是REST、SOAP或gRPC的替代品

我根据官方示例，创建一个spring boot项目，来实现获取图书详细信息的功能。



使用GraphQL，您可以将以下查询发送到服务器，以获取ID为“book-1”的书籍的详细信息：

```graphql
{
  bookById(id: "book-1"){
    id
    name
    pageCount
    author {
      firstName
      lastName
    }
  }
}
```

服务器响应的数据应该是如下结构：

```json
{
  "bookById":
  {
    "id":"book-1",
    "name":"Harry Potter and the Philosopher's Stone",
    "pageCount":223,
    "author": {
      "firstName":"Joanne",
      "lastName":"Rowling"
    }
  }
}
```

## 创建项目
