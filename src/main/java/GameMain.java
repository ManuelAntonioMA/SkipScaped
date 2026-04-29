import javax.swing.SwingUtilities;
import ui.GameWindow;
import actor.*;
import engine.*;
import Item.*;
import LevelSection.*;
import model.*;
import trap.*;


public class GameMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameWindow();
        });
    }
}