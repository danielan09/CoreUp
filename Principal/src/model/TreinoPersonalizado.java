package model;

public class TreinoPersonalizado extends Treino {

    public TreinoPersonalizado() {
        super("Treino do Usuário");
    }

    public void adicionarExercicio(Exercicio exercicio) {
        exercicios.add(exercicio);
    }

    public boolean removerExercicio(int indice) {
        if (indice < 0 || indice >= exercicios.size()) {
            return false;
        }
        exercicios.remove(indice);
        return true;
    }

    @Override
    public String getOrigem() {
        return "Personalizado";
    }
}
