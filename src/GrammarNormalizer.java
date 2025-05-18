import java.util.*;
import java.util.stream.Collectors;

public class GrammarNormalizer {

    static class Rule {
        String left;
        List<String> right;

        Rule(String left, String rightSide) {
            this.left = left;
            this.right = Arrays.asList(rightSide.trim().split(" "));
        }

        @Override
        public String toString() {
            return left + " → " + String.join(" ", right);
        }
    }

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

        System.out.println("\nИтоговая нормализованная грамматика:");
        rules.forEach(System.out::println);
        return rules;
    }

    static boolean languageExists(String start, Set<String> nonTerminals, Set<String> terminals, List<Rule> rules) {
        Set<String> N = new HashSet<>();
        boolean changed;
        do {
            changed = false;
            for (Rule rule : rules) {
                if (!N.contains(rule.left)) {
                    boolean allInNOrTerm = rule.right.stream().allMatch(sym -> terminals.contains(sym) || N.contains(sym) || sym.equals("ε"));
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
                    if (rule.right.contains("ε") || rule.right.stream().allMatch(sym -> terminals.contains(sym) || productive.contains(sym))) {
                        productive.add(rule.left);
                        changed = true;
                    }
                }
            }
        } while (changed);
        return rules.stream()
                .filter(rule -> productive.contains(rule.left)
                        && rule.right.stream().allMatch(sym -> terminals.contains(sym) || productive.contains(sym) || sym.equals("ε")))
                .toList();
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
                        if ((nonTerminals.contains(sym) || terminals.contains(sym)) && !reachable.contains(sym)) {
                            reachable.add(sym);
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
        return rules.stream()
                .filter(rule -> reachable.contains(rule.left)
                        && rule.right.stream().allMatch(sym -> reachable.contains(sym) || sym.equals("ε")))
                .toList();
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
                    } else if (rule.right.stream().allMatch(s -> nullable.contains(s))) {
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
                    updatedRules.add(new Rule(rule.left, String.join(" ", filtered)));
                }
            }
        }

        List<Rule> finalRules = new ArrayList<>(updatedRules);
        if (nullable.contains(startSymbol)) {
            String newStart = startSymbol + "'";
            finalRules.add(new Rule(newStart, startSymbol));
            finalRules.add(new Rule(newStart, "ε"));
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
                Set<String> toAdd = new HashSet<>();
                for (Rule rule : rules) {
                    if (reachable.contains(rule.left) && rule.right.size() == 1 && nonTerminals.contains(rule.right.get(0))) {
                        String B = rule.right.get(0);
                        if (!reachable.contains(B)) {
                            toAdd.add(B);
                            changed = true;
                        }
                    }
                }
                reachable.addAll(toAdd);
            } while (changed);
            chainMap.put(A, reachable);
        }

        List<Rule> newRules = new ArrayList<>();
        for (String A : nonTerminals) {
            for (String B : chainMap.get(A)) {
                for (Rule rule : rules) {
                    if (rule.left.equals(B)) {
                        if (!(rule.right.size() == 1 && nonTerminals.contains(rule.right.get(0)))) {
                            newRules.add(new Rule(A, String.join(" ", rule.right)));
                        }
                    }
                }
            }
        }
        return newRules;
    }
}
