CREATE TABLE IF NOT EXISTS user_tb (
    id BIGINT AUTO_INCREMENT,
    available_join_count INT,
    available_like_count INT,
    avatar_url VARCHAR(255),
    bio VARCHAR(50),
    created_at DATETIME(6),
    deleted_at DATETIME(6),
    github_id VARCHAR(255),
    github_pk BIGINT,
    github_url VARCHAR(255),
    jandi_rate DOUBLE,
    region ENUM('JONGNO', 'JUNG', 'YONGSAN', 'SEONGDONG', 'GWANGJIN', 'DONGDAEMUN', 'JUNGNANG', 'SEONGBUK', 'GANGBUK', 'DOBONG', 'NOWON', 'EUNPYEONG', 'SEODAEMUN', 'MAPO', 'YANGCHEON', 'GANGSEO', 'GURO', 'GEUMCHEON', 'YOUNGDEUNGPO', 'DONGJAK', 'GWANAK', 'SEOCHO', 'GANGNAM', 'SONGPA', 'GANGDONG', 'BUNDANG'),
    region_authenticated_at datetime(6),
    role ENUM('ROLE_USER'),
    username VARCHAR(255),
    signup_yn TINYINT,
    achievement_id BIGINT,
    repository_url VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_wanted_job_tb (
    id BIGINT AUTO_INCREMENT,
    wanted_job ENUM('BACKEND', 'FRONTEND', 'FULLSTACK', 'ANDROID', 'IOS', 'MACHINE_LEARNING', 'ARTIFICIAL_INTELLIGENCE', 'DATA_ENGINEER', 'DBA', 'MOBILE_GAME', 'SYSTEM_NETWORK', 'SYSTEM_SW', 'DEVOPS', 'INTERNET_SECURITY', 'EMBEDDED_SOFTWARE', 'ROBOTICS_MIDDLEWARE', 'QA', 'IOT', 'APPLICATION_SW', 'BLOCKCHAIN', 'PROJECT_MANAGEMENT', 'WEB_PUBLISHING', 'CROSS_PLATFORM', 'VR_AR_3D', 'ERP', 'GRAPHICS'),
    user_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_develop_language_tb (
    id BIGINT AUTO_INCREMENT,
    byte_size INT,
    develop_language ENUM('PYTHON', 'JAVA', 'JAVASCRIPT', 'C', 'CPP', 'CSHARP', 'RUBY', 'SWIFT', 'KOTLIN', 'GO', 'TYPESCRIPT', 'SCALA', 'RUST', 'PHP', 'HTML', 'CSS', 'ELM', 'ERLANG', 'HASKELL', 'R', 'SHELL', 'SQL', 'DART', 'OBJECT_C', 'ETC'),
    user_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_achievement_tb (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    achievement_id BIGINT,
    completed TINYINT(1),
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_activity_tb (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    activity_type ENUM('FROM_ONE_STEP', 'GOOD_PERSON_GOOD_MEETUP', 'LIKE_E', 'MY_DESTINY', 'CAPTURE_FAIL_EXIST', 'RUN_AWAY_FROM_MONSTER_BALL', 'PLEASE_GIVE_ME_MOGAK', 'BRAVE_EXPLORER', 'NOMAD_CODER', 'CATCH_ME_IF_YOU_CAN', 'LEAVE_YOUR_MARK', 'WHAT_A_POPULAR_PERSON', 'CONTACT_WITH_GOD', 'FRESH_DEVELOPER'),
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS project_tb (
    id BIGINT AUTO_INCREMENT,
    creator_id BIGINT,
    bio VARCHAR(255),
    jandi_rating DOUBLE,
    avatar_url VARCHAR(255),
    region ENUM('JONGNO', 'JUNG', 'YONGSAN', 'SEONGDONG', 'GWANGJIN', 'DONGDAEMUN', 'JUNGNANG', 'SEONGBUK', 'GANGBUK', 'DOBONG', 'NOWON', 'EUNPYEONG', 'SEODAEMUN', 'MAPO', 'YANGCHEON', 'GANGSEO', 'GURO', 'GEUMCHEON', 'YOUNGDEUNGPO', 'DONGJAK', 'GWANAK', 'SEOCHO', 'GANGNAM', 'SONGPA', 'GANGDONG', 'BUNDANG'),
    username VARCHAR(255),
    meet_start_time TIMESTAMP,
    meet_end_time TIMESTAMP,
    meet_location POINT,
    meet_detail VARCHAR(255),
    creator_github_id VARCHAR(255),
    main_achievement_id BIGINT,
    project_status ENUM('PENDING', 'MATCHED', 'CANCELED', 'FINISHED'),
    created_at TIMESTAMP,
    deleted_at TIMESTAMP,
    user_github_id VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS project_tag_tb (
    id BIGINT AUTO_INCREMENT,
    project_id BIGINT,
    content VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS profile_card_tb (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT,
    total_like_amount BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS project_join_request_tb (
    id BIGINT AUTO_INCREMENT,
    sender_id BIGINT,
    project_id BIGINT,
    join_request_status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELED'),
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS matching_tb (
    id BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP,
    project_id BIGINT,
    sender_id BIGINT,
    matching_status ENUM('PROGRESS', 'CANCELED', 'FINISHED'),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS profile_card_like_tb (
    id BIGINT AUTO_INCREMENT,
    sender_id BIGINT,
    receiver_id BIGINT,
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS review_tb (
    id BIGINT AUTO_INCREMENT,
    sender_id BIGINT,
    receiver_id BIGINT,
    project_id BIGINT,
    rating ENUM('1', '2', '3', '4', '5'),
    created_at TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS achievement_tb (
    id BIGINT AUTO_INCREMENT,
    title VARCHAR(255),
    img_url VARCHAR(255),
    description VARCHAR(255),
    progress_level INT,
    requirement_type ENUM('SEQUENCE', 'ACCUMULATE'),
    requirement_value INT,
    activity_type ENUM('FROM_ONE_STEP', 'GOOD_PERSON_GOOD_MEETUP', 'LIKE_E', 'MY_DESTINY', 'CAPTURE_FAIL_EXIST', 'RUN_AWAY_FROM_MONSTER_BALL', 'PLEASE_GIVE_ME_MOGAK', 'BRAVE_EXPLORER', 'NOMAD_CODER', 'CATCH_ME_IF_YOU_CAN', 'LEAVE_YOUR_MARK', 'WHAT_A_POPULAR_PERSON', 'CONTACT_WITH_GOD', 'FRESH_DEVELOPER'),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS chat_room_tb (
    id BIGINT AUTO_INCREMENT,
    project_id BIGINT,
    creator_id BIGINT,
    sender_id BIGINT,
    status ENUM('OPEN', 'CLOSE'),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS notification_tb (
    id BIGINT AUTO_INCREMENT,
    created_at DATETIME(6),
    detail_data VARCHAR(255),
    tag ENUM('ACHIEVEMENT', 'REQUEST_ARRIVAL', 'MATCHING_FINISHED', 'MATCHING_SUCCEEDED', 'MATCHING_FAILED', 'REVIEW_REQUEST'),
    sender_id BIGINT,
    receiver_id BIGINT,
    checked_yn TINYINT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS fcm_token_tb (
    id BIGINT,
    token VARCHAR(255),
    PRIMARY KEY (id)
);
