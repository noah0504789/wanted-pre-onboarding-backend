package com.wantedbackendassignment.api;

import com.wantedbackendassignment.api.post.Post;
import com.wantedbackendassignment.api.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostUtils {
    public static Post createDummyPost(String title, String description, User author) {
        Post dummyPost = createDummyPost(title, description);
        dummyPost.setAuthor(author);
        return dummyPost;
    }

    public static Post createDummyPost(String title, String description) {
        return Post.builder().title(title).description(description).build();
    }
}
