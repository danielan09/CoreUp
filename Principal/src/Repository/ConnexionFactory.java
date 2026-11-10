package Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnexionFactory {

    private static final String URL = "jdbc:sqlite:academia5.db";

    public static Connection getConnexion() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Drive do SQLite não encontrado", e);
        }
        Connection connexion = DriverManager.getConnection(URL);
        try (Statement st = connexion.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return connexion;
    }

    public static void criarTabelas() throws SQLException {
        String sqlUsuario = """
                CREATE TABLE IF NOT EXISTS usuario (
                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                 nome TEXT NOT NULL,
                 idade INTEGER NOT NULL,
                 peso REAL NOT NULL DEFAULT 0,
                 altura REAL NOT NULL DEFAULT 0,
                 objetivo TEXT
                )""";

        String sqlExercicio = """
                CREATE TABLE IF NOT EXISTS exercicio (
                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                 usuario_id INTEGER NOT NULL,
                 tipo TEXT NOT NULL,
                 nome TEXT NOT NULL,
                 grupo_muscular TEXT,
                 series INTEGER,
                 repeticoes INTEGER,
                 duracao_minutos INTEGER,
                 FOREIGN KEY (usuario_id) REFERENCES usuario(id)
                )""";

        try (Connection conexao = getConnexion(); Statement st = conexao.createStatement()) {
            st.execute(sqlUsuario);
            st.execute(sqlExercicio);

            try (ResultSet rs = conexao.getMetaData().getColumns(null, null, "usuario", null)) {
                boolean hasPeso = false;
                boolean hasAltura = false;
                boolean hasObjetivo = false;
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    if ("peso".equalsIgnoreCase(columnName)) hasPeso = true;
                    if ("altura".equalsIgnoreCase(columnName)) hasAltura = true;
                    if ("objetivo".equalsIgnoreCase(columnName)) hasObjetivo = true;
                }
                if (!hasPeso) st.execute("ALTER TABLE usuario ADD COLUMN peso REAL NOT NULL DEFAULT 0");
                if (!hasAltura) st.execute("ALTER TABLE usuario ADD COLUMN altura REAL NOT NULL DEFAULT 0");
                if (!hasObjetivo) st.execute("ALTER TABLE usuario ADD COLUMN objetivo TEXT");
            }
        }
    }
}
