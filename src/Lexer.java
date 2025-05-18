import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;

    private static final Set<Character> OPERATOR_START_CHARS = Set.of('=', '<', '>', '+', '-', '*', '/', '(', ')', ',', ':');

    private static final Map<String, TokenType> OPERATORS = Map.ofEntries(
            Map.entry("=", TokenType.EQ),
            Map.entry("<>", TokenType.NEQ),
            Map.entry("<", TokenType.LT),
            Map.entry("<=", TokenType.LE),
            Map.entry(">", TokenType.GT),
            Map.entry(">=", TokenType.GE),
            Map.entry("+", TokenType.PLUS),
            Map.entry("-", TokenType.MINUS),
            Map.entry("or", TokenType.OR),
            Map.entry("*", TokenType.MUL),
            Map.entry("/", TokenType.DIV),
            Map.entry("and", TokenType.AND),
            Map.entry("not", TokenType.NOT),
            Map.entry("ass", TokenType.UNKNOWN) // оператор присваивания "ass"
    );

    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "true", TokenType.TRUE,
            "false", TokenType.FALSE,
            "or", TokenType.OR,
            "and", TokenType.AND,
            "not", TokenType.NOT
    );

    public Lexer(String input) {
        this.input = input;
    }

    private char peek() {
        if (pos >= input.length()) return '\0';
        return input.charAt(pos);
    }

    private char next() {
        if (pos >= input.length()) return '\0';
        char ch = input.charAt(pos++);
        if (ch == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        return ch;
    }

    private void skipWhitespace() {
        while (true) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                next();
            } else if (c == '{') {
                skipComment();
            } else {
                break;
            }
        }
    }

    private void skipComment() {
        next(); // пропускаем '{'
        while (true) {
            char c = peek();
            if (c == '\0') {
                throw new RuntimeException("Unterminated comment at line " + line + ", col " + col);
            }
            if (c == '}') {
                next(); // пропускаем '}'
                break;
            } else {
                next();
            }
        }
    }

    // Новый потоковый метод — возвращает следующий токен или EOF
    public Token nextToken() {
        skipWhitespace();

        int startLine = line;
        int startCol = col;

        char c = peek();
        if (c == '\0') {
            return new Token(TokenType.EOF, "", startLine, startCol);
        }

        if (isLetter(c)) {
            return readIdentifierOrKeyword();
        } else if (isDigit(c) || c == '.') {
            return readNumber();
        } else if (c == '(') {
            next();
            return new Token(TokenType.LPAREN, "(", startLine, startCol);
        } else if (c == ')') {
            next();
            return new Token(TokenType.RPAREN, ")", startLine, startCol);
        } else if (c == ',') {
            next();
            return new Token(TokenType.COMMA, ",", startLine, startCol);
        } else if (c == ':') {
            next();
            return new Token(TokenType.COLON, ":", startLine, startCol);
        } else if (c == ';') {
            next();
            return new Token(TokenType.SEMICOLON, ";", startLine, startCol);
        } else if (isOperatorStart(c)) {
            return readOperator();
        } else {
            throw new RuntimeException("Unknown character '" + c + "' at line " + line + ", col " + col);
        }
    }

    private boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private Token readIdentifierOrKeyword() {
        int startLine = line;
        int startCol = col;
        StringBuilder sb = new StringBuilder();
        sb.append(next()); // первая буква

        while (true) {
            char c = peek();
            if (isLetter(c) || isDigit(c)) {
                sb.append(next());
            } else {
                break;
            }
        }

        String word = sb.toString();
        TokenType type = KEYWORDS.getOrDefault(word.toLowerCase(), TokenType.IDENTIFIER);
        return new Token(type, word, startLine, startCol);
    }

    private Token readNumber() {
        int startLine = line;
        int startCol = col;
        StringBuilder sb = new StringBuilder();

        boolean hasDot = false;
        boolean hasExp = false;

        while (true) {
            char c = peek();
            if (isDigit(c)) {
                sb.append(next());
            } else if (c == '.' && !hasDot) {
                hasDot = true;
                sb.append(next());
            } else if ((c == 'E' || c == 'e') && !hasExp) {
                hasExp = true;
                sb.append(next());
                char sign = peek();
                if (sign == '+' || sign == '-') {
                    sb.append(next());
                }
            } else {
                break;
            }
        }

        char suffix = peek();
        if (suffix == 'B' || suffix == 'b' ||
                suffix == 'O' || suffix == 'o' ||
                suffix == 'D' || suffix == 'd' ||
                suffix == 'H' || suffix == 'h') {
            sb.append(next());
            return new Token(TokenType.INTEGER, sb.toString(), startLine, startCol);
        } else if (hasDot || hasExp) {
            return new Token(TokenType.FLOAT, sb.toString(), startLine, startCol);
        } else {
            return new Token(TokenType.INTEGER, sb.toString(), startLine, startCol);
        }
    }

    private boolean isOperatorStart(char c) {
        return OPERATOR_START_CHARS.contains(c);
    }

    private Token readOperator() {
        int startLine = line;
        int startCol = col;

        // Максимум 3 символа
        int maxLen = Math.min(3, input.length() - pos);

        for (int len = maxLen; len > 0; len--) {
            String op = input.substring(pos, pos + len);
            String opLower = op.toLowerCase();
            if (OPERATORS.containsKey(opLower)) {
                for (int i = 0; i < len; i++) next();
                return new Token(OPERATORS.get(opLower), op, startLine, startCol);
            }
        }

        char c = next();
        String op = String.valueOf(c);
        if (OPERATORS.containsKey(op)) {
            return new Token(OPERATORS.get(op), op, startLine, startCol);
        }

        throw new RuntimeException("Unknown operator starting at line " + startLine + ", col " + startCol);
    }
}
