package Repository;

import model.ExercicioModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CataloJdbcRepositorio {
    public void popularSeVazio() {
        if (!estaVazio()) return;

        String[][] dados = {
                {"FORCA", "Peito", "Supino reto"},
                {"FORCA", "Peito", "Supino inclinado"},
                {"FORCA", "Peito", "Cruxifixo"},
                {"FORCA", "Peito", "Flexão de braço"},
                {"FORCA", "Costas", "Puxada frontal"},
                {"FORCA", "Costas", "Remada curvada"},
                {"FORCA", "Costas", "Remada sentada"},
                {"FORCA", "Costas", "Barra fixa"},
                {"FORCA", "Pernas", "Agachamento"},
                {"FORCA", "Pernas", "Leg press"},
                {"FORCA", "Pernas", "Cadeira flexora"},
                {"FORCA", "Pernas", "Mesa flexora"},
                {"FORCA", "Ombros", "Desenvolvimento"},
                {"FORCA", "Ombros", "Elevação lateral"},
                {"FORCA", "Biceps", "Rosca direta"},
                {"FORCA", "Biceps", "Rosca martelo"},
                {"FORCA", "Tríceps", "Tríceps francês"},
                {"FORCA", "Tríceps", "Tríceps na Polia"},
                {"FORCA", "Abdômen", "Abdominal"},
                {"FORCA", "Abdômen", "Prancha"},
                {"CARDIO", "Cardio", "Esteira"},
                {"CARDIO", "Cardio", "Bicicleta"},
                {"CARDIO", "Cardio", "Elíptico"},
                {"CARDIO", "Cardio", "Corrida"},
                {"CARDIO", "Cardio", "Pular corda"}
        };

        String sql = "INSERT INTO exercicio_modelo (tipo, grupo_muscular, nome VALUE (?,?,?)";

        try (Connection conexao = ConnexionFactory.getConnexion();
             PreparedStatement ps =conexao.prepareStatement(sql)) {
            for (String[] linha : dados) {
                ps.setString(1, linha [0]);
                ps.setString(2, linha [1]);
                ps.setString(3, linha [2]);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao catálogo popular", e);
        }
    }

    private boolean estaVazio() {
        String sql = "SELECT COUNT(*) FROM exercicio_modelo";
        try (Connection conexao = ConnexionFactory.getConnexion();
             Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar catálogo", e);
        }
    }

    public List<ExercicioModelo> listarGrupos(String grupo) {
        List<ExercicioModelo> lista = new ArrayList<>();
        String sql = "SELECT * FROM exercicio_modelo WHERE grupo_muscular = ? ODER BY id";
        try (Connection conexao = ConnexionFactory.getConnexion();
             PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, grupo);
            try (ResultSet rs = ps .executeQuery()) {
                while (rs.next()) {
                    lista.add(new ExercicioModelo(
                            rs.getInt("id"),
                            rs.getString("tipo"),
                            rs.getString("grupo_muscular"),
                            rs.getString("nome")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar exercícios do catálogo", e);
        }
        return lista;
    }

}
