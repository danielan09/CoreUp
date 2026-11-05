package model;

public class ExercicioCardio extends Exercicio {
    private int duracaoMinutos;

    public ExercicioCardio(String nome, int duracaoMinutos) {
        super(nome,"Cardio");
            this.duracaoMinutos = duracaoMinutos;
        }

        public int getDuracaoMinutos() { return duracaoMinutos; }

        @Override
        public String getDescricao() {
        return getNome() + "(Cardio) -" +duracaoMinutos + "min";
        }
}