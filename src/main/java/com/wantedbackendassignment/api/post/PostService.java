package com.wantedbackendassignment.api.post;

import com.wantedbackendassignment.api.dto.PostInfoDto;
import com.wantedbackendassignment.api.dto.PostUpdateDto;
import com.wantedbackendassignment.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;

    @Override
    public Post create(PostUpdateDto postUpdateDto, User author) {
        Post newPost = postMapper.toPost(postUpdateDto);
        newPost.setAuthor(author);

        return postRepository.save(newPost);
    }

    @Override
    public List<PostInfoDto> getPostDtos(PageRequest pageRequest) {
        List<Post> withPagination = postRepository.findWithPagination(pageRequest);

        return postMapper.toDtoList(withPagination);
    }
}
