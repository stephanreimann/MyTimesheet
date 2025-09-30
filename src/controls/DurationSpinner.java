/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.util.StringConverter;
import java.time.Duration;

/**
 *
 * @author adrest18
 */
public class DurationSpinner extends Spinner<Duration> {
    
    private final static String COLON = ":";
    private final static String MINUS = "-";
    private final static String EMPTY_STRING = "";
    private final static String TEXT_FORMAT_PATTERN = "-?[0-9]{0,2}:[0-9]{0,2}";
    private final static String CONVERT_PATTERN = "%s%02d:%02d";
    private final static String PARSE_LIMIT =  "PT99H59M";
    
    private final StringConverter<Duration> localDurationConverter;
    private final Duration initialDuration = Duration.ZERO;
    private final ObjectProperty<Mode> editingMode = new SimpleObjectProperty<>(Mode.HOURS);

    // Constructor
    public DurationSpinner(){
        this(Duration.ZERO);
    }

    public DurationSpinner(Duration duration){
        setEditable(true);

        localDurationConverter = new StringConverter<Duration>(){
            @Override
            public String toString(Duration duration) {
                long seconds = duration.getSeconds();
                long absSeconds = Math.abs(seconds);
                String sign = seconds < 0 ? MINUS : EMPTY_STRING;
                return String.format(CONVERT_PATTERN, sign, absSeconds / 3600, (absSeconds % 3600) / 60);
            }

            @Override
            public Duration fromString(String string) {
                boolean negative = string.startsWith(MINUS);
                String[] tokens = string.replace(MINUS, EMPTY_STRING).split(COLON);
                int hours = getIntField(tokens, 0);
                int minutes = getIntField(tokens, 1);
                int totalSeconds = (hours * 60 + minutes) * 60;
                return Duration.ofSeconds(negative ? -totalSeconds : totalSeconds);
            }

            private int getIntField(String[] tokens, int index){
                if(tokens.length <= index || tokens[index].isEmpty()){
                    return 0;
                }
                return Integer.parseInt(tokens[index]);
            }
        };

        // The textFormatter both manages the text <-> Duration conversion,
        // and vetoes any edits that are not valid. We just make sure we have
        // two colons and only digits in between:
        TextFormatter<Duration> textFormatter = new TextFormatter<>(localDurationConverter, initialDuration, c -> {
            String newText = c.getControlNewText();
            if (newText.matches(TEXT_FORMAT_PATTERN)) {
                return c;
            }
            return null;
        });

        // The spinner value factory defines increment and decrement by
        // delegating to the current editing mode:
        SpinnerValueFactory<Duration> valueFactory = new SpinnerValueFactory<Duration>(){
            @Override
            public void decrement(int steps){
                Duration duration = editingMode.get().decrement(getValue(), steps);
                Duration delta = Duration.parse(PARSE_LIMIT).minus(duration);
                if(delta.isNegative()) {
                    return;
                }
                setValue(duration);
                editingMode.get().select(DurationSpinner.this);
            }

            @Override
            public void increment(int steps){
                Duration duration = editingMode.get().increment(getValue(), steps);
                Duration delta = Duration.parse(PARSE_LIMIT).minus(duration);
                if(delta.isNegative()) {
                    return;
                }
                setValue(duration);
                editingMode.get().select(DurationSpinner.this);
            }
        };
        valueFactory.setConverter(localDurationConverter);
        valueFactory.setValue(duration);

        this.setValueFactory(valueFactory);
        this.getEditor().setTextFormatter(textFormatter);

        // Update the mode when the user interacts with the editor.
        // This is a bit of a hack, e.g. calling spinner.getEditor().positionCaret()
        // could result in incorrect state. Directly observing the caretPostion
        // didn't work well though; getting that to work properly might be
        // a better approach in the long run.
        this.getEditor().addEventHandler(InputEvent.ANY, e -> {
            int caretPos = this.getEditor().getCaretPosition();
            int hrIndex = this.getEditor().getText().indexOf(COLON);
            if(caretPos <= hrIndex){
                editingMode.set(Mode.HOURS);
            }else{
                editingMode.set(Mode.MINUTES);
            }
        });

        // When the mode changes, select the new portion:
        editingMode.addListener((obs, oldMode, newMode) -> newMode.select(this));
    }

    public StringConverter<Duration> getDurationConverter() {
        return localDurationConverter;
    }
    
    // Mode represents the unit that is currently being edited.
    // For convenience expose methods for incrementing and decrementing that
    // unit, and for selecting the appropriate portion in a spinner's editor
    enum Mode{
        HOURS{
            @Override
            Duration increment(Duration duration, int steps){
                return duration.plusHours(steps);
            }

            @Override
            void select(DurationSpinner spinner){
                int index = spinner.getEditor().getText().indexOf(COLON);
                spinner.getEditor().selectRange(0, index);
            }
        },

        MINUTES{
            @Override
            Duration increment(Duration duration, int steps){
                return duration.plusMinutes(steps);
            }

            @Override
            void select(DurationSpinner spinner){
                int index = spinner.getEditor().getText().lastIndexOf(COLON);
                spinner.getEditor().selectRange(index+1, index+3);
            }
        };

        abstract Duration increment(Duration duration, int steps);

        abstract void select(DurationSpinner spinner);

        Duration decrement(Duration duration, int steps){
            return increment(duration, -steps);
        }
    }

}