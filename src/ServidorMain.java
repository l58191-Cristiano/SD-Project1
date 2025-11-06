import java.io.FileInputStream;
import java.util.Properties;

public class ServidorMain {

    public static void main(String[] args) {

        Properties props = new Properties();

        // 1. CARREGAR CONFIGS
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (Exception e) {
            System.err.println("Erro: Não foi possível ler o 'config.properties'.");
            e.printStackTrace();
            return; // Termina se não houver config
        }

        IO.println("Configurações carregadas com sucesso.");

        // 2. EXTRAIR DADOS DA BD
        String dbHost = props.getProperty("db.host");
        String dbPort = props.getProperty("db.port");
        String dbName = props.getProperty("db.database");
        String dbUser = props.getProperty("db.user");
        String dbPass = props.getProperty("db.password");

        // 3. INICIALIZAR O CONECTOR DA BD
        PostgresConnector connector = new PostgresConnector(dbHost, dbName, dbUser, dbPass, dbPort);

        try {
            connector.connect(); // Tenta ligar à BD
            IO.println("Ligação à Base de Dados estabelecida.");
        } catch (Exception e) {
            System.err.println("Erro: Falha ao ligar à Base de Dados.");
            e.printStackTrace();
            return; // Termina se a BD falhar
        }

        // 4. INICIALIZAR A LÓGICA DE NEGÓCIO
        ServidorLogica gestor = new ServidorLogica(connector);

        IO.println("ServidorLógica inicializado.");

    }
}
