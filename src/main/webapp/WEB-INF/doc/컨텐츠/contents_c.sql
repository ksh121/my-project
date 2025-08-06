-- /src/main/webapp/WEB-INF/doc/컨텐츠/contents_c.sql
DROP TABLE contents CASCADE CONSTRAINTS; -- 자식 무시하고 삭제 가능
DROP TABLE contents;

CREATE TABLE contents(
        contentsno                            NUMBER(10)         NOT NULL         PRIMARY KEY,
        userno                              NUMBER(10)     NOT NULL , -- FK
        categono                                NUMBER(10)         NOT NULL , -- FK
        title                                 VARCHAR2(200)         NOT NULL,
        content                               CLOB                  NOT NULL,
        recom                                 NUMBER(7)         DEFAULT 0         NOT NULL,
        cnt                                   NUMBER(7)         DEFAULT 0         NOT NULL,
        replycnt                              NUMBER(7)         DEFAULT 0         NOT NULL,
        passwd                                VARCHAR2(100)         NOT NULL,
        word                                  VARCHAR2(200)         NULL ,
        rdate                                 DATE               NOT NULL,
        file1                                   VARCHAR(100)          NULL,  -- 원본 파일명 image
        file1saved                            VARCHAR(100)          NULL,  -- 저장된 파일명, image
        thumb1                              VARCHAR(100)          NULL,   -- preview image
        size1                                 NUMBER(10)      DEFAULT 0 NULL,  -- 파일 사이즈
        map                                   VARCHAR2(1000)            NULL,
        youtube                               VARCHAR2(1000)            NULL,
        mp4                                  VARCHAR2(100)            NULL,
        visible                                CHAR(1)         DEFAULT 'Y' NOT NULL,
        FOREIGN KEY (userno) REFERENCES users (userno),
        FOREIGN KEY (categono) REFERENCES catego (categono)
);

COMMENT ON TABLE contents is '컨텐츠 - 순례길';
COMMENT ON COLUMN contents.contentsno is '컨텐츠 번호';
COMMENT ON COLUMN contents.userno is '관리자 번호';
COMMENT ON COLUMN contents.categono is '카테고리 번호';
COMMENT ON COLUMN contents.title is '제목';
COMMENT ON COLUMN contents.content is '내용';
COMMENT ON COLUMN contents.recom is '추천수';
COMMENT ON COLUMN contents.cnt is '조회수';
COMMENT ON COLUMN contents.replycnt is '댓글수';
COMMENT ON COLUMN contents.passwd is '패스워드';
COMMENT ON COLUMN contents.word is '검색어';
COMMENT ON COLUMN contents.rdate is '등록일';
COMMENT ON COLUMN contents.file1 is '메인 이미지';
COMMENT ON COLUMN contents.file1saved is '실제 저장된 메인 이미지';
COMMENT ON COLUMN contents.thumb1 is '메인 이미지 Preview';
COMMENT ON COLUMN contents.size1 is '메인 이미지 크기';
COMMENT ON COLUMN contents.map is '지도';
COMMENT ON COLUMN contents.youtube is 'Youtube 영상';
COMMENT ON COLUMN contents.mp4 is '영상';
COMMENT ON COLUMN contents.visible is '출력 모드';

DROP SEQUENCE contents_seq;

CREATE SEQUENCE contents_seq
  START WITH 1                -- 시작 번호
  INCREMENT BY 1            -- 증가값
  MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
  CACHE 2                        -- 2번은 메모리에서만 계산
  NOCYCLE;                      -- 다시 1부터 생성되는 것을 방지
  

COMMIT;

SELECT COUNT(*) as cnt 
FROM contents
WHERE contentsno=14 AND passwd='fS/kjO+fuEKk06Zl7VYMhg==';

-- 추천
UPDATE contents
SET recom = recom + 1
WHERE contentsno = 1;

-- 비추천
UPDATE contents
SET recom = recom - 1
WHERE contentsno = 1;

