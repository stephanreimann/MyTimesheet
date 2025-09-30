@ECHO OFF
D:\Java\jdk-23.0.1\bin\java.exe ^
-Dfile.encoding=UTF-8 ^
--module-path D:\Java\javafx-sdk-23.0.1\lib ^
--add-modules javafx.controls,javafx.fxml ^
-classpath E:\Tools\MyTimesheet\lib\commons-lang3-3.14.0.jar;E:\Tools\MyTimesheet\lib\ical4j-2.0-beta1.jar;E:\Tools\MyTimesheet\lib\log4j-api-2.16.0.jar;E:\Tools\MyTimesheet\lib\log4j-core-2.16.0.jar;E:\Tools\MyTimesheet\lib\slf4j-api-1.7.25.jar;E:\Tools\MyTimesheet\lib\slf4j-simple-1.7.25.jar;E:\Tools\MyTimesheet\lib\sqlite-jdbc-3.23.1.jar;E:\Tools\MyTimesheet\MyTimesheet.jar;E:\Tools\MyTimesheet\build\classes;D:\Java\javafx-sdk-23.0.1\lib\javafx-swt.jar;D:\Java\javafx-sdk-23.0.1\lib\javafx.base.jar;D:\Java\javafx-sdk-23.0.1\lib\javafx.controls.jar;D:\Java\javafx-sdk-23.0.1\lib\javafx.fxml.jar;D:\Java\javafx-sdk-23.0.1\lib\javafx.graphics.jar;D:\Java\javafx-sdk-23.0.1\lib\javafx.media.jar;D:\Java\javafx-sdk-23.0.1\lib\javafx.swing.jar;D:\Java\javafx-sdk-23.0.1\lib\javafx.web.jar main.Main
