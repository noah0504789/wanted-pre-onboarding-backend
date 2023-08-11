package com.wantedbackendassignment.api;

import com.wantedbackendassignment.api.dto.PostInfoDto;
import com.wantedbackendassignment.api.dto.PostUpdateDto;
import com.wantedbackendassignment.api.post.IPostService;
import com.wantedbackendassignment.api.utils.HttpUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;
    private final HttpUtils httpUtils;

    @PostMapping("") // TODO : ArgumentResolver 이용해서 User 인증객체 받기
    public ResponseEntity create(final @Valid @RequestBody PostUpdateDto postUpdateDto) {
        HttpStatus created = HttpStatus.CREATED;

        // TODO : User 인증객체 주입
        // postService.create(postUpdateDto, user);

        return new ResponseEntity<>(
                httpUtils.createSuccessResponse("create success", created.value()),
                created
        );
    }

    @GetMapping("/list")
    public ResponseEntity postList(@RequestParam Map<String, String> pageRequest) {
        HttpStatus ok = HttpStatus.OK;

        int pageNumber = Integer.parseInt(pageRequest.get("pageNumber"));
        int pageSize = Integer.parseInt(pageRequest.get("pageSize"));
        String sortColumn = pageRequest.get("sortColumn");
        String sortOrder = pageRequest.get("sortOrder");

        PageRequest pageReq = PageRequest.of(
            pageNumber,
            pageSize,
            Sort.by(
                sortOrder.equals("ASC") ? Direction.ASC : Direction.DESC,
                sortColumn
            )
        );

        List<PostInfoDto> postInfoDtos = postService.getPostDtos(pageReq);

        return new ResponseEntity<>(
                httpUtils.createSuccessResponse(postInfoDtos, ok.value()),
                ok
        );
    }
}
