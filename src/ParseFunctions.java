
public class ParseFunctions implements IParseFunctions {
    StringBuffer sb = new StringBuffer("");
    boolean exprFlag;
    String lineNumStr = "Строка ";

    public int make(String S, StringBuffer sb, int num_string) {
        this.sb = sb;
        exprFlag = false;  // наличие необязательного выражения до ;
        lineNumStr = "Строка ";
        lineNumStr = lineNumStr + num_string + ": ";
        S = S.trim();
        if (S.length() == 0) {
            return 1;
        }
        S = S.toLowerCase();
        S = _For(S); // ищется слово for
        if (S.length() > 0){
            S = OpeningBrace(S); // ищется '('
        }else {
            sb.append(lineNumStr + "Отсутствует '('\n");
        }
        if (S.length() > 0 && !(S.substring(0, 1)).equals(";")
                && !(S.substring(0, 1)).equals(")"))
            S = Identificator(S); // ищется идентификатор
        if (exprFlag) // если идентификатор был найден
        {
            S = EqualSign(S); // ищется =
            S = Const(S); // ищется константа
        }
        if (S.length() > 0) {
            if (!(S.substring(0, 1)).equals(";")) // если нет ;
            {
                sb.append(lineNumStr + "Нет ; 1\n");
            } else {
                S = S.substring(1, S.length());
                S = S.trim();
            }
        } else {
            sb.append(lineNumStr + "Нет ; 1\n");
        }
        exprFlag = false;
        if (S.length() > 0 && !(S.substring(0, 1)).equals(";")
                && !(S.substring(0, 1)).equals(")"))
            S = Identificator(S); // сканируем идентификатор
        if (exprFlag) // если идентификатор был найден
        {
            S = LogicSign(S); // логический знак
            S = IdentOrCons(S); // идентификатор или константа
        }
        if (S.length() > 0) {
            if (!(S.substring(0, 1)).equals(";")) // если нет ;
            {
                sb.append(lineNumStr + "Нет ; 2\n");
            } else {
                S = S.substring(1, S.length());
                S = S.trim();
            }
        } else {
            sb.append(lineNumStr + "Нет ; 2\n");
        }
        exprFlag = false;
        if (S.length() > 0 && !(S.substring(0, 1)).equals(")"))
            S = Identificator(S); // сканируем идентификатор
        if (exprFlag) // если идентификатор был найден
        {
            if (S.length() == 0 || !(S.substring(0, 1)).equals("+")
                    && !(S.substring(0, 1)).equals("-"))
                sb.append(lineNumStr + "Нет  ++ or --\n");
            else {
                S = DoublePlusOrMinus(S);
            } // ищем ++ или --
            exprFlag = false;
        }
        if (S.length() > 0) {
            S = ClosingBrace(S); // ищется ')'
        } else {
            sb.append(lineNumStr + "Отсутствует ')'\n");
        }
        if (S.length() == 0 || (S.substring(0, 1)).equals(";"))
            sb.append(lineNumStr + "Отсутствут идентификатор после цикла\n");
        else if (!exprFlag)
            S = Identificator(S); // сканируем идентификатор
        S = EqualSign(S); // ищется =
        S = ArifExpr(S); // сканируем арифметическое выражение
        if (S.length() == 0 || !(S.substring(0, 1)).equals(";")) // если нет
        // последней
        // ';'
        {
            sb.append(lineNumStr + "Нет ; 3\n");
        } else {
            S = S.substring(1, S.length());
            S = S.trim();
        }
        if (S.length() != 0) // окончание
        {
            sb.append(lineNumStr + "Недопустимое сочетание символов '" + S + "'\n");
        }
        return 0;
    }

   public String _For(String S) {
        if (S.length() > 2 && S.substring(0, 3).equals("for")) // если найден
        // for
        {
            S = S.substring(3, S.length());
            S = S.trim();
            return S;
        }
        int j = 0;
        boolean fl = false;
        if (S.length() > 4) {
            do {
                j++;
                if (S.substring(j, j + 4).equals(" for")) {
                    fl = true;
                    break;
                }
            } while (!(S.substring(j - 1, j)).equals("(")
                    && !(S.substring(j - 1, j)).equals(";")
                    && !(S.substring(j - 1, j)).equals(")")
                    && S.length() > j + 4);
        }
        if (fl) // если найден for
        {
            sb.append(lineNumStr + "Недопустимое сочетание символов '"
                    + S.substring(0, j) + "'\n");
            j += 5;
        } else // если не найден for
        {
            sb.append(lineNumStr + "Отсутствует ключевое слово 'for'\n");
            if (j == 0) {
                while (j < S.length()) {
                    if (S.substring(j, j + 1).equals(";")
                            || S.substring(j, j + 1).equals("(")
                            || S.substring(j, j + 1).equals(")")) {
                        fl = true;
                        break;
                    }
                    j++;
                }
                if (fl) {
                    if (j != 0) {
                        sb.append(lineNumStr + "Недопустимое сочетание символов '"
                                + S.substring(0, j) + "'\n");
                    }
                    j++;
                } else {
                    sb.append(lineNumStr + "Недопустимое сочетание символов '"
                            + S.substring(0, S.length()) + "'\n");
                    j = S.length() + 1;
                }

            } else {
                if (j != 1) {
                    sb.append(lineNumStr + "Недопустимое сочетание символов '"
                            + S.substring(0, j - 1) + "'\n");
                    // j++;
                }
            }
        }
        S = S.substring(j - 1, S.length());
        S = S.trim();
        return S;
    }

    public String OpeningBrace(String S) {
        if (S.length() > 0 && S.substring(0, 1).equals("(")) // если найдена '('
        {
            S = S.substring(1, S.length());
            S = S.trim();
            return S;
        }
        int j = 0;
        boolean fl = false;
        if (S.length() > 1) {
            do {
                j++;
                if (S.substring(j, j + 1).equals("(")) {
                    fl = true;
                    break;
                }
            } while (!S.substring(j - 1, j).equals(";")
                    && !S.substring(j - 1, j).equals(")") && S.length() > j + 1);
        }
        if (fl) // если найдена '('
        {
            sb.append(lineNumStr + "Недопустимое сочетание символов '"
                    + S.substring(0, j) + "'\n");
            j += 3;
        } else // если не найдена '('
        {
            sb.append(lineNumStr + "Отсутствует '('\n");
            if (j == 0) {
                while (j < S.length()) {
                    if (S.substring(j, j + 1).equals(";")
                            || S.substring(j, j + 1).equals("(")
                            || S.substring(j, j + 1).equals(")")) {
                        fl = true;
                        break;
                    }
                    j++;
                }
                if (fl) {
                    if (j != 0) {
                        sb.append(lineNumStr + "Недопустимое сочетание символов '"
                                + S.substring(0, j) + "'\n");
                    }
                    j++;
                } else {
                    //if (j != 0) {
                    sb.append(lineNumStr + "Недопустимое сочетание символов '"
                            + S.substring(0, S.length()) + "'\n");
                    j = S.length() + 1;
                    //}
                }

            } else {
                //j++;
                //if (j != 1) {
                 /*   sb.append(lineNumStr + "Недопустимое сочетание символов '"
                            + S.substring(0, j - 1) + "'\n");
                    j++;   */
                //}
                boolean fst= false;
                int n=0;
                while (j <= S.length()) {
                    if (S.substring(j-1, j).equals(";")
                            || S.substring(j-1, j ).equals("(")
                            || S.substring(j-1, j ).equals(")")) {
                        if(n==0) fst=true;
                        fl = true;
                        break;
                    }
                    j++;
                    n++;
                }
                if(!fst){
                    if (fl) {
                        if (j != 0) {
                            sb.append(lineNumStr + "Недопустимое сочетание символов '"
                                    + S.substring(0, j-1) + "'\n");
                        }
                        ///j++;
                    } else {
                        //if (j != 0) {
                        sb.append(lineNumStr + "Недопустимое сочетание символов '"
                                + S.substring(0, S.length()) + "'\n");
                        j = S.length() + 1;
                        //}
                    }
                }
            }
        }
        S = S.substring(j - 1, S.length());
        S = S.trim();
        return S;
    }

    public String Identificator(String S) {
        int i = 0, j = 0, f = 0;
        String Expr = null;
        if (S.length() == 0 || S.substring(i, i + 1).equals(")")
                || S.substring(i, i + 1).equals(";")
                || S.substring(i, i + 1).equals("=")
                || S.substring(i, i + 1).equals("<")
                || S.substring(i, i + 1).equals(">")
                || S.substring(i, i + 1).equals("!") || S.length() == 1
                || S.substring(i, i + 2).equals("++")
                || S.substring(i, i + 2).equals("--")) {
            // если не найден
            // идентификатор
            sb.append(lineNumStr + "Отсутствует идентификатор переменной\n");
            exprFlag = true;

            S = S.trim();
            return S;
        }
        exprFlag = true;
        for (i = 1; i < S.length(); i++) // считываем символы в буфер
        {
            if (S.substring(i, i + 1).equals("\0")
                    || S.substring(i, i + 1).equals("=")
                    || S.substring(i, i + 1).equals(";")
                    || S.substring(i, i + 1).equals(" ")
                    || S.substring(i, i + 1).equals(">")
                    || S.substring(i, i + 1).equals("<")
                    || S.substring(i, i + 1).equals(")")
                    || i <= S.length() - 2
                    && (S.substring(i, i + 2).equals(">=")
                    || S.substring(i, i + 2).equals("<=")
                    || S.substring(i, i + 2).equals("==")
                    || S.substring(i, i + 2).equals("!=")
                    || S.substring(i, i + 2).equals("++") || S
                    .substring(i, i + 2).equals("--"))) {
                Expr = S.substring(0, i).trim();
                j = i;
                break;
            }
        }
        if (j == 0) { // если отсканировали до конца строки
            f = 1;
            Expr = S.trim();
            j = S.length();
        }
        if (!Character.isDigit(S.charAt(0))) // если не цифра
        {
            boolean fl = false;
            for (i = 0; i < Expr.length(); i++) { // если неверный формат
                // идентификатора
                if (!Character.isLetter(S.charAt(i))
                        && !Character.isDigit(S.charAt(i)))
                    fl = true;
            }
            if (fl)
                sb.append(lineNumStr + "Неверный идентификатор переменной '"
                        + Expr + "'\n");
        } else { // если неверный формат идентификатора
            sb.append(lineNumStr + "Неверный идентификатор переменной '" + Expr
                    + "'\n");
        }
        if (f == 1)
            return S.substring(j, S.length()).trim(); // если конец строки
        return S.substring(j, S.length()).trim();
    }

    public String EqualSign(String S) {
        if (S.length() > 0 && S.substring(0, 1).equals("=")) // если =
        {
            return S.substring(1, S.length()).trim();
        } else // если не =
        {
            sb.append(lineNumStr + "Пропущен символ '='\n");
            return S.trim();
        }
    }

    public String Const(String S) {
        int i = 0, j = 0;
        String Expr = null;
        if (S.length() == 0 || S.substring(i, i + 1).equals(";")
                || S.substring(i, i + 1).equals(")")) // если
        // отсутствует
        // число
        {
            sb.append(lineNumStr + "Отсутствует константа\n");
            S = S.trim();
            return S;
        }
        exprFlag = true;
        for (; i < S.length(); i++) // записываем константу в буфер
        {
            if (S.substring(i, i + 1).equals(")")
                    || S.substring(i, i + 1).equals(";")
                    || S.substring(i, i + 1).equals(" ")) {
                Expr = S.substring(/* 1, i + i - 1 */0, i).trim();
                j = i;
                break;
            }
        }
        if (j == 0) {
            Expr = S.trim();
            j = S.length();
        }
        i = 0;
        for (; i < Expr.length(); i++) {
            // формат константы
            if (!Character.isDigit(S.charAt(i))) // если неверный формат
            // константы
            {
                sb.append(lineNumStr + "Неверный формат конcтанты '" + Expr
                        + "'\n");
                break;
            }
        }
        return S.substring(j, S.length()).trim();
    }

    public String LogicSign(String S) {
        int i = 0, j = 0;
        String Expr = null;
        if (S.substring(0, 1).equals(";") || S.length() == 0) // если отсутствует логический знак
        {
            sb.append(lineNumStr + "Отсутствует логический знак\n");
            S = S.trim();
            return S;
        }
        if (S.substring(0, 2).equals(">=") || S.substring(0, 2).equals("<=")
                || S.substring(0, 2).equals("==")
                || S.substring(0, 2).equals("!=")) { // если знак найден
            return S.substring(2, S.length()).trim();
        }
        // если знак найден
        if (S.substring(0, 1).equals(">") || S.substring(0, 1).equals("<")) {
            return S.substring(1, S.length()).trim();
        }
        for (; i < S.length(); i++) // запишем в буфер
        {
            if (S.substring(i, i + 1).equals(";")
                    || S.substring(i, i + 1).equals(" ")) {
                Expr = S.substring(/* 1, i + i - 1 */0, i).trim();
                j = i;
                break;
            }
            if (S.substring(i, i + 2).equals(">=")
                    || S.substring(i, i + 2).equals("<=")
                    || S.substring(i, i + 2).equals("==")
                    || S.substring(i, i + 2).equals("!=")) {
                Expr = S.substring(/* 1, i + i - 1 */0, i).trim();
                sb.append(lineNumStr + "Нераспознанные символы '" + Expr + "'\n");
                return S.substring(i + 2, S.length()).trim();
            }

            if (S.substring(i, i + 1).equals(">")
                    || S.substring(i, i + 1).equals("<")) {
                Expr = S.substring(/* 1, i + i - 1 */0, i).trim();
                sb.append(lineNumStr + "Нераспознанные символы '" + Expr + "'\n");
                return S.substring(i + 1, S.length()).trim();
            }
        }
        if (j == 0) {
            Expr = S.trim();
            j = S.length();
        }
        sb.append(lineNumStr + "Нераспознанные символы '" + Expr + "'\n");
        sb.append(lineNumStr + "Отсутствует логический знак\n");
        return S.substring(j, S.length()).trim();
    }

    public String IdentOrCons(String S) {
        int i = 0, j = 0;
        if (S.substring(i, i + 1).equals(";")
                || S.substring(i, i + 1).equals(")") || S.length() == 0) {
            sb.append(lineNumStr + "Отсутствует переменная или константа\n");
            S = S.trim();
            return S;
        }
        if (Character.isLetter(S.charAt(0)))
            return Identificator(S); // если идентификатор
        if (Character.isDigit(S.charAt(0)))
            return Const(S); // если число
        String Expr = null;
        for (; i <= S.length(); i++) // запишем в буфер
        {
            if (S.substring(i, i + 1).equals(";")
                    || S.substring(i, i + 1).equals(" ")) {
                Expr = S.substring(/* 1, i + i - 1 */0, i).trim();
                j = i;
                break;
            }
        }
        if (j == 0) {
            Expr = S.trim();
            j = S.length();
        }
        sb.append(lineNumStr + "Нераспознанные символы '" + Expr + "'\n");
        sb.append(lineNumStr + "Отсутствует переменная или константа\n");
        return S.substring(j, S.length()).trim();
    }

    public String DoublePlusOrMinus(String S) {
        int i = 0, j = 0;
        String Expr = null;
        if (S.substring(0, 1).equals(";") || S.substring(0, 1).equals(")")
                || S.length() == 0) {
            sb.append(lineNumStr + "Отсутствует постфиксное увеличение\n");
            S = S.trim();
            return S;
        }
        if (S.substring(0, 2).equals("++") || S.substring(0, 2).equals("--")) // найдено
        // правильное
        // сочетание
        {
            return S.substring(2, S.length()).trim();
        }
        for (; i < S.length(); i++) { // сканируем в буфер
            if (S.substring(i, i + 1).equals(";")
                    || S.substring(i, i + 1).equals(")")
                    || S.substring(i, i + 1).equals(" ")) {
                Expr = S.substring(/* 1, i + i - 1 */0, i).trim();
                j = i;
                break;
            }
            if (S.substring(i, i + 2).equals("++")
                    || S.substring(i, i + 2).equals("--")) // найдено
            // правильное
            // сочетание
            {
                Expr = S.substring(/* 1, i + i - 1 */0, i).trim();
                sb.append(lineNumStr + "Нераспознанные символы '" + Expr + "'\n");
                return S.substring(i + 2, S.length()).trim();
            }
        }
        if (j == 0) {
            Expr = S.trim();
            j = S.length();
        }
        sb.append(lineNumStr + "Нераспознанные символы '" + Expr + "'\n");
        sb.append(lineNumStr + "Отсутствует постфиксное увеличение\n");
        return S.substring(j, S.length()).trim();
    }

    public String ClosingBrace(String S) {
        if (S.substring(0, 1).equals(")")) // если найдена ')'
        {
            S = S.substring(1, S.length());
            S = S.trim();
            return S;
        }
        int j = 0;
        boolean fl = false;
        do {
            j++;
            if (S.substring(j, j + 1).equals(")")) {
                fl = true;
                break;
            }
        } while (!S.substring(j, j + 1).equals(" ")
                && !S.substring(j, j + 1).equals("=") && S.length() >= j
                && !Character.isLetter(S.charAt(j)));
        if (fl) // если найдена ')'
        {
            sb.append(lineNumStr + "Недопустимое сочетание символов '"
                    + S.substring(/* 1, 1 + j - 1 */0, j) + "'\n");
            j += 2;
        } else {
            sb.append(lineNumStr + "Отсутствует ')'\n");
            if (j != 1) {
                sb.append(lineNumStr + "Недопустимое сочетание символов '"
                        + S.substring(/* 1, 1 + j - 1 */0, j) + "'\n");
            }
        }
        S = S.substring(j - 1, S.length());
        S = S.trim();
        return S;
    }

    public String ArifExpr(String S) {
        int i = 0;
        if (S.length() == 0 || S.substring(i, i + 1).equals(";")) // если нет
        // выражения
        {
            sb.append(lineNumStr + "Отсутствует выражение в теле цикла\n");
            S = S.trim();
            return S;
        }
        int fl = 0;
        for (; i < S.length(); i++) {
            // сканируем терм
            if (S.substring(i + 1, i + 2).equals("+")
                    || S.substring(i + 1, i + 2).equals("-")
                    || S.substring(i + 1, i + 2).equals(";")
                    || i - 1 == S.length()) {
                T(S.substring(/* fl, i - fl + 1 */fl, i + 1).trim());
                fl = i + 2;
            }
            if (S.substring(i + 1, i + 2).equals(";")) {
                return S.substring(i + 1, S.length());
            }
        }

        S = S.substring(i, S.length());
        S = S.trim();
        return S;
    }

    public void T(String S) {
        int i = 0;
        int beg = 0;
        for (; i < S.length(); i++) { // сканируем операнд
            if (S.substring(i, i + 1).equals("*")
                    || S.substring(i, i + 1).equals("/")) // операнд
            // до
            // знака
            {
                S = S.trim();
                O(S.substring(beg, /* i + i - beg */i).trim());
                beg = i + 1;
            }
        }
        O(S.substring(beg, /* i + i - */i).trim()); // последний операнд
    }

    public void O(String S) {
        if (S.length() == 0) // отсутствует операнд
        {
            sb.append(lineNumStr + "Отсутствует константа или переменная\n");
        }
        if (Character.isDigit(S.charAt(0))) // если цифра
        {
            boolean fl = false;
            for (int i = 0; i < S.length(); i++) {
                if (!Character.isDigit(S.charAt(i)))
                    fl = true;
            }
            if (fl)
                sb.append(lineNumStr + "Неверный формат константы в выражении '"
                        + S + "'\n");
        } else // если символ
        {
            boolean fl = false;
            for (int i = 0; i < S.length(); i++) {
                if (!Character.isDigit(S.charAt(i))
                        && !Character.isLetter(S.charAt(i)))
                    fl = true;
            }
            if (fl)
                sb.append(lineNumStr + "Неверный идентификатор переменной '" + S
                        + "'\n");
        }
    }

}
