import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClienteGeral {
    public String addr;
    public int port;

    public ClienteGeral(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    static void main() {

        // Para clientesGerais
        ClienteGeral cl = new ClienteGeral("localhost", 9000);
        try {
            Socket conn = new Socket(cl.addr, cl.port);

            System.out.println("Opções do servidor: ");
            System.out.println("1.  Registrar Jogador                2. Inscrever no Torneio            3. Listar Todos os Torneios");
            System.out.println("4.  Listar Torneios por Jogadores    5. Listar Torneios por Estados     6. Listar Todas as Partidas");
            System.out.println("7.  Listar Partidas por Jogadores    8. Listar Partidas por Torneios    9. Listar Todos os Jogadores");
            System.out.println("10. Listar Jogadores por Torneio     11. Listar Jogadores por Estado    12. Sair ");

            while (true) {

                System.out.print("Escolha uma opção: ");
                Scanner sc = new Scanner(System.in);
                String op = sc.nextLine();
                menu(op, conn);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menu das opções
    public static void menu(String op, Socket conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // Registar Jogador
        switch (op) {
            case "1" -> {
                System.out.print("Insira o nome do Jogador: ");
                String name = br.readLine();
                System.out.print("Insira o email do Jogador: ");
                String email = br.readLine();
                System.out.print("Insira o clube do Jogador: ");
                String clube = br.readLine();
                ResgJogador obj = new ResgJogador(name, email, clube);

                ResgJogador response = (ResgJogador) enviarObj(obj, conn);

                if (response != null && response.completed) {
                    System.out.println("O Jogador foi registrado com sucesso");
                } else {
                    System.out.println("Jogador não foi registrado.");
                }
            }

            // Inscrever Torneio
            case "2" -> {
                System.out.print("Insira o id do Torneio: ");
                int torneio = Integer.parseInt(br.readLine());
                System.out.print("Insira o id do Jogador: ");
                int jogador = Integer.parseInt(br.readLine());

                InscreverTorneio obj = new InscreverTorneio(torneio, jogador);

                InscreverTorneio response = (InscreverTorneio) enviarObj(obj, conn);

                if (response != null && response.completed) {
                    System.out.println("O Jogador foi registrado com sucesso");
                } else {
                    System.out.println("Jogador não foi registrado.");
                }
            }

            // Listar Todos Torneios
            case "3" -> {
                System.out.println("Torneios Registados: ");
                ListarTorneios obj = new ListarTorneios();

                ListarTorneios response = (ListarTorneios) enviarObj(obj, conn);

                if (response != null && response instanceof ListarTorneios) {
                    response.mostrarListaTorneios();
                } else {
                    System.out.println("Erro ao listar Torneios.");
                }
            }

            // Listar Torneios por Jogador
            case "4" -> {
                System.out.print("Insira o id do Jogador: ");
                int idJogador = Integer.parseInt(br.readLine());

                System.out.println("Torneios Registados: ");
                ListarTorneios obj = new ListarTorneios(idJogador);

                ListarTorneios response = (ListarTorneios) enviarObj(obj, conn);

                if (response != null && response instanceof ListarTorneios) {
                    response.mostrarListaTorneios();
                } else {
                    System.out.println("Erro ao listar Torneios.");
                }
            }

            // Listar Torneios por Estado
            case "5" -> {

                System.out.println("Selecione um Estado: ");
                System.out.println("1- Agendado     2- Ativo      3- Concluído");
                op = br.readLine();

                //Seleciona o estado apartir da escolha
                op = estadoTorneio(op);

                if (op != null) {
                    System.out.println("Torneios Registados: ");
                    ListarTorneios obj = new ListarTorneios(op);

                    ListarTorneios response = (ListarTorneios) enviarObj(obj, conn);

                    if (response != null && response instanceof ListarTorneios) {
                        response.mostrarListaTorneios();
                    } else {
                        System.out.println("Erro ao listar Torneios.");
                    }
                }
            }
            // Listar Todas as Partidas
            case "6" -> {

                System.out.println("Partidas Registados: ");
                ListarPartidas obj = new ListarPartidas();
                enviarObj(obj, conn);

                ListarPartidas response = (ListarPartidas) enviarObj(obj, conn);

                if (response != null && response instanceof ListarPartidas) {
                    response.mostrarListaPartidas();
                } else {
                    System.out.println("Erro ao listar Partidas.");
                }
            }

            // Listar Partidas por Jogador
            case "7" -> {

                System.out.print("Selecione o ID do Jogador: ");
                int idJogador = Integer.parseInt(br.readLine());

                System.out.println("Partidas Registados: ");
                ListarPartidas obj = new ListarPartidas(idJogador, idJogador);
                enviarObj(obj, conn);

                ListarPartidas response = (ListarPartidas) enviarObj(obj, conn);

                if (response != null && response instanceof ListarPartidas) {
                    response.mostrarListaPartidas();
                } else {
                    System.out.println("Erro ao listar Partidas.");
                }
            }

            // Listar Partidas por Torneio
            case "8" -> {
                System.out.print("Selecione o ID do Torneio: ");
                int idTorneio = Integer.parseInt(br.readLine());

                System.out.println("Partidas Registados: ");
                ListarPartidas obj = new ListarPartidas(idTorneio);
                enviarObj(obj, conn);

                ListarPartidas response = (ListarPartidas) enviarObj(obj, conn);

                if (response != null && response instanceof ListarPartidas) {
                    response.mostrarListaPartidas();
                } else {
                    System.out.println("Erro ao listar Partidas.");
                }
            }

            // Listar Todos os Jogadores
            case "9" -> {

                System.out.println("Jogadores Registados: ");
                ListarJogadores obj = new ListarJogadores();

                ListarJogadores response = (ListarJogadores) enviarObj(obj, conn);

                if (response != null && response instanceof ListarJogadores) {
                    response.mostrarListaJogadores();
                } else {
                    System.out.println("Erro ao listar jogadores.");
                }

            }

            // Listar Jogadores por Torneio
            case "10" -> {
                System.out.print("Selecione um Torneio: ");
                op = br.readLine();

                System.out.println("Jogadores Registados: ");
                ListarJogadores obj = new ListarJogadores(Integer.parseInt(op));

                ListarJogadores response = (ListarJogadores) enviarObj(obj, conn);

                if (response != null && response instanceof ListarJogadores) {
                    response.mostrarListaJogadores();
                } else {
                    System.out.println("Erro ao listar jogadores.");
                }
            }

            //Listar Jogadores por Estado
            case "11" -> {

                System.out.println("Selecione um Estado: ");
                System.out.println("1- Inscrito     2- Em Jogo      3- Eliminado");
                op = br.readLine();

                // Seleciona o estado apartir da escolha
                op = estadoJogador(op);

                // Se a opção for válida
                if (op != null) {
                    System.out.println("Jogadores Registados: ");
                    ListarJogadores obj = new ListarJogadores(op);

                    ListarJogadores response = (ListarJogadores) enviarObj(obj, conn);

                    if (response != null && response instanceof ListarJogadores) {
                        response.mostrarListaJogadores();
                    } else {
                        System.out.println("Erro ao listar jogadores.");
                    }
                }
            }
            // Sair do programa
            case "12" -> System.exit(0);
            default -> System.out.println("Opção Inválida");
        }
    }

    public static String estadoJogador(String op) {
        switch (op) {
            case "1" -> {
                return "Inscrito";
            }
            case "2" -> {
                return "Em Jogo";
            }
            case "3" -> {
                return "Eliminado";
            }
            default -> {
                System.out.println("Opção Inválida");
                return null;
            }
        }
    }

    public static String estadoTorneio(String op) {
        switch (op) {
            case "1" -> {
                return "Agendado";
            }
            case "2" -> {
                return "Ativo";
            }
            case "3" -> {
                return "Concluído";
            }
            default -> {
                System.out.println("Opção Inválida");
                return null;
            }
        }
    }

    // Função principal de enviar Objetos
    public static funcGeral enviarObj(funcGeral funcX, Socket conn) {
        try {

            // Enviar Objeto
            ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());

            // Escrever Objeto
            out.writeObject(funcX);

            // Ler resposta de Retorno
            ObjectInputStream in = new ObjectInputStream(conn.getInputStream());

            return (funcGeral) in.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}