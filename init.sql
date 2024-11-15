CREATE TABLE notifications (
                               notification_id SERIAL PRIMARY KEY,
                               user_id INTEGER NOT NULL,
                               type VARCHAR(100),
                               message TEXT,
                               is_read BOOLEAN DEFAULT FALSE,
                               related_entity_id INTEGER,
                               entity_type VARCHAR(50)
);

