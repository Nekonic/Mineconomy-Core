CREATE TABLE users (
    name_id TEXT PRIMARY KEY,            -- 사용자 고유 ID
    balance REAL DEFAULT 0.0,            -- 보유 화폐
    uuid TEXT UNIQUE,                    -- 플레이어의 고유 UUID
    language TEXT DEFAULT 'en',          -- 사용자 언어 설정
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP -- 계정 생성 시간
);

CREATE TABLE user_settings (
    name_id TEXT NOT NULL,               -- 사용자 ID
    setting_key TEXT NOT NULL,           -- 설정 키 (예: 언어, 통화)
    setting_value TEXT,                  -- 설정 값
    PRIMARY KEY (name_id, setting_key),
    FOREIGN KEY (name_id) REFERENCES users(name_id)
);

CREATE TABLE companies (
    company_id TEXT PRIMARY KEY,         -- 회사 고유 ID
    name TEXT NOT NULL,                  -- 회사 이름
    owner_id TEXT,                       -- 현재 소유자 ID (최대 주주)
    balance REAL DEFAULT 0.0,            -- 회사 자산
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 설립 시간
    FOREIGN KEY (owner_id) REFERENCES users(name_id)
);

-- stocks : 주식 정보 저장
CREATE TABLE stocks (
    ticker TEXT PRIMARY KEY,             -- 주식 고유 식별자 (심볼)
    current_price REAL NOT NULL,         -- 현재 주식 가격
    historical_prices TEXT,              -- 과거 가격 데이터 (JSON 형식)
    volume INTEGER DEFAULT 0,            -- 거래량
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP -- 마지막 업데이트 시간
);

-- transactions : 거래 기록 관리
CREATE TABLE transactions (
    transaction_id TEXT PRIMARY KEY,     -- 거래 고유 ID
    name_id TEXT NOT NULL,               -- 거래 수행 사용자 ID
    type TEXT CHECK(type IN ('DEPOSIT', 'WITHDRAWAL', 'TRADE')) NOT NULL, -- 거래 유형
    amount REAL NOT NULL,                -- 거래 금액
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, -- 거래 발생 시간
    FOREIGN KEY (name_id) REFERENCES users(name_id)
);

-- company_shares: 주식 보유 현황
CREATE TABLE company_shares (
    company_id TEXT NOT NULL,            -- 회사 ID
    shareholder_id TEXT NOT NULL,        -- 주식 소유자 ID (개인, 회사, 은행)
    share_quantity INTEGER DEFAULT 0,    -- 보유 주식 수량
    PRIMARY KEY (company_id, shareholder_id),
    FOREIGN KEY (company_id) REFERENCES companies(company_id),
    FOREIGN KEY (shareholder_id) REFERENCES users(name_id)
);

CREATE TABLE server_stats (
    stat_key TEXT PRIMARY KEY,           -- 통계 키 (예: total_balance, active_users)
    stat_value TEXT NOT NULL,            -- 통계 값 (JSON 형식)
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP -- 마지막 업데이트 시간
);