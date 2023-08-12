package com.wantedbackendassignment.api.post;

import com.wantedbackendassignment.api.PostController;
import com.wantedbackendassignment.api.dto.PostUpdateDto;
import com.wantedbackendassignment.api.dto.PostInfoDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = PostController.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PostMapper {

    Post toPost(PostUpdateDto postUpdateDto);

    @Mapping(source = "post.author.email", target = "authorEmail")
    PostInfoDto toPostInfoDto(Post post);

    @Mapping(source = "postUpdateDto.title", target = "title")
    @Mapping(source = "postUpdateDto.description", target = "description")
    Post updatePost(PostUpdateDto postUpdateDto, @MappingTarget Post post);
}
