import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
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

    public String registarPartida(int id_torneio, int id_jogador_1, int id_jogador_2) {
        // Primeiro verificar se o jogador e repetido
        if (id_jogador_1 == id_jogador_2) {
            IO.println("AVISO: Um jogador não pode jogar contra si mesmo.");
            return "AVISO: Um jogador não pode jogar contra si mesmo.";
        }

        // Segundo, garante a ordem (id1 < id2) p/ verificacao
        if (id_jogador_1 > id_jogador_2) {
            int aux = id_jogador_1;
            id_jogador_1 = id_jogador_2;
            id_jogador_2 = aux;
        }

        // Terceiro, verifica se o torneio esta aprovado e se existe
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
                        return "AVISO: O torneio " + id_torneio + " não está Aprovado.";
                    }
                } else {
                    // Torneio nao existe
                    IO.println("ERRO: O torneio " + id_torneio + " não foi encontrado.");
                    return "ERRO: O torneio " + id_torneio + " não foi encontrado.";
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar estado do torneio: " + e.getMessage());
            return "Erro ao verificar estado do torneio: " + e.getMessage();
        }

        // Quarto, verifica se os jogadores estao no torneio
        String sqlCheckJogadores = "SELECT COUNT(id_jogador) FROM Inscricoes WHERE id_torneio = ? AND (id_jogador = ? OR id_jogador = ?)";

        try (PreparedStatement checkJdr = this.connector.getConnection().prepareStatement(sqlCheckJogadores)) {
            checkJdr.setInt(1, id_torneio);
            checkJdr.setInt(2, id_jogador_1); // ID mais baixo
            checkJdr.setInt(3, id_jogador_2); // ID mais alto

            try (ResultSet rs = checkJdr.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1); // Resultado do COUNT

                    // Se nao for 2 entao ambos nao estao no torneio
                    if (count != 2) {
                        IO.println("AVISO: Um ou ambos os jogadores (" + id_jogador_1 + ", " + id_jogador_2 + ") não estão inscritos no torneio " + id_torneio + ".");
                        return "AVISO: Um ou ambos os jogadores (" + id_jogador_1 + ", " + id_jogador_2 + ") não estão inscritos no torneio " + id_torneio + ".";
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar inscrição dos jogadores: " + e.getMessage());
            return "AVISO: Um ou ambos os jogadores (" + id_jogador_1 + ", " + id_jogador_2 + ") não estão inscritos no torneio " + id_torneio + ".";
        }

        // Por fim, regista a partida
        String sql = "INSERT INTO Partidas (id_torneio, id_jogador_1, id_jogador_2) VALUES (?, ?, ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {

            statement.setInt(1, id_torneio);
            statement.setInt(2, id_jogador_1);
            statement.setInt(3, id_jogador_2);

            if (statement.executeUpdate() > 0) {
                IO.println("Partida registada com sucesso.");
                return "Partida registada com sucesso.";
            } else {
                IO.println("Registo da partida falhou (erro no INSERT).");
                return "Registo da partida falhou (erro no INSERT).";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registar partida: " + e.getMessage());
            return "Erro ao registar partida: " + e.getMessage();
        }
    }

    public String resultadoPartida(int id_partida, int id_jogador) {
        String sql = "UPDATE Partidas SET estado_partida = 'Encerrado', ganhador = ? WHERE id_partida = ? AND (id_jogador_1 = ? OR id_jogador_2 = ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_jogador);
            statement.setInt(2, id_partida);
            statement.setInt(3, id_jogador);
            statement.setInt(4, id_jogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Resultado da partida " + id_partida + " registado com sucesso.");
                return "Resultado da partida " + id_partida + " registado com sucesso.";
            } else {
                IO.println("Falha: Partida " + id_partida + " não encontrada ou Jogador " + id_jogador + " não participou.");
                return "Falha: Partida " + id_partida + " não encontrada ou Jogador " + id_jogador + " não participou.";
            }

        } catch (SQLException e) {
            System.err.println("Erro ao registar resultado da partida: " + e.getMessage());
            return "Erro ao registar resultado da partida: " + e.getMessage();
        }
    }

    public String estadoPartida(int id_partida, String estado_partida) {

        if (!estado_partida.equals("Agendado") && !estado_partida.equals("Decorrer") && !estado_partida.equals("Encerrado")) {
            System.err.println("Erro: Estado de partida inválida: " + estado_partida);
            return "Erro: Estado de partida inválida: " + estado_partida;
        }

        String sql = "UPDATE Partidas SET estado_partida = ? WHERE id_partida = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_partida);
            statement.setInt(2, id_partida);

            if (statement.executeUpdate() > 0) {
                IO.println("Estado da partida " + id_partida + " alterado para '" + estado_partida + "'.");
                return "Estado da partida " + id_partida + " alterado para '" + estado_partida + "'.";
            } else {
                IO.println("Partida com ID " + id_partida + " não foi encontrada.");
                return "Partida com ID " + id_partida + " não foi encontrada.";
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado da partida: " + e.getMessage());
            return "Erro ao atualizar estado da partida: " + e.getMessage();
        }
    }

    public String aprovarJogador(int id_jogador) {
        String sql = "UPDATE Jogadores SET estado_admin = 'Aprovado' WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_jogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Jogador com ID " + id_jogador + " aprovado com sucesso.");
                return "Jogador com ID " + id_jogador + " aprovado com sucesso.";
            } else {
                IO.println("Jogador com ID " + id_jogador + "não foi encontrado.");
                return "Jogador com ID " + id_jogador + "não foi encontrado.";
            }

        } catch (SQLException e) {
            System.err.println("Erro ao aprovar jogador: " + e.getMessage());
            return "Erro ao aprovar jogador: " + e.getMessage();
        }
    }

    public String estadoGeralJogador(int id_jogador, String estado_geral) {

        // Valida o input, independentemente do que o cliente envia
        if (!estado_geral.equals("Em Jogo") && !estado_geral.equals("Eliminado") && !estado_geral.equals("Inscrito")) {
            System.err.println("Erro: Estado geral inválido: " + estado_geral);
            return "Erro: Estado geral inválido: " + estado_geral;
        }

        String sql = "UPDATE Jogadores SET estado_geral = ? WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_geral);
            statement.setInt(2, id_jogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Estado do Jogador " + id_jogador + " alterado para '" + estado_geral + "'.");
                return "Estado do Jogador " + id_jogador + " alterado para '" + estado_geral + "'.";
            } else {
                IO.println("Jogador com ID " + id_jogador + " não foi encontrado.");
                return "Jogador com ID " + id_jogador + " não foi encontrado.";
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado_geral: " + e.getMessage());
            return "Erro ao atualizar estado_geral: " + e.getMessage();
        }
    }

    public String ratingJogador(int id_jogador, int newRating) {
        String sql = "UPDATE Jogadores SET rating = ? WHERE id_jogador = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, newRating);
            statement.setInt(2, id_jogador);

            if (statement.executeUpdate() > 0) {
                IO.println("Rating do jogador " + id_jogador + " atualizado para " + newRating + ".");
                return "Rating do jogador " + id_jogador + " atualizado para " + newRating + ".";
            } else {
                IO.println("Jogador com ID " + id_jogador + "não foi encontrado.");
                return "Jogador com ID " + id_jogador + "não foi encontrado.";
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar rating: " + e.getMessage());
            return "Erro ao atualizar rating: " + e.getMessage();
        }
    }

    public String registarTorneios(String nome, Date data, String local, int premio) {
        String sql = "INSERT INTO Torneios (nome, data, local, premio) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setDate(2, data);
            statement.setString(3, local);
            statement.setInt(4, premio);

            if (statement.executeUpdate() > 0) {
                IO.println("Torneio " + nome + " registado com sucesso.");
                return "Torneio " + nome + " registado com sucesso.";
            } else {
                IO.println("Registo do torneio " + nome + " falhou.");
                return "Registo do torneio " + nome + " falhou.";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registar torneio: " + e.getMessage());
            return "Erro ao registar torneio: " + e.getMessage();
        }
    }

    public String aprovarTorneios(int id_torneio) {
        String sql = "UPDATE Torneios SET estado_admin = 'Aprovado' WHERE id_torneio = ?";

        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_torneio);

            if (statement.executeUpdate() > 0) {
                IO.println("Torneio com ID " + id_torneio + " aprovado com sucesso.");
                return "Torneio com ID " + id_torneio + " aprovado com sucesso.";
            } else {
                IO.println("Torneio com ID " + id_torneio + "não foi encontrado.");
                return "Torneio com ID " + id_torneio + "não foi encontrado.";
            }

        } catch (SQLException e) {
            System.err.println("Erro ao aprovar torneio: " + e.getMessage());
            return "Erro ao aprovar torneio: " + e.getMessage();
        }
    }

    public String estadoGeralTorneio(int id_torneio, String estado_torneio) {

        if (!estado_torneio.equals("Agendado") && !estado_torneio.equals("Ativo") && !estado_torneio.equals("Encerrado")) {
            System.err.println("Erro: Estado do torneio inválido: " + estado_torneio);
            return "Erro: Estado do torneio inválido: " + estado_torneio;
        }

        String sql = "UPDATE torneios SET estado_torneio = ? WHERE id_torneio = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setString(1, estado_torneio);
            statement.setInt(2, id_torneio);

            if (statement.executeUpdate() > 0) {
                IO.println("Estado do torneio " + id_torneio + " alterado para '" + estado_torneio + "'.");
                return "Estado do torneio " + id_torneio + " alterado para '" + estado_torneio + "'.";
            } else {
                IO.println("Torneio com ID " + id_torneio + " não foi encontrado.");
                return "Torneio com ID " + id_torneio + " não foi encontrado.";
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar estado_torneio: " + e.getMessage());
            return "Erro ao atualizar estado_torneio: " + e.getMessage();
        }
    }

    // Quero fazer isto modular, mas nao sei como D:, talvez uma private class auxiliar
    public void remJogador(int id_jogador) {
        try (Connection connection = this.connector.getConnection()) {

            try {
                connection.setAutoCommit(false);

                // Copia do remJogadorPartidas e remJogadorTorneios
                String sqlPartidas = "DELETE FROM Partidas WHERE id_jogador_1 = ? OR id_jogador_2 = ?";
                try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sqlPartidas)) {
                    statement.setInt(1, id_jogador);
                    statement.setInt(2, id_jogador);
                    statement.executeUpdate();
                }

                String sqlTorneios = "DELETE FROM Inscricoes WHERE id_jogador = ?";
                try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sqlTorneios)) {
                    statement.setInt(1, id_jogador);
                    statement.executeUpdate();
                }

                String sqlJogador = "DELETE FROM Jogadores WHERE id_jogador = ?";
                try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sqlJogador)) {
                    statement.setInt(1, id_jogador);
                    statement.executeUpdate();
                }

                connection.commit();
                IO.println("Jogador " + id_jogador + " removido de toda a base de dados.");
            } catch (SQLException e) {
                System.err.println("Erro ao remover jogador da base de dados: " + e.getMessage());
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao reverter transação: " + ex.getMessage());
                }
            } finally {
                // Volta a colocar a conexão normal
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO: " + e.getMessage());
        }
    }

    public void remJogadorTorneios(int id_jogador) {
        try (Connection connection = this.connector.getConnection()) {

            try {
                connection.setAutoCommit(false);

                // Mesma logica que remJogadorPartidas
                String sqlPartidas = "DELETE FROM Partidas WHERE id_jogador_1 = ? OR id_jogador_2 = ?";
                try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sqlPartidas)) {
                    statement.setInt(1, id_jogador);
                    statement.setInt(2, id_jogador);
                    statement.executeUpdate();
                }

                String sqlTorneios = "DELETE FROM Inscricoes WHERE id_jogador = ?";
                try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sqlTorneios)) {
                    statement.setInt(1, id_jogador);
                    statement.executeUpdate();
                }

                connection.commit();
                IO.println("Jogador " + id_jogador + " removido de todos os torneios (Partidas e Inscrições).");
            } catch (SQLException e) {
                System.err.println("Erro ao remover jogador dos torneios: " + e.getMessage());
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao reverter transação: " + ex.getMessage());
                }
            } finally {
                // Volta a colocar a conexão normal
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO: " + e.getMessage());
        }
    }

    public void remJogadorPartidas(int id_jogador) {
        String sql = "DELETE FROM Partidas WHERE id_jogador_1 = ? OR id_jogador_2 = ?";
        try (PreparedStatement statement = this.connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, id_jogador);
            statement.setInt(2, id_jogador);

            int remLinhas = statement.executeUpdate();
            if (remLinhas > 0) {
                IO.println("Jogador " + id_jogador + " removido de " + remLinhas + " partidas.");
            } else {
                IO.println("ERRO: Jogador " + id_jogador + " não foi encontrado em nenhuma partida");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover jogador da partida: " + e.getMessage());
        }

    }
}
