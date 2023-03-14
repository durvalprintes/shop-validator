create schema if not exists shop; 

create table shop.product(
  id bigserial primary key, 
  product_identifier varchar(100) not null,
  amount int not null 
);
