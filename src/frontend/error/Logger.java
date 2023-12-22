package frontend.error;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public class Logger {
    private static Logger logger;
    private ArrayList<Error> errors = new ArrayList<>();

    public static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public void printErrors(FileWriter fw) throws IOException {
        TreeSet<Error> sortedErrors = new TreeSet<>(Comparator.comparingInt(a -> a.line));
        sortedErrors.addAll(errors);
        for (Error error : sortedErrors) {
            fw.write(error.toString() + "\n");
        }
        fw.flush();
    }
}
