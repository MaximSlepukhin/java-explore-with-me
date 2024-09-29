create TABLE IF NOT EXISTS users (
id                  BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name                VARCHAR,
email               VARCHAR,
event_id            BIGINT,
UNIQUE(email));
CREATE INDEX idx_users_id ON users (id);
CREATE INDEX idx_users_name ON users (name);


CREATE TABLE IF NOT EXISTS categories (
    id SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name VARCHAR(70) NOT NULL CONSTRAINT unique_category_name UNIQUE
    );
CREATE INDEX idx_cat_id ON categories (id);
CREATE INDEX idx_cat_name ON categories (category_name);


create TABLE IF NOT EXISTS locations (
id                  BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
lat DECIMAL (8,6),
lon DECIMAL (9,6));
CREATE INDEX idx_loc_id ON locations (id);

create TABLE IF NOT EXISTS events (
id                  BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
annotation          VARCHAR,
category_id         SMALLINT,
created_date        TIMESTAMP WITHOUT TIME ZONE,
description         VARCHAR,
event_date          TIMESTAMP WITHOUT TIME ZONE,
initiator_id        BIGINT,
location_id         BIGINT,
paid                BOOLEAN,
participant_limit   BIGINT,
published_date      TIMESTAMP,
request_moderation  BOOLEAN,
state               VARCHAR,
title               VARCHAR,
confirmed_requests   BIGINT,
CONSTRAINT fk_events_to_categories FOREIGN KEY(category_id) REFERENCES categories(id),
CONSTRAINT fk_events_to_locations FOREIGN KEY(location_id) REFERENCES locations(id),
CONSTRAINT fk_events_to_users FOREIGN KEY(initiator_id) REFERENCES users(id));
CREATE INDEX idx_event_id ON events (id);
CREATE INDEX idx_events_state ON events (state);
CREATE INDEX idx_events_category ON events (category_id);
CREATE INDEX idx_events_paid ON events (paid);

create TABLE IF NOT EXISTS requests (
id                  BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
requester_id        BIGINT,
event_id            BIGINT,
created_date TIMESTAMP,
status              VARCHAR,
CONSTRAINT fk_requests_to_users FOREIGN KEY(requester_id) REFERENCES users(id),
CONSTRAINT fk_requests_to_events FOREIGN KEY(event_id) REFERENCES events(id));
CREATE INDEX idx_req_id ON requests (id);

create TABLE IF NOT EXISTS compilations (
id                  BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
title               VARCHAR,
pinned              BOOLEAN);
CREATE INDEX idx_comp_id ON compilations (id);


create TABLE IF NOT EXISTS relation_event_compilation (
id                  BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
compilation_id      BIGINT,
event_id            BIGINT,
CONSTRAINT fk_relation_to_compilation FOREIGN KEY(compilation_id) REFERENCES compilations(id),
CONSTRAINT fk_relation_to_events FOREIGN KEY(event_id) REFERENCES events(id));