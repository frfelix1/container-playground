-- Create initial table structure. 
-- IDENTITY(x,y) generates the id value of each new row. x is the initial value, y is the increment.
CREATE TABLE projects (
id BIGINT IDENTITY(1, 1) PRIMARY KEY,
name NVARCHAR(200) NOT NULL
);
