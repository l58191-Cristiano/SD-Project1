import java.io.Serializable;

// Usamos Integer porque ganhador pode ser null
public record Partida(int id_partida, int id_torneio, int id_jogador1, int id_jogador2, String estado_partida,
                      Integer ganhador) implements Serializable {
}
