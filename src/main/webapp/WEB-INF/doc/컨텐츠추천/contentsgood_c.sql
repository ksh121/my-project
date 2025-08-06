DROP TABLE contentsgood;

CREATE TABLE contentsgood (
    contentsgoodno    NUMBER(10)        NOT NULL PRIMARY KEY,
    rdate            DATE        NOT NULL,
    contentsno        NUMBER(10)        NOT NULL,
    userno        NUMBER(10)        NOT NULL,
    FOREIGN KEY (contentsno) REFERENCES contents(contentsno),
    FOREIGN KEY (userno) REFERENCES users(userno)
);


DROP SEQUENCE contentsgood_seq;

CREATE SEQUENCE contentsgood_seq
START WITH 1         -- 시작 번호
INCREMENT BY 1       -- 증가값
MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
CACHE 2              -- 2번은 메모리에서만 계산
NOCYCLE;             -- 다시 1부터 생성되는 것을 방지


-- 데이터 삽입
INSERT INTO contentsgood(contentsgoodno, rdate, contentsno, userno)
VALUES (contentsgood_seq.nextval, sysdate, 38, 1);

INSERT INTO contentsgood(contentsgoodno, rdate, contentsno, userno)
VALUES (contentsgood_seq.nextval, sysdate, 1, 3);

INSERT INTO contentsgood(contentsgoodno, rdate, contentsno, userno)
VALUES (contentsgood_seq.nextval, sysdate, 1, 4);

INSERT INTO contentsgood(contentsgoodno, rdate, contentsno, userno)
VALUES (contentsgood_seq.nextval, sysdate, 3, 5);

COMMIT;

-- 전체 목록
SELECT contentsgoodno, rdate, contentsno, userno
FROM contentsgood
ORDER BY contentsgoodno DESC;

CONTENTSGOODNO RDATE               CONTENTSNO   userno
-------------- ------------------- ---------- ----------
             5 2025-01-07 10:51:32          3          5
             3 2025-01-07 10:50:51          1          4
             2 2025-01-07 10:50:34          1          3
             1 2025-01-07 10:50:17          1          1

-- 조회
SELECT contentsgoodno, rdate, contentsno, userno
FROM contentsgood
WHERE contentsgoodno = 1;

-- 삭제
DELETE FROM contentsgood
WHERE contentsgoodno = 5;

commit;

SELECT COUNT(*) as cnt
FROM contentsgood
WHERE contentsno=1 AND userno=1;

       CNT
----------
         1 <-- 이미 추천을 함
         
SELECT COUNT(*) as cnt
FROM contentsgood
WHERE contentsno=2 AND userno=5;

       CNT
----------
         0 <-- 추천 안됨