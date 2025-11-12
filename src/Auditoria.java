import java.io.Serializable;

public record Auditoria(int id, String tabela, int id_entidade, String operacao, String old_values, String new_values, String timestamp) implements Serializable {
}
