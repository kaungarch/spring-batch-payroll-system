CREATE TABLE MONTHLY_PAYROLL (
                                 id BIGSERIAL PRIMARY KEY,

                                 employee_id VARCHAR(50) NOT NULL,
                                 name VARCHAR(100) NOT NULL,
                                 job_position VARCHAR(50) NOT NULL,

                                 base_salary BIGINT NOT NULL,
                                 work_days INTEGER NOT NULL,
                                 bonus BIGINT NOT NULL,

                                 monthly_salary BIGINT NOT NULL,
                                 month INTEGER NOT NULL,
                                 year INTEGER NOT NULL,

                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
