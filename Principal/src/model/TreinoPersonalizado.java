package model;

public class TreinoPersonalizado extends Treino {
    public TreinoPersonalizado() {
        super("Treino do Usuário");
    }

    public boolean removerExercicio(int var1) {
        if (var1 >= 0 && var1 < this.exercicios.size()) {
            this.exercicios.remove(var1);
            return true;
        } else {
            return false;
        }
    }

    public String getOrigem() {
        return "Personalizado";
    }
}
