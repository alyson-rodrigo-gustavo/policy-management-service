package br.com.alysongustavo.policymanagementservice.domain.exception;

public class PolicyTypeNotFoundException extends BusinessException {

    public PolicyTypeNotFoundException(Long id)
    {
        super("POLICY_TYPE_NOT_FOUND", id);
    }
}
