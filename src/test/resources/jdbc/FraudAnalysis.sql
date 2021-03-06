CREATE TABLE FRAUD_ANALYSIS
(
    AUTH_ID           DECIMAL(3)               NOT NULL,
    HRCH_DETL_1_ID    VARCHAR(12)               NOT NULL,
    HRCH_DETL_2_ID    VARCHAR(12)               NOT NULL,
    HRCH_DETL_3_ID    VARCHAR(12)               NOT NULL,
    HRCH_DETL_4_ID    VARCHAR(12)               NOT NULL,
    AUTH_TRAN_DT      TIMESTAMP(0)              NOT NULL,
    CONSTRAINT FRAUD_ANALYSIS_PK PRIMARY KEY (AUTH_ID)
);
INSERT INTO FRAUD_ANALYSIS
(
	AUTH_ID, HRCH_DETL_1_ID, HRCH_DETL_2_ID, HRCH_DETL_3_ID, HRCH_DETL_4_ID, AUTH_TRAN_DT
)
VALUES
(
	615416212381377, '5918', '8406', '1115', '0000', '2021-04-01 08:00:00.0'
);