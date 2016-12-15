# AccountBook
Todo List with Spring Boot

## Environments

- Ubuntu 16.04.1 LTS amd64
- OpenJDK 8
- Gradle 2.10


## How to run

```
$ git clone https://github.com/hashiwa000/AccountBook.git
$ cd TodoListWithSpringBoot
$ vi src/main/resources/config/application.yml (edit database url)
$ vi src/test/resources/config/application.yml (edit database url)
$ gradle test
$ gradle bootRun
```

Access http://localhost:8080/accountbook from your browser.

## How to use REST API

```
$ curl -X POST -d 'title=test&memo=This is memo.&deadline=2016-11-28 00:00:00' localhost:8080/rest/accountbook
[{"id":1,"title":"test","memo":"This is memo.","deadline":1480258800000}]

$ curl localhost:8080/rest/accountbook
[{"id":1,"title":"test","memo":"This is memo.","deadline":1480258800000}]
```

