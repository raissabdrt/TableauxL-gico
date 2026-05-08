package TABLEAUX;

import java.util.*;

/*
 @raissa.brito
 Esta é a classe tablaux, que implementa o algoritmo de tableaux analíticos para lógica proposicional.
 Ela irá mostrar que, para provar que Γ ⊢ φ, assumimos Γ verdadeiro e φ falso, e, dessa forma, tentamos encontrar uma contradição.
  OS PRINCIPAIS PRINCÍPIOS DO TABLEAUX SÃO:
  1. Fórmulas marcadas com V (Verdadeiro) e F (Falso);
  2. Regras de expansão α (não ramificam) e β (ramificam);
  3. Ramo fechado quando encontra contradição (Vp e Fp);
  4. Tableaux fechado quando todos os ramos fecham.
 */

class Tableaux {

    /* REGRAS DE EXPANSÃO ALFA:
      As fórmulas alfa, quando expandidas, vão gerar somente um ramo com novas fórmulas.
      As regras alfa são:
      1. V(A ∧ B)→VA, VB
      2. F(A ∨ B)→FA, FB
      3. F(A → B)→VA, FB
      4. V¬A→FA
      5. F¬A→VA
     */

    public static List<Formula> expandirAlfa(Formula formula) {
        List<Formula> resultado = new ArrayList<>();

        if (!formula.ehAlfa()) {
            throw new IllegalArgumentException("A fórmula " + formula + " não é do tipo alfa!");
        }

        boolean valorVerdade = formula.isValorVerdade();
        Categoria categoria = formula.getCategoria();

        if (valorVerdade) {
            switch (categoria) {
                case E: // regra: V(A∧B)→VA,VB
                    resultado.add(criarFormulaComValor(formula.getEsquerda(), true));
                    resultado.add(criarFormulaComValor(formula.getDireita(), true));
                    break;

                case NEGACAO: // regra: V¬A→FA
                    resultado.add(criarFormulaComValor(formula.getEsquerda(), false));
                    break;
            }
        } else {
            switch (categoria) {
                case OU: // regra: F(A∨B)→FA,FB
                    resultado.add(criarFormulaComValor(formula.getEsquerda(), false));
                    resultado.add(criarFormulaComValor(formula.getDireita(), false));
                    break;

                case IMPLICACAO: // regra: F(A→B)→VA,FB
                    resultado.add(criarFormulaComValor(formula.getEsquerda(), true));
                    resultado.add(criarFormulaComValor(formula.getDireita(), false));
                    break;

                case NEGACAO:
                    if (valorVerdade) {
                        // V¬A → FA
                        resultado.add(criarFormulaComValor(formula.getEsquerda(), false));
                    } else {
                        // F¬A → VA  ← CORREÇÃO CRÍTICA!
                        resultado.add(criarFormulaComValor(formula.getEsquerda(), true));
                    }
                    break;
            }
        }

        return resultado;
    }

    /* REGRAS DA EXPANSÃO BETA:
      As fórmulas beta, quando expandidas, vão gerar dois ou mais ramos diferentes com novas fórmulas.
      As regras beta são:
       1. V(A∨B)→VA|VB (dois ramos)
       2. V(A→B)→FA|VB (dois ramos)
       3. F(A∧B)→FA|FB (dois ramos)
     */
    public static List<List<Formula>> expandirBeta(Formula formula) {
        List<List<Formula>> resultado = new ArrayList<>();

        if (!formula.ehBeta()) {
            throw new IllegalArgumentException("A fórmula " + formula + " não é do tipo beta!");
        }

        boolean valorVerdade = formula.isValorVerdade();
        Categoria categoria = formula.getCategoria();

        if (valorVerdade) {
            switch (categoria) {
                case OU: // regra: V(A∨B)→[VA],[VB]
                    List<Formula> ramo1 = new ArrayList<>();
                    ramo1.add(criarFormulaComValor(formula.getEsquerda(), true));

                    List<Formula> ramo2 = new ArrayList<>();
                    ramo2.add(criarFormulaComValor(formula.getDireita(), true));

                    resultado.add(ramo1);
                    resultado.add(ramo2);
                    break;

                case IMPLICACAO: // regra: V(A→B)→[FA],[VB]
                    List<Formula> ramo1_impl = new ArrayList<>();
                    ramo1_impl.add(criarFormulaComValor(formula.getEsquerda(), false));

                    List<Formula> ramo2_impl = new ArrayList<>();
                    ramo2_impl.add(criarFormulaComValor(formula.getDireita(), true));

                    resultado.add(ramo1_impl);
                    resultado.add(ramo2_impl);
                    break;
            }
        } else {
            switch (categoria) {
                case E: // regra: F(A∧B)→[FA],[FB]
                    List<Formula> ramo1_and = new ArrayList<>();
                    ramo1_and.add(criarFormulaComValor(formula.getEsquerda(), false));

                    List<Formula> ramo2_and = new ArrayList<>();
                    ramo2_and.add(criarFormulaComValor(formula.getDireita(), false));

                    resultado.add(ramo1_and);
                    resultado.add(ramo2_and);
                    break;

                case NEGACAO: // regra: F¬A→[VA] (CASO ESPECIAL)
                    List<Formula> ramo_neg = new ArrayList<>();
                    ramo_neg.add(criarFormulaComValor(formula.getEsquerda(), true));
                    resultado.add(ramo_neg);
                    break;
            }
        }

        return resultado;
    }


    /* VERIFICAÇÃO DE RAMO FECHADO:
      Um ramo vai estar fechado quando contiver uma contradição clara:
      - Existe pelo menos um átomo que aparece com V e F no mesmo ramo;
      - Exemplo: se o ramo contém Vp e Fp, então vai estar fechado.
     */

    public static boolean estaFechado(List<Formula> ramo) {
        Map<String, Boolean> valoresAtomos = new HashMap<>();

        for (Formula formula : ramo) {
            if (formula.ehAtomo()) {
                String nomeAtomo = formula.getAtomo();
                Boolean valorExistente = valoresAtomos.get(nomeAtomo);

                if (valorExistente != null) {
                    if (valorExistente != formula.isValorVerdade()) {
                        return true; // RAMO FECHADO - contradição encontrada
                    }
                } else {
                    valoresAtomos.put(nomeAtomo, formula.isValorVerdade());
                }
            }
            // VERIFICAÇÃO ADICIONAL PARA NEGAÇÕES
            else if (formula.getCategoria() == Categoria.NEGACAO) {
                Formula subFormula = formula.getEsquerda();
                if (subFormula.ehAtomo()) {
                    // Verificar se existe a negação contraditória no ramo
                    boolean valorNegado = !formula.isValorVerdade();
                    for (Formula f : ramo) {
                        if (f.ehAtomo() && f.getAtomo().equals(subFormula.getAtomo()) &&
                                f.isValorVerdade() == valorNegado) {
                            return true; // Contradição encontrada
                        }
                    }
                }
            }
        }

        return false;
    }

    /*
         MÉTODO PRINCIPAL PARA QUE PROVEMOS POR TABLEAUX:
         1. Primeiramente, vamos assumir as premissas como V e conclusão como F;
         2. Depois, iremos expandir as fórmulas sistematicamente, dando prioridade para as fórmulas do tipo alfa;
         3. Então, vamos verificar as contradições em cada ramo;
         4. Logo após, se todos os ramos fecharem chegaremos a conclusão de que o argumento é válido;
         5. Porém, se verificarmos que algum ramo vai permanecer aberto, podemos concluir que é um argumento inválido.
         */
    public static boolean provar(List<Formula> formulas) {
        Stack<List<Formula>> pilhaRamos = new Stack<>();
        Set<String> historico = new HashSet<>();

        // Exibir fórmulas iniciais
        List<String> formulasComValor = new ArrayList<>();
        for (Formula f : formulas) {
            formulasComValor.add((f.isValorVerdade() ? "V" : "F") + f.toString());
        }
        System.out.println("Iniciando o tableaux com: " + formulasComValor);

        pilhaRamos.push(new ArrayList<>(formulas));

        while (!pilhaRamos.isEmpty()) {
            List<Formula> ramoAtual = pilhaRamos.pop();

            // Exibir ramo atual
            List<String> ramoComValor = new ArrayList<>();
            for (Formula f : ramoAtual) {
                ramoComValor.add((f.isValorVerdade() ? "V" : "F") + f.toString());
            }
            System.out.println("Processando ramo: " + ramoComValor);

            // Verificar se ramo já foi processado
            String hashRamo = ramoComValor.toString();
            if (historico.contains(hashRamo)) {
                System.out.println("Este ramo já foi processado");
                continue;
            }
            historico.add(hashRamo);

            // Verificar se ramo está fechado
            if (estaFechado(ramoAtual)) {
                System.out.println("Este ramo foi fechado devido a contradição");
                continue;
            }

            boolean expandiu = false;

            // Primeiro: expandir fórmulas alfa
            for (int i = 0; i < ramoAtual.size(); i++) {
                Formula formula = ramoAtual.get(i);

                if (formula.ehAtomo()) {
                    continue;
                }

                if (formula.ehAlfa()) {
                    System.out.println("Expandindo a fórmula alfa: " + (formula.isValorVerdade() ? "V" : "F") + formula.toString());

                    List<Formula> novasFormulas = expandirAlfa(formula);

                    // Exibir novas fórmulas
                    List<String> novasComValor = new ArrayList<>();
                    for (Formula f : novasFormulas) {
                        novasComValor.add((f.isValorVerdade() ? "V" : "F") + f.toString());
                    }
                    System.out.println("Novas formulas: " + novasComValor);

                    // Criar novo ramo REMOVENDO a fórmula expandida e adicionando as novas
                    List<Formula> novoRamo = new ArrayList<>(ramoAtual);
                    novoRamo.remove(i);
                    novoRamo.addAll(novasFormulas);

                    pilhaRamos.push(novoRamo);
                    expandiu = true;
                    break; // Uma expansão por vez
                }
            }

            if (!expandiu) {
                // Segundo: expandir fórmulas beta
                for (int i = 0; i < ramoAtual.size(); i++) {
                    Formula formula = ramoAtual.get(i);

                    if (formula.ehAtomo()) {
                        continue;
                    }

                    if (formula.ehBeta()) {
                        System.out.println("Expandindo fórmula beta: " + (formula.isValorVerdade() ? "V" : "F") + formula.toString());

                        List<List<Formula>> alternativas = expandirBeta(formula);

                        // Exibir alternativas
                        List<List<String>> alternativasComValor = new ArrayList<>();
                        for (List<Formula> alternativa : alternativas) {
                            List<String> altComValor = new ArrayList<>();
                            for (Formula f : alternativa) {
                                altComValor.add((f.isValorVerdade() ? "V" : "F") + f.toString());
                            }
                            alternativasComValor.add(altComValor);
                        }
                        System.out.println("Alternativas: " + alternativasComValor);

                        // Para cada alternativa, criar um novo ramo REMOVENDO a fórmula expandida
                        for (List<Formula> alternativa : alternativas) {
                            List<Formula> novoRamo = new ArrayList<>(ramoAtual);
                            novoRamo.remove(i);
                            novoRamo.addAll(alternativa);
                            pilhaRamos.push(novoRamo);
                        }

                        expandiu = true;
                        break; // Uma expansão por vez
                    }
                }
            }

            if (!expandiu) {
                // Ramo saturado - não há mais fórmulas para expandir
                System.out.println("Ramo saturado e aberto, pois foi encontrado um contraexemplo!");
                return false;
            }
        }

        System.out.println("Todos os ramos fecharam!");
        return true;
    }


    // Método auxiliar para verificar se uma fórmula já foi expandida
    private static boolean foiExpandida(Formula formula, List<Formula> ramo) {
        // Estratégia: verificar por conteúdo
        // Criar uma assinatura única baseada no conteúdo da fórmula
        if (formula.ehAtomo()) {
            return true;
        }
        // Criar assinatura baseada no conteúdo
        String assinatura = formula.getCategoria() + ":" +
                (formula.isValorVerdade() ? "V" : "F") + ":" +
                formula.toString();
        // Verificar se já vimos uma fórmula com esta assinatura no ramo
        for (Formula f : ramo) {
            if (f == formula) {
                // Mesma instância, então definitivamente já foi considerada
                return true;
            }
            // Verificar por conteúdo similar
            String assinaturaF = f.getCategoria() + ":" +
                    (f.isValorVerdade() ? "V" : "F") + ":" +
                    f.toString();
            if (assinatura.equals(assinaturaF)) {
                // Mesmo conteúdo - considerar como já expandida
                return true;
            }
        }
        return false;
    }
    // estes são os métodos auxiliares, que tem a intenção de auxiliar para criar objetos Formula a partir de Strings:

    public static Formula criarFormula(String expressao) {
        expressao = expressao.trim();
        if (expressao.startsWith("V")) {
            return processarExpressao(expressao.substring(1), true);
        } else if (expressao.startsWith("F")) {
            return processarExpressao(expressao.substring(1), false);
        } else {
            throw new IllegalArgumentException("Expressão deve começar com V ou F: " + expressao);
        }
    }

    // este método processa expressões recursivamente, ele vai processar a string da fórmula e construir uma formula (objeto Formula) correspondente. O método vai fazer isso de forma recursiva, por meio dos parênteses e dos operadores:
    private static Formula processarExpressao(String expressao, boolean valor) {
        expressao = expressao.trim();
        // negação: ¬A
        if (expressao.startsWith("¬")) {
            Formula subFormula = processarExpressao(expressao.substring(1), true);
            return new Formula(Categoria.NEGACAO, subFormula, valor);
        }
        // parênteses: (A operador B)
        if (expressao.startsWith("(") && expressao.endsWith(")")) {
            String interior = expressao.substring(1, expressao.length() - 1);
            // encontra o operador principal:
            int nivelParenteses = 0;
            for (int i = 0; i < interior.length(); i++) {
                char c = interior.charAt(i);
                if (c == '(') nivelParenteses++;
                else if (c == ')') nivelParenteses--;
                else if (nivelParenteses == 0) {
                    // encontra os operadores binários:
                    if (c == '^') {
                        String parteEsquerda = interior.substring(0, i).trim();
                        String parteDireita = interior.substring(i + 1).trim();
                        Formula esq = processarExpressao(parteEsquerda, true);
                        Formula dir = processarExpressao(parteDireita, true);
                        return new Formula(Categoria.E, esq, dir, valor);
                    } else if (c == 'v') {
                        String parteEsquerda = interior.substring(0, i).trim();
                        String parteDireita = interior.substring(i + 1).trim();
                        Formula esq = processarExpressao(parteEsquerda, true);
                        Formula dir = processarExpressao(parteDireita, true);
                        return new Formula(Categoria.OU, esq, dir, valor);
                    } else if (expressao.contains("->")) {
                        // tratamento para a implicação:
                        int indexImplicacao = interior.indexOf("->");
                        if (indexImplicacao != -1) {
                            String parteEsquerda = interior.substring(0, indexImplicacao).trim();
                            String parteDireita = interior.substring(indexImplicacao + 2).trim();
                            Formula esq = processarExpressao(parteEsquerda, true);
                            Formula dir = processarExpressao(parteDireita, true);

                            return new Formula(Categoria.IMPLICACAO, esq, dir, valor);
                        }
                    }
                }
            }
        }

        // este é para o caso de um átomo simples, sem os operadores:
        return new Formula(expressao, valor);
    }
    // Método auxiliar para criar fórmulas com valores de verdade específicos
    public static Formula criarFormulaComValor(Formula formula, boolean valorVerdade) {
        if (formula.ehAtomo()) {
            return new Formula(formula.getAtomo(), valorVerdade);
        } else if (formula.getCategoria() == Categoria.NEGACAO) {
            return new Formula(Categoria.NEGACAO, formula.getEsquerda(), valorVerdade);
        } else {
            return new Formula(formula.getCategoria(), formula.getEsquerda(), formula.getDireita(), valorVerdade);
        }
    }
}