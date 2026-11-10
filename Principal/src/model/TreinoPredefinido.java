package model;

import java.util.ArrayList;
import java.util.List;

public class TreinoPredefinido extends Treino {

    public TreinoPredefinido(int idade) {
        super("Treino Base do App");
        montarPorIdade(idade);
    }

    private TreinoPredefinido(String nome) {
        super(nome);
    }

    public static List<TreinoPredefinido> listarPlanos() {
        List<TreinoPredefinido> planos = new ArrayList<>();

        TreinoPredefinido costas = new TreinoPredefinido("Costas");
        costas.adicionarForca("Puxada frontal", "Costas", 3, 10);
        costas.adicionarForca("Remada baixa", "Costas", 3, 12);
        costas.adicionarForca("Remada unilateral", "Costas", 3, 10);
        costas.adicionarForca("Pullover na polia", "Costas", 3, 12);
        planos.add(costas);

        TreinoPredefinido pernas = new TreinoPredefinido("Pernas");
        pernas.adicionarForca("Agachamento", "Pernas", 3, 10);
        pernas.adicionarForca("Leg press", "Pernas", 3, 12);
        pernas.adicionarForca("Cadeira extensora", "Quadríceps", 3, 12);
        pernas.adicionarForca("Mesa flexora", "Posterior de coxa", 3, 12);
        pernas.adicionarForca("Elevação de panturrilha", "Panturrilhas", 3, 15);
        planos.add(pernas);

        TreinoPredefinido peito = new TreinoPredefinido("Peito");
        peito.adicionarForca("Supino reto", "Peito", 3, 10);
        peito.adicionarForca("Supino inclinado com halteres", "Peito", 3, 10);
        peito.adicionarForca("Crucifixo na máquina", "Peito", 3, 12);
        peito.adicionarForca("Crossover", "Peito", 3, 12);
        planos.add(peito);

        TreinoPredefinido bracos = new TreinoPredefinido("Braços");
        bracos.adicionarForca("Rosca direta", "Bíceps", 3, 10);
        bracos.adicionarForca("Rosca martelo", "Bíceps", 3, 12);
        bracos.adicionarForca("Tríceps na polia", "Tríceps", 3, 12);
        bracos.adicionarForca("Tríceps francês", "Tríceps", 3, 10);
        planos.add(bracos);

        TreinoPredefinido abdomen = new TreinoPredefinido("Abdômen");
        abdomen.adicionarForca("Abdominal crunch", "Abdômen", 3, 15);
        abdomen.adicionarForca("Elevação de pernas", "Abdômen", 3, 12);
        abdomen.adicionarForca("Abdominal bicicleta", "Abdômen", 3, 20);
        abdomen.adicionarForca("Dead bug", "Abdômen", 3, 10);
        planos.add(abdomen);

        return List.copyOf(planos);
    }

    private void adicionarForca(String nome, String grupoMuscular, int series, int repeticoes) {
        exercicios.add(new ExercicioForca(nome, grupoMuscular, series, repeticoes));
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
