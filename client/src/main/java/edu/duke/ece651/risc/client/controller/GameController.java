package edu.duke.ece651.risc.client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import edu.duke.ece651.risc.client.ChatThread;
import edu.duke.ece651.risc.client.ClientGUI;
import edu.duke.ece651.risc.client.Popup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import shared.Board;
import shared.GameBoard;
import shared.Region;
import shared.checkers.AdjacentChecker;
import shared.instructions.Ally;
import shared.instructions.Attack;
import shared.instructions.FoodSupport;
import shared.instructions.Instruction;
import shared.instructions.Move;
import shared.instructions.TechUpgrade;
import shared.instructions.UnitUpgrade;
import shared.instructions.InciteDefection;

public class GameController implements Initializable{

  private ClientGUI gui;

  public GameController(ClientGUI g) {
    this.gui = g;
  }

  @FXML
  private BorderPane mainView;
  @FXML
  private TabPane games;
  @FXML
  private Tab newGame;
  @FXML
  private ChoiceBox<String> actionChoice;
  @FXML
  private VBox right;
  @FXML
  private VBox action;
  @FXML
  private Group group;
  @FXML
  private Circle color;
  @FXML
  private Label info;

//  @FXML
//  private TextArea area;
//  @FXML private TextField input;
//  @FXML private Button send;

  Board board;
  boolean init = true;
  int currentRoom;
//  ChatThread chat;
  @FXML private VBox chat;
  @FXML
  private ChatController chatController;

  private static Map<String, Color> colorMapper = new HashMap<>();
  private ArrayList<Instruction> insList = new ArrayList<>();

  static {
    colorMapper.put("Player1", Color.BLUE);
    colorMapper.put("Player2", Color.YELLOW);
    colorMapper.put("Player3", Color.GREEN);
    colorMapper.put("Player4", Color.RED);
    colorMapper.put("Player5", Color.ORANGE);
  }
  public GameController addBoard(Board board) {
    this.board = board;
    String currentName = gui.getCurrentName(currentRoom - 1);
    this.color.setFill(colorMapper.get(currentName));
    String s;
    if (board.getPlayer(currentName).getAlly() != null) {
      s = String.format("Name: %s\nLevel: %s\nFood resource: %s\nTech resource: %s\nAlly: %s\n", currentName,
          board.getPlayer(currentName).getCurrLevel(), board.getPlayer(currentName).getFoodAmount(),
          board.getPlayer(currentName).getTechAmount(), board.getPlayer(currentName).getAlly().getName());
    }
    else {
      s = String.format("Name: %s\nLevel: %s\nFood resource: %s\nTech resource: %s\nAlly: N/A\n", currentName,
          board.getPlayer(currentName).getCurrLevel(), board.getPlayer(currentName).getFoodAmount(),
          board.getPlayer(currentName).getTechAmount());
    }
    
    this.info.setText(s);
    chooseAction("move");
    if (board.getAllOwners().size() == 2) {
      actionChoice.getItems().remove("ally");
    }
    return this;
  }

  public GameController addMap(Group group) {
    this.group = group;
    initMap();
    mainView.setCenter(group);
    return this;
  }

  public GameController setCurrentRoom(int room) {
    this.currentRoom = room;
    System.out.println("set current room " + currentRoom);
    games.getSelectionModel().select(currentRoom - 1);
    initChat();
    init = false;
    return this;
  }

  public void chooseAction(String a) {
    a = a.split(" ")[0];
    URL resource = getClass().getResource(String.format("/fxml/component/%s.fxml", a));
    try {
      action = FXMLLoader.load(resource);
    } catch (IOException e) {
      e.printStackTrace();
    }
    right.getChildren().set(3, action);
    String pname = gui.getCurrentName(currentRoom - 1);
    setSrcChoice(pname, a);
    setDestChoice(pname, a);
    setP1Choice(pname, a);
  }

  public void refreshPage() {
    mainView.setRight(right);
    Stage window = (Stage)mainView.getScene().getWindow();
    window.setScene(mainView.getScene());
  }

  @FXML
  public void doAdd() {
    int room = currentRoom - 1;
    String pname = gui.getCurrentName(room);
    String ins = actionChoice.getValue();

    // Move
    if (ins.equals("move")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> src = (ChoiceBox<String>) entry.getChildren().get(1);
      ChoiceBox<String> dest = (ChoiceBox<String>) entry.getChildren().get(3);
      TextField level = (TextField) entry.getChildren().get(5);
      TextField num = (TextField) entry.getChildren().get(7);

      System.out.println("making move");
      
      Move moveIns = new Move(pname, src.getValue(), dest.getValue(),
                                  Integer.parseInt(level.getText()), Integer.parseInt(num.getText()));
      System.out.println("move made: " + src.getValue() + dest.getValue()+
                         level.getText() + num.getText());
                                 
      if(gui.getClient().isValidInst(room, moveIns)) {
          insList.add(moveIns);
          Popup.showInfo("Instruction added!");
      }
      else {
        Popup.showInfo("Invalid instruction!");
      }       
     }
    
    // Attack
    else if (ins.equals("attack")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> src = (ChoiceBox<String>) entry.getChildren().get(1);
      ChoiceBox<String> dest = (ChoiceBox<String>) entry.getChildren().get(3);
      TextField level = (TextField) entry.getChildren().get(5);
      TextField num = (TextField) entry.getChildren().get(7);
      
      Attack attackIns = new Attack(pname, src.getValue(), dest.getValue(),
                              Integer.parseInt(level.getText()), Integer.parseInt(num.getText()));
      if(gui.getClient().isValidInst(room, attackIns)) {
        insList.add(attackIns);
        Popup.showInfo("Instruction added!");
      }
      else {
        Popup.showInfo("Invalid instruction!");
      }
    }
    
    // Upgrade unit
    else if (ins.equals("unit upgrade")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> region = (ChoiceBox<String>) entry.getChildren().get(1);
      TextField oldlevel = (TextField) entry.getChildren().get(3);
      TextField newlevel = (TextField) entry.getChildren().get(5);
      TextField num = (TextField) entry.getChildren().get(7);
          
      UnitUpgrade upUnitIns = new UnitUpgrade(pname, region.getValue(), Integer.parseInt(oldlevel.getText()),
                                                 Integer.parseInt(newlevel.getText()),Integer.parseInt(num.getText()));
      if(gui.getClient().isValidInst(room, upUnitIns)) {
        insList.add(upUnitIns);
        Popup.showInfo("Instruction added!");
      }
      else {
        Popup.showInfo("Invalid instruction!");
      }
    }
    
    // Upgrade technology
    else if (ins.equals("tech upgrade")) {
      TechUpgrade upTechIns = new TechUpgrade(pname, board.getPlayer(pname).getCurrLevel(), board.getPlayer(pname).getCurrLevel()+1);
      if(gui.getClient().isValidInst(room, upTechIns)) {
        insList.add(upTechIns);
        Popup.showInfo("Instruction added!");
      }
      else {
        Popup.showInfo("Invalid instruction!");
      }
      actionChoice.getItems().remove(ins);
    }

    // Ally
    else if (ins.equals("ally")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> allys = (ChoiceBox<String>) entry.getChildren().get(1);
      Ally allyIns = new Ally(pname, allys.getValue());
      if(gui.getClient().isValidInst(room, allyIns)) {
        insList.add(allyIns);
        Popup.showInfo("Instruction added!");
      }
      else {
        Popup.showInfo("Invalid instruction!");
      }
      actionChoice.getItems().remove(ins);
    }

    // Food supproo
    else if (ins.equals("food support")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> supports = (ChoiceBox<String>) entry.getChildren().get(1);
      TextField amount = (TextField) entry.getChildren().get(3);
      FoodSupport foodIns = new FoodSupport(pname, supports.getValue(), Integer.parseInt(amount.getText()));
      if(gui.getClient().isValidInst(room, foodIns)) {
        insList.add(foodIns);
        Popup.showInfo("Instruction added!");
      }
      else {
        Popup.showInfo("Invalid instruction!");
      }
    }

    // Incite
    else if (ins.equals("incite defection")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> src = (ChoiceBox<String>) entry.getChildren().get(1);
      ChoiceBox<String> dest = (ChoiceBox<String>) entry.getChildren().get(3);
      
      InciteDefection inciteIns = new InciteDefection(null, src.getValue(), dest.getValue(), 0, 0);
      if(gui.getClient().isValidInst(room, inciteIns)) {
        insList.add(inciteIns);
        Popup.showInfo("Instruction added!");
      }
      else {
        Popup.showInfo("Invalid instruction!");
      }
      actionChoice.getItems().remove(ins);
    }
       
  }

  @FXML
  public void doDone() {
    // send instructions, set board
    int room = currentRoom - 1;
    gui.sendObj(room, insList);

    
        Board newBoard = (GameBoard) gui.receiveObj(room);
        // System.out.println(newBoard.toString());
        gui.getClient().setBoard(room, newBoard);
        Popup.showInfo("Instructrion submitted.");
        insList.clear();

        // check win/lose
        if (gui.getClient().hasWon(room)) {
          // System.out.println("you win!");
          gui.setWinScene();
        }
        else if (gui.getClient().hasLost(room)) {
          // System.out.println("you lost!");
          gui.setLoseScene(room);
        }
     gui.setGameScene(currentRoom);
  }

  private void initMap() {
    List<Region> allRegions = board.getAllRegions();
    for (Region region : allRegions) {
      String owner = region.getOwner().getName();
      Circle circle = (Circle)group.lookup("#" + region.getName());
      circle.setOnMouseClicked(this::showInfo);
      circle.setFill(colorMapper.get(owner));
    }
  }

  @FXML
  void createNewGame() throws IOException {
    System.out.println("start game " + (currentRoom + 1));
    if (games.getTabs().size() > 1) {
      System.out.println("Start game, activeGames > 1");
      gui.getClient().joinGame();
      gui.setNumPlayersScene();
    }
  }

  @FXML
  public void showInfo(MouseEvent event) {
    Node source = (Node)event.getSource();
    String id = source.getId();
    String pname = gui.getCurrentName(currentRoom - 1);
    Popup.showInfo(board.getRegion(id).getInfo(pname));
  }

  private void generateTabs(int activeRoom) {
    int size = games.getTabs().size();
    while (size - 1 < activeRoom) {
      Tab tab = new Tab("Game " + size);
      tab.setId(String.valueOf(size));
      // System.out.println("create tab with id " + size);
      tab.setOnSelectionChanged(event -> {
        if (init) return;
        if (tab.isSelected()) {
          String id = tab.getId();
          System.out.println("switch to room " + id);
          gui.setGameScene(Integer.parseInt(id));
          chatController.setCurrentRoom(Integer.parseInt(id)); // chat change content
        }
      });
      games.getTabs().add(size - 1, tab);
      size++;
    }
  }


  // Fill in source selection
  public void setSrcChoice(String pname, String ins) {
    if (ins.equals("unit") || ins.equals("incite")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> srcChoice = (ChoiceBox<String>) entry.getChildren().get(1);
      srcChoice.getItems().clear();
      for (String regionName: board.getRegionNames(pname)) {
        srcChoice.getItems().add(regionName);
      }
    }
    else if(ins.equals("move") || ins.equals("attack")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> srcChoice = (ChoiceBox<String>) entry.getChildren().get(1);
      srcChoice.getItems().clear();
      for (String regionName: board.getRegionNames(pname)) {
        srcChoice.getItems().add(regionName);
      }
      if (board.getPlayer(pname).getAlly() != null) {
        for (String regionName: board.getRegionNames(board.getPlayer(pname).getAlly().getName())) {
          srcChoice.getItems().add(regionName);
        }
      }
    }
  }

  // Fill in destination selection
  public void setDestChoice(String pname, String ins) {
    if (ins.equals("move")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> destChoice = (ChoiceBox<String>) entry.getChildren().get(3);
      destChoice.getItems().clear();
      for (String regionName: board.getRegionNames(pname)) {
        destChoice.getItems().add(regionName);
      }
      if (board.getPlayer(pname).getAlly() != null) {
        for (String regionName: board.getRegionNames(board.getPlayer(pname).getAlly().getName())) {
          destChoice.getItems().add(regionName);
        }
      }
    }
    else if (ins.equals("attack") || ins.equals("incite")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> destChoice = (ChoiceBox<String>) entry.getChildren().get(3);
      destChoice.getItems().clear();
      Set<String> ownRegion = board.getRegionNames(pname);
      for (String regionName: board.getAllRegionNames()) {
        if (!ownRegion.contains(regionName) && adj(board.getRegion(regionName), pname)) {
            destChoice.getItems().add(regionName);
        }
      }
    }
  }

  // check if adjacent to a player's region
  public boolean adj(Region reg, String pname) {
    for (Region region: board.getAllRegions(pname)) {
      AdjacentChecker acheck = new AdjacentChecker(board, reg, region);
      if (acheck.isValid()) {
        return true;
      }
    }
    return false;
  }


  // Fill in player1 selection
  public void setP1Choice(String pname, String ins) {
    if (ins.equals("ally") || ins.equals("food")) {
      VBox entry = (VBox) right.getChildren().get(3);
      ChoiceBox<String> p1Choice = (ChoiceBox<String>) entry.getChildren().get(1);
      p1Choice.getItems().clear();
      for (String name: board.getAllOwners()) {
        if (!name.equals(pname)) {
          p1Choice.getItems().add(name);
        }
      }
    }
  }
//
  private void initChat() {
    URL resource = getClass().getResource("/fxml/component/chat.fxml");
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(resource);
    try {
      fxmlLoader.setControllerFactory(c -> new ChatController(gui));
      chat = fxmlLoader.load();

      chatController = fxmlLoader.getController();
      chatController.setCurrentRoom(currentRoom);
    } catch (IOException e) {
      e.printStackTrace();
    }
    right.getChildren().set(6, chat);
  }
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    System.out.println("initialize");
    generateTabs(gui.getActiveGames());
    actionChoice.getItems().addAll("attack", "move", "unit upgrade", "tech upgrade", "ally", "food support", "incite defection");
    actionChoice.setValue("attack");

    actionChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
      chooseAction(actionChoice.getItems().get((int)newValue));
      refreshPage();
    });
//    initChat();
//    startChat();
  }
}

