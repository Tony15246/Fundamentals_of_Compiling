package frontend.error;

public class Error {
    public final int line;
    public final String type;

    public Error(int line, String type) {
        this.line = line;
        this.type = type;
    }

    @Override
    public String toString() {
        return line + " " + type;
    }
}
