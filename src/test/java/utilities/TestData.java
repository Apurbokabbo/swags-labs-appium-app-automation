package utilities;

/**
 * Central place for test data. Credentials come from .env / env vars / -D flags
 * (see ConfigReader) with the public Swag Labs demo values as fallback.
 */
public final class TestData {

    private TestData() {}

    public static String standardUser()  { return ConfigReader.get("STANDARD_USER", "standard_user"); }
    public static String lockedOutUser() { return ConfigReader.get("LOCKED_OUT_USER", "locked_out_user"); }
    public static String problemUser()   { return ConfigReader.get("PROBLEM_USER", "problem_user"); }
    public static String password()      { return ConfigReader.get("PASSWORD", "secret_sauce"); }

    // Checkout form
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME  = "Doe";
    public static final String ZIP_CODE   = "12345";
}
