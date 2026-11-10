package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Usuario {
    
    private int id;
    private String nome;
    private int idade;
    private double peso;      // em kg
    private double altura;    // em cm (ex: 174)
    private String objetivo;  // EMAGRECER, GANHAR_MASSA, SAUDE_GERAL

    // // [ENTRADA PRINCIPAL] O Construtor valida todos os dados iniciais
    public Usuario(int id, String nome, int idade, double peso, double altura, String objetivo) {
        
        // // Valida se o nome está vazio, nulo ou contém números
        if (nome == null || nome.trim().isEmpty() || nome.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Erro: O nome não pode estar vazio ou conter números.");
        }
        
        // // Valida se a idade está num intervalo real (1 a 120 anos)
        if (idade <= 0 || idade > 120) {
            throw new IllegalArgumentException("Erro: Idade inválida (" + idade + ").");
        }
        
        // // Valida se o peso é maior que zero e menor que 500kg
        if (peso <= 0 || peso > 500) {
            throw new IllegalArgumentException("Erro: Peso inválido (" + peso + ").");
        }
        
        // // Valida se a altura em cm é realista (entre 50cm e 300cm)
        if (altura <= 50 || altura > 300) {
            throw new IllegalArgumentException("Erro: Altura inválida (" + altura + "). Use cm (ex: 174).");
        }

        this.id = id;
        this.nome = nome.trim();
        this.idade = idade;
        this.peso = peso;
        this.altura = altura;
        this.objetivo = objetivo != null ? objetivo : "SAUDE_GERAL";
    }

    // --- Métodos de Negócio (IMC) ---

    // // Calcula o IMC usando BigDecimal para garantir precisão decimal
    public BigDecimal calcularIMC() {
        BigDecimal pesoBd = BigDecimal.valueOf(this.peso);
        BigDecimal alturaBd = BigDecimal.valueOf(this.altura);
        
        // // Fórmula do IMC com altura em cm e arredondamento HALF_UP
        return pesoBd.multiply(BigDecimal.valueOf(10000))
                .divide(alturaBd.multiply(alturaBd), 1, RoundingMode.HALF_UP);
    }

    // // Classifica o resultado do IMC de acordo com as faixas da OMS
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

    // --- Getters e Setters com validações defensivas ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    
    // // [ENTRADA SECUNDÁRIA] Setter para alterar o nome depois
    public void setNome(String nome) { 
        if (nome == null || nome.trim().isEmpty() || nome.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Nome inválido.");
        }
        this.nome = nome.trim(); 
    }

    public int getIdade() { return idade; }
    
    // // [ENTRADA SECUNDÁRIA] Setter para alterar a idade depois
    public void setIdade(int idade) { 
        if (idade <= 0 || idade > 120) { throw new IllegalArgumentException("Idade inválida."); }
        this.idade = idade; 
    }

    public double getPeso() { return peso; }
    
    // // [ENTRADA SECUNDÁRIA] Setter para alterar o peso depois
    public void setPeso(double peso) { 
        if (peso <= 0 || peso > 500) { throw new IllegalArgumentException("Peso inválido."); }
        this.peso = peso; 
    }

    public double getAltura() { return altura; }
    
    // // [ENTRADA SECUNDÁRIA] Setter para alterar a altura depois
    public void setAltura(double altura) { 
        if (altura <= 50 || altura > 300) { throw new IllegalArgumentException("Altura inválida."); }
        this.altura = altura; 
    }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    // // Formata a saída textual com dados e IMC do utilizador
    @Override
    public String toString() {
        return String.format("Usuário #%d - %s, %d anos | Altura: %.0fcm | Peso: %.1fkg | IMC: %s (%s)",
                id, nome, idade, altura, peso, calcularIMC(), obterClassificacaoIMC());
    }
}
