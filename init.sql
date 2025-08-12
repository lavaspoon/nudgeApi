-- TB_LMS_DEPT 테이블 생성
CREATE TABLE TB_LMS_DEPT (
                             id SERIAL PRIMARY KEY,
                             dept_name VARCHAR(255) NOT NULL,
                             parent_dept_id INT,
                             depth INT NOT NULL,
                             use_yn CHAR(1) DEFAULT 'Y',
                             FOREIGN KEY (parent_dept_id) REFERENCES TB_LMS_DEPT(id)
);

-- tb_lms_member 테이블 생성 (실제 스키마에 맞게)
CREATE TABLE tb_lms_member (
                               user_id VARCHAR(255) NOT NULL PRIMARY KEY,
                               com_code VARCHAR(255),
                               company VARCHAR(255),
                               dept_idx INTEGER,
                               dept_name VARCHAR(255),
                               email VARCHAR(255),
                               mb_name VARCHAR(255),
                               mb_position INTEGER,
                               mb_position_name VARCHAR(255),
                               mb_status INTEGER,
                               revel VARCHAR(255),
                               use_yn VARCHAR(255),
                               skid VARCHAR(255) UNIQUE
);

-- TB_LMS_DEPT 계층형 구조 데이터 삽입
-- 1단계: 최상위 부서 (전주유선)
INSERT INTO TB_LMS_DEPT (dept_name, parent_dept_id, depth, use_yn) VALUES
    ('전주유선', NULL, 0, 'Y');

-- 2단계: CS실, 로열실
INSERT INTO TB_LMS_DEPT (dept_name, parent_dept_id, depth, use_yn) VALUES
                                                                       ('CS실', 1, 1, 'Y'),
                                                                       ('로열실', 1, 1, 'Y');

-- 3단계: CS 마케팅 1실~7실, 로열 1실~4실
INSERT INTO TB_LMS_DEPT (dept_name, parent_dept_id, depth, use_yn) VALUES
                                                                       ('CS 마케팅 1실', 2, 2, 'Y'),
                                                                       ('CS 마케팅 2실', 2, 2, 'Y'),
                                                                       ('CS 마케팅 3실', 2, 2, 'Y'),
                                                                       ('CS 마케팅 4실', 2, 2, 'Y'),
                                                                       ('CS 마케팅 5실', 2, 2, 'Y'),
                                                                       ('CS 마케팅 6실', 2, 2, 'Y'),
                                                                       ('CS 마케팅 7실', 2, 2, 'Y'),
                                                                       ('로열 1실', 3, 2, 'Y'),
                                                                       ('로열 2실', 3, 2, 'Y'),
                                                                       ('로열 3실', 3, 2, 'Y'),
                                                                       ('로열 4실', 3, 2, 'Y');

-- tb_lms_member 데이터 삽입

-- 전주유선 센터장 (ID: 1) - com_code: 65
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
    ('center001', '65', '전주유선', 1, '전주유선', 'center@jjys.co.kr', '김센터장', 1, '센터장', 1, 'A', 'Y', 'ROLE001');

-- CS실 그룹장 (ID: 2) - com_code: 45
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
    ('cs_group001', '45', '전주유선', 2, 'CS실', 'cs.group@jjys.co.kr', '이CS그룹장', 2, '그룹장', 1, 'A', 'Y', 'ROLE002');

-- 로열실 그룹장 (ID: 3) - com_code: 45
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
    ('royal_group001', '45', '전주유선', 3, '로열실', 'royal.group@jjys.co.kr', '박로열그룹장', 2, '그룹장', 1, 'A', 'Y', 'ROLE003');

-- CS 마케팅 1실 (ID: 4) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm1_chief01', '35', '전주유선', 4, 'CS 마케팅 1실', 'csm1.chief01@jjys.co.kr', '김마케팅1실장A', 3, '실장', 1, 'B', 'Y', 'ROLE101'),
                                                                                                                                                               ('csm1_chief02', '35', '전주유선', 4, 'CS 마케팅 1실', 'csm1.chief02@jjys.co.kr', '이마케팅1실장B', 3, '실장', 1, 'B', 'Y', 'ROLE102'),
                                                                                                                                                               ('csm1_chief03', '35', '전주유선', 4, 'CS 마케팅 1실', 'csm1.chief03@jjys.co.kr', '박마케팅1실장C', 3, '실장', 1, 'B', 'Y', 'ROLE103');

-- CS 마케팅 1실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm1_mgr01', '20', '전주유선', 4, 'CS 마케팅 1실', 'csm1.mgr01@jjys.co.kr', '김상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1201'),
                                                                                                                                                               ('csm1_mgr02', '20', '전주유선', 4, 'CS 마케팅 1실', 'csm1.mgr02@jjys.co.kr', '이상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1202'),
                                                                                                                                                               ('csm1_mgr03', '20', '전주유선', 4, 'CS 마케팅 1실', 'csm1.mgr03@jjys.co.kr', '박상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1203');

-- CS 마케팅 2실 (ID: 5) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm2_chief01', '35', '전주유선', 5, 'CS 마케팅 2실', 'csm2.chief01@jjys.co.kr', '최마케팅2실장A', 3, '실장', 1, 'B', 'Y', 'ROLE201'),
                                                                                                                                                               ('csm2_chief02', '35', '전주유선', 5, 'CS 마케팅 2실', 'csm2.chief02@jjys.co.kr', '정마케팅2실장B', 3, '실장', 1, 'B', 'Y', 'ROLE202'),
                                                                                                                                                               ('csm2_chief03', '35', '전주유선', 5, 'CS 마케팅 2실', 'csm2.chief03@jjys.co.kr', '조마케팅2실장C', 3, '실장', 1, 'B', 'Y', 'ROLE203');

-- CS 마케팅 2실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm2_mgr01', '20', '전주유선', 5, 'CS 마케팅 2실', 'csm2.mgr01@jjys.co.kr', '조상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1301'),
                                                                                                                                                               ('csm2_mgr02', '20', '전주유선', 5, 'CS 마케팅 2실', 'csm2.mgr02@jjys.co.kr', '신상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1302'),
                                                                                                                                                               ('csm2_mgr03', '20', '전주유선', 5, 'CS 마케팅 2실', 'csm2.mgr03@jjys.co.kr', '문상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1303');

-- CS 마케팅 3실 (ID: 6) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm3_chief01', '35', '전주유선', 6, 'CS 마케팅 3실', 'csm3.chief01@jjys.co.kr', '신마케팅3실장A', 3, '실장', 1, 'B', 'Y', 'ROLE301'),
                                                                                                                                                               ('csm3_chief02', '35', '전주유선', 6, 'CS 마케팅 3실', 'csm3.chief02@jjys.co.kr', '문마케팅3실장B', 3, '실장', 1, 'B', 'Y', 'ROLE302'),
                                                                                                                                                               ('csm3_chief03', '35', '전주유선', 6, 'CS 마케팅 3실', 'csm3.chief03@jjys.co.kr', '송마케팅3실장C', 3, '실장', 1, 'B', 'Y', 'ROLE303');

-- CS 마케팅 3실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm3_mgr01', '20', '전주유선', 6, 'CS 마케팅 3실', 'csm3.mgr01@jjys.co.kr', '서상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1401'),
                                                                                                                                                               ('csm3_mgr02', '20', '전주유선', 6, 'CS 마케팅 3실', 'csm3.mgr02@jjys.co.kr', '임상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1402'),
                                                                                                                                                               ('csm3_mgr03', '20', '전주유선', 6, 'CS 마케팅 3실', 'csm3.mgr03@jjys.co.kr', '노상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1403');

-- CS 마케팅 4실 (ID: 7) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm4_chief01', '35', '전주유선', 7, 'CS 마케팅 4실', 'csm4.chief01@jjys.co.kr', '배마케팅4실장A', 3, '실장', 1, 'B', 'Y', 'ROLE401'),
                                                                                                                                                               ('csm4_chief02', '35', '전주유선', 7, 'CS 마케팅 4실', 'csm4.chief02@jjys.co.kr', '서마케팅4실장B', 3, '실장', 1, 'B', 'Y', 'ROLE402'),
                                                                                                                                                               ('csm4_chief03', '35', '전주유선', 7, 'CS 마케팅 4실', 'csm4.chief03@jjys.co.kr', '임마케팅4실장C', 3, '실장', 1, 'B', 'Y', 'ROLE403');

-- CS 마케팅 4실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm4_mgr01', '20', '전주유선', 7, 'CS 마케팅 4실', 'csm4.mgr01@jjys.co.kr', '구상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1501'),
                                                                                                                                                               ('csm4_mgr02', '20', '전주유선', 7, 'CS 마케팅 4실', 'csm4.mgr02@jjys.co.kr', '홍상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1502'),
                                                                                                                                                               ('csm4_mgr03', '20', '전주유선', 7, 'CS 마케팅 4실', 'csm4.mgr03@jjys.co.kr', '전상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1503');

-- CS 마케팅 5실 (ID: 8) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm5_chief01', '35', '전주유선', 8, 'CS 마케팅 5실', 'csm5.chief01@jjys.co.kr', '노마케팅5실장A', 3, '실장', 1, 'B', 'Y', 'ROLE501'),
                                                                                                                                                               ('csm5_chief02', '35', '전주유선', 8, 'CS 마케팅 5실', 'csm5.chief02@jjys.co.kr', '하마케팅5실장B', 3, '실장', 1, 'B', 'Y', 'ROLE502'),
                                                                                                                                                               ('csm5_chief03', '35', '전주유선', 8, 'CS 마케팅 5실', 'csm5.chief03@jjys.co.kr', '유마케팅5실장C', 3, '실장', 1, 'B', 'Y', 'ROLE503');

-- CS 마케팅 5실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm5_mgr01', '20', '전주유선', 8, 'CS 마케팅 5실', 'csm5.mgr01@jjys.co.kr', '강상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1601'),
                                                                                                                                                               ('csm5_mgr02', '20', '전주유선', 8, 'CS 마케팅 5실', 'csm5.mgr02@jjys.co.kr', '윤상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1602'),
                                                                                                                                                               ('csm5_mgr03', '20', '전주유선', 8, 'CS 마케팅 5실', 'csm5.mgr03@jjys.co.kr', '오상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1603');

-- CS 마케팅 6실 (ID: 9) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm6_chief01', '35', '전주유선', 9, 'CS 마케팅 6실', 'csm6.chief01@jjys.co.kr', '구마케팅6실장A', 3, '실장', 1, 'B', 'Y', 'ROLE601'),
                                                                                                                                                               ('csm6_chief02', '35', '전주유선', 9, 'CS 마케팅 6실', 'csm6.chief02@jjys.co.kr', '홍마케팅6실장B', 3, '실장', 1, 'B', 'Y', 'ROLE602'),
                                                                                                                                                               ('csm6_chief03', '35', '전주유선', 9, 'CS 마케팅 6실', 'csm6.chief03@jjys.co.kr', '전마케팅6실장C', 3, '실장', 1, 'B', 'Y', 'ROLE603');

-- CS 마케팅 6실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm6_mgr01', '20', '전주유선', 9, 'CS 마케팅 6실', 'csm6.mgr01@jjys.co.kr', '고상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1701'),
                                                                                                                                                               ('csm6_mgr02', '20', '전주유선', 9, 'CS 마케팅 6실', 'csm6.mgr02@jjys.co.kr', '안상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1702'),
                                                                                                                                                               ('csm6_mgr03', '20', '전주유선', 9, 'CS 마케팅 6실', 'csm6.mgr03@jjys.co.kr', '차상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1703');

-- CS 마케팅 7실 (ID: 10) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm7_chief01', '35', '전주유선', 10, 'CS 마케팅 7실', 'csm7.chief01@jjys.co.kr', '황마케팅7실장A', 3, '실장', 1, 'B', 'Y', 'ROLE701'),
                                                                                                                                                               ('csm7_chief02', '35', '전주유선', 10, 'CS 마케팅 7실', 'csm7.chief02@jjys.co.kr', '민마케팅7실장B', 3, '실장', 1, 'B', 'Y', 'ROLE702'),
                                                                                                                                                               ('csm7_chief03', '35', '전주유선', 10, 'CS 마케팅 7실', 'csm7.chief03@jjys.co.kr', '강마케팅7실장C', 3, '실장', 1, 'B', 'Y', 'ROLE703');

-- CS 마케팅 7실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('csm7_mgr01', '20', '전주유선', 10, 'CS 마케팅 7실', 'csm7.mgr01@jjys.co.kr', '지상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1801'),
                                                                                                                                                               ('csm7_mgr02', '20', '전주유선', 10, 'CS 마케팅 7실', 'csm7.mgr02@jjys.co.kr', '양상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1802'),
                                                                                                                                                               ('csm7_mgr03', '20', '전주유선', 10, 'CS 마케팅 7실', 'csm7.mgr03@jjys.co.kr', '우상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1803');

-- 로열 1실 (ID: 11) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('royal1_chief01', '35', '전주유선', 11, '로열 1실', 'royal1.chief01@jjys.co.kr', '김로열1실장A', 3, '실장', 1, 'B', 'Y', 'ROLE801'),
                                                                                                                                                               ('royal1_chief02', '35', '전주유선', 11, '로열 1실', 'royal1.chief02@jjys.co.kr', '이로열1실장B', 3, '실장', 1, 'B', 'Y', 'ROLE802'),
                                                                                                                                                               ('royal1_chief03', '35', '전주유선', 11, '로열 1실', 'royal1.chief03@jjys.co.kr', '박로열1실장C', 3, '실장', 1, 'B', 'Y', 'ROLE803');

-- 로열 1실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('royal1_mgr01', '20', '전주유선', 11, '로열 1실', 'royal1.mgr01@jjys.co.kr', '월상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1901'),
                                                                                                                                                               ('royal1_mgr02', '20', '전주유선', 11, '로열 1실', 'royal1.mgr02@jjys.co.kr', '유상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1902'),
                                                                                                                                                               ('royal1_mgr03', '20', '전주유선', 11, '로열 1실', 'royal1.mgr03@jjys.co.kr', '윤상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE1903');

-- 로열 2실 (ID: 12) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('royal2_chief01', '35', '전주유선', 12, '로열 2실', 'royal2.chief01@jjys.co.kr', '최로열2실장A', 3, '실장', 1, 'B', 'Y', 'ROLE901'),
                                                                                                                                                               ('royal2_chief02', '35', '전주유선', 12, '로열 2실', 'royal2.chief02@jjys.co.kr', '정로열2실장B', 3, '실장', 1, 'B', 'Y', 'ROLE902'),
                                                                                                                                                               ('royal2_chief03', '35', '전주유선', 12, '로열 2실', 'royal2.chief03@jjys.co.kr', '조로열2실장C', 3, '실장', 1, 'B', 'Y', 'ROLE903');

-- 로열 2실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('royal2_mgr01', '20', '전주유선', 12, '로열 2실', 'royal2.mgr01@jjys.co.kr', '임상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2001'),
                                                                                                                                                               ('royal2_mgr02', '20', '전주유선', 12, '로열 2실', 'royal2.mgr02@jjys.co.kr', '장상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2002'),
                                                                                                                                                               ('royal2_mgr03', '20', '전주유선', 12, '로열 2실', 'royal2.mgr03@jjys.co.kr', '전상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2003');

-- 로열 3실 (ID: 13) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('royal3_chief01', '35', '전주유선', 13, '로열 3실', 'royal3.chief01@jjys.co.kr', '신로열3실장A', 3, '실장', 1, 'B', 'Y', 'ROLE1001'),
                                                                                                                                                               ('royal3_chief02', '35', '전주유선', 13, '로열 3실', 'royal3.chief02@jjys.co.kr', '문로열3실장B', 3, '실장', 1, 'B', 'Y', 'ROLE1002'),
                                                                                                                                                               ('royal3_chief03', '35', '전주유선', 13, '로열 3실', 'royal3.chief03@jjys.co.kr', '송로열3실장C', 3, '실장', 1, 'B', 'Y', 'ROLE1003');

-- 로열 3실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('royal3_mgr01', '20', '전주유선', 13, '로열 3실', 'royal3.mgr01@jjys.co.kr', '주상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2101'),
                                                                                                                                                               ('royal3_mgr02', '20', '전주유선', 13, '로열 3실', 'royal3.mgr02@jjys.co.kr', '진상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2102'),
                                                                                                                                                               ('royal3_mgr03', '20', '전주유선', 13, '로열 3실', 'royal3.mgr03@jjys.co.kr', '차상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2103');

-- 로열 4실 (ID: 14) - 실장들 com_code: 35
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('royal4_chief01', '35', '전주유선', 14, '로열 4실', 'royal4.chief01@jjys.co.kr', '배로열4실장A', 3, '실장', 1, 'B', 'Y', 'ROLE1101'),
                                                                                                                                                               ('royal4_chief02', '35', '전주유선', 14, '로열 4실', 'royal4.chief02@jjys.co.kr', '서로열4실장B', 3, '실장', 1, 'B', 'Y', 'ROLE1102'),
                                                                                                                                                               ('royal4_chief03', '35', '전주유선', 14, '로열 4실', 'royal4.chief03@jjys.co.kr', '임로열4실장C', 3, '실장', 1, 'B', 'Y', 'ROLE1103');

-- 로열 4실 상담매니저들 com_code: 20
INSERT INTO tb_lms_member (user_id, com_code, company, dept_idx, dept_name, email, mb_name, mb_position, mb_position_name, mb_status, revel, use_yn, skid) VALUES
                                                                                                                                                               ('royal4_mgr01', '20', '전주유선', 14, '로열 4실', 'royal4.mgr01@jjys.co.kr', '탁상담매니저1', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2201'),
                                                                                                                                                               ('royal4_mgr02', '20', '전주유선', 14, '로열 4실', 'royal4.mgr02@jjys.co.kr', '편상담매니저2', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2202'),
                                                                                                                                                               ('royal4_mgr03', '20', '전주유선', 14, '로열 4실', 'royal4.mgr03@jjys.co.kr', '표상담매니저3', 4, '상담매니저', 1, 'C', 'Y', 'ROLE2203');

-- 데이터 확인 쿼리
-- 계층형 구조 확인
SELECT
    d1.id,
    d1.dept_name,
    d1.depth,
    d2.dept_name AS parent_dept_name
FROM TB_LMS_DEPT d1
         LEFT JOIN TB_LMS_DEPT d2 ON d1.parent_dept_id = d2.id
ORDER BY d1.depth, d1.id;

-- 각 부서별 직원 수 및 권한별 현황 확인
SELECT
    d.dept_name,
    COUNT(m.user_id) as member_count,
    STRING_AGG(DISTINCT m.com_code, ', ') as com_codes,
    STRING_AGG(DISTINCT m.mb_position_name, ', ') as positions
FROM TB_LMS_DEPT d
         LEFT JOIN tb_lms_member m ON d.id = m.dept_idx
GROUP BY d.id, d.dept_name
ORDER BY d.depth, d.id;

-- 권한별 사용자 현황
SELECT
    com_code,
    mb_position_name,
    COUNT(*) as count,
    STRING_AGG(mb_name, ', ' ORDER BY mb_name) as members
FROM tb_lms_member
GROUP BY com_code, mb_position_name
ORDER BY com_code DESC;

-- TB_NUDGE_DATA 테이블 생성
CREATE TABLE TB_NUDGE_DATA (
                               id BIGSERIAL PRIMARY KEY,
                               consulation_date VARCHAR(255),
                               user_id VARCHAR(255),
                               customer_inquiry TEXT,
                               nudge_yn CHAR(1),
                               marketing_type VARCHAR(255),
                               marketing_message TEXT,
                               customer_consent_yn CHAR(1),
                               inappropriate_response_yn CHAR(1),
                               inappropriate_response_message TEXT
);

-- TB_NUDGE_DATA 샘플 데이터 삽입 (8월 1일~11일 영업일 기준)
-- 영업일: 8월 1일(금), 8월 4일(월), 8월 5일(화), 8월 6일(수), 8월 7일(목), 8월 8일(금), 8월 11일(월)

-- 8월 1일(금) 데이터
-- CS 마케팅 1실 실장들
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-01', 'csm1_chief01', '인터넷 속도가 느려서 문의드립니다', 'Y', 'GIGA 전환', 'GIGA 요금제로 변경하시면 더 빠른 속도를 경험하실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_chief01', '월 요금이 너무 비싸요', 'Y', 'CRM 전환', 'CRM 할인 혜택을 적용해드릴 수 있습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_chief02', 'TV 채널 추가 문의', 'Y', 'TDS 전환', 'TDS 패키지 상품으로 더 많은 채널을 시청하실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_chief02', '서비스 해지 문의', 'N', NULL, NULL, 'N', 'Y', '해지하시면 다시 가입이 어려울 수 있습니다'),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_chief03', '인터넷 연결 불량', 'Y', 'GIGA 전환', 'GIGA 서비스로 안정적인 연결을 제공해드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_chief03', '고객센터 연결 지연', 'N', NULL, NULL, 'N', 'N', NULL);

-- CS 마케팅 1실 상담매니저들
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-01', 'csm1_mgr01', '요금제 변경 문의', 'Y', 'CRM 전환', 'CRM 프로모션을 통해 더 저렴한 요금으로 이용 가능합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_mgr01', 'WiFi 비밀번호 분실', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_mgr02', '속도 업그레이드 문의', 'Y', 'GIGA 전환', 'GIGA 요금제로 업그레이드하시면 최고 속도를 경험하실 수 있습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_mgr02', '이사 시 서비스 이전', 'Y', 'TDS 전환', 'TDS 서비스로 이전하시면 추가 혜택을 받으실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_mgr03', '장애신고', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-01', 'csm1_mgr03', '청구서 문의', 'Y', 'CRM 전환', 'CRM 서비스로 청구서를 더 편리하게 관리하실 수 있습니다', 'Y', 'N', NULL);

-- 8월 2일(토), 8월 3일(일) - 주말, 영업일 아님

-- 8월 4일(월) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-04', 'csm2_chief01', '인터넷 설치 문의', 'Y', 'GIGA 전환', '신규 설치 시 GIGA 서비스로 시작하시면 특별 혜택이 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-04', 'csm2_chief01', '결합상품 문의', 'Y', 'TDS 전환', 'TDS 결합상품으로 더욱 경제적으로 이용하실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-04', 'csm2_mgr01', 'VOD 서비스 문의', 'Y', 'CRM 전환', 'CRM VOD 패키지로 더 많은 콘텐츠를 즐기실 수 있습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-04', 'csm2_mgr01', '요금 할인 문의', 'N', NULL, NULL, 'N', 'N', NULL);

-- 8월 5일(화) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-05', 'csm3_chief01', '인터넷 속도 불만', 'Y', 'GIGA 전환', 'GIGA 프리미엄으로 업그레이드하시면 문제가 해결됩니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-05', 'csm3_chief01', 'TV 서비스 추가', 'Y', 'TDS 전환', 'TDS 프리미엄 채널을 추가하시겠어요?', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-05', 'royal1_chief01', '로열 고객 전용 서비스 문의', 'Y', 'CRM 전환', 'CRM 로열 등급 고객님께는 특별한 혜택을 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-05', 'royal1_chief01', 'VIP 서비스 문의', 'Y', 'GIGA 전환', 'GIGA VIP 서비스로 최고의 품질을 경험하세요', 'N', 'N', NULL);

-- 8월 6일(수) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-06', 'csm4_mgr01', '게임 랙 문제', 'Y', 'GIGA 전환', '게이머를 위한 GIGA 게임 전용 요금제를 추천드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-06', 'csm4_mgr01', '스트리밍 서비스 끊김', 'Y', 'TDS 전환', 'TDS 스트리밍 최적화 서비스로 끊김 없이 시청하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-06', 'royal2_mgr01', '프리미엄 고객 관리', 'Y', 'CRM 전환', 'CRM 프리미엄 고객 전용 서비스를 이용해보세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-06', 'royal2_mgr01', '맞춤형 서비스 제안', 'N', NULL, NULL, 'N', 'N', NULL);

-- 8월 7일(목) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-07', 'csm5_chief02', '홈 네트워크 구성 문의', 'Y', 'GIGA 전환', 'GIGA 홈 네트워크 솔루션으로 집 전체를 커버합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-07', 'csm5_chief02', '보안 서비스 추가', 'Y', 'CRM 전환', 'CRM 보안 패키지로 안전한 인터넷 환경을 만들어보세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-07', 'csm6_mgr02', '스마트홈 연동', 'Y', 'TDS 전환', 'TDS IoT 서비스로 스마트홈을 완성하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-07', 'csm6_mgr02', '가족 요금제 문의', 'Y', 'GIGA 전환', 'GIGA 가족 패키지로 온 가족이 함께 이용하세요', 'Y', 'N', NULL);

-- 8월 8일(금) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-08', 'csm7_chief03', '재택근무 인터넷 구성', 'Y', 'GIGA 전환', '재택근무용 GIGA 비즈니스 요금제를 추천드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-08', 'csm7_chief03', '화상회의 품질 개선', 'Y', 'CRM 전환', 'CRM 화상회의 전용 서비스로 품질을 보장합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-08', 'royal3_mgr02', '기업 전용 서비스', 'Y', 'TDS 전환', 'TDS 기업 솔루션으로 업무 효율성을 높이세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-08', 'royal3_mgr02', '클라우드 서비스 연동', 'N', NULL, NULL, 'N', 'N', NULL);

-- 8월 9일(토), 8월 10일(일) - 주말, 영업일 아님

-- 8월 11일(월) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-11', 'royal1_mgr01', 'VIP 고객 전용 혜택 문의', 'Y', 'CRM 전환', 'VIP 고객님만을 위한 CRM 프리미엄 서비스입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal1_mgr01', '24시간 전담 상담 서비스', 'Y', 'GIGA 전환', 'GIGA VIP는 24시간 전담 기술지원을 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal4_mgr03', '로열 고객 등급 관리', 'Y', 'TDS 전환', 'TDS 로열 서비스로 더 높은 등급의 혜택을 누리세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal4_mgr03', '개인 맞춤 서비스 설정', 'N', NULL, NULL, 'N', 'N', NULL);

-- 8월 4일 추가 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-04', 'csm3_mgr02', '학생 할인 문의', 'Y', 'GIGA 전환', '학생분들을 위한 GIGA 에듀 요금제가 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-04', 'csm3_mgr02', '온라인 수업 최적화', 'Y', 'CRM 전환', 'CRM 에듀케이션 패키지로 온라인 학습을 지원합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-04', 'csm4_chief02', '소상공인 지원 서비스', 'Y', 'TDS 전환', '소상공인을 위한 TDS 비즈니스 솔루션을 제공합니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-04', 'csm4_chief02', '매장용 WiFi 구성', 'Y', 'GIGA 전환', 'GIGA 비즈니스로 고객용 WiFi를 안정적으로 제공하세요', 'Y', 'N', NULL);

-- 8월 5일 추가 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-05', 'csm5_mgr01', '시니어 요금제 문의', 'Y', 'CRM 전환', '시니어 고객님을 위한 CRM 실버 요금제를 추천드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-05', 'csm5_mgr01', '간편 사용법 안내', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-05', 'royal2_chief01', '골드 등급 승급 안내', 'Y', 'GIGA 전환', 'GIGA 골드 등급으로 승급하시면 더 많은 혜택이 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-05', 'royal2_chief01', '충성고객 리워드', 'Y', 'TDS 전환', 'TDS 리워드 프로그램에 참여해보세요', 'N', 'N', NULL);

-- 8월 6일 추가 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-06', 'csm6_chief01', '펜션 사업용 인터넷', 'Y', 'GIGA 전환', '펜션 사업자를 위한 GIGA 호스피탈리티 요금제입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-06', 'csm6_chief01', '다중 접속 최적화', 'Y', 'CRM 전환', 'CRM 멀티 커넥션 서비스로 동시 접속을 원활하게', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-06', 'csm7_mgr01', '크리에이터 지원 서비스', 'Y', 'TDS 전환', '크리에이터를 위한 TDS 스트리밍 전용 서비스입니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-06', 'csm7_mgr01', '고화질 업로드 지원', 'Y', 'GIGA 전환', 'GIGA 크리에이터 요금제로 빠른 업로드를 경험하세요', 'Y', 'N', NULL);

-- 8월 7일 추가 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-07', 'royal3_chief01', '플래티넘 등급 혜택', 'Y', 'CRM 전환', '플래티넘 등급 고객님을 위한 CRM 익스클루시브 서비스', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-07', 'royal3_chief01', '전용 상담사 배정', 'Y', 'GIGA 전환', 'GIGA 플래티넘은 전용 상담사를 배정해드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-07', 'royal4_chief02', '다이아몬드 등급 심사', 'Y', 'TDS 전환', 'TDS 다이아몬드 등급 심사를 진행해드리겠습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-07', 'royal4_chief02', '최고 등급 서비스 체험', 'N', NULL, NULL, 'N', 'N', NULL);

-- 8월 11일 추가 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('2025-08-11', 'csm3_mgr01', '개인사업자 인터넷 구축', 'Y', 'GIGA 전환', '개인사업자를 위한 GIGA 프로페셔널 요금제입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm3_mgr01', '업무용 보안 강화', 'Y', 'CRM 전환', 'CRM 비즈니스 보안 솔루션을 제안드립니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm4_mgr03', '온라인 쇼핑몰 운영', 'Y', 'TDS 전환', 'TDS 이커머스 전용 패키지로 안정적인 운영을 보장합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm4_mgr03', '결제 시스템 연동', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm5_mgr03', '의료진을 위한 안정적 네트워크', 'Y', 'GIGA 전환', 'GIGA 메디컬 요금제로 원격진료를 지원합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm5_mgr03', '환자 정보 보안', 'Y', 'CRM 전환', 'CRM 의료 특화 보안 서비스를 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm6_mgr01', '펜션 예약 시스템 연동', 'Y', 'TDS 전환', 'TDS 예약관리 시스템과 연동하여 효율적으로 운영하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm6_mgr01', '고객 WiFi 관리', 'Y', 'GIGA 전환', 'GIGA 호스피탈리티로 고객 만족도를 높이세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm7_mgr03', '유튜버 라이브 스트리밍', 'Y', 'CRM 전환', 'CRM 크리에이터 전용 업로드 가속 서비스입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'csm7_mgr03', '4K 영상 편집', 'Y', 'GIGA 전환', 'GIGA 크리에이터 플러스로 4K 편집을 원활하게', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal1_mgr02', 'VIP 맞춤 상담', 'Y', 'TDS 전환', 'TDS VIP 전담팀이 맞춤 상담을 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal1_mgr02', '프리미엄 기술지원', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal2_mgr03', '다이아몬드 회원 혜택', 'Y', 'CRM 전환', 'CRM 다이아몬드 회원님만의 특별한 혜택을 확인하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal2_mgr03', '연간 이용료 할인', 'Y', 'GIGA 전환', 'GIGA 연간 약정으로 최대 30% 할인 혜택', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal3_mgr01', '플래티넘 고객 관리', 'Y', 'TDS 전환', 'TDS 플래티넘 고객님을 위한 프리미엄 관리 서비스', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal3_mgr01', '전용 핫라인 개설', 'Y', 'CRM 전환', 'CRM 플래티넘 전용 핫라인을 개설해드리겠습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal3_mgr03', '골드 등급 상향 검토', 'Y', 'GIGA 전환', 'GIGA 골드 플러스로 등급 상향을 검토해보세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal3_mgr03', '충성고객 보상', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal4_mgr01', '최고등급 심사', 'Y', 'TDS 전환', 'TDS 다이아몬드 플러스 등급 심사를 진행하겠습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal4_mgr01', 'VIP 라운지 이용', 'Y', 'CRM 전환', 'CRM VIP 라운지를 이용하실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal4_mgr02', '개인 컨시어지 서비스', 'Y', 'GIGA 전환', 'GIGA 컨시어지 서비스로 모든 것을 해결해드립니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('2025-08-11', 'royal4_mgr02', '맞춤형 솔루션 제안', 'N', NULL, NULL, 'N', 'N', NULL);

-- 데이터 현황 확인 쿼리
SELECT
    consulation_date,
    COUNT(*) as total_records,
    SUM(CASE WHEN nudge_yn = 'Y' THEN 1 ELSE 0 END) as nudge_count,
    ROUND(
            (SUM(CASE WHEN nudge_yn = 'Y' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2
    ) as nudge_percentage,
    SUM(CASE WHEN nudge_yn = 'Y' AND marketing_type = 'GIGA 전환' THEN 1 ELSE 0 END) as giga_count,
    SUM(CASE WHEN nudge_yn = 'Y' AND marketing_type = 'CRM 전환' THEN 1 ELSE 0 END) as crm_count,
    SUM(CASE WHEN nudge_yn = 'Y' AND marketing_type = 'TDS 전환' THEN 1 ELSE 0 END) as tds_count
FROM TB_NUDGE_DATA
GROUP BY consulation_date
ORDER BY consulation_date;

-- 전체 월간 통계 (8월)
SELECT
    '2025-08' as month,('2025-08-05', 'csm4_chief02', '매장용 WiFi 구성', 'Y', 'GIGA 전환', 'GIGA 비즈니스로 고객용 WiFi를 안정적으로 제공하세요', 'Y', 'N', NULL);


-- TB_NUDGE_DATA INSERT 문 (날짜 형식: yyyyMMddHHmm)

-- 8월 1일(금) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('202508010901', 'csm1_chief01', '인터넷 속도가 느려서 문의드립니다', 'Y', 'GIGA 전환', 'GIGA 요금제로 변경하시면 더 빠른 속도를 경험하실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508011105', 'csm1_chief01', '월 요금이 너무 비싸요', 'Y', 'CRM 전환', 'CRM 할인 혜택을 적용해드릴 수 있습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508010930', 'csm1_chief02', 'TV 채널 추가 문의', 'Y', 'TDS 전환', 'TDS 패키지 상품으로 더 많은 채널을 시청하실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508011430', 'csm1_chief02', '서비스 해지 문의', 'N', NULL, NULL, 'N', 'Y', '해지하시면 다시 가입이 어려울 수 있습니다'),
                                                                                                                                                                                                         ('202508011000', 'csm1_chief03', '인터넷 연결 불량', 'Y', 'GIGA 전환', 'GIGA 서비스로 안정적인 연결을 제공해드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508011615', 'csm1_chief03', '고객센터 연결 지연', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508010915', 'csm1_mgr01', '요금제 변경 문의', 'Y', 'CRM 전환', 'CRM 프로모션을 통해 더 저렴한 요금으로 이용 가능합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508011320', 'csm1_mgr01', 'WiFi 비밀번호 분실', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508010945', 'csm1_mgr02', '속도 업그레이드 문의', 'Y', 'GIGA 전환', 'GIGA 요금제로 업그레이드하시면 최고 속도를 경험하실 수 있습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508011145', 'csm1_mgr02', '이사 시 서비스 이전', 'Y', 'TDS 전환', 'TDS 서비스로 이전하시면 추가 혜택을 받으실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508011500', 'csm1_mgr03', '장애신고', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508011700', 'csm1_mgr03', '청구서 문의', 'Y', 'CRM 전환', 'CRM 서비스로 청구서를 더 편리하게 관리하실 수 있습니다', 'Y', 'N', NULL);

-- 8월 4일(월) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('202508040920', 'csm2_chief01', '인터넷 설치 문의', 'Y', 'GIGA 전환', '신규 설치 시 GIGA 서비스로 시작하시면 특별 혜택이 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508041400', 'csm2_chief01', '결합상품 문의', 'Y', 'TDS 전환', 'TDS 결합상품으로 더욱 경제적으로 이용하실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508041015', 'csm2_mgr01', 'VOD 서비스 문의', 'Y', 'CRM 전환', 'CRM VOD 패키지로 더 많은 콘텐츠를 즐기실 수 있습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508041530', 'csm2_mgr01', '요금 할인 문의', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508040935', 'csm3_mgr02', '학생 할인 문의', 'Y', 'GIGA 전환', '학생분들을 위한 GIGA 에듀 요금제가 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508041200', 'csm3_mgr02', '온라인 수업 최적화', 'Y', 'CRM 전환', 'CRM 에듀케이션 패키지로 온라인 학습을 지원합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508041030', 'csm4_chief02', '소상공인 지원 서비스', 'Y', 'TDS 전환', '소상공인을 위한 TDS 비즈니스 솔루션을 제공합니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508041600', 'csm4_chief02', '매장용 WiFi 구성', 'Y', 'GIGA 전환', 'GIGA 비즈니스로 고객용 WiFi를 안정적으로 제공하세요', 'Y', 'N', NULL);

-- 8월 5일(화) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('202508050910', 'csm3_chief01', '인터넷 속도 불만', 'Y', 'GIGA 전환', 'GIGA 프리미엄으로 업그레이드하시면 문제가 해결됩니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508051345', 'csm3_chief01', 'TV 서비스 추가', 'Y', 'TDS 전환', 'TDS 프리미엄 채널을 추가하시겠어요?', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508050940', 'royal1_chief01', '로열 고객 전용 서비스 문의', 'Y', 'CRM 전환', 'CRM 로열 등급 고객님께는 특별한 혜택을 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508051420', 'royal1_chief01', 'VIP 서비스 문의', 'Y', 'GIGA 전환', 'GIGA VIP 서비스로 최고의 품질을 경험하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508051025', 'csm5_mgr01', '시니어 요금제 문의', 'Y', 'CRM 전환', '시니어 고객님을 위한 CRM 실버 요금제를 추천드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508051500', 'csm5_mgr01', '간편 사용법 안내', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508051045', 'royal2_chief01', '골드 등급 승급 안내', 'Y', 'GIGA 전환', 'GIGA 골드 등급으로 승급하시면 더 많은 혜택이 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508051630', 'royal2_chief01', '충성고객 리워드', 'Y', 'TDS 전환', 'TDS 리워드 프로그램에 참여해보세요', 'N', 'N', NULL);

-- 8월 6일(수) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('202508060925', 'csm4_mgr01', '게임 랙 문제', 'Y', 'GIGA 전환', '게이머를 위한 GIGA 게임 전용 요금제를 추천드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508061330', 'csm4_mgr01', '스트리밍 서비스 끊김', 'Y', 'TDS 전환', 'TDS 스트리밍 최적화 서비스로 끊김 없이 시청하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508061000', 'royal2_mgr01', '프리미엄 고객 관리', 'Y', 'CRM 전환', 'CRM 프리미엄 고객 전용 서비스를 이용해보세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508061445', 'royal2_mgr01', '맞춤형 서비스 제안', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508060950', 'csm6_chief01', '펜션 사업용 인터넷', 'Y', 'GIGA 전환', '펜션 사업자를 위한 GIGA 호스피탈리티 요금제입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508061415', 'csm6_chief01', '다중 접속 최적화', 'Y', 'CRM 전환', 'CRM 멀티 커넥션 서비스로 동시 접속을 원활하게', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508061035', 'csm7_mgr01', '크리에이터 지원 서비스', 'Y', 'TDS 전환', '크리에이터를 위한 TDS 스트리밍 전용 서비스입니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508061530', 'csm7_mgr01', '고화질 업로드 지원', 'Y', 'GIGA 전환', 'GIGA 크리에이터 요금제로 빠른 업로드를 경험하세요', 'Y', 'N', NULL);

-- 8월 7일(목) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('202508070930', 'csm5_chief02', '홈 네트워크 구성 문의', 'Y', 'GIGA 전환', 'GIGA 홈 네트워크 솔루션으로 집 전체를 커버합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508071400', 'csm5_chief02', '보안 서비스 추가', 'Y', 'CRM 전환', 'CRM 보안 패키지로 안전한 인터넷 환경을 만들어보세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508071015', 'csm6_mgr02', '스마트홈 연동', 'Y', 'TDS 전환', 'TDS IoT 서비스로 스마트홈을 완성하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508071530', 'csm6_mgr02', '가족 요금제 문의', 'Y', 'GIGA 전환', 'GIGA 가족 패키지로 온 가족이 함께 이용하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508070945', 'royal3_chief01', '플래티넘 등급 혜택', 'Y', 'CRM 전환', '플래티넘 등급 고객님을 위한 CRM 익스클루시브 서비스', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508071435', 'royal3_chief01', '전용 상담사 배정', 'Y', 'GIGA 전환', 'GIGA 플래티넘은 전용 상담사를 배정해드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508071100', 'royal4_chief02', '다이아몬드 등급 심사', 'Y', 'TDS 전환', 'TDS 다이아몬드 등급 심사를 진행해드리겠습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508071600', 'royal4_chief02', '최고 등급 서비스 체험', 'N', NULL, NULL, 'N', 'N', NULL);

-- 8월 8일(금) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('202508080920', 'csm7_chief03', '재택근무 인터넷 구성', 'Y', 'GIGA 전환', '재택근무용 GIGA 비즈니스 요금제를 추천드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508081350', 'csm7_chief03', '화상회의 품질 개선', 'Y', 'CRM 전환', 'CRM 화상회의 전용 서비스로 품질을 보장합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508081005', 'royal3_mgr02', '기업 전용 서비스', 'Y', 'TDS 전환', 'TDS 기업 솔루션으로 업무 효율성을 높이세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508081445', 'royal3_mgr02', '클라우드 서비스 연동', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508080935', 'csm1_mgr02', '아파트 단지 서비스', 'Y', 'GIGA 전환', '아파트 단지 전체를 위한 GIGA 커뮤니티 서비스입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508081420', 'csm1_mgr02', '공동 WiFi 구축', 'Y', 'CRM 전환', 'CRM 커뮤니티 WiFi로 단지 내 인터넷을 통합 관리하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508081030', 'csm2_mgr02', '카페 사장님 지원', 'Y', 'TDS 전환', '카페 운영을 위한 TDS 비즈니스 솔루션을 제안드립니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508081515', 'csm2_mgr02', '고객용 무료 WiFi', 'Y', 'GIGA 전환', 'GIGA 비즈니스로 고품질 고객용 WiFi를 제공하세요', 'Y', 'N', NULL);

-- 8월 11일(월) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('202508110900', 'royal1_mgr01', 'VIP 고객 전용 혜택 문의', 'Y', 'CRM 전환', 'VIP 고객님만을 위한 CRM 프리미엄 서비스입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111345', 'royal1_mgr01', '24시간 전담 상담 서비스', 'Y', 'GIGA 전환', 'GIGA VIP는 24시간 전담 기술지원을 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508110930', 'royal4_mgr03', '로열 고객 등급 관리', 'Y', 'TDS 전환', 'TDS 로열 서비스로 더 높은 등급의 혜택을 누리세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508111415', 'royal4_mgr03', '개인 맞춤 서비스 설정', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508110915', 'csm3_mgr01', '개인사업자 인터넷 구축', 'Y', 'GIGA 전환', '개인사업자를 위한 GIGA 프로페셔널 요금제입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111300', 'csm3_mgr01', '업무용 보안 강화', 'Y', 'CRM 전환', 'CRM 비즈니스 보안 솔루션을 제안드립니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508110945', 'csm4_mgr03', '온라인 쇼핑몰 운영', 'Y', 'TDS 전환', 'TDS 이커머스 전용 패키지로 안정적인 운영을 보장합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111430', 'csm4_mgr03', '결제 시스템 연동', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508111000', 'csm5_mgr03', '의료진을 위한 안정적 네트워크', 'Y', 'GIGA 전환', 'GIGA 메디컬 요금제로 원격진료를 지원합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111500', 'csm5_mgr03', '환자 정보 보안', 'Y', 'CRM 전환', 'CRM 의료 특화 보안 서비스를 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111015', 'csm6_mgr01', '펜션 예약 시스템 연동', 'Y', 'TDS 전환', 'TDS 예약관리 시스템과 연동하여 효율적으로 운영하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508111545', 'csm6_mgr01', '고객 WiFi 관리', 'Y', 'GIGA 전환', 'GIGA 호스피탈리티로 고객 만족도를 높이세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111045', 'csm7_mgr03', '유튜버 라이브 스트리밍', 'Y', 'CRM 전환', 'CRM 크리에이터 전용 업로드 가속 서비스입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111630', 'csm7_mgr03', '4K 영상 편집', 'Y', 'GIGA 전환', 'GIGA 크리에이터 플러스로 4K 편집을 원활하게', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111100', 'royal1_mgr02', 'VIP 맞춤 상담', 'Y', 'TDS 전환', 'TDS VIP 전담팀이 맞춤 상담을 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111700', 'royal1_mgr02', '프리미엄 기술지원', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508111115', 'royal2_mgr03', '다이아몬드 회원 혜택', 'Y', 'CRM 전환', 'CRM 다이아몬드 회원님만의 특별한 혜택을 확인하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508111715', 'royal2_mgr03', '연간 이용료 할인', 'Y', 'GIGA 전환', 'GIGA 연간 약정으로 최대 30% 할인 혜택', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111130', 'royal3_mgr01', '플래티넘 고객 관리', 'Y', 'TDS 전환', 'TDS 플래티넘 고객님을 위한 프리미엄 관리 서비스', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111730', 'royal3_mgr01', '전용 핫라인 개설', 'Y', 'CRM 전환', 'CRM 플래티넘 전용 핫라인을 개설해드리겠습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111200', 'royal3_mgr03', '골드 등급 상향 검토', 'Y', 'GIGA 전환', 'GIGA 골드 플러스로 등급 상향을 검토해보세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508111800', 'royal3_mgr03', '충성고객 보상', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508111215', 'royal4_mgr01', '최고등급 심사', 'Y', 'TDS 전환', 'TDS 다이아몬드 플러스 등급 심사를 진행하겠습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111815', 'royal4_mgr01', 'VIP 라운지 이용', 'Y', 'CRM 전환', 'CRM VIP 라운지를 이용하실 수 있습니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508111230', 'royal4_mgr02', '개인 컨시어지 서비스', 'Y', 'GIGA 전환', 'GIGA 컨시어지 서비스로 모든 것을 해결해드립니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508111830', 'royal4_mgr02', '맞춤형 솔루션 제안', 'N', NULL, NULL, 'N', 'N', NULL);

-- 8월 12일(화) 데이터
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
                                                                                                                                                                                                         ('202508120900', 'csm2_chief02', '신규 개통 문의', 'Y', 'GIGA 전환', '신규 고객님께는 GIGA 웰컴 패키지를 제안드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121345', 'csm2_chief02', '기존 서비스 업그레이드', 'Y', 'CRM 전환', 'CRM 프리미엄으로 업그레이드하시면 더 많은 혜택이 있습니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508120915', 'csm2_chief03', '패밀리 요금제 문의', 'Y', 'TDS 전환', 'TDS 패밀리 패키지로 온 가족이 경제적으로 이용하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121400', 'csm2_chief03', '해외 로밍 서비스', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508120930', 'csm2_mgr02', '학원 인터넷 구축', 'Y', 'GIGA 전환', '교육기관을 위한 GIGA 에듀 전용 요금제입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121415', 'csm2_mgr02', '다수 접속 최적화', 'Y', 'CRM 전환', 'CRM 멀티유저 솔루션으로 안정적인 접속을 보장합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508120945', 'csm2_mgr03', '음식점 사장님 지원', 'Y', 'TDS 전환', '음식점 운영에 필요한 TDS 비즈니스 솔루션을 제공합니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121430', 'csm2_mgr03', '포스기 연동 서비스', 'Y', 'GIGA 전환', 'GIGA 비즈니스로 포스기와 안정적으로 연동하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121000', 'csm3_chief02', '아파트 관리사무소 지원', 'Y', 'CRM 전환', '관리사무소 전용 CRM 솔루션을 제안드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121500', 'csm3_chief02', 'CCTV 시스템 연동', 'Y', 'GIGA 전환', 'GIGA 보안 패키지로 CCTV를 안정적으로 운영하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121015', 'csm3_chief03', '독서실 인터넷 구성', 'Y', 'TDS 전환', '독서실 고객을 위한 TDS 스터디 솔루션입니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121515', 'csm3_chief03', '시간 제한 WiFi 관리', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121030', 'csm3_mgr03', '헬스장 회원 WiFi', 'Y', 'GIGA 전환', 'GIGA 피트니스 솔루션으로 회원 만족도를 높이세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121530', 'csm3_mgr03', '운동 앱 연동 서비스', 'Y', 'CRM 전환', 'CRM 헬스케어 패키지로 다양한 앱과 연동하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121045', 'csm4_chief01', '변호사 사무실 보안', 'Y', 'TDS 전환', 'TDS 법무 전용 보안 솔루션을 제공합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121545', 'csm4_chief01', '기밀 문서 전송 보안', 'Y', 'GIGA 전환', 'GIGA 시큐어로 안전한 문서 전송을 보장합니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121100', 'csm4_chief03', '병원 원무과 지원', 'Y', 'CRM 전환', '의료기관 전용 CRM 솔루션으로 업무 효율성을 높이세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121600', 'csm4_chief03', '환자 대기 시간 WiFi', 'Y', 'GIGA 전환', 'GIGA 메디컬 게스트 WiFi로 환자 만족도를 개선하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121115', 'csm4_mgr02', '미용실 예약 시스템', 'Y', 'TDS 전환', 'TDS 뷰티 솔루션으로 예약 관리를 효율화하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121615', 'csm4_mgr02', '고객 대기 WiFi 제공', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121130', 'csm5_chief01', '부동산 사무소 지원', 'Y', 'GIGA 전환', '부동산 업무용 GIGA 프로페셔널을 추천드립니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121630', 'csm5_chief01', '매물 정보 실시간 업데이트', 'Y', 'CRM 전환', 'CRM 리얼에스테이트로 매물 정보를 실시간 관리하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121200', 'csm5_chief03', '세무사 사무실 네트워크', 'Y', 'TDS 전환', 'TDS 택스 솔루션으로 세무 업무를 지원합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121700', 'csm5_chief03', '전자신고 시스템 연동', 'Y', 'GIGA 전환', 'GIGA 비즈니스로 전자신고를 안정적으로 처리하세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121215', 'csm5_mgr02', '어린이집 안전 관리', 'Y', 'CRM 전환', 'CRM 키즈케어로 어린이집 안전을 강화하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121715', 'csm5_mgr02', '학부모 소통 앱 연동', 'Y', 'GIGA 전환', 'GIGA 에듀케어로 학부모와 실시간 소통하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121230', 'csm6_chief02', '호텔 투숙객 WiFi', 'Y', 'TDS 전환', 'TDS 호스피탈리티로 투숙객 만족도를 높이세요', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121730', 'csm6_chief02', '객실 스마트 시스템', 'Y', 'GIGA 전환', 'GIGA 스마트룸으로 객실을 첨단화하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121245', 'csm6_chief03', '펜션 예약 관리', 'Y', 'CRM 전환', 'CRM 펜션 매니지먼트로 예약을 체계적으로 관리하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121745', 'csm6_chief03', '게스트 리뷰 관리', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121300', 'csm6_mgr03', '웨딩홀 행사 지원', 'Y', 'GIGA 전환', '웨딩홀 전용 GIGA 이벤트 솔루션입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121800', 'csm6_mgr03', '라이브 스트리밍 지원', 'Y', 'TDS 전환', 'TDS 라이브 솔루션으로 행사를 실시간 중계하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121315', 'csm7_chief01', '대형마트 POS 연동', 'Y', 'CRM 전환', '대형마트 전용 CRM POS 솔루션을 제공합니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121815', 'csm7_chief01', '재고 관리 시스템', 'Y', 'GIGA 전환', 'GIGA 리테일로 재고를 실시간 관리하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121330', 'csm7_chief02', '약국 처방전 시스템', 'Y', 'TDS 전환', 'TDS 파마시 솔루션으로 처방전을 안전하게 관리하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121830', 'csm7_chief02', '의약품 재고 연동', 'Y', 'GIGA 전환', 'GIGA 메디컬로 의약품 재고를 실시간 확인하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121400', 'csm7_mgr02', '자동차 정비소 지원', 'Y', 'CRM 전환', '정비소 전용 CRM 오토케어 솔루션입니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121900', 'csm7_mgr02', '부품 주문 시스템', 'N', NULL, NULL, 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121415', 'royal1_mgr03', 'VIP 전용 컨시어지', 'Y', 'GIGA 전환', 'GIGA VIP 컨시어지로 모든 요청을 처리합니다', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121915', 'royal1_mgr03', '개인 맞춤 서비스 설계', 'Y', 'TDS 전환', 'TDS 커스텀으로 고객님만의 서비스를 설계합니다', 'N', 'N', NULL),
                                                                                                                                                                                                         ('202508121430', 'royal2_mgr02', '플래티넘 회원 혜택', 'Y', 'CRM 전환', 'CRM 플래티넘 회원님만의 독점 혜택을 확인하세요', 'Y', 'N', NULL),
                                                                                                                                                                                                         ('202508121930', 'royal2_mgr02', '연간 서비스 플랜', 'Y', 'GIGA 전환', 'GIGA 연간 플랜으로 최고의 서비스를 경험하세요', 'Y', 'N', NULL);


-- TB_NUDGE_POINT 테이블 생성 (포인트 적립/지출 내역)
CREATE TABLE TB_NUDGE_POINT (
                                id SERIAL PRIMARY KEY,
                                user_id VARCHAR(255) NOT NULL,
                                point_amount INTEGER NOT NULL,
                                point_type VARCHAR(10) NOT NULL, -- 'EARN' 또는 'SPEND'
                                point_reason TEXT,
                                created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                nudge_count INTEGER,
                                grade VARCHAR(20),
                                grade_bonus_rate DECIMAL(5,3)
);

-- TB_USER_POINT_SUMMARY 테이블 생성 (사용자별 포인트 요약)
CREATE TABLE TB_USER_POINT_SUMMARY (
                                       user_id VARCHAR(255) PRIMARY KEY,
                                       total_points INTEGER DEFAULT 0,
                                       current_grade VARCHAR(20) DEFAULT 'bronze',
                                       month_nudge_count INTEGER DEFAULT 0,
                                       last_processed_month VARCHAR(6),
                                       updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- csm6_mgr01 사용자를 위한 7월 영업일 데이터 (날짜 형식: yyyyMMddHHmm)
-- 7월 1일(화) - 7월 3일(목)
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
('202507010900', 'csm6_mgr01', '인터넷 속도가 느려서 문의드립니다', 'Y', 'GIGA 전환', 'GIGA 요금제로 변경하시면 더 빠른 속도를 경험하실 수 있습니다', 'Y', 'N', NULL),
('202507011030', 'csm6_mgr01', '월 요금이 너무 비싸요', 'Y', 'CRM 전환', 'CRM 할인 혜택을 적용해드릴 수 있습니다', 'N', 'N', NULL),
('202507011500', 'csm6_mgr01', 'TV 채널 추가 문의', 'Y', 'TDS 전환', 'TDS 패키지 상품으로 더 많은 채널을 시청하실 수 있습니다', 'Y', 'N', NULL),
('202507020915', 'csm6_mgr01', '서비스 해지 문의', 'N', NULL, NULL, 'N', 'Y', '해지하시면 다시 가입이 어려울 수 있습니다'),
('202507021200', 'csm6_mgr01', '인터넷 연결 불량', 'Y', 'GIGA 전환', 'GIGA 서비스로 안정적인 연결을 제공해드립니다', 'Y', 'N', NULL),
('202507021600', 'csm6_mgr01', '고객센터 연결 지연', 'N', NULL, NULL, 'N', 'N', NULL),
('202507030930', 'csm6_mgr01', '요금제 변경 문의', 'Y', 'CRM 전환', 'CRM 프로모션을 통해 더 저렴한 요금으로 이용 가능합니다', 'Y', 'N', NULL),
('202507031145', 'csm6_mgr01', 'WiFi 비밀번호 분실', 'N', NULL, NULL, 'N', 'N', NULL),
('202507031430', 'csm6_mgr01', '속도 업그레이드 문의', 'Y', 'GIGA 전환', 'GIGA 요금제로 업그레이드하시면 최고 속도를 경험하실 수 있습니다', 'N', 'N', NULL);

-- 7월 7일(월) - 7월 11일(금)
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
('202507070900', 'csm6_mgr01', '인터넷 설치 문의', 'Y', 'GIGA 전환', '신규 설치 시 GIGA 서비스로 시작하시면 특별 혜택이 있습니다', 'Y', 'N', NULL),
('202507071030', 'csm6_mgr01', '결합상품 문의', 'Y', 'TDS 전환', 'TDS 결합상품으로 더욱 경제적으로 이용하실 수 있습니다', 'Y', 'N', NULL),
('202507071500', 'csm6_mgr01', 'VOD 서비스 문의', 'Y', 'CRM 전환', 'CRM VOD 패키지로 더 많은 콘텐츠를 즐기실 수 있습니다', 'N', 'N', NULL),
('202507080915', 'csm6_mgr01', '요금 할인 문의', 'N', NULL, NULL, 'N', 'N', NULL),
('202507081200', 'csm6_mgr01', '학생 할인 문의', 'Y', 'GIGA 전환', '학생분들을 위한 GIGA 에듀 요금제가 있습니다', 'Y', 'N', NULL),
('202507081600', 'csm6_mgr01', '온라인 수업 최적화', 'Y', 'CRM 전환', 'CRM 에듀케이션 패키지로 온라인 학습을 지원합니다', 'Y', 'N', NULL),
('202507090930', 'csm6_mgr01', '소상공인 지원 서비스', 'Y', 'TDS 전환', '소상공인을 위한 TDS 비즈니스 솔루션을 제공합니다', 'N', 'N', NULL),
('202507091145', 'csm6_mgr01', '매장용 WiFi 구성', 'Y', 'GIGA 전환', 'GIGA 비즈니스로 고객용 WiFi를 안정적으로 제공하세요', 'Y', 'N', NULL),
('202507091430', 'csm6_mgr01', '인터넷 속도 불만', 'Y', 'GIGA 전환', 'GIGA 프리미엄으로 업그레이드하시면 문제가 해결됩니다', 'Y', 'N', NULL),
('202507100900', 'csm6_mgr01', 'TV 서비스 추가', 'Y', 'TDS 전환', 'TDS 프리미엄 채널을 추가하시겠어요?', 'Y', 'N', NULL),
('202507101030', 'csm6_mgr01', '로열 고객 전용 서비스 문의', 'Y', 'CRM 전환', 'CRM 로열 등급 고객님께는 특별한 혜택을 제공합니다', 'Y', 'N', NULL),
('202507101500', 'csm6_mgr01', 'VIP 서비스 문의', 'Y', 'GIGA 전환', 'GIGA VIP 서비스로 최고의 품질을 경험하세요', 'N', 'N', NULL),
('202507110915', 'csm6_mgr01', '게임 랙 문제', 'Y', 'GIGA 전환', '게이머를 위한 GIGA 게임 전용 요금제를 추천드립니다', 'Y', 'N', NULL),
('202507111200', 'csm6_mgr01', '스트리밍 서비스 끊김', 'Y', 'TDS 전환', 'TDS 스트리밍 최적화 서비스로 끊김 없이 시청하세요', 'Y', 'N', NULL),
('202507111600', 'csm6_mgr01', '프리미엄 고객 관리', 'Y', 'CRM 전환', 'CRM 프리미엄 고객 전용 서비스를 이용해보세요', 'N', 'N', NULL);

-- 7월 14일(월) - 7월 18일(금)
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
('202507140900', 'csm6_mgr01', '홈 네트워크 구성 문의', 'Y', 'GIGA 전환', 'GIGA 홈 네트워크 솔루션으로 집 전체를 커버합니다', 'Y', 'N', NULL),
('202507141030', 'csm6_mgr01', '보안 서비스 추가', 'Y', 'CRM 전환', 'CRM 보안 패키지로 안전한 인터넷 환경을 만들어보세요', 'Y', 'N', NULL),
('202507141500', 'csm6_mgr01', '스마트홈 연동', 'Y', 'TDS 전환', 'TDS IoT 서비스로 스마트홈을 완성하세요', 'N', 'N', NULL),
('202507150915', 'csm6_mgr01', '가족 요금제 문의', 'Y', 'GIGA 전환', 'GIGA 가족 패키지로 온 가족이 함께 이용하세요', 'Y', 'N', NULL),
('202507151200', 'csm6_mgr01', '재택근무 인터넷 구성', 'Y', 'GIGA 전환', '재택근무용 GIGA 비즈니스 요금제를 추천드립니다', 'Y', 'N', NULL),
('202507151600', 'csm6_mgr01', '화상회의 품질 개선', 'Y', 'CRM 전환', 'CRM 화상회의 전용 서비스로 품질을 보장합니다', 'Y', 'N', NULL),
('202507160930', 'csm6_mgr01', '기업 전용 서비스', 'Y', 'TDS 전환', 'TDS 기업 솔루션으로 업무 효율성을 높이세요', 'N', 'N', NULL),
('202507161145', 'csm6_mgr01', '클라우드 서비스 연동', 'N', NULL, NULL, 'N', 'N', NULL),
('202507161430', 'csm6_mgr01', '시니어 요금제 문의', 'Y', 'CRM 전환', '시니어 고객님을 위한 CRM 실버 요금제를 추천드립니다', 'Y', 'N', NULL),
('202507170900', 'csm6_mgr01', '간편 사용법 안내', 'N', NULL, NULL, 'N', 'N', NULL),
('202507171030', 'csm6_mgr01', '골드 등급 승급 안내', 'Y', 'GIGA 전환', 'GIGA 골드 등급으로 승급하시면 더 많은 혜택이 있습니다', 'Y', 'N', NULL),
('202507171500', 'csm6_mgr01', '충성고객 리워드', 'Y', 'TDS 전환', 'TDS 리워드 프로그램에 참여해보세요', 'N', 'N', NULL),
('202507180915', 'csm6_mgr01', '펜션 사업용 인터넷', 'Y', 'GIGA 전환', '펜션 사업자를 위한 GIGA 호스피탈리티 요금제입니다', 'Y', 'N', NULL),
('202507181200', 'csm6_mgr01', '다중 접속 최적화', 'Y', 'CRM 전환', 'CRM 멀티 커넥션 서비스로 동시 접속을 원활하게', 'Y', 'N', NULL),
('202507181600', 'csm6_mgr01', '크리에이터 지원 서비스', 'Y', 'TDS 전환', '크리에이터를 위한 TDS 스트리밍 전용 서비스입니다', 'N', 'N', NULL);

-- 7월 21일(월) - 7월 25일(금)
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
('202507210900', 'csm6_mgr01', '고화질 업로드 지원', 'Y', 'GIGA 전환', 'GIGA 크리에이터 요금제로 빠른 업로드를 경험하세요', 'Y', 'N', NULL),
('202507211030', 'csm6_mgr01', '플래티넘 등급 혜택', 'Y', 'CRM 전환', '플래티넘 등급 고객님을 위한 CRM 익스클루시브 서비스', 'Y', 'N', NULL),
('202507211500', 'csm6_mgr01', '전용 상담사 배정', 'Y', 'GIGA 전환', 'GIGA 플래티넘은 전용 상담사를 배정해드립니다', 'Y', 'N', NULL),
('202507220915', 'csm6_mgr01', '다이아몬드 등급 심사', 'Y', 'TDS 전환', 'TDS 다이아몬드 등급 심사를 진행해드리겠습니다', 'N', 'N', NULL),
('202507221200', 'csm6_mgr01', '최고 등급 서비스 체험', 'N', NULL, NULL, 'N', 'N', NULL),
('202507221600', 'csm6_mgr01', 'VIP 고객 전용 혜택 문의', 'Y', 'CRM 전환', 'VIP 고객님만을 위한 CRM 프리미엄 서비스입니다', 'Y', 'N', NULL),
('202507230930', 'csm6_mgr01', '24시간 전담 상담 서비스', 'Y', 'GIGA 전환', 'GIGA VIP는 24시간 전담 기술지원을 제공합니다', 'Y', 'N', NULL),
('202507231145', 'csm6_mgr01', '로열 고객 등급 관리', 'Y', 'TDS 전환', 'TDS 로열 서비스로 더 높은 등급의 혜택을 누리세요', 'N', 'N', NULL),
('202507231430', 'csm6_mgr01', '개인 맞춤 서비스 설정', 'N', NULL, NULL, 'N', 'N', NULL),
('202507240900', 'csm6_mgr01', '개인사업자 인터넷 구축', 'Y', 'GIGA 전환', '개인사업자를 위한 GIGA 프로페셔널 요금제입니다', 'Y', 'N', NULL),
('202507241030', 'csm6_mgr01', '업무용 보안 강화', 'Y', 'CRM 전환', 'CRM 비즈니스 보안 솔루션을 제안드립니다', 'N', 'N', NULL),
('202507241500', 'csm6_mgr01', '온라인 쇼핑몰 운영', 'Y', 'TDS 전환', 'TDS 이커머스 전용 패키지로 안정적인 운영을 보장합니다', 'Y', 'N', NULL),
('202507250915', 'csm6_mgr01', '결제 시스템 연동', 'N', NULL, NULL, 'N', 'N', NULL),
('202507251200', 'csm6_mgr01', '의료진을 위한 안정적 네트워크', 'Y', 'GIGA 전환', 'GIGA 메디컬 요금제로 원격진료를 지원합니다', 'Y', 'N', NULL),
('202507251600', 'csm6_mgr01', '환자 정보 보안', 'Y', 'CRM 전환', 'CRM 의료 특화 보안 서비스를 제공합니다', 'Y', 'N', NULL);

-- 7월 28일(월) - 7월 31일(목)
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
('202507280900', 'csm6_mgr01', '펜션 예약 시스템 연동', 'Y', 'TDS 전환', 'TDS 예약관리 시스템과 연동하여 효율적으로 운영하세요', 'N', 'N', NULL),
('202507281030', 'csm6_mgr01', '고객 WiFi 관리', 'Y', 'GIGA 전환', 'GIGA 호스피탈리티로 고객 만족도를 높이세요', 'Y', 'N', NULL),
('202507281500', 'csm6_mgr01', '유튜버 라이브 스트리밍', 'Y', 'CRM 전환', 'CRM 크리에이터 전용 업로드 가속 서비스입니다', 'Y', 'N', NULL),
('202507290915', 'csm6_mgr01', '4K 영상 편집', 'Y', 'GIGA 전환', 'GIGA 크리에이터 플러스로 4K 편집을 원활하게', 'Y', 'N', NULL),
('202507291200', 'csm6_mgr01', 'VIP 맞춤 상담', 'Y', 'TDS 전환', 'TDS VIP 전담팀이 맞춤 상담을 제공합니다', 'Y', 'N', NULL),
('202507291600', 'csm6_mgr01', '프리미엄 기술지원', 'N', NULL, NULL, 'N', 'N', NULL),
('202507300930', 'csm6_mgr01', '다이아몬드 회원 혜택', 'Y', 'CRM 전환', 'CRM 다이아몬드 회원님만의 특별한 혜택을 확인하세요', 'N', 'N', NULL),
('202507301145', 'csm6_mgr01', '연간 이용료 할인', 'Y', 'GIGA 전환', 'GIGA 연간 약정으로 최대 30% 할인 혜택', 'Y', 'N', NULL),
('202507301430', 'csm6_mgr01', '플래티넘 고객 관리', 'Y', 'TDS 전환', 'TDS 플래티넘 고객님을 위한 프리미엄 관리 서비스', 'Y', 'N', NULL),
('202507310900', 'csm6_mgr01', '전용 핫라인 개설', 'Y', 'CRM 전환', 'CRM 플래티넘 전용 핫라인을 개설해드리겠습니다', 'Y', 'N', NULL),
('202507311030', 'csm6_mgr01', '골드 등급 상향 검토', 'Y', 'GIGA 전환', 'GIGA 골드 플러스로 등급 상향을 검토해보세요', 'N', 'N', NULL),
('202507311500', 'csm6_mgr01', '충성고객 보상', 'N', NULL, NULL, 'N', 'N', NULL);

-- csm6_mgr01 사용자를 위한 8월 영업일 데이터 (8월 12일까지)
-- 8월 1일(금)
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
('202508010900', 'csm6_mgr01', '인터넷 속도가 느려서 문의드립니다', 'Y', 'GIGA 전환', 'GIGA 요금제로 변경하시면 더 빠른 속도를 경험하실 수 있습니다', 'Y', 'N', NULL),
('202508011030', 'csm6_mgr01', '월 요금이 너무 비싸요', 'Y', 'CRM 전환', 'CRM 할인 혜택을 적용해드릴 수 있습니다', 'N', 'N', NULL),
('202508011500', 'csm6_mgr01', 'TV 채널 추가 문의', 'Y', 'TDS 전환', 'TDS 패키지 상품으로 더 많은 채널을 시청하실 수 있습니다', 'Y', 'N', NULL);

-- 8월 4일(월) - 8월 8일(금)
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
('202508040900', 'csm6_mgr01', '서비스 해지 문의', 'N', NULL, NULL, 'N', 'Y', '해지하시면 다시 가입이 어려울 수 있습니다'),
('202508041030', 'csm6_mgr01', '인터넷 연결 불량', 'Y', 'GIGA 전환', 'GIGA 서비스로 안정적인 연결을 제공해드립니다', 'Y', 'N', NULL),
('202508041500', 'csm6_mgr01', '고객센터 연결 지연', 'N', NULL, NULL, 'N', 'N', NULL),
('202508050915', 'csm6_mgr01', '요금제 변경 문의', 'Y', 'CRM 전환', 'CRM 프로모션을 통해 더 저렴한 요금으로 이용 가능합니다', 'Y', 'N', NULL),
('202508051200', 'csm6_mgr01', 'WiFi 비밀번호 분실', 'N', NULL, NULL, 'N', 'N', NULL),
('202508051600', 'csm6_mgr01', '속도 업그레이드 문의', 'Y', 'GIGA 전환', 'GIGA 요금제로 업그레이드하시면 최고 속도를 경험하실 수 있습니다', 'N', 'N', NULL),
('202508060930', 'csm6_mgr01', '이사 시 서비스 이전', 'Y', 'TDS 전환', 'TDS 서비스로 이전하시면 추가 혜택을 받으실 수 있습니다', 'Y', 'N', NULL),
('202508061145', 'csm6_mgr01', '장애신고', 'N', NULL, NULL, 'N', 'N', NULL),
('202508061430', 'csm6_mgr01', '청구서 문의', 'Y', 'CRM 전환', 'CRM 서비스로 청구서를 더 편리하게 관리하실 수 있습니다', 'Y', 'N', NULL),
('202508070900', 'csm6_mgr01', '인터넷 설치 문의', 'Y', 'GIGA 전환', '신규 설치 시 GIGA 서비스로 시작하시면 특별 혜택이 있습니다', 'Y', 'N', NULL),
('202508071030', 'csm6_mgr01', '결합상품 문의', 'Y', 'TDS 전환', 'TDS 결합상품으로 더욱 경제적으로 이용하실 수 있습니다', 'Y', 'N', NULL),
('202508071500', 'csm6_mgr01', 'VOD 서비스 문의', 'Y', 'CRM 전환', 'CRM VOD 패키지로 더 많은 콘텐츠를 즐기실 수 있습니다', 'N', 'N', NULL),
('202508080915', 'csm6_mgr01', '요금 할인 문의', 'N', NULL, NULL, 'N', 'N', NULL),
('202508081200', 'csm6_mgr01', '학생 할인 문의', 'Y', 'GIGA 전환', '학생분들을 위한 GIGA 에듀 요금제가 있습니다', 'Y', 'N', NULL),
('202508081600', 'csm6_mgr01', '온라인 수업 최적화', 'Y', 'CRM 전환', 'CRM 에듀케이션 패키지로 온라인 학습을 지원합니다', 'Y', 'N', NULL);

-- 8월 11일(월) - 8월 12일(화)
INSERT INTO TB_NUDGE_DATA (consulation_date, user_id, customer_inquiry, nudge_yn, marketing_type, marketing_message, customer_consent_yn, inappropriate_response_yn, inappropriate_response_message) VALUES
('202508110900', 'csm6_mgr01', '소상공인 지원 서비스', 'Y', 'TDS 전환', '소상공인을 위한 TDS 비즈니스 솔루션을 제공합니다', 'N', 'N', NULL),
('202508111030', 'csm6_mgr01', '매장용 WiFi 구성', 'Y', 'GIGA 전환', 'GIGA 비즈니스로 고객용 WiFi를 안정적으로 제공하세요', 'Y', 'N', NULL),
('202508111500', 'csm6_mgr01', '인터넷 속도 불만', 'Y', 'GIGA 전환', 'GIGA 프리미엄으로 업그레이드하시면 문제가 해결됩니다', 'Y', 'N', NULL),
('202508120915', 'csm6_mgr01', 'TV 서비스 추가', 'Y', 'TDS 전환', 'TDS 프리미엄 채널을 추가하시겠어요?', 'Y', 'N', NULL),
('202508121200', 'csm6_mgr01', '로열 고객 전용 서비스 문의', 'Y', 'CRM 전환', 'CRM 로열 등급 고객님께는 특별한 혜택을 제공합니다', 'Y', 'N', NULL),
('202508121600', 'csm6_mgr01', 'VIP 서비스 문의', 'Y', 'GIGA 전환', 'GIGA VIP 서비스로 최고의 품질을 경험하세요', 'N', 'N', NULL);