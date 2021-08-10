package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import usociety.manager.app.api.MessageApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.message.MessageService;

@Tag(name = "Message controller")
@Validated
@RestController
@RequestMapping(path = "v1/services/messages")
public class MessageController extends AbstractController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Send to group")
    @PostMapping(path = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendGroupMessage(
            @NotNull @PathVariable(value = "groupId") Long groupId,
            @Valid @RequestBody MessageApi message
    ) throws GenericException {
        messageService.sendGroupMessage(getUser(), groupId, message);
        return new ResponseEntity<>(CREATED);
    }

    @Operation(summary = "Get all by group")
    @GetMapping(path = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessageApi>> getByFilter(
            @PathVariable("groupId") Long groupId,
            Pageable pageable
    ) throws GenericException {
        return ResponseEntity.ok(messageService.getGroupMessages(getUser(), groupId, pageable));
    }

}
