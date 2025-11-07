import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServidorLogica {

    private final PostgresConnector connector;

    public ServidorLogica(PostgresConnector connector) {
        this.connector = connector;
    }

    // --- METODOS DO CLIENTE GERAL ---

    public boolean registarJogador(String nome, String email, String clube) {
        String sql = "INSERT INTO Jogadores (nome, email, clube) VALUES (?, ?, ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            // ID eh criado automaticamente pelo BD
            statement.setString(1, nome);  // O primeiro '?' é o nome
            statement.setString(2, email); // O segundo '?' é o email
            statement.setString(3, clube); // O terceiro '?' é o clube

            if (statement.executeUpdate() > 0) {
                IO.println("Jogador " + nome + " registado com sucesso.");
                return true;
            } else {
                IO.println("Registo do jogador " + nome + " falhou.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registar jogador: " + e.getMessage());
            return false;
        }
    }

    public boolean inscreverJogadorTorneio(int idJogador, int idTorneio) {
        String sql = "INSERT INTO Inscricoes (id_jogador, id_torneio) VALUES (?, ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {

            statement.setInt(1, idJogador);
            statement.setInt(2, idTorneio);

            if (statement.executeUpdate() > 0) {
                IO.println("Jogador " + idJogador + " inscrito ao torneio " + idTorneio + " com sucesso.");
                return true;
            } else {
                IO.println("Registo do jogador " + idJogador + " ao torneio " + idTorneio + " falhou.");
                return false;
            }

        } catch (SQLException e) {
            // O codigo 23505 é para unique_violation
            if (e.getSQLState().equals("23505")) {
                System.err.println("Erro: O jogador " + idJogador + " já está inscrito no torneio " + idTorneio + ".");
            } else {
                System.err.println("Erro ao inscrever jogador ao torneio: " + e.getMessage());
            }
            return false;
        }

    }

    // Listar todos os torneios
    public List<Torneio> listarTorneios() {
        List<Torneio> torneios = new ArrayList<>();
        String sql = "SELECT * FROM Torneios";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                torneios.add(new Torneio(rs.getInt("id_torneio"), rs.getString("nome"), rs.getDate("data"), rs.getString("local"), rs.getInt("premio"), rs.getString("estado_torneio"), rs.getString("estado_admin")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar torneios: " + e.getMessage());
        }
        return torneios;
    }

    // Listar torneios em que X jogador esteja
    public List<Torneio> listarTorneios(int idJogador) {
        List<Torneio> torneios = new ArrayList<>();
        String sql = "SELECT T.* FROM Torneios T INNER JOIN Inscricoes I ON T.id_torneio = I.id_torneio WHERE I.id_jogador = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {

            statement.setInt(1, idJogador);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    torneios.add(new Torneio(rs.getInt("id_torneio"), rs.getString("nome"), rs.getDate("data"), rs.getString("local"), rs.getInt("premio"), rs.getString("estado_torneio"), rs.getString("estado_admin")));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar torneios do jogador: " + e.getMessage());
        }
        return torneios;
    }

    // --- METODOS DO CLIENTE ADMIN ---

    public boolean aprovarJogador(int idJogador) {
        String sql = "UPDATE Jogadores SET estado_admin = 'Aprovado' WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, idJogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Jogador com ID " + idJogador + " aprovado com sucesso.");
                return true;
            } else {
                IO.println("Jogador com ID " + idJogador + "não foi encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao aprovar jogador: " + e.getMessage());
            return false;
        }
    }

    public boolean estadoGeralJogador(int idJogador, String estado_geral) {
        String sql = "UPDATE Jogadores SET estado_geral = ? WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_geral);
            statement.setInt(2, idJogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Estado do Jogador " + idJogador + " alterado para" + estado_geral + ".");
                return true;
            } else {
                IO.println("Jogador com ID " + idJogador + "não foi encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado_geral: " + e.getMessage());
            return false;
        }

    }

    public boolean ratingJogador(int idJogador, int newRating) {
        String sql = "UPDATE Jogadores SET rating = ? WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, newRating);
            statement.setInt(2, idJogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Rating do jogador " + idJogador + " atualizado para " + newRating + ".");
                return true;
            } else {
                IO.println("Jogador com ID " + idJogador + "não foi encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar rating: " + e.getMessage());
            return false;
        }
    }

    public boolean registarTorneios(String nome, java.sql.Date data, String local, int premio) {
        String sql = "INSERT INTO Torneios (nome, data, local, premio) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setDate(2, data);
            statement.setString(3, local);
            statement.setInt(4, premio);

            if (statement.executeUpdate() > 0) {
                IO.println("Torneio " + nome + " registado com sucesso.");
                return true;
            } else {
                IO.println("Registo do torneio " + nome + " falhou.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registar torneio: " + e.getMessage());
            return false;
        }
    }
}