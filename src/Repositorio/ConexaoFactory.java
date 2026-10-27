package Repositorio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoFactory {

    private static final String URL = "jdbc:sqlite:academia.db";

    public static Connection getConexao() throws SQLException {
        Connection coon = DriverManager.getConnection(URL);
        try (Statement st = coon.createStatement()) {
          st.execute("PRAGMA foreing_keys = ON ");
        }
        return coon;
    }

public static void criarTabelad() {
        String sqlUsuario = """
                CREATE TABLE IF NOT EXIST usuario (
                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                 nome TEXT NOT NULL,
                 idade INTEGER NOT NULL
                )""";

        String sqlExercicio = """
                CREATE TABLE IF NOT EXIST exercicio (
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

        try (Connection conn = getConexao(); Statement st = conn.createStatement()) {
            st.execute(sqlUsuario);
            st.execute(sqlExercicio);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas", e);
        }
}
}
