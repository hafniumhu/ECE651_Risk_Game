package edu.duke.ece651.risc.client.controller;

import edu.duke.ece651.risc.client.ClientGUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.net.URL;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(ApplicationExtension.class)
public class LoseControllerTest {


    @Start
    private void start(Stage stage) {
        ClientGUI gui = mock(ClientGUI.class);
        doNothing().when(gui).setWatchScene(0);
        doNothing().when(gui).sendStr(0, "yes");
        URL resource = ClientGUI.class.getResource("/fxml/lose.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(c -> new LoseController(gui, 0));
        fxmlLoader.setLocation(resource);
        Parent load = null;
        try {
            load = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoseController controller = fxmlLoader.getController();
        controller.watchGame();
        stage.setScene(new Scene(load));
        stage.show();
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void should_contain_button_with_text(FxRobot robot) {
        FxAssert.verifyThat(".button", LabeledMatchers.hasText("Watch Game"));
//        robot.clickOn(".button");
    }
}