package Repository;

import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioJdbcRepositorio implements Repositorio<Usuario> {

    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, idade, peso, altura, objetivo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnexionFactory.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setInt(2, usuario.getIdade());
            ps.setDouble(3, usuario.getPeso());
            ps.setDouble(4, usuario.getAltura());
            ps.setString(5, usuario.getObjetivo());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";

        try (Connection conn = ConnexionFactory.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getInt("idade"),
                            rs.getDouble("peso"),
                            rs.getDouble("altura"),
                            rs.getString("objetivo")
                    );
                    return Optional.of(usuario);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try (Connection conn = ConnexionFactory.getConnexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("idade"),
                        rs.getDouble("peso"),
                        rs.getDouble("altura"),
                        rs.getString("objetivo")
                );
                lista.add(usuario);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários", e);
        }
        return lista;
    }

    public boolean remover(int usuarioId) {
        try (Connection conn = ConnexionFactory.getConnexion()) {
            conn.setAutoCommit(false);
            try {
                boolean usuarioExiste;
                try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM usuario WHERE id = ?")) {
                    ps.setInt(1, usuarioId);
                    try (ResultSet rs = ps.executeQuery()) {
                        usuarioExiste = rs.next();
                    }
                }

                if (!usuarioExiste) {
                    conn.rollback();
                    return false;
                }

                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM exercicio WHERE usuario_id = ?")) {
                    ps.setInt(1, usuarioId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM usuario WHERE id = ?")) {
                    ps.setInt(1, usuarioId);
                    ps.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover usuário", e);
        }
    }
}
