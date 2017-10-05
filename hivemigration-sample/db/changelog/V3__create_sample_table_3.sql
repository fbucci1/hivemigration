create table IF NOT EXISTS ${schema}.sample_table_3 (
  id                int,
  name              string
)
partitioned by (ddate string);
