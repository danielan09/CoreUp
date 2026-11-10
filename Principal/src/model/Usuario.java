package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Usuario {
    
    private int id;
    private String nome;
    private int idade;
    
    // Novos campos do Módulo de Perfil e IMC
    private double peso;      // em kg
    private double altura;    // em cm
    private String objetivo;  // EMAGRECER, GANHAR_MASSA, SAUDE_GERAL

    public Usuario(String nome, int idade) {
        this(0, nome, idade, 1, 1, "SAUDE_GERAL");
    }

    // Construtor atualizado mantendo a validação original e adicionando os novos campos
    public Usuario(int id, String nome, int idade, double peso, double altura, String objetivo) {
        if (idade <= 0 || idade > 120) {
            throw new IllegalArgumentException("Idade inválida: " + idade);
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("Peso deve ser maior que zero.");
        }
        if (altura <= 0) {
            throw new IllegalArgumentException("Altura deve ser maior que zero.");
        }

        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.peso = peso;
        this.altura = altura;
        this.objetivo = objetivo;
    }

    // --- Métodos de Negócio (IMC) ---

    // Cálculo do IMC usando BigDecimal e arredondamento HALF_UP (altura em cm)
    public BigDecimal calcularIMC() {
        BigDecimal pesoBd = BigDecimal.valueOf(this.peso);
        BigDecimal alturaBd = BigDecimal.valueOf(this.altura);
        
        return pesoBd.multiply(BigDecimal.valueOf(10000))
                .divide(alturaBd.multiply(alturaBd), 1, RoundingMode.HALF_UP);
    }

    // Classificação do IMC baseada na tabela da OMS
    public String obterClassificacaoIMC() {
        BigDecimal imc = calcularIMC();
        
        if (imc.compareTo(new BigDecimal("18.5")) < 0) {
            return "ABAIXO_DO_PESO";
        } else if (imc.compareTo(new BigDecimal("25.0")) < 0) {
            return "NORMAL";
        } else if (imc.compareTo(new BigDecimal("30.0")) < 0) {
            return "SOBREPESO";
        } else if (imc.compareTo(new BigDecimal("35.0")) < 0) {
            return "OBESIDADE_I";
        } else if (imc.compareTo(new BigDecimal("40.0")) < 0) {
            return "OBESIDADE_II";
        } else {
            return "OBESIDADE_III";
        }
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    @Override
    public String toString() {
        return String.format("Usuário #%d - %s, %d anos | IMC: %s (%s)",
                id, nome, idade, calcularIMC(), obterClassificacaoIMC());
    }
}
