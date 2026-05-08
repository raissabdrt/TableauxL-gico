package TABLEAUX;

import java.util.*;

/*
 * @IsoldaLis
 * Esta é a classe Main para testar e demonstrar o funcionamento do Tableaux Analítico,
 * responsável por criar exemplos de uso e testar a implementação!
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== PROVADOR DE TABLEAUX ANALÍTICOS ===");

        // Testando exemplo 1: p → q, q → r ⊢ p → r (VÁLIDO)
        System.out.println("1. Testando: p → q, q → r ⊢ p → r");

        // Criando átomos:
        Formula p = new Formula("p", true);
        Formula q = new Formula("q", true);
        Formula r = new Formula("r", true);

        // Criando fórmulas: T(p → q), T(q → r) e F(p → r):
        Formula imp1 = new Formula(Categoria.IMPLICACAO, p, q, true);
        Formula imp2 = new Formula(Categoria.IMPLICACAO, q, r, true);
        Formula imp3 = new Formula(Categoria.IMPLICACAO, p, r, false);

        List<Formula> formulas1 = Arrays.asList(imp1, imp2, imp3);
        boolean resultado1 = Tableaux.provar(formulas1);
        System.out.println("Resultado: " + resultado1 + " (deve ser true - VÁLIDO)");

        // Testando exemplo 2: p ∨ ¬p (TAUTOLOGIA) - CORRIGIDO
        System.out.println("\n2. Testando: p ∨ ¬p (tautologia)");

        Formula notP = new Formula(Categoria.NEGACAO, p, false); // ¬p
        Formula pOrNotP = new Formula(Categoria.OU, p, notP, false); // F(p ∨ ¬p)

        List<Formula> formulas2 = Arrays.asList(pOrNotP);
        boolean resultado2 = Tableaux.provar(formulas2);
        System.out.println("Resultado: " + resultado2 + " (deve ser true - TAUTOLOGIA)");

        // Testando exemplo 3: p ∧ q ⊢ p (VÁLIDO)
        System.out.println("\n3. Testando: p ∧ q ⊢ p");

        Formula pAndQ = new Formula(Categoria.E, p, q, true); // T(p ∧ q)
        Formula notP2 = new Formula("p", false); // Fp
        List<Formula> formulas3 = Arrays.asList(pAndQ, notP2);
        boolean resultado3 = Tableaux.provar(formulas3);
        System.out.println("Resultado: " + resultado3 + " (deve ser true - VÁLIDO)");

        // Testando exemplo 4: p ⊢ q (INVÁLIDO)
        System.out.println("\n4. Testando: p ⊢ q (inválido)");

        Formula pTrue = new Formula("p", true); // Tp
        Formula qFalse = new Formula("q", false); // Fq
        List<Formula> formulas4 = Arrays.asList(pTrue, qFalse);
        boolean resultado4 = Tableaux.provar(formulas4);
        System.out.println("Resultado: " + resultado4 + " (deve ser false - INVÁLIDO)");

        // Testando exemplo 5: (p → q) ∧ (q → r) → (p → r) (VÁLIDO)
        System.out.println("\n5. Testando: (p → q) ∧ (q → r) → (p → r)");

        // T((p→q)∧(q→r)) e F(p→r)
        Formula imp4 = new Formula(Categoria.IMPLICACAO, p, q, true);
        Formula imp5 = new Formula(Categoria.IMPLICACAO, q, r, true);
        Formula conj = new Formula(Categoria.E, imp4, imp5, true);
        Formula imp6 = new Formula(Categoria.IMPLICACAO, p, r, false);

        List<Formula> formulas5 = Arrays.asList(conj, imp6);
        boolean resultado5 = Tableaux.provar(formulas5);
        System.out.println("Resultado: " + resultado5 + " (deve ser true - VÁLIDO)");

        System.out.println("\n=== FIM DOS TESTES ===");
    }

    // Método auxiliar para criar fórmulas a partir de strings
    public static Formula criarFormulaPorString(String expressao) {
        try {
            return Tableaux.criarFormula(expressao);
        } catch (Exception e) {
            System.out.println("Erro ao criar fórmula: " + e.getMessage());
            return null;
        }
    }
}