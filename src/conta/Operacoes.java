package conta;

import db.Conexao;
import java.sql.*;
import java.util.Scanner;

public class Operacoes {

    public static void sacar(Scanner scanner, int idCliente) {
        
    	try (Connection conn = Conexao.conectar()) {
            int idConta = pegarIdConta(conn, idCliente);
            if (idConta == -1) {
                System.out.println("Conta não encontrada.");
                return;
            }

            System.out.print("Informe o valor para saque: ");
            double valor = scanner.nextDouble();
            
            double saldo = calcularSaldo(conn, idConta);
            if (valor <= 0) {
                System.out.println("Valor inválido.");
                return;
            }
            if (valor > saldo) {
                System.out.println("Saldo insuficiente.");
                return;
            }
    
            String sql = "INSERT INTO Transacao (tipo, valor, id_conta) VALUES ('saque', ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, valor);
            ps.setInt(2, idConta);
            ps.executeUpdate();

            System.out.println("Saque realizado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro no saque: " + e.getMessage());
        }
    }

    public static void depositar(Scanner scanner, int idCliente) {
        
    	try (Connection conn = Conexao.conectar()) {
            int idConta = pegarIdConta(conn, idCliente);
            if (idConta == -1) {
                System.out.println("Conta não encontrada.");
                return;
            }

            System.out.print("Informe o valor para depósito: ");
            double valor = scanner.nextDouble();

            if (valor <= 0) {
                System.out.println("Valor inválido.");
                return;
            }

            String sql = "INSERT INTO Transacao (tipo, valor, id_conta) VALUES ('deposito', ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, valor);
            ps.setInt(2, idConta);
            ps.executeUpdate();

            System.out.println("Depósito realizado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro no depósito: " + e.getMessage());
        }
    }

    public static void transferir(Scanner scanner, int idCliente) {
        
    	try (Connection conn = Conexao.conectar()) {
            int idContaOrigem = pegarIdConta(conn, idCliente);
            if (idContaOrigem == -1) {
                System.out.println("Conta de origem não encontrada.");
                return;
            }

            System.out.print("Informe o ID da conta destino: ");
            int idContaDestino = scanner.nextInt();

            if (idContaDestino == idContaOrigem) {
                System.out.println("Não é possível transferir para a mesma conta.");
                return;
            }

            System.out.print("Informe o valor para transferência: ");
            double valor = scanner.nextDouble();

            if (valor <= 0) {
                System.out.println("Valor inválido.");
                return;
            }

            double saldo = calcularSaldo(conn, idContaOrigem);
            if (valor > saldo) {
                System.out.println("Saldo insuficiente.");
                return;
            }

            String sqlCheckDestino = "SELECT 1 FROM Conta WHERE id_conta = ?";
            PreparedStatement psCheck = conn.prepareStatement(sqlCheckDestino);
            psCheck.setInt(1, idContaDestino);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next()) {
                System.out.println("Conta destino não encontrada.");
                return;
            }

            try {
                conn.setAutoCommit(false);

                String sqlTransferencia = "INSERT INTO Transferencia (id_conta_origem, id_conta_destino, valor) VALUES (?, ?, ?)";
                PreparedStatement psTransfer = conn.prepareStatement(sqlTransferencia);
                psTransfer.setInt(1, idContaOrigem);
                psTransfer.setInt(2, idContaDestino);
                psTransfer.setDouble(3, valor);
                psTransfer.executeUpdate();

                String sqlSaque = "INSERT INTO Transacao (tipo, valor, id_conta) VALUES ('saque', ?, ?)";
                PreparedStatement psSaque = conn.prepareStatement(sqlSaque);
                psSaque.setDouble(1, valor);
                psSaque.setInt(2, idContaOrigem);
                psSaque.executeUpdate();

                String sqlDeposito = "INSERT INTO Transacao (tipo, valor, id_conta) VALUES ('deposito', ?, ?)";
                PreparedStatement psDeposito = conn.prepareStatement(sqlDeposito);
                psDeposito.setDouble(1, valor);
                psDeposito.setInt(2, idContaDestino);
                psDeposito.executeUpdate();

                conn.commit();
                System.out.println("Transferência realizada com sucesso!");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro na transferência: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Erro na transferência: " + e.getMessage());
        }
    }

    public static void alterarDados(Scanner scanner, int idCliente) {
        
    	try (Connection conn = Conexao.conectar()) {
            System.out.print("Novo nome: ");
            String novoNome = scanner.nextLine();

            System.out.print("Novo CPF (somente números): ");
            String novoCpf = scanner.nextLine();

            String sqlCheck = "SELECT 1 FROM Cliente WHERE cpf = ? AND id_cliente <> ?";
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setString(1, novoCpf);
            psCheck.setInt(2, idCliente);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                System.out.println("CPF já está em uso por outro cliente.");
                return;
            }

            String sql = "UPDATE Cliente SET nome = ?, cpf = ? WHERE id_cliente = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, novoNome);
            ps.setString(2, novoCpf);
            ps.setInt(3, idCliente);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Dados atualizados com sucesso!");
            } else {
                System.out.println("Falha ao atualizar dados.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar dados: " + e.getMessage());
        }
    }
    
    public static void excluirUsuario(int id) {
    	try (Connection conn = Conexao.conectar()){
    		 String call = "{ call remover_usuario(?) }"; 

    	        CallableStatement cs = conn.prepareCall(call);
    	        cs.setInt(1, id); 
    	        cs.execute();

    	        System.out.println("Seu usuário foi excluído!");
			 
    		conn.close();
		} catch (SQLException e) { 
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            e.printStackTrace(); 
        } catch (Exception e) { 
            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    public static void consultarSaldo(int idCliente) {
        
    	try (Connection conn = Conexao.conectar()) {
            int idConta = pegarIdConta(conn, idCliente);
            if (idConta == -1) {
                System.out.println("Conta não encontrada.");
                return;
            }

            double saldo = calcularSaldo(conn, idConta);
            System.out.printf("Saldo atual: R$ %.2f%n", saldo);
        } catch (SQLException e) {
            System.out.println("Erro ao consultar saldo: " + e.getMessage());
        }
    }

    public static void consultarTransferencias(int idCliente) {
        
    	try (Connection conn = Conexao.conectar()) {
            int idConta = pegarIdConta(conn, idCliente);
            if (idConta == -1) {
                System.out.println("Conta não encontrada.");
                return;
            }

            String sql = "SELECT id_transferencia, id_conta_origem, id_conta_destino, valor, data " +
                         "FROM Transferencia WHERE id_conta_origem = ? OR id_conta_destino = ? ORDER BY data DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idConta);
            ps.setInt(2, idConta);
            ResultSet rs = ps.executeQuery();

            System.out.println("Histórico de Transferências:");
            boolean temTransferencia = false;
            while (rs.next()) {
                temTransferencia = true;
                System.out.printf("ID: %d | Origem: %d | Destino: %d | Valor: R$ %.2f | Data: %s%n",
                        rs.getInt("id_transferencia"),
                        rs.getInt("id_conta_origem"),
                        rs.getInt("id_conta_destino"),
                        rs.getDouble("valor"),
                        rs.getTimestamp("data").toString());
            }
            if (!temTransferencia) {
                System.out.println("Nenhuma transferência encontrada.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar transferências: " + e.getMessage());
        }
    }
    
    public static void consultarClientesEContas() {
        String sql = "select * from vw_clientes_com_contas";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
        	if(rs.next()) {
	            while (rs.next()) {
	                String nome = rs.getString("nome");
	                String cpf = rs.getString("cpf");
	                int numeroConta = rs.getInt("id_conta");
	                String tipoConta = rs.getString("tipo");
	
	                System.out.println("Nome: " + nome +
	                                   ", CPF: " + cpf +
	                                   ", Conta: " + numeroConta +
	                                   ", Tipo: " + tipoConta);
	            }
        	}else {
        		System.out.println("Não tem nenhum registro.");
        	}

        } catch (SQLException e) {
            System.out.println("Erro ao consultar: " + e.getMessage());
        }
    }

    private static int pegarIdConta(Connection conn, int idCliente) throws SQLException {
        String sql = "SELECT id_conta FROM Conta WHERE id_cliente = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idCliente);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id_conta");
        }
        return -1;
    }

    private static double calcularSaldo(Connection conn, int idConta) throws SQLException {
        String sql = "SELECT tipo, SUM(valor) as total FROM Transacao WHERE id_conta = ? GROUP BY tipo";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idConta);
        ResultSet rs = ps.executeQuery();

        double saldo = 0.0;
        while (rs.next()) {
            String tipo = rs.getString("tipo");
            double total = rs.getDouble("total");
            if ("deposito".equals(tipo)) {
                saldo += total;
            } else if ("saque".equals(tipo)) {
                saldo -= total;
            }
        }
        return saldo;
    }
}

