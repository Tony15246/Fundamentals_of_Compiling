package frontend.error;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Logger {
    private static Logger logger;
    private final FileWriter fw;
    private ArrayList<Error> errors = new ArrayList<>();

    public static Logger GetLogger(FileWriter fw) {
        if (logger == null) {
            logger = new Logger(fw);
        }
        return logger;
    }

    private Logger(FileWriter fw) {
        this.fw = fw;
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public void printErrors() throws IOException {
        for (Error error : errors) {
            fw.write(error.toString() + "\n");
        }
    }
}
