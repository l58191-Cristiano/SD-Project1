import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.List;

// Interface com os métodos que os Clientes Administradores têm acesso
public interface funcAdm extends Remote {

    // Lista torneios filtrados por estado administrativo (Aprovado/Não Aprovado)
    List<Torneio> listarTorneiosAdmin(String estado_admin) throws RemoteException;

    // Lista jogadores filtrados por estado administrativo (Aprovado/Não Aprovado)
    List<Jogador> listarJogadoresAdmin(String estado_admin) throws RemoteException;

    // Lista todas as partidas registadas no sistema
    List<Partida> listarPartidas() throws RemoteException;

    // Regista uma nova partida entre dois jogadores num torneio específico
    String registarPartida(int id_torneio, int id_jogador_1, int id_jogador_2) throws RemoteException;

    // Define o jogador vencedor de uma partida específica
    String resultadoPartida(int id_partida, int id_jogador) throws RemoteException;

    // Atualiza o estado de uma partida (Agendada/Decorrer/Encerrado)
    String estadoPartida(int id_partida, String estado_partida) throws RemoteException;

    // Aprova o registo de um jogador no sistema
    String aprovarJogador(int id_jogador) throws RemoteException;

    // Atualiza o estado geral de um jogador (Inscrito/Em Jogo/Eliminado)
    String estadoGeralJogador(int id_jogador, String estado_geral) throws RemoteException;

    // Atualiza o rating de um jogador específico
    String ratingJogador(int id_jogador, int newRating) throws RemoteException;

    // Regista um novo torneio no sistema com nome, data, local e prémio
    String registarTorneios(String nome, Date data, String local, int premio) throws RemoteException;

    // Aprova um torneio previamente registado
    String aprovarTorneios(int id_torneio) throws RemoteException;

    // Atualiza o estado geral de um torneio (Agendado/Ativo/Encerrado)
    String estadoGeralTorneio(int id_torneio, String estado_torneio) throws RemoteException;
}
