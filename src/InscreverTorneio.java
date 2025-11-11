import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InscreverTorneio extends funcGeral {

    public int id_torneio;
    public int id_jogador;
    public String msg;

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
                msg = "Jogador " + id_jogador + " inscrito ao torneio " + id_torneio + " com sucesso.";
                IO.println(msg);
            } else {
                msg = "Registo do jogador " + id_jogador + " ao torneio " + id_torneio + " falhou.";
                IO.println(msg);
            }

        } catch (SQLException e) {
            // O codigo 23505 é para unique_violation
            if (e.getSQLState().equals("23505")) {
                msg ="Erro: O jogador " + id_jogador + " já está inscrito no torneio " + id_torneio + ".";
                System.err.println(msg);
            } else {
                msg ="Erro ao inscrever jogador ao torneio: " + e.getMessage();
                System.err.println(msg);
            }
        }
    }
}
