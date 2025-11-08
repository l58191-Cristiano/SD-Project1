import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

public class ServidorConnc extends Thread {
    //Porta Default
    private int serverPort;
    //0 é Adm | 1 é Geral
    private int clientType;
    //O connector do posgres
    private final PostgresConnector connector;

    public ServidorConnc(int p, int type, PostgresConnector conn){
        serverPort = p;
        clientType = type;
        connector = conn;
    }

    public void run(){

        //-------------------------
        //Para os clientes Gerais
        if(this.clientType == 1) {
           this.servicoClienteGeral();
        }
        //-------------------------
        //Para os clientes Adm
        else {
            try {
                //Criar o obj remoto
                funcAdm obj = new funcAdmImpl(this.connector);
                //Usar a Registry Local na porta do servidor, neste caso 1099
                java.rmi.registry.Registry registry = LocateRegistry.getRegistry(this.serverPort);
                //Dar bind no Objecto
                registry.rebind("funcAdm", obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void main(String[] args) {

        //Inicilizar Postgres
        PostgresConnector conn =  InicializarPosGres();
        if(conn == null){
            System.exit(0);
            return;
        }

        //Para os clientes Gerais
        ServidorConnc sCliente = new ServidorConnc(9000, 1, conn);
        sCliente.start();

        //-------------------------
        //Para os clientes Adm
        //Antes de usar inicializar o Binder dos Objectos
        //Comando na pasta SD-Project1
        //rmiregistry -J-classpath -J out/production/SD-Project1 porta
        // O makefile já deve fazer isso por ti
        // --> make all
        //--------------------------
        //ServidorConnc sAdm = new ServidorConnc(1099, 0, conn);
        //sAdm.start();


        System.out.println("Servidor criado com sucesso");
    }


    public static PostgresConnector InicializarPosGres(){

        Properties props = new Properties();

        // 1. CARREGAR CONFIGS
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (Exception e) {
            System.err.println("Erro: Não foi possível ler o 'config.properties'.");
            e.printStackTrace();
            return null; // Termina se não houver config
        }

        IO.println("Configurações carregadas com sucesso.");

        // 2. EXTRAIR DADOS DA BD
        String dbHost = props.getProperty("db.host");
        String dbPort = props.getProperty("db.port");
        String dbName = props.getProperty("db.database");
        String dbUser = props.getProperty("db.user");
        String dbPass = props.getProperty("db.password");

        // 3. INICIALIZAR O CONECTOR DA BD
        PostgresConnector connector = new PostgresConnector(dbHost, dbName, dbUser, dbPass, dbPort);

        try {
            connector.connect(); // Tenta ligar à BD
            IO.println("Ligação à Base de Dados estabelecida.");
        } catch (Exception e) {
            System.err.println("Erro: Falha ao ligar à Base de Dados.");
            e.printStackTrace();
            return null; // Termina se a BD falhar
        }

        // 4. INICIALIZAR A LÓGICA DE NEGÓCIO (Não é usado)
        //ServidorLogica gestor = new ServidorLogica(connector);

        //IO.println("ServidorLógica inicializado.");

        return connector;
    }

    public void servicoClienteGeral(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            while(true){
                //Espera por um connecção
                Socket conn = serverSocket.accept();
                try{

                    while(conn.isConnected()) {
                        funcGeral obj = null;
                        //Ler dados (Obj)
                        ObjectInputStream socketIn = new ObjectInputStream(conn.getInputStream());
                        obj = (funcGeral) socketIn.readObject();

                        //Dependendo da instancia do Objecto
                        //São realizadas funções diferentes
                        //Chamar função
                        funcClienteGeral(obj, conn);
                    }
                }catch(Exception e){

                }
                finally {
                    //Fechar o socket de dados para este client adm
                    conn.close();
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void funcClienteGeral (funcGeral obj, Socket conn) throws IOException {
        if(obj == null){
            System.err.println("Erro no Pedido! (Objecto Inválido)");
        }
        //Realiza a ação para o objecto X
        if(obj instanceof ResgJogador){
            //Exemplo lê o novo objecto
            ResgJogador cliente = (ResgJogador) obj;

            cliente.registarJogador(this.connector);

            //E envia uma nova frase de volta dentro do objecto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);

        }
        else if(obj instanceof InscreverTorneio){
            //Exemplo lê o novo objecto
            InscreverTorneio cliente = (InscreverTorneio) obj;

            cliente.inscreverJogadorTorneio(this.connector);

            //E envia uma nova frase de volta dentro do objecto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);

        }
        else if(obj instanceof  ListarJogadores){
            //Exemplo lê o novo objecto
            ListarJogadores cliente = (ListarJogadores) obj;
            //Seleciona o método, dependo do construtor chamado
            listarJogadores(cliente);
            //E envia uma nova frase de volta dentro do objecto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);
        }
        else if(obj instanceof  ListarTorneios){
            //Exemplo lê o novo objecto
            ListarTorneios cliente = (ListarTorneios) obj;
            //Seleciona o método, dependo do construtor chamado
            listarTorneios(cliente);
            //E envia uma nova frase de volta dentro do objecto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);
        }
        else if(obj instanceof  ListarPartidas){
            //Exemplo lê o novo objecto
            ListarPartidas cliente = (ListarPartidas) obj;
            //Seleciona o método, dependo do construtor chamado
            listarPartidas(cliente);
            //E envia uma nova frase de volta dentro do objecto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);
        }
        else {
            System.err.println("Erro no Pedido! (Objecto Desconhecido)");
        }

    }

    public void listarJogadores (ListarJogadores obj){
        if(obj.id_torneio != -1){
            obj.listarJogadoresTorneio(this.connector);
        }
        else if (!obj.estado_geral.equals(null)){
            obj.listarJogadoresGeral(this.connector);
        }
        else{
            obj.listarJogadores(this.connector);
        }
    }
    public void listarTorneios (ListarTorneios obj){
        if(obj.id_jogador != -1){
            obj.listarTorneiosJogador(this.connector);
        }
        else if (!obj.estado_torneio.equals(null)){
            obj.listarTorneiosEstado(this.connector);
        }
        else{
            obj.listarTorneios(this.connector);
        }
    }
    public void listarPartidas(ListarPartidas obj){
        if(obj.id_jogador != -1){
            obj.listarPartidasJogador(this.connector);
        }
        else if (obj.id_torneio != 1){
            obj.listarPartidasTorneio(this.connector);
        }
        else{
            obj.listarPartidas(this.connector);
        }
    }


}
