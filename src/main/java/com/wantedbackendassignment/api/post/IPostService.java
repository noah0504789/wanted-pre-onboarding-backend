package com.wantedbackendassignment.api.post;

import com.wantedbackendassignment.api.dto.PostInfoDto;
import com.wantedbackendassignment.api.dto.PostUpdateDto;
import com.wantedbackendassignment.api.user.User;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IPostService {
    Post create(PostUpdateDto postUpdateDto, User author);

    List<PostInfoDto> getPostDtos(PageRequest pageRequest);
}
