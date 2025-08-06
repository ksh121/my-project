- 테이블 구조
-- member 삭제전에 FK가 선언된 blog 테이블 먼저 삭제합니다.
DROP TABLE qna;
DROP TABLE reply;
DROP TABLE users;
-- 제약 조건과 함께 삭제(제약 조건이 있어도 삭제됨, 권장하지 않음.)
DROP TABLE users CASCADE CONSTRAINTS; 
 
CREATE TABLE users (
  userno NUMBER(10) NOT NULL, -- 회원 번호, 레코드를 구분하는 컬럼 
  id         VARCHAR(30)   NOT NULL UNIQUE, -- 이메일(아이디), 중복 안됨, 레코드를 구분 
  passwd     VARCHAR(200)   NOT NULL, -- 패스워드, 영숫자 조합, 암호화
  uname      VARCHAR(30)   NOT NULL, -- 성명, 한글 10자 저장 가능
  phone            VARCHAR(14)   NOT NULL, -- 전화번호
  postal_code     VARCHAR(5)        NULL, -- 우편번호, 12345
  address1    VARCHAR(80)       NULL, -- 주소 1
  address2    VARCHAR(50)       NULL, -- 주소 2
  udate       DATE             NOT NULL, -- 가입일    
  grade        NUMBER(2)     NOT NULL, -- 등급(1~10: 관리자, 11~20: 회원, 40~49: 정지 회원, 99: 탈퇴 회원)
  PRIMARY KEY (userno)               -- 한번 등록된 값은 중복 안됨
);
 
COMMENT ON TABLE USERS is '회원';
COMMENT ON COLUMN USERS.USERNO is '회원 번호';
COMMENT ON COLUMN USERS.ID is '아이디';
COMMENT ON COLUMN USERS.PASSWD is '패스워드';
COMMENT ON COLUMN USERS.UNAME is '성명';
COMMENT ON COLUMN USERS.PHONE is '전화번호';
COMMENT ON COLUMN USERS.POSTAL_CODE is '우편번호';
COMMENT ON COLUMN USERS.ADDRESS1 is '주소1';
COMMENT ON COLUMN USERS.ADDRESS2 is '주소2';
COMMENT ON COLUMN USERS.UDATE is '가입일';
COMMENT ON COLUMN USERS.GRADE is '등급';

DROP SEQUENCE user_seq;

CREATE SEQUENCE user_seq
  START WITH 1              -- 시작 번호
  INCREMENT BY 1          -- 증가값
  MAXVALUE 9999999999 -- 최대값: 9999999 --> NUMBER(7) 대응
  CACHE 2                       -- 2번은 메모리에서만 계산
  NOCYCLE;                     -- 다시 1부터 생성되는 것을 방지
  
COMMIT;


  1. 등록
 
1) id 중복 확인(null 값을 가지고 있으면 count에서 제외됨)
SELECT COUNT(id)
FROM users
WHERE id='user1';

SELECT COUNT(id) as cnt
FROM users
WHERE id='user1';
 
 cnt
 ---
   0   ← 중복 되지 않음.
   
2) 등록
-- 회원 관리용 계정, Q/A 용 계정
INSERT INTO users(userno, id, passwd, uname, phone, postal_code,
                                address1, address2, udate, grade)
VALUES (user_seq.nextval, 'admin', '1234', '통합 관리자', '000-0000-0000', '12345',
             '서울시 종로구', '관철동', sysdate, 1);
             
INSERT INTO users(userno, id, passwd, uname, phone, postal_code,
                                address1, address2, udate, grade)
VALUES (user_seq.nextval, 'qnaadmin', '1234', '질문답변관리자', '000-0000-0000', '12345',
             '서울시 종로구', '관철동', sysdate, 1);
 
-- 개인 회원 테스트 계정
INSERT INTO users(userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade)
VALUES (user_seq.nextval, 'user1@gmail.com', '1234', '왕눈이', '000-0000-0000', '12345', '서울시 종로구', '관철동', sysdate, 15);
 
INSERT INTO users(userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade)
VALUES (user_seq.nextval, 'user2@gmail.com', '1234', '아로미', '000-0000-0000', '12345', '서울시 종로구', '관철동', sysdate, 15);
 
INSERT INTO users(userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade)
VALUES (user_seq.nextval, 'user3@gmail.com', '1234', '투투투', '000-0000-0000', '12345', '서울시 종로구', '관철동', sysdate, 15);
 
-- 부서별(그룹별) 공유 회원 기준
INSERT INTO users(userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade)
VALUES (user_seq.nextval, 'team1', '1234', '개발팀', '000-0000-0000', '12345', '서울시 종로구', '관철동', sysdate, 15);
 
INSERT INTO users(userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade)
VALUES (user_seq.nextval, 'team2', '1234', '웹퍼블리셔팀', '000-0000-0000', '12345', '서울시 종로구', '관철동', sysdate, 15);
 
INSERT INTO users(userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade)
VALUES (user_seq.nextval, 'team3', '1234', '디자인팀', '000-0000-0000', '12345', '서울시 종로구', '관철동', sysdate, 15);

COMMIT;

 
2. 목록
- 검색을 하지 않는 경우, 전체 목록 출력
 
SELECT userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade
FROM users
ORDER BY grade ASC, id ASC;
     
     
3. 조회
 
1) 사원 정보 조회
SELECT userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade
FROM users
WHERE userno = 1;

SELECT userno, id, passwd, uname, phone, postal_code, address1, address2, udate, grade
FROM users
WHERE id = 'user1@gmail.com';
 
    
4. 수정, PK: 변경 못함, UNIQUE: 변경을 권장하지 않음(id)
UPDATE users 
SET uname='조인성', phone='111-1111-1111', postal_code='00000',
    address1='강원도', address2='홍천군', grade=14
WHERE userno=12;

COMMIT;

 
5. 삭제
1) 모두 삭제
DELETE FROM users;
 
2) 특정 회원 삭제
DELETE FROM users
WHERE userno=12;

COMMIT;

 
6. 로그인
SELECT COUNT(userno) as cnt
FROM users
WHERE id='user1@gmail.com' AND passwd='1234';
 cnt
 ---
   0
   
   
7. 패스워드 변경
1) 패스워드 검사
SELECT COUNT(userno) as cnt
FROM users
WHERE userno=1 AND passwd='1234';
 
2) 패스워드 수정
UPDATE users
SET passwd='0000'
WHERE userno=1;

COMMIT;

8. 회원 등급 변경
-- 정지 회원
UPDATE users
SET grade = 30
WHERE userno=5;

-- 탈퇴 회원
UPDATE users
SET grade = 40
WHERE userno=9;

COMMIT;