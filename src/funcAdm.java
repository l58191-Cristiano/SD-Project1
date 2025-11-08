import java.rmi.Remote;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

//INterface com os métodos que os Clientes Admistradores têm acesso

//Todos métodos que um admistrador terá acesso

//
// Ver os estados admistrativos dos Torneios e Jogadores
//
// Registrar Partidas ----> (Vou partir do ponto q apenas adm fazem isso)
//
// Atualizar o resultado de uma partidas
//
// Atualizar o estado de uma partida
//
// Aprovar um jogador
//
// Atualizar o estado de um jogador
//
// Atualizar o rating de um Jogador
//
// Registrar Torneios  ------> (Está em Adm por enquanto/sujeito a Modificações)
//
// Aprovar um Torneio
//
// Atualizar o estado de um torneio
//
public interface funcAdm extends Remote {

    List<Torneio> listarTorneiosAdmin(String estado_admin) throws java.rmi.RemoteException;
    List<Jogador> listarJogadoresAdmin(String estado_admin) throws java.rmi.RemoteException;
    boolean registarPartida(int id_torneio, int id_jogador_1, int id_jogador_2) throws java.rmi.RemoteException;
    boolean resultadoPartida(int id_partida, int id_jogador) throws java.rmi.RemoteException;
    boolean estadoPartida(int id_partida, String estado_partida) throws java.rmi.RemoteException;
    boolean aprovarJogador(int id_jogador) throws java.rmi.RemoteException;
    boolean estadoGeralJogador(int id_jogador, String estado_geral) throws java.rmi.RemoteException;
    boolean ratingJogador(int id_jogador, int newRating) throws java.rmi.RemoteException;
    boolean registarTorneios(String nome, java.sql.Date data, String local, int premio) throws java.rmi.RemoteException;
    boolean aprovarTorneios(int id_torneio) throws java.rmi.RemoteException;
    boolean estadoGeralTorneio(int id_torneio, String estado_torneio) throws  java.rmi.RemoteException;


}
