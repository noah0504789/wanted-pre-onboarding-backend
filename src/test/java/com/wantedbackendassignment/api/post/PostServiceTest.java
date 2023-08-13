package com.wantedbackendassignment.api.post;

import com.wantedbackendassignment.api.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.wantedbackendassignment.api.PostUtils.createPost;
import static com.wantedbackendassignment.api.UserUtils.createUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("create() - 성공 : 게시글 생성")
    void create_success() {
        User author = createUser("wanted@test.com", "12345678");
        Post dummyPost = createPost("title", "description");

        when(postRepository.save(any(Post.class))).thenReturn(dummyPost);

        postService.create(dummyPost, author);

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("getPost() - 성공 : 게시글 가져오기")
    void getPost_success() {
        Long dummyPostId = 1L;
        Post dummyPost = createPost("title", "description");

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(dummyPost));

        postService.getPost(dummyPostId);

        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("getPost() - 성공 : 페이지 게시글 리스트 가져오기")
    void getPosts_success() {
        Post dummyPost = createPost("title", "description");
        Post dummyPost2 = createPost("title2", "description2");
        Post dummyPost3 = createPost("title3", "description3");

        List<Post> postList = Arrays.asList(dummyPost, dummyPost2, dummyPost3);
        Page<Post> posts = new PageImpl<>(postList);

        when(postRepository.findAll(any(PageRequest.class))).thenReturn(posts);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "title");
        postService.getPosts(pageRequest);

        verify(postRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("save() - 성공 : 페이지 게시글 DB 저장")
    void save_success() {
        Post dummyPost = createPost("saved title", "saved description");

        when(postRepository.save(any(Post.class))).thenReturn(dummyPost);

        postService.save(dummyPost);

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("save() - 성공 : 페이지 게시글 DB 삭제")
    void delete_success() {
        Post dummyPost = createPost("deleted title", "deleted description");

        postRepository.delete(dummyPost);

        verify(postRepository, times(1)).delete(any(Post.class));
    }
}
