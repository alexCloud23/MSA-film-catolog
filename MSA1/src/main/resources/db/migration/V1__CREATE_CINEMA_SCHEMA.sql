CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    birth_date DATE,
    created_at DATE,
    updated_at DATE
);

CREATE TABLE films (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    duration_minutes INT NOT NULL CHECK (duration_minutes >= 1),
    genre VARCHAR(255),
    rating FLOAT CHECK (rating >= 0 AND rating <= 10),
    release_year INT CHECK (release_year >= 1800 AND release_year <= 2100),
    created_at DATE,
    updated_at DATE
);

CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    film_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    text TEXT NOT NULL,
    created_at DATE,
    updated_at DATE,
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_films_genre ON films(genre);
CREATE INDEX idx_films_year ON films(release_year);
CREATE INDEX idx_reviews_film ON reviews(film_id);
CREATE INDEX idx_reviews_author ON reviews(author_id);
