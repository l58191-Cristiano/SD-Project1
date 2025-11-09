import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

public class ServidorConnc extends Thread {
    // O connector do posgres
    private final PostgresConnector connector;
    // Porta Default
    private final int serverPort;
    // 0 é Adm | 1 é Geral
    private final int clientType;

    public ServidorConnc(int p, int type, PostgresConnector conn) {
        serverPort = p;
        clientType = type;
        connector = conn;
    }

    static void main(String[] args) {

        // Inicilizar Postgres
        PostgresConnector conn = InicializarPosGres();
        if (conn == null) {
            System.exit(0);
            return;
        }

        //Para os clientes Gerais
        ServidorConnc sCliente = new ServidorConnc(9000, 1, conn);
        sCliente.start();

        //-------------------------
        // Para os clientes Adm
        // Antes de usar inicializar o Binder dos Objectos
        // Comando na pasta SD-Project1
        // rmiregistry -J-classpath -J out/production/SD-Project1 porta
        // O makefile já deve fazer isso por ti
        // --> make all
        //--------------------------
        ServidorConnc sAdm = new ServidorConnc(1099, 0, conn);
        sAdm.start();
        System.out.println("Servidor criado com sucesso");
    }

    public static PostgresConnector InicializarPosGres() {

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
        return connector;
    }

    public void run() {

        //-------------------------
        //Para os clientes Gerais
        if (this.clientType == 1) {
            this.servicoClienteGeral();
        }
        //-------------------------
        //Para os clientes Adm
        else {
            try {
                // Criar o obj remoto
                funcAdm obj = new funcAdmImpl(this.connector);

                // Usar a Registry Local na porta do servidor, neste caso 1099
                java.rmi.registry.Registry registry = LocateRegistry.createRegistry(this.serverPort);

                // Dar bind no object stub
                registry.rebind("funcAdm", obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void servicoClienteGeral() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            while (true) {
                // Espera por uma conexão
                Socket conn = serverSocket.accept();
                try {

                    while (conn.isConnected()) {
                        funcGeral obj = null;
                        //Ler dados (Obj)
                        ObjectInputStream socketIn = new ObjectInputStream(conn.getInputStream());
                        obj = (funcGeral) socketIn.readObject();

                        //Dependendo da instância do objeto
                        //São realizadas funções diferentes
                        //Chamar função
                        funcClienteGeral(obj, conn);
                    }
                } catch (Exception _) {

                } finally {
                    // Fechar o socket de dados para este client adm
                    conn.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void funcClienteGeral(funcGeral obj, Socket conn) throws IOException {
        if (obj == null) {
            System.err.println("Erro no Pedido! (Objeto Inválido)");
        }
        // Realiza a ação para o objeto X
        if (obj instanceof ResgJogador cliente) {
            // Exemplo lê o novo objecto

            cliente.registarJogador(this.connector);

            // E envia uma nova frase de volta dentro do objeto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);

        } else if (obj instanceof InscreverTorneio cliente) {
            // Exemplo lê o novo objeto

            cliente.inscreverJogadorTorneio(this.connector);

            // E envia uma nova frase de volta dentro do objeto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);

        } else if (obj instanceof ListarJogadores cliente) {
            // Exemplo lê o novo objeto
            // Seleciona o metodo, dependo do construtor chamado
            listarJogadores(cliente);
            // E envia uma nova frase de volta dentro do objeto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);
        } else if (obj instanceof ListarTorneios cliente) {
            //Exemplo lê o novo objeto
            //Seleciona o metodo, dependo do construtor chamado
            listarTorneios(cliente);
            //E envia uma nova frase de volta dentro do objecto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);
        } else if (obj instanceof ListarPartidas cliente) {
            //Exemplo lê o novo objeto
            //Seleciona o metodo, dependo do construtor chamado
            listarPartidas(cliente);
            //E envia uma nova frase de volta dentro do objeto!
            ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
            socketOut.writeObject(cliente);
        } else {
            System.err.println("Erro no Pedido! (Objeto Desconhecido)");
        }
    }

    public void listarJogadores(ListarJogadores obj) {
        if (obj.id_torneio != -1) {
            obj.listarJogadoresTorneio(this.connector);
        } else if (obj.estado_geral != null) {
            obj.listarJogadoresGeral(this.connector);
        } else {
            obj.listarJogadores(this.connector);
        }
    }

    public void listarTorneios(ListarTorneios obj) {
        if (obj.id_jogador != -1) {
            obj.listarTorneiosJogador(this.connector);
        } else if (obj.estado_torneio != null) {
            obj.listarTorneiosEstado(this.connector);
        } else {
            obj.listarTorneios(this.connector);
        }
    }

    public void listarPartidas(ListarPartidas obj) {
        if (obj.id_jogador != -1) {
            obj.listarPartidasJogador(this.connector);
        } else if (obj.id_torneio != 1) {
            obj.listarPartidasTorneio(this.connector);
        } else {
            obj.listarPartidas(this.connector);
        }
    }
}