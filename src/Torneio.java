import java.io.Serializable;
import java.sql.Date;

public record Torneio(int id_torneio, String nome, Date data, String local, int premio, String estado_torneio,
                      String estado_admin) implements Serializable {
}