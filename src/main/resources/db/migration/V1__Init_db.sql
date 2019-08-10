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
    bg_url          text,
    birthday        timestamp,
    city            text,
    country         text,
    email           text            not null unique,
    first_name      text            not null,
    last_name       text            not null,
    image_url       text,
    password        text            not null,
    phone           text            not null unique,
    role            text            not null,
    username        text            not null unique
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
    date            timestamp       not null,
    image_url       text            not null,
    text            text            not null,
    title           text            not null,
    publisher_id    uuid            not null references users (id)
);

create index if not exists posts_publisher_id on posts (publisher_id);

---

create table if not exists post_tag
(
    post_id         uuid            not null references posts (id),
    tag_id          uuid            not null references tags (id),
    constraint post_tag_pkey primary key (post_id, tag_id)
);

---

create table if not exists user_bookmark
(
    user_id         uuid            not null references users,
    post_id         uuid            not null references posts,
    constraint user_bookmark_pkey primary key (user_id, post_id)
);

---

create table if not exists user_follower
(
    user_id         uuid            not null references users,
    follower_id     uuid            not null references users,
    constraint user_follower_pkey primary key (user_id, follower_id)
);

---

create table if not exists user_tag
(
    user_id         uuid            not null references users,
    tag_id          uuid            not null references tags,
    constraint user_tag_pkey primary key (user_id, tag_id)
);
