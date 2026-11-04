package model;

public class ExercicioCardio extends Exercicio {
    private final int duracao;

    public ExercicioCardio(String var1, String var2, int var3) {
        super(var1, var2);
        if (var3 <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero.");
        } else {
            this.duracao = var3;
        }
    }

    public String getDescricao() {
        String var10000 = this.getNome();
        return "Exercicio: " + var10000 + "\nGrupo muscular: " + this.getGrupoMuscular() + "\nDuracao: " + this.duracao + " minutos";
    }
}
