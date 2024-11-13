-- schema.sql

-- users 테이블: 모든 사용자(플레이어, 기업, 은행)의 계정 정보를 관리
CREATE TABLE IF NOT EXISTS users (
    name_id TEXT PRIMARY KEY,            -- 플레이어 또는 기업을 구분하는 고유 ID
    balance REAL DEFAULT 0.0,            -- 사용자 보유 화폐
    type TEXT CHECK(type IN ('PLAYER', 'COMPANY', 'BANK')) NOT NULL,  -- ID 유형 (PLAYER, COMPANY, BANK)
    uuid TEXT UNIQUE                     -- 플레이어의 경우 고유한 UUID, 기업이나 은행의 경우 NULL
);

-- stocks 테이블: 주식 정보 저장
CREATE TABLE IF NOT EXISTS stocks (
    ticker TEXT PRIMARY KEY,            -- 주식의 고유 식별자
    current_price REAL NOT NULL,        -- 현재 주식 가격
    historical_prices TEXT,             -- 과거 주식 가격 (JSON 형식으로 저장 가능)
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP  -- 마지막 업데이트 시간
);

-- transactions 테이블: 거래 기록 관리
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id TEXT PRIMARY KEY,    -- 거래 고유 ID
    name_id TEXT NOT NULL,              -- 거래 수행 사용자 ID (users.name_id 참조)
    ticker TEXT NOT NULL,               -- 거래 주식 ID (stocks.ticker 참조)
    amount INTEGER NOT NULL,            -- 거래된 주식 양 (양수: 매수, 음수: 매도)
    price REAL NOT NULL,                -- 거래 당시 주식 가격
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, -- 거래 발생 시간
    FOREIGN KEY (name_id) REFERENCES users(name_id),
    FOREIGN KEY (ticker) REFERENCES stocks(ticker)
);

-- user_stocks 테이블 (선택적): 각 사용자의 주식 보유 현황
CREATE TABLE IF NOT EXISTS user_stocks (
    name_id TEXT NOT NULL,              -- 사용자 ID
    ticker TEXT NOT NULL,               -- 주식 고유 식별자
    quantity INTEGER DEFAULT 0,         -- 보유 주식 수량
    PRIMARY KEY (name_id, ticker),
    FOREIGN KEY (name_id) REFERENCES users(name_id),
    FOREIGN KEY (ticker) REFERENCES stocks(ticker)
);
