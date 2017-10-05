create table IF NOT EXISTS ${schema}.sample_table_2 (
  id                int,
  name              string
)
partitioned by (ddate string);
