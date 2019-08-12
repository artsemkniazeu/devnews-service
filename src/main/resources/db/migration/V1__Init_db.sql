-- Categories

create table if not exists categories
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    name            text            not null unique,
    value           text            not null unique,
    parent_id       uuid            not null references categories (id)
);

create index if not exists categories_parent_id on categories (parent_id);

--- Tags

create table if not exists tags
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    name            text            not null unique,
    value           text            not null unique,
    category_id     uuid            not null references categories (id)
);

create index if not exists tags_category_id on tags (category_id);

--- Users

create table if not exists users
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    username        text            unique,
    email           text            not null unique,
    phone           text            unique,
    password        text            not null,
    role            text            not null,
    first_name      text            not null,
    last_name       text            not null,
    birthday        timestamp,
    city            text,
    country         text,
    image_url       text,
    bg_url          text

);

--- Comments

create table if not exists comments
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    likes           bigint          not null,
    text            text            not null,
    parent_id       uuid            not null references comments (id),
    user_id         uuid            not null references users (id)
);

create index if not exists comments_parent_id on comments (parent_id);

create index if not exists comments_user_id on comments (user_id);


--- Posts

create table if not exists posts
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    update_date     timestamp,
    publish_date    timestamp       not null,
    image_url       text            not null,
    text            text            not null,
    title           text            not null,
    publisher_id    uuid            not null references users (id)
);

create index if not exists posts_publisher_id on posts (publisher_id);

--- Post Tags

create table if not exists post_tag
(
    post_id         uuid            not null references posts (id),
    tag_id          uuid            not null references tags (id),
    constraint post_tag_pkey primary key (post_id, tag_id)
);

--- User Bookmarks

create table if not exists user_bookmark
(
    user_id         uuid            not null references users,
    post_id         uuid            not null references posts,
    constraint user_bookmark_pkey primary key (user_id, post_id)
);

--- User Followers

create table if not exists user_follower
(
    user_id         uuid            not null references users,
    follower_id     uuid            not null references users,
    constraint user_follower_pkey primary key (user_id, follower_id)
);

--- User Tags

create table if not exists user_tag
(
    user_id         uuid            not null references users,
    tag_id          uuid            not null references tags,
    constraint user_tag_pkey primary key (user_id, tag_id)
);

--- Uploads

create table if not exists uploads
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    url             text            not null,
    user_id         uuid            not null references users (id),
    post_id         uuid            not null references posts (id)
);

create index if not exists uploads_user_id on uploads (user_id);

create index if not exists uploads_post_id on uploads (post_id);