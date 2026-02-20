package br.com.alysongustavo.policymanagementservice.domain.exception;

public class PolicyExistException extends BusinessException {

    public PolicyExistException(String document)
    {
        super("POLICY_ALREADY_EXISTS", document);
    }
}