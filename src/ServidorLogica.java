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
}
