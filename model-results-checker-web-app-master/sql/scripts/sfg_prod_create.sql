USE sfg_prod;
create sequence hibernate_sequence start 1 increment 1
create table container (container_id int8 not null, blob_name varchar(255), class_name varchar(255), csv_name varchar(255), description varchar(255) not null, frame_rate float8 not null, name varchar(255) not null, csv_csv_id int8, persistentcsv_persistent_csv_id int8, project_project_id int8, submission_id int8, primary key (container_id))
create table container_classes (container_container_id int8 not null, classes varchar(255))
create table csv (csv_id  bigserial not null, container_container_id int8, primary key (csv_id))
create table persistent_csv (persistent_csv_id  bigserial not null, container_container_id int8, primary key (persistent_csv_id))
create table persistent_record (record_id int8 not null, frame_num int4 not null, label varchar(255), model_confidence float8 not null, object_id int4 not null, tracker_confidence float8 not null, trackerh int4 not null, trackerl int4 not null, trackert int4 not null, trackerw int4 not null, persistent_csv_persistent_csv_id int8 not null, primary key (record_id))
create table project (project_id  bigserial not null, description varchar(255) not null, title varchar(255) not null, admin_id varchar(255), project_holder_project_holder_id int8, primary key (project_id))
create table project_holder (project_holder_id  bigserial not null, client_id varchar(255), primary key (project_holder_id))
create table record (record_id int8 not null, frame_num int4 not null, label varchar(255), model_confidence float8 not null, object_id int4 not null, tracker_confidence float8 not null, trackerh int4 not null, trackerl int4 not null, trackert int4 not null, trackerw int4 not null, csv_csv_id int8 not null, primary key (record_id))
create table submission (id int8 not null, client_id varchar(255), container_container_id int8, primary key (id))
create table user_account (type varchar(31) not null, id varchar(255) not null, email varchar(255) not null, registration_date date not null, username varchar(255) not null, project_holder_project_holder_id int8, submission_id int8, primary key (id))
create index IDX5b9y3occe9yuswf6u7nbq4app on record (csv_csv_id, object_id)
alter table if exists user_account add constraint UK_hl02wv5hym99ys465woijmfib unique (email)
alter table if exists container add constraint FKm7i3b7blob2pl4skqpvung2ik foreign key (csv_csv_id) references csv on delete cascade
alter table if exists container add constraint FKid3ufjgkfehapmpwipqlxaj5o foreign key (persistentcsv_persistent_csv_id) references persistent_csv on delete cascade
alter table if exists container add constraint FKbm0axj48yo1u02cx7sra2561g foreign key (project_project_id) references project on delete cascade
alter table if exists container add constraint FKeeu3r6aiur06sgr6o08q487k4 foreign key (submission_id) references submission
alter table if exists container_classes add constraint FKqba0pdigakl66shayx6xcff7y foreign key (container_container_id) references container
alter table if exists csv add constraint FKdic8jq7tgr2q69mds32081dko foreign key (container_container_id) references container
alter table if exists persistent_csv add constraint FKm87trfdaod546l4n5iuhryvfn foreign key (container_container_id) references container
alter table if exists persistent_record add constraint FKjcol9cq9phwtge3jklvgbxqp8 foreign key (persistent_csv_persistent_csv_id) references persistent_csv
alter table if exists project add constraint FKxxbpe7jaf2anhu2g3h0vo33c foreign key (admin_id) references user_account
alter table if exists project add constraint FKmnsva8pxg7ymef57b1wf9nnhd foreign key (project_holder_project_holder_id) references project_holder
alter table if exists project_holder add constraint FKd6ctaxki3kydoorifscyahxsp foreign key (client_id) references user_account
alter table if exists record add constraint FKbd87isjhb661mgf6yhlklh0av foreign key (csv_csv_id) references csv
alter table if exists submission add constraint FK5eqhdeq42gem6se0dc93jf1m4 foreign key (client_id) references user_account
alter table if exists submission add constraint FKcy9xwgfg8pomtdc85jsx4t3wm foreign key (container_container_id) references container
alter table if exists user_account add constraint FKdbjc88jbx9266rg8cjw8feanu foreign key (project_holder_project_holder_id) references project_holder
alter table if exists user_account add constraint FKck2ung5argm4vfa6v2j4i454i foreign key (submission_id) references submission
