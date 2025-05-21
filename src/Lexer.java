import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;
    private final LexemeTable lexemeTable = new LexemeTable();
    private final IdentifierTable identifierTable = new IdentifierTable();

    //Набор символов, с которых могут начинаться операторы,
    private static final Set<Character> OPERATOR_START_CHARS = Set.of('=', '<', '>', '+', '-', '*', '/', '(', ')', ',', ':');

    //Карта строковых представлений операторов
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
            Map.entry("ass", TokenType.ASSIGN) // оператор присваивания "ass"
    );
    //Карта ключевых слов языка
    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("true", TokenType.TRUE),
            Map.entry("false", TokenType.FALSE),
            Map.entry("or", TokenType.OR),
            Map.entry("and", TokenType.AND),
            Map.entry("not", TokenType.NOT),
            Map.entry("ass", TokenType.ASSIGN),

            Map.entry("program", TokenType.PROGRAM),
            Map.entry("var", TokenType.VAR),
            Map.entry("begin", TokenType.BEGIN),
            Map.entry("end", TokenType.END),

            Map.entry("int", TokenType.INT),
            Map.entry("float", TokenType.FLOAT_TYPE), // если FLOAT_TYPE в enum
            Map.entry("bool", TokenType.BOOL),

            Map.entry("if", TokenType.IF),
            Map.entry("then", TokenType.THEN),
            Map.entry("else", TokenType.ELSE),

            Map.entry("for", TokenType.FOR),
            Map.entry("to", TokenType.TO),
            Map.entry("do", TokenType.DO),

            Map.entry("while", TokenType.WHILE),

            Map.entry("read", TokenType.READ),
            Map.entry("write", TokenType.WRITE)
    );
    //Набор состояний
    private enum State {
        START,
        IDENTIFIER_OR_KEYWORD,
        NUMBER,
        OPERATOR,
        COMMENT,
        ERROR
    }
    //Конструктор
    public Lexer(String input) {
        this.input = input;
    }
    //Возвращает текущий символ (без продвижения позиции
    private char peek() {
        if (pos >= input.length()) return '\0';
        return input.charAt(pos);
    }
    //Возвращает текущий символ и сдвигает позицию вперёд
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
    //Пропускает пробелы, табы, переводы строк и комментарии { ... }
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
    //Пропускает символы до закрывающей фигурной скобки }. Бросает исключение, если комментарий не закрыт.
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
    //Проверяют, является ли символ буквой
    private boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
    //Проверяют, является ли символ цифрой
    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    //Считывает идентификатор (или ключевое слово) из букв и цифр.
    //Возвращает токен типа IDENTIFIER если не найден в KEYWORDS или соответствующий ключевому слову
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
    //Возвращает следующий символ после текущего, не сдвигая позицию
    private char peekNext() {
        if (pos + 1 >= input.length()) return '\0';
        return input.charAt(pos + 1);
    }
    //Считывает целое или вещественное число
    //Возвращает TokenType.INTEGER или TokenType.FLOAT
    private Token readNumber() {
        int startLine = line;
        int startCol = col;
        StringBuilder sb = new StringBuilder();

        boolean hasDot = false;
        boolean hasExp = false;

        char first = peek();

        // Если точка не сопровождается цифрой, это не число
        if (first == '.' && !isDigit(peekNext())) {
            // Не число — это отдельный символ '.'
            next(); // продвигаемся вперёд
            return new Token(TokenType.DOT, ".", startLine, startCol);
        }

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

    //Проверяет, начинается ли оператор с символа char c
    private boolean isOperatorStart(char c) {
        return OPERATOR_START_CHARS.contains(c);
    }

    //Пытается найти оператор длиной от 3 до 1 символа, начиная с текущей позиции.
    //Если найден — возвращает соответствующий токен.
    //Если неизвестный оператор — бросает ошибку.
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




    //Преобразует символ (, ), ; и т.п. в соответствующий TokenType
    private TokenType mapSymbolToTokenType(char c) {
        switch (c) {
            case '(': return TokenType.LPAREN;
            case ')': return TokenType.RPAREN;
            case ',': return TokenType.COMMA;
            case ':': return TokenType.COLON;
            case ';': return TokenType.SEMICOLON;
            default: return TokenType.UNKNOWN;
        }
    }
    //Печатает содержимое таблиц лексем и идентификаторов.
    public void printTables() {
        lexemeTable.print();
        System.out.println();
        identifierTable.print();
    }
    //Возвращают ссылку на таблицу лексем
    public LexemeTable getLexemeTable() {
        return lexemeTable;
    }
    //Возвращают ссылку на таблицу идентификаторов
    public IdentifierTable getIdentifierTable() {
        return identifierTable;
    }
    //Основной метод: полностью обходит входной текст, генерирует все токены и заполняет таблицы.

    public void tokenizeAll() {
        State state = State.START;
        StringBuilder buffer = new StringBuilder();
        int tokenStartLine = line;
        int tokenStartCol = col;

        while (true) {
            skipWhitespace();
            char c = peek();

            switch (state) {
                case START:
                    skipWhitespace();
                    c = peek();
                    buffer.setLength(0);
                    tokenStartLine = line;
                    tokenStartCol = col;

                    if (c == '\0') {
                        lexemeTable.add(new Token(TokenType.EOF, "", line, col));
                        return;
                    } else if (isLetter(c)) {
                        state = State.IDENTIFIER_OR_KEYWORD;
                    } else if (isDigit(c) || c == '.') {
                        state = State.NUMBER;
                    }else if (c == '(' || c == ')' || c == ',' || c == ':' || c == ';') {
                        Token token = new Token(mapSymbolToTokenType(c), String.valueOf(c), line, col);
                        next();
                        lexemeTable.add(token);
                        state = State.START;
                        continue;
                    }else if (isOperatorStart(c)) {
                        state = State.OPERATOR;
                    } else {
                        throw new RuntimeException("Unknown character '" + c + "' at line " + line + ", col " + col);
                    }
                    break;

                case IDENTIFIER_OR_KEYWORD:
                    while (isLetter(peek()) || isDigit(peek())) {
                        buffer.append(next());
                    }
                    String word = buffer.toString();
                    TokenType type = KEYWORDS.getOrDefault(word.toLowerCase(), TokenType.IDENTIFIER);
                    lexemeTable.add(new Token(type, word, tokenStartLine, tokenStartCol));
                    if (type == TokenType.IDENTIFIER) identifierTable.add(word);
                    state = State.START;
                    break;

                case NUMBER:
                    boolean hasDot = false, hasExp = false;
                    if (peek() == '.' && !isDigit(peekNext())) {
                        buffer.append(next());
                        lexemeTable.add(new Token(TokenType.DOT, buffer.toString(), tokenStartLine, tokenStartCol));
                        state = State.START;
                        break;
                    }

                    while (true) {
                        c = peek();
                        if (isDigit(c)) {
                            buffer.append(next());
                        } else if (c == '.' && !hasDot) {
                            hasDot = true;
                            buffer.append(next());
                        } else if ((c == 'e' || c == 'E') && !hasExp) {
                            hasExp = true;
                            buffer.append(next());
                            char sign = peek();
                            if (sign == '+' || sign == '-') buffer.append(next());
                        } else {
                            break;
                        }
                    }

                    char suffix = peek();
                    if ("bBoOdDhH".indexOf(suffix) >= 0) {
                        buffer.append(next());
                        lexemeTable.add(new Token(TokenType.INTEGER, buffer.toString(), tokenStartLine, tokenStartCol));
                    } else if (hasDot || hasExp) {
                        lexemeTable.add(new Token(TokenType.FLOAT, buffer.toString(), tokenStartLine, tokenStartCol));
                    } else {
                        lexemeTable.add(new Token(TokenType.INTEGER, buffer.toString(), tokenStartLine, tokenStartCol));
                    }
                    state = State.START;
                    break;

                case OPERATOR:
                    int maxLen = Math.min(3, input.length() - pos);
                    boolean matched = false;
                    for (int len = maxLen; len > 0; len--) {
                        String op = input.substring(pos, pos + len).toLowerCase();
                        System.out.println((op));
                        if (OPERATORS.containsKey(op)) {

                            for (int i = 0; i < len; i++) buffer.append(next());
                            lexemeTable.add(new Token(OPERATORS.get(op), buffer.toString(), tokenStartLine, tokenStartCol));
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        //System.out.println("Unknown operator at line " + line + ", col " + col);
                        printTables();
                         throw new RuntimeException("Unknown operator at line " + line + ", col " + col);
                    }
                    state = State.START;
                    break;
            }
        }
    }

}
