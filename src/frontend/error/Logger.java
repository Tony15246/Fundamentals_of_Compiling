package frontend.error;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

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
        errors.sort(Comparator.comparingInt(a -> a.line));
        for (Error error : errors) {
            fw.write(error.toString() + "\n");
        }
    }
}
