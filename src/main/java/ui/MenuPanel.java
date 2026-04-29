package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import LevelSection.Difficulty;

public class MenuPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(18, 18, 18);
    private static final Color PANEL_COLOR = new Color(38, 38, 38);
    private static final Color TEXT_COLOR = new Color(245, 245, 245);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_TEXT_COLOR = Color.BLACK;

    private final Consumer<Difficulty> difficultySelectedHandler;

    public MenuPanel(Consumer<Difficulty> difficultySelectedHandler) {
        if (difficultySelectedHandler == null) {
            throw new IllegalArgumentException("Difficulty selected handler cannot be null");
        }

        this.difficultySelectedHandler = difficultySelectedHandler;

        buildUi();
    }

    private void buildUi() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(PANEL_COLOR);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Chase Game");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        JLabel subtitleLabel = new JLabel("Choose a difficulty to start");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(createDifficultyButton("Easy", Difficulty.EASY));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        contentPanel.add(createDifficultyButton("Medium", Difficulty.MEDIUM));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        contentPanel.add(createDifficultyButton("Hard", Difficulty.HARD));

        add(contentPanel);
    }

    private JButton createDifficultyButton(String label, Difficulty difficulty) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusable(false);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(220, 48));
        button.setMaximumSize(new Dimension(220, 48));
        button.addActionListener(e -> difficultySelectedHandler.accept(difficulty));
        return button;
    }
}
