CREATE TABLE users
(
    name_id  TEXT UNIQUE,                -- 사용자 고유 ID
    balance  REAL NOT NULL DEFAULT 0.0,  -- 보유 화폐
    uuid     TEXT PRIMARY KEY,           -- 플레이어의 고유 UUID
    language TEXT NOT NULL DEFAULT 'en', -- 사용자 언어 설정
);

CREATE TABLE companies
(
    company_id TEXT PRIMARY KEY AUTOINCREMENT,              -- 회사 고유 ID
    name       TEXT     NOT NULL,                           -- 회사 이름
    owner_id   TEXT     NOT NULL,                           -- 현재 소유자 ID (최대 주주)
    balance    REAL     NOT NULL DEFAULT 0.0,               -- 회사 자산
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 설립 시간
    FOREIGN KEY (owner_id) REFERENCES users (name_id)
);

-- stocks : 주식 정보 저장
CREATE TABLE stocks
(
    ticker            TEXT PRIMARY KEY UNIQUE,                    -- 주식 고유 식별자 (심볼)
    current_price     REAL     NOT NULL DEFAULT 0.0,              -- 현재 주식 가격
    historical_prices TEXT,                                       -- 과거 가격 데이터 (JSON 형식)
    volume            INTEGER           DEFAULT 0,                -- 거래량
    last_updated      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP -- 마지막 업데이트 시간
        FOREIGN KEY (company_id) REFERENCES companies(company_id) -- 회사 ID
);

CREATE TABLE stock_prices
(
    trade_id  INTEGER PRIMARY KEY AUTOINCREMENT,           -- 거래 고유 ID
    stock_id  INTEGER  NOT NULL,                           -- references stocks(stock_id)
    ticker    TEXT     NOT NULL,                           -- 주식 고유 식별자 (심볼)
    price     REAL     NOT NULL,                           -- 주식 가격
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 가격 업데이트 시간
    PRIMARY KEY (ticker, timestamp),
    FOREIGN KEY (ticker) REFERENCES stocks (ticker)
);

-- transactions : 거래 기록 관리
CREATE TABLE transactions
(
    transaction_id   TEXT PRIMARY KEY AUTOINCREMENT,     -- 거래 고유 ID
    from_entity_id   TEXT,                               -- Could store player_id or company_id if from_type = 'PLAYER'/'COMPANY'
    from_entity_type TEXT CHECK (from_entity_type IN ('PLAYER', 'COMPANY')),
    to_entity_id     TEXT,
    to_entity_type   TEXT CHECK (to_entity_type IN ('PLAYER', 'COMPANY')),
    amount           REAL NOT NULL,                      -- 거래 금액
    transaction_type TEXT NOT NULL,                      -- 거래 유형 (TAX, PAYMENT, PURCHASE, SALE)
    timestamp        DATETIME DEFAULT CURRENT_TIMESTAMP, -- 거래 발생 시간
    FOREIGN KEY (name_id) REFERENCES users (name_id)
);

-- company_shares: 주식 보유 현황
CREATE TABLE company_shares
(
    company_id     TEXT NOT NULL,     -- 회사 ID
    shareholder_id TEXT NOT NULL,     -- 주식 소유자 ID (개인, 회사, 은행)
    share_quantity INTEGER DEFAULT 0, -- 보유 주식 수량
    PRIMARY KEY (company_id, shareholder_id),
    FOREIGN KEY (company_id) REFERENCES companies (company_id),
    FOREIGN KEY (shareholder_id) REFERENCES users (name_id)
);

CREATE TABLE server_stats
(
    stat_key   TEXT PRIMARY KEY,                  -- 통계 키 (예: total_balance, active_users)
    stat_value TEXT NOT NULL,                     -- 통계 값 (JSON 형식)
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP -- 마지막 업데이트 시간
);