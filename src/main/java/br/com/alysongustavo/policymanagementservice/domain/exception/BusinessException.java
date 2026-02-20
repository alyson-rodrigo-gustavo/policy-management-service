package br.com.alysongustavo.policymanagementservice.domain.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final String code;
    private final Object[] args;

    public BusinessException(String code, Object... args) {
        super(code);
        this.code = code;
        this.args = args == null ? new Object[0] : args;
    }

}
