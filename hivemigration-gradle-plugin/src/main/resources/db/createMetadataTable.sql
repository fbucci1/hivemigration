-- This script is the template for creating metadata table
-- Sample values: https://flywaydb.org/getstarted/how
--    seq: Timestamp when script started executing (System.currentTimeMillis())
--    version: Database version. Retrieved from file name. 0 for R files.
--    script: name of the script file.
--    installed_by: user.
--    installed_on: timestamp when script started being executed.
--    execution_time: milliseconds ellapsed for the execution of the script.
--    success: 1 ok, 0 error.

CREATE TABLE ${schema}.${table} (
    seq BIGINT,
    version INT, 
    script VARCHAR(1000),
    installed_by VARCHAR(100),
    installed_on VARCHAR(23),
    execution_time BIGINT,
    success DECIMAL(1)
);

--ALTER TABLE ${schema}.${table} ADD CONSTRAINT ${table}_pk PRIMARY KEY (installed_rank);

--CREATE INDEX ${schema}.${table}_s_idx ON ${schema}.${table} ("success");

