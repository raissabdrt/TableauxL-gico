package TABLEAUX;

/*
@iza.francine
 * Essa classe, Fórmula, foi criada com o objetivo de representar computacionalmente
 * e manipular as fórmulas da lógica proposicional dentro do tableau. Aqui, as expressões
 * lógicas serão transformadas para um formato que possa ser interpretado em termos de 
 * máquina, como exemplo: V(P ^ Q) -> aqui, P ^ Q é verdade (V), ou ainda F(P -> Q) -> em 
 * que P -> Q é falso (F). Dessa forma, carrega também o valor verdade das fórmulas 
 * trabalhadas. Além disso, contém métodos que auxiliam na implementação da classe Tableau. 
 * 
 */

public class Formula {

    private Categoria categoria;    //Enumeração dos tipos possíveis 
    private String atomo;           //String para fórmulas atômicas
    private Formula esquerda;       //Referenciamento para subfórmulas menores 
    private Formula direita;        //Referenciamento para subfórmulas menores
    private boolean valorVerdade;   //Indica o valor de verdade das fórmulas/átomos

    /* Método Construtor específico para Átomos */

    Formula(String atomo, boolean valorVerdade){
        this.categoria = Categoria.ATOMO;
        this.atomo = atomo;
        this.valorVerdade = valorVerdade;
    }
 
    /* Método Construtor específico para Operador Unário. */

    Formula(Categoria categoria, Formula esquerda, boolean valorVerdade){
        if( categoria != Categoria.NEGACAO){
            throw new IllegalArgumentException("Não é um operador unário (NEGAÇÃO)! ");
        }
        this.categoria = categoria;
        this.esquerda = esquerda;
        this.valorVerdade = valorVerdade;
    }

    /* Método Construtor específico para Operados Binários */

    Formula(Categoria categoria, Formula esquerda, Formula direita,
            boolean valorVerdade){
        if( categoria == Categoria.ATOMO || categoria == Categoria.NEGACAO){
            throw new IllegalArgumentException("Não é um operador binário! ");
        }
        this.categoria = categoria;
        this.esquerda = esquerda;
        this.direita = direita;
        this.valorVerdade = valorVerdade;
    }

    /** 
     * @return Categoria
     */
    /* Métodos Getter's para acessar os atributos privados */

    public Categoria getCategoria() {
        return categoria;
    }

    /** 
     * @return String
     */
    public String getAtomo() {
        return atomo;
    }

    /** 
     * @return Formula
     */
    public Formula getEsquerda() {
        return esquerda;
    }

    /** 
     * @return Formula
     */
    public Formula getDireita() {
        return direita;
    }

    /** 
     * @return boolean
     */
    public boolean isValorVerdade() {
        return valorVerdade;
    }


    /** 
     * @return boolean
     */
    /* Métodos própios para utilização na classe Tlabeaux */
    /* Aqui, as regras do Tableaux são implementadas, definindo se uma fórmula é Alfa (Que
     * seguindo as regras, significa que não ramifica), ou se é Beta (Se ramifica gerando 
     * novas possibilidades na árvore do Tbleaux), dependendo do rótulo (V/F) e do tipo do 
     * conectivo lógico, seguindo a mesma lógica empregada no Tableaux convencional. Sua 
     * importância está em permitir a classificação da fórmula em um dos dois tipos (Alfa 
     * ou Beta).
     * Além disso, contém o método ehAtomo, que identifica se uma fórmula é um átomo (uma
     * folha na árvore do Tableaux); Ela é fundamental para expressar a condição de parada 
     * das expansões.
     */

    public boolean ehAlfa(){
        switch (categoria){
            case E:
                return valorVerdade;           // V(A∧B) é alfa
            case NEGACAO:
                return true;                   // CORREÇÃO: TODAS as negações são alfa (V¬A e F¬A)
            case OU:
                return !valorVerdade;          // F(A∨B) é alfa
            case IMPLICACAO:
                return !valorVerdade;          // F(A→B) é alfa
            default:
                return false;
        }
    }

    public boolean ehBeta(){
        switch (categoria){
            case E:
                return !valorVerdade;          // F(A∧B) é beta
            case OU:
                return valorVerdade;           // V(A∨B) é beta
            case IMPLICACAO:
                return valorVerdade;           // V(A→B) é beta
            default:
                return false;                  // NEGAÇÕES NUNCA são beta
        }
    }
    
    /** 
     * @return boolean
     */
    public boolean ehAtomo(){
        return categoria == Categoria.ATOMO;
    }

    /** 
     * @return String
     */
    /* Métodos Sobrescritos  */

    /* Esse método é importante no contexto de saída, para que o usuário possa compreender
     * a notação da fórmula. Assim, transforma a estrutura interpretada pela máquina para 
     * uma notação lógica entendível.
     */
    @Override
    public String toString() {
        if(categoria == Categoria.ATOMO){
            return atomo != null ? atomo : ""; // Garante que não retorne null
        }
        else if(categoria == Categoria.NEGACAO){
            String subFormulaStr = esquerda != null ? esquerda.toString() : "";
            return "¬" + subFormulaStr;
        }
        else{
            String operacao = " ";
            switch (categoria){
                case E: operacao = " ^ "; break;
                case OU: operacao = " v "; break;
                case IMPLICACAO: operacao = " -> "; break;
                default: break;
            }
            String esqStr = esquerda != null ? esquerda.toString() : "";
            String dirStr = direita != null ? direita.toString() : "";
            return "(" + esqStr + " " + operacao + " " + dirStr + ")";
        }
    }
    /** 
     * @param objeto
     * @return boolean
     */
    /* Aqui, o próposito é determinar se duas fórmulas são semanticamente equivalentes, sendo
     * importante para garantir que fórmulas iguais sejam tratadas igualmente, além de poder 
     * impedir a expansão redundante da mesma fórmula. 
     */
    @Override
    public boolean equals(Object objeto){
        if(this == objeto){
            return true;
        }
        if(objeto == null || getClass() != objeto.getClass()){
            return false;
        }

        Formula novoObjeto = (Formula) objeto;
        String thisStr = this.toString();
        String objStr = novoObjeto.toString();

        // Verifica se nenhuma das strings é null antes de comparar
        if (thisStr == null && objStr == null) return true;
        if (thisStr == null || objStr == null) return false;

        return thisStr.equals(objStr);
    }

    /** 
     * @return int
     */
    /* Esse método garante que fórmulas iguais tenham hash's iguais, e que assim possam ser
     * buscadas mais rapidamente.
     */
    @Override
    public int hashCode(){
        return toString().hashCode();
    }

    /* Especificações/Possíveis Dúvidas:  
     * -> Fórmulas construídas recursivamente, para facilitar o manuseio de fórmulas maiores;
     * -> Atributos privados para manter o encapsulamento;
     * -> Dados imutáveis como constantes (ATOMO, NEGACAO, E, OU, IMPLICACAO).
     * 
     * -> O princípio da recursividade é mantido em "private Formula esquerda" e "private 
     * Formula direita;", uma vez que isso permite que uma fórmula tenha outras fórmulas 
     * dentro dela mesma, e assim colaborando para a estrutura de árvore que é utilizada no
     * Tableaux.
     * -> Os métodos construtores são implementados separadamente para que cada fórmula seja
     * construída separadamente, uma vez que temos fórmulas atômicas, com operadores unários
     * e operadores binários.
     * 
    */


}
