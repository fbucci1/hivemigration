set hive.variable.substitute=true;

create table IF NOT EXISTS ${schema}.table_name (
  id                int,
  name              string
)
partitioned by (ddate string);
