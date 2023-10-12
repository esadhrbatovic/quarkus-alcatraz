package at.ac.fhcampuswien.alcatraz.shared.cli;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Menu<T> {

    private static final String NEW_LINE = "\n";

    private String title;

    private final List<MenuEntry<T>> menuEntries;

    public Menu(String title) {
        menuEntries = new ArrayList<>();
        this.setTitle(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void insert(String key, String text, T element) {
        menuEntries.add(new MenuEntry<>(key, text, element));
    }

    public T exec() {
        System.out.println(NEW_LINE + NEW_LINE + title);
        for (int i = 0; i < title.length(); i++)
            System.out.print("*");

        System.out.print(NEW_LINE);
        menuEntries.forEach(m -> System.out.println(m.key + ")\t" + m.text));
        System.out.print(NEW_LINE);

        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        do {
            String value = "\0";
            System.out.print(">");
            try {
                value = inReader.readLine();
                System.out.print(value);
            } catch (IOException e) {
                System.out.println("Error reading from cmd:" + e.toString());
                System.out.println(NEW_LINE);
                System.out.println("Exit with Ctrl C");
            }
            if (!value.isEmpty()) {
                for (MenuEntry<T> m : menuEntries)
                    if (m.key.trim().equalsIgnoreCase(value.trim()))
                        return m.element;

            }
            System.out.println("Wrong input");
        } while (true);
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
