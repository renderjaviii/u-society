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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.PostApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.post.PostService;

@CrossOrigin(origins = "*", maxAge = 86400)
@Validated
@RestController
@RequestMapping(path = "services/posts")
public class PostController extends AbstractController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ApiOperation(value = "Create post.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Post created."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostApi> sendGroupMessage(@Valid @RequestBody CreatePostRequest request)
            throws GenericException {
        return new ResponseEntity<>(postService.create(getUser(), request), CREATED);
    }

    @ApiOperation(value = "Get all posts by group.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Posts."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/{groupId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PostApi>> getAllByGroup(
            @PathVariable("groupId") Long groupId,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) throws GenericException {
        return ResponseEntity.ok(postService.getAllByUserAndGroup(getUser(), groupId, page));
    }

    @ApiOperation(value = "React into a post.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Post react saved."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/{id}/react", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> react(
            @PathVariable("id") Long id,
            @NotNull @RequestParam("value") ReactTypeEnum value
    ) throws GenericException {
        postService.react(getUser(), id, value);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Comment into to a post.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Post comment saved."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/{id}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> comment(
            @PathVariable("id") Long id,
            @RequestBody CommentPostRequest request
    ) throws GenericException {
        postService.comment(getUser(), id, request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Vote in survey.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Survey vote saved."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/{id}/vote")
    public ResponseEntity<Void> interactWithSurvey(
            @PathVariable("id") Long id,
            @PositiveOrZero @RequestParam("vote") Integer vote
    )
            throws GenericException {
        postService.vote(getUser(), id, vote);
        return ResponseEntity.ok().build();
    }

}
