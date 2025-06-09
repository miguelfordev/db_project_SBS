CREATE DATABASE ucsalsbs
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

COMMENT ON DATABASE ucsalsbs
    IS 'Banco de Dados para atividade de Sistema Bancário Simples. (SBS)';