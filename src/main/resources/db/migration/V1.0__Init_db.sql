create extension if not exists citext;


-- Categories

create table if not exists categories
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    name            citext          not null unique,
    value           text            not null unique,
    parent_id       uuid            references categories (id)
);

create index if not exists categories_parent_id on categories (parent_id);

--- Tags

create table if not exists tags
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    name            citext          not null unique
);

--- Users

create table if not exists users
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    username        citext          unique,
    email           text            not null unique,
    phone           text            unique,
    password        text            not null,
    role            text            not null,
    first_name      citext          not null,
    last_name       citext          not null,
    full_name       citext          not null,
    birthday        timestamp,
    city            text,
    country         text,
    image_url       text,
    bg_url          text,
    activation_key  uuid,
    reset_key       uuid,
    enabled         boolean         not null default false,
    locked          boolean         not null,
    image_upload_id uuid, -- reference
    bg_upload_id    uuid  -- reference
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

-- Groups

create table if not exists groups
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    name            citext          not null,
    value           citext          not null unique,
    about           text,
    image_url       text,
    bg_url          text,
    owner_id        uuid            not null references users (id),
    image_upload_id uuid, -- reference
    bg_upload_id    uuid  -- reference
);

create index if not exists groups_owner_id on groups (owner_id);

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
    text            citext          not null,
    title           citext          not null,
    publisher_id    uuid            not null references users (id),
    group_id        uuid            references groups (id)
);

create index if not exists posts_publisher_id on posts (publisher_id);

create index if not exists posts_group_id on posts (group_id);


--- Uploads

create table if not exists uploads
(
    id              uuid            not null primary key,
    created_at      timestamp       not null,
    deleted_at      timestamp,
    updated_at      timestamp,
    url             text            not null,
    bucket          text            not null,
    filename        text            not null,
    generation      bigint          not null,
    user_id         uuid            references users (id),
    post_id         uuid            references posts (id)
);

create index if not exists uploads_user_id on uploads (user_id);

create index if not exists uploads_post_id on uploads (post_id);

--- Post Categories

create table if not exists post_category
(
    post_id         uuid            not null references posts (id),
    category_id     uuid            not null references categories (id),
    constraint post_category_pkey primary key (post_id, category_id)
);

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

--- Group User

create table if not exists group_user
(
    group_id         uuid            not null references groups,
    user_id          uuid            not null references users,
    constraint group_user_pkey primary key (group_id, user_id)
);

alter table users
add constraint users_uploads_image_upload_id foreign key (image_upload_id) references uploads (id);

alter table users
add constraint users_uploads_bg_upload_id foreign key (bg_upload_id) references uploads (id);

alter table groups
add constraint groups_uploads_image_upload_id foreign key (image_upload_id) references uploads (id);

alter table groups
add constraint groups_uploads_bg_upload_id foreign key (bg_upload_id) references uploads (id);
