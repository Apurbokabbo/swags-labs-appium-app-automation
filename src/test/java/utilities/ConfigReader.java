package utilities;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigReader {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .ignoreIfMalformed()
            .load();

    /**
     * Priority: System Property (-DKEY=...) > OS Env Var > .env file > default
     */
    public static String get(String key, String defaultValue) {

        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }

        String envVar = System.getenv(key);
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        }

        String dotenvValue = dotenv.get(key);
        if (dotenvValue != null && !dotenvValue.isEmpty()) {
            return dotenvValue;
        }

        return defaultValue;
    }

    // ===== Environment Support =====

    /** Active environment: SIT or UAT (default UAT) */
    public static String env() {
        return get("ENV", "UAT").trim().toUpperCase();
    }

    /**
     * Reads the environment-prefixed key.
     * Example:
     * getEnvValue("BASE_URL") -> UAT_BASE_URL or SIT_BASE_URL
     */
    public static String getEnvValue(String key) {

        String fullKey = env() + "_" + key;
        String value = get(fullKey, null);

        if (value == null) {
            throw new IllegalStateException(
                    "Missing config key: " + fullKey +
                            " (ENV=" + env() + "). Check your .env file.");
        }

        return value.trim();
    }

    /** Same as above, but with a fallback instead of throwing. */
    public static String getEnvValue(String key, String defaultValue) {

        String value = get(env() + "_" + key, defaultValue);
        return value == null ? null : value.trim();
    }
}