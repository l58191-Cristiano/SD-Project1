import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class funcAdmImpl extends UnicastRemoteObject implements funcAdm, Serializable {

    private final PostgresConnector connector;

    funcAdmImpl(PostgresConnector conn) throws RemoteException {
        connector = conn;
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

    public boolean registarPartida(int id_torneio, int id_jogador_1, int id_jogador_2) {
        String sqlCheck = "SELECT estado_admin FROM Torneios WHERE id_torneio = ?";
        try (PreparedStatement checkStmt = this.connector.getConnection().prepareStatement(sqlCheck)) {
            checkStmt.setInt(1, id_torneio);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Torneio foi encontrado
                    String estado_admin = rs.getString("estado_admin");
                    if (!estado_admin.equals("Aprovado")) {
                        // Existe, mas nao foi aprovado
                        IO.println("AVISO: O torneio " + id_torneio + " não está Aprovado.");
                        return false;
                    }
                } else {
                    // Torneio nao existe
                    IO.println("ERRO: O torneio " + id_torneio + " não foi encontrado.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar estado do torneio: " + e.getMessage());
            return false;
        }

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

    public boolean registarTorneios(String nome, Date data, String local, int premio) {
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
