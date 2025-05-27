import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class main2 {
    public static void main(String[] args) {

        String startSymbol = "Программа";

        Set<String> nonTerminals = Set.of(
                "Программа", "Описание", "Идентификаторы", "Тип", "ОператорныйБлок", "ДопОператоры",
                "Оператор", "Составной", "СоставныеОператоры", "Присваивание", "Условный", "ИначеБлок",
                "ФиксЦикл", "УсловнЦикл", "Ввод", "Вывод", "Выражения", "Выражение", "Операнд", "Слагаемое",
                "Множитель", "Число", "Целое", "Действительное", "ЛогическаяКонстанта",
                "ОперацияОтношения", "ОперацияСложения", "ОперацияУмножения", "УнарнаяОперация",
                "ЧисловаяСтрока", "Порядок", "Цифра", "Комментарий", "ВсёВнутри"
        );

        Set<String> terminals = Set.of(
                "program", "var", "begin", "end", "INTEGER", "FLOAT", "BOOL",
                "if", "then", "else", "for", "to", "do", "while",
                "read", "write", "ass", "(", ")", "{", "}", ":", ";",
                "+", "-", "*", "/", "and", "or", "<", ">", "<=", ">=", "=", "<>",
                "IDENTIFIER", "true", "false",
                ".", ",", "NEWLINE", "ε",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y", "Z",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z"

        );

        List<GrammarNormalizer.Rule> rules = List.of(
                new GrammarNormalizer.Rule("Программа", "program var Описание begin ОператорныйБлок end ."),
                new GrammarNormalizer.Rule("Описание", "Тип IDENTIFIER Идентификаторы"),
                new GrammarNormalizer.Rule("Идентификаторы", ", IDENTIFIER Идентификаторы"),
                new GrammarNormalizer.Rule("Идентификаторы", "ε"),
                new GrammarNormalizer.Rule("Тип", "INTEGER"),
                new GrammarNormalizer.Rule("Тип", "FLOAT"),
                new GrammarNormalizer.Rule("Тип", "BOOL"),
                new GrammarNormalizer.Rule("ОператорныйБлок", "Оператор ДопОператоры"),
                new GrammarNormalizer.Rule("ДопОператоры", "; Оператор ДопОператоры"),
                new GrammarNormalizer.Rule("ДопОператоры", "ε"),
                new GrammarNormalizer.Rule("Оператор", "Составной"),
                new GrammarNormalizer.Rule("Оператор", "Присваивание"),
                new GrammarNormalizer.Rule("Оператор", "Условный"),
                new GrammarNormalizer.Rule("Оператор", "ФиксЦикл"),
                new GrammarNormalizer.Rule("Оператор", "УсловнЦикл"),
                new GrammarNormalizer.Rule("Оператор", "Ввод"),
                new GrammarNormalizer.Rule("Оператор", "Вывод"),
                new GrammarNormalizer.Rule("Составной", "Оператор СоставныеОператоры"),
                new GrammarNormalizer.Rule("СоставныеОператоры", ": Оператор СоставныеОператоры"),
                new GrammarNormalizer.Rule("СоставныеОператоры", "NEWLINE Оператор СоставныеОператоры"),
                new GrammarNormalizer.Rule("СоставныеОператоры", "ε"),
                new GrammarNormalizer.Rule("Присваивание", "IDENTIFIER ass Выражение"),
                new GrammarNormalizer.Rule("Условный", "if Выражение then Оператор ИначеБлок"),
                new GrammarNormalizer.Rule("ИначеБлок", "else Оператор"),
                new GrammarNormalizer.Rule("ИначеБлок", "ε"),
                new GrammarNormalizer.Rule("ФиксЦикл", "for Присваивание to Выражение do Оператор"),
                new GrammarNormalizer.Rule("УсловнЦикл", "while Выражение do Оператор"),
                new GrammarNormalizer.Rule("Ввод", "read ( IDENTIFIER Идентификаторы )"),
                new GrammarNormalizer.Rule("Вывод", "write ( Выражение Выражения )"),

                new GrammarNormalizer.Rule("ОперацияОтношения", "<"),
                new GrammarNormalizer.Rule("ОперацияОтношения", ">"),
                new GrammarNormalizer.Rule("ОперацияОтношения", "="),
                new GrammarNormalizer.Rule("ОперацияОтношения", "<="),
                new GrammarNormalizer.Rule("ОперацияОтношения", ">="),
                new GrammarNormalizer.Rule("ОперацияОтношения", "<>"),

                // Операции сложения
                new GrammarNormalizer.Rule("ОперацияСложения", "+"),
                new GrammarNormalizer.Rule("ОперацияСложения", "-"),
                new GrammarNormalizer.Rule("ОперацияСложения", "or"),

                // Операции умножения
                new GrammarNormalizer.Rule("ОперацияУмножения", "*"),
                new GrammarNormalizer.Rule("ОперацияУмножения", "/"),
                new GrammarNormalizer.Rule("ОперацияУмножения", "and"),

                // Унарная операция
                new GrammarNormalizer.Rule("УнарнаяОперация", "not"),

                // Выражения
                new GrammarNormalizer.Rule("Выражение", "Операнд"),
                new GrammarNormalizer.Rule("Выражение", "Операнд ОперацияОтношения Операнд"),

                new GrammarNormalizer.Rule("Операнд", "Слагаемое"),
                new GrammarNormalizer.Rule("Операнд", "Слагаемое ОперацияСложения Слагаемое"),

                new GrammarNormalizer.Rule("Слагаемое", "Множитель"),
                new GrammarNormalizer.Rule("Слагаемое", "Множитель ОперацияУмножения Множитель"),

                new GrammarNormalizer.Rule("Множитель", "IDENTIFIER"),
                new GrammarNormalizer.Rule("Множитель", "Число"),
                new GrammarNormalizer.Rule("Множитель", "ЛогическаяКонстанта"),
                new GrammarNormalizer.Rule("Множитель", "УнарнаяОперация Множитель"),
                new GrammarNormalizer.Rule("Множитель", "( Выражение )"),

                // Логическая константа
                new GrammarNormalizer.Rule("ЛогическаяКонстанта", "true"),
                new GrammarNormalizer.Rule("ЛогическаяКонстанта", "false"),

                // Число
                new GrammarNormalizer.Rule("Число", "Целое"),
                new GrammarNormalizer.Rule("Число", "Действительное"),

                // Целые числа
                new GrammarNormalizer.Rule("Целое", "Двоичное"),
                new GrammarNormalizer.Rule("Целое", "Восьмеричное"),
                new GrammarNormalizer.Rule("Целое", "Десятичное"),
                new GrammarNormalizer.Rule("Целое", "Шестнадцатеричное")







        );

        List<GrammarNormalizer.Rule> normalizedRules =
                GrammarNormalizer.normalize(startSymbol, nonTerminals, terminals, rules);

        GrammarNormalizer.printRules(normalizedRules, terminals);


    }
}
//
//                new GrammarNormalizer.Rule("Выражения", ", Выражение Выражения"),
//                new GrammarNormalizer.Rule("Выражения", "ε"),
//                new GrammarNormalizer.Rule("Выражение", "IDENTIFIER"),
//                new GrammarNormalizer.Rule("Выражение", "INTEGER"),
//                new GrammarNormalizer.Rule("Выражение", "FLOAT"),
//                new GrammarNormalizer.Rule("Выражение", "( Выражение )"),
//                new GrammarNormalizer.Rule("Выражение", "Выражение Операция Выражение"),
//
//
//
//
//
//                new GrammarNormalizer.Rule("Операция", "+"),
//                new GrammarNormalizer.Rule("Операция", "-"),
//                new GrammarNormalizer.Rule("Операция", "*"),
//                new GrammarNormalizer.Rule("Операция", "/"),
//                new GrammarNormalizer.Rule("Операция", "and"),
//                new GrammarNormalizer.Rule("Операция", "or"),
//                new GrammarNormalizer.Rule("Операция", "<"),
//                new GrammarNormalizer.Rule("Операция", ">"),
//                new GrammarNormalizer.Rule("Операция", "=="),
//                new GrammarNormalizer.Rule("Комментарий", "{ ВсёВнутри }"),
//                new GrammarNormalizer.Rule("ВсёВнутри", "ID ВсёВнутри"),
//                new GrammarNormalizer.Rule("ВсёВнутри", "INTEGER ВсёВнутри"),
//                new GrammarNormalizer.Rule("ВсёВнутри", "FLOAT ВсёВнутри"),
//                new GrammarNormalizer.Rule("ВсёВнутри", "ε") // можно добавить обобщенное правило