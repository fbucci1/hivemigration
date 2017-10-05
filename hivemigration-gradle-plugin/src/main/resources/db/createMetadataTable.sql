-- This script is the template for creating metadata table
-- Sample values: https://flywaydb.org/getstarted/how
--    installed_rank: integer sequence
--    version: Clear retrieved from file name, 0 for R files.
--    description: Clear text retrieved from file name.
--    type: "SQL".
--    script: name of the script file. "res:" is used as a prefix for resources.
--    checksum: MD5 of the file content.
--    installed_by: db user.
--    installed_on: timestamp script started being executed.
--    execution_time: milliseconds ellapsed for the execution of the script.
--    success: 1 ok, 0 error.

CREATE TABLE ${schema}.${table} (
    installed_rank INT,
    version INT, 
    script VARCHAR(1000),
    installed_by VARCHAR(100),
    installed_on TIMESTAMP,
    execution_time INT,
    success DECIMAL(1)
);

--ALTER TABLE ${schema}.${table} ADD CONSTRAINT ${table}_pk PRIMARY KEY (installed_rank);

--CREATE INDEX ${schema}.${table}_s_idx ON ${schema}.${table} ("success");

