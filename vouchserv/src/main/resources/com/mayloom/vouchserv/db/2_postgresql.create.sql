create table "ROLE" (
	ID int8 not null unique,
	AUTHORITY varchar(255) not null,
	primary key (ID)
);
-- NOTICE:  CREATE TABLE / PRIMARY KEY will create implicit index "ROLE_pkey" for table "ROLE" 

create table "USER_ROLE" (
	USER_ID varchar(255) not null,
	ROLE_ID int8 not null,
	unique (USER_ID, ROLE_ID)
);
-- NOTICE:  CREATE TABLE / UNIQUE will create implicit index "USER_ROLE_user_id_role_id_key" for table "USER_ROLE"

create table "USER" (
	USERNAME varchar(255) not null unique,
 	ACCNT_NON_EXP bool not null,
 	ACCNT_NON_LOCK bool not null,
 	CRED_NON_EXP bool not null,
 	ENABLED bool not null,
 	PASSWORD varchar(255) not null,
	primary key (USERNAME)
);
-- NOTICE:  CREATE TABLE / PRIMARY KEY will create implicit index "USER_pkey" for table "USER"
-- NOTICE:  CREATE TABLE / UNIQUE will create implicit index "USER_username_key" for table "USER"

create table "VOUCHER_BATCH_OWNER" (
	CODE varchar(255) not null unique,
	CREATION_DATE timestamp not null,
	LAST_UPDATE timestamp,
	NEXT_BATCH_START_SERIAL_NUMBER int8 not null,
	USER_ID varchar(255) not null,
	primary key (CODE),
	unique (CODE)
);
-- NOTICE:  CREATE TABLE / PRIMARY KEY will create implicit index "VOUCHER_BATCH_OWNER_pkey" for table "VOUCHER_BATCH_OWNER"
 
create table "VOUCHER_BATCH" (
	ID int8 not null unique,
	ACTIVE bool not null,
	BATCH_NUMBER int4 not null,
	CREATION_DATE timestamp not null,
	EXPIRY_DATE timestamp,
	GEN_SIZE int4 not null,
	REQ_SIZE int4 not null,
	VBO_ID varchar(255) not null,
	primary key (ID)
);
-- NOTICE:  CREATE TABLE / PRIMARY KEY will create implicit index "VOUCHER_BATCH_pkey" for table "VOUCHER_BATCH"
 
create table "VOUCHER" (
	PIN varchar(255) not null,
	VBO_ID varchar(255) not null,
	ACTIVE bool,
	EXPIRY_DATE timestamp,
	REDEMPTION_DATE timestamp,
	SERIAL_NUMBER int8 not null,
	VOUCHER_BATCH_ID int8,
	primary key (PIN, VBO_ID)
);
-- NOTICE:  CREATE TABLE / PRIMARY KEY will create implicit index "VOUCHER_pkey" for table "VOUCHER"

alter table "USER_ROLE" 
	add constraint FKBC16F46A79053263 
	foreign key (ROLE_ID) 
	references "ROLE";
 
alter table "USER_ROLE" 
	add constraint FKBC16F46A1E2FF643 
	foreign key (USER_ID) 
	references "USER";
 
alter table "VOUCHER_BATCH_OWNER" 
	add constraint FKC09514FD1E2FF643 
	foreign key (USER_ID) 
	references "USER";
 
alter table "VOUCHER_BATCH" 
	add constraint FK298ABA29FBB3DEB5 
	foreign key (VBO_ID) 
	references "VOUCHER_BATCH_OWNER";
	
ALTER TABLE "VOUCHER_BATCH" ADD CHECK (gen_size <= req_size);
 
alter table "VOUCHER" 
	add constraint FK50F41A8EFBB3DEB5 
	foreign key (VBO_ID) 
	references "VOUCHER_BATCH_OWNER";
 
alter table "VOUCHER" 
	add constraint FK50F41A8E729BD746 
	foreign key (VOUCHER_BATCH_ID) 
	references "VOUCHER_BATCH";
 
create sequence hibernate_sequence;