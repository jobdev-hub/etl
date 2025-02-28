# Configuração usuário API oracle-db ambiente de desenvolvimento

## Acesse o container do banco de dados Oracle
```bash
docker exec -it oracle-db bash
```

## Acesse o SQLPlus como SYSDBA
```bash
sqlplus / as sysdba
```

## Configure o PDB XEPDB1
```sql
-- Verifica status do PDB
SELECT name, open_mode FROM v$pdbs;

-- Garante que XEPDB1 está aberto
ALTER PLUGGABLE DATABASE XEPDB1 OPEN;
ALTER PLUGGABLE DATABASE XEPDB1 SAVE STATE;

-- Altera sessão para o XEPDB1
ALTER SESSION SET CONTAINER = XEPDB1;
```

## Crie o usuário API
```sql
CREATE USER API IDENTIFIED BY api123;

-- Concede privilégios ao usuário API
GRANT CREATE SESSION TO API;
GRANT CREATE TABLE TO API;
GRANT CREATE VIEW TO API;
GRANT CREATE SEQUENCE TO API;
GRANT CREATE PROCEDURE TO API;
GRANT UNLIMITED TABLESPACE TO API;

-- Concede privilégios de manipulação de dados
GRANT SELECT ANY TABLE TO API;
GRANT INSERT ANY TABLE TO API;
GRANT UPDATE ANY TABLE TO API;
GRANT DELETE ANY TABLE TO API;
```

## Verifique a criação
```sql
-- Verifica usuário
SELECT username, account_status FROM dba_users WHERE username = 'API';

-- Verifica privilégios
SELECT privilege FROM dba_sys_privs WHERE grantee = 'API';
```

## Testa a conexão
```sql
-- Conecta com o usuário API
CONNECT API/api123@XEPDB1;

-- Cria tabela de teste
CREATE TABLE test_table (id NUMBER PRIMARY KEY, name VARCHAR2(50));

-- Insere dados na tabela de teste
INSERT INTO test_table VALUES (1, 'Test API User');

-- Verifica dados na tabela de teste
SELECT * FROM test_table;
```