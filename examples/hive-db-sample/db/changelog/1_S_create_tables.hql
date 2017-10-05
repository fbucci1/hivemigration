set hive.variable.substitute=true;

-- check db version first................ select * from EXISTS STAGE_${hiveconf:ENV}.__VERSIONING__ where script='${hiveconf:SCRIPT_NAME}'

create table IF NOT EXISTS STAGE_${hiveconf:ENV}.table_name (
  id                int,
  name              string
)
partitioned by (ddate string);
