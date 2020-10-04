package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.group.GroupService;

@Validated
@RestController
@RequestMapping(path = "services/groups")
public class GroupController extends CommonController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @ApiOperation(value = "Create.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Group data."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupApi> create(@Valid @RequestPart("group") CreateGroupRequest request,
                                           @RequestPart("photo") MultipartFile photo)
            throws GenericException {
        return new ResponseEntity(groupService.create(getUser(), request, photo), CREATED);
    }

    @ApiOperation(value = "Get.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Group data."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetGroupResponse> get(@PathVariable(name = "id") Long id)
            throws GenericException {
        return ResponseEntity.ok(groupService.get(id, getUser()));
    }

    @ApiOperation(value = "Get user groups.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Group list in where user is member."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/{username}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupApi>> getAllUserGroups(@PathVariable(name = "username") String username)
            throws GenericException {
        validateUser(username);
        return ResponseEntity.ok(groupService.getAllUserGroups(username));
    }

    @ApiOperation(value = "Update membership data.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Membership data updated."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PutMapping(path = "/update-membership",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMembership(@RequestBody UserGroupApi request)
            throws GenericException {
        groupService.updateMembership(request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Update.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Group data updated."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@Valid @RequestBody UpdateGroupRequest request)
            throws GenericException, JsonProcessingException {
        groupService.update(request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Get by filters.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Group data updated."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/by-filters", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupApi>> getByFilters(@RequestParam("name") String name,
                                                       @RequestParam("categoryId") Long categoryId)
            throws GenericException {
        return ResponseEntity.ok(groupService.getByFilters(name, categoryId));
    }

    @ApiOperation(value = "Join to group.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Join to group request sent."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "{id}/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupApi>> jointToGroup(@PathVariable("id") Long id)
            throws GenericException {
        groupService.join(id, getUser());
        return ResponseEntity.ok().build();
    }

}
