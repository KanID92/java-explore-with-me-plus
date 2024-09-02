DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS LOCATIONS CASCADE;
DROP TABLE IF EXISTS EVENTS CASCADE;


CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME    VARCHAR(64) NOT NULL,
    EMAIL   VARCHAR(512) NOT NULL,
    CONSTRAINT USERS_PK
        PRIMARY KEY (USER_ID),
    CONSTRAINT USER_EMAIL_UNIQUE
        UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS LOCATIONS
(
    LOCATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    LAT   NUMERIC(8,6) NOT NULL,
    LON   NUMERIC(8,6) NOT NULL,
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
    INITIATOR_ID BIGINT NOT NULL,
    LOCATION_ID BIGINT NOT NULL,
    PAID BOOLEAN NOT NULL,
    PARTICIPANT_LIMIT BIGINT NOT NULL,
    PUBLISHED_ON TIMESTAMP NOT NULL,
    REQUEST_MODERATION BOOLEAN NOT NULL,
    STATE VARCHAR(64) NOT NULL,
    TITLE VARCHAR(128) NOT NULL,

    CONSTRAINT EVENTS_PK
            PRIMARY KEY (EVENT_ID),
    CONSTRAINT EVENTS_TITLE_UNIQUE
        UNIQUE (TITLE),
--     CONSTRAINT EVENTS_CATEGORY_ID_FK_CATEGORY_CATEGORY_ID
--         FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY (CATEGORY_ID) ON DELETE CASCADE,
    CONSTRAINT EVENTS_LOCATION_ID_FK_LOCATIONS_LOCATION_ID
        FOREIGN KEY (LOCATION_ID) REFERENCES LOCATIONS (LOCATION_ID) ON DELETE CASCADE,
    CONSTRAINT EVENTS_INITIATOR_ID_FK_USERS_USER_ID
        FOREIGN KEY (INITIATOR_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE

)