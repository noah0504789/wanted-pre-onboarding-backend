package com.wantedbackendassignment.api.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.dto.PostUpdateDto;
import com.wantedbackendassignment.api.user.User;
import com.wantedbackendassignment.api.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.wantedbackendassignment.api.PostUtils.createPost;
import static com.wantedbackendassignment.api.PostUtils.createPostUpdateDto;
import static com.wantedbackendassignment.api.UserUtils.createUser;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private PostService postService;

    String postCreateUrl = "/api/post";
    String postInfoUrl = "/api/post";
    String postListUrl = postInfoUrl + "/list";
    String postEditUrl = "/api/post";
    String postDeleteUrl = "/api/post";

    final String emailOfExistsUser = "test@wanted.com";
    final String passwordOfExistsUser = "12345678";

    User author;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx)
                .alwaysDo(print())
                .build();

        author = createUser(emailOfExistsUser, passwordOfExistsUser);

        userRepository.save(author);
    }

    @Test
    @DisplayName("create() 성공 : 유효한 값으로 게시글 생성 요청")
    void create_success() throws Exception {
        String title = "title";
        String description = "description";
        PostUpdateDto dummyPostUpdateDto = createPostUpdateDto(title, description);
        Post dummyPost = createPost(title, description);

        when(postService.create(any(Post.class), any(User.class))).thenReturn(dummyPost);

        mvc.perform(post(postCreateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dummyPostUpdateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("responseCode", is(201)))
                .andExpect(jsonPath("data", is("post create success")))
                .andReturn();
    }

    @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}, {1}, {2}")
    @MethodSource("invalidInputForCreatingPost")
    @DisplayName("create() 실패 : 유효하지 않은 값으로 게시글 생성 요청")
    void create_failure_input_invalid(String invalidTitle, String invalidDescription) throws Exception {
        PostUpdateDto dummyPostUpdateDto = createPostUpdateDto(invalidTitle, invalidDescription);

        mvc.perform(post(postCreateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dummyPostUpdateDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(jsonPath("responseCode", is(400)))
                .andExpect(jsonPath("$.error.errors[0].objectName", is("postUpdateDto")))
                .andExpect(jsonPath("$.error.errors[0].field", either(is("title")).or(is("description"))))
                .andReturn();
    }

    @Test
    @DisplayName("detail() 성공 : 유효한 값으로 게시글 조회 요청")
    void detail_success() throws Exception {
        Long existsPostId = 1L;
        String title = "title";
        String description = "description";
        Post existsPost = createPost(title, description);

        when(postService.getPost(anyLong())).thenReturn(existsPost);

        mvc.perform(get(postInfoUrl)
                        .param("id", String.valueOf(existsPostId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("responseCode", is(200)))
                .andExpect(jsonPath("$.data.title", is(title)))
                .andExpect(jsonPath("$.data.description", is(description)))
                .andReturn();
    }

    @Test
    @DisplayName("list() 성공 : 유효한 값으로 게시글 페이지 조회 요청")
    void list_success() throws Exception {
        Integer pageNumber = 1;
        Integer pageSize = 5;
        String sortColumn = "createdDate";
        String sortOrder = "DESC";

        String title = "title";
        String description = "description";

        List<Post> posts = Arrays.asList(
                createPost(title, description),
                createPost(title + "1", description + "1")
        );

        Page<Post> postPage = new PageImpl<>(posts);

        when(postService.getPosts(any(PageRequest.class))).thenReturn(postPage);

        mvc.perform(get(postListUrl)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sortColumn", sortColumn)
                        .param("sortOrder", sortOrder))
                .andExpect(status().isOk())
                .andExpect(jsonPath("responseCode", is(200)))
                .andExpect(jsonPath("$.data[0].title", either(is(title)).or(is(title+1))))
                .andExpect(jsonPath("$.data[0].description", either(is(description)).or(is(description+1))))
                .andReturn();
    }

    @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}, {1}, {2}")
    @MethodSource("invalidInputForPageRequest")
    @DisplayName("list() 실패 : 유효하지 않은 값으로 게시글 페이지 조회 요청")
    void list_failure_queryString_invalid(Integer pageNumber, Integer pageSize, String sortColumn, String sortOrder) throws Exception {
        String title = "title";
        String description = "description";

        List<Post> posts = Arrays.asList(
                createPost(title, description),
                createPost(title + "1", description + "1")
        );

        Page<Post> postPage = new PageImpl<>(posts);

        when(postService.getPosts(any(PageRequest.class))).thenReturn(postPage);

        mvc.perform(get(postListUrl)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sortColumn", sortColumn)
                        .param("sortOrder", sortOrder))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("responseCode", is(400)))
                .andExpect(jsonPath("error",
                        either(is("The page number is 1 or higher"))
                           .or(is("Page request field missing"))
                           .or(is("Sort column field value is not valid"))
                           .or(is("Sort order field value is not valid"))
                           .or(containsString("For input string"))
                ))
                .andReturn();
    }

    @Test
    @WithUserDetails(value = emailOfExistsUser, userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("edit() 성공 : 유효한 값으로 게시글 수정 요청")
    void edit_success() throws Exception {
        Long existsPostId = 1L;
        String title = "title";
        String description = "description";

        String updatedTitle = "update" + title;
        String updatedDescription = "update" + description;
        PostUpdateDto postUpdateDto = createPostUpdateDto(updatedTitle, updatedDescription);

        Post updatedPost = createPost(updatedTitle, updatedDescription, author);
        Post existsPost = createPost(title, description, author);

        when(postService.getPost(anyLong())).thenReturn(existsPost);
        when(postService.create(any(Post.class), any(User.class))).thenReturn(updatedPost);

        mvc.perform(put(postEditUrl)
                        .param("id", String.valueOf(existsPostId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postUpdateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("responseCode", is(201)))
                .andExpect(jsonPath("data", is("edit post success")))
                .andReturn();
    }

    @ParameterizedTest(name = "#{index} - {displayName} = Test with Argument={0}, {1}, {2}")
    @MethodSource("invalidInputForUpdatingPost")
    @DisplayName("edit() 실패 : 유효하지 않은 값으로 게시글 수정 요청")
    void edit_failure_invalid_input_value(String invalidTitle, String invalidDescription) throws Exception {
        Long existsPostId = 1L;

        PostUpdateDto postUpdateDto = createPostUpdateDto(invalidTitle, invalidDescription);

        mvc.perform(put(postEditUrl)
                        .param("id", String.valueOf(existsPostId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postUpdateDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("responseCode", is(400)))
                .andExpect(jsonPath("$.error.errors[0].objectName", is("postUpdateDto")))
                .andExpect(jsonPath("$.error.errors[0].field", either(is("title")).or(is("description"))))
                .andReturn();
    }

    @Test
    @WithUserDetails(value = emailOfExistsUser, userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("edit() 실패 : 유효하지 않은 조회중인 게시글 ID를 전달하고 게시글 수정 요청")
    void edit_failure_invalid_queryString_value() throws Exception {
        Long existsPostId = 1L;
        Long nonExistsPostId = 10L;

        String title = "title";
        String description = "description";
        Post existsPost = createPost(title, description, author);

        when(postService.getPost(existsPostId)).thenReturn(existsPost);
        when(postService.save(any(Post.class))).thenReturn(existsPost);;

        String updatedTitle = "update" + title;
        String updatedDescription = "update" + description;
        PostUpdateDto postUpdateDto = createPostUpdateDto(updatedTitle, updatedDescription);

        mvc.perform(put(postEditUrl)
                        .param("id", String.valueOf(nonExistsPostId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postUpdateDto)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("responseCode", is(400)))
                .andExpect(jsonPath("error", is("Required request parameter 'id' for method parameter type Post is present but converted to null")))
                .andReturn();
    }

    @Test
    @WithUserDetails(value = emailOfExistsUser, userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("delete() 성공 : 유효한 값으로 게시글 삭제 요청")
    void delete_success() throws Exception {
        Long existsPostId = 1L;
        String title = "title";
        String description = "description";

        Post existsPost = createPost(title, description, author);

        when(postService.getPost(anyLong())).thenReturn(existsPost);
        doNothing().when(postService).delete(any(Post.class));

        mvc.perform(delete(postDeleteUrl)
                        .param("id", String.valueOf(existsPostId))
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("responseCode", is(200)))
                .andExpect(jsonPath("data", is("delete post success")))
                .andReturn();
    }

    @Test
    @WithUserDetails(value = emailOfExistsUser, userDetailsServiceBeanName = "userService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("delete() 실패 : 유효하지 않은 조회중인 게시글 ID를 전달하고 게시글 수정 요청")
    void delete_failure_invalid_queryString_value() throws Exception {
        Long existsPostId = 1L;
        Long nonExistsPostId = 10L;

        String title = "title";
        String description = "description";

        Post existsPost = createPost(title, description, author);
        when(postService.getPost(existsPostId)).thenReturn(existsPost);

        doAnswer(invo -> {
            return null;
        }).when(postService).delete(any(Post.class));

        mvc.perform(delete(postEditUrl)
                        .param("id", String.valueOf(nonExistsPostId))
                    )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("responseCode", is(400)))
                .andExpect(jsonPath("error", is("Required request parameter 'id' for method parameter type Post is present but converted to null")))
                .andReturn();
    }

    static Stream<Arguments> invalidInputForCreatingPost() {
        String invalidTitleValue = "more than 100 characters".repeat(6);
        String invalidDescriptionValue = "more than 500 characters".repeat(26);

        return Stream.of(
                arguments(invalidTitleValue, "description"),
                arguments("title", invalidDescriptionValue),
                arguments(invalidTitleValue, invalidDescriptionValue)
        );
    }

    static Stream<Arguments> invalidInputForUpdatingPost() {
        String invalidTitleValue = "more than 100 characters".repeat(6);
        String invalidDescriptionValue = "more than 500 characters".repeat(26);

        return Stream.of(
                arguments(invalidTitleValue, "description"),
                arguments("title", invalidDescriptionValue),
                arguments(invalidTitleValue, invalidDescriptionValue)
        );
    }

    static Stream<Arguments> invalidInputForPageRequest() {
        Integer invalidPageNumber = 0;
        Integer invalidPageSize = null;
        String invalidSortColumn = "notExistsColumn";
        String invalidSortOrder = "NO DIRECT VALUE";

        return Stream.of(
                arguments(invalidPageNumber, 1, "createdDate", "ASC"),
                arguments(1, invalidPageSize, "createdDate", "ASC"),
                arguments(1, 1, invalidSortColumn, "ASC"),
                arguments(1, 1, "createdDate", invalidSortOrder),
                arguments(invalidPageNumber, invalidPageSize, invalidSortColumn, invalidSortOrder)
        );
    }
}
