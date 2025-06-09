INSERT INTO Agencia (nome, cidade) VALUES
('Agência Central', 'São Paulo'),
('Agência Norte', 'Brasília');

INSERT INTO Cliente (nome, cpf, data_nascimento, email, senha, id_agencia) VALUES
('Maria Silva', '12345678900', '1990-05-12', 'maria@gmail.com', 123456, 1),
('João Souza', '98765432100', '1985-03-20', 'joao@gmail.com', 7891011, 1),
('Ana Costa', '11122233344', '1992-09-10', 'ana@gmail.com', 112233, 2);

INSERT INTO Conta (tipo, data_abertura, id_cliente) VALUES
('corrente', CURRENT_DATE, 1),
('poupança', CURRENT_DATE, 2),
('corrente', CURRENT_DATE, 3);

INSERT INTO Transacao (tipo, valor, id_conta) VALUES
('deposito', 1000.00, 1),
('saque', 200.00, 1);

INSERT INTO Transacao (tipo, valor, id_conta) VALUES
('deposito', 500.00, 2);

INSERT INTO Transacao (tipo, valor, id_conta) VALUES
('deposito', 800.00, 3);

INSERT INTO Transferencia (id_conta_origem, id_conta_destino, valor) VALUES
(1, 3, 150.00);
