create table coins
(
  id           bigint            not null primary key auto_increment,
  coin_value   varchar(255)      not null,
  count        int               not null check (count > 0)
);

create table quotes
(
  id           bigint           not null primary key auto_increment,
  quote        varchar(256)     not null,
  available    boolean          not null default true,
  price        int              not null check (price > 0)
);

-- no indexes needed for now