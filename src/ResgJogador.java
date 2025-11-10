import java.sql.PreparedStatement;
import java.sql.SQLException;

// Registo de funcGerais
public class ResgJogador extends funcGeral {

    public String nome;
    public String email;
    public String clube;
    // Guarda o valor de resposta para o cliente
    public boolean completed;

    ResgJogador(String nome, String email, String clube) {
        this.nome = nome;
        this.email = email;
        this.clube = clube;
    }

    public void registarJogador(PostgresConnector connector) {
        String sql = "INSERT INTO Jogadores (nome, email, clube) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {
            // ID eh criado automaticamente pelo BD
            statement.setString(1, nome);  // O primeiro '?' é o nome
            statement.setString(2, email); // O segundo '?' é o email
            statement.setString(3, clube); // O terceiro '?' é o clube

            if (statement.executeUpdate() > 0) {
                IO.println("Jogador " + nome + " registado com sucesso.");
                completed = true;
            } else {
                IO.println("Registo do jogador " + nome + " falhou.");
                completed = false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registar jogador: " + e.getMessage());
            completed = false;
        }
    }

    public ResgJogador getJogador() {
        return this;
    }
}