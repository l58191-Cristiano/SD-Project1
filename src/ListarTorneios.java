import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListarTorneios extends funcGeral {

    public String estado_torneio;
    public int id_jogador;
    //Fica guardado o resultado da query
    List<Torneio> torneiosR = new ArrayList<>();

    public ListarTorneios(){
        estado_torneio = null;
        id_jogador = -1;
    }
    public ListarTorneios(int jogador){
        this.id_jogador = jogador;
    }
    public ListarTorneios(String estado){
        this.estado_torneio = estado;
    }

    // Listar todos os torneios
    public List<Torneio> listarTorneios(PostgresConnector connector) {
        List<Torneio> torneios = new ArrayList<>();
        String sql = "SELECT * FROM Torneios";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                torneios.add(new Torneio(rs.getInt("id_torneio"), rs.getString("nome"), rs.getDate("data"), rs.getString("local"), rs.getInt("premio"), rs.getString("estado_torneio"), rs.getString("estado_admin")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar torneios: " + e.getMessage());
        }
        torneiosR = torneios;
        return torneios;
    }

    // Listar torneios em que X jogador esteja
    public List<Torneio> listarTorneiosJogador(PostgresConnector connector) {
        List<Torneio> torneios = new ArrayList<>();
        String sql = "SELECT T.* FROM Torneios T INNER JOIN Inscricoes I ON T.id_torneio = I.id_torneio WHERE I.id_jogador = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {

            statement.setInt(1, id_jogador);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    torneios.add(new Torneio(rs.getInt("id_torneio"), rs.getString("nome"), rs.getDate("data"), rs.getString("local"), rs.getInt("premio"), rs.getString("estado_torneio"), rs.getString("estado_admin")));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar torneios do jogador: " + e.getMessage());
        }
        torneiosR = torneios;
        return torneios;
    }

    // Listar torneios por estado_torneio
    public List<Torneio> listarTorneiosEstado(PostgresConnector connector) {
        List<Torneio> torneios = new ArrayList<>();
        String sql = "SELECT * FROM torneios WHERE estado_torneio = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_torneio);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    torneios.add(new Torneio(rs.getInt("id_torneio"), rs.getString("nome"), rs.getDate("data"), rs.getString("local"), rs.getInt("premio"), rs.getString("estado_torneio"), rs.getString("estado_admin")));
                }

            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar torneios por estado_torneio: " + e.getMessage());
        }
        torneiosR = torneios;
        return torneios;
    }

    //TODO:
    //Fazer método de Mostrar Lista
    public void mostrarListaTorneios(){

    }
}
