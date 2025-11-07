import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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

        Echo objServidor = (Echo) java.rmi.Naming.lookup("rmi://" + cl.regHost + ":" + cl.regPort + "/echo");

        try {
            //Chamar os métodos do Echo
            objServidor.adicionarEcho();

            List<String> list = objServidor.ListaDeEchos();

            //Ver a lista devolvida
            for(String s : list){
                System.out.println(s);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
