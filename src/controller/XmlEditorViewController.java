/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.fortuna.ical4j.data.ParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import service.LanguageService;
import service.UndoService;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class XmlEditorViewController implements Initializable, IViewController {

    private final String selectResourceKey = "Select";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";
    private final String pathToXmlFileResourceKey = "PathToXmlFile";
    
    private final Logger log = LogManager.getLogger(XmlEditorViewController.class.getName());

    @FXML
    TextField filePathAndNameTextFieldValue;
    @FXML 
    Button selectButton;

    @FXML
    TextArea xmlTextArea;
    
    @FXML 
    private Button acceptButton;
    @FXML
    private Button cancelButton;

    private Stage primaryStage;
    private LanguageService languageService;
    private UndoService undoService;
    private ResourceBundle rb;
    private ControllerRepository controllerRepository;
    private EventManager eventManager;
    
    XmlEditorViewController(LanguageService languageService, UndoService undoService) {
        if(languageService == null) throw new NullPointerException("languageService");
        if(undoService == null) throw new NullPointerException("undoService");
        
        this.languageService = languageService;
        this.undoService = undoService;
        this.controllerRepository = ControllerRepository.getInstance();
        this.eventManager = new EventManager();
    }
    
    @FXML
    private void selectAction(ActionEvent event) throws IOException, ParserException {
        File choosenFile = initializeFileChooserAndShowIt();
        if(choosenFile != null && choosenFile.exists()) {
            String file = choosenFile.getPath();
            filePathAndNameTextFieldValue.textProperty().unbind();
            filePathAndNameTextFieldValue.setText(file);
            try {
                loadFromFile(filePathAndNameTextFieldValue.getText());

            } catch(IOException | ParserException ex) {
                log.fatal("Exception: " + ex.getMessage());
                throw ex;
            }
        }    
    }

    private File initializeFileChooserAndShowIt() throws IOException {
        String holydaysDirectory = new File(".").getCanonicalPath();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("FileChooserTitle"));
        fileChooser.setInitialDirectory(new File(holydaysDirectory));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(primaryStage);
    }
    
    private void loadFromFile(String filePathAndName) throws IOException, ParserException {
        String content = Files.readString(Path.of(filePathAndName), StandardCharsets.UTF_8);
        xmlTextArea.setText(content);
    }
     
    @FXML
    private void acceptAction(ActionEvent event) {
        String filePath = filePathAndNameTextFieldValue.getText();
        String content = xmlTextArea.getText();

        if (filePath == null || filePath.isBlank()) {
            showAlert(AlertType.ERROR, "No file selected", "Please select an XML file first.");
            log.error("No file path selected.");
            return;
        }

        if (content == null || content.isBlank()) {
            showAlert(AlertType.ERROR, "Empty XML", "The XML content is empty.");
            log.error("XML content is empty.");
            return;
        }

        try {
            validateXml(content);
            Files.writeString(Path.of(filePath), content, StandardCharsets.UTF_8);
            showAlert(AlertType.INFORMATION, "Success", "XML file saved successfully.");
            primaryStage.close();
        } catch (ParserConfigurationException | SAXException ex) {
            showAlert(AlertType.ERROR, "Invalid XML", "XML validation failed:\n" + ex.getMessage());
            log.error("XML validation failed: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            showAlert(AlertType.ERROR, "Save Error", "The XML file could not be saved:\n" + ex.getMessage());
            log.fatal("Error saving XML file: " + ex.getMessage(), ex);
        }
    }

    private void validateXml(String xmlContent) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(new InputSource(new StringReader(xmlContent)));
    }    

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void cancelAction(ActionEvent event) {
        primaryStage.close();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;

        acceptButton.disableProperty().bind(xmlTextArea.textProperty().isEmpty());
        
        Platform.runLater(() -> selectButton.requestFocus());
        
        languageService.updateGuiItems();        
    }

    @Override
    public void updateGuiItems() {
        selectButton.setText(rb.getString(selectResourceKey));
        acceptButton.setText(rb.getString(acceptResourceKey));
        cancelButton.setText(rb.getString(cancelResourceKey));
        filePathAndNameTextFieldValue.setText(rb.getString(pathToXmlFileResourceKey));
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
    
}
