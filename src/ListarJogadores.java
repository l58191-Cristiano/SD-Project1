import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListarJogadores extends funcGeral{

    public int id_torneio;
    public String estado_geral;
    //Fica guardado o resultado da query
    List<Jogador> jogadoresR = new ArrayList<>();

    ListarJogadores(){
        id_torneio = -1;
        estado_geral = null;
    }
    ListarJogadores(int torneio){
        this.id_torneio = torneio;
    }
    ListarJogadores(String estado){
        this.estado_geral = estado;
    }


    // Listar todos os jogadores
    public List<Jogador> listarJogadores(PostgresConnector connector) {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT * FROM Jogadores";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                jogadores.add(new Jogador(rs.getInt("id_jogador"), rs.getString("nome"), rs.getInt("rating"), rs.getString("email"), rs.getString("clube"), rs.getString("estado_admin"), rs.getString("estado_geral")));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar jogadores: " + e.getMessage());
        }
        jogadoresR = jogadores;
        return jogadores;
    }

    // Listar jogadores em um torneio
    public List<Jogador> listarJogadoresTorneio(PostgresConnector connector) {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT J.* FROM Jogadores J INNER JOIN Inscricoes I ON J.id_jogador = I.id_jogador WHERE I.id_torneio = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_torneio);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    jogadores.add(new Jogador(rs.getInt("id_jogador"), rs.getString("nome"), rs.getInt("rating"), rs.getString("email"), rs.getString("clube"), rs.getString("estado_admin"), rs.getString("estado_geral")));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar jogadores do torneio: " + e.getMessage());
        }
        jogadoresR = jogadores;
        return jogadores;
    }

    // Listar jogadores por estado_geral
    public List<Jogador> listarJogadoresGeral(PostgresConnector connector) {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT * FROM Jogadores WHERE estado_geral = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_geral);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    jogadores.add(new Jogador(rs.getInt("id_jogador"), rs.getString("nome"), rs.getInt("rating"), rs.getString("email"), rs.getString("clube"), rs.getString("estado_admin"), rs.getString("estado_geral")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar jogadores por estado_geral: " + e.getMessage());
        }
        jogadoresR = jogadores;
        return jogadores;
    }

    //TODO:
    //Mostrar a Lista de Jogadores Obtida
    public void mostrarListaJogadores() {

    }
}
