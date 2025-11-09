import java.sql.Connection;
import java.sql.DriverManager;

// Modificado para nao gerir Statements, apenas Connections
// Motivo: Desta forma varios clientes podem se conectar ao banco de dados

/**
 * @author jsaias
 */
public class PostgresConnector {

    private final String PG_HOST;
    private final String PG_PORT;
    private final String PG_DB;
    private final String USER;
    private final String PWD;
    private Connection con = null;

    public PostgresConnector(String host, String db, String user, String pw, String port) {
        PG_HOST = host;
        PG_PORT = port;
        PG_DB = db;
        USER = user;
        PWD = pw;
    }

    public void connect() throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
            // url = "jdbc:postgresql://host:port/database",
            con = DriverManager.getConnection("jdbc:postgresql://" + PG_HOST + ":" + PG_PORT + "/" + PG_DB, USER, PWD);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Problems setting the connection");
        }
    }

    public void disconnect() {
        // importante: fechar a ligacao a BD
        try {
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return con;
    }
}