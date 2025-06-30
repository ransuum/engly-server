create table message_reads
(
    message_id varchar(255)                not null
        constraint fk4hf3vygb7iguer8spwq89xwsa
            references messages,
    user_id    varchar(255)                not null
        constraint fkd25ttt384b7vp2bqtw19gighu
            references users,
    read_at    timestamp(6) with time zone not null,
    primary key (message_id, user_id)
);