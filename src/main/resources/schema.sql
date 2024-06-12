DROP TABLE IF EXISTS MEMBER;

CREATE TABLE MEMBER
(
    member_id       BIGINT NOT NULL AUTO_INCREMENT,
    login_id VARCHAR(50),
    password VARCHAR(100),
    name     VARCHAR(50),
    authority VARCHAR(50),
    is_deleted BOOLEAN DEFAULT false,
    created_at DATETIME,
    updated_at DATETIME,
    PRIMARY KEY (member_id),
    UNIQUE (`member_id`)
);