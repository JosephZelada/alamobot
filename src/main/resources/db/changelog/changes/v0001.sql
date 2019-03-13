create table movie_entity (
  market_id varchar(255) NOT NULL,
  cinema_id varchar(255) NOT NULL,
  film_id varchar(255) NOT NULL,
  format_id varchar(255) NOT NULL,
  session_id BIGINT NOT NULL,
  session_date_time timestamp NOT NULL,
  session_status varchar(255),
  seats_left SMALLINT,
  watched boolean NOT NULL,
  primary key (session_id)
);

create table market_entity (
  id varchar(255) NOT NULL,
  name varchar(255),
  slug varchar(255),
  watched boolean NOT NULL,
  primary key (id)
);

create table cinema_entity (
  id varchar(255) NOT NULL,
  name varchar(255),
  slug varchar(255),
  watched boolean NOT NULL,
  market_id varchar(255),
  primary key (id)
);

create table film_entity (
  id varchar(255) NOT NULL,
  name varchar(255),
  slug varchar(255),
  watched boolean NOT NULL,
  primary key (id)
);

create table format_entity (
  id varchar(255) NOT NULL,
  name varchar(50),
  primary key (id)
);

create table film_alert_entity (
  id BIGINT NOT NULL,
  film_name varchar(255),
  preferred_cinemas varchar(255),
  earliest_showtime time,
  latest_showtime time,
  preferred_days_of_the_week varchar(255),
  override_seating_algorithm BOOLEAN,
  primary key (id)
);

create table seat_entity (
  id varchar(255) NOT NULL,
  session_id BIGINT NOT NULL,
  name varchar(255),
  row_number SMALLINT,
  seat_id SMALLINT,
  seat_number SMALLINT,
  area_index SMALLINT,
  row_index SMALLINT,
  column_index SMALLINT,
  area_id SMALLINT,
  vista_area_number SMALLINT,
  vista_row_index SMALLINT,
  vista_column_index SMALLINT,
  priority SMALLINT,
  default_price_in_cents SMALLINT,
  screen_number SMALLINT,
  seat_style VARCHAR(255),
  seat_description VARCHAR(255),
  seat_status VARCHAR(255),
  table_style VARCHAR(255),
  warning_message VARCHAR(255),
  warning_code SMALLINT,
  UNIQUE (session_id, row_index, column_index)
);