#!/bin/bash

DB_CONTAINER_NAME=postgres
DB_NAME=payroll_db
DB_USER=postgres

DROP_SQL=src/sql/drop-batch-schema.sql
CREATE_SQL=src/sql/create-batch-schema.sql

DROP_PAYROLL_SQL=src/sql/drop-payroll-schema.sql
CREATE_PAYROLL_SQL=src/sql/create-payroll-schema.sql

echo "Running drop schema script..."
docker exec -i $DB_CONTAINER_NAME psql -U $DB_USER -d $DB_NAME < $DROP_SQL
docker exec -i $DB_CONTAINER_NAME psql -U $DB_USER -d $DB_NAME < $DROP_PAYROLL_SQL


if [ $? -ne 0 ]; then
  echo "Drop schema failed. Stopping."
  exit 1
fi

echo "Running create schema script..."
docker exec -i $DB_CONTAINER_NAME psql -U $DB_USER -d $DB_NAME < $CREATE_SQL
docker exec -i $DB_CONTAINER_NAME psql -U $DB_USER -d $DB_NAME < $CREATE_PAYROLL_SQL

if [ $? -ne 0 ]; then
  echo "Create schema failed."
  exit 1
fi

echo "Database schema reset completed successfully."
