/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.UserViewController.DataAction;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.*;
import sqlite.*;
import utils.ControllerUtilities;
import utils.Encryptor;

/**
 *
 * @author adrest18
 */
public class UserDetailsViewController implements Initializable, IViewController {

    private final String hidePasswordIcon = "icons/hide.png";
    private final String showPasswordIcon = "icons/show.png";
    
    private final String hidePasswordResourceKey = "HidePassword";
    private final String showPasswordResourceKey = "ShowPassword";
    private final String userFirstNameResourceKey = "UserFirstName";
    private final String userLastNameResourceKey = "UserLastName";
    private final String userLoginResourceKey = "UserLogin";
    private final String userPasswordResourceKey ="UserPassword";
    private final String userVacationLeftResourceKey = "UserVacationLeft";
    private final String userVacationLeftUnitResourceKey = "Days";
    private final String userContractNameResourceKey = "UserContractName";
    private final String userRoleNameResourceKey = "UserRoleName";
    private final String userAddressResourceKey = "UserAddress";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";
    
    private final Logger log = LogManager.getLogger(UserDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private User user;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final RoleDAO roleDao;
    private final AddressDAO addressDao;
    private final ContractDAO contractDao;
    private final ObservableList<User> userData;
    private DataAction dataAction;
    
    private String oldUserFirstName;
    private String newUserFirstName;
    private String oldUserLastName;
    private String newUserLastName;
    private String oldUserLogin;
    private String newUserLogin;
    private String oldUserPassword;
    private String newUserPassword;
    private String oldUserVacationleft;
    private String newUserVacationleft;
    private Contract oldUserContract;
    private Contract newUserContract;
    private Role oldUserRole;
    private Role newUserRole;
    private Address oldUserAddress;
    private Address newUserAddress;
    
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label loginLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label vacationLeftLabel;
    @FXML
    private Label contractNameLabel;
    @FXML
    private Label roleNameLabel;
    @FXML
    private Label addressLabel;
    
    @FXML
    private TextField firstNameTextFieldValue;
    @FXML
    private TextField lastNameTextFieldValue;
    @FXML
    private TextField loginTextFieldValue;
    @FXML
    private PasswordField hiddenPasswordFieldValue;
    @FXML
    private TextField shownPasswordFieldValue;
    @FXML 
    private ToggleButton togglePasswordButton;
    @FXML
    private TextField vacationLeftTextFieldValue;
    @FXML
    private Label vacationLeftUnitLabel;
    @FXML
    private ChoiceBox<Contract> contractChoiceBoxValue;
    @FXML
    private ChoiceBox<Role> roleChoiceBoxValue;
    @FXML
    private ChoiceBox<Address> addressChoiceBoxValue;
    
    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    public UserDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<User> userData) throws SQLException {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(userData == null) throw new NullPointerException("userData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.userData = userData;
        this.roleDao = new RoleDAO(connection);
        this.addressDao = new AddressDAO(connection);
        this.contractDao = new ContractDAO(connection);
    }
    
    @FXML
    private void acceptAction(ActionEvent event) {
        user.setFirstname(firstNameTextFieldValue.getText());
        user.setLastname(lastNameTextFieldValue.getText());
        user.setLogin(loginTextFieldValue.getText());
        
        Encryptor encryptor = new Encryptor();
        user.setPassword(encryptor.encrypt(hiddenPasswordFieldValue.getText()));

        user.setVacationleft(Long.valueOf(vacationLeftTextFieldValue.getText()));
        user.setContract(contractChoiceBoxValue.getSelectionModel().getSelectedItem());
        user.setRole(roleChoiceBoxValue.getSelectionModel().getSelectedItem());
        user.setAddress(addressChoiceBoxValue.getSelectionModel().getSelectedItem());

        primaryStage.close();
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        primaryStage.close();
    }
      
    @FXML
    private void togglePasswordVisibilityAction(ActionEvent event) {
        if (togglePasswordButton.isSelected()) {
            shownPasswordFieldValue.setVisible(true);
            
            Encryptor encryptor = new Encryptor();
            shownPasswordFieldValue.setTooltip(new Tooltip(encryptor.decrypt(shownPasswordFieldValue.getText())));
            
            hiddenPasswordFieldValue.setVisible(false);
            togglePasswordButton.setGraphic(new ImageView(hidePasswordIcon));
            togglePasswordButton.setTooltip(new Tooltip(rb.getString(hidePasswordResourceKey)));
        } else {
            hiddenPasswordFieldValue.setVisible(true);
            shownPasswordFieldValue.setVisible(false);
            togglePasswordButton.setGraphic(new ImageView(showPasswordIcon));
            togglePasswordButton.setTooltip(new Tooltip(rb.getString(showPasswordResourceKey)));
        }
    }
    
    @FXML
    private void decryptPasswordAction(ActionEvent event) {
        
    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        
        acceptButton.setDisable(true);
        togglePasswordVisibilityAction(null);
        
        firstNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newUserFirstName = newValue;
            isInputValid();
        });        
        lastNameTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newUserLastName = newValue;
            isInputValid();
        });        
        loginTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newUserLogin = newValue;
            isInputValid();
        });        
        hiddenPasswordFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newUserPassword = newValue;
            isInputValid();
        });
        shownPasswordFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newUserPassword = newValue;
            isInputValid();
        });
        vacationLeftTextFieldValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newUserVacationleft = newValue;
            validateNumberInput(newValue, vacationLeftTextFieldValue);
            isInputValid();
        });        
        contractChoiceBoxValue.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Contract> observable, Contract oldValue, Contract newValue) -> {
            newUserContract = newValue;
            isInputValid();
        });
        roleChoiceBoxValue.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Role> observable, Role oldValue, Role newValue) -> {
            newUserRole = newValue;
            isInputValid();
        });
        addressChoiceBoxValue.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Address> observable, Address oldValue, Address newValue) -> {
            newUserAddress = newValue;
            isInputValid();
        });
    }

    @Override
    public void updateGuiItems() {
        firstNameLabel.setText(rb.getString(userFirstNameResourceKey));
        lastNameLabel.setText(rb.getString(userLastNameResourceKey));
        loginLabel.setText(rb.getString(userLoginResourceKey));
        passwordLabel.setText(rb.getString(userPasswordResourceKey));
        vacationLeftLabel.setText(rb.getString(userVacationLeftResourceKey));
        vacationLeftUnitLabel.setText(rb.getString(userVacationLeftUnitResourceKey));
        contractNameLabel.setText(rb.getString(userContractNameResourceKey));
        roleNameLabel.setText(rb.getString(userRoleNameResourceKey));
        addressLabel.setText(rb.getString(userAddressResourceKey));
        acceptButton.setText(rb.getString(acceptResourceKey));
        cancelButton.setText(rb.getString(cancelResourceKey));
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
        
   public void setAction(DataAction action) {
        this.dataAction = action;
    }
        
    public void showUserDetails(User user) throws SQLException {
        this.user = user;

        //We save the actual user information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualUserInformation(user);
        
        contractChoiceBoxValue.getItems().addAll(contractDao.selectAll());
        roleChoiceBoxValue.getItems().addAll(roleDao.selectAll());
        addressChoiceBoxValue.getItems().addAll(addressDao.selectAll());

        firstNameTextFieldValue.setText(user.getFirstname());
        lastNameTextFieldValue.setText(user.getLastname());
        loginTextFieldValue.setText(user.getLogin());        
        hiddenPasswordFieldValue.setText(user.getPassword());
        shownPasswordFieldValue.setText(user.getPassword());
        
        if(isNewUser(user)) {
            vacationLeftTextFieldValue.setText("");
        } else {
            vacationLeftTextFieldValue.setText(user.getVacationleft().toString());
        }
        contractChoiceBoxValue.setValue(user.getContract());
        roleChoiceBoxValue.setValue(user.getRole());
        addressChoiceBoxValue.setValue(user.getAddress());
    }

    private void saveActualUserInformation(User user) {
        oldUserFirstName = user.getFirstname();
        oldUserLastName = user.getLastname();
        oldUserLogin = user.getLogin();
        oldUserPassword = user.getPassword();
        oldUserVacationleft = user.getVacationleft().toString();
        oldUserContract = user.getContract();
        oldUserRole = user.getRole();
        oldUserAddress = user.getAddress();
    }
    
    private boolean isNewUser(User user) {
        return (user.getFirstname() == null &&
            user.getLastname() == null &&
            user.getLogin() == null &&
            user.getPassword() == null &&
            user.getVacationleft() == 0 && 
            user.getContract() == null &&
            user.getRole() == null &&
            user.getAddress() == null);
    }

    private void validateNumberInput(String newValue, TextField textField) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
       
    // <editor-fold defaultstate="collapsed" desc="Input Validation Rules">
    private boolean isInputValid() {
        boolean result = false;

        switch(dataAction) {
            case DataAction.NEW -> {
                boolean r1 = isInputFilled();
                boolean r2 = isInputUnique();
                
                result = r1 && r2;
            }
            case DataAction.EDIT -> {
                boolean r1 = isInputFilled();
                boolean r2 = hasInputChanged();

                result = r1 && r2;       
            }

        }
        
        if(result) {
            acceptButton.setDisable(false);
            return true;
        } else {
            acceptButton.setDisable(true);
            return false;
        }        
    }

    private boolean isInputFilled() {
        boolean r1 = isFirstNameFilled(firstNameTextFieldValue);
        boolean r2 = isLastNameFilled(lastNameTextFieldValue);
        boolean r3 = isLoginFilled(loginTextFieldValue);
        boolean r4 = isPasswordFilled(hiddenPasswordFieldValue);
        boolean r5 = isPasswordFilled(shownPasswordFieldValue);
        boolean r6 = isVacationLeftFilled(vacationLeftTextFieldValue);
        boolean r7 = isContractFilled(contractChoiceBoxValue);
        boolean r8 = isRoleFilled(roleChoiceBoxValue);
        boolean r9 = isAddressFilled(addressChoiceBoxValue);
        
        boolean result = r1 && r2 && r3 && (r4 || r5) && r6 && r7 && r8 && r9;
        
        return result;
    }
    
    private boolean isFirstNameFilled(TextField firstName) {
        return !ControllerUtilities.isNullOrEmpty(firstName.getText());
    }
    
    private boolean isLastNameFilled(TextField lastName) {
        return !ControllerUtilities.isNullOrEmpty(lastName.getText());
    }

    private boolean isLoginFilled(TextField login) {
        return !ControllerUtilities.isNullOrEmpty(login.getText());
    }
    
    private boolean isPasswordFilled(TextField password) {
        return !ControllerUtilities.isNullOrEmpty(password.getText());
    }

    private boolean isVacationLeftFilled(TextField vacationLeft) {
        return vacationLeft.getText().matches("^(\\d|[12]\\d|3[00])$");
    }

    private boolean isContractFilled(ChoiceBox<Contract> contract) {
        var selectedItem = contract.getSelectionModel().getSelectedItem();
        return selectedItem != null;
    }

    private boolean isRoleFilled(ChoiceBox<Role> role) {
        var selectedItem = role.getSelectionModel().getSelectedItem();
        return selectedItem != null;
    }

    private boolean isAddressFilled(ChoiceBox<Address> address) {
        var selectedItem = address.getSelectionModel().getSelectedItem();
        return selectedItem != null;
    }

    private boolean isInputUnique() {
        boolean r1 = isLoginUnique(loginTextFieldValue);
        
        boolean result = r1;
        
        return result;
    }
    
    private boolean isLoginUnique(TextField login) {
        List<User> result = userData.stream().filter(c -> c.getLogin().equals(login.getText())).toList();
        return result.isEmpty();
    }

    private boolean hasInputChanged() {
        boolean r1 = !oldUserFirstName.equals(newUserFirstName);
        boolean r2 = !oldUserLastName.equals(newUserLastName);
        boolean r3 = !oldUserLogin.equals(newUserLogin);
        boolean r4 = !oldUserPassword.equals(newUserPassword);
        boolean r5 = !oldUserVacationleft.equals(newUserVacationleft);
        boolean r6 = !oldUserContract.equals(newUserContract);
        boolean r7 = !oldUserRole.equals(newUserRole);
        boolean r8 = !oldUserAddress.equals(newUserAddress);
        
        boolean result = r1 || r2 || r3 || r4 || r5 || r6 || r7 || r8;
        
        return result;
    }
    // </editor-fold> 
}
