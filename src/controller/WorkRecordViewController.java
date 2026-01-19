/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.*;
import java.time.*;
import static java.time.DayOfWeek.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.*;
import model.*;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.*;
import tablecelltemplates.*;
import utils.*;

/**
 * https://code.makery.ch/blog/javafx-8-tableview-cell-renderer/
 * @author stephan
 */
@SuppressWarnings("unused")
public class WorkRecordViewController implements Initializable, IViewController, IEventListener {

    private final String selectedUserResourceKey = "SelectedUser";
    private final String startDateResourceKey = "StartDate";
    private final String endDateResourceKey = "EndDate";
    private final String dateResourceKey = "Date";
    private final String startTimeResourceKey = "StartTime";
    private final String endTimeResourceKey = "EndTime";
    private final String workTimeResourceKey = "WorkTime";
    private final String overTimeResourceKey = "OverTime";
    private final String overallOverTimeCorrectionResourceKey = "OverallOverTimeCorrection";
    private final String vacationCorrectionResourceKey = "VacationCorrection";
    private final String locationResourceKey = "Location";
    private final String projectResourceKey = "Project";
    private final String descriptionResourceKey = "Description";

    private final String newWorkrecordEvent = "NewWorkrecord";
    private final String editWorkrecordEvent = "EditWorkrecord";    
    private final String deleteWorkrecordEvent = "DeleteWorkrecord";    
    private final String selectedWorkRecordChangedEvent = "SelectedWorkRecordChanged";
    
    private final String userChangedEvent = "UserChanged";

    private final String startDateChangedEvent = "StartDateChanged";
    private final String endDateChangedEvent = "EndDateChanged";

    private final String workRecordDetailsDateChangedEvent = "WorkRecordDetailsDateChanged";
    
    private final String newUserEventEvent = "NewUser";
    private final String editUserEvent = "EditUser";
    private final String deleteUserEvent = "DeleteUser";

    private final String newHolydayEvent = "NewHolyday";
    private final String editHolydayEvent = "EditHolyday";
    private final String deleteHolydayEvent = "DeleteHolyday";
    private final String importHolydayEvent = "ImportHolyday";
    
    @FXML
    private ToolBar workrecordToolBar;
    @FXML
    private Label selectedUserLabel;
    @FXML
    private ComboBox selectedUserComboBox;
    @FXML
    private Label startDateLabel;
    @FXML
    private DatePicker startDateDatePicker;
    @FXML
    private void handleOnStartDateChangedAction(ActionEvent event) throws SQLException {
        handleStartDateChangedAction(event);
    }
    @FXML
    private Label endDateLabel;
    @FXML
    private DatePicker endDateDatePicker;
    @FXML
    private void handleOnEndDateChangedAction(ActionEvent event) throws SQLException {
        handleEndDateChangedAction(event);
    }
    @FXML
    private TableView<Workrecord> workrecordTableView;
    @FXML
    private TableColumn<Workrecord, LocalDate> workrecordDateTableColumn;
    @FXML
    private TableColumn<Workrecord, LocalTime> workrecordStartTimeTableColumn;
    @FXML
    private TableColumn<Workrecord, LocalTime> workrecordEndTimeTableColumn;
    @FXML
    private TableColumn<Workrecord, LocalTime> workrecordWorkTimeTableColumn;
    @FXML
    private TableColumn<Workrecord, String> workrecordOverTimeTableColumn;
    @FXML
    private TableColumn<Workrecord, String> workrecordOverTimeCorrectionTableColumn;
    @FXML
    private TableColumn<Workrecord, Integer> workrecordVacationCorrectionTableColumn;
    @FXML
    private TableColumn<Workrecord, String> workrecordLocationTableColumn;
    @FXML
    private TableColumn<Workrecord, String> workrecordProjectTableColumn;
    @FXML
    private TableColumn<Workrecord, String> workrecordDescriptionTableColumn;

    private final Logger log = LogManager.getLogger(WorkRecordViewController.class.getName());
    
    private Stage primaryStage;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final UserDAO userDAO;
    private ObservableList<User> userData;    
    private final WorkrecordDAO workrecordDao;
    private ObservableList<Workrecord> workrecordData;
    private final HolydayDAO holydayDao;
    private ObservableList<Holyday> holydayData;
    public EventManager eventManager;
    private final ControllerRepository controllerRepository;
    
    public WorkRecordViewController(LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(propertiesService == null) throw new NullPointerException("propertiesService");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.propertiesService = propertiesService;
        this.userDAO = new UserDAO(connection);
        this.userData = FXCollections.observableArrayList(this.userDAO.selectAll());
        this.workrecordDao = new WorkrecordDAO(connection);
        this.workrecordData = FXCollections.observableArrayList();
        this.holydayDao = new HolydayDAO(connection);
        this.holydayData = FXCollections.observableArrayList(this.holydayDao.selectAll());
        this.controllerRepository = ControllerRepository.getInstance();   
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(selectedWorkRecordChangedEvent);
        this.eventManager.registerEventType(startDateChangedEvent);
        this.eventManager.registerEventType(endDateChangedEvent);
        this.eventManager.registerEventType(userChangedEvent);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        workrecordTableView.setItems(workrecordData);
        
        refreshSelectedUserComboBox(userData);        
        setLastActiveUser(selectedUserComboBox);
        
        setStartDateToFirstDayOfActualMonth();
        initStartDateCellFactory();
        setEndDateToLastDayOfActualMonth();
        initEndDateCellFactory();
        
        //HOWTO: Cell Value Factory
        //The cell must know which part of Workrecord it needs to display. For all cells in the workrecordDateTableColumn this will be the Workrecord date value.
        workrecordDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
        workrecordStartTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStarttimeProperty());
        workrecordEndTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndtimeProperty());
        workrecordWorkTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getWorktimeProperty());
        workrecordWorkTimeTableColumn.setCellFactory((TableColumn<Workrecord, LocalTime> cellData) -> new ReadOnlyRedGreenColoredLocalTimeCellByEvaluationOfMinusSign<>());
        workrecordOverTimeTableColumn.setCellValueFactory(cellData -> cellData.getValue().getOvertimeProperty());
        workrecordOverTimeTableColumn.setCellFactory((TableColumn<Workrecord, String> cellData) -> new ReadOnlyRedGreenColoredStringCellByEvaluationOfMinusSign<>());
        workrecordOverTimeCorrectionTableColumn.setCellValueFactory(cellData -> cellData.getValue().getOvertimecorrectionProperty());
        workrecordOverTimeCorrectionTableColumn.setCellFactory((TableColumn<Workrecord, String> cellData) -> new ReadOnlyRedGreenColoredStringCellByEvaluationOfMinusSign<>());
        workrecordVacationCorrectionTableColumn.setCellValueFactory(cellData -> cellData.getValue().getVacationcorrectionProperty());
        workrecordVacationCorrectionTableColumn.setCellFactory((TableColumn<Workrecord, Integer> cellData) -> new ReadOnlyRedGreenColoredIntegerCellByEvaluationOfMinusSign<>());
        workrecordLocationTableColumn.setCellValueFactory(cellData -> cellData.getValue().getWorklocationProperty().asString());
        workrecordProjectTableColumn.setCellValueFactory(cellData -> cellData.getValue().getProjectProperty().asString());
        workrecordDescriptionTableColumn.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());

        ChangeListener<Workrecord> selectedWorkrecordChangedListener = (observable, oldValue, newValue) -> {
            eventManager.notifyListenerOfEvent(selectedWorkRecordChangedEvent, newValue);    
        };
        workrecordTableView.getSelectionModel().selectedItemProperty().addListener(selectedWorkrecordChangedListener);
        
        ChangeListener<User> selectedUserChangedListener = new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                if(newValue != null && oldValue != newValue) {
                    propertiesService.setProperty(selectedUserResourceKey, ((User)newValue).getLastname());
                    eventManager.notifyListenerOfEvent(userChangedEvent, newValue);
                    refreshWorkrecordTableView();
                    selectWorkrecordOf(LocalDate.now());
                }
            }
        };                
        selectedUserComboBox.getSelectionModel().selectedItemProperty().addListener(selectedUserChangedListener);
        
        refreshWorkrecordTableView();
        selectWorkrecordOf(LocalDate.now());
        
        languageService.updateGuiItems();
    }

    @Override
    public void updateGuiItems() {
        selectedUserLabel.setText(rb.getString(selectedUserResourceKey));
        startDateLabel.setText(rb.getString(startDateResourceKey));
        endDateLabel.setText(rb.getString(endDateResourceKey));
        workrecordDateTableColumn.setText(rb.getString(dateResourceKey));
        renderWorkrecordDateTableColumn(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        workrecordStartTimeTableColumn.setText(rb.getString(startTimeResourceKey));
        workrecordEndTimeTableColumn.setText(rb.getString(endTimeResourceKey));
        workrecordWorkTimeTableColumn.setText(rb.getString(workTimeResourceKey));
        workrecordOverTimeTableColumn.setText(rb.getString(overTimeResourceKey));
        workrecordOverTimeCorrectionTableColumn.setText(rb.getString(overallOverTimeCorrectionResourceKey));
        workrecordVacationCorrectionTableColumn.setText(rb.getString(vacationCorrectionResourceKey));
        workrecordLocationTableColumn.setText(rb.getString(locationResourceKey));
        workrecordProjectTableColumn.setText(rb.getString(projectResourceKey));
        workrecordDescriptionTableColumn.setText(rb.getString(descriptionResourceKey));

        refreshStartDateFormat();
        initStartDateCellFactory();
        refreshEndDateFormat();
        initEndDateCellFactory();
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return rb;
    }

    @Override
    public void setResourceBundle(ResourceBundle rb) {
        this.rb = rb;
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    @Override
    public void update(String eventType, Object source) {
        switch (eventType) {
            case workRecordDetailsDateChangedEvent -> {
                LocalDate workrecordDate = (LocalDate)source;
                if(workrecordDate != null) {                    
                    selectWorkrecordOf(workrecordDate);
                }
            }
            case newUserEventEvent, editUserEvent, deleteUserEvent -> {
                refreshUsers(); 
            }
            case newWorkrecordEvent, editWorkrecordEvent, deleteWorkrecordEvent -> {
                refreshWorkrecordTableView();
            }
            case newHolydayEvent, editHolydayEvent, deleteHolydayEvent, importHolydayEvent -> {
                try {
                    holydayData = FXCollections.observableArrayList(this.holydayDao.selectAll());
                    updateGuiItems();
                } catch (SQLException ex) {
                    log.fatal("SelectAll holydays failed");
                }
            }
        }
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    public boolean IsDummyWorkrecord(Workrecord workrecord) {
        if(workrecord != null) {
            LocalDate workrecordDate = workrecord.getDate();
            Workrecord dummyWorkrecord = createDummyWorkRecord(workrecordDate);
            return workrecord.equals(dummyWorkrecord);
        }
        return true;
    }
    
    public void selectWorkrecordOf(LocalDate localDate) {
        Platform.runLater(() -> {
            Workrecord workrecordToUse;
            Workrecord dummyWorkrecord = createDummyWorkRecord(localDate);
            List<Workrecord> storedWorkrecords = new ArrayList<>();
            try {
                User selectedUser = (User)selectedUserComboBox.getSelectionModel().getSelectedItem();
                if(selectedUser != null) {
                    storedWorkrecords = workrecordDao.selectAll(selectedUser, localDate);
                }
            } catch (SQLException ex) {
                log.fatal("Select workrecord of " + localDate + " failed");
            }
            
            workrecordTableView.requestFocus();
            if(storedWorkrecords.isEmpty()) {
                workrecordToUse = dummyWorkrecord;
            } else {
                workrecordToUse = storedWorkrecords.getFirst();
            }
            workrecordTableView.getSelectionModel().select(workrecordToUse);
            workrecordTableView.scrollTo(workrecordToUse);
            eventManager.notifyListenerOfEvent(selectedWorkRecordChangedEvent, workrecordToUse);
        });
    }
        
    private void colorHolydays(LocalDate item, TableCell<Workrecord, LocalDate> tableCell) {
        for(Holyday holyday : holydayData) {
            if(holyday.getDate().equals(item)) {
                tableCell.setTextFill(Color.BLUE);
                tableCell.setTooltip(new Tooltip(holyday.getName()));
            }
        }
    }

    private void colorWeekends(LocalDate item, TableCell<Workrecord, LocalDate> tableCell) {
        DayOfWeek dayOfWeek = item.getDayOfWeek();
        switch (dayOfWeek) {
            case SATURDAY -> {
                tableCell.setTextFill(Color.ORANGE);
                tableCell.setTooltip(new Tooltip(DayOfWeek.SATURDAY.name()));
            }
            case SUNDAY -> {
                tableCell.setTextFill(Color.ORANGE);
                tableCell.setTooltip(new Tooltip(DayOfWeek.SUNDAY.name()));
            }
            default -> tableCell.setTextFill(Color.BLACK);
        }
    }
    
    private Workrecord createDummyWorkRecord(LocalDate date) {
        LocalTime dummyTime = LocalTime.MIN;
        
        Role role = new Role(0L, "", "");
        Address addresse = new Address(0L, "", 0L, "", 0L, "", "", "", 0L, "");
        Contract contract = new Contract(0L, "", 0L, 0L , 0L, "", LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN, LocalTime.MIN);
        User user = new User(0L, role, addresse, contract, "", "", "", "", 0L);
        Project project = new Project(0L, "", "", "", "", "", "");
        Worklocation worklocation = new Worklocation(0L, "", "");

        DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        
        return new Workrecord(0L, user, project, date, dummyTime, dummyTime, dummyTime, "00:00", "00:00", 0, worklocation, "");
    }    

    private void createDummyWorkrecords(LocalDate startDate, LocalDate endDate) {
        long daysBetweenStartAndEndDate = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
        for (int i = 0; i < daysBetweenStartAndEndDate; i++) {
            LocalDate newDate = startDate.plusDays(i);
            Workrecord dummy = createDummyWorkRecord(newDate);
            if(!workrecordData.contains(dummy)) {
                workrecordData.add(dummy);
            }
        }    
    }
    
    private void formatDatePickerValue(DatePicker datePicker) {
        DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate localDate) {
                if(localDate == null) {
                    return "";
                }
                return localDate.format(dateTimeFormater);
            }
            @Override
            public LocalDate fromString(String string) {
                return LocalDate.parse(string, dateTimeFormater);
            }
        });
    }

    public LocalDate getStartDate() {
        return startDateDatePicker.getValue();
    }
    
    public LocalDate getEndDate() {
        return endDateDatePicker.getValue();
    }
    
    public User getSelectedUser() {
        return (User)selectedUserComboBox.getValue();
    }
    
    public TableView<Workrecord> getWorkrecordTableView() {
        return workrecordTableView;
    }
    
    private void handleStartDateChangedAction(ActionEvent event) throws SQLException {
        refreshWorkrecordTableView();
        eventManager.notifyListenerOfEvent(startDateChangedEvent, startDateDatePicker.getValue());
    }

    private void handleEndDateChangedAction(ActionEvent event) throws SQLException {
        refreshWorkrecordTableView();
        eventManager.notifyListenerOfEvent(endDateChangedEvent, endDateDatePicker.getValue());
    }

    private void initStartDateCellFactory() {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty)
                {
                    super.updateItem(item, empty);
                    LocalDate selectedEndDate = endDateDatePicker.getValue();
                    if(item.isAfter(selectedEndDate))
                    {
                        setStyle("-fx-background-color: #ffc0cb;");
                        setDisable(true);
                    }
                }
            };
        };
        startDateDatePicker.setDayCellFactory(dayCellFactory);
    }
    
    private void initEndDateCellFactory() {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty)
                {
                    super.updateItem(item, empty);
                    LocalDate selectedStartDate = startDateDatePicker.getValue();
                    if(item.isBefore(selectedStartDate))
                    {
                        setStyle("-fx-background-color: #ffc0cb;");
                        setDisable(true);
                    }
                }
            };
        };
        endDateDatePicker.setDayCellFactory(dayCellFactory);
    }
    
    private void mergeWorkrecords(List<Workrecord> storedWorkrecords) {
        storedWorkrecords.forEach((workrecord) -> {
            Workrecord dummy = createDummyWorkRecord(workrecord.getDate());
            if (workrecordData.contains(dummy)) {
                workrecordData.remove(dummy);
            }
            workrecordData.add(workrecord);
        });
    }

    //HOWTO: Cell Factory (Custom rendering of the table cell)
    //Once the cell has the value, it must know how to display that value. 
    //In our case, the Workrecord’s LocalDate value must be formatted and colored 
    //depending on the logic that is implemented.
    private void renderWorkrecordDateTableColumn(DateTimeFormatter dateTimeFormater) {
        Callback<TableColumn<Workrecord, LocalDate>, TableCell<Workrecord, LocalDate>> dayCellFactory = column -> {
            return new TableCell<Workrecord, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(dateTimeFormater.format(item));
                        colorWeekends(item, this);
                        colorHolydays(item, this);
                    }
                }
            };
        };
        workrecordDateTableColumn.setCellFactory(dayCellFactory);
    }
    
    private void refreshStartDateFormat() {
        formatDatePickerValue(startDateDatePicker);        
    }
  
    private void refreshEndDateFormat() {
        formatDatePickerValue(endDateDatePicker);
    }
        
    private void refreshUsers() {
        try {
            User activeUser = (User)selectedUserComboBox.getValue();
            
            userData = FXCollections.observableArrayList(userDAO.selectAll());            

            refreshSelectedUserComboBox(userData);
            
            if(activeUser != null) {            
                String lastNameOfActiveUser = activeUser.getLastname();
                ObservableList<User> userList = selectedUserComboBox.getItems();
                for(User user : userList) {
                    if(user.getLastname().equals(lastNameOfActiveUser)) {
                        selectedUserComboBox.getSelectionModel().select(user);
                        eventManager.notifyListenerOfEvent(userChangedEvent, user);
                    }
                }
            }
        } catch (SQLException ex) {
            log.fatal("Refresh of users failed");
        }
    }
       
    private void refreshSelectedUserComboBox(ObservableList<User> userData) {
        selectedUserComboBox.getItems().clear();
        selectedUserComboBox.setItems(userData);
    }
    
    private void refreshWorkrecordTableView() {
        User selectedUser = (User)selectedUserComboBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDateDatePicker.getValue();
        LocalDate endDate = endDateDatePicker.getValue();
            
        try {
            if(selectedUser != null) {
                List<Workrecord> storedWorkrecords = workrecordDao.selectAll(selectedUser, startDate, endDate);
                workrecordData.clear();
                createDummyWorkrecords(startDate, endDate);
                mergeWorkrecords(storedWorkrecords);
                sortWorkrecordTableViewByDate();
            }
        } catch (SQLException | NullPointerException ex ) {
            log.error("Refresh of WorkrecordTableView failed!");
        }
    }

    private void setLastActiveUser(ComboBox selectedUserComboBox) {
        String lastNameOfActiveUser = propertiesService.getProperty(selectedUserResourceKey);
        ObservableList<User> userList = selectedUserComboBox.getItems();
        for(User user : userList) {
            if(user.getLastname().equals(lastNameOfActiveUser)) {
                selectedUserComboBox.getSelectionModel().select(user);
            }
        }        
    }
    
    private void setStartDateToFirstDayOfActualMonth() {
        refreshStartDateFormat();
        
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        int firstDayOfMonth = 1;
 
        startDateDatePicker.setValue(LocalDate.of(year, month, firstDayOfMonth));
        eventManager.notifyListenerOfEvent(startDateChangedEvent, startDateDatePicker.getValue());
    }
    
    private void setEndDateToLastDayOfActualMonth() {
        refreshEndDateFormat();

        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();
        int lastDayOfMonth = now.lengthOfMonth();
        
        endDateDatePicker.setValue(LocalDate.of(year, month, lastDayOfMonth));
        eventManager.notifyListenerOfEvent(endDateChangedEvent, endDateDatePicker.getValue());

    }

    private void sortWorkrecordTableViewByDate() {
        workrecordDateTableColumn.setSortType(TableColumn.SortType.ASCENDING);
        workrecordTableView.getSortOrder().setAll(workrecordDateTableColumn);
        workrecordTableView.sort();
    }

    @Override
    public void preCloseAction() {

    }
    
}
