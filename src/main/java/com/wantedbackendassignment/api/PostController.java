package com.wantedbackendassignment.api;

import com.wantedbackendassignment.api.dto.PostInfoDto;
import com.wantedbackendassignment.api.dto.PostUpdateDto;
import com.wantedbackendassignment.api.post.IPostService;
import com.wantedbackendassignment.api.post.Post;
import com.wantedbackendassignment.api.post.PostMapper;
import com.wantedbackendassignment.api.post.PostPropertyEditor;
import com.wantedbackendassignment.api.user.User;
import com.wantedbackendassignment.api.utils.HttpUtils;
import jakarta.inject.Provider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostMapper postMapper;
    private final IPostService postService;
    private final HttpUtils httpUtils;
    private final Provider<PostPropertyEditor> postPropertyEditorProvider;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Post.class, postPropertyEditorProvider.get());
    }

    @PostMapping("")
    public ResponseEntity create(final @Valid @RequestBody PostUpdateDto postUpdateDto, @AuthenticationPrincipal User currentUser) {
        HttpStatus created = HttpStatus.CREATED;

        Post newPost = postMapper.toPost(postUpdateDto);
        postService.create(newPost, currentUser);

        return new ResponseEntity<>(httpUtils.createSuccessResponse("post create success", created.value()), created);
    }

    @GetMapping("")
    public ResponseEntity detail(@RequestParam("id") Post currentPost) {
        HttpStatus ok = HttpStatus.OK;

        PostInfoDto postInfoDto = postMapper.toPostInfoDto(currentPost);

        return new ResponseEntity<>(httpUtils.createSuccessResponse(postInfoDto, ok.value()), ok);
    }

    @GetMapping("/list")
    public ResponseEntity list(@RequestParam Map<String, String> pageRequest) {
        HttpStatus ok = HttpStatus.OK;

        List<PostInfoDto> postInfoDtos = postService.getPosts(getPageRequest(pageRequest)).stream()
                .map(postMapper::toPostInfoDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(httpUtils.createSuccessResponse(postInfoDtos, ok.value()), ok);
    }

    @PutMapping("")
    public ResponseEntity edit(final @Valid @RequestBody PostUpdateDto postUpdateDto, @RequestParam("id") Post currentPost, @AuthenticationPrincipal User currentUser) {
        checkAuthorship(currentPost, currentUser);

        HttpStatus created = HttpStatus.CREATED;

        Post updatedPost = postMapper.updatePost(postUpdateDto, currentPost);
        postService.save(updatedPost);

        return new ResponseEntity<>(httpUtils.createSuccessResponse("edit post success", created.value()), created);
    }

    @DeleteMapping("")
    public ResponseEntity delete(@RequestParam("id") Post currentPost, @AuthenticationPrincipal User currentUser) {
        checkAuthorship(currentPost, currentUser);

        HttpStatus ok = HttpStatus.OK;

        postService.delete(currentPost);

        return new ResponseEntity<>(httpUtils.createSuccessResponse("delete post success", ok.value()), ok);
    }

    private PageRequest getPageRequest(Map<String, String> pageRequest) {
        Integer pageNumber = Integer.parseInt(pageRequest.get("pageNumber"));
        Integer pageSize = Integer.parseInt(pageRequest.get("pageSize"));
        String sortColumn = pageRequest.get("sortColumn");
        String sortOrder = pageRequest.get("sortOrder");

        validatePageRequest(pageNumber, pageSize, sortColumn, sortOrder);

        pageNumber -= 1;

        return PageRequest.of(
                pageNumber, pageSize, Sort.by(sortOrder.equals("ASC") ? Direction.ASC : Direction.DESC, sortColumn)
        );
    }

    private void validatePageRequest(Integer pageNumber, Integer pageSize, String sortColumn, String sortOrder) {
        if (pageNumber == null || sortColumn == null || sortOrder == null || pageSize == null) {
            throw new IllegalArgumentException("Page request field missing");
        }

        if (pageNumber <= 0) {
            throw new IllegalArgumentException("The page number is 1 or higher");
        }

        if (!loadSortColumns().contains(sortColumn)) {
            throw new IllegalArgumentException("Sort column field value is not valid");
        }

        try {
            Direction.valueOf(sortOrder);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Sort order field value is not valid");
        }
    }

    private List<String> loadSortColumns() {
        return List.of("title", "createdDate");
    }

    private void checkAuthorship(Post currentPost, User currentUser) {
        if (!currentPost.isAuthor(currentUser)) {
            throw new IllegalStateException("Only the author can edit the post");
        }
    }
}
