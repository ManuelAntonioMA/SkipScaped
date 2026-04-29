package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import LevelSection.Difficulty;

public class GameWindow extends JFrame {

    public GameWindow() {
        setTitle("Chase Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1200, 700);
        setLocationRelativeTo(null);

        showMenu();
        setVisible(true);
    }

    private void showMenu() {
        setContentPane(new MenuPanel(this::startGame));
        revalidate();
        repaint();
    }

    private void startGame(Difficulty difficulty) {
        GamePanel gamePanel = new GamePanel(difficulty, this::showMenu);
        setContentPane(gamePanel);
        revalidate();
        repaint();
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }
}