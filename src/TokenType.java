public enum TokenType {
    // Операции отношения
    EQ, NEQ, LT, LE, GT, GE,

    // Операции сложения
    PLUS, MINUS, OR,

    // Операции умножения
    MUL, DIV, AND,

    // Унарная операция
    NOT,

    // Логические литералы
    TRUE, FALSE,

    // Числа
    INTEGER, FLOAT,

    // Идентификаторы
    IDENTIFIER,

    // Разделители
    LPAREN, RPAREN, COLON, SEMICOLON, COMMA,

    // Прочее
    UNKNOWN,
    EOF
}
