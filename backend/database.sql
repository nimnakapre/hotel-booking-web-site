-- Create database
CREATE DATABASE IF NOT EXISTS hotel_booking_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hotel_booking_db;

-- Table: users
CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  role ENUM('ADMIN','CLIENT') NOT NULL DEFAULT 'CLIENT',
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: rooms
CREATE TABLE IF NOT EXISTS rooms (
  id BIGINT NOT NULL AUTO_INCREMENT,
  room_number VARCHAR(10) NOT NULL,
  room_type VARCHAR(50) NOT NULL,
  capacity INT UNSIGNED NOT NULL,
  price_per_night DECIMAL(10,2) NOT NULL,
  description VARCHAR(500) NULL,
  image_name VARCHAR(255) NULL,
  is_available TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uk_rooms_room_number (room_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: contact_information
CREATE TABLE IF NOT EXISTS contact_information (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(10) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  phone VARCHAR(20) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_contact_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: bookings
CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,
  contact_id BIGINT NULL,
  check_in_date DATE NOT NULL,
  check_out_date DATE NOT NULL,
  number_of_guests INT UNSIGNED NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL,
  status ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_bookings_room FOREIGN KEY (room_id) REFERENCES rooms(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_bookings_contact FOREIGN KEY (contact_id) REFERENCES contact_information(id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT chk_booking_dates CHECK (check_out_date > check_in_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Helpful indexes for lookups and availability queries
CREATE INDEX idx_bookings_user ON bookings (user_id, check_in_date, check_out_date);
CREATE INDEX idx_bookings_room ON bookings (room_id, check_in_date, check_out_date, status);



-- Update Queries

-- Add image_name column to existing rooms table
ALTER TABLE rooms ADD COLUMN image_name VARCHAR(255) NULL AFTER description;


-- Insert Queries

-- Sample room data with images
INSERT INTO rooms (room_number, room_type, capacity, price_per_night, description, image_name, is_available) VALUES
('101', 'Deluxe Suite', 2, 150.00, 'Spacious deluxe suite with city view', 'deluxe-suite-101.jpg', 1),
('102', 'Standard Room', 2, 100.00, 'Comfortable standard room with modern amenities', 'standard-room-102.jpg', 1),
('103', 'Family Room', 4, 200.00, 'Large family room perfect for families', 'family-room-103.jpg', 1),
('201', 'Executive Suite', 2, 250.00, 'Luxurious executive suite with premium amenities', 'executive-suite-201.jpg', 1),
('202', 'Ocean View', 2, 180.00, 'Beautiful room with stunning ocean views', 'ocean-view-202.jpg', 1),
('301', 'Penthouse', 6, 500.00, 'Exclusive penthouse with panoramic city views', 'penthouse-301.jpg', 1);

