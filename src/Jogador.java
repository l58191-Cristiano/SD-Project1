import java.io.Serializable;

public record Jogador(int id_jogador, String nome, int rating, String email, String clube, String estado_admin,
                      String estado_geral) implements Serializable {
}
