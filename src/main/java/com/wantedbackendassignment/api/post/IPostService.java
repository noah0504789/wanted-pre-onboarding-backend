package com.wantedbackendassignment.api.post;

import com.wantedbackendassignment.api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IPostService {
    Post create(Post newPost, User author);

    Page<Post> getPosts(PageRequest pageRequest);

    Post getPost(Long id);

    Post save(Post updated);

    void delete(Post post);
}
