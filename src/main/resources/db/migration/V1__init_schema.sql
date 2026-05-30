CREATE TABLE tickets
(
    id        UUID PRIMARY KEY,
    numbers   JSONB     NOT NULL,
    draw_date TIMESTAMP NOT NULL
);

CREATE TABLE winning_numbers
(
    id              UUID PRIMARY KEY,
    winning_numbers JSONB     NOT NULL,
    draw_date       TIMESTAMP NOT NULL UNIQUE
);

CREATE TABLE ticket_results
(
    id           UUID PRIMARY KEY,
    ticket_id    UUID      NOT NULL UNIQUE,
    user_numbers JSONB     NOT NULL,
    won_numbers  JSONB     NOT NULL,
    hit_numbers  JSONB     NOT NULL,
    hit_count    INT       NOT NULL,
    draw_date    TIMESTAMP NOT NULL,
    is_winner    BOOLEAN   NOT NULL
);

CREATE TABLE result_responses
(
    id           UUID PRIMARY KEY,
    ticket_id    UUID      NOT NULL UNIQUE,
    user_numbers JSONB     NOT NULL,
    won_numbers  JSONB     NOT NULL,
    hit_numbers  JSONB     NOT NULL,
    hit_count    INT       NOT NULL,
    draw_date    TIMESTAMP NOT NULL,
    is_winner    BOOLEAN   NOT NULL
);