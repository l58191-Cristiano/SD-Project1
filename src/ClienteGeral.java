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

    public static void main() {

        //Para clientesGerais
        ClienteGeral cl = new ClienteGeral("localhost", 9000);
        try {
            Socket conn = new Socket(cl.addr, cl.port);

            System.out.println("Opções do servidor: ");
            System.out.println("1.  Registrar Jogador                2. Inscrever no Torneio            3. Listar Todos os Torneios");
            System.out.println("4.  Listar Torneios por Jogadores    5. Listar Torneios por Estados     6. Listar Todas as Partidas");
            System.out.println("7.  Listar Partidas por Jogadores    8. Listar Partidas por Torneios    9. Listar Todos os Jogadores");
            System.out.println("10. Listar Jogadores por Torneio     11. Listar Jogadores por Estado    12. Sair ");

            while(true){

                System.out.print("Escolha uma opção: ");
                Scanner sc = new Scanner(System.in);
                String op = sc.nextLine();
                menu(op, conn);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Menu das opções
    public static void menu(String op, Socket conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //Registrar Jogador
        if(op.equals("1")) {
            System.out.print("Insira o nome do Jogador: ");
            String name = (String) br.readLine();
            System.out.print("Insira o email do Jogador: ");
            String email = (String) br.readLine();
            System.out.print("Insira o clube do Jogador: ");
            String clube = (String) br.readLine();
            ResgJogador obj = new ResgJogador(name, email, clube);

            ResgJogador response = (ResgJogador) enviarObj(obj, conn);

            if(response!=null && response.completed){
                System.out.println("O Jogador foi registrado com sucesso");
            }else {
                System.out.println("Jogador não foi registrado.");
            }

        }
        //Inscrever Torneio
        else if(op.equals("2")){
            System.out.print("Insira o nome do Jogador: ");
            int torneio = Integer.parseInt(br.readLine());
            System.out.print("Insira o email do Jogador: ");
            int jogador = Integer.parseInt(br.readLine());

            InscreverTorneio obj = new InscreverTorneio(torneio,jogador);
            InscreverTorneio response = (InscreverTorneio) enviarObj(obj,conn);

            if(response!=null && response.completed){
                System.out.println("O Jogador foi registrado com sucesso");
            }else {
                System.out.println("Jogador não foi registrado.");
            }

        }
        //Listar Todos Torneios
        else if(op.equals("3")){
            System.out.println("Torneios Registados: ");
            ListarTorneios obj = new ListarTorneios();
            ListarTorneios response = (ListarTorneios) enviarObj(obj,conn);
            response.mostrarListaTorneios();

        }
        //Listar Torneios por Jogador
        else if(op.equals("4")){

        }
        //Listar Torneios por Estado
        else if(op.equals("5")){

        }
        //Listar Todas as Partidas
        else if(op.equals("6")){

            System.out.println("Partidas Registados: ");
            ListarPartidas obj = new ListarPartidas();
            ListarPartidas response = (ListarPartidas) enviarObj(obj,conn);
            response.mostrarListaPartidas();

        }
        //Listar Partidas por Jogador
        else if(op.equals("7")){

        }
        //Listar Partidas por Torneio
        else if(op.equals("8")){

        }
        //Listar Todos os Jogadores
        else if(op.equals("9")){

            System.out.println("Jogadores Registados: ");
            ListarJogadores obj = new ListarJogadores();
            ListarJogadores response = (ListarJogadores) enviarObj(obj,conn);
            response.mostrarListaJogadores();

        }
        //Listar Jogadores por Torneio
        else if(op.equals("10")){

        }
        //Listar Jogadores por Estado
        else if(op.equals("11")){

        }
        //Sair do programa
        else if(op.equals("12")){
            System.exit(0);
        }
    }

    //Função principal de enviar Objectos
    public static funcGeral enviarObj(funcGeral funcX, Socket conn) {
        try{

            //Enviar Objecto
            ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());

            //Escrever Objeto
            out.writeObject(funcX);
            //Obriga o envio
            //out.flush();

            //Ler resposta de Retorno
            ObjectInputStream in = new ObjectInputStream(conn.getInputStream());

            funcGeral objServer = null;
            objServer = (funcGeral) in.readObject();

            return objServer;

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
