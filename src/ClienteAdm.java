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

        funcAdm objServidor = (funcAdm) java.rmi.Naming.lookup("rmi://" + cl.regHost + ":" + cl.regPort + "/funcAdm");

        try {
            //Chamar MÉTODOS AQUI
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
