import java.util.*;
import java.util.stream.Collectors;

public class GrammarNormalizer {

    static class Rule {
        String left;
        List<String> right;

        Rule(String left, String rightSide) {
            this.left = left;
            this.right = Arrays.asList(rightSide.trim().split("\\s+"));
        }

        Rule(String left, List<String> right) {
            this.left = left;
            this.right = new ArrayList<>(right);
        }

        @Override
        public String toString() {
            return left + " → " + String.join(" ", right);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Rule)) return false;
            Rule other = (Rule) obj;
            return Objects.equals(left, other.left) && Objects.equals(right, other.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }

    private static int helperCounter = 0;
    private static final Map<List<String>, String> helperMap = new HashMap<>();

    public static List<Rule> normalize(String startSymbol,
                                       Set<String> nonTerminals,
                                       Set<String> terminals,
                                       List<Rule> rules) {

        if (!languageExists(startSymbol, nonTerminals, terminals, rules)) {
            System.out.println("Язык не содержит конечных слов. Разбор невозможен.");
            return Collections.emptyList();
        }

        rules = removeNonProductiveSymbols(nonTerminals, terminals, rules);
        rules = removeUnreachableSymbols(startSymbol, nonTerminals, terminals, rules);
        rules = removeEpsilonRules(nonTerminals, terminals, rules, startSymbol);
        rules = removeChainRules(nonTerminals, rules);
        rules = convertToCNF(new HashSet<>(nonTerminals), new HashSet<>(terminals), rules);

        return new ArrayList<>(new LinkedHashSet<>(rules));
    }

    public static void printRules(List<Rule> rules, Set<String> terminals) {
        Map<String, List<Rule>> grouped = new TreeMap<>();
        for (Rule rule : rules) {
            grouped.computeIfAbsent(rule.left, k -> new ArrayList<>()).add(rule);
        }

        System.out.println("\nНормализованные правила в Хомской НФ:");
        for (Map.Entry<String, List<Rule>> entry : grouped.entrySet()) {
            System.out.println(entry.getKey() + " →");
            for (Rule r : entry.getValue()) {
                System.out.println("    " + String.join(" ", r.right));
            }
        }
    }

    static boolean languageExists(String start, Set<String> nonTerminals, Set<String> terminals, List<Rule> rules) {
        Set<String> N = new HashSet<>();
        boolean changed;
        do {
            changed = false;
            for (Rule rule : rules) {
                if (!N.contains(rule.left)) {
                    boolean allInNOrTerm = rule.right.stream().allMatch(sym ->
                            terminals.contains(sym) || N.contains(sym) || sym.equals("ε"));
                    if (allInNOrTerm) {
                        N.add(rule.left);
                        changed = true;
                    }
                }
            }
        } while (changed);
        return N.contains(start);
    }

    static List<Rule> removeNonProductiveSymbols(Set<String> nonTerminals, Set<String> terminals, List<Rule> rules) {
        Set<String> productive = new HashSet<>();
        boolean changed;
        do {
            changed = false;
            for (Rule rule : rules) {
                if (!productive.contains(rule.left)) {
                    if (rule.right.contains("ε") || rule.right.stream().allMatch(sym ->
                            terminals.contains(sym) || productive.contains(sym))) {
                        productive.add(rule.left);
                        changed = true;
                    }
                }
            }
        } while (changed);
        return rules.stream()
                .filter(rule -> productive.contains(rule.left)
                        && rule.right.stream().allMatch(sym ->
                        terminals.contains(sym) || productive.contains(sym) || sym.equals("ε")))
                .collect(Collectors.toList());
    }

    static List<Rule> removeUnreachableSymbols(String startSymbol, Set<String> nonTerminals, Set<String> terminals, List<Rule> rules) {
        Set<String> reachable = new HashSet<>();
        reachable.add(startSymbol);
        boolean changed;
        do {
            changed = false;
            for (Rule rule : rules) {
                if (reachable.contains(rule.left)) {
                    for (String sym : rule.right) {
                        if ((nonTerminals.contains(sym) || terminals.contains(sym)) && reachable.add(sym)) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
        return rules.stream()
                .filter(rule -> reachable.contains(rule.left)
                        && rule.right.stream().allMatch(sym -> reachable.contains(sym) || sym.equals("ε")))
                .collect(Collectors.toList());
    }

    static List<Rule> removeEpsilonRules(Set<String> nonTerminals, Set<String> terminals, List<Rule> rules, String startSymbol) {
        Set<String> nullable = new HashSet<>();
        boolean changed;
        do {
            changed = false;
            for (Rule rule : rules) {
                if (!nullable.contains(rule.left)) {
                    if (rule.right.size() == 1 && rule.right.get(0).equals("ε")) {
                        nullable.add(rule.left);
                        changed = true;
                    } else if (rule.right.stream().allMatch(nullable::contains)) {
                        nullable.add(rule.left);
                        changed = true;
                    }
                }
            }
        } while (changed);

        List<Rule> noEpsilonRules = rules.stream()
                .filter(rule -> !(rule.right.size() == 1 && rule.right.get(0).equals("ε")))
                .collect(Collectors.toList());

        Set<Rule> updatedRules = new HashSet<>(noEpsilonRules);
        for (Rule rule : noEpsilonRules) {
            List<String> right = rule.right;
            List<Integer> nullableIndices = new ArrayList<>();
            for (int i = 0; i < right.size(); i++) {
                if (nullable.contains(right.get(i))) {
                    nullableIndices.add(i);
                }
            }
            int combinations = 1 << nullableIndices.size();
            for (int i = 1; i < combinations; i++) {
                List<String> newRight = new ArrayList<>(right);
                for (int j = 0; j < nullableIndices.size(); j++) {
                    if ((i & (1 << j)) != 0) {
                        newRight.set(nullableIndices.get(j), null);
                    }
                }
                List<String> filtered = newRight.stream().filter(Objects::nonNull).collect(Collectors.toList());
                if (!filtered.isEmpty()) {
                    updatedRules.add(new Rule(rule.left, filtered));
                }
            }
        }

        List<Rule> finalRules = new ArrayList<>(updatedRules);
        if (nullable.contains(startSymbol)) {
            String newStart = startSymbol + "'";
            finalRules.add(new Rule(newStart, List.of(startSymbol)));
            finalRules.add(new Rule(newStart, List.of("ε")));
            nonTerminals.add(newStart);
        }

        return finalRules;
    }

    static List<Rule> removeChainRules(Set<String> nonTerminals, List<Rule> rules) {
        Map<String, Set<String>> chainMap = new HashMap<>();
        for (String A : nonTerminals) {
            Set<String> reachable = new HashSet<>();
            reachable.add(A);
            boolean changed;
            do {
                changed = false;
                for (Rule rule : rules) {
                    if (reachable.contains(rule.left) &&
                            rule.right.size() == 1 &&
                            nonTerminals.contains(rule.right.get(0))) {
                        String B = rule.right.get(0);
                        if (reachable.add(B)) changed = true;
                    }
                }
            } while (changed);
            chainMap.put(A, reachable);
        }

        List<Rule> newRules = new ArrayList<>();
        for (String A : nonTerminals) {
            for (String B : chainMap.get(A)) {
                for (Rule rule : rules) {
                    if (rule.left.equals(B) &&
                            !(rule.right.size() == 1 && nonTerminals.contains(rule.right.get(0)))) {
                        newRules.add(new Rule(A, rule.right));
                    }
                }
            }
        }
        return newRules;
    }

    static List<Rule> convertToCNF(Set<String> nonTerminals, Set<String> terminals, List<Rule> rules) {
        List<Rule> cnfRules = new ArrayList<>();
        Map<String, String> terminalToNonTerminal = new HashMap<>();

        for (Rule rule : rules) {
            List<String> right = rule.right;
            if (right.size() == 1) {
                cnfRules.add(rule);
            } else {
                List<String> newRight = new ArrayList<>();
                for (String sym : right) {
                    if (terminals.contains(sym)) {
                        terminalToNonTerminal.putIfAbsent(sym, "T_" + sym.toUpperCase());
                        String nt = terminalToNonTerminal.get(sym);
                        newRight.add(nt);
                    } else {
                        newRight.add(sym);
                    }
                }

                for (Map.Entry<String, String> entry : terminalToNonTerminal.entrySet()) {
                    Rule tRule = new Rule(entry.getValue(), List.of(entry.getKey()));
                    if (!cnfRules.contains(tRule)) {
                        cnfRules.add(tRule);
                        nonTerminals.add(entry.getValue());
                    }
                }

                String currentLeft = rule.left;
                while (newRight.size() > 2) {
                    List<String> pair = newRight.subList(0, 2);
                    String newNonTerminal = helperMap.computeIfAbsent(new ArrayList<>(pair), k -> "X" + (++helperCounter));
                    cnfRules.add(new Rule(newNonTerminal, new ArrayList<>(pair)));
                    nonTerminals.add(newNonTerminal);
                    newRight = new ArrayList<>(newRight.subList(1, newRight.size()));
                    newRight.set(0, newNonTerminal);
                }
                cnfRules.add(new Rule(currentLeft, newRight));
            }
        }

        return cnfRules;
    }
}
