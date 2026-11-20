-- Creates one database and one login per service. Runs as sa from the
-- sqlserver-init service in compose.yaml, on every "docker compose up",
-- so every statement checks first and is safe to run again.
-- $(NAME) = sqlcmd variable, filled in from -v before SQL Server sees the text.

IF DB_ID('appdata') IS NULL CREATE DATABASE appdata;
IF DB_ID('iam') IS NULL CREATE DATABASE iam;
GO

-- LOGIN = server level: "you may connect to this SQL Server" (name + password).
-- On re-runs, ALTER keeps the password in sync with .env.
IF SUSER_ID('appdata_svc') IS NULL
    CREATE LOGIN appdata_svc WITH PASSWORD = '$(APPDATA_DB_PASSWORD)', DEFAULT_DATABASE = appdata;
ELSE
    ALTER LOGIN appdata_svc WITH PASSWORD = '$(APPDATA_DB_PASSWORD)';

IF SUSER_ID('iam_svc') IS NULL
    CREATE LOGIN iam_svc WITH PASSWORD = '$(IAM_DB_PASSWORD)', DEFAULT_DATABASE = iam;
ELSE
    ALTER LOGIN iam_svc WITH PASSWORD = '$(IAM_DB_PASSWORD)';
GO

-- USER = database level: "this login may enter THIS database".
-- A login with no user in a database can't open it. That's the whole split:
-- appdata_svc gets a user only in appdata, iam_svc only in iam.
USE appdata;
GO
IF USER_ID('appdata_svc') IS NULL CREATE USER appdata_svc FOR LOGIN appdata_svc;
-- shortcut: db_owner lets the same login run Flyway (DDL) and the app; split into a migration login and a read/write-only app login if this ever faces real users.
ALTER ROLE db_owner ADD MEMBER appdata_svc;
GO

USE iam;
GO
IF USER_ID('iam_svc') IS NULL CREATE USER iam_svc FOR LOGIN iam_svc;
ALTER ROLE db_owner ADD MEMBER iam_svc;
GO
