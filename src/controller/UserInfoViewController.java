/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.*;
import java.text.MessageFormat;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.ResourceBundle;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.*;
import org.apache.logging.log4j.*;
import service.*;
import sqlite.*;
import utils.*;

/**
 *
 * @author adrest18
 */
public class UserInfoViewController implements Initializable, IViewController, IEventListener {

    private final String userInfoExpandedPaneResourceKey = "LastExpandedUserInfoPane";
    
    private final String userInfoHeaderResourceKey = "UserInfoHeader";
    private final String userGeneralInfoResourceKey = "UserGeneralInfo";
    private final String userFirstNameResourceKey = "UserFirstName";
    private final String userLastNameResourceKey = "UserLastName";
    private final String userLoginResourceKey = "UserLogin";
    private final String userContractNameResourceKey = "UserContractName";
    private final String userRoleNameResourceKey = "UserRoleName";
    private final String userAddressResourceKey = "UserAddress";
    
    private final String userWorktimeInfoResourceKey = "UserWorktimeInfo";
    private final String workhoursResourceKey = "Workhours";
    private final String maxWorkhoursResourceKey = "MaxWorkhours";
    private final String breakfastOfftimeStartResourceKey = "BreakfastOfftimeStart";
    private final String breakfastOfftimeEndResourceKey = "BreakfastOfftimeEnd";
    private final String lunchOfftimeStartResourceKey = "LunchOfftimeStart";
    private final String lunchOfftimeEndResourceKey = "LunchOfftimeEnd";
    private final String earliestWorktimeStartResourceKey = "EarliestWorktimeStart";
    private final String latestWorktimeEndResourceKey = "LatestWorktimeEnd";
    private final String regularWorktimeCurrentMonthResourceKey = "RegularWorktimeCurrentMonth";
    private final String worktimeCurrentMonthResourceKey = "WorktimeCurrentMonth";
    private final String overtimeCurrentMonthResourceKey = "OvertimeCurrentMonth";
    private final String overallOvertimeResourceKey = "OverallOvertime";
    
    private final String userVacationInfoResourceKey = "UserVacationInfo";
    private final String vacationdaysResourceKey = "Vacationdays"; 
    private final String vacationReconciliationDateResourceKey = "VacationReconciliationDate";
    private final String vacationLeftResourceKey = "UserVacationLeft";
    private final String vacationCorrectionResourceKey = "VacationCorrection";
    private final String vacationCorrectionTypeResourceKey = "VacationCorrectionType";
    
    private final String userWorkdaysInfoResourceKey = "UserWorkdaysInfo";
    private final String workdaysResourceKey = "Workdays";
    private final String worklocationResourceKey = "Worklocation";
    private final String selectedWorklocationResourceKey = "SelectedWorklocation";
    
    private final String userChangedEvent = "UserChanged";
    
    private final String newWorkrecordEvent = "NewWorkrecord";
    private final String editWorkrecordEvent = "EditWorkrecord";
    private final String deleteWorkrecordEvent = "DeleteWorkrecord";

    private final String newWorkLocationEvent = "NewWorkLocation";
    private final String editWorkLocationEvent = "EditWorkLocation";
    private final String deleteWorkLocationEvent = "DeleteWorkLocation";

    private final String defaultOvertimeThreshold = "PT00H";
    private final String thresholdExceededResourceKey = "ThresholdExceeded";
    
    private final Logger log = LogManager.getLogger(UserInfoViewController.class.getName());
    
    @FXML
    private Label userInfoHeaderLabel;

    @FXML
    private Accordion userAccordion;
    
    @FXML
    private TitledPane userGeneralInfoTitledPane;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label firstNameLabelValue;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label lastNameLabelValue;
    @FXML
    private Label loginLabel;
    @FXML
    private Label loginLabelValue;
    @FXML
    private Label contractNameLabel;
    @FXML
    private Label contractNameLabelValue;
    @FXML
    private Label roleNameLabel;
    @FXML
    private Label roleNameLabelValue;
    @FXML
    private Label addressLabel;
    @FXML
    private Label addressLabelValue;
       
    @FXML
    private GridPane userWorktimeInfoGridPane;
    @FXML
    private TitledPane userWorktimeInfoTitledPane;
    @FXML
    private Label workhoursLabel;
    @FXML
    private Label workhoursLabelValue;    
    @FXML
    private Label maxWorkhoursLabel;
    @FXML
    private Label maxWorkhoursLabelValue;
    @FXML
    private Label breakfastOfftimeStartLabel;
    @FXML
    private Label breakfastOfftimeStartLabelValue;
    @FXML
    private Label breakfastOfftimeEndLabel;
    @FXML
    private Label breakfastOfftimeEndLabelValue;
    @FXML
    private Label lunchOfftimeStartLabel;
    @FXML
    private Label lunchOfftimeStartLabelValue;
    @FXML
    private Label lunchOfftimeEndLabel;
    @FXML
    private Label lunchOfftimeEndLabelValue;
    @FXML
    private Label earliestWorktimeStartLabel;
    @FXML
    private Label earliestWorktimeStartLabelValue;
    @FXML
    private Label latestWorktimeEndLabel;
    @FXML
    private Label latestWorktimeEndLabelValue;
    @FXML
    private Label regularWorktimeCurrentMonthLabel;
    @FXML
    private Label regularWorktimeCurrentMonthLabelValue;
    @FXML
    private Label worktimeCurrentMonthLabel;
    @FXML
    private Label worktimeCurrentMonthLabelValue;
    @FXML
    private Label overtimeCurrentMonthLabel;
    @FXML
    private Label overtimeCurrentMonthLabelValue;
    @FXML
    private Label overallOvertimeLabel;
    @FXML
    private Label overallOvertimeLabelValue;
    @FXML
    private ImageView overallOvertimeImageView;
    
    @FXML
    private GridPane userVacationInfoGridPane;
    @FXML
    private TitledPane userVacationInfoTitledPane;
    @FXML
    private Label vacationdaysLabel;
    @FXML
    private Label vacationDaysType;
    @FXML
    private Label vacationReconciliationDateLabel;
    @FXML
    private Label vacationdaysLabelValue;
    @FXML
    private Label vacationReconciliationDateLabelValue;
    @FXML
    private Label vacationLeftLabel;
    @FXML
    private Label vacationLeftLabelValue;
    @FXML
    private Label userVacationLeftType;
    
    @FXML
    private TitledPane userWorkdaysInfoTitledPane;
    @FXML
    private ChoiceBox<Worklocation> worklocationsChoiceBox;
    @FXML
    private Label workdaysLabel;
    @FXML
    private Label workdaysLabelValue;
    
    private Stage primaryStage;
    private final ControllerRepository controllerRepository;    
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private final PropertiesService propertiesService;
    private ResourceBundle rb;
    private final WorkRecordViewController workRecordViewController;
    private final HolydayDAO holydayDao;
    private ObservableList<Holyday> holydayData;
    private ObservableList<Worklocation> worklocationData;  
    private final WorkrecordDAO workrecordDao;
    private final WorklocationDAO worklocationDao;
    private EventManager eventManager;
    
    public UserInfoViewController(ControllerRepository controllerRepository, LanguageService languageService, Connection connection, UndoService undoService, PropertiesService propertiesService) throws SQLException {
        if(controllerRepository == null) throw new NullPointerException("controllerRepository");
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(propertiesService == null) throw new NullPointerException("propertiesService");

        this.workRecordViewController = (WorkRecordViewController) controllerRepository.get(WorkRecordViewController.class.getName());

        this.holydayDao = new HolydayDAO(connection);
        this.worklocationDao = new WorklocationDAO(connection);
        
        this.holydayData = FXCollections.observableArrayList(this.holydayDao.selectAll());
        this.worklocationData = FXCollections.observableArrayList(this.worklocationDao.selectAll());
        
        this.workrecordDao = new WorkrecordDAO(connection);
            
        this.controllerRepository = controllerRepository;
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.propertiesService = propertiesService;
        
        this.eventManager = new EventManager();
        this.eventManager.registerEventType(newWorkLocationEvent);
        this.eventManager.registerEventType(editWorkLocationEvent);
        this.eventManager.registerEventType(deleteWorkLocationEvent);    
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;

        restoreLastSelectedUserInfoPane();
        userAccordion.expandedPaneProperty().addListener((observable, oldValue, newValue) -> saveLastSelectedUserInfoPane(newValue));
        
        User selectedUser = workRecordViewController.getSelectedUser();
        if(selectedUser != null) {
            refreshUserGeneralInfos(selectedUser);
            try {
                refreshUserWorktimeInfos(selectedUser);
                refreshUserVacationInfos(selectedUser);
                worklocationsChoiceBox.setItems(worklocationData);
                setLastSelectedWorkLocation(worklocationsChoiceBox);
                refreshWorkdaysLabelValue(selectedUser, (Worklocation)worklocationsChoiceBox.getValue());
            } catch (SQLException ex) {
                log.error("Refresh of user worktime information failed!");
            }
        }
        
        worklocationsChoiceBox.valueProperty().addListener((ObservableValue<? extends Worklocation> observable, Worklocation oldValue, Worklocation newValue) -> {
            if(newValue != null) {
                propertiesService.setProperty(selectedWorklocationResourceKey, ((Worklocation)newValue).getName());
                refreshWorkdaysLabelValue(selectedUser, newValue);
            }
        });
    }

    @Override
    public void updateGuiItems() {
        userInfoHeaderLabel.setText(rb.getString(userInfoHeaderResourceKey));
        userGeneralInfoTitledPane.setText(rb.getString(userGeneralInfoResourceKey));
        firstNameLabel.setText(rb.getString(userFirstNameResourceKey));
        lastNameLabel.setText(rb.getString(userLastNameResourceKey));
        loginLabel.setText(rb.getString(userLoginResourceKey));
        contractNameLabel.setText(rb.getString(userContractNameResourceKey));
        roleNameLabel.setText(rb.getString(userRoleNameResourceKey));
        addressLabel.setText(rb.getString(userAddressResourceKey));
        
        userWorktimeInfoTitledPane.setText(rb.getString(userWorktimeInfoResourceKey));
        workhoursLabel.setText(rb.getString(workhoursResourceKey));
        maxWorkhoursLabel.setText(rb.getString(maxWorkhoursResourceKey));
        breakfastOfftimeStartLabel.setText(rb.getString(breakfastOfftimeStartResourceKey));
        breakfastOfftimeEndLabel.setText(rb.getString(breakfastOfftimeEndResourceKey));
        lunchOfftimeStartLabel.setText(rb.getString(lunchOfftimeStartResourceKey));
        lunchOfftimeEndLabel.setText(rb.getString(lunchOfftimeEndResourceKey));
        earliestWorktimeStartLabel.setText(rb.getString(earliestWorktimeStartResourceKey));
        latestWorktimeEndLabel.setText(rb.getString(latestWorktimeEndResourceKey));
        regularWorktimeCurrentMonthLabel.setText(rb.getString(regularWorktimeCurrentMonthResourceKey));
        worktimeCurrentMonthLabel.setText(rb.getString(worktimeCurrentMonthResourceKey));
        overtimeCurrentMonthLabel.setText(rb.getString(overtimeCurrentMonthResourceKey));
        overallOvertimeLabel.setText(rb.getString(overallOvertimeResourceKey));

        userVacationInfoTitledPane.setText(rb.getString(userVacationInfoResourceKey));
        vacationdaysLabel.setText(rb.getString(vacationdaysResourceKey));
        vacationReconciliationDateLabel.setText(rb.getString(vacationReconciliationDateResourceKey));
        vacationLeftLabel.setText(rb.getString(vacationLeftResourceKey));
        vacationDaysType.setText(rb.getString(vacationCorrectionTypeResourceKey));
        userVacationLeftType.setText(rb.getString(vacationCorrectionTypeResourceKey));
        
        userWorkdaysInfoTitledPane.setText(rb.getString(userWorkdaysInfoResourceKey));
        workdaysLabel.setText(rb.getString(workdaysResourceKey));  
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
    public void preCloseAction() {

    }
        
    public EventManager getEventManager() {
        return eventManager;
    }
    
    @Override
    public void update(String eventType, Object source) {
        User user = null;
        switch (eventType) {
            case userChangedEvent -> {
                user = (User)source;
            }
            case newWorkrecordEvent, editWorkrecordEvent, deleteWorkrecordEvent -> {
                try {
                    user = workRecordViewController.getSelectedUser();
                    refreshUserVacationInfos(user);
                } catch (SQLException ex) {
                    log.fatal("User vacation infos could not be refreshed");
                }
            }
            case newWorkLocationEvent, editWorkLocationEvent, deleteWorkLocationEvent -> {
                refreshWorkrecordLocationChoiceBox();
            }
            
        }
        refreshUserInfos(user);
        refreshWorkdaysLabelValue(user, (Worklocation)worklocationsChoiceBox.getValue());
    }

    private void refreshWorkrecordLocationChoiceBox() {
        try {
           Worklocation selectedWorklocation = (Worklocation)worklocationsChoiceBox.getSelectionModel().getSelectedItem();
            worklocationsChoiceBox.getItems().clear();
            worklocationsChoiceBox.getItems().addAll(worklocationDao.selectAll());
            worklocationsChoiceBox.getSelectionModel().select(selectedWorklocation);
        } catch (SQLException ex) {
            log.fatal("Worklocation choicebox could not be refreshed");
        }
    }
    
    private void refreshWorkdaysLabelValue(User user, Worklocation worklocation) {
        try {
            Long workdays = calculateDaysForWorklocation(user, LocalDate.now(), worklocation);
            workdaysLabelValue.setText(workdays.toString());
        } catch (SQLException ex) {
            log.error("Calculation of workdays for " + worklocation.getName() + " failed!");
        }
    }
    
    private void refreshUserInfos(User user) {
        try {
            refreshUserGeneralInfos(user);
            refreshUserWorktimeInfos(user);
            refreshUserVacationInfos(user);
        } catch (SQLException ex) {
            log.error("Refresh of user information failed!");
        }
    }
    
    private void refreshUserGeneralInfos(User user) {
        if(user != null) {
            firstNameLabelValue.setText(user.getFirstname());
            lastNameLabelValue.setText(user.getLastname());
            loginLabelValue.setText(user.getLogin());
            contractNameLabelValue.setText(user.getContract().getName());
            roleNameLabelValue.setText(user.getRole().getName());
            addressLabelValue.setText(formatAddressInfo(user.getAddress()));
        } else {
            firstNameLabelValue.setText("");
            lastNameLabelValue.setText("");
            loginLabelValue.setText("");
            contractNameLabelValue.setText("");
            roleNameLabelValue.setText("");
            addressLabelValue.setText("");
        }
    }

    private void refreshUserWorktimeInfos(User user) throws SQLException {
        if(user != null) {
            workhoursLabelValue.setText(user.getContract().getWorkhours().toString());
            maxWorkhoursLabelValue.setText(user.getContract().getMaxworkhours().toString());
            breakfastOfftimeStartLabelValue.setText(user.getContract().getBreakfastofftimestart().toString());
            breakfastOfftimeEndLabelValue.setText(user.getContract().getBreakfastofftimeend().toString());
            lunchOfftimeStartLabelValue.setText(user.getContract().getLunchofftimestart().toString());
            lunchOfftimeEndLabelValue.setText(user.getContract().getLunchofftimeend().toString());
            earliestWorktimeStartLabelValue.setText(user.getContract().getEarliestworktimestart().toString());
            latestWorktimeEndLabelValue.setText(user.getContract().getLatestworktimeend().toString());

            LocalDate now = LocalDate.now();
            YearMonth yearMonth = YearMonth.of(now.getYear(), now.getMonth());
            Long regularWorktimeCurrentMonth = calculateRegularWorktime(user, yearMonth);
            regularWorktimeCurrentMonthLabelValue.setText(regularWorktimeCurrentMonth.toString());

            Long worktimeCurrentMonth = calculateWorktimeCurrentMonth(user, LocalDate.now());
            worktimeCurrentMonthLabelValue.setText(TimeConverter.hoursAndMinutesToString(worktimeCurrentMonth));
            TimeStyler.styleTimeLabel(worktimeCurrentMonthLabelValue, worktimeCurrentMonth);

            Long overtimeCurrentMonth = calculateOvertimeCurrentMonth(user, LocalDate.now());
            overtimeCurrentMonthLabelValue.setText(TimeConverter.hoursAndMinutesToString(overtimeCurrentMonth));
            TimeStyler.styleTimeLabel(overtimeCurrentMonthLabelValue, overtimeCurrentMonth);

            Long overallOvertime = calculateOverallOvertime(user);
            overallOvertimeLabelValue.setText(TimeConverter.hoursAndMinutesToString(overallOvertime));
            String upperOvertimeThreshold = propertiesService.getProperty("UpperOvertimeThreshold", defaultOvertimeThreshold);
            Long upperOvertimeThresholdAsLong = Duration.parse(upperOvertimeThreshold).toMinutes();
            String lowerOvertimeThreshold = propertiesService.getProperty("LowerOvertimeThreshold", defaultOvertimeThreshold);
            Long lowerOvertimeThresholdAsLong = Duration.parse(lowerOvertimeThreshold).toMinutes();
            TimeStyler.styleTimeLabel(overallOvertimeLabelValue, overallOvertime, upperOvertimeThresholdAsLong, lowerOvertimeThresholdAsLong);
            if(overallOvertime < lowerOvertimeThresholdAsLong) {
                String formattedLowerOvertimeThreshold = DurationConverter.convertDurationStringToSignedStringOfHoursAndMinutes(lowerOvertimeThreshold);
                String formatedThresholdTooltipMsg = MessageFormat.format(rb.getString(thresholdExceededResourceKey), formattedLowerOvertimeThreshold);
                overallOvertimeLabelValue.setTooltip(new Tooltip(formatedThresholdTooltipMsg));
                overallOvertimeImageView.setVisible(true);
                log.warn(formatedThresholdTooltipMsg);
            } else if(overallOvertime > upperOvertimeThresholdAsLong) {
                String formattedUpperOvertimeThreshold = DurationConverter.convertDurationStringToSignedStringOfHoursAndMinutes(upperOvertimeThreshold);
                String formatedThresholdTooltipMsg = MessageFormat.format(rb.getString(thresholdExceededResourceKey), formattedUpperOvertimeThreshold);
                overallOvertimeLabelValue.setTooltip(new Tooltip(formatedThresholdTooltipMsg));
                overallOvertimeImageView.setVisible(true);
                log.warn(formatedThresholdTooltipMsg);
            } else {
                overallOvertimeLabelValue.setTooltip(null);
                overallOvertimeImageView.setVisible(false);
            }
        } else {
            workhoursLabelValue.setText("");
            maxWorkhoursLabelValue.setText("");
            breakfastOfftimeStartLabelValue.setText("");
            breakfastOfftimeEndLabelValue.setText("");
            lunchOfftimeStartLabelValue.setText("");
            lunchOfftimeEndLabelValue.setText("");
            earliestWorktimeStartLabelValue.setText("");
            latestWorktimeEndLabelValue.setText("");
            regularWorktimeCurrentMonthLabelValue.setText("");
            worktimeCurrentMonthLabelValue.setText("");
            overtimeCurrentMonthLabelValue.setText("");            
        }
    }
    
    private void refreshUserVacationInfos(User user) throws SQLException {
        if(user != null) {
            String vacationReconciliationDateAsString = user.getContract().getVacationreconciliationdate();
            String[] dateParts = vacationReconciliationDateAsString.split("\\.");
            int dayPart = Integer.parseInt(dateParts[0]);
            int monthPart = Integer.parseInt(dateParts[1]);
            int yearPart = LocalDate.now().getYear();
            LocalDate vacationReconciliationDate = LocalDate.of(yearPart, monthPart, dayPart);
            
            LocalDate today = LocalDate.now();
            vacationdaysLabelValue.setText(user.getContract().getVacationdays().toString());
            vacationReconciliationDateLabelValue.setText(vacationReconciliationDateAsString);
             
            Long vacationLeftLastYear = calculateVacationLeft(user, today.minusYears(1));
            Long vacationLeftCurrentYear = calculateVacationLeft(user, today);
            
            if(today.isBefore(vacationReconciliationDate)) {
                Long vacationLeftWithLastYear = vacationLeftLastYear + vacationLeftCurrentYear;
                vacationLeftLabelValue.setText(vacationLeftWithLastYear.toString());
            } else {
                vacationLeftLabelValue.setText(vacationLeftCurrentYear.toString());            
            }
        } else {
            vacationdaysLabelValue.setText("");
            vacationReconciliationDateLabelValue.setText("");
            vacationLeftLabelValue.setText("");
        }
    }
    
    private String formatAddressInfo(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address.getStreetname()).append(" ");
        sb.append(address.getHousenumber()).append("\n");
        sb.append(address.getUnitnumber()).append(" ");
        sb.append(address.getUnitname()).append(" ");
        sb.append(address.getUnitlocation()).append("\n");
        sb.append(address.getZipcode()).append(" ");
        sb.append(address.getCity()).append("\n");
        sb.append(address.getCountry()).append("\\");
        sb.append(address.getState());
        return sb.toString();
    }
    
    private Long calculateRegularWorktime(User user, YearMonth yearMonth) {
        int workDaysOfMonth = 0;
        
        for(int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day);
            
            if(DateChecker.isHolyday(holydayData, date) || DateChecker.isWeekend(date)) {
                continue;
            }
            
            workDaysOfMonth += 1;
        }
        
        Long workHours = user.getContract().getWorkhours();
        
        return workDaysOfMonth * workHours;
    }

    private Long calculateWorktimeCurrentMonth(User user, LocalDate localDate) throws SQLException {
        Long worktimeCurrentMonth = 0L;
        
        int year = localDate.getYear();
        Month month = localDate.getMonth();
        int lastDayOfMonth = localDate.lengthOfMonth();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, lastDayOfMonth);

        for(Workrecord workrecord : workrecordDao.selectAll(user, startDate, endDate)) {
            if("true".equals(workrecord.getProject().getIsWorktimeRelevant().toLowerCase())) {
                worktimeCurrentMonth += TimeConverter.hoursAndMinutesToLong(workrecord.getWorktime().toString());
            }            
        }
        
        return worktimeCurrentMonth;
    }

    private Long calculateOvertimeCurrentMonth(User user, LocalDate localDate) throws SQLException {
        Long overtimeCurrentMonth = 0L;
        
        int year = localDate.getYear();
        Month month = localDate.getMonth();
        int lastDayOfMonth = localDate.lengthOfMonth();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, lastDayOfMonth);

        for(Workrecord workrecord : workrecordDao.selectAll(user, startDate, endDate)) {
            if("true".equals(workrecord.getProject().getIsWorktimeRelevant().toLowerCase())) {
                overtimeCurrentMonth += TimeConverter.hoursAndMinutesToLong(workrecord.getOvertime());
            }
        }

        return overtimeCurrentMonth;
    }

    private Long calculateOverallOvertime(User user) throws SQLException {
        Long overallOvertime = 0L;
        
        for(Workrecord workrecord : workrecordDao.selectAll(user)) {
            if("true".equals(workrecord.getProject().getIsWorktimeRelevant().toLowerCase())) {
                String overtime = workrecord.getOvertime();
                String overtimeCorrection = workrecord.getOvertimecorrection();
                overallOvertime += TimeConverter.hoursAndMinutesToLong(workrecord.getOvertime());
                overallOvertime += TimeConverter.hoursAndMinutesToLong(workrecord.getOvertimecorrection());
            }
        }
        
        return overallOvertime;
    }
        
    private Long calculateVacationLeft(User user, LocalDate localDate) throws SQLException {
        Long vacationLeft = user.getContract().getVacationdays();

        int year = localDate.getYear();
        LocalDate startDate = LocalDate.of(year, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(year, Month.DECEMBER, 31);

        for(Workrecord workrecord : workrecordDao.selectAll(user, startDate, endDate)) {
            vacationLeft += workrecord.getVacationcorrection();
            if("true".equals(workrecord.getProject().getIsVacationRelevant().toLowerCase())) {
                vacationLeft -= 1L;
                if(vacationLeft < 0L) {
                    log.error("Vacation left can not be negative!");
                }
            }
        }
        
        return vacationLeft;
    }
    
    private Long calculateDaysForWorklocation(User user, LocalDate localDate, Worklocation worklocation) throws SQLException {
        Long workdays = 0L;
        int year = localDate.getYear();
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        if(user != null && worklocation != null) {
            //Remove duplicates workrecords with same date from list.
            HashSet<LocalDate> duplicate = new HashSet<>();
            List<Workrecord> workrecords = workrecordDao.selectAll(user, startDate, endDate);
            workrecords.removeIf(e -> !duplicate.add(e.getDate()));

            for(Workrecord workrecord : workrecords) {
                Worklocation workrecordWorklocation = workrecord.getWorklocation();
                if(worklocation.equals(workrecordWorklocation)) {
                    workdays += 1;   
                }
            }
        }
        return workdays;
    }
    
    private void restoreLastSelectedUserInfoPane() {
        String lastSelectedUserInfoExpandedPane = propertiesService.getProperty(userInfoExpandedPaneResourceKey, "UserWorktimelInfoPane");
        switch(lastSelectedUserInfoExpandedPane) {
            case "UserGeneralInfoPane" -> {
                userAccordion.setExpandedPane(userGeneralInfoTitledPane);
            }
            case "UserWorktimelInfoPane" -> {
                userAccordion.setExpandedPane(userWorktimeInfoTitledPane);
            }
            case "UserVacationInfoPane" -> {
                userAccordion.setExpandedPane(userVacationInfoTitledPane);
            }
            case "UserWorkdaysInfoPane" -> {
                userAccordion.setExpandedPane(userWorkdaysInfoTitledPane);
            }
        }
    }
    
    private void saveLastSelectedUserInfoPane(TitledPane newValue) {
        if(newValue != null) {
            propertiesService.setProperty(userInfoExpandedPaneResourceKey, newValue.getId());
        }
    }
    
    private void setLastSelectedWorkLocation(ChoiceBox<Worklocation> selectedWorklocationChoiceBox) {
        String lastSelectedWorklocation = propertiesService.getProperty(selectedWorklocationResourceKey);
        ObservableList<Worklocation> worklocationList = worklocationsChoiceBox.getItems();
        for(Worklocation worklocation : worklocationList) {
            if(worklocation.getName().equals(lastSelectedWorklocation)) {
                selectedWorklocationChoiceBox.getSelectionModel().select(worklocation);
            }
        }        
    }
    
}
