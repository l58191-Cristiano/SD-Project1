import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListarPartidas extends funcGeral{

    public int id_jogador;
    public int id_torneio;
    //Fica guardado o resultado da query
    List<Partida> partidasR = new ArrayList<>();

    ListarPartidas(){
        id_torneio = -1;
        id_jogador = -1;
    }
    ListarPartidas(int jogador,  int id_torneio){
        this.id_jogador = jogador;
    }
    ListarPartidas(int torneio){
        this.id_torneio = torneio;
    }

    // Listar todas as partidas
    public List<Partida> listarPartidas(PostgresConnector connector) {
        List<Partida> partidas = new ArrayList<>();
        String sql = "SELECT * FROM Partidas";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                partidas.add(new Partida(rs.getInt("id_partida"), rs.getInt("id_torneio"), rs.getInt("id_jogador_1"), rs.getInt("id_jogador_2"), rs.getString("estado_partida"), rs.getObject("ganhador", Integer.class)));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar partidas: " + e.getMessage());
        }
        partidasR = partidas;
        return partidas;
    }

    // Listar partidas de um torneio
    public List<Partida> listarPartidasTorneio(PostgresConnector connector) {
        List<Partida> partidas = new ArrayList<>();
        String sql = "SELECT * FROM Partidas WHERE id_torneio = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_torneio);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    partidas.add(new Partida(rs.getInt("id_partida"), rs.getInt("id_torneio"), rs.getInt("id_jogador_1"), rs.getInt("id_jogador_2"), rs.getString("estado_partida"), rs.getObject("ganhador", Integer.class)));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar partidas do torneio: " + e.getMessage());
        }
        partidasR = partidas;
        return partidas;
    }

    // Listar partidas de um Jogador
    public List<Partida> listarPartidasJogador(PostgresConnector connector) {
        List<Partida> partidas = new ArrayList<>();
        String sql = "SELECT * FROM Partidas WHERE id_jogador_1 = ? OR id_jogador_2 = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_jogador);
            statement.setInt(2, id_jogador);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    partidas.add(new Partida(rs.getInt("id_partida"), rs.getInt("id_torneio"), rs.getInt("id_jogador_1"), rs.getInt("id_jogador_2"), rs.getString("estado_partida"), rs.getObject("ganhador", Integer.class)));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar partidas do jogador: " + e.getMessage());
        }
        partidasR = partidas;
        return partidas;
    }

    //TODO:
    //Fazer método de Mostrar Lista
    public void mostrarListaPartidas(){

    }
}
