DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS LOCATIONS CASCADE;
DROP TABLE IF EXISTS CATEGORIES CASCADE;
DROP TABLE IF EXISTS EVENTS CASCADE;
DROP TABLE IF EXISTS REQUESTS CASCADE;
DROP TABLE IF EXISTS COMPILATIONS CASCADE;
DROP TABLE IF EXISTS COMPILATIONS_EVENTS CASCADE;
DROP TABLE IF EXISTS LIKES_EVENTS CASCADE;
DROP TABLE IF EXISTS LIKES_LOCATIONS CASCADE;


CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME    VARCHAR(254) NOT NULL,
    EMAIL   VARCHAR(512) NOT NULL,
    CONSTRAINT USERS_PK
        PRIMARY KEY (USER_ID),
    CONSTRAINT USER_EMAIL_UNIQUE
        UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS LOCATIONS
(
    LOCATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    LAT   NUMERIC(10,6) NOT NULL,
    LON   NUMERIC(10,6) NOT NULL,
    CONSTRAINT LOCATIONS_PK
        PRIMARY KEY (LOCATION_ID)
);

CREATE TABLE IF NOT EXISTS CATEGORIES
(
    CATEGORY_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME    VARCHAR(128) NOT NULL,
    CONSTRAINT CATEGORIES_PK PRIMARY KEY (CATEGORY_ID),
    CONSTRAINT CATEGORY_NAME_UNIQUE UNIQUE (NAME)
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    EVENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    ANNOTATION VARCHAR(2000) NOT NULL,
    CATEGORY_ID BIGINT NOT NULL,
    DESCRIPTION VARCHAR(7000) NOT NULL,
    EVENT_DATE TIMESTAMP NOT NULL,
    CREATED_ON TIMESTAMP NOT NULL,
    INITIATOR_ID BIGINT NOT NULL,
    LOCATION_ID BIGINT NOT NULL,
    PAID BOOLEAN NOT NULL,
    PARTICIPANT_LIMIT BIGINT NOT NULL,
    PUBLISHED_ON TIMESTAMP,
    REQUEST_MODERATION BOOLEAN NOT NULL,
    STATE VARCHAR(64) NOT NULL,
    TITLE VARCHAR(128) NOT NULL,

    CONSTRAINT EVENTS_PK
            PRIMARY KEY (EVENT_ID),
    CONSTRAINT EVENTS_TITLE_UNIQUE
        UNIQUE (TITLE),
    CONSTRAINT EVENTS_CATEGORY_ID_FK_CATEGORY_CATEGORY_ID
        FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES (CATEGORY_ID) ON DELETE RESTRICT,
    CONSTRAINT EVENTS_LOCATION_ID_FK_LOCATIONS_LOCATION_ID
        FOREIGN KEY (LOCATION_ID) REFERENCES LOCATIONS (LOCATION_ID) ON DELETE CASCADE,
    CONSTRAINT EVENTS_INITIATOR_ID_FK_USERS_USER_ID
        FOREIGN KEY (INITIATOR_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    COMPILATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    TITLE  VARCHAR(128) NOT NULL,
    PINNED BOOLEAN NOT NULL,
    CONSTRAINT COMPILATIONS_PK PRIMARY KEY (COMPILATION_ID)
);

CREATE TABLE IF NOT EXISTS COMPILATIONS_EVENTS
(
    COMPILATION_ID     BIGINT   NOT NULL,
    EVENT_ID  BIGINT   NOT NULL,
    PRIMARY KEY (COMPILATION_ID, EVENT_ID),
    FOREIGN KEY (COMPILATION_ID) REFERENCES COMPILATIONS (COMPILATION_ID) ON DELETE CASCADE,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    REQUEST_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    CREATED TIMESTAMP NOT NULL,
    EVENT_ID BIGINT NOT NULL,
    REQUESTER_ID BIGINT NOT NULL,
    STATUS VARCHAR(32) NOT NULL,

    CONSTRAINT REQUESTS_PK
        PRIMARY KEY (REQUEST_ID),
    CONSTRAINT REQUESTS_UNIQUE_EVENT_ID_REQUESTER_ID
        UNIQUE (EVENT_ID, REQUESTER_ID),
    CONSTRAINT REQUESTS_EVENT_ID_FK_EVENTS_EVENT_ID
        FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE,
    CONSTRAINT REQUESTS_REQUESTER_ID_FK_USERS_USER_ID
        FOREIGN KEY (REQUESTER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS LIKES_EVENTS
(
    EVENT_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
    CONSTRAINT LIKES_EVENTS_PK
        PRIMARY KEY (EVENT_ID, USER_ID),
    CONSTRAINT LIKES_EVENTS_EVENT_ID_FK_EVENTS_EVENT_ID
        FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE,
    CONSTRAINT LIKES_EVENTS_USER_ID_FK_USERS_USER_ID
        FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS LIKES_LOCATIONS
(
    LOCATION_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
    CONSTRAINT LIKES_PK
        PRIMARY KEY (LOCATION_ID, USER_ID),
    CONSTRAINT LIKES_EVENT_ID_FK_EVENTS_EVENT_ID
        FOREIGN KEY (LOCATION_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE,
    CONSTRAINT LIKES_EVENT_USER_ID_FK_USERS_USER_ID
        FOREIGN KEY (USER_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE
);





