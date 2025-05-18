import java.util.*;

public class LexemeTable {
    private final List<Token> lexemes = new ArrayList<>();

    public void add(Token token) {
        lexemes.add(token);
    }

    public List<Token> getLexemes() {
        return lexemes;
    }

    public void print() {
        System.out.println("Таблица лексем:");
        for (Token token : lexemes) {
            System.out.printf("%s\t'%s'\t(%d:%d)\n", token.getType(), token.getValue(), token.getLine(), token.getColumn());
        }
    }
    public void printTable() {
        System.out.println("Таблица лексем:");
        System.out.printf("%-4s %-12s %-12s %-6s %-6s%n", "№", "Лексема", "Тип", "Строка", "Колонка");
        int i = 1;
        for (Token token : lexemes) {
            System.out.printf("%-4d %-12s %-12s %-6d %-6d%n",
                    i++, token.getValue(), token.getType(), token.getLine(), token.getColumn());
        }
    }

}
