package shogun.sdk;

import org.slf4j.Logger;
import shogun.logging.LoggerFactory;

import java.io.*;

public class SDKLauncher {
    private final static Logger logger = LoggerFactory.getLogger();

    /**
     * Run specified command
     *
     * @param command Command to run
     * @return output
     */
    public static String exec(String... command) {
        try {
            File tempFile = File.createTempFile("sdk", "log");
            command[command.length - 1] = command[command.length - 1] + " >" + tempFile.getAbsolutePath() + " 2>&1";
            logger.debug("Command to be executed: {}", (Object) command);
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            OutputStream outputStream = process.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            // say yes
            printWriter.write("n\n");
            printWriter.flush();
            process.waitFor();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new FileInputStream(tempFile).transferTo(baos);
            return trimANSIEscapeCodes(baos.toString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * trim ANSI escape codes to decorate terminal characters
     *
     * @param escaped string with ANSI escape sequences
     * @return string without ANSI escape sequences
     */
    static String trimANSIEscapeCodes(String escaped) {
        return escaped.replaceAll("\u001B\\[[0-9;]*m", "");
    }
}
