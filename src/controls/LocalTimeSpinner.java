/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controls;

import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.util.StringConverter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author adrest18
 */
public class LocalTimeSpinner extends Spinner<LocalTime> {

    private final static String COLON = ":";
    private final static String TIME_FORMAT_PATTERN = "HH:mm";
    private final static String TEXT_FORMAT_PATTERN = "[0-9]{0,2}:[0-9]{0,2}";
    
    private final LocalTime initialTime = LocalTime.MIN;
    private final ObjectProperty<Mode> editingMode = new SimpleObjectProperty<>(Mode.HOURS);

    public ObjectProperty<Mode> modeProperty(){
        return editingMode;
    }
    
    public final Mode getMode(){
        return modeProperty().get();
    }
    
    public final void setMode(Mode mode){
        modeProperty().set(mode);
    }

    // Constructor
    public LocalTimeSpinner(){
        this(LocalTime.MIN);
    }

    public LocalTimeSpinner(LocalTime time){
        setEditable(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN);
        StringConverter<LocalTime> localTimeConverter = new StringConverter<LocalTime>(){
            @Override
            public String toString(LocalTime time){
                return formatter.format(time);
            }

            @Override
            public LocalTime fromString(String string){
                String[] tokens = string.split(COLON);
                int hours = getIntField(tokens, 0);
                int minutes = getIntField(tokens, 1);
                int totalSeconds = (hours * 60 + minutes) * 60;
                return LocalTime.of((totalSeconds / 3600) % 24, (totalSeconds / 60) % 60);
            }

            private int getIntField(String[] tokens, int index){
                if(tokens.length <= index || tokens[index].isEmpty()){
                    return 0;
                }
                return Integer.parseInt(tokens[index]);
            }
        };

        // The textFormatter both manages the text <-> LocalTime conversion,
        // and vetoes any edits that are not valid. We just make sure we have
        // two colons and only digits in between:
        TextFormatter<LocalTime> textFormatter = new TextFormatter<>(localTimeConverter, initialTime, c -> {
            String newText = c.getControlNewText();
            if(newText.matches(TEXT_FORMAT_PATTERN)){
                return c;
            }
            return null;
        });

        // The spinner value factory defines increment and decrement by
        // delegating to the current editing mode:
        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<LocalTime>(){
            @Override
            public void decrement(int steps){
                setValue(editingMode.get().decrement(getValue(), steps));
                editingMode.get().select(LocalTimeSpinner.this);
            }

            @Override
            public void increment(int steps){
                setValue(editingMode.get().increment(getValue(), steps));
                editingMode.get().select(LocalTimeSpinner.this);
            }
        };
        valueFactory.setConverter(localTimeConverter);
        valueFactory.setValue(time);

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

    // Mode represents the unit that is currently being edited.
    // For convenience expose methods for incrementing and decrementing that
    // unit, and for selecting the appropriate portion in a spinner's editor
    enum Mode{
        HOURS{
            @Override
            LocalTime increment(LocalTime time, int steps){
                return time.plusHours(steps);
            }

            @Override
            void select(LocalTimeSpinner spinner){
                int index = spinner.getEditor().getText().indexOf(COLON);
                spinner.getEditor().selectRange(0, index);
            }
        },

        MINUTES{
            @Override
            LocalTime increment(LocalTime time, int steps){
                return time.plusMinutes(steps);
            }

            @Override
            void select(LocalTimeSpinner spinner){
                int index = spinner.getEditor().getText().lastIndexOf(COLON);
                spinner.getEditor().selectRange(index + 1, spinner.getEditor().getText().length());
            }
        };

        abstract LocalTime increment(LocalTime time, int steps);

        abstract void select(LocalTimeSpinner spinner);

        LocalTime decrement(LocalTime time, int steps){
            return increment(time, -steps);
        }
    }

}