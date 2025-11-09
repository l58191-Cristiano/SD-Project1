import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InscreverTorneio extends funcGeral {

    public int id_torneio;
    public int id_jogador;
    public boolean completed;

    public InscreverTorneio(int torneio, int jogador) {
        id_torneio = torneio;
        id_jogador = jogador;
    }

    public void inscreverJogadorTorneio(PostgresConnector connector) {
        String sql = "INSERT INTO Inscricoes (id_jogador, id_torneio) VALUES (?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {

            statement.setInt(1, id_jogador);
            statement.setInt(2, id_torneio);

            if (statement.executeUpdate() > 0) {
                IO.println("Jogador " + id_jogador + " inscrito ao torneio " + id_torneio + " com sucesso.");
                completed = true;
            } else {
                IO.println("Registo do jogador " + id_jogador + " ao torneio " + id_torneio + " falhou.");
                completed = false;
            }

        } catch (SQLException e) {
            // O codigo 23505 é para unique_violation
            if (e.getSQLState().equals("23505")) {
                System.err.println("Erro: O jogador " + id_jogador + " já está inscrito no torneio " + id_torneio + ".");
            } else {
                System.err.println("Erro ao inscrever jogador ao torneio: " + e.getMessage());
            }
            completed = false;
        }
    }
}
