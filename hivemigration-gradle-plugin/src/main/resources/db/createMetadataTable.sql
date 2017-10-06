--
-- Copyright 2017 Fernando Raul Bucci
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

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
    seq DECIMAL(20),
    version INT, 
    script VARCHAR(1000),
    installed_by VARCHAR(100),
    installed_on VARCHAR(20),
    execution_time DECIMAL(20),
    success DECIMAL(1)
);

--ALTER TABLE ${schema}.${table} ADD CONSTRAINT ${table}_pk PRIMARY KEY (installed_rank);

--CREATE INDEX ${schema}.${table}_s_idx ON ${schema}.${table} ("success");

