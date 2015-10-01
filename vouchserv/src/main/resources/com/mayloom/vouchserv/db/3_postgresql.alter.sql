alter table "VOUCHER_BATCH"
	add column REQ_START_SERIAL int8 not null,
	add column REQ_BATCH_TYPE varchar(255) not null,
	add column REQ_PIN_LENGTH int4 not null,
	add column REQ_PIN_TYPE varchar(255) not null;