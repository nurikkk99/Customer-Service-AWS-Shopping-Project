create table image (
  id int8 not null,
  good_id int8,
  s3_uri varchar(255),
  primary key (id)
);
