import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServidorLogica {

    // NO FUTURO ORGANIZR OS METODOS POR USO DE ADMIN E GERAL

    private final PostgresConnector connector;

    public ServidorLogica(PostgresConnector connector) {
        this.connector = connector;
    }

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

    public boolean inscreverJogadorTorneio(int id_jogador, int id_torneio) {
        String sql = "INSERT INTO Inscricoes (id_jogador, id_torneio) VALUES (?, ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {

            statement.setInt(1, id_jogador);
            statement.setInt(2, id_torneio);

            if (statement.executeUpdate() > 0) {
                IO.println("Jogador " + id_jogador + " inscrito ao torneio " + id_torneio + " com sucesso.");
                return true;
            } else {
                IO.println("Registo do jogador " + id_jogador + " ao torneio " + id_torneio + " falhou.");
                return false;
            }

        } catch (SQLException e) {
            // O codigo 23505 é para unique_violation
            if (e.getSQLState().equals("23505")) {
                System.err.println("Erro: O jogador " + id_jogador + " já está inscrito no torneio " + id_torneio + ".");
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
    public List<Torneio> listarTorneios(int id_jogador) {
        List<Torneio> torneios = new ArrayList<>();
        String sql = "SELECT T.* FROM Torneios T INNER JOIN Inscricoes I ON T.id_torneio = I.id_torneio WHERE I.id_jogador = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {

            statement.setInt(1, id_jogador);
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

    // Listar torneios por estado_torneio
    public List<Torneio> listarTorneiosEstado(String estado_torneio) {
        List<Torneio> torneios = new ArrayList<>();
        String sql = "SELECT * FROM torneios WHERE estado_torneio = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_torneio);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    torneios.add(new Torneio(rs.getInt("id_torneio"), rs.getString("nome"), rs.getDate("data"), rs.getString("local"), rs.getInt("premio"), rs.getString("estado_torneio"), rs.getString("estado_admin")));
                }

            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar torneios por estado_torneio: " + e.getMessage());
        }
        return torneios;
    }

    // Listar torneios por estado_admin
    public List<Torneio> listarTorneiosAdmin(String estado_admin) {
        List<Torneio> torneios = new ArrayList<>();
        String sql = "SELECT * FROM torneios WHERE estado_admin = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_admin);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    torneios.add(new Torneio(rs.getInt("id_torneio"), rs.getString("nome"), rs.getDate("data"), rs.getString("local"), rs.getInt("premio"), rs.getString("estado_torneio"), rs.getString("estado_admin")));
                }

            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar torneios por estado_admin: " + e.getMessage());
        }
        return torneios;
    }

    // Listar todas as partidas
    public List<Partida> listarPartidas() {
        List<Partida> partidas = new ArrayList<>();
        String sql = "SELECT * FROM Partidas";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                partidas.add(new Partida(rs.getInt("id_partida"), rs.getInt("id_torneio"), rs.getInt("id_jogador_1"), rs.getInt("id_jogador_2"), rs.getString("estado_partida"), rs.getObject("ganhador", Integer.class)));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar partidas: " + e.getMessage());
        }
        return partidas;
    }

    // Listar partidas de um torneio
    public List<Partida> listarPartidasTorneio(int id_torneio) {
        List<Partida> partidas = new ArrayList<>();
        String sql = "SELECT * FROM Partidas WHERE id_torneio = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_torneio);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    partidas.add(new Partida(rs.getInt("id_partida"), rs.getInt("id_torneio"), rs.getInt("id_jogador_1"), rs.getInt("id_jogador_2"), rs.getString("estado_partida"), rs.getObject("ganhador", Integer.class)));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar partidas do torneio: " + e.getMessage());
        }
        return partidas;
    }

    // Listar partidas de um Jogador
    public List<Partida> listarPartidasJogador(int id_jogador) {
        List<Partida> partidas = new ArrayList<>();
        String sql = "SELECT * FROM Partidas WHERE id_jogador_1 = ? OR id_jogador_2 = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
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
        return partidas;
    }

    // Listar todos os jogadores
    public List<Jogador> listarJogadores() {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT * FROM Jogadores";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                jogadores.add(new Jogador(rs.getInt("id_jogador"), rs.getString("nome"), rs.getInt("rating"), rs.getString("email"), rs.getString("clube"), rs.getString("estado_admin"), rs.getString("estado_geral")));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar jogadores: " + e.getMessage());
        }
        return jogadores;
    }

    // Listar jogadores em um torneio
    public List<Jogador> listarJogadoresTorneio(int id_torneio) {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT J.* FROM Jogadores J INNER JOIN Inscricoes I ON J.id_jogador = I.id_jogador WHERE I.id_torneio = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_torneio);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    jogadores.add(new Jogador(rs.getInt("id_jogador"), rs.getString("nome"), rs.getInt("rating"), rs.getString("email"), rs.getString("clube"), rs.getString("estado_admin"), rs.getString("estado_geral")));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar jogadores do torneio: " + e.getMessage());
        }
        return jogadores;
    }

    // Listar jogadores por estado_admin
    public List<Jogador> listarJogadoresAdmin(String estado_admin) {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT * FROM Jogadores WHERE estado_admin = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_admin);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    jogadores.add(new Jogador(rs.getInt("id_jogador"), rs.getString("nome"), rs.getInt("rating"), rs.getString("email"), rs.getString("clube"), rs.getString("estado_admin"), rs.getString("estado_geral")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar jogadores por estado_admin: " + e.getMessage());
        }
        return jogadores;
    }

    // Listar jogadores por estado_geral
    public List<Jogador> listarJogadoresGeral(String estado_geral) {
        List<Jogador> jogadores = new ArrayList<>();
        String sql = "SELECT * FROM Jogadores WHERE estado_geral = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_geral);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    jogadores.add(new Jogador(rs.getInt("id_jogador"), rs.getString("nome"), rs.getInt("rating"), rs.getString("email"), rs.getString("clube"), rs.getString("estado_admin"), rs.getString("estado_geral")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar jogadores por estado_geral: " + e.getMessage());
        }
        return jogadores;
    }

    public boolean registarPartida(int id_torneio, int id_jogador_1, int id_jogador_2) {
        String sql = "INSERT INTO Partidas (id_torneio, id_jogador_1, id_jogador_2) VALUES (?, ?, ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {

            statement.setInt(1, id_torneio);
            statement.setInt(2, id_jogador_1);
            statement.setInt(3, id_jogador_2);

            if (statement.executeUpdate() > 0) {
                IO.println("Partida registada com sucesso.");
                return true;
            } else {
                IO.println("Registo da partida falhou.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registar partida: " + e.getMessage());
            return false;
        }
    }

    public boolean resultadoPartida(int id_partida, int id_jogador) {
        String sql = "UPDATE Partidas SET estado_partida = 'Encerrado', ganhador = ? WHERE id_partida = ? AND (id_jogador_1 = ? OR id_jogador_2 = ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_jogador);
            statement.setInt(2, id_partida);
            statement.setInt(3, id_jogador);
            statement.setInt(4, id_jogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Resultado da partida " + id_partida + " registado com sucesso.");
                return true;
            } else {
                IO.println("Falha: Partida " + id_partida + " não encontrada ou Jogador " + id_jogador + " não participou.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao registar resultado da partida: " + e.getMessage());
            return false;
        }

    }

    public boolean estadoPartida(int id_partida, String estado_partida) {

        if (!estado_partida.equals("Agendado") && !estado_partida.equals("Decorrer") && !estado_partida.equals("Encerrado")) {
            System.err.println("Erro: Estado de partida inválida: " + estado_partida);
            return false;
        }

        String sql = "UPDATE Partidas SET estado_partida = ? WHERE id_partida = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_partida);
            statement.setInt(2, id_partida);

            if (statement.executeUpdate() > 0) {
                IO.println("Estado da partida " + id_partida + " alterado para '" + estado_partida + "'.");
                return true;
            } else {
                IO.println("Partida com ID " + id_partida + " não foi encontrada.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado da partida: " + e.getMessage());
            return false;
        }
    }

    public boolean aprovarJogador(int id_jogador) {
        String sql = "UPDATE Jogadores SET estado_admin = 'Aprovado' WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_jogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Jogador com ID " + id_jogador + " aprovado com sucesso.");
                return true;
            } else {
                IO.println("Jogador com ID " + id_jogador + "não foi encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao aprovar jogador: " + e.getMessage());
            return false;
        }
    }

    public boolean estadoGeralJogador(int id_jogador, String estado_geral) {

        // Valida o input, independentemente do que o cliente envia
        if (!estado_geral.equals("Em Jogo") && !estado_geral.equals("Eliminado") && !estado_geral.equals("Inscrito")) {
            System.err.println("Erro: Estado geral inválido: " + estado_geral);
            return false;
        }

        String sql = "UPDATE Jogadores SET estado_geral = ? WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_geral);
            statement.setInt(2, id_jogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Estado do Jogador " + id_jogador + " alterado para '" + estado_geral + "'.");
                return true;
            } else {
                IO.println("Jogador com ID " + id_jogador + " não foi encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado_geral: " + e.getMessage());
            return false;
        }
    }

    public boolean ratingJogador(int id_jogador, int newRating) {
        String sql = "UPDATE Jogadores SET rating = ? WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, newRating);
            statement.setInt(2, id_jogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Rating do jogador " + id_jogador + " atualizado para " + newRating + ".");
                return true;
            } else {
                IO.println("Jogador com ID " + id_jogador + "não foi encontrado.");
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

    public boolean aprovarTorneios(int id_torneio) {
        String sql = "UPDATE Torneios SET estado_admin = 'Aprovado' WHERE id_torneio = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_torneio);

            if (statement.executeUpdate() > 0) {
                IO.println("Torneio com ID " + id_torneio + " aprovado com sucesso.");
                return true;
            } else {
                IO.println("Torneio com ID " + id_torneio + "não foi encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao aprovar torneio: " + e.getMessage());
            return false;
        }
    }

    public boolean estadoGeralTorneio(int id_torneio, String estado_torneio) {

        if (!estado_torneio.equals("Agendado") && !estado_torneio.equals("Ativo") && !estado_torneio.equals("Encerrado")) {
            System.err.println("Erro: Estado do torneio inválido: " + estado_torneio);
            return false;
        }

        String sql = "UPDATE torneios SET estado_torneio = ? WHERE id_torneio = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_torneio);
            statement.setInt(2, id_torneio);

            if (statement.executeUpdate() > 0) {
                IO.println("Estado do torneio " + id_torneio + " alterado para '" + estado_torneio + "'.");
                return true;
            } else {
                IO.println("Torneio com ID " + id_torneio + " não foi encontrado.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado_torneio: " + e.getMessage());
            return false;
        }

    }
}