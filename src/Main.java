import model.Usuario;

public class Main {
    public static void main(String[] args) {
        Usuario usuario = new Usuario("Wesley", 25);

        TreinoPredefinido treinoDoApp = new TreinoPredefinido(usuario.getIdade());

        TreinoPersonalizado treinoDoUsuario = new TreinoPersonalizado();
        treinoDoUsuario.adicionarExercicio(
                new ExercicioForca("Flexão de braço", "Peito", 3, 10));
        treinoDoUsuario.adicionarExercicio(
                new ExercicioCardio("Corrida leve", 15));

        TreinoCompleto treinoCompleto = new TreinoCompleto(
                treinoDoApp,
                treinoDoUsuario
        );
        usuario.setTreino(treinoCompleto);
        usuario.getTreino().exibir();
    }
}
