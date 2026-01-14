create table booking (
                         id uuid primary key,
                         foodtruck_name varchar(200) not null,
                         customer_name varchar(200) not null,
                         start_time timestamp not null,
                         end_time timestamp not null,
                         status varchar(30) not null,
                         description text,
                         created_at timestamp not null,
                         updated_at timestamp not null
);

create index idx_booking_status on booking(status);
create index idx_booking_foodtruck_name on booking(foodtruck_name);
