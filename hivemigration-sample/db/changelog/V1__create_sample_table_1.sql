set hive.variable.substitute=true;

create table IF NOT EXISTS ${schema}.sample_table_1 (
  id                int,
  name              string
)
partitioned by (ddate string);
