package model;

public class TreinoPredefinido extends Treino {
    public TreinoPredefinido() {
        super("Treino Definido pelo Aplicativo");
        this.adicionarExercicio(new ExercicioForca("Supino", "Peito", 3, 10));
        this.adicionarExercicio(new ExercicioForca("Puxada frontal", "Costas", 3, 10));
        this.adicionarExercicio(new ExercicioForca("Agachamento", "Pernas", 3, 12));
        this.adicionarExercicio(new ExercicioForca("Rosca direta", "Bíceps", 2, 10));
        this.adicionarExercicio(new ExercicioForca("Tríceps", "Tríceps", 2, 10));
        this.adicionarExercicio(new ExercicioCardio("Caminhada",  25));
    }

    public String getOrigem() {
        return "Predefinido pelo aplicativo";
    }
}
