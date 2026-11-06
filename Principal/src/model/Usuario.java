package model;

public class Usuario {
    private int id;
    private String nome;
    private int idade;

    public Usuario (String nome, int idade) {
        if (idade<= 0 || idade > 120) {
            throw new IllegalArgumentException("Idade inválida: " + idade);
        }
        this.id = id;
        this.nome = nome;
        this.idade = idade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public int getIdade() { return idade; }

    @Override
    public String toString() {
        return "Usuário #" +id+ "-" +nome+ "," +idade+ "anos";
    }
}