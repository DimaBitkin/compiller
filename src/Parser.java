import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        if (pos >= tokens.size()) return new Token(TokenType.EOF, "", -1, -1);
        return tokens.get(pos);
    }

    private Token next() {
        return tokens.get(pos++);
    }

    private boolean match(TokenType expected) {
        if (peek().getType() == expected) {
            next();
            return true;
        }
        return false;
    }

    private void expect(TokenType expected) {
        if (!match(expected)) {
            Token current = peek();
            throw new RuntimeException("Ожидался " + expected + ", но найдено: " + current.getType() +
                    " в строке " + current.getLine() + ", колонке " + current.getColumn());
        }
    }

    private void error(String msg) {
        Token current = peek();
        throw new RuntimeException(msg + " в строке " + current.getLine() + ", колонке " + current.getColumn());
    }

    public void parseProgram() {
        expect(TokenType.PROGRAM);
        expect(TokenType.IDENTIFIER); // имя программы
        expect(TokenType.VAR);
        parseDescription();
        expect(TokenType.BEGIN);
        parseCompoundOperator();
        expect(TokenType.END);
        expect(TokenType.DOT);
        System.out.println("Программа успешно разобрана.");
    }

    private void parseDescription() {
        while (isType(peek().getType())) {
            parseType();
            expect(TokenType.IDENTIFIER);
            while (match(TokenType.COMMA)) {
                expect(TokenType.IDENTIFIER);
            }
        }
    }

    private void parseType() {
        if (!(match(TokenType.INT) ||
                match(TokenType.FLOAT_TYPE) ||
                match(TokenType.BOOL))) {
            error("Ожидался тип (int|float|bool), найдено: " + peek().getValue());
        }
    }

    private void parseCompoundOperator() {
        parseOperator();
        while (match(TokenType.SEMICOLON)) {
            parseOperator();
        }
    }

    private void parseOperator() {
        Token current = peek();
        switch (current.getType()) {
            case IDENTIFIER:
                parseAssignment();
                break;
            case IF:
                parseIf();
                break;
            case FOR:
                parseFor();
                break;
            case WHILE:
                parseWhile();
                break;
            case READ:
                parseRead();
                break;
            case WRITE:
                parseWrite();
                break;
            case BEGIN:
                parseCompound();
                break;
            default:
                error("Ожидался оператор, найдено: " + current.getType());
        }
    }

    private void parseCompound() {
        expect(TokenType.BEGIN);
        parseCompoundOperator();
        expect(TokenType.END);
    }

    private void parseAssignment() {
        expect(TokenType.IDENTIFIER);
        expect(TokenType.ASSIGN);
        parseExpression();
    }

    private void parseIf() {
        expect(TokenType.IF);
        parseExpression();
        expect(TokenType.THEN);
        parseOperator();
        if (match(TokenType.ELSE)) {
            parseOperator();
        }
    }

    private void parseFor() {
        expect(TokenType.FOR);
        parseAssignment();
        expect(TokenType.TO);
        parseExpression();
        expect(TokenType.DO);
        parseOperator();
    }

    private void parseWhile() {
        expect(TokenType.WHILE);
        parseExpression();
        expect(TokenType.DO);
        parseOperator();
    }

    private void parseRead() {
        expect(TokenType.READ);
        expect(TokenType.LPAREN);
        expect(TokenType.IDENTIFIER);
        while (match(TokenType.COMMA)) {
            expect(TokenType.IDENTIFIER);
        }
        expect(TokenType.RPAREN);
    }

    private void parseWrite() {
        expect(TokenType.WRITE);
        expect(TokenType.LPAREN);
        parseExpression();
        while (match(TokenType.COMMA)) {
            parseExpression();
        }
        expect(TokenType.RPAREN);
    }

    private void parseExpression() {
        parseOperand();
        while (isRelationOperator(peek().getType())) {
            next(); // оператор отношения
            parseOperand();
        }
    }

    private void parseOperand() {
        parseTerm();
        while (isAdditiveOperator(peek().getType())) {
            next(); // плюс, минус, or
            parseTerm();
        }
    }

    private void parseTerm() {
        parseFactor();
        while (isMultiplicativeOperator(peek().getType())) {
            next(); // умножение, деление, and
            parseFactor();
        }
    }

    private void parseFactor() {
        Token current = peek();
        switch (current.getType()) {
            case IDENTIFIER:
            case INTEGER:
            case FLOAT:
            case TRUE:
            case FALSE:
                next();
                break;
            case NOT:
            case MINUS:
                next();
                parseFactor();
                break;
            case LPAREN:
                next();
                parseExpression();
                expect(TokenType.RPAREN);
                break;
            default:
                error("Ожидался множитель, найдено: " + current.getType());
        }
    }

    private boolean isRelationOperator(TokenType type) {
        return switch (type) {
            case EQ, NEQ, LT, LE, GT, GE -> true;
            default -> false;
        };
    }

    private boolean isAdditiveOperator(TokenType type) {
        return switch (type) {
            case PLUS, MINUS, OR -> true;
            default -> false;
        };
    }

    private boolean isMultiplicativeOperator(TokenType type) {
        return switch (type) {
            case MUL, DIV, AND -> true;
            default -> false;
        };
    }

    private boolean isType(TokenType type) {
        return type == TokenType.INT || type == TokenType.FLOAT_TYPE || type == TokenType.BOOL;
    }
}