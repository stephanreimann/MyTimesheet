/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import adapter.Log4jAdapter;
import java.sql.Connection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Sprint;
import model.User;
import model.Worklocation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LanguageService;
import service.PropertiesService;
import service.UndoService;
import sqlite.ConnectionFactory;
import testhelper.ReflectionHelper;
import testhelper.StubFactory;

import static org.junit.Assert.*;

public class UserInfoViewControllerTest {

    private final static String DATABASE_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/sqlite/testdb.sqlite";
    private final static String LOG4J2_PATH_AND_FULL_NAME = System.getProperty("user.dir") + "/log4j2.xml";

    private StubFactory stubFactory;
    private ReflectionHelper reflectionHelper;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private ResourceBundle resourceBundle;
    private PropertiesService propertiesService;
    private LanguageService languageService;
    private UndoService undoService;
    private Log4jAdapter log4jAdapter;
    private ControllerRepository controllerRepository;

    private UserInfoViewController controller;

    private Label userInfoHeaderLabel;
    private Accordion userAccordion;
    private TitledPane userGeneralInfoTitledPane;
    private Label firstNameLabel;
    private Label firstNameLabelValue;
    private Label lastNameLabel;
    private Label lastNameLabelValue;
    private Label loginLabel;
    private Label loginLabelValue;
    private Label contractNameLabel;
    private Label contractNameLabelValue;
    private Label roleNameLabel;
    private Label roleNameLabelValue;
    private Label addressLabel;
    private Label addressLabelValue;

    private GridPane userWorktimeInfoGridPane;
    private TitledPane userWorktimeInfoTitledPane;
    private Label workhoursLabel;
    private Label workhoursLabelValue;
    private Label maxWorkhoursLabel;
    private Label maxWorkhoursLabelValue;
    private Label breakfastOfftimeStartLabel;
    private Label breakfastOfftimeStartLabelValue;
    private Label breakfastOfftimeEndLabel;
    private Label breakfastOfftimeEndLabelValue;
    private Label lunchOfftimeStartLabel;
    private Label lunchOfftimeStartLabelValue;
    private Label lunchOfftimeEndLabel;
    private Label lunchOfftimeEndLabelValue;
    private Label earliestWorktimeStartLabel;
    private Label earliestWorktimeStartLabelValue;
    private Label latestWorktimeEndLabel;
    private Label latestWorktimeEndLabelValue;
    private Label regularWorktimeCurrentMonthLabel;
    private Label regularWorktimeCurrentMonthLabelValue;
    private Label worktimeCurrentMonthLabel;
    private Label worktimeCurrentMonthLabelValue;
    private Label overtimeCurrentMonthLabel;
    private Label overtimeCurrentMonthLabelValue;
    private Label overallOvertimeLabel;
    private Label overallOvertimeLabelValue;
    private ImageView overallOvertimeImageView;

    private GridPane userVacationInfoGridPane;
    private TitledPane userVacationInfoTitledPane;
    private Label vacationdaysLabel;
    private Label vacationDaysType;
    private Label vacationReconciliationDateLabel;
    private Label vacationdaysLabelValue;
    private Label vacationReconciliationDateLabelValue;
    private Label vacationLeftLabel;
    private Label vacationLeftLabelValue;
    private Label userVacationLeftType;

    private TitledPane userWorkdaysInfoTitledPane;
    private ChoiceBox<Worklocation> worklocationsChoiceBox;
    private Label workdaysLabel;
    private Label workdaysLabelValue;

    private TitledPane userWorkitemsInfoTitledPane;
    private Label sprintChoiceBoxLabel;
    private ChoiceBox<Sprint> sprintChoiceBox;
    private TableView<String[]> trackingItemTableView;
    private TableColumn<String[], String> trackingItemShortcutTableColumn;
    private TableColumn<String[], String> trackingItemNameTableColumn;
    private TableColumn<String[], String> trackingItemTimeTableColumn;

    @BeforeClass
    public static void initJfx() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        latch.await();
    }

    @Before
    public void setUp() throws Exception {
        stubFactory = new StubFactory();
        reflectionHelper = new ReflectionHelper();

        languageService = stubFactory.createLanguageServiceStub();
        undoService = new UndoService();
        propertiesService = PropertiesService.getInstance();
        controllerRepository = ControllerRepository.getInstance();

        // Custom ResourceBundle stub returning keys for lookup
        resourceBundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return key;
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.emptyEnumeration();
            }

            @Override
            public Locale getLocale() {
                return Locale.ENGLISH;
            }
        };

        log4jAdapter = new Log4jAdapter(LOG4J2_PATH_AND_FULL_NAME);
        connectionFactory = new ConnectionFactory(resourceBundle, log4jAdapter);
        connection = connectionFactory.getConnection(DATABASE_PATH_AND_FULL_NAME);

        // Create and set up WorkRecordViewController stub with injected ComboBox to avoid NPE
        WorkRecordViewController workRecordViewControllerStub = new WorkRecordViewController(
                languageService, connection, undoService, propertiesService
        );
        ComboBox<User> dummyComboBox = new ComboBox<>();
        reflectionHelper.setField(workRecordViewControllerStub, "selectedUserComboBox", dummyComboBox);
        controllerRepository.put(WorkRecordViewController.class.getName(), workRecordViewControllerStub);

        controller = new UserInfoViewController(controllerRepository, languageService, connection, undoService, propertiesService);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.setPrimaryStage(new Stage());
                setupUiControls();
                injectUiFields();
                controller.initialize(null, resourceBundle);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
    
    private void setupUiControls() {
        userInfoHeaderLabel = new Label();
        userAccordion = new Accordion();
        userGeneralInfoTitledPane = new TitledPane();
        firstNameLabel = new Label();
        firstNameLabelValue = new Label();
        lastNameLabel = new Label();
        lastNameLabelValue = new Label();
        loginLabel = new Label();
        loginLabelValue = new Label();
        contractNameLabel = new Label();
        contractNameLabelValue = new Label();
        roleNameLabel = new Label();
        roleNameLabelValue = new Label();
        addressLabel = new Label();
        addressLabelValue = new Label();

        userWorktimeInfoGridPane = new GridPane();
        userWorktimeInfoTitledPane = new TitledPane();
        workhoursLabel = new Label();
        workhoursLabelValue = new Label();
        maxWorkhoursLabel = new Label();
        maxWorkhoursLabelValue = new Label();
        breakfastOfftimeStartLabel = new Label();
        breakfastOfftimeStartLabelValue = new Label();
        breakfastOfftimeEndLabel = new Label();
        breakfastOfftimeEndLabelValue = new Label();
        lunchOfftimeStartLabel = new Label();
        lunchOfftimeStartLabelValue = new Label();
        lunchOfftimeEndLabel = new Label();
        lunchOfftimeEndLabelValue = new Label();
        earliestWorktimeStartLabel = new Label();
        earliestWorktimeStartLabelValue = new Label();
        latestWorktimeEndLabel = new Label();
        latestWorktimeEndLabelValue = new Label();
        regularWorktimeCurrentMonthLabel = new Label();
        regularWorktimeCurrentMonthLabelValue = new Label();
        worktimeCurrentMonthLabel = new Label();
        worktimeCurrentMonthLabelValue = new Label();
        overtimeCurrentMonthLabel = new Label();
        overtimeCurrentMonthLabelValue = new Label();
        overallOvertimeLabel = new Label();
        overallOvertimeLabelValue = new Label();
        overallOvertimeImageView = new ImageView();

        userVacationInfoGridPane = new GridPane();
        userVacationInfoTitledPane = new TitledPane();
        vacationdaysLabel = new Label();
        vacationDaysType = new Label();
        vacationReconciliationDateLabel = new Label();
        vacationdaysLabelValue = new Label();
        vacationReconciliationDateLabelValue = new Label();
        vacationLeftLabel = new Label();
        vacationLeftLabelValue = new Label();
        userVacationLeftType = new Label();

        userWorkdaysInfoTitledPane = new TitledPane();
        worklocationsChoiceBox = new ChoiceBox<>();
        workdaysLabel = new Label();
        workdaysLabelValue = new Label();

        userWorkitemsInfoTitledPane = new TitledPane();
        sprintChoiceBoxLabel = new Label();
        sprintChoiceBox = new ChoiceBox<>();
        trackingItemTableView = new TableView<>();
        trackingItemShortcutTableColumn = new TableColumn<>();
        trackingItemNameTableColumn = new TableColumn<>();
        trackingItemTimeTableColumn = new TableColumn<>();
    }

    private void injectUiFields() throws Exception {
        reflectionHelper.setField(controller, "userInfoHeaderLabel", userInfoHeaderLabel);
        reflectionHelper.setField(controller, "userAccordion", userAccordion);
        reflectionHelper.setField(controller, "userGeneralInfoTitledPane", userGeneralInfoTitledPane);
        reflectionHelper.setField(controller, "firstNameLabel", firstNameLabel);
        reflectionHelper.setField(controller, "firstNameLabelValue", firstNameLabelValue);
        reflectionHelper.setField(controller, "lastNameLabel", lastNameLabel);
        reflectionHelper.setField(controller, "lastNameLabelValue", lastNameLabelValue);
        reflectionHelper.setField(controller, "loginLabel", loginLabel);
        reflectionHelper.setField(controller, "loginLabelValue", loginLabelValue);
        reflectionHelper.setField(controller, "contractNameLabel", contractNameLabel);
        reflectionHelper.setField(controller, "contractNameLabelValue", contractNameLabelValue);
        reflectionHelper.setField(controller, "roleNameLabel", roleNameLabel);
        reflectionHelper.setField(controller, "roleNameLabelValue", roleNameLabelValue);
        reflectionHelper.setField(controller, "addressLabel", addressLabel);
        reflectionHelper.setField(controller, "addressLabelValue", addressLabelValue);

        reflectionHelper.setField(controller, "userWorktimeInfoGridPane", userWorktimeInfoGridPane);
        reflectionHelper.setField(controller, "userWorktimeInfoTitledPane", userWorktimeInfoTitledPane);
        reflectionHelper.setField(controller, "workhoursLabel", workhoursLabel);
        reflectionHelper.setField(controller, "workhoursLabelValue", workhoursLabelValue);
        reflectionHelper.setField(controller, "maxWorkhoursLabel", maxWorkhoursLabel);
        reflectionHelper.setField(controller, "maxWorkhoursLabelValue", maxWorkhoursLabelValue);
        reflectionHelper.setField(controller, "breakfastOfftimeStartLabel", breakfastOfftimeStartLabel);
        reflectionHelper.setField(controller, "breakfastOfftimeStartLabelValue", breakfastOfftimeStartLabelValue);
        reflectionHelper.setField(controller, "breakfastOfftimeEndLabel", breakfastOfftimeEndLabel);
        reflectionHelper.setField(controller, "breakfastOfftimeEndLabelValue", breakfastOfftimeEndLabelValue);
        reflectionHelper.setField(controller, "lunchOfftimeStartLabel", lunchOfftimeStartLabel);
        reflectionHelper.setField(controller, "lunchOfftimeStartLabelValue", lunchOfftimeStartLabelValue);
        reflectionHelper.setField(controller, "lunchOfftimeEndLabel", lunchOfftimeEndLabel);
        reflectionHelper.setField(controller, "lunchOfftimeEndLabelValue", lunchOfftimeEndLabelValue);
        reflectionHelper.setField(controller, "earliestWorktimeStartLabel", earliestWorktimeStartLabel);
        reflectionHelper.setField(controller, "earliestWorktimeStartLabelValue", earliestWorktimeStartLabelValue);
        reflectionHelper.setField(controller, "latestWorktimeEndLabel", latestWorktimeEndLabel);
        reflectionHelper.setField(controller, "latestWorktimeEndLabelValue", latestWorktimeEndLabelValue);
        reflectionHelper.setField(controller, "regularWorktimeCurrentMonthLabel", regularWorktimeCurrentMonthLabel);
        reflectionHelper.setField(controller, "regularWorktimeCurrentMonthLabelValue", regularWorktimeCurrentMonthLabelValue);
        reflectionHelper.setField(controller, "worktimeCurrentMonthLabel", worktimeCurrentMonthLabel);
        reflectionHelper.setField(controller, "worktimeCurrentMonthLabelValue", worktimeCurrentMonthLabelValue);
        reflectionHelper.setField(controller, "overtimeCurrentMonthLabel", overtimeCurrentMonthLabel);
        reflectionHelper.setField(controller, "overtimeCurrentMonthLabelValue", overtimeCurrentMonthLabelValue);
        reflectionHelper.setField(controller, "overallOvertimeLabel", overallOvertimeLabel);
        reflectionHelper.setField(controller, "overallOvertimeLabelValue", overallOvertimeLabelValue);
        reflectionHelper.setField(controller, "overallOvertimeImageView", overallOvertimeImageView);

        reflectionHelper.setField(controller, "userVacationInfoGridPane", userVacationInfoGridPane);
        reflectionHelper.setField(controller, "userVacationInfoTitledPane", userVacationInfoTitledPane);
        reflectionHelper.setField(controller, "vacationdaysLabel", vacationdaysLabel);
        reflectionHelper.setField(controller, "vacationDaysType", vacationDaysType);
        reflectionHelper.setField(controller, "vacationReconciliationDateLabel", vacationReconciliationDateLabel);
        reflectionHelper.setField(controller, "vacationdaysLabelValue", vacationdaysLabelValue);
        reflectionHelper.setField(controller, "vacationReconciliationDateLabelValue", vacationReconciliationDateLabelValue);
        reflectionHelper.setField(controller, "vacationLeftLabel", vacationLeftLabel);
        reflectionHelper.setField(controller, "vacationLeftLabelValue", vacationLeftLabelValue);
        reflectionHelper.setField(controller, "userVacationLeftType", userVacationLeftType);

        reflectionHelper.setField(controller, "userWorkdaysInfoTitledPane", userWorkdaysInfoTitledPane);
        reflectionHelper.setField(controller, "worklocationsChoiceBox", worklocationsChoiceBox);
        reflectionHelper.setField(controller, "workdaysLabel", workdaysLabel);
        reflectionHelper.setField(controller, "workdaysLabelValue", workdaysLabelValue);

        reflectionHelper.setField(controller, "userWorkitemsInfoTitledPane", userWorkitemsInfoTitledPane);
        reflectionHelper.setField(controller, "sprintChoiceBoxLabel", sprintChoiceBoxLabel);
        reflectionHelper.setField(controller, "sprintChoiceBox", sprintChoiceBox);
        reflectionHelper.setField(controller, "trackingItemTableView", trackingItemTableView);
        reflectionHelper.setField(controller, "trackingItemShortcutTableColumn", trackingItemShortcutTableColumn);
        reflectionHelper.setField(controller, "trackingItemNameTableColumn", trackingItemNameTableColumn);
        reflectionHelper.setField(controller, "trackingItemTimeTableColumn", trackingItemTimeTableColumn);
    }

    @After
    public void tearDown() {
        // Cleanup if needed
    }

    // --- Constructor & Validation Tests ---

    @Test(expected = NullPointerException.class)
    public void constructor_NullControllerRepository_ThrowsNPE() throws Exception {
        var _ = new UserInfoViewController(null, languageService, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullLanguageService_ThrowsNPE() throws Exception {
        var _ = new UserInfoViewController(controllerRepository, null, connection, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullConnection_ThrowsNPE() throws Exception {
        var _ = new UserInfoViewController(controllerRepository, languageService, null, undoService, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullUndoService_ThrowsNPE() throws Exception {
        var _ = new UserInfoViewController(controllerRepository, languageService, connection, null, propertiesService);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_NullPropertiesService_ThrowsNPE() throws Exception {
        var _ = new UserInfoViewController(controllerRepository, languageService, connection, undoService, null);
    }

    // --- Initialization & GUI Tests ---

    @Test
    public void initialize_ValidResourceBundle_StoresBundleAndInitializes() {
        controller.initialize(null, resourceBundle);
        assertEquals(resourceBundle, controller.getResourceBundle());
        assertNotNull(controller.getEventManager());
    }

    @Test
    public void updateGuiItems_UpdatesComponentsCorrectly() {
        controller.setResourceBundle(resourceBundle);
        controller.updateGuiItems();

        assertEquals("UserInfoHeader", userInfoHeaderLabel.getText());
        assertEquals("UserGeneralInfo", userGeneralInfoTitledPane.getText());
        assertEquals("UserFirstName", firstNameLabel.getText());
        assertEquals("UserLastName", lastNameLabel.getText());
        assertEquals("UserLogin", loginLabel.getText());
        assertEquals("UserContractName", contractNameLabel.getText());
        assertEquals("UserRoleName", roleNameLabel.getText());
        assertEquals("UserAddress", addressLabel.getText());
        assertEquals("UserWorktimeInfo", userWorktimeInfoTitledPane.getText());
        assertEquals("Workhours", workhoursLabel.getText());
        assertEquals("MaxWorkhours", maxWorkhoursLabel.getText());
        assertEquals("UserVacationInfo", userVacationInfoTitledPane.getText());
        assertEquals("Vacationdays", vacationdaysLabel.getText());
        assertEquals("UserWorkdaysInfo", userWorkdaysInfoTitledPane.getText());
        assertEquals("Workdays", workdaysLabel.getText());
        assertEquals("UserWorkitemsInfo", userWorkitemsInfoTitledPane.getText());
        assertEquals("Sprint", sprintChoiceBoxLabel.getText());
    }

    @Test
    public void preCloseAction_ExecutesSafely() {
        controller.preCloseAction();
        assertTrue(true);
    }
}