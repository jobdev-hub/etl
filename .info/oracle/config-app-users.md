# Configuração app users oracle-db em ambiente de desenvolvimento

# Conecte-se ao container
```bash
docker exec -it oracle-db bash
```

# Acesse o SQLPlus como SYSDBA
```bash
sqlplus / as sysdba
```

# Crie os usuários APP_MAIN e APP_BATCH
```sql
ALTER SESSION SET CONTAINER = XEPDB1;

CREATE USER APP_MAIN IDENTIFIED BY APP_MAIN_123;
GRANT DBA TO APP_MAIN;

CREATE USER APP_BATCH IDENTIFIED BY APP_BATCH_123;
GRANT DBA TO APP_BATCH;
```