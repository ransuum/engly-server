-- Table for MessageReads
create table if not exists message_reads (
    message_id varchar(255) not null,
    user_id varchar(255) not null,
    read_at timestamp not null,
    primary key (message_id, user_id),
    foreign key (message_id) references messages(id),
    foreign key (user_id) references users(id)
);