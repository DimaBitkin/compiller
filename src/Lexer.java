import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int column = 1;

    private static final Set<String> KEYWORDS = Set.of(
            "program", "var", "begin", "end", "int", "float", "bool",
            "if", "then", "else", "for", "to", "do", "while",
            "read", "write", "true", "false"
    );
    private static final Set<String> OPERATORS = Set.of(
            "=", "<>", "<", "<=", ">", ">=", "+", "-", "*", "/", "or", "and", "not", "ass"
    );
    private static final Set<Character> SEPARATORS = Set.of(
            '(', ')', ',', ';', '.'
    );


    public Lexer(String input) {
        this.input = input;
    }

    private char currentChar() {
        return pos < input.length() ? input.charAt(pos) : '\0';
    }

    private void advance() {
        if (currentChar() == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        pos++;
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(currentChar())) {
            advance();
        }
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private boolean isLetterOrDigit(char c) {
        return Character.isLetterOrDigit(c);
    }

    public Token nextToken() {
        skipWhitespace();

        char c = currentChar();
        int startLine = line;
        int startColumn = column;

        if (c == '\0') return null; // конец входа
        if (c == '{') {
            skipComment();
            return nextToken(); // пропустить комментарий и перейти к следующему токену
        }
        // Идентификаторы, ключевые слова, or/and/not/ass
        if (isLetter(c)) {
            return readWord();
        }
        if (Character.isDigit(c)) {
            return readNumber();
        }




        // Разделители: (, ), ; , .
        if (SEPARATORS.contains(c)) {
            advance();
            return new Token(TokenType.SEPARATOR, Character.toString(c), startLine, startColumn);
        }

        // Операторы: =, <, >, <=, >=, <>, +, -, *, /
        if ("=<>+-*/".indexOf(c) != -1) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            advance();
            // Возможны двойные операторы: <=, >=, <>
            if ((sb.charAt(0) == '<' || sb.charAt(0) == '>') && currentChar() == '=') {
                sb.append(currentChar());
                advance();
            } else if (sb.charAt(0) == '<' && currentChar() == '>') {
                sb.append(currentChar());
                advance();
            }
            String op = sb.toString();
            if (OPERATORS.contains(op)) {
                return new Token(TokenType.OPERATOR, op, startLine, startColumn);
            }
        }


        // Неизвестный символ
        advance();
        return new Token(TokenType.OPERATOR, Character.toString(c), startLine, startColumn); // пока как OPERATOR
    }


    private Token readWord() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(currentChar())) {
            sb.append(currentChar());
            advance();
        }
        String word = sb.toString();
        if (OPERATORS.contains(word)) {
            return new Token(TokenType.OPERATOR, word, startLine, startColumn);
        }else if (word.equals("true") || word.equals("false")) {
            return new Token(TokenType.BOOLEAN, word, startLine, startColumn);
        }else if (KEYWORDS.contains(word)) {
            return new Token(TokenType.KEYWORD, word, startLine, startColumn);
        } else {
            return new Token(TokenType.IDENTIFIER, word, startLine, startColumn);
        }
    }
    private Token readNumber() {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();

        boolean hasDot = false;
        boolean hasExponent = false;

        while (Character.isDigit(currentChar())) {
            sb.append(currentChar());
            advance();
        }

        // Проверка на дробную часть
        if (currentChar() == '.') {
            hasDot = true;
            sb.append(currentChar());
            advance();

            while (Character.isDigit(currentChar())) {
                sb.append(currentChar());
                advance();
            }
        }

        // Проверка на порядок (e.g., E+3)
        if (currentChar() == 'e' || currentChar() == 'E') {
            hasExponent = true;
            sb.append(currentChar());
            advance();

            if (currentChar() == '+' || currentChar() == '-') {
                sb.append(currentChar());
                advance();
            }

            while (Character.isDigit(currentChar())) {
                sb.append(currentChar());
                advance();
            }
        }

        // Если это дробное число — конец
        if (hasDot || hasExponent) {
            return new Token(TokenType.FLOAT, sb.toString(), startLine, startColumn);
        }

        // Теперь проверим суффиксы систем счисления
        char suffix = Character.toLowerCase(currentChar());
        if (suffix == 'b' || suffix == 'o' || suffix == 'd' || suffix == 'h') {
            sb.append(currentChar());
            advance();
        }

        return new Token(TokenType.INTEGER, sb.toString(), startLine, startColumn);
    }

    private void skipComment() {
        advance(); // пропустить '{'
        while (currentChar() != '\0') {
            if (currentChar() == '}') {
                advance(); // пропустить '}'
                return;
            }
            advance(); // переход по символам
        }

        throw new RuntimeException("Unclosed comment at line " + line + ", column " + column);
    }



}
