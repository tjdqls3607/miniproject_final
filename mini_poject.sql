CREATE TABLE member (
    member_id INT AUTO_INCREMENT PRIMARY KEY,	-- 회원 ID (기본 키)
    name VARCHAR(50) NOT NULL,	-- 회원 이름
    phone_number VARCHAR(15) UNIQUE NOT NULL,	-- 회원 전화번호 (중복 방지)
    birth_date DATE NOT NULL	-- 회원 생년월일
);

CREATE TABLE model (	
    model_id INT AUTO_INCREMENT PRIMARY KEY,	-- 휴대폰 기종 ID (기본 키)
    model_name VARCHAR(50) NOT NULL,	-- 휴대폰 기종명
    manufacturer VARCHAR(50) NOT NULL,	-- 제조사 (예: 삼성, 애플)
    price DECIMAL(10,2) NOT NULL	-- 출고가 (소수점 두 자리까지)
);

CREATE TABLE stock (
    stock_id INT AUTO_INCREMENT PRIMARY KEY,	-- 재고 ID (기본 키)
    model_id INT NOT NULL,	-- 휴대폰 기종 ID (외래 키)
    quantity INT NOT NULL,	-- 현재 재고 수량
    FOREIGN KEY (model_id) REFERENCES model(model_id) ON DELETE CASCADE	
);

CREATE TABLE membership (
    membership_id INT AUTO_INCREMENT PRIMARY KEY,	-- 멤버십 ID (기본 키)
    member_id INT NOT NULL,		-- 회원 ID (외래 키) 
    level ENUM('BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND') NOT NULL,	-- 멤버십 등급 
    discount_rate DECIMAL(5,2) NOT NULL,	-- 할인율 (% 단위, 예: 10.00 → 10%)
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

CREATE TABLE sales (
    sales_id INT AUTO_INCREMENT PRIMARY KEY,	-- 판매 ID (기본 키)
    member_id INT NOT NULL,		-- 구매 회원 ID (외래 키)
    model_id INT NOT NULL,		-- 구매한 휴대폰 기종 ID (외래 키)
    sale_date DATETIME DEFAULT CURRENT_TIMESTAMP,	-- 판매 일자 (기본값: 현재 시간)
    final_price DECIMAL(10,2) NOT NULL,	-- 최종 결제 금액 (할인 적용 후 가격)
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
    FOREIGN KEY (model_id) REFERENCES model(model_id) ON DELETE CASCADE
);

CREATE TABLE inventory_log (		-- 입출고 기록 테이블
    log_id INT AUTO_INCREMENT PRIMARY KEY,		-- 입출고 기록 ID (기본 키)
    model_id INT NOT NULL,			-- 휴대폰 기종 ID (외래 키)
    change_quantity INT NOT NULL,	-- 변동 수량 (+값: 입고, -값: 출고)	
    change_type ENUM('IN', 'OUT') NOT NULL,	-- 입출고 유형 ('IN': 입고, 'OUT': 출고)
    change_date DATETIME DEFAULT CURRENT_TIMESTAMP,	-- 입출고 발생 일자
    FOREIGN KEY (model_id) REFERENCES model(model_id) ON DELETE CASCADE
);


-- 회원 데이터 삽입
INSERT INTO member (name, phone_number, birth_date) VALUES
('Alice', '010-1111-1111', '1990-01-01'),
('Bob', '010-2222-2222', '1991-02-02'),
('Charlie', '010-3333-3333', '1992-03-03'),
('David', '010-4444-4444', '1993-04-04'),
('Eve', '010-5555-5555', '1994-05-05'),
('Frank', '010-6666-6666', '1995-06-06'),
('Grace', '010-7777-7777', '1996-07-07'),
('Henry', '010-8888-8888', '1997-08-08'),
('Irene', '010-9999-9999', '1998-09-09'),
('Jack', '010-0000-0000', '1999-10-10');

-- 휴대폰 기종 데이터 삽입
INSERT INTO model (model_name, manufacturer, price) VALUES
('iPhone 13', 'Apple', 1000000.00),
('Galaxy S21', 'Samsung', 900000.00),
('Pixel 6', 'Google', 800000.00),
('iPhone 14', 'Apple', 1200000.00),
('Galaxy S22', 'Samsung', 1100000.00),
('Pixel 7', 'Google', 950000.00),
('iPhone SE', 'Apple', 700000.00),
('Galaxy A53', 'Samsung', 600000.00),
('Pixel 5a', 'Google', 550000.00),
('iPhone 12', 'Apple', 850000.00);

-- 재고 데이터 삽입
INSERT INTO stock (model_id, quantity) VALUES
(1, 10), (2, 15), (3, 20), (4, 8), (5, 12),
(6, 18), (7, 25), (8, 30), (9, 22), (10, 14);

-- 멤버십 데이터 삽입
INSERT INTO membership (member_id, level, discount_rate) VALUES
(1, 'GOLD', 20.00),
(2, 'SILVER', 10.00),
(3, 'BRONZE', 5.00),
(4, 'GOLD', 20.00),
(5, 'SILVER', 10.00),
(6, 'BRONZE', 5.00),
(7, 'GOLD', 20.00),
(8, 'SILVER', 10.00),
(9, 'BRONZE', 5.00),
(10, 'GOLD', 20.00);