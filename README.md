# 원티드 프리온보딩 인턴십 과제 레포지토리
<hr/>
지원자 : 김노아

## Architecture
<img src="./img/architecture.png" width="600" height="300">

## TABLE ERD
<img src="./img/ERD.png" style="float: left;" width="350" height="300">
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

## docker-compose를 이용한 기동 가이드
### 1. 기동하기
<img src="gif/docker-compose.gif" width="520" height="270">

```
docker-compose up -d --scale application=3
```

### 2. 컨테이너 중지
```
docker-compose down -v
```
<br/>

## API
### 1. Sign-Up
<img src="gif/sign-up.gif" width="520" height="270">

`POST /api/auth/sign-up`
#### request
```http
curl -X POST 
     -H 'Accept: application/json;charset=UTF-8'
     -d {"email": "test@wanted.com", "password": "12345678"}
http://3.25.239.122:80/api/auth/sign-up
```

#### response
```
{
    "responseCode": 201,
    "data": "sign-up success"
}
```
<br/>

### 2. Log-In
<img src="./gif/login.gif" width="520" height="270">

`POST /api/login`

#### request
```http
curl -X POST 
     -H 'Accept: application/json;charset=UTF-8'
     -d {"email": "test@wanted.com", "password": "12345678"}
http://3.25.239.122:80/api/login
```

#### response
```
HTTP/1.1 200 
Server: nginx/1.25.1
Date: Tue, 15 Aug 2023 15:23:06 GMT
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Connection: keep-alive
Set-Cookie: access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBY2Nlc3MgVG9rZW4gKHRlc3RAd2FudGVkLmNvbSkiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTY5MjEyMDE4NiwiaWF0IjoxNjkyMTEyOTg2LCJlbWFpbCI6InRlc3RAd2FudGVkLmNvbSJ9.szle_rI_Lp4Il-5u0HEnmXFQWyF6kd-DAc2lxXd9_pY; Max-Age=7200; Expires=Tue, 15 Aug 2023 17:23:06 GMT; Secure; HttpOnly

{
  "responseCode": 200,
  "data": "login success"
}
```
<br/>

### 3. Post : 특정 게시글 조회
<img src="./gif/post-get.gif" width="520" height="270">

`GET /api/post?id=[post-id]`
#### request
```
curl -X GET http://3.25.239.122:80/api/post?id=1 
```

#### response
```
{
  "responseCode": 200,
  "data": {
    "id": 1,
    "title": "create post",
    "description": "hi",
    "authorEmail": "test@wanted.com"
  }
}
```
<br/>

### 4. Post : 특정 게시글 페이지 조회
<img src="./gif/post-page-get.gif" width="520" height="270">

#### request
```http                           
curl -X GET http://3.25.239.122:80/api/post/list?pageNumber=1&
                                                 pageSize=10&
                                                 sortColumn=createdDate&
                                                 sortOrder=ASC                           
```

#### response
```
{
  "responseCode": 200,
  "data": [
    {
      "id": 1,
      "title": "create post",
      "description": "hi",
      "authorEmail": "test@wanted.com"
    }
  ]
}
```

<br/>

### 5. Post : 게시글 생성
<img src="./gif/post-post.gif" width="520" height="270">

`POST /api/post`

#### request
```http
curl -X POST 
     -H 'Accept: application/json;charset=UTF-8'
     -H 'Authorization: Bearer [access-token]'
     -d {"title": "title", "description": "description"}
http://3.25.239.122:80/api/post
````

#### response
```
{
  "responseCode": 201,
  "data": "post create success"
}
```

<br/>

### 6. Post : 게시글 수정
<img src="./gif/post-put.gif" width="520" height="270">

`PUT /api/post?id=[post-id]`
#### request
```
curl -X PUT 
     -H 'Accept: application/json;charset=UTF-8'
     -H 'Authorization: Bearer [access-token]'
     -d {"title": "update title", "description": "update description"}
http://3.25.239.122:80/api/post?id=[post-id]
```

#### response
```
{
  "responseCode": 201,
  "data": "edit post success"
}
```
<br/>

### 7. Post : 게시글 삭제
<img src="./gif/post-delete.gif" width="520" height="270">

`DELETE /api/post?id=[post-id]`
#### request
```
curl -X DELETE
     -H 'Authorization: Bearer [access-token]' 
http://3.25.239.122:80/api/post?id=[post-id]
```

#### response
```
{
  "responseCode": 200,
  "data": "delete post success"
}
```
<br/>

