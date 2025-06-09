package conta;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuOperacoes {

    public static void executar(Scanner scanner, int idCliente) {
        boolean logado = true;

        while (logado) {
            System.out.println("\n=== MENU DE OPERAÇÕES ===");
            System.out.println("1 - Sacar");
            System.out.println("2 - Depositar");
            System.out.println("3 - Transferir");
            System.out.println("4 - Alterar Dados (Nome, CPF)");
            System.out.println("5 - Consultar Saldo");
            System.out.println("6 - Consultar Transferências");
            System.out.println("7 - Excluir Conta");
            System.out.println("8 - Logout");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        Operacoes.sacar(scanner, idCliente);
                        break;
                    case 2:
                        Operacoes.depositar(scanner, idCliente);
                        break;
                    case 3:
                        Operacoes.transferir(scanner, idCliente);
                        break;
                    case 4:
                        Operacoes.alterarDados(scanner, idCliente);
                        break;
                    case 5:
                        Operacoes.consultarSaldo(idCliente);
                        break;
                    case 6:
                        Operacoes.consultarTransferencias(idCliente);
                        break;
                    case 7:
                        logado = false;
                        Operacoes.excluirUsuario(idCliente);
                        System.out.println("Conta excluída com sucesso.");
                        break;
                    case 8:
                        logado = false;
                        System.out.println("Logout realizado.");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite apenas números.");
                scanner.nextLine(); 
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
    }
}