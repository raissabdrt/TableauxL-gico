# Tableaux Analítico - Provador de Lógica Proposicional

Implementação em Java do método de Tableaux Analítico para lógica proposicional. O sistema verifica a validade de argumentos lógicos assumindo as premissas como verdadeiras e a conclusão como falsa, expandindo a árvore sistematicamente em busca de contradições.

---

## Sobre o Projeto

O Tableaux Analítico é um método de prova por refutação: para provar que `Γ ⊢ φ`, assume-se `Γ` verdadeiro e `φ` falso e tenta-se fechar todos os ramos da árvore com contradições. Se todos os ramos fecharem, o argumento é válido; se algum ramo permanecer aberto (saturado sem contradição), o argumento é inválido e esse ramo constitui um contraexemplo.

---

## Estrutura de Classes

```
├── Categoria.java       # Enum dos tipos de fórmula (ATOMO, NEGACAO, E, OU, IMPLICACAO)
├── Formula.java         # Representação de fórmulas proposicionais (árvore recursiva)
├── Tableaux.java        # Algoritmo principal de prova por tableaux
├── Main.java            # Exemplos de uso e demonstração
└── TableauxTeste.java   # Testes unitários com JUnit
```

---

## Representação das Fórmulas

As fórmulas são construídas recursivamente como uma árvore, onde cada nó carrega:

- a **categoria** do conectivo (átomo, negação, conjunção, disjunção, implicação)
- o **valor verdade** associado (V ou F), seguindo a notação do tableaux analítico
- referências para as **subfórmulas** esquerda e direita

Três construtores distintos cobrem os três casos estruturais:

| Construtor | Uso |
|---|---|
| `Formula(String atomo, boolean valor)` | Proposições atômicas (p, q, r...) |
| `Formula(Categoria, Formula esq, boolean valor)` | Operador unário (negação) |
| `Formula(Categoria, Formula esq, Formula dir, boolean valor)` | Operadores binários (^, v, ->) |

---

## Regras de Expansão

### Regras Alfa (não ramificam)

Geram um único ramo com novas fórmulas.

| Fórmula | Resultado |
|---|---|
| V(A ^ B) | VA, VB |
| F(A v B) | FA, FB |
| F(A -> B) | VA, FB |
| V(¬A) | FA |
| F(¬A) | VA |

### Regras Beta (ramificam)

Geram dois ramos alternativos.

| Fórmula | Ramo 1 | Ramo 2 |
|---|---|---|
| V(A v B) | VA | VB |
| V(A -> B) | FA | VB |
| F(A ^ B) | FA | FB |

---

## Algoritmo de Prova

O método `Tableaux.provar()` opera com uma pilha de ramos, processando um ramo por vez:

1. Verifica se o ramo já foi processado (controle de histórico via `HashSet`)
2. Verifica se o ramo está fechado — existe um átomo com V e F simultaneamente
3. Expande **fórmulas alfa** com prioridade (sem ramificação)
4. Se não houver alfa, expande **fórmulas beta** (gera novos ramos na pilha)
5. Se o ramo estiver saturado (sem fórmulas para expandir) e não fechado, retorna `false` — argumento inválido

O argumento é válido somente se todos os ramos fecharem.

---

## Exemplos de Uso

```java
// Definir átomos
Formula p = new Formula("p", true);
Formula q = new Formula("q", true);
Formula r = new Formula("r", true);

// Provar: p -> q, q -> r |- p -> r
Formula imp1 = new Formula(Categoria.IMPLICACAO, p, q, true);   // V(p->q)
Formula imp2 = new Formula(Categoria.IMPLICACAO, q, r, true);   // V(q->r)
Formula imp3 = new Formula(Categoria.IMPLICACAO, p, r, false);  // F(p->r)

boolean valido = Tableaux.provar(Arrays.asList(imp1, imp2, imp3));
// valido == true
```

Casos cobertos no `Main.java`:

| Argumento | Esperado |
|---|---|
| p -> q, q -> r ⊢ p -> r | Válido |
| p v ¬p (tautologia) | Válido |
| p ^ q ⊢ p | Válido |
| p ⊢ q | Inválido |
| (p->q) ^ (q->r) -> (p->r) | Válido |

---

## Como Executar

### Pré-requisitos
- Java 11 ou superior
- JUnit 4 (para os testes)

### Compilação

```bash
javac TABLEAUX/*.java
```

### Execução

```bash
java TABLEAUX.Main
```

### Testes unitários

```bash
# Com JUnit no classpath:
java -cp .:junit-4.x.jar org.junit.runner.JUnitCore TABLEAUX.TableauxTeste
```

---

## Testes Unitários

A classe `TableauxTeste.java` cobre os seguintes cenários com JUnit 4:

- Argumento válido com implicações encadeadas
- Tautologia (lei do terceiro excluído)
- Conjunção válida
- Argumento inválido (contraexemplo)
- Expansão de fórmula alfa
- Expansão de fórmula beta
- Verificação de ramo fechado
- Verificação de ramo aberto

---

## Decisões de Design

**Enum `Categoria`** centraliza os tipos de conectivo como constantes imutáveis, evitando strings mágicas e garantindo type safety.

**Árvore recursiva em `Formula`** com referências `esquerda` e `direita` reflete diretamente a estrutura sintática das fórmulas proposicionais, simplificando a expansão recursiva.

**Prioridade alfa antes de beta** no algoritmo de prova é uma otimização clássica do tableaux: expansões alfa não aumentam o número de ramos, reduzindo o espaço de busca.

**`Stack` para gerenciar ramos** é a estrutura natural para a exploração da árvore, permitindo processar cada ramo de forma isolada sem recursão explícita.

**`HashSet` de histórico** previne o reprocessamento de ramos idênticos, evitando loops em grafos com ciclos lógicos.

---

## Autores

Projeto desenvolvido para a disciplina de **Lógica para Computação**.
