package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateOrUpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.group.GroupService;

@Tag(name = "Group controller")
@Validated
@RestController
@RequestMapping(path = "v1/services/groups")
public class GroupController extends AbstractController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "Create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupApi> create(@Valid @RequestBody CreateOrUpdateGroupRequest request)
            throws GenericException {
        return new ResponseEntity<>(groupService.create(getUser(), request), CREATED);
    }

    @Operation(summary = "Get by ID")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetGroupResponse> get(@PathVariable(name = "id") Long id)
            throws GenericException {
        return ResponseEntity.ok(groupService.get(getUser(), id));
    }

    @Operation(summary = "Get by slug")
    @GetMapping(path = "/{slug}/slug", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetGroupResponse> getBySlug(@PathVariable(name = "slug") String slug)
            throws GenericException {
        return ResponseEntity.ok(groupService.getBySlug(getUser(), slug));
    }

    @Operation(summary = "Update")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupApi> update(
            @NotNull @PathVariable(name = "id") Long id,
            @Valid @RequestBody CreateOrUpdateGroupRequest request
    ) throws GenericException {
        return ResponseEntity.ok(groupService.update(getUser(), id, request));
    }

    @Operation(summary = "Get user's groups")
    @GetMapping(path = "/{username}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupApi>> getAllUserGroups(@PathVariable(name = "username") String username)
            throws GenericException {
        validateUser(username);
        return ResponseEntity.ok(groupService.getAllUserGroups(username));
    }

    @Operation(summary = "Update membership")
    @PatchMapping(path = "/{id}/update-membership", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMembership(
            @PathVariable("id") Long id,
            @RequestBody UserGroupApi request
    ) throws GenericException {
        groupService.updateMembership(getUser(), id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get by filters")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupApi>> getByFilters(
            @RequestParam("name") String name,
            @RequestParam("categoryId") Long categoryId
    ) throws GenericException {
        return ResponseEntity.ok(groupService.getByFilters(name, categoryId));
    }

    @Operation(summary = "Join group")
    @PostMapping(path = "{id}/join")
    public ResponseEntity<Void> jointToGroup(@PathVariable("id") Long id)
            throws GenericException, MessagingException {
        groupService.join(getUser(), id);
        return ResponseEntity.noContent().build();
    }

}
