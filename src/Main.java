import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String code = "program Test ; float x , y; int q; begin x ass 2.51 ; y ass x + 1; WHILE x do y ass y * 2; end.";
        Lexer lexer = new Lexer(code);
        lexer.tokenizeAll();
        lexer.printTables();
//        Parser parser = new Parser(lexer.getLexemeTable().getLexemes());
//        parser.parseProgram();

//        Set<String> nonTerminals = Set.of("Q", "A", "B", "C", "D");
//        Set<String> terminals = Set.of("a", "b", "c", "d");
//
//        List<GrammarNormalizer.Rule> rules = List.of(
//                new GrammarNormalizer.Rule("Q", "a c A"),
//                new GrammarNormalizer.Rule("Q", "a c B"),
//                new GrammarNormalizer.Rule("Q", "ε"),
//
//                new GrammarNormalizer.Rule("B", "A"),
//                new GrammarNormalizer.Rule("B", "C b"),
//                new GrammarNormalizer.Rule("B", "ε"),
//
//                new GrammarNormalizer.Rule("A", "A a"),
//                new GrammarNormalizer.Rule("A", "A b"),
//                new GrammarNormalizer.Rule("A", "a"),
//
//                new GrammarNormalizer.Rule("C", "d C c"),
//
//                new GrammarNormalizer.Rule("D", "d c")
//        );
//
//        String startSymbol = "Q";
//
//        List<GrammarNormalizer.Rule> normalized = GrammarNormalizer.normalize(startSymbol,
//                new HashSet<>(nonTerminals),
//                new HashSet<>(terminals),
//                new ArrayList<>(rules)
//        );
    }
}