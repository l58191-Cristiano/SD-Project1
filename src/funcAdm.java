import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//INterface com os métodos que os Clientes Admistradores têm acesso

//Todos métodos que um admistrador terá acesso

//
// Ver os estados admistrativos dos Torneios e Jogadores
//
// Ver todas as Partidas
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
public interface funcAdm extends Remote{

    List<Torneio> listarTorneiosAdmin(String estado_admin) throws RemoteException;
    List<Jogador> listarJogadoresAdmin(String estado_admin) throws RemoteException;
    List<Partida> listarPartidas() throws RemoteException;
    boolean registarPartida(int id_torneio, int id_jogador_1, int id_jogador_2) throws RemoteException;
    boolean resultadoPartida(int id_partida, int id_jogador) throws RemoteException;
    boolean estadoPartida(int id_partida, String estado_partida) throws RemoteException;
    boolean aprovarJogador(int id_jogador) throws RemoteException;
    boolean estadoGeralJogador(int id_jogador, String estado_geral) throws RemoteException;
    boolean ratingJogador(int id_jogador, int newRating) throws RemoteException;
    boolean registarTorneios(String nome, Date data, String local, int premio) throws RemoteException;
    boolean aprovarTorneios(int id_torneio) throws RemoteException;
    boolean estadoGeralTorneio(int id_torneio, String estado_torneio) throws  RemoteException;
}
