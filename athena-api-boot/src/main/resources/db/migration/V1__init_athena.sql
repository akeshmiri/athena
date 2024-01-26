CREATE SCHEMA athena_core;
CREATE SCHEMA athena_pipeline;
CREATE SCHEMA athena_openapi;
CREATE SCHEMA athena_tms;
CREATE SCHEMA athena_kube;
create table athena_core.environment (id bigserial not null, project_code bigint not null, code varchar(10) not null unique, name varchar(50), primary key (id));
create table athena_core.project (id bigserial not null, code varchar(10) not null unique, name varchar(50) not null unique, primary key (id));
create table athena_core.user (id bigserial not null, name varchar(300) not null unique, primary key (id));
create table athena_core.version (id bigserial not null, project_code bigint not null, code varchar(10) not null unique, name varchar(50) not null unique, primary key (id));
create table athena_openapi.api_parameter (id bigserial not null, name varchar(100) not null, type varchar(100) not null, primary key (id));
create table athena_openapi.api (api_spec_id bigint not null, id bigserial not null, method varchar(10) not null, url varchar(500) not null, title varchar(1000) not null, description varchar(5000), parameters jsonb, primary key (id));
create table athena_openapi.api_metadata (id bigserial not null, name varchar(100) not null, value varchar(2000) not null, primary key (id));
create table athena_openapi.api_spec (first_time_seen timestamp(6), id bigserial not null, last_time_seen timestamp(6), project_code bigint not null, version varchar(10) not null, name varchar(100) not null, title varchar(100) not null, primary key (id));
create table athena_openapi.api_spec_metadata (id bigserial not null, name varchar(100) not null, value varchar(2000) not null, primary key (id));
create table athena_openapi.api_spec_metadata_mid (api_spec_id bigint not null, metadata_id bigint not null, primary key (api_spec_id, metadata_id));
create table athena_openapi.server_metadata_mid (metadata_id bigint not null, path_id bigint not null, primary key (metadata_id, path_id));
create table athena_pipeline.execution (before_class_end_time timestamp(6), before_class_start_time timestamp(6), before_method_end_time timestamp(6), before_method_start_time timestamp(6), end_time timestamp(6) not null, executor_id bigint, id bigserial not null, pipeline_id bigint, start_time timestamp(6) not null, status_id bigint not null, test_end_time timestamp(6), test_start_time timestamp(6), class_name varchar(300) not null, method_name varchar(300) not null, package_name varchar(300) not null, parameters varchar(300), primary key (id));
create table athena_pipeline.execution_metadata (id bigserial not null, name varchar(100) not null, value varchar(2000) not null, primary key (id));
create table athena_pipeline.execution_metadata_mid (execution_id bigint not null, metadata_id bigint not null, primary key (execution_id, metadata_id));
create table athena_pipeline.pipeline (end_date timestamp(6), environment_code bigint not null, id bigserial not null, start_date timestamp(6) not null, name varchar(100) not null, number varchar(100) not null, description varchar(300) not null, primary key (id));
create table athena_pipeline.pipeline_metadata (id bigserial not null, name varchar(100) not null, value varchar(2000) not null, primary key (id));
create table athena_pipeline.pipeline_metadata_mid (metadata_id bigint not null, pipeline_id bigint not null, primary key (metadata_id, pipeline_id));
create table athena_pipeline.scenario_execution (before_scenario_end_time timestamp(6) not null, before_scenario_start_time timestamp(6) not null, end_time timestamp(6) not null, executor_id bigint, id bigserial not null, pipeline_id bigint, start_time timestamp(6) not null, status_id bigint not null, parameters varchar(300), scenario varchar(300) not null, feature varchar(1000) not null, primary key (id));
create table athena_pipeline.scenario_metadata_mid (execution_id bigint not null, metadata_id bigint not null, primary key (execution_id, metadata_id));
create table athena_pipeline.status (id bigserial not null, name varchar(100) not null unique, primary key (id));
alter table if exists athena_core.environment add constraint FK_ENVIRONMENT_PROJECT foreign key (project_code) references athena_core.project;
alter table if exists athena_core.version add constraint FK_VERSION_PROJECT foreign key (project_code) references athena_core.project;
alter table if exists athena_openapi.api add constraint FKj6ccwbiw3uyhk9i301nvjoq0f foreign key (api_spec_id) references athena_openapi.api_spec;
alter table if exists athena_openapi.api_spec add constraint FK_API_SPEC_PROJECT foreign key (project_code) references athena_core.project;
alter table if exists athena_openapi.api_spec_metadata_mid add constraint FKa7fg5ffcjf1wiao3wffe67c01 foreign key (metadata_id) references athena_openapi.api_spec_metadata;
alter table if exists athena_openapi.api_spec_metadata_mid add constraint FK9epune7khiuwoye9a4jjlqjjq foreign key (api_spec_id) references athena_openapi.api_spec;
alter table if exists athena_openapi.server_metadata_mid add constraint FK82g85b0kl2qfd4kcpv8hm3fet foreign key (metadata_id) references athena_openapi.api_metadata;
alter table if exists athena_openapi.server_metadata_mid add constraint FKfc7orj1oxch007i91mesrwiq8 foreign key (path_id) references athena_openapi.api;
alter table if exists athena_pipeline.execution add constraint FK_EXECUTION_USER foreign key (executor_id) references athena_core.user;
alter table if exists athena_pipeline.execution add constraint FK_EXECUTION_PIPELINE foreign key (pipeline_id) references athena_pipeline.pipeline;
alter table if exists athena_pipeline.execution add constraint FK_EXECUTION_STATUS foreign key (status_id) references athena_pipeline.status;
alter table if exists athena_pipeline.execution_metadata_mid add constraint FKickcqlum2vg9let6scpuwkr3b foreign key (metadata_id) references athena_pipeline.execution_metadata;
alter table if exists athena_pipeline.execution_metadata_mid add constraint FKm9u1oqvmevg9d5qy25mi8h10x foreign key (execution_id) references athena_pipeline.execution;
alter table if exists athena_pipeline.pipeline add constraint FK_PIPELINE_ENVIRONMENT foreign key (environment_code) references athena_core.environment;
alter table if exists athena_pipeline.pipeline_metadata_mid add constraint FKcs5r08ptgvp6eijpulce3dsm0 foreign key (metadata_id) references athena_pipeline.pipeline_metadata;
alter table if exists athena_pipeline.pipeline_metadata_mid add constraint FKg07aj55k425j3nwva53a6a2pd foreign key (pipeline_id) references athena_pipeline.pipeline;
alter table if exists athena_pipeline.scenario_execution add constraint FK_EXECUTION_USER foreign key (executor_id) references athena_core.user;
alter table if exists athena_pipeline.scenario_execution add constraint FK_EXECUTION_PIPELINE foreign key (pipeline_id) references athena_pipeline.pipeline;
alter table if exists athena_pipeline.scenario_execution add constraint FK_EXECUTION_STATUS foreign key (status_id) references athena_pipeline.status;
alter table if exists athena_pipeline.scenario_metadata_mid add constraint FKohg9ibf8as6d3gnd3yqg0hu0f foreign key (metadata_id) references athena_pipeline.execution_metadata;
alter table if exists athena_pipeline.scenario_metadata_mid add constraint FKacptnescmpf3du47da8ihq9ps foreign key (execution_id) references athena_pipeline.scenario_execution;
