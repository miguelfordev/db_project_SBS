CREATE TABLE Agencia (
    id_agencia SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL
);

CREATE TABLE Cliente (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha INT NOT NULL,
    id_agencia INT NOT NULL REFERENCES Agencia(id_agencia)
);

CREATE TABLE Conta (
    id_conta SERIAL PRIMARY KEY,
    tipo VARCHAR(20) CHECK (tipo IN ('corrente', 'poupança')),
    data_abertura DATE NOT NULL DEFAULT CURRENT_DATE,
    id_cliente INT NOT NULL REFERENCES Cliente(id_cliente)
);

CREATE TABLE Transacao (
    id_transacao SERIAL PRIMARY KEY,
    tipo VARCHAR(10) CHECK (tipo IN ('deposito', 'saque')),
    valor NUMERIC(10,2) NOT NULL CHECK (valor > 0),
    data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_conta INT NOT NULL REFERENCES Conta(id_conta)
);

CREATE TABLE Transferencia (
    id_transferencia SERIAL PRIMARY KEY,
    id_conta_origem INT NOT NULL REFERENCES Conta(id_conta),
    id_conta_destino INT NOT NULL REFERENCES Conta(id_conta),
    valor NUMERIC(10,2) NOT NULL CHECK (valor > 0),
    data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);