/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.TrackingItemViewController.DataAction;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.TrackingItem;
import org.apache.logging.log4j.*;
import service.*;
import utils.ControllerUtilities;

/**
 *
 * @author adrest18
 */
public class TrackingItemDetailsViewController implements Initializable, IViewController {

    private final String trackingItemIdResourceKey = "TrackingItemId";
    private final String trackingItemNameResourceKey = "TrackingItemName";
    private final String trackingItemShortcutResourceKey = "TrackingItemShortcut";
    private final String trackingItemDescriptionResourceKey = "TrackingItemDescription";

    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";
    
    private final Logger log = LogManager.getLogger(TrackingItemDetailsViewController.class.getName());
    
    private Stage primaryStage;
    private TrackingItem trackingItem;
    private final LanguageService languageService;
    private final Connection connection;
    private final UndoService undoService;
    private ResourceBundle rb;
    private final ControllerRepository controllerRepository;
    private final ObservableList<TrackingItem> trackingItemData;
    private DataAction dataAction;
    
    private Long oldTrackingItemId;
    private Long newTrackingItemId;
    private String oldTrackingItemName;
    private String newTrackingItemName;
    private String oldTrackingItemShortcut;
    private String newTrackingItemShortcut;
    private String oldTrackingItemDescription;
    private String newTrackingItemDescription;
    
    @FXML
    private Label trackingItemIdLabel;
    @FXML
    private Label trackingItemNameLabel;
    @FXML
    private Label trackingItemShortcutLabel;
    @FXML
    private Label trackingItemDescriptionLabel;

    @FXML
    private TextField trackingItemIdLabelValue;
    @FXML
    private TextField trackingItemNameLabelValue;
    @FXML
    private TextField trackingItemShortcutLabelValue;
    @FXML
    private TextField trackingItemDescriptionLabelValue;
    
    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    
    public TrackingItemDetailsViewController(LanguageService languageService, Connection connection, UndoService undoService, ObservableList<TrackingItem> trackingItemData) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(connection == null) throw new NullPointerException("connection");
        if(undoService == null) throw new NullPointerException("undoService");
        if(trackingItemData == null) throw new NullPointerException("trackingItemData");
        
        this.languageService = languageService;
        this.connection = connection;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        
        this.trackingItemData = trackingItemData;
    }

    @FXML
    private void acceptAction(ActionEvent event) {
        String trackingItemIdAsString = trackingItemIdLabelValue.getText();
        if(ControllerUtilities.isNullOrEmpty(trackingItemIdAsString)) {
            trackingItemIdAsString = "0";
        }
        trackingItem.setId(Long.valueOf(trackingItemIdAsString));
        trackingItem.setName(trackingItemNameLabelValue.getText());
        trackingItem.setShortcut(trackingItemShortcutLabelValue.getText());
        trackingItem.setDescription(trackingItemDescriptionLabelValue.getText());
        primaryStage.close();
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        primaryStage.close();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;

        acceptButton.setDisable(true);
        
        trackingItemIdLabelValue.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validateNumberInput(newValue, trackingItemIdLabelValue);
                try {
                    newTrackingItemId = Long.valueOf(newValue);
                } catch (NumberFormatException ex) {
                    //trackingItemIdLabelValue.setText(newValue.replaceAll("[^\\d]", ""));
                    acceptButton.setDisable(true);
                }
                validateInput();
            }
        });        
        trackingItemNameLabelValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newTrackingItemName = newValue;
            trackingItemNameLabelValue.setText(newValue);
            validateInput();
        });
        trackingItemShortcutLabelValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newTrackingItemShortcut = newValue;
            trackingItemShortcutLabelValue.setText(newValue);
            validateInput();
        });
        trackingItemDescriptionLabelValue.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            newTrackingItemDescription = newValue;
            trackingItemDescriptionLabelValue.setText(newValue);
            validateInput();
        });        
    }

    @Override
    public void updateGuiItems() {
        trackingItemIdLabel.setText(rb.getString(trackingItemIdResourceKey));
        trackingItemNameLabel.setText(rb.getString(trackingItemNameResourceKey));
        trackingItemShortcutLabel.setText(rb.getString(trackingItemShortcutResourceKey));
        trackingItemDescriptionLabel.setText(rb.getString(trackingItemDescriptionResourceKey));
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

    public void showTrackingItemDetails(TrackingItem trackingItem) {
        this.trackingItem = trackingItem;
        
        //We save the actual ho information to be able to 
        //check for changes of each Information at validation of Innput.
        saveActualTrackingItemInformation(trackingItem);
        
        String idAsString = trackingItem.getId().toString();
        trackingItemIdLabelValue.setText(idAsString);
        trackingItemNameLabelValue.setText(trackingItem.getName());
        trackingItemShortcutLabelValue.setText(trackingItem.getShortcut());
        trackingItemDescriptionLabelValue.setText(trackingItem.getDescription());
    }

    private void saveActualTrackingItemInformation(TrackingItem trackingItem) {
        oldTrackingItemId = trackingItem.getId();
        oldTrackingItemName = trackingItem.getName();
        oldTrackingItemShortcut = trackingItem.getShortcut();
        oldTrackingItemDescription = trackingItem.getDescription();
    }

    private boolean validateInput() {
        if(isInputValid()) {
            acceptButton.setDisable(false);
            return true;
        } else {
            acceptButton.setDisable(true);
            return false;
        }
    }

    private boolean isInputValid() {
        boolean result = false;
        switch(dataAction) {
            case DataAction.NEW -> {
                boolean r1 = isInputFilled();
                boolean r2 = isInputUnique(); 
                
                return r1 && r2;
            }
            case DataAction.EDIT -> {
                boolean r1 = isInputFilled();
                boolean r2 = hasInputChanged();
                
                return r1 && r2;
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
        boolean r1 = isTextFieldFilled(trackingItemIdLabelValue);
        boolean r2 = isTextFieldFilled(trackingItemNameLabelValue);
        boolean r3 = isTextFieldFilled(trackingItemShortcutLabelValue);
        boolean r4 = isTextFieldFilled(trackingItemDescriptionLabelValue);
        
        boolean result = r1 && r2 && r3 && r4;
        return result;
    }

    private boolean isTextFieldFilled(TextField textFieldValue) {
        return !isNullOrEmpty(textFieldValue);
    }
        
    private boolean isNullOrEmpty(TextField textField) {
        return ControllerUtilities.isNullOrEmpty(textField.getText());
    }    

    private boolean isInputUnique() {
        String trackingItemIdAsString = trackingItemIdLabelValue.getText();
        if(ControllerUtilities.isNullOrEmpty(trackingItemIdAsString)) {
            trackingItemIdAsString = "0";
        }
        Long trackingItemId = Long.valueOf(trackingItemIdAsString);
        String trackingItemName = trackingItemNameLabelValue.getText();
        String trackingItemShortcut = trackingItemShortcutLabelValue.getText();
        String trackingItemDescription = trackingItemDescriptionLabelValue.getText();
        TrackingItem tempTrackingItem = new TrackingItem(trackingItemId, trackingItemName, trackingItemShortcut, trackingItemDescription);
        return !trackingItemData.contains(tempTrackingItem);
    }

    private boolean hasInputChanged() {
        boolean r1 = !oldTrackingItemId.equals(newTrackingItemId);
        boolean r2 = !oldTrackingItemName.equals(newTrackingItemName);
        boolean r3 = !oldTrackingItemShortcut.equals(newTrackingItemShortcut);
        boolean r4 = !oldTrackingItemDescription.equals(newTrackingItemDescription);

        boolean result = r1 || r2 || r3 || r4;      
        return result;
    }

    private void validateNumberInput(String newValue, TextField textField) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
    
}
