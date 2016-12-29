# AccountBook
Home account book application.

## Environments

- Ubuntu 16.04.1 LTS amd64
- OpenJDK 8
- Gradle 2.10


## How to run

```
$ git clone https://github.com/hashiwa000/AccountBook.git
$ cd AccountBook
$ vi src/main/resources/config/application.yml (edit database url)
$ vi src/test/resources/config/application.yml (edit database url)
$ gradle test
$ gradle bootRun
```

Access http://localhost:8080/accountbook from your browser.

## How to use REST API

At first, store authenticated cookies.

```
$ curl -X POST -d username=<username> -d password=<password> -c /tmp/cookies.jar http://localhost:8080/login

$ file /tmp/cookies.jar
/tmp/cookies.jar: Netscape cookie, ASCII text
```

And, do RESTful API by using the cookies.

```
$ curl -X POST -b /tmp/cookies.jar -d 'date=2016-12-15&amount=1000&name=test&type=xxx' http://localhost:8080/rest/accountbook | python -m json.tool
{
    "amount": 1000,
    "date": 1481727600000,
    "description": "",
    "id": 1401,
    "name": "test",
    "remarks": "",
    "type": "xxx"
}

$ curl -X GET -b /tmp/cookies.jar http://localhost:8080/rest/accountbook/1401 | python -m json.tool
{
    "amount": 1000,
    "date": 1481727600000,
    "description": "",
    "id": 1401,
    "name": "test",
    "remarks": "",
    "type": "xxx"
}

```

