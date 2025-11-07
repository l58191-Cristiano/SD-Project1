import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class EchoImpl extends UnicastRemoteObject implements Echo, Serializable {

    List<String> echos;

    //Existe sempre um construtor
    public EchoImpl() throws RemoteException {
        super();
        echos = new ArrayList<>();
    }

    public void adicionarEcho() throws RemoteException {
        this.echos.add("Echo");
        //Esta mensagem aparece só no servidor
        System.out.println("Echo");
    }

    @Override
    public List<String> ListaDeEchos() throws RemoteException {
        return this.echos;
    }
}
