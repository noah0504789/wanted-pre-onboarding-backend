package com.wantedbackendassignment.api;

import com.wantedbackendassignment.api.dto.PostInfoDto;
import com.wantedbackendassignment.api.dto.PostUpdateDto;
import com.wantedbackendassignment.api.post.Post;
import com.wantedbackendassignment.api.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PostUtils {
    public static Post createPost(String title, String description, User author) {
        Post dummyPost = createPost(title, description);
        dummyPost.setAuthor(author);
        return dummyPost;
    }

    public static Post createPost(String title, String description) {
        return Post.builder().title(title).description(description).build();
    }

    public static PostUpdateDto createPostUpdateDto(String title, String description) {
        return new PostUpdateDto(title, description);
    }

    public static PostInfoDto createPostInfoDto(Long id, String title, String description, String authorEmail) {
        return new PostInfoDto(id, title, description, authorEmail);
    }

    public static PostInfoDto createPostInfoDto(String title, String description) {
        return new PostInfoDto(null, title, description, null);
    }

    public static PageRequest createPageRequest(Integer pageNumber, Integer pageSize, String sortColumn, String sortOrder) {
        return PageRequest.of(
                pageNumber, pageSize, Sort.by(sortOrder.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortColumn)
        );
    }
}
