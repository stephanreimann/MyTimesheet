/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.*;
import net.fortuna.ical4j.data.ParserException;
import org.apache.logging.log4j.*;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
import org.xml.sax.*;
import service.*;
import utils.EventManager;

/**
 *
 * @author adrest18
 */
public class XmlEditorViewController implements Initializable, IViewController {

    private static final Pattern XML_TAG = Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))"
    		+"|(?<COMMENT><!--(.|\\v)+?-->)");
    
    private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=)(\\h*\"[^\"]+\")");
    
    private static final int GROUP_OPEN_BRACKET = 2;
    private static final int GROUP_ELEMENT_NAME = 3;
    private static final int GROUP_ATTRIBUTES_SECTION = 4;
    private static final int GROUP_CLOSE_BRACKET = 5;
    private static final int GROUP_ATTRIBUTE_NAME = 1;
    private static final int GROUP_EQUAL_SYMBOL = 2;
    private static final int GROUP_ATTRIBUTE_VALUE = 3;
    
    private final String selectResourceKey = "Select";
    private final String acceptResourceKey = "Accept";
    private final String cancelResourceKey = "Cancel";
    private final String pathToXmlFileResourceKey = "PathToXmlFile";
        
    private final Logger log = LogManager.getLogger(XmlEditorViewController.class.getName());

    @FXML
    AnchorPane anchorPane;
    
    @FXML
    VBox vBox;
    
    @FXML
    HBox hBox;
                       
    @FXML
    TextField filePathAndNameTextFieldValue;
    
    @FXML 
    Button selectButton;
    
    @FXML
    ToolBar toolBar;
            
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
    private String originalText;
    private CodeArea codeArea;
    private VirtualizedScrollPane virtualizedScrollPane;
    
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
    
    @SuppressWarnings("unchecked")
    private void loadFromFile(String filePathAndName) throws IOException, ParserException {
        acceptButton.setDisable(true);

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        vBox.getChildren().remove(1);
        vBox.getChildren().add(1, virtualizedScrollPane);
        
        codeArea.prefWidthProperty().bind(vBox.widthProperty());
        codeArea.prefHeightProperty().bind(vBox.heightProperty());
        
        String content = Files.readString(Path.of(filePathAndName), StandardCharsets.UTF_8);

        StyleSpans<Collection<String>> xmlHighlighting = computeHighlighting(content);
        codeArea.clear();
        if(xmlHighlighting != null) {
            codeArea.setStyleSpans(0, xmlHighlighting);
            codeArea.replaceText(content);
        } else {
            codeArea.replaceText("Error loading the XML content!");
        }

        String style = XmlEditorViewController.class.getResource("/resources/xml-highlighting.css").toExternalForm();
        codeArea.getStylesheets().add(style);
        
        originalText = content;
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        try {
            Matcher matcher = XML_TAG.matcher(text);
            int lastKwEnd = 0;
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
            while(matcher.find()) {

                    spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                    if(matcher.group("COMMENT") != null) {
                            spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
                    }
                    else {
                            if(matcher.group("ELEMENT") != null) {
                                    String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);

                                    spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
                                    spansBuilder.add(Collections.singleton("anytag"), matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));

                                    if(!attributesText.isEmpty()) {

                                            lastKwEnd = 0;

                                            Matcher amatcher = ATTRIBUTES.matcher(attributesText);
                                            while(amatcher.find()) {
                                                    spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
                                                    spansBuilder.add(Collections.singleton("attribute"), amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
                                                    spansBuilder.add(Collections.singleton("tagmark"), amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
                                                    spansBuilder.add(Collections.singleton("avalue"), amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
                                                    lastKwEnd = amatcher.end();
                                            }
                                            if(attributesText.length() > lastKwEnd)
                                                    spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
                                    }

                                    lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);

                                    spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
                            }
                    }
                lastKwEnd = matcher.end();
            }
            spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
            return spansBuilder.create();
        } catch(Exception | StackOverflowError ex) {
            return null;
        }
    }
 
    @FXML
    private void acceptAction(ActionEvent event) {
        String filePath = filePathAndNameTextFieldValue.getText();
        String content = codeArea.getText();

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
            showAlert(AlertType.INFORMATION, "Success", "XML file saved successfully.\nChanges become active after restart of application!");
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
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle rb) {
        this.rb = rb;
        codeArea = new CodeArea();
        virtualizedScrollPane = new VirtualizedScrollPane(codeArea);
        
        acceptButton.setDisable(true);
        
        codeArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldText, String newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
            if(!oldText.isEmpty() && !newText.equals(originalText)) {
                acceptButton.setDisable(false);
            } else {
                acceptButton.setDisable(true);
            }
        });          
        
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
