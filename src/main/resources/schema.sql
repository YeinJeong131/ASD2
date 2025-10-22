use wiki;

create table if not exists article (
                                       id bigint auto_increment primary key,
                                       title varchar(255) not null,
                                       body longtext,
                                       publish_date date,
                                       author json default (json_array()),
                                       tags json default (json_array())
);
use wiki;
