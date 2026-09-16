/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

/**
 *
 * @author adrest18
 */
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.scene.input.InputEvent;
import java.time.Duration;

public class DurationSpinner extends Spinner<Duration> {
    
    private final static String COLON = ":";
    private final static String MINUS = "-";
    private final static String EMPTY_STRING = "";
    // Modified patterns to be dynamic
    private final static String NEGATIVE_ALLOWED_PATTERN = "-?[0-9]{0,2}:[0-9]{0,2}";
    private final static String NEGATIVE_FORBIDDEN_PATTERN = "[0-9]{0,2}:[0-9]{0,2}";
    
    private final static String CONVERT_PATTERN = "%s%02d:%02d";
    private final static String PARSE_LIMIT =  "PT99H59M";
    
    private final StringConverter<Duration> localDurationConverter;
    private final Duration initialDuration = Duration.ZERO;
    private final ObjectProperty<Mode> editingMode = new SimpleObjectProperty<>(Mode.HOURS);
    private final boolean allowNegative;

    // Constructors
    public DurationSpinner() {
        this(Duration.ZERO, true);
    }

    public DurationSpinner(boolean allowNegative) {
        this(Duration.ZERO, allowNegative);
    }

    public DurationSpinner(Duration duration, boolean allowNegative) {
        this.allowNegative = allowNegative;
        setEditable(true);

        localDurationConverter = new StringConverter<Duration>() {
            @Override
            public String toString(Duration duration) {
                if (duration == null) return String.format(CONVERT_PATTERN, EMPTY_STRING, 0, 0);
                long seconds = duration.getSeconds();
                long absSeconds = Math.abs(seconds);
                String sign = seconds < 0 ? MINUS : EMPTY_STRING;
                return String.format(CONVERT_PATTERN, sign, absSeconds / 3600, (absSeconds % 3600) / 60);
            }

            @Override
            public Duration fromString(String string) {
                if (string == null || string.isEmpty()) return Duration.ZERO;
                boolean negative = string.startsWith(MINUS);
                String[] tokens = string.replace(MINUS, EMPTY_STRING).split(COLON);
                int hours = getIntField(tokens, 0);
                int minutes = getIntField(tokens, 1);
                int totalSeconds = (hours * 60 + minutes) * 60;
                return Duration.ofSeconds(negative ? -totalSeconds : totalSeconds);
            }

            private int getIntField(String[] tokens, int index) {
                if (tokens.length <= index || tokens[index].isEmpty()) {
                    return 0;
                }
                try {
                    return Integer.parseInt(tokens[index]);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        };

        // Select pattern based on constructor argument
        String activePattern = allowNegative ? NEGATIVE_ALLOWED_PATTERN : NEGATIVE_FORBIDDEN_PATTERN;

        TextFormatter<Duration> textFormatter = new TextFormatter<>(localDurationConverter, initialDuration, c -> {
            String newText = c.getControlNewText();
            if (newText.matches(activePattern)) {
                return c;
            }
            return null;
        });

        SpinnerValueFactory<Duration> valueFactory = new SpinnerValueFactory<Duration>() {
            @Override
            public void decrement(int steps) {
                Duration duration = editingMode.get().decrement(getValue(), steps);
                
                // Logic change: Clamp to ZERO if negative values are not allowed
                if (!DurationSpinner.this.allowNegative && duration.isNegative()) {
                    duration = Duration.ZERO;
                }

                // Check upper limit
                Duration delta = Duration.parse(PARSE_LIMIT).minus(duration.abs());
                if (delta.isNegative()) return;

                setValue(duration);
                editingMode.get().select(DurationSpinner.this);
            }

            @Override
            public void increment(int steps) {
                Duration duration = editingMode.get().increment(getValue(), steps);
                
                // Check upper limit
                Duration delta = Duration.parse(PARSE_LIMIT).minus(duration.abs());
                if (delta.isNegative()) return;

                setValue(duration);
                editingMode.get().select(DurationSpinner.this);
            }
        };
        
        valueFactory.setConverter(localDurationConverter);
        valueFactory.setValue(duration);

        this.setValueFactory(valueFactory);
        this.getEditor().setTextFormatter(textFormatter);

        this.getEditor().addEventHandler(InputEvent.ANY, e -> {
            int caretPos = this.getEditor().getCaretPosition();
            int hrIndex = this.getEditor().getText().indexOf(COLON);
            if (caretPos <= hrIndex) {
                editingMode.set(Mode.HOURS);
            } else {
                editingMode.set(Mode.MINUTES);
            }
        });

        editingMode.addListener((obs, oldMode, newMode) -> newMode.select(this));
    }

    public boolean isAllowNegative() {
        return allowNegative;
    }

    public StringConverter<Duration> getDurationConverter() {
        return localDurationConverter;
    }
    
    enum Mode {
        HOURS {
            @Override
            Duration increment(Duration duration, int steps) {
                return duration.plusHours(steps);
            }

            @Override
            void select(DurationSpinner spinner) {
                int index = spinner.getEditor().getText().indexOf(COLON);
                if (index > -1) {
                    spinner.getEditor().selectRange(0, index);
                }
            }
        },

        MINUTES {
            @Override
            Duration increment(Duration duration, int steps) {
                return duration.plusMinutes(steps);
            }

            @Override
            void select(DurationSpinner spinner) {
                int index = spinner.getEditor().getText().lastIndexOf(COLON);
                if (index > -1) {
                    spinner.getEditor().selectRange(index + 1, index + 3);
                }
            }
        };

        abstract Duration increment(Duration duration, int steps);
        abstract void select(DurationSpinner spinner);

        Duration decrement(Duration duration, int steps) {
            return increment(duration, -steps);
        }
    }
}