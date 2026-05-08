package TABLEAUX;

import java.util.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/*
 * @IsoldaLis
 * Essa é a classe de testes unitários para validar o Tableaux Analítico
 * Utiliza JUnit para testes automatizados.
 */
public class TableauxTeste {

    private Formula p, q, r;

    @Before
    public void setUp() {
        p = new Formula("p", true);
        q = new Formula("q", true);
        r = new Formula("r", true);
    }

    @Test
    public void testExemploValido() {
        // p → q, q → r ⊢ p → r
        Formula imp1 = new Formula(Categoria.IMPLICACAO, p, q, true);
        Formula imp2 = new Formula(Categoria.IMPLICACAO, q, r, true);
        Formula imp3 = new Formula(Categoria.IMPLICACAO, p, r, false);

        List<Formula> formulas = Arrays.asList(imp1, imp2, imp3);
        assertTrue(Tableaux.provar(formulas));
    }

    @Test
    public void testTautologia() {
        // p ∨ ¬p
        Formula notP = new Formula(Categoria.NEGACAO, p, false);
        List<Formula> formulas = Arrays.asList(notP);
        assertTrue(Tableaux.provar(formulas));
    }

    @Test
    public void testConjunçãoValida() {
        // p ∧ q ⊢ p
        Formula pAndQ = new Formula(Categoria.E, p, q, true);
        Formula notP = new Formula("p", false);
        List<Formula> formulas = Arrays.asList(pAndQ, notP);
        assertTrue(Tableaux.provar(formulas));
    }

    @Test
    public void testArgumentoInvalido() {
        // p ⊢ q (inválido)
        Formula pTrue = new Formula("p", true);
        Formula qFalse = new Formula("q", false);
        List<Formula> formulas = Arrays.asList(pTrue, qFalse);
        assertFalse(Tableaux.provar(formulas));
    }

    @Test
    public void testExpansaoAlfa() {
        // Testa expansão de fórmula alfa: V(A ∧ B)
        Formula pAndQ = new Formula(Categoria.E, p, q, true);
        List<Formula> resultado = Tableaux.expandirAlfa(pAndQ);

        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).toString().contains("Vp"));
        assertTrue(resultado.get(1).toString().contains("Vq"));
    }

    @Test
    public void testExpansaoBeta() {
        // Testa expansão de fórmula beta: V(A ∨ B)
        Formula pOrQ = new Formula(Categoria.OU, p, q, true);
        List<List<Formula>> resultado = Tableaux.expandirBeta(pOrQ);

        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).size());
        assertEquals(1, resultado.get(1).size());
    }

    @Test
    public void testRamoFechado() {
        // Ramo com Vp e Fp deve estar fechado
        Formula vp = new Formula("p", true);
        Formula fp = new Formula("p", false);
        List<Formula> ramo = Arrays.asList(vp, fp);

        assertTrue(Tableaux.estaFechado(ramo));
    }

    @Test
    public void testRamoAberto() {
        // Ramo com Vp e Vq deve estar aberto
        Formula vp = new Formula("p", true);
        Formula vq = new Formula("q", true);
        List<Formula> ramo = Arrays.asList(vp, vq);

        assertFalse(Tableaux.estaFechado(ramo));
    }
}