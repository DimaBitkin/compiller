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
    LPAREN, RPAREN, COLON, SEMICOLON, COMMA,DOT,

    // Ключевые слова
    PROGRAM,
    VAR,
    BEGIN,
    END,
    INT,
    FLOAT_TYPE,  // Чтобы не путать с FLOAT (числом), можно назвать иначе
    BOOL,
    ASSIGN,

    IF,
    THEN,
    ELSE,
    FOR,
    TO,
    DO,
    WHILE,
    READ,
    WRITE,
    // Прочее
    UNKNOWN,
    EOF,


}
