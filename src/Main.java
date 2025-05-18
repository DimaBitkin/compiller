public class Main {
    public static void main(String[] args) {
        String code = "x <> 5; { this is a comment } y = 10;";
        Lexer lexer = new Lexer(code);
        Token token;
        do {
            token = lexer.nextToken();
            System.out.println(token);
        } while (token.getType() != TokenType.EOF);
    }
}
