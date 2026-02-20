CREATE SEQUENCE policy_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE policy (
     id BIGINT PRIMARY KEY DEFAULT NEXTVAL('policy_seq'),
     document VARCHAR(50) NOT NULL,
     premium_value DECIMAL(10, 2) NOT NULL,
     coverage_value DECIMAL(10, 2) NOT NULL,
     policy_type_id BIGINT NOT NULL,

     CONSTRAINT fk_policy_policy_type
         FOREIGN KEY (policy_type_id)
             REFERENCES policy_type(id)
                ON DELETE RESTRICT
);

CREATE INDEX idx_policy_document
    ON policy(document);

CREATE INDEX idx_policy_policy_type_id
    ON policy(policy_type_id);

CREATE INDEX idx_policy_document_type
    ON policy(document, policy_type_id);

CREATE UNIQUE INDEX uq_policy_document_type
    ON policy(document, policy_type_id);