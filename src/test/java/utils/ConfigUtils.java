package utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.platform.commons.util.StringUtils;

public class ConfigUtils {
    private static Dotenv dotenv;

    public static Dotenv getDotenv() {
        String currentProfile = System.getenv("testProfile");
        if (StringUtils.isBlank(currentProfile)) {
            currentProfile = "local";
        }
        if (dotenv == null) {
            dotenv = Dotenv.configure()
                    .directory("configs")
                    .filename(String.format("%s.env", currentProfile))
                    .load();
        }
        return dotenv;
    }
}