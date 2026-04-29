-- Spring Modulith Event Publication Table
-- Required for JDBC-based event publication registry

CREATE TABLE IF NOT EXISTS event_publication (
    id UUID PRIMARY KEY,
    listener_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    serialized_event TEXT NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_event_publication_completion_date 
    ON event_publication(completion_date) 
    WHERE completion_date IS NULL;

CREATE INDEX IF NOT EXISTS idx_event_publication_publication_date 
    ON event_publication(publication_date);
