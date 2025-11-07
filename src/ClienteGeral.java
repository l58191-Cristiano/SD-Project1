import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteGeral {
    private String addr;
    private int port;

    public ClienteGeral(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    public static void main() {

        //Para clientesGerais
        ClienteGeral cl = new ClienteGeral("localhost", 9000);

        //Exemplo de funcionamento (Não é um loop este exemplo)
        cl.enviarFrase();

    }

    //Exemplo
    public void enviarFrase() {

        //O cliente envia um objecto com a frase Teste para o cliente
        ObjFrase objCliente = new ObjFrase("Teste");

        //E irá receber devolta a frase "Recebido"
        enviarObj(objCliente);

    }

    //Função principal de enviar Objectos
    public void enviarObj(Obj objCliente) {
        try{

            Socket conn = new Socket(addr, port);

            //Enviar Objecto
            ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());

            //Escrever Objeto
            out.writeObject(objCliente);
            //Obriga o envio
            //out.flush();

            //Ler resposta de Retorno
            ObjectInputStream in = new ObjectInputStream(conn.getInputStream());

            Object objServer = null;
            objServer = in.readObject();

            //Avaliar o Objeto de Retorno
            if(objServer == null){
                System.err.println("Erro no Pedido! (Objecto Inválido)");
            }
            //Realiza a ação para o objecto X
            if(objServer instanceof ObjFrase){
                //Exemplo lê o novo objecto
                ObjFrase cliente = new ObjFrase(((ObjFrase) objServer).getFrase());

                System.out.println("Mensagem do Servidor " + cliente.getFrase());


            }
            else {
                System.err.println("Erro no Pedido! (Objecto Desconhecido)");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
