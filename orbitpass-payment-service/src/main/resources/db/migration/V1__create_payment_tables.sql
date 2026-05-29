CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    payment_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    payment_type VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL
);
