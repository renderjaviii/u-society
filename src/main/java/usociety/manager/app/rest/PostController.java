package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import usociety.manager.app.api.PostApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.post.PostService;

@Tag(name = "Post controller")
@Validated
@RestController
@RequestMapping(path = "v1/services/posts")
public class PostController extends AbstractController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Send", responses = @ApiResponse(responseCode = "201"))
    @PostMapping(path = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostApi> sendGroupMessage(
            @NotNull @PathVariable(value = "groupId") Long groupId,
            @Valid @RequestBody CreatePostRequest request
    ) throws GenericException {
        return new ResponseEntity<>(postService.create(getUser(), groupId, request), CREATED);
    }

    @Operation(summary = "Get all by group", responses = @ApiResponse(responseCode = "200"))
    @GetMapping(path = "/{groupId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PostApi>> getAllByGroup(
            @NotNull @PathVariable("groupId") Long groupId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) throws GenericException {
        return ResponseEntity.ok(postService.getAllByUserAndGroup(getUser(), groupId, page, pageSize));
    }

    @Operation(summary = "React into a post", responses = @ApiResponse(responseCode = "204"))
    @PostMapping(path = "/{id}/react", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> react(
            @NotNull @PathVariable("id") Long id,
            @NotNull @RequestParam("value") ReactTypeEnum value
    ) throws GenericException {
        postService.react(getUser(), id, value);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Comment into a post", responses = @ApiResponse(responseCode = "204"))
    @PostMapping(path = "/{id}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> comment(
            @NotNull @PathVariable("id") Long id,
            @RequestBody CommentPostRequest request
    ) throws GenericException {
        postService.comment(getUser(), id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vote in survey", responses = @ApiResponse(responseCode = "204"))
    @PostMapping(path = "/{id}/vote")
    public ResponseEntity<Void> interactWithSurvey(
            @NotNull @PathVariable("id") Long id,
            @NotNull @PositiveOrZero @RequestParam("option") Integer vote
    ) throws GenericException {
        postService.vote(getUser(), id, vote);
        return ResponseEntity.noContent().build();
    }

}
