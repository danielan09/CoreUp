package Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnexionFactory {

    private static final  String URL = "jdbc:sqlite:academia5.db";

    public static Connection getConnexion() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Drive do SQLite não encontrado", e);
        }
        Connection connexion = DriverManager.getConnection(URL);
        try (Statement st = connexion.createStatement()) {
          st.execute("PRAGMA foreign_keys = ON ");
        }
        return connexion;
    }

public static void criarTabelas() {
        String sqlUsuario = """
                CREATE TABLE IF NOT EXISTS usuario (
                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                 nome TEXT NOT NULL,
                 idade INTEGER NOT NULL,
                 peso REAL NOT NULL DEFAULT 70.0,
                 altura REAL NOT NULL DEFAULT 170.0,
                 objetivo TEXT NOT NULL DEFAULT 'SAUDE_GERAL'
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
            adicionarColunaSeAusente(st, "peso", "REAL NOT NULL DEFAULT 70.0");
            adicionarColunaSeAusente(st, "altura", "REAL NOT NULL DEFAULT 170.0");
            adicionarColunaSeAusente(st, "objetivo", "TEXT NOT NULL DEFAULT 'SAUDE_GERAL'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
}

    private static void adicionarColunaSeAusente(Statement st, String coluna, String definicao) throws SQLException {
        try (ResultSet colunas = st.executeQuery("PRAGMA table_info(usuario)")) {
            while (colunas.next()) {
                if (coluna.equalsIgnoreCase(colunas.getString("name"))) {
                    return;
                }
            }
        }
        st.execute("ALTER TABLE usuario ADD COLUMN " + coluna + " " + definicao);
    }
}
