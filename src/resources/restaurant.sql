-- =========================
-- USERS
-- =========================
# drop table if exists OrderItem;
# drop table if exists Orders;
# drop table if exists Users;
# drop table if exists Menu;
# drop table if exists Tables;
create table if not exists Users (
	user_id int auto_increment primary key,
    username varchar(100),
    password varchar(100),
    role enum ('MANAGER', 'CHEF', 'CUSTOMER') not null,
    status enum ('ACTIVE', 'BANNED') default 'ACTIVE'
);

-- =========================
-- TABLES (BÀN ĂN)
-- =========================

create table if not exists Tables (
	table_id int auto_increment primary key,
    name varchar(50),
    capacity int not null check (capacity > 0),
    status enum ('FULL', 'EMPTY') default 'EMPTY'
);

-- =========================
-- MENU
-- =========================

create table if not exists Menu (
	menu_id int auto_increment primary key,
    name varchar(50),
    type enum ('FOOD', 'DRINK') not null,
    price double not null check (price > 0),
    stock int default 0,
    status enum ('AVAILABLE', 'OUT_OF_STOCK') default 'AVAILABLE'
);

-- =========================
-- ORDERS
-- =========================

create table if not exists Orders (
	order_id int auto_increment primary key,
    table_id int,
    customer_id int,
    status enum ('OPEN', 'CLOSED') default 'OPEN',
    create_at datetime default current_timestamp,

    foreign key(table_id) references Tables(table_id),
    foreign key(customer_id) references Users(user_id)
);

-- =========================
-- ORDER ITEMS
-- =========================

create table if not exists OrderItem (
	order_item int auto_increment primary key,
    order_id int,
    menu_id int,
    quantity int,
    status enum ('PENDING', 'COOKING', 'READY', 'SERVED') default 'PENDING',

    foreign key (order_id) references Orders(order_id) on delete cascade,
    foreign key (menu_id) references Menu(menu_id)
);

