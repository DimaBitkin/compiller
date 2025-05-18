public class Main {
    public static void main(String[] args) {
        String code = "x <> 5; { this is a comment } y = 10;";
        Lexer lexer = new Lexer(code);

        Token token;
        while ((token = lexer.nextToken()) != null) {
            System.out.println(token);
        }
    }
}
