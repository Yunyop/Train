CREATE TABLE sms_records (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             phone_number VARCHAR(20) NOT NULL,
                             verification_code VARCHAR(6) NOT NULL,
                             expiration_time TIMESTAMP NOT NULL,
                             used BOOLEAN DEFAULT FALSE,
                             business_type VARCHAR(50),
                             send_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             use_time TIMESTAMP
);
