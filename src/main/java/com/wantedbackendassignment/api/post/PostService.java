package com.wantedbackendassignment.api.post;

import com.wantedbackendassignment.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;

    @Override
    public Post create(Post newPost, User author) {
        newPost.setAuthor(author);

        return postRepository.save(newPost);
    }

    @Override
    public Page<Post> getPosts(PageRequest pageRequest) {
        return postRepository.findAll(pageRequest);
    }

    @Override
    public Post getPost(Long id) {
        return postRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("No post"));
    }

    @Override
    public Post save(Post updatedPost) {
        return postRepository.save(updatedPost);
    }
}
