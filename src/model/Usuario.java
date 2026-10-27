package model;

public class Usuario {
    private static int proximoId = 1;

    private int id;
    private String nome;
    private int idade;
    private TreinoCompleto treino;

    public Usuario(String nome, int idade) {
        while (idade <=0 || idade >120) {
            throw new IllegalArgumentException("idade inválida: " + idade);
        }
        this.id = proximoId++;
        this.nome = nome;
        this.idade = idade;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getIdade() {return idade; }
    public TreinoCompleto getTreino() { return treino; }
    public void setTreino(TreinoCompleto treino) { this.treino = treino; }

    @Override
    public String toString(){
        return "Usuário " + id + " - " + nome + ", " +idade + " anos";
    }
}
