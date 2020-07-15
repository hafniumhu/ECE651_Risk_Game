package edu.duke.ece651.risc.client.controller;

import edu.duke.ece651.risc.client.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartController {

  private ClientGUI gui;
  
  public StartController(ClientGUI g) {
    this.gui = g;
  }

  @FXML
  public void startGame() {
    try {
        gui.getClient().joinGame();
        gui.setNumPlayersScene();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
  }

}
