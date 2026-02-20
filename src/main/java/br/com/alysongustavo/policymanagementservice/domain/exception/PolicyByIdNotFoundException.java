package br.com.alysongustavo.policymanagementservice.domain.exception;

public class PolicyByIdNotFoundException extends BusinessException {

    public PolicyByIdNotFoundException(Long id)
    {
        super("POLICY_NOT_FOUND", id);
    }
}
