CREATE TABLE report
( id number(10) NOT NULL,
  report_name varchar2(100) ,
  report_file blob,
  CONSTRAINT report_pk PRIMARY KEY (id),
  CONSTRAINT report_name_unique UNIQUE (report_name)
);

CREATE SEQUENCE report_id_seq START WITH 1 CACHE 50;

CREATE TABLE report_request
( id number(10) NOT NULL,
  sql_request varchar2(4000),
  id_report number(10) NOT NULL,
  prefix varchar2(50),
  CONSTRAINT report_request_pk PRIMARY KEY (id),
  CONSTRAINT fk_report_id
    FOREIGN KEY (id_report)
    REFERENCES report(id)
);

CREATE SEQUENCE report_request_id_seq START WITH 1 CACHE 50;

commit;