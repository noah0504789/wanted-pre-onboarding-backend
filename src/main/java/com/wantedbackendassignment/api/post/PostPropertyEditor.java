package com.wantedbackendassignment.api.post;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

@Scope("prototype")
@Component
@RequiredArgsConstructor
public class PostPropertyEditor extends PropertyEditorSupport {

    private final IPostService postService;

    @Override
    public String getAsText() {
        Post post = (Post) getValue();

        return String.valueOf(post.getId());
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Long postId = Long.parseLong(text.trim());
        Post post = postService.getPost(postId);

        setValue(post);
    }
}
