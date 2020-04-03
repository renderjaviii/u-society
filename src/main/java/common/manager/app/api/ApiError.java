package common.manager.app.api;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiError {

    @JsonProperty
    private String description;
    @JsonProperty
    private String statusCode;
    @JsonProperty
    private LocalDateTime timestamp;

    public ApiError(String description, String statusCode) {
        this.description = description;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

}