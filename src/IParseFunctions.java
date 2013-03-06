/**
* Interface for parsing grammar
* */
public interface IParseFunctions {
    int make(String string, StringBuffer sb, int i);

    String _For(String S);

    String OpeningBrace(String S);

    String Identificator(String S);

    String EqualSign(String S);

    String Const(String S); // <ЦБЗ> грамматики

    String LogicSign(String S);

    String IdentOrCons(String S); // <ид.ч.> грамматики

    String DoublePlusOrMinus(String S); // <пос>

    String ClosingBrace(String S);

    String ArifExpr(String S); // <АВ> грамматики

    void T(String S); // T - грамматики

    void O(String S); // O - грамматики
}
