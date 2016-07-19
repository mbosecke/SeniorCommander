alter table command add access_level character varying(15);

update command set access_level = 'USER';

alter table command alter access_level set not null;