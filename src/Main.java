public class Main {
    public static void main(String[] args) {
        String code = "x := 42;\ny := x + 1;";
        Lexer lexer = new Lexer(code);
        lexer.tokenizeAll();
        lexer.printTables();
    }
}