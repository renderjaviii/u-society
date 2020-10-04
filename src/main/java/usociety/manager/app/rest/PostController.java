package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.PostApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.post.PostService;

@Validated
@RestController
@RequestMapping(path = "services/posts")
public class PostController extends CommonController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ApiOperation(value = "Create.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Post created."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostApi> sendGroupMessage(@Valid @RequestBody PostApi request)
            throws GenericException, JsonProcessingException {
        return new ResponseEntity<>(postService.create(getUser(), request), CREATED);
    }

    @ApiOperation(value = "Get all group posts.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Group posts."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/{groupId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PostApi>> getAll(@PathVariable("groupId") Long groupId) throws GenericException {
        return ResponseEntity.ok(postService.getAll(getUser(), groupId));
    }

    @ApiOperation(value = "React to a post.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Post react saved."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/{postId}/react", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> react(@PathVariable("postId") Long postId,
                                      @NotNull @RequestParam("react") Integer react) throws GenericException {
        postService.react(getUser(), postId, react);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Comment into to a post.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Post comment saved."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/{postId}/comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> comment(@PathVariable("postId") Long postId,
                                        @RequestBody CommentPostRequest request) throws GenericException {
        postService.comment(getUser(), postId, request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Vote in survey.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Survey vote saved."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/{postId}/survey", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> interactWithSurvey(@PathVariable("postId") Long postId,
                                                   @NotNull @RequestParam("vote") Integer vote)
            throws GenericException {
        postService.interactWithSurvey(getUser(), postId, vote);
        return ResponseEntity.ok().build();
    }

}
