BEGIN;
ALTER TABLE public.users DISABLE TRIGGER ALL;
ALTER TABLE public.uploads DISABLE TRIGGER ALL;
-- now the RI over table b is disabled

INSERT INTO public.users (id, created_at, deleted_at, updated_at, username, email, phone, password, role, first_name, last_name, full_name, birthday, city, country, image_url, bg_url, activation_key, reset_key, enabled, locked, image_upload_id, bg_upload_id) VALUES ('58561815-3011-4cbf-8d9d-63245aee229c', '2019-11-06 19:24:16.737385', null, '2019-11-06 19:25:37.562939', 'JaneDoe', 'jane.doe@example.com', null, '$2a$10$pI/M3vfUl.LBm1RUk1Uj4eyErnxYeToTCrmAF0JHm0yDAOiJ5.TfG', 'USER', 'Jane', 'Doe', 'Jane Doe', '2019-11-06 19:25:36.095000', 'Bialystok', 'PL', 'https://www.googleapis.com/download/storage/v1/b/devnews-image-bucket/o/a6c4c440-0d83-41f3-834b-5d23f550d19e-avatar.jpg?generation=1573064720611859&alt=media', 'https://www.googleapis.com/download/storage/v1/b/devnews-image-bucket/o/c8290c96-1ed8-411a-ac0a-74de0b05933c-backround.jpg?generation=1573064723637201&alt=media', null, null, true, false, 'e8d51aca-7f7e-4228-8676-724b845e702b', '178de793-5284-482e-8741-094d17beebf1');
INSERT INTO public.users (id, created_at, deleted_at, updated_at, username, email, phone, password, role, first_name, last_name, full_name, birthday, city, country, image_url, bg_url, activation_key, reset_key, enabled, locked, image_upload_id, bg_upload_id) VALUES ('08988379-4ed8-44ec-bfec-eb3301bda635', '2019-11-06 19:26:14.230756', null, '2019-11-06 19:26:52.374271', 'JohnDoe', 'john.doe@example.com', null, '$2a$10$LlVp1wmHx4ahnS9qDIVxRex17xErPkSHDa/6DzfneqqET5Z56ehgC', 'USER', 'John', 'Doe', 'John Doe', '2019-11-06 19:26:50.708000', 'Bialystok', 'PL', 'https://www.googleapis.com/download/storage/v1/b/devnews-image-bucket/o/a852570e-5626-4e0f-8f60-f6bd7e530151-guardians_of_the_galaxy_enot_oruzhie_agressiya_104117_3840x2400.jpg?generation=1573064800288384&alt=media', 'https://www.googleapis.com/download/storage/v1/b/devnews-image-bucket/o/9781d333-8679-4f5e-b8e7-547eb6dadcc3-deer_art_vector_134088_3840x2400.jpg?generation=1573064802922147&alt=media', null, null, true, false, '0b83a14c-411b-4b1a-b051-92f17e82cedf', '2c82ef9d-2cc2-4b5d-8759-0e3f71d1d24d');

INSERT INTO public.uploads (id, created_at, deleted_at, updated_at, url, bucket, filename, generation, user_id, post_id) VALUES ('e8d51aca-7f7e-4228-8676-724b845e702b', '2019-11-06 19:25:20.880362', null, '2019-11-06 19:24:25.302626', 'https://www.googleapis.com/download/storage/v1/b/devnews-image-bucket/o/a6c4c440-0d83-41f3-834b-5d23f550d19e-avatar.jpg?generation=1573064720611859&alt=media', 'devnews-image-bucket', 'a6c4c440-0d83-41f3-834b-5d23f550d19e-avatar.jpg', 1573064720611859, '58561815-3011-4cbf-8d9d-63245aee229c', null);
INSERT INTO public.uploads (id, created_at, deleted_at, updated_at, url, bucket, filename, generation, user_id, post_id) VALUES ('178de793-5284-482e-8741-094d17beebf1', '2019-11-06 19:25:23.744076', null, '2019-11-06 19:25:20.882787', 'https://www.googleapis.com/download/storage/v1/b/devnews-image-bucket/o/c8290c96-1ed8-411a-ac0a-74de0b05933c-backround.jpg?generation=1573064723637201&alt=media', 'devnews-image-bucket', 'c8290c96-1ed8-411a-ac0a-74de0b05933c-backround.jpg', 1573064723637201, '58561815-3011-4cbf-8d9d-63245aee229c', null);
INSERT INTO public.uploads (id, created_at, deleted_at, updated_at, url, bucket, filename, generation, user_id, post_id) VALUES ('0b83a14c-411b-4b1a-b051-92f17e82cedf', '2019-11-06 19:26:40.407282', null, '2019-11-06 19:26:23.524292', 'https://www.googleapis.com/download/storage/v1/b/devnews-image-bucket/o/a852570e-5626-4e0f-8f60-f6bd7e530151-guardians_of_the_galaxy_enot_oruzhie_agressiya_104117_3840x2400.jpg?generation=1573064800288384&alt=media', 'devnews-image-bucket', 'a852570e-5626-4e0f-8f60-f6bd7e530151-guardians_of_the_galaxy_enot_oruzhie_agressiya_104117_3840x2400.jpg', 1573064800288384, '08988379-4ed8-44ec-bfec-eb3301bda635', null);
INSERT INTO public.uploads (id, created_at, deleted_at, updated_at, url, bucket, filename, generation, user_id, post_id) VALUES ('2c82ef9d-2cc2-4b5d-8759-0e3f71d1d24d', '2019-11-06 19:26:43.106826', null, '2019-11-06 19:26:23.524292', 'https://www.googleapis.com/download/storage/v1/b/devnews-image-bucket/o/9781d333-8679-4f5e-b8e7-547eb6dadcc3-deer_art_vector_134088_3840x2400.jpg?generation=1573064802922147&alt=media', 'devnews-image-bucket', '9781d333-8679-4f5e-b8e7-547eb6dadcc3-deer_art_vector_134088_3840x2400.jpg', 1573064802922147, '08988379-4ed8-44ec-bfec-eb3301bda635', null);

ALTER TABLE public.users ENABLE TRIGGER ALL;
ALTER TABLE public.uploads ENABLE TRIGGER ALL;
COMMIT;