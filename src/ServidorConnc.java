import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;

public class ServidorConnc extends Thread {
    //Porta Default
    private int serverPort;
    //0 é Adm | 1 é Geral
    private int clientType;

    public ServidorConnc(int p, int type){
        serverPort = p;
        clientType = type;
    }

    public void run(){

        //-------------------------
        //Para os clientes Gerais
        if(this.clientType == 1) {
           this.servicoClienteGeral();
        }
        //-------------------------
        else {
            try {
                //Criar o obj remoto
                Echo obj = new EchoImpl();
                //Usar a Registry Local na porta do servidor, neste caso 1099
                java.rmi.registry.Registry registry = LocateRegistry.getRegistry(this.serverPort);
                //Dar bind no Objecto
                registry.rebind("echo", obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void main(String[] args) {

        //Para os clientes Gerais
        ServidorConnc sCliente = new ServidorConnc(9000, 1);
        sCliente.start();

        //-------------------------
        //Para os clientes Adm
        //Antes de usar inicializar o Binder dos Objectos
        //Comando na pasta SD-Project1
        //rmiregistry -J-classpath -J out/production/SD-Project1 porta
        // O makefile já deve fazer isso por ti
        // --> make all
        //--------------------------
        ServidorConnc sAdm = new ServidorConnc(1099, 0);
        sAdm.start();


        System.out.println("Servidor criado com sucesso");
    }



    public void servicoClienteGeral(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            while(true){
                //Espera por um connecção
                Socket conn = serverSocket.accept();
                try{
                    Object obj = null;
                    //Ler dados (Obj)
                    ObjectInputStream socketIn = new ObjectInputStream(conn.getInputStream());
                    obj = socketIn.readObject();

                    //Dependendo da instancia do Objecto
                    //São realizadas funções diferentes
                    if(obj == null){
                        System.err.println("Erro no Pedido! (Objecto Inválido)");
                    }
                    //Realiza a ação para o objecto X
                    if(obj instanceof ObjFrase){
                        //Exemplo lê o novo objecto
                        ObjFrase cliente = new ObjFrase(((ObjFrase) obj).getFrase());

                        System.out.println("Mensagem do Cliente " + cliente.getFrase());

                        //E envia uma nova frase de volta dentro do objecto!
                        ObjectOutputStream socketOut = new ObjectOutputStream(conn.getOutputStream());
                        cliente = new ObjFrase("Recebido");
                        socketOut.writeObject(cliente);

                    }
                    else {
                        System.err.println("Erro no Pedido! (Objecto Desconhecido)");
                    }



                }catch(Exception e){
                    e.printStackTrace();
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

}
