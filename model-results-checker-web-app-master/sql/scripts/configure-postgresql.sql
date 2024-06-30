CREATE DATABASE sfg_prod;

#create database service accounts
CREATE USER 'sfg_prod_user'@'%' IDENTIFIED BY 'postgres';

#Database grants
GRANT SELECT ON sfg_prod.* to  'sfg_prod_user'@'%';
GRANT INSERT ON sfg_prod.* to  'sfg_prod_user'@'%';
GRANT DELETE ON sfg_prod.* to  'sfg_prod_user'@'%';
GRANT UPDATE ON sfg_prod.* to  'sfg_prod_user'@'%';