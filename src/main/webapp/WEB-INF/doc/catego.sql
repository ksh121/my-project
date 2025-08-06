DROP TABLE CATEGO;
DROP TABLE CATEGO CASCADE CONSTRAINTS; 

CREATE TABLE catego (
    CATEGONO   NUMBER(10)     NOT NULL PRIMARY KEY,
    NAME       VARCHAR2(30)   NOT NULL,
    title      VARCHAR(100)   NOT NULL,
    artist     VARCHAR(100)   NOT NULL,
    difficulty VARCHAR(30)    NOT NULL,
    VISIBLE    CHAR(1)        DEFAULT 'N' NOT NULL,
    RDATE      DATE           NOT NULL,
    SEQNO      NUMBER(5)      DEFAULT 1   NOT NULL,
    CNT        NUMBER(7)      DEFAULT 0   NOT NULL
);

COMMENT ON TABLE catego is '카테고리';
COMMENT ON COLUMN catego.CATEGONO is '카테고리 번호';
COMMENT ON COLUMN catego.NAME is '카테고리 이름';
COMMENT ON COLUMN catego.title is '곡제목';
COMMENT ON COLUMN catego.artist is '아티스트명';
COMMENT ON COLUMN catego.difficulty is '난이도';
COMMENT ON COLUMN cate.SEQNO is '출력 순서';
COMMENT ON COLUMN catego.cnt is '관련 자료수';
COMMENT ON COLUMN catego.VISIBLE is '출력 모드';
COMMENT ON COLUMN catego.RDATE is '등록일';

DROP SEQUENCE CATEGO_SEQ;

CREATE SEQUENCE CATEGO_SEQ
START WITH 1         -- 시작 번호
INCREMENT BY 1       -- 증가값
MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
CACHE 2              -- 2번은 메모리에서만 계산
NOCYCLE;             -- 다시 1부터 생성되는 것을 방지


INSERT INTO catego(categono, name, title, artist, difficulty, visible, rdate,)
VALUES(CATEGO_SEQ.nextval, '합주', 'oddities', 'the poles', '중간', 'Y', SYSDATE);

INSERT INTO catego(categono, name, title, artist, difficulty, visible, rdate)
VALUES(CATEGO_SEQ.nextval, '개인연습', 'beck', 'wave to earth', ' 어려움', 'Y', SYSDATE);


DELETE FROM catego WHERE categono=1;

SELECT categono, name, title, artist, difficulty, visible, rdate, seqno, cnt
FROM catego
ORDER BY categono ASC;

UPDATE catego SET title = '가나', artist='다라', rdate=SYSDATE
WHERE categono=5;

ALTER TABLE catego ADD SEQNO NUMBER(5) DEFAULT 1 NOT NULL;

COMMIT;

-- 카테고리 공개 설정
UPDATE catego SET visible='Y' WHERE categono=1;

-- 카테고리 비공개 설정 
UPDATE catego SET visible='N' WHERE categono=1; 

COMMIT;

-- 회원/비회원에게 공개할 카테고리 그룹(대분류) 목록
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
ORDER BY categono ASC;
  CATEGONO NAME                           TITLE                                                                                                ARTIST                                                                                               DIFFICULTY                     V RDATE                    SEQNO
---------- ------------------------------ ---------------------------------------------------------------------------------------------------- ---------------------------------------------------------------------------------------------------- ------------------------------ - ------------------- ----------
        14 ㅁㄴㅇㄹ                       ㅁㄴㅇㄹ                                                                                             ㅁㄴㅇㄹㄴㅁㅇㄹ                                                                                     ★★★☆☆                     Y 2025-03-25 05:23:23          4
        15 합주/개인연습                  ㅁㄴㅇㄹ                                                                                             ㅁㄴㅇㄹ                                                                                             ★★★☆☆                     Y 2025-03-26 01:18:15          2
        16 ㅋㅋㅋ                         ㅋㅋㅋ                                                                                               ㅋㅋㅋ                                                                                               ★★★☆☆                     Y 2025-03-26 02:46:49          3
        20 서엉                           호오

-- 공개된 대분류만 출력
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE title='--' AND visible='Y'
ORDER BY categono ASC;

-- 회원/비회원에게 공개할 카테고리(중분류) 목록
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE name='합주' AND visible='Y'
ORDER BY seqno ASC;

-- 개발 그룹의 중분류 출력(*)
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE name = '합주' AND name != '--' AND visible = 'Y'
ORDER BY seqno ASC;

COMMIT;

-- 공개된 대분류만 출력
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE artist='--' AND visible='Y'
ORDER BY categono ASC;
CATEGONO NAME                           TITLE                                                                                                ARTIST                                                                                               DIFFICULTY                     V RDATE                    SEQNO
---------- ------------------------------ ---------------------------------------------------------------------------------------------------- ---------------------------------------------------------------------------------------------------- ------------------------------ - ------------------- ----------
        21 개인                           --                                                                                                   --                                                                                                   --                             Y 2025-03-27 04:56:37          1
        22 공연                           --                                                                                                   --                                                                                                   ★★★☆☆                     Y 2025-03-27 04:56:55        301
        23 합주                           --                                                                                                   --                                                                                                   --                             Y 2025-03-27 04:56:51        101

-- 회원/비회원에게 공개할 카테고리(중분류) 목록
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE name='개인' AND visible='Y'
ORDER BY seqno ASC;
 CATEGONO NAME                           TITLE                                                                                                ARTIST                                                                                               DIFFICULTY                     V RDATE                    SEQNO
---------- ------------------------------ ---------------------------------------------------------------------------------------------------- ---------------------------------------------------------------------------------------------------- ------------------------------ - ------------------- ----------
        21 개인                           --                                                                                                   --                                                                                                   --                             Y 2025-03-27 04:56:37          1
        16 개인                           ㅋㅋㅋ                                                                                               ㅋㅋㅋ                                                                                               ★★★☆☆                     Y 2025-03-27 04:53:51          2
        14 개인                           ㅁㄴㅇㄹ                                                                                             ㅁㄴㅇㄹㄴㅁㅇㄹ                                                                                     ★★★☆☆                     Y 2025-03-27 04:54:03          4
        

-- 개발 그룹의 중분류 출력(*)
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE name = '개인' AND artist != '--' AND visible = 'Y'
ORDER BY seqno ASC;
CATEGONO NAME                           TITLE                                                                                                ARTIST                                                                                               DIFFICULTY                     V RDATE                    SEQNO
---------- ------------------------------ ---------------------------------------------------------------------------------------------------- ---------------------------------------------------------------------------------------------------- ------------------------------ - ------------------- ----------
        16 개인                           ㅋㅋㅋ                                                                                               ㅋㅋㅋ                                                                                               ★★★☆☆                     Y 2025-03-27 04:53:51          2
        14 개인                           ㅁㄴㅇㄹ                                                                                             ㅁㄴㅇㄹㄴㅁㅇㄹ                                                                                     ★★★☆☆                     Y 2025-03-27 04:54:03          4
        
SELECT categono, name FROM catego WHERE artist = '--' ORDER BY seqno ASC;
  CATEGONO NAME                          
---------- ------------------------------
        21 개인                          
        23 합주                          
        22 공연                          
        
SELECT name FROM catego WHERE artist = '--' ORDER BY seqno ASC; -- 권장
NAME                          
------------------------------
개인
합주
공연

-- SELECT DISTINCT cateno, grp FROM cate ORDER BY seqno ASC;  X

-- FWGHSRO
-- SELECT DISTINCT grp FROM cate ORDER BY seqno ASC; X        
SELECT DISTINCT name  FROM catego; 

-- 검색
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE (UPPER(name) LIKE '%' || UPPER('개인') || '%') OR (UPPER(title) LIKE '%' || UPPER('개인') || '%')
ORDER BY seqno ASC;
  CATEGONO NAME                           TITLE                                                                                                ARTIST                                                                                               DIFFICULTY                     V RDATE                    SEQNO
---------- ------------------------------ ---------------------------------------------------------------------------------------------------- ---------------------------------------------------------------------------------------------------- ------------------------------ - ------------------- ----------
        21 개인                           --                                                                                                   --                                                                                                   --                             Y 2025-03-27 04:56:37          1
        16 개인                           ㅋㅋㅋ                                                                                               ㅋㅋㅋ                                                                                               ★★★☆☆                     N 2025-03-27 04:53:51          2
        14 개인                           ㅁㄴㅇㄹ                                                                                             ㅁㄴㅇㄹㄴㅁㅇㄹ                                                                                     ★★★☆☆                     Y 2025-03-27 04:54:03          4
        
-- '카테고리 그룹'을 제외한 경우        
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE (title != '--') AND ((UPPER(name) LIKE '%' || UPPER('개인') || '%') OR (UPPER(title) LIKE '%' || UPPER('개인') || '%'))
ORDER BY seqno ASC;

COMMIT;

-- -----------------------------------------------------------------------------
-- 페이징: 정렬 -> ROWNUM -> 분할
-- -----------------------------------------------------------------------------
-- ① 정렬
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
FROM catego
WHERE (UPPER(name) LIKE '%' || UPPER('개인') || '%') OR (UPPER(title) LIKE '%' || UPPER('ㅇㅇ') || '%')
ORDER BY seqno ASC;

-- ② 정렬 -> ROWNUM
SELECT categono, name, title, artist, difficulty, visible, rdate, rownum as r, seqno
FROM (
    SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
    FROM catego
    WHERE (UPPER(name) LIKE '%' || UPPER('개인') || '%') OR (UPPER(title) LIKE '%' || UPPER('까페') || '%')
    ORDER BY seqno ASC
);

-- ③ 정렬 -> ROWNUM -> 분할
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno, r
FROM (
    SELECT categono, name, title, artist, difficulty, visible, rdate, rownum as r, seqno
    FROM (
        SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
        FROM catego
        WHERE (UPPER(name) LIKE '%' || UPPER('개인') || '%') OR (UPPER(title) LIKE '%' || UPPER('까페') || '%')
        ORDER BY seqno ASC
    )
)
WHERE r >= 1 AND r <= 3;

    CATENO grp                NAME                                  CNT      SEQNO V RDATE                        R
---------- -------------------- ------------------------------ ---------- ---------- - ------------------- ----------
         8 까페                 --                                      0          1 Y 2024-09-13 10:04:04          1
        10 까페                 강화도2                                 0         10 Y 2024-09-24 05:42:54          2
        12 까페                 김포                                    0         11 Y 2024-09-19 04:19:50          3
        
SELECT categono, name, title, artist, difficulty, visible, rdate, seqno, r
FROM (
    SELECT categono, name, title, artist, difficulty, visible, rdate, rownum as r, seqno
    FROM (
        SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
        FROM catego
        WHERE (UPPER(name) LIKE '%' || UPPER('개인') || '%') OR (UPPER(title) LIKE '%' || UPPER('까페') || '%')
        ORDER BY seqno ASC
    )
)
WHERE r >= 4 AND r <= 6;

    CATENO grp                NAME                                  CNT      SEQNO V RDATE                        R
---------- -------------------- ------------------------------ ---------- ---------- - ------------------- ----------
        15 까페                 추천                                    0         12 Y 2024-09-19 04:20:21          4
        17 까페                 남한산성                                0         15 Y 2024-09-24 04:01:35          5
        18 까페                 영종도                                  0         16 Y 2024-09-24 04:02:56          6

SELECT categono, name, title, artist, difficulty, visible, rdate, seqno, r
FROM (
    SELECT categono, name, title, artist, difficulty, visible, rdate, rownum as r, seqno
    FROM (
        SELECT categono, name, title, artist, difficulty, visible, rdate, seqno
        FROM catego
        WHERE (UPPER(name) LIKE '%' || UPPER('개인') || '%') OR (UPPER(title) LIKE '%' || UPPER('까페') || '%')
        ORDER BY seqno ASC
    )
)
WHERE r >= 7 AND r <= 9;

    CATENO grp                NAME                                  CNT      SEQNO V RDATE                        R
---------- -------------------- ------------------------------ ---------- ---------- - ------------------- ----------
        19 까페                 빵까페                                  0         19 Y 2024-09-24 04:08:50          7
        
COMMIT;

-- 목록 + 페이지(name별 합산cnt포함)
SELECT c.categono, c.name, c.title, c.artist, c.difficulty, c.visible, c.rdate, c.seqno,
       (
         CASE 
           WHEN c.title = '--' THEN 
             (SELECT COUNT(*) 
              FROM contents ct 
              WHERE ct.categono IN (
                SELECT categono FROM catego WHERE name = c.name
              )
             )
           ELSE 
             (SELECT COUNT(*) 
              FROM contents ct 
              WHERE ct.categono = c.categono
             )
         END
       ) AS cnt,
       r
FROM (
  SELECT catego.*, ROWNUM AS r
  FROM (
    SELECT * 
    FROM catego
    WHERE (UPPER(name) LIKE '%' || UPPER('검색어') || '%') 
       OR (UPPER(title) LIKE '%' || UPPER('검색어') || '%')
    ORDER BY seqno ASC
  ) catego
) c
WHERE r BETWEEN 1 AND 10;

-- 특정 categono의 cnt 갱신
UPDATE catego
SET cnt = (
  SELECT COUNT(*) FROM contents WHERE categono = 3
)
WHERE categono = 3;

-- 특정 name기준 대분류(title = '--') cnt 갱신
UPDATE catego
SET cnt = (
  SELECT COUNT(*) 
  FROM contents 
  WHERE categono IN (
    SELECT categono FROM catego WHERE name = '카테고리'
  )
)
WHERE name = '카테고리' AND title = '--';



COMMIT;

-- 갯수 전달받아 대분류 감소
UPDATE catego SET cnt = cnt - 5 WHERE name='개인연습' and name='--';