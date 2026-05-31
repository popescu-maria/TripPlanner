package audit;

import exceptions.DataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {

    private static final String FILE_NAME = "audit_log.csv";
    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static AuditService instance;
    private final Path filePath;

    private AuditService() {
        this.filePath = Paths.get(FILE_NAME);
        try {
            if (!Files.exists(filePath)) {
                Files.writeString(filePath, "action_name,timestamp\n",
                        StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new DataAccessException("Could not initialize audit file: " + e.getMessage());
        }
    }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public synchronized void log(String actionName) {
        String line = actionName + "," + LocalDateTime.now().format(TS) + "\n";
        try {
            Files.writeString(filePath, line,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Audit write failed: " + e.getMessage());
        }
    }

    public Path getFilePath() {
        return filePath.toAbsolutePath();
    }
}