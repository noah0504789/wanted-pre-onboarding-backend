# 원티드 프리온보딩 인턴십 과제 레포지토리
<hr/>
지원자 : 김노아

## Architecture
<img src="./img/architecture.png">
<br/>

## TABLE ERD
<img src="img/erd.png" style="float: left;">
<br/>

## 구현 설명
### Spring Security 도입
- 인증 기능을 제공하기 위해 Spring Security를 도입하였습니다.
  - 역할 분담과 유연성, Spring Boot의 통합된 보안 기능 제공  
  - 모듈화되어 확장성 및 커스터마이징을 할 수 있음 

#### Login
- 로그인을 핸들러에서 처리하는 것이 아닌, 필터단에서 처리하도록 하였습니다. 
- 핸들러까지 갈 필요가 없으며, 간결하고 명확한 역할 분리를 위해 도입하였습니다.
- `UsernamePasswordAuthenticationFilter`를 확장하여 구현하였습니다 
  - 인증을 담당하는 `LoginProvider`를 구현하였습니다.
  - 각 상황별 역할을 담당하는 `LoginSuccessHandler`, `LoginFailureHandler`를 구현하였습니다. 

#### Authentication with Jwt Token
- 인증이 필요한 API는 handler까지 요청이 도달하기 전에, 인증 헤더값인 access token 값을 검증을 위해 `JwtAuthenticationFilter`를 거치도록 하였습니다.
- 인증이 필요하지 않은 API는 spring security 필터체인을 무시하도록 URL RequestMatcher를 설정하였습니다.

#### CustomExceptionHandlerFilter
- 인증, 인가 예외가 아닌 클라이언트 예외를 처리하기 위해 spring security filter 맨 앞단에 exception을 처리하는 handler를 두었습니다.
- 로그인 요청 - `LoginInvalidException` 
    - invalid form 응답 
-  Secured URL 요청 - `AuthenticationException`, `JwtException`
    - 올바르지 않은 인증 헤더 응답  
    - 유효하지 않은 access token 응답   

### GlobalExceptionHandler
- `@RestControllerAdvice` 어노테이션을 사용
  - 컨트롤러에서 발생하는 모든 예외를 중앙 집중적으로 처리하도록 설계하였습니다.
  - 예외 처리 로직이 전체 애플리케이션에 분산되지 않고, 한 곳에서 관리되어 유지보수성과 가독성을 향상시키고자 했습니다.
- validation 실패 응답 - `MethodArgumentNotValidException`
- 잘못된 자원 요청 응답 - `IllegalArgumentException`, `NumberFormatException`, `MissingServletRequestParameterException`

### Test
#### secured URL 접근 허용 어노테이션 사용 
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {
    // ...

    @Test
    @WithUserDetails(value = emailOfExistsUser, userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("edit() 실패 : 유효하지 않은 조회중인 게시글 ID를 전달하고 게시글 수정 요청")
    void edit_failure_invalid_queryString_value() throws Exception {
        // ...
    }
}
```

- 게시글 수정 요청을 테스트하기 전에 인증을 성공해야 합니다.
- 해당 컨트롤러 테스트는 게시글 수정 컨트롤러 핸들러만을 테스트하므로 인증 과정을 거치는 코드가 작성되는 것은 의도에 맞지 않으며 단일 책임 원칙이 위배됩니다.
- `@WithMockUser`를 사용하여 Security Filter를 거치지 않은 채 핸들러가 요청을 처리할 떄 미리 목 유저로 만들어진 `Authentication` 객체를 `SecurityContext`에 저장하려 하였습니다.
- 그러나 `UserDetailsService`를 구현한 `UserService`는 `UserDetails`를 확장한 User 도메인 객체를 리턴하므로 핸들러에서 `@AuthenticationPrincipal` 어노테이션으로 인증 객체를 가져올 수 없습니다.
- 이를 위해 스프링 시큐리티에서는 `UserDetails`의 서브클래스를 리턴하는 커스터마이징된 `UserDetetailsService`를 구현한 서비스를 위한 `@WithUserDetails`를 제공합니다.
- `@WithUserDetails`는 `UserDetetailsService`의 loadUserByUsername() 메서드를 실제로 호출하여 리턴된 인증객체를 `SecurityContext`에 저장하도록 동작합니다.
  - `@BeforeAll` 블록에 `UserRepository`를 사용하여 test용 유저 객체를 저장하였습니다.
  - loadUserByUsername()에서 db에 조회할 때 사용하는 id값을 `@WithUserDetails`(value = emailOfExistsUser) 로 전달하여 가져오도록 하였습니다. 
- db관련 빈들도 컨테이너에 올렸어야 했으므로 `@SpringBootTest`를 사용하였습니다. 
  - 컨트롤러 테스트인 `AuthControllerTest`를 보시면 필요한 빈들만 올리는 슬라이싱 테스트로 효율적인 테스트 진행하였습니다.
    ```java
    @WebMvcTest(
        value = AuthController.class,
        includeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {HttpUtils.class, SecurityConfig.class, UserMapper.class})
        }
    )
    class AuthControllerTest {
    // ...
    }
    ```
<br/>

## docker-compose를 이용한 기동 가이드
### 1. 기동하기
<img src="gif/docker-compose.gif">

```
docker-compose up -d --scale application=3
```

### 2. 컨테이너 중지
```
docker-compose down -v
```
<br/>

## API 명세 및 시연 영상
[swagger 문서](http://3.25.239.122/swagger-ui/index.html)

### 1. Sign-Up
<img src="gif/sign-up.gif">

#### request 
`POST /api/auth/sign-up`
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
<img src="./gif/login.gif">

#### request 
`POST /api/login`
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
<img src="./gif/post-get.gif">

#### request 
`GET /api/post?id=[post-id]`
```http
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
<img src="./gif/post-page-get.gif">

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
<img src="./gif/post-post.gif">

#### request 
`POST /api/post`
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
<img src="./gif/post-put.gif">

#### request 
`PUT /api/post?id=[post-id]`
```http
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
<img src="./gif/post-delete.gif">

#### request 
`DELETE /api/post?id=[post-id]`
```http
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

## Test result
### 각각의 컴포넌트들에 대한 단위 테스트 (service, provider, handler)
### 서비스 환경에서 컨트롤러 테스트 (controller with authentication)
### 로그인 테스트 (filter)

<img src="img/test-result.png">

모두 성공하였습니다
