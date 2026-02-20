package org.manage.service.util;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing Telegram messages for time tracking.
 * Supported formats:
 * - HH:MM (e.g., 2:30)
 * - Xh Ym (e.g., 2h 30m, 2ч 30м)
 * - X.Yh (e.g., 1.5h)
 * - Xm (e.g., 90m)
 */
public class TelegramMessageParser {

    private static final Pattern TIME_PATTERN = Pattern.compile("^(\\d{1,2}):(\\d{2})$");

    // Support X.Y H, X H, Y M, etc.
    private static final Pattern DURATION_COMPONENTS_PATTERN = Pattern.compile("(\\d+(?:[.,]\\d+)?)([HMD])");

    private TelegramMessageParser() {
        // Utility class
    }

    /**
     * Parses a duration string into a java.time.Duration.
     *
     * @param input the duration string
     * @return the parsed Duration
     * @throws IllegalArgumentException if the format is invalid
     */
    public static Duration parseDuration(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        String normalized = input.trim().toUpperCase().replaceAll("\\s", "");

        // Handle HH:MM
        Matcher timeMatcher = TIME_PATTERN.matcher(normalized);
        if (timeMatcher.matches()) {
            int hours = Integer.parseInt(timeMatcher.group(1));
            int minutes = Integer.parseInt(timeMatcher.group(2));
            if (minutes >= 60) {
                throw new IllegalArgumentException("Minutes must be less than 60");
            }
            return Duration.ofHours(hours).plusMinutes(minutes);
        }

        // Normalize units
        normalized = normalized
            .replace("В", "D")
            .replace("Д", "D")
            .replace("Р", "H")
            .replace("Ч", "H")
            .replace("Ь", "M")
            .replace("М", "M");

        Matcher matcher = DURATION_COMPONENTS_PATTERN.matcher(normalized);
        Duration totalDuration = Duration.ZERO;
        boolean found = false;
        int lastEnd = 0;

        while (matcher.find()) {
            found = true;
            if (matcher.start() != lastEnd) {
                throw new IllegalArgumentException("Invalid duration format");
            }
            double value = Double.parseDouble(matcher.group(1).replace(",", "."));
            String unit = matcher.group(2);

            switch (unit) {
                case "D":
                    totalDuration = totalDuration.plusMinutes((long) (value * 8 * 60)); // 1 Day = 8 Hours
                    break;
                case "H":
                    totalDuration = totalDuration.plusMinutes((long) (value * 60));
                    break;
                case "M":
                    totalDuration = totalDuration.plusSeconds((long) (value * 60));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }
            lastEnd = matcher.end();
        }

        if (found && lastEnd == normalized.length()) {
            return totalDuration;
        }

        // Fallback for purely numeric input (minutes)
        if (normalized.matches("^\\d+$")) {
            return Duration.ofMinutes(Long.parseLong(normalized));
        }

        throw new IllegalArgumentException("Invalid duration format: " + input);
    }

    /**
     * Result of parsing a full Telegram message.
     */
    public static class ParserResult {
        private final Duration duration;
        private final String description;

        public ParserResult(Duration duration, String description) {
            this.duration = duration;
            this.description = description;
        }

        public Duration getDuration() {
            return duration;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Parses a full message into duration and description.
     * Expects the duration to be at the beginning of the message.
     *
     * @param message the full message string
     * @return the ParserResult, or null if parsing fails
     */
    public static ParserResult parseMessage(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }

        String trimmed = message.trim();
        String[] tokens = trimmed.split("\\s+");
        if (tokens.length < 2) {
            return null;
        }

        StringBuilder durationPart = new StringBuilder();
        Duration totalDuration = Duration.ZERO;
        int tokenIndex = 0;

        while (tokenIndex < tokens.length) {
            try {
                String token = tokens[tokenIndex];
                // Special case: if it's the first token and it's HH:MM, we take it and stop duration parsing
                if (tokenIndex == 0 && TIME_PATTERN.matcher(token).matches()) {
                    totalDuration = parseDuration(token);
                    durationPart.append(token);
                    tokenIndex++;
                    break;
                }

                // Try to parse token as duration component
                Duration tokenDuration = parseDuration(token);
                totalDuration = totalDuration.plus(tokenDuration);
                if (durationPart.length() > 0) {
                    durationPart.append(" ");
                }
                durationPart.append(token);
                tokenIndex++;
            } catch (IllegalArgumentException e) {
                // Not a duration token, stop here
                break;
            }
        }

        if (totalDuration.isZero() || tokenIndex == 0 || tokenIndex == tokens.length) {
            return null;
        }

        String description = trimmed.substring(durationPart.length()).trim();
        if (description.isEmpty()) {
            return null;
        }

        return new ParserResult(totalDuration, description);
    }
}
