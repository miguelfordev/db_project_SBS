package conta;

import db.Conexao;
import java.sql.*;
import java.util.Scanner;

public class CriarConta {

    public static void executar(Scanner scanner) {

        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);

            System.out.println("\n=== Criar Conta ===");

            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            System.out.print("CPF (somente números): ");
            String cpf = scanner.nextLine();

            System.out.print("Data de nascimento (AAAA-MM-DD): ");
            String dataNascimentoStr = scanner.nextLine();
            Date dataNascimento;
            try {
                dataNascimento = Date.valueOf(dataNascimentoStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Formato de data inválido. Use AAAA-MM-DD.");
                return;
            }

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Senha (somente números): ");
            int senha = scanner.nextInt();
            scanner.nextLine(); 
            
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT 1 FROM Cliente WHERE cpf = ? OR email = ?")) {
                checkStmt.setString(1, cpf);
                checkStmt.setString(2, email);
                try (ResultSet checkRs = checkStmt.executeQuery()) {
                    if (checkRs.next()) {
                        System.out.println("CPF ou email já cadastrados.");
                        conn.rollback();
                        return;
                    }
                }
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id_agencia, nome, cidade FROM Agencia")) { 
                System.out.println("\nAgências:");
                
                while (rs.next()) {
                    System.out.printf("%d - %s (%s)%n", rs.getInt("id_agencia"), rs.getString("nome"), rs.getString("cidade"));
                }
            }

            System.out.print("Escolha o ID da agência: ");
            int idAgencia = scanner.nextInt();
            scanner.nextLine(); 


            String sqlCliente = "INSERT INTO Cliente (nome, cpf, data_nascimento, email, senha, id_agencia) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_cliente";
            try (PreparedStatement psCliente = conn.prepareStatement(sqlCliente)) {
                psCliente.setString(1, nome);
                psCliente.setString(2, cpf);
                psCliente.setDate(3, dataNascimento);
                psCliente.setString(4, email);
                psCliente.setInt(5, senha);
                psCliente.setInt(6, idAgencia);

                try (ResultSet clienteResult = psCliente.executeQuery()) {
                    if (clienteResult.next()) {
                        int idCliente = clienteResult.getInt("id_cliente");

                        System.out.print("Tipo de conta (corrente/poupança): ");
                        String tipo = scanner.nextLine().toLowerCase();

                        if (!tipo.equals("corrente") && !tipo.equals("poupança")) {
                            System.out.println("Tipo de conta inválido. Escolha 'corrente' ou 'poupança'.");
                            conn.rollback(); 
                            return;
                        }

                        String sqlConta = "INSERT INTO Conta (tipo, data_abertura, id_cliente) VALUES (?, CURRENT_DATE, ?)";
                        try (PreparedStatement psConta = conn.prepareStatement(sqlConta)) {
                            psConta.setString(1, tipo);
                            psConta.setInt(2, idCliente);
                            psConta.executeUpdate(); 

                            conn.commit(); 
                            System.out.println("Conta criada com sucesso!");
                        }
                    } else {
                        System.out.println("Erro ao obter o ID do cliente após a inserção.");
                        conn.rollback();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro de banco de dados ao criar conta: " + e.getMessage());
            e.printStackTrace();
            try (Connection conn = Conexao.conectar()) { 
                if (conn != null && !conn.getAutoCommit()) {
                    conn.rollback();
                    System.out.println("Transação desfeita devido a erro.");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao tentar desfazer a transação: " + rollbackEx.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Erro inesperado ao criar conta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}