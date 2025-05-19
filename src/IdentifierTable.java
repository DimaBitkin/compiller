import java.util.*;

public class IdentifierTable {
    private final Map<String, Integer> table = new HashMap<>();
    private int nextId = 1;

    public int add(String name) {
        if (!table.containsKey(name)) {
            table.put(name, nextId++);
        }
        return table.get(name);
    }

    public Integer getId(String name) {
        return table.get(name);
    }

    public Set<String> getIdentifiers() {
        return table.keySet();
    }

    public void print() {
        System.out.println("Идентификаторы:");
        for (Map.Entry<String, Integer> entry : table.entrySet()) {
            System.out.printf("%d\t%s\n", entry.getValue(), entry.getKey());
        }
    }
    public void printTable() {
        System.out.println("Таблица идентификаторов:");
        System.out.printf("%-4s %-12s%n", "№", "Имя");
        for (Map.Entry<String, Integer> entry : table.entrySet()) {
            System.out.printf("%-4d %-12s%n", entry.getValue(), entry.getKey());
        }
    }

}
