import java.sql.PreparedStatement;
import java.sql.SQLException;

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
            statement.executeUpdate();

            IO.println("Jogador '" + nome + "' registado com sucesso.");
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao registar jogador: " + e.getMessage());
            return false;
        }
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

    public boolean estadoAdminJogador(int idJogador, String estado_admin) {
        String sql = "UPDATE Jogadores SET estado_admin = ? WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_admin);
            statement.setInt(2, idJogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Estado do Jogador " + idJogador + " alterado para" + estado_admin + ".");
                return true;
            } else {
                IO.println("Jogador com ID " + idJogador + "não foi encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado_admin: " + e.getMessage());
            return false;
        }

    }

    public boolean ratingJogador (int idJogador, int newRating) {
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

    public boolean registarTorneios(String nome, String data, String local, int premio) {
        String sql = "INSERT INTO Torneios (nome, data, local, premio) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setString(2, data);
            statement.setString(3, local);
            statement.setInt(4, premio);

            IO.println("Torneio '" + nome + "' registado com sucesso.");
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao registar torneio: " + e.getMessage());
            return false;
        }
    }
}