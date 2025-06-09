package conta;

import db.Conexao;
import java.sql.*;
import java.util.Scanner;

public class Login {
    public static void executar(Scanner scanner) {
        System.out.println("\n=== Login ===");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        int senha = scanner.nextInt();

        try (Connection conn = Conexao.conectar()) {
            String sql = "SELECT id_cliente, nome FROM Cliente WHERE email = ? AND senha = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, senha);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int idCliente = rs.getInt("id_cliente");
                String nome = rs.getString("nome");
                System.out.println("Login realizado com sucesso! Bem-vindo(a), " + nome);

                MenuOperacoes.executar(scanner, idCliente);
            } else {
                System.out.println("Email ou senha inválidos.");
            }
        } catch (SQLException e) {
            System.out.println("Erro no login: " + e.getMessage());
        }
    }
}
