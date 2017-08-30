package io.temperley.cmd;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 *
 */
public class Arguments {

    /**
     * Command-line arguments. Elements are set to
     * {@code null} after they have been processed.
     */
    private final String[] arguments;


    /**
     * The locale. Locale will be fetch from the {@code "-locale"}
     * argument, if presents. Otherwise, the default locale will be used.
     */
    public final Locale locale;

    /**
     * The encoding, or {@code null} for the platform default.
     */
    private final String encoding;

    /**
     * Constructs a set of arguments.
     *
     * @param args Command line arguments. Arguments {@code "-encoding"} and {@code "-locale"}
     *             will be automatically parsed.
     */
    public Arguments(final String[] args) {
        this.arguments = args.clone();
        this.locale = getLocale(getOptionalString("-locale"));
        this.encoding = getOptionalString("-encoding");
        PrintWriter out = null;
        Exception error = null;
        if (encoding != null) try {
            out = new PrintWriter(new OutputStreamWriter(System.out, encoding), true);
        } catch (IOException exception) {
            error = exception;
        }
        if (out == null) {
//            out = getPrintWriter(System.out);
        }
        if (error != null) {
            illegalArgument(error);
        }
    }

    /**
     * Returns the specified locale.
     *
     * @param locale The programmatic locale string (e.g. "fr_CA").
     * @return The locale, or the default one if {@code locale} was null.
     * @throws IllegalArgumentException if the locale string is invalid.
     */
    private Locale getLocale(final String locale) throws IllegalArgumentException {
        if (locale != null) {
            final String[] s = Pattern.compile("_").split(locale);
            switch (s.length) {
                case 1:
                    return new Locale(s[0]);
                case 2:
                    return new Locale(s[0], s[1]);
                case 3:
                    return new Locale(s[0], s[1], s[2]);
                default:
                    illegalArgument(new IllegalArgumentException(String.valueOf(s[0])));
            }
        }
        return Locale.getDefault();
    }

    /**
     * Returns an optional string value from the command line. This method should be called
     * exactly once for each parameter. Second invocation for the same parameter will returns
     * {@code null}, unless the same parameter appears many times on the command line.
     * <p>
     * Paramater may be instructions like "-encoding cp850" or "-encoding=cp850".
     * Both forms (with or without "=") are accepted. Spaces around the '=' character,
     * if any, are ignored.
     *
     * @param name The parameter name (e.g. "-encoding"). Name are case-insensitive.
     * @return The parameter value, of {@code null} if there is no parameter
     * given for the specified name.
     */
    public String getOptionalString(final String name) {
        for (int i = 0; i < arguments.length; i++) {
            String arg = arguments[i];
            if (arg != null) {
                arg = arg.trim();
                String value = "";
                int split = arg.indexOf('=');
                if (split >= 0) {
                    value = arg.substring(split + 1).trim();
                    arg = arg.substring(0, split).trim();
                }
                if (arg.equalsIgnoreCase(name)) {
                    arguments[i] = null;
                    if (value.length() != 0) {
                        return value;
                    }
                    while (++i < arguments.length) {
                        value = arguments[i];
                        arguments[i] = null;
                        if (value == null) {
                            break;
                        }
                        value = value.trim();
                        if (split >= 0) {
                            return value;
                        }
                        if (!value.equals("=")) {
                            return value.startsWith("=") ? value.substring(1).trim() : value;
                        }
                        split = 0;
                    }
                    illegalArgument(new IllegalArgumentException());
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Returns an required string value from the command line. This method
     * works like {@link #getOptionalString}, except that it will invokes
     * {@link #illegalArgument} if the specified parameter was not given
     * on the command line.
     *
     * @param name The parameter name. Name are case-insensitive.
     * @return The parameter value.
     */
    public String getRequiredString(final String name) {
        final String value = getOptionalString(name);
        if (value == null) {
            illegalArgument(new IllegalArgumentException(name));
        }
        return value;
    }

    /**
     * Returns an optional integer value from the command line. Numbers are parsed as
     * of the {@link Integer#parseInt(String)} method,  which means that the parsing
     * is locale-insensitive. Locale insensitive parsing is required in order to use
     * arguments in portable scripts.
     *
     * @param name The parameter name. Name are case-insensitive.
     * @return The parameter value, of {@code null} if there is no parameter
     * given for the specified name.
     */
    public Integer getOptionalInteger(final String name) {
        final String value = getOptionalString(name);
        if (value != null) try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            illegalArgument(exception);
        }
        return null;
    }

    /**
     * Returns a required integer value from the command line. Numbers are parsed as
     * of the {@link Integer#parseInt(String)} method,  which means that the parsing
     * is locale-insensitive. Locale insensitive parsing is required in order to use
     * arguments in portable scripts.
     *
     * @param name The parameter name. Name are case-insensitive.
     * @return The parameter value.
     */
    public int getRequiredInteger(final String name) {
        final String value = getRequiredString(name);
        if (value != null) try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            illegalArgument(exception);
        }
        return 0;
    }

    /**
     * Returns an optional floating-point value from the command line. Numbers are parsed
     * as of the {@link Double#parseDouble(String)} method,  which means that the parsing
     * is locale-insensitive. Locale insensitive parsing is required in order to use
     * arguments in portable scripts.
     *
     * @param name The parameter name. Name are case-insensitive.
     * @return The parameter value, of {@code null} if there is no parameter
     * given for the specified name.
     */
    public Double getOptionalDouble(final String name) {
        final String value = getOptionalString(name);
        if (value != null) try {
            return Double.valueOf(value);
        } catch (NumberFormatException exception) {
            illegalArgument(exception);
        }
        return null;
    }

    /**
     * Returns a required floating-point value from the command line. Numbers are parsed
     * as of the {@link Double#parseDouble(String)} method, which means that the parsing
     * is locale-insensitive. Locale insensitive parsing is required in order to use
     * arguments in portable scripts.
     *
     * @param name The parameter name. Name are case-insensitive.
     * @return The parameter value.
     */
    public double getRequiredDouble(final String name) {
        final String value = getRequiredString(name);
        if (value != null) try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            illegalArgument(exception);
        }
        return Double.NaN;
    }

    /**
     * Returns an optional boolean value from the command line.
     * The value, if defined, must be "true" or "false".
     *
     * @param name The parameter name. Name are case-insensitive.
     * @return The parameter value, of {@code null} if there is no parameter
     * given for the specified name.
     */
    public Boolean getOptionalBoolean(final String name) {
        final String value = getOptionalString(name);
        if (value != null) {
            if (value.equalsIgnoreCase("true")) return Boolean.TRUE;
            if (value.equalsIgnoreCase("false")) return Boolean.FALSE;
            illegalArgument(new IllegalArgumentException(value));
        }
        return null;
    }

    /**
     * Returns a required boolean value from the command line.
     * The value must be "true" or "false".
     *
     * @param name The parameter name. Name are case-insensitive.
     * @return The parameter value.
     */
    public boolean getRequiredBoolean(final String name) {
        final String value = getRequiredString(name);
        if (value != null) {
            if (value.equalsIgnoreCase("true")) return true;
            if (value.equalsIgnoreCase("false")) return false;
            illegalArgument(new IllegalArgumentException(value));
        }
        return false;
    }

    /**
     * Returns {@code true} if the specified flag is set on the command line.
     * This method should be called exactly once for each flag. Second invocation
     * for the same flag will returns {@code false} (unless the same flag
     * appears many times on the command line).
     *
     * @param name The flag name.
     * @return {@code true} if this flag appears on the command line, or {@code false}
     * otherwise.
     */
    public boolean getFlag(final String name) {
        for (int i = 0; i < arguments.length; i++) {
            String arg = arguments[i];
            if (arg != null) {
                arg = arg.trim();
                if (arg.equalsIgnoreCase(name)) {
                    arguments[i] = null;
                    return true;
                }
            }
        }
        return false;
    }


    protected void illegalArgument(final Exception exception) {
        System.err.println(exception.getMessage());
        exception.printStackTrace();
        System.exit(-1);
    }
}