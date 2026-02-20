INSERT INTO policy_type (name)
SELECT 'AUTO'
WHERE NOT EXISTS (
    SELECT 1 FROM policy_type WHERE name = 'AUTO'
);

INSERT INTO policy_type (name)
SELECT 'HOME'
WHERE NOT EXISTS (
    SELECT 1 FROM policy_type WHERE name = 'HOME'
);

INSERT INTO policy_type (name)
SELECT 'LIFE'
WHERE NOT EXISTS (
    SELECT 1 FROM policy_type WHERE name = 'LIFE'
);

INSERT INTO policy_type (name)
SELECT 'HEALTH'
WHERE NOT EXISTS (
    SELECT 1 FROM policy_type WHERE name = 'HEALTH'
);

INSERT INTO policy_type (name)
SELECT 'TRAVEL'
WHERE NOT EXISTS (
    SELECT 1 FROM policy_type WHERE name = 'TRAVEL'
);
