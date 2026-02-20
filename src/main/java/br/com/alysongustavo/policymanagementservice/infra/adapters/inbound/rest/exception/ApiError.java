package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.exception;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId,
        Map<String, Object> details
){
    public static ApiError of(int status, String code, String message, String path, String traceId, Map<String, Object> details) {
        return new ApiError(Instant.now(), status, code, message, path, traceId, details);
    }
}
