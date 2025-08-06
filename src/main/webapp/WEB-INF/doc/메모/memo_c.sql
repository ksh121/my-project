DROP TABLE memo;

CREATE TABLE memo (
    memono        NUMBER(10)     NOT NULL PRIMARY KEY, -- 메모 번호
    title         VARCHAR(200)   NOT NULL,             -- 메모 제목
    content       CLOB           NOT NULL,              -- 메모 내용
    rdate         DATE           DEFAULT SYSDATE NOT NULL, -- 등록일
    userno        NUMBER(10)     NOT NULL,              -- 작성자

    FOREIGN KEY (userno) REFERENCES users(userno)
);




CREATE SEQUENCE memo_seq
  START WITH 1                -- 시작 번호
  INCREMENT BY 1            -- 증가값
  MAXVALUE 9999999999      -- NUMBER(10)에 맞춰서 최대값 설정
  CACHE 2                   -- 성능 개선을 위한 캐시
  NOCYCLE;                  -- 값이 다시 1부터 시작하지 않도록 설정


INSERT INTO memo (memono, content, rdate, userno, contentsno)
VALUES (memo_seq.NEXTVAL, '메모 내용', SYSDATE, 1, 1); -- 예시