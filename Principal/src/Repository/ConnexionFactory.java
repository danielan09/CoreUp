package Repository;

import java.sql.Connection;
import java.sql.DriverManager;
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
                 idade INTEGER NOT NULL
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

        String sqlModelo = """
                CREATE TABLE IF NOT EXISTS exercicio_modelo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT NOT NULL,
                grupo_muscular TEXT NOT NULL,
                nome TEXT NOT NULL
                )""";

        try (Connection conexao = getConnexion(); Statement st = conexao.createStatement()) {
            st.execute(sqlUsuario);
            st.execute(sqlExercicio);
            st.execute(sqlModelo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
}
}
