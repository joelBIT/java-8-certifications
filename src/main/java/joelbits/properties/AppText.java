package joelbits.properties;

import java.util.ListResourceBundle;

/**
 * By using this class as a Resource Bundle it is possible to have
 * other value types than just String, even though this is not necessary in this
 * particular application.
 */
public class AppText extends ListResourceBundle {
    public static final String BUNDLE_PROPERTIES = "AppText";
    public static final String BUNDLE_JAVA = "joelbits.properties." + BUNDLE_PROPERTIES;

    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"start", "Welcome to fileConverter desktop version. Type \"-help\" for a list of available commands."},
                {"input", "Enter input: "},
                {"exiting", "Exiting..."},
                {"parse_error", "Could not parse input: "},
                {"convert_desc", "The desired file to be converted."}
        };
    }
}
