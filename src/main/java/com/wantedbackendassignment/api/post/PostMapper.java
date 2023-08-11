package com.wantedbackendassignment.api.post;

import com.wantedbackendassignment.api.dto.PostUpdateDto;
import com.wantedbackendassignment.api.dto.PostInfoDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = PostService.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostMapper {
    Post toPost(PostUpdateDto postUpdateDto);

    List<PostInfoDto> toDtoList(List<Post> postList);
}
