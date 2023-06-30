create table posts (
  id serial primary key,
  name text,
  description text,
  created timestamp without time zone not null default now()
);