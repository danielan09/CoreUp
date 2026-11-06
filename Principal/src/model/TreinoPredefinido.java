package model;

public class TreinoPredefinido extends Treino {

    public TreinoPredefinido(int idade) {
        super("Treino Base do App");
        montarPorIdade(idade);
    }

    private void montarPorIdade(int idade) {
        if (idade < 30) {
            exercicios.add(new ExercicioForca("Agachamento", "Pernas", 4, 12));
            exercicios.add(new ExercicioForca("Supino reto", "Peito", 4, 10));
            exercicios.add(new ExercicioForca("Remada curvada", "Costas", 3, 12));
            exercicios.add(new ExercicioCardio("Esteira", 15));
        } else if (idade < 50) {
            exercicios.add(new ExercicioForca("Leg press", "Pernas", 3, 12));
            exercicios.add(new ExercicioForca("Supino máquina", "Peito", 3, 12));
            exercicios.add(new ExercicioForca("Puxada frontal", "Costas", 3, 12));
            exercicios.add(new ExercicioCardio("Bicicleta", 20));
        } else {
            exercicios.add(new ExercicioForca("Leg press leve", "Pernas", 2, 15));
            exercicios.add(new ExercicioForca("Remada sentada", "Costas", 2, 15));
            exercicios.add(new ExercicioForca("Elevação lateral", "Ombros", 2, 15));
            exercicios.add(new ExercicioCardio("Caminhada", 25));
        }
    }

    @Override
    public String getOrigem() {
        return "Predefinido";
    }
}
