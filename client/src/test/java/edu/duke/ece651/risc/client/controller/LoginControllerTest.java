package edu.duke.ece651.risc.client.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.duke.ece651.risc.client.ClientGUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxService;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.net.URL;

@ExtendWith(ApplicationExtension.class)
class LoginControllerTest {
    @Start
    private void start(Stage stage) {
        ClientGUI gui = mock(ClientGUI.class);
        doNothing().when(gui).sendStr("&&");
        when(gui.receiveStr()).thenReturn("yes");
        doNothing().when(gui).setStartScene();

        URL resource = ClientGUI.class.getResource("/fxml/login.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(c -> new LoginController(gui));
        fxmlLoader.setLocation(resource);
        Parent load = null;
        try {
            load = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(load));
        stage.show();
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void should_contain_button_with_text(FxRobot robot) {
        FxAssert.verifyThat(".button", LabeledMatchers.hasText("Login"));
        robot.clickOn(".button");

    }
}