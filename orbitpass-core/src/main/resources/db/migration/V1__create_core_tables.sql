CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    phone VARCHAR(50)
);

CREATE TABLE tours (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    destination VARCHAR(255) NOT NULL,
    price NUMERIC(15, 2) NOT NULL
);

CREATE TABLE tour_dates (
    id BIGSERIAL PRIMARY KEY,
    tour_id BIGINT NOT NULL,
    departure_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP NOT NULL,
    total_spots INT NOT NULL,
    booked_spots INT DEFAULT 0,

    CONSTRAINT fk_tour
        FOREIGN KEY (tour_id)
        REFERENCES tours(id)
        ON DELETE CASCADE
);

CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tour_date_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    booking_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    price NUMERIC(15, 2) NOT NULL,

    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_tour_date
        FOREIGN KEY (tour_date_id)
        REFERENCES tour_dates(id)
);
