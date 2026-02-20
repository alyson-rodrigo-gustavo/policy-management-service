package br.com.alysongustavo.policymanagementservice.domain.exception;

public class PolicyNotFoundException extends BusinessException {

    public PolicyNotFoundException(String document)
    {
        super("POLICY_NOT_FOUND_BY_DOCUMENT", document);
    }
}
