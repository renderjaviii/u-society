package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.MessageApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.message.MessageService;

@CrossOrigin(origins = "*", maxAge = 86400)
@Validated
@RestController
@RequestMapping(path = "services/messages")
public class MessageController extends CommonController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @ApiOperation(value = "Send group message.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Message sent."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Void> sendGroupMessage(@Valid @RequestBody MessageApi request)
            throws GenericException {
        messageService.sendGroupMessage(getUser(), request);
        return new ResponseEntity<>(CREATED);
    }

    @ApiOperation(value = "Get group messages.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Group messages."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessageApi>> getByFilter(@PathVariable("groupId") Long groupId)
            throws GenericException {
        return ResponseEntity.ok(messageService.getGroupMessages(getUser(), groupId));
    }

}
