package shared.instructions;

import shared.Board;
import shared.Player;
import shared.Region;
import shared.UpgradeLookup;
import shared.checkers.UnitUpgradeChecker;

public class UnitUpgrade extends UpgradeInstruction {
  private String src;
  private int numUnit;
  private static final long serialVersionUID = 923749348;

  public UnitUpgrade(String pname, String s, int oldL, int newL, int num) {
    super(pname, oldL, newL);
    this.src = s;
    this.numUnit = num;
  }

  @Override
  public void execute(Board b) {
    Player p = b.getPlayer(playerName);
    Region source = b.getRegion(src);
    source.upgradeUnit(oldLevel, newLevel, numUnit);
    p.decreaseTech(numUnit * getCost(new UpgradeLookup()));
  }

  @Override
  public boolean isValid(Board b) {
    UnitUpgradeChecker checker= new UnitUpgradeChecker(b, this);
    return checker.isValid();
  }
  
  // Getters
  public String getSrc() {
    return src;
  }

  public int getCost(UpgradeLookup table) {
    return table.getCostUnit(oldLevel, newLevel);
  }

  public int getNumUnit() {
    return numUnit;
  }
}
