import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class main2 {
    public static void main(String[] args) {
        Set<String> terminals = Set.of(
                "program", "var", "begin", "end", "int", "float", "bool",
                "if", "then", "else", "for", "to", "do", "while",
                "read", "write", "(", ")", ",", ";", ":", "id", "number", "ass"
        );

        Set<String> nonTerminals = Set.of(
                "Program", "Decls", "Type", "IdList", "IdListTail",
                "StmtList", "StmtListTail", "Stmt", "Compound", "CompoundTail",
                "Assign", "If", "ElsePart", "For", "While", "Input", "Output",
                "ExprList", "ExprListTail", "Expr"
        );
        List<GrammarNormalizer.Rule> rules = List.of(
                new GrammarNormalizer.Rule("Program", "program var Decls begin StmtList end"),
                new GrammarNormalizer.Rule("Decls", "Type IdList"),
                new GrammarNormalizer.Rule("Type", "int"),
                new GrammarNormalizer.Rule("Type", "float"),
                new GrammarNormalizer.Rule("Type", "bool"),
                new GrammarNormalizer.Rule("IdList", "id IdListTail"),
                new GrammarNormalizer.Rule("IdListTail", ", id IdListTail"),
                new GrammarNormalizer.Rule("IdListTail", "ε"),
                new GrammarNormalizer.Rule("StmtList", "Stmt StmtListTail"),
                new GrammarNormalizer.Rule("StmtListTail", "; Stmt StmtListTail"),
                new GrammarNormalizer.Rule("StmtListTail", "ε"),
                new GrammarNormalizer.Rule("Stmt", "Compound"),
                new GrammarNormalizer.Rule("Stmt", "Assign"),
                new GrammarNormalizer.Rule("Stmt", "If"),
                new GrammarNormalizer.Rule("Stmt", "For"),
                new GrammarNormalizer.Rule("Stmt", "While"),
                new GrammarNormalizer.Rule("Stmt", "Input"),
                new GrammarNormalizer.Rule("Stmt", "Output"),
                new GrammarNormalizer.Rule("Compound", "Stmt CompoundTail"),
                new GrammarNormalizer.Rule("CompoundTail", ": Stmt CompoundTail"),
                new GrammarNormalizer.Rule("CompoundTail", "ε"),
                new GrammarNormalizer.Rule("Assign", "id ass Expr"),
                new GrammarNormalizer.Rule("If", "if Expr then Stmt ElsePart"),
                new GrammarNormalizer.Rule("ElsePart", "else Stmt"),
                new GrammarNormalizer.Rule("ElsePart", "ε"),
                new GrammarNormalizer.Rule("For", "for Assign to Expr do Stmt"),
                new GrammarNormalizer.Rule("While", "while Expr do Stmt"),
                new GrammarNormalizer.Rule("Input", "read ( IdList )"),
                new GrammarNormalizer.Rule("Output", "write ( ExprList )"),
                new GrammarNormalizer.Rule("ExprList", "Expr ExprListTail"),
                new GrammarNormalizer.Rule("ExprListTail", ", Expr ExprListTail"),
                new GrammarNormalizer.Rule("ExprListTail", "ε"),
                new GrammarNormalizer.Rule("Expr", "id"),
                new GrammarNormalizer.Rule("Expr", "number")
        );

        String startSymbol = "Program";
        List<GrammarNormalizer.Rule> normalizedRules =
                GrammarNormalizer.normalize(startSymbol, nonTerminals, terminals, rules);

    }
}
