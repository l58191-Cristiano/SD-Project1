import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ClienteAdm {

    String regHost;
    String regPort;

    //Registro
    public ClienteAdm(String regHost, String regPort) {
        this.regHost = regHost;
        this.regPort = regPort;
    }

    static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {

        ClienteAdm cl = new ClienteAdm("localhost", "1099");
        funcAdm objServidor = (funcAdm) java.rmi.Naming.lookup("rmi://" + cl.regHost + ":" + cl.regPort + "/funcAdm");

        try {

            System.out.println("Opções do servidor: ");
            System.out.println("1- Listar Jogadores por Estado de Aprovação     2- Listar Torneios por Estado de Aprovação      3- Listar Partidas ");
            System.out.println("4- Regsitar uma Partida                         5- Atualizar Estado de uma Partida              6- Atualizar Resultado de uma Partida ");
            System.out.println("7- Registrar um Torneio                         8- Aprovar um torneio                           9- Modificar o Estado de um Torneio");
            System.out.println("10- Aprovar um Jogador                          11- Atualizar Rating de um Jogador              12- Atualizar Estado de um JOgador");
            System.out.println("13- Sair do servidor.");
            //Chamar MÉTODOS AQUI
            menu(objServidor);

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void verListaJ(List<Jogador> list){
        System.out.println("--------------------------------------");
        System.out.println("| ID do Jogador |       Estado       |");
        for(Jogador jogador: list){
            System.out.printf("| %-14d| %-19s|\n", jogador.id_jogador(), jogador.estado_admin());
        }
        System.out.println("--------------------------------------");
    }
    public static void verListaT(List<Torneio> list){
        System.out.println("--------------------------------------");
        System.out.println("| ID do Torneio |       Estado       |");
        for(Torneio torneio : list){
            System.out.printf("| %-14d| %-19s|\n", torneio.id_torneio(), torneio.estado_admin());
        }
        System.out.println("--------------------------------------");
    }

    //Não é usado por enquanto
    public static void verListaP(List<Partida> list){
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("| ID | ID Torneio | ID Jogador1 | ID Jogador2 |       Estado       | Vencedor |");
        for (Partida partida : list) {
            System.out.printf("| %-3d| %-11d| %-12d| %-12d| %-19s| %-9d|\n", partida.id_partida(), partida.id_torneio(), partida.id_jogador1(), partida.id_jogador2(), partida.estado_partida(), partida.ganhador());
        }
        System.out.println("-------------------------------------------------------------------------------");
    }

    public static void menu(funcAdm objServidor) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(true){

            System.out.print("Escolha uma opção: ");
            String op = br.readLine();

            //Listar Jogadores
            if(op.equals("1")){

                System.out.println("Selecione o Estado de Aprovação");
                System.out.println("1- Aprovado         2- Não Aprovado");
                op = br.readLine();

                op = estadoAprovacao(op);

                if(op != null) {
                    System.out.println("Lista dos Jogadores:");
                    List<Jogador> lista = objServidor.listarJogadoresAdmin(op);
                    verListaJ(lista);
                }
            }
            //Listar Torneios
            else if(op.equals("2")){

                    System.out.println("Selecione o Estado de Aprovação");
                    System.out.println("1- Aprovado         2- Não Aprovado");
                    op = br.readLine();
                    op = estadoAprovacao(op);

                if(op != null) {
                    System.out.println("Lista dos Torneios:");
                    List<Torneio> lista = objServidor.listarTorneiosAdmin(op);
                    verListaT(lista);
                }
            }
            //Listar Partidas
            else if(op.equals("3")){

                System.out.println("Lista dos Torneios:");
                List<Partida> lista = objServidor.listarPartidas();
                verListaP(lista);

            }
            //Registrar Partidas
            else if(op.equals("4")){

                System.out.print("Escreva o id do Torneio: ");
                int id_torneio = Integer.parseInt(br.readLine());
                System.out.print("Escreva o id do Jogador 1: ");
                int id_jogador1 = Integer.parseInt(br.readLine());
                System.out.print("Escreva o id do Jogador 2: ");
                int id_jogador2 = Integer.parseInt(br.readLine());

                boolean result = objServidor.registarPartida(id_torneio, id_jogador1, id_jogador2);
                if(result == true){
                    System.out.println("Partida registrada com sucesso.");
                }
                else{
                    System.out.println("A partida não foi registrada.");
                }

            }
            //Atualizar o Estado de uma Partida
            else if(op.equals("5")){

                System.out.print("Selecione uma partida:  (Insira o seu id)");
                int id_partida = Integer.parseInt(br.readLine());

                System.out.print("Selecione um Estado para a Partida: ");
                System.out.println("1- Agendada         2- Decorrer         3- Encerrado");
                op = estadoPartidas(br.readLine());
                if(op != null) {
                    boolean result = objServidor.estadoPartida(id_partida, op);
                    if(result == true){
                        System.out.println("Estado da Partida Atualizado com sucesso.");
                    }
                    else{
                        System.out.println("O Estado não foi atualizado.");
                    }
                }

            }
            //Atualizar o resultado de uma partida
            else if(op.equals("6")){

                System.out.print("Selecione uma partida:  (Insira o seu id)");
                int id_partida = Integer.parseInt(br.readLine());

                System.out.print("Selecione o ID do Vencedor: ");
                int id_vencedor = Integer.parseInt(br.readLine());

                boolean result = objServidor.resultadoPartida(id_partida, id_vencedor);
                if(result == true){
                    System.out.println("Resultado da Partida Atualizado com sucesso.");
                }
                else{
                    System.out.println("O Resultado não foi atualizado.");
                }
            }
            //Registrar Torneio
            else if(op.equals("7")){
                try {

                    System.out.print("Selecione o nome do Torneio: ");
                    String nome = br.readLine();

                    System.out.print("Selecione a data do Torneio (yyyy-MM-dd): ");
                    String data_torneio = br.readLine();
                    // Define the date format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // Parse the string to java.util.Date
                    java.util.Date utilDate = dateFormat.parse(data_torneio);
                    // Convert java.util.Date to java.sql.Date
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                    System.out.print("Selecione o local do Torneio: ");
                    String local = br.readLine();
                    System.out.print("Selecione o premio do Torneio: ");
                    int premio = Integer.parseInt(br.readLine());

                    boolean result = objServidor.registarTorneios(nome, sqlDate, local, premio);
                }
                catch(Exception e){
                    System.out.println("Input Inválido.");
                }

            }
            //Aprovar o Torneio
            else if(op.equals("8")){

                System.out.print("Selecione o ID do Torneio: ");
                int id_torneio = Integer.parseInt(br.readLine());

                Boolean result = objServidor.aprovarTorneios(id_torneio);
                if(result == true){
                    System.out.println("Torneio aprovado com sucesso.");
                }
                else{
                    System.out.println("Torneio não foi aprovado.");
                }

            }
            //Atualizar o Estado de um Torneio
            else if(op.equals("9")){

                System.out.print("Selecione o ID do Torneio: ");
                int id_torneio = Integer.parseInt(br.readLine());

                System.out.print("Selecione um Estado para o Torneio: ");
                System.out.println("1- Agendado         2- Ativo         3- Encerrado");
                op = estadoTorneios(br.readLine());
                if(op != null) {
                    boolean result = objServidor.estadoGeralTorneio(id_torneio, op);
                    if(result == true){
                        System.out.println("Estado do Torneio Atualizado com sucesso.");
                    }
                    else{
                        System.out.println("O Estado não foi atualizado.");
                    }
                }

            }
            //Aprovar Jogador
            else if(op.equals("10")){

                System.out.print("Selecione o ID do Jogador: ");
                int id_jogador = Integer.parseInt(br.readLine());

                Boolean result = objServidor.aprovarJogador(id_jogador);
                if(result == true){
                    System.out.println("Jogador aprovado com sucesso.");
                }
                else{
                    System.out.println("Jogador não foi aprovado.");
                }

            }
            //Atualizar Rating de um Jogador
            else if(op.equals("11")){

                System.out.print("Selecione o ID do Jogador: ");
                int id_jogador = Integer.parseInt(br.readLine());
                System.out.print("Selecione o novo Rating do Jogador: ");
                int new_rating = Integer.parseInt(br.readLine());

                Boolean result = objServidor.ratingJogador(id_jogador, new_rating);
                if(result == true){
                    System.out.println("Rating Atualizado com sucesso.");
                }
                else{
                    System.out.println("Rating não foi atualizado.");
                }

            }
            //Atualizar Estado de um Jogador
            else if(op.equals("12")){

                System.out.print("Selecione o ID do Jogador: ");
                int id_jogador = Integer.parseInt(br.readLine());

                System.out.print("Selecione um Estado para o Jogador: ");
                System.out.println("1- Inscrito         2- Em Jogo         3- Eliminado");
                op = estadoJogadores(br.readLine());
                if(op != null) {
                    boolean result = objServidor.estadoGeralJogador(id_jogador, op);
                    if(result == true){
                        System.out.println("Estado do Jogador atualizado com sucesso.");
                    }
                    else{
                        System.out.println("O Estado não foi atualizado.");
                    }
                }

            }
            //Sair do Programa
            else if(op.equals("13")){
                System.exit(0);
            }
            else{
                System.out.println("Escolha Inválida.");
            }

        }



    }

    public static String estadoAprovacao(String op){
        if(op.equals("1")){
            return "Aprovado";
        }
        else if(op.equals("2")){
            return "Não Aprovado";
        }
        else{
            System.out.println("Opção Inválida");
            return null;
        }
    }

    public static String estadoPartidas(String op){
        if(op.equals("1")){
            return "Agendado";
        }
        else if(op.equals("2")){
            return "Decorrer";
        }
        else if(op.equals("3")){
            return "Encerrado";
        }
        else{
            System.out.println("Opção Inválida");
            return null;
        }
    }

    public static String estadoTorneios(String op){
        if(op.equals("1")){
            return "Agendado";
        }
        else if(op.equals("2")){
            return "Ativo";
        }
        else if(op.equals("3")){
            return "Encerrado";
        }
        else{
            System.out.println("Opção Inválida");
            return null;
        }
    }

    public static String estadoJogadores(String op){
        if(op.equals("1")){
            return "Inscrito";
        }
        else if(op.equals("2")){
            return "Em Jogo";
        }
        else if(op.equals("3")){
            return "Eliminado";
        }
        else{
            System.out.println("Opção Inválida");
            return null;
        }
    }
}
