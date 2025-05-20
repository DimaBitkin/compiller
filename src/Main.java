public class Main {
    public static void main(String[] args) {
        String code = "begin x ass 2; y ass x + 1; end.";
        Lexer lexer = new Lexer(code);
        lexer.tokenizeAll();
        lexer.printTables();
        Parser parser = new Parser(lexer.getLexemeTable().getLexemes());
        parser.parseProgram();
    }
}