package at.ac.fhcampuswien.alcatraz.shared.cli;

class MenuEntry<T> {
    protected MenuEntry(String k, String t, T r) {
        key = k;
        text = t;
        element = r;
    }

    protected String key;
    protected String text;
    protected T element;
}
