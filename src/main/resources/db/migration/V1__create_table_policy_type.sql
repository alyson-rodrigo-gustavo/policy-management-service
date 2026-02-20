CREATE SEQUENCE policy_type_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE policy_type (
   id BIGINT PRIMARY KEY DEFAULT NEXTVAL('policy_type_seq'),
   name VARCHAR(50) NOT NULL,
   CONSTRAINT uq_policy_type_name UNIQUE (name)
);