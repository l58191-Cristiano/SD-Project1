import java.rmi.Remote;
import java.util.List;

//Definimos os Métodos que o cliente admistrador têm acesso
public interface Echo extends Remote {

   void adicionarEcho() throws java.rmi.RemoteException;
   List<String> ListaDeEchos() throws java.rmi.RemoteException;
}
