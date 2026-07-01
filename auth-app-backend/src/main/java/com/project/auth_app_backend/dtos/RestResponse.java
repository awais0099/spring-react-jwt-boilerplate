package com.project.auth_app_backend.dtos;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Global Uniform REST API Response Wrapper for Successful Operations")
@JsonPropertyOrder({ "success", "statusCode", "message", "timestamp", "data" })
public class RestResponse<T> {

    @Schema(description = "Explicit boolean flag marking application execution completion status", example = "true")
    private final boolean success;

    @Schema(description = "HTTP Status code integer value mapping", example = "200")
    private final int statusCode;

    @Schema(description = "Human-readable descriptive status message intended for frontend UI alerts", example = "Operation completed successfully.")
    private final String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @Schema(description = "Timestamp instance tracking response packaging moment (ISO 8601)", example = "2026-06-29T10:49:11Z")
    private final Instant timestamp;

    @Schema(description = "Generic contextual data carrier payload block")
    private final T data;

    // Private constructor enforces the use of static factory methods
    private RestResponse(int statusCode, String message, T data) {
        this.success = true; // Always true for successful response envelopes
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = Instant.now();
        this.data = data;
    }

    /**
     * Factory Method: Standard 200 OK Response Wrapper with full payload context.
     */
    public static <T> RestResponse<T> success(String message, T data) {
        return new RestResponse<>(200, message, data);
    }

    /**
     * Factory Method: Standard 201 Created Response Wrapper for provisioning actions.
     */
    public static <T> RestResponse<T> created(String message, T data) {
        return new RestResponse<>(201, message, data);
    }

    /**
     * Factory Method: Action complete with empty/void return requirements (e.g., Deletions).
     */
    public static RestResponse<Void> success(String message) {
        return new RestResponse<>(200, message, null);
    }
}
