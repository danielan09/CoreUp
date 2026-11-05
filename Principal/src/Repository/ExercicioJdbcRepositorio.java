package Repository;

import model.Exercicio;
import model.ExercicioCardio;
import model.ExercicioForca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExercicioJdbcRepositorio {

    public void salvar(Exercicio exercicio, int usuarioId) {
        String sql = """
            INSERT INTO exercicio
            (usuario_id, tipo, nome, grupo_muscular, series, repeticoes, duracao_minutos)
            VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (Connection conn = ConnexionFactory.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, usuarioId);
            ps.setString(3, exercicio.getNome());
            ps.setString(4, exercicio.getGrupoMuscular());

            // Herança: descobrimos qual subclasse é para preencher as colunas certas
            if (exercicio instanceof ExercicioForca forca) {
                ps.setString(2, "FORCA");
                ps.setInt(5, forca.getSeries());
                ps.setInt(6, forca.getRepeticoes());
                ps.setNull(7, Types.INTEGER);
            } else if (exercicio instanceof ExercicioCardio cardio) {
                ps.setString(2, "CARDIO");
                ps.setNull(5, Types.INTEGER);
                ps.setNull(6, Types.INTEGER);
                ps.setInt(7, cardio.getDuracaoMinutos());
            }

            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    exercicio.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar exercício", e);
        }
    }

    public List<Exercicio> listarPorUsuario(int usuarioId) {
        List<Exercicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM exercicio WHERE usuario_id = ?";

        try (Connection conn = ConnexionFactory.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Exercicio ex;
                    // Lemos o "tipo" e criamos o objeto da classe certa
                    if (rs.getString("tipo").equals("FORCA")) {
                        ex = new ExercicioForca(rs.getString("nome"), rs.getString("grupo_muscular"),
                                rs.getInt("series"), rs.getInt("repeticoes"));
                    } else {
                        ex = new ExercicioCardio(rs.getString("nome"), rs.getInt("duracao_minutos"));
                    }
                    ex.setId(rs.getInt("id"));
                    lista.add(ex);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar exercícios", e);
        }
        return lista;
    }

    public void remover(int exercicioId) {
        String sql = "DELETE FROM exercicio WHERE id = ?";

        try (Connection conn = ConnexionFactory.getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, exercicioId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover exercício", e);
        }
    }
}
