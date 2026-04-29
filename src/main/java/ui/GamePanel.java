package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import Item.BonusReward;
import Item.RegularReward;
import Item.RewardManager;
import LevelSection.Difficulty;
import actor.Direction;
import actor.MainCharacter;
import actor.MovingEnemy;
import engine.Action;
import engine.GameEngine;
import engine.GameState;
import engine.LoseReason;
import engine.PlayerTurn;
import engine.Result;
import engine.Status;
import model.Board;
import model.Cell;
import model.CellType;
import model.Position;
import trap.Trap;
import LevelSection.Level;
import LevelSection.LevelConfig;
import LevelSection.LevelFactory;

public class GamePanel extends JPanel {
    private static final int REALTIME_TICK_MS = 1000;

    private final GameEngine engine;
    private final Timer gameTimer;
    private int titleSize = 32;
    private final Difficulty selectedDifficulty;
    private final Runnable returnToMenu;
    private boolean showInstructions = false;
    private Rectangle infoButtonBounds;
    private boolean showEndOverlay = false;
    private Result endResult = null;
    private boolean showConfirmOverlay = false;
    private String confirmAction = null; //restart or quit
    private boolean isPaused = false;
    private final Level level;
    private final LevelConfig levelConfig;

    private static final int INITIAL_LIVES = 3;
    private static final int INITIAL_TIME = 120;
    private static final int ENEMY_SPEED = 2;
    private static final int REWARD_POINTS = 10;
    private static final int INITIAL_REWARD_COUNT = 3;

    private static final List<Position> INITIAL_ENEMY_POSITIONS = List.of(
            new Position(1, 1),
            new Position(3, 10),
            new Position(10, 3),
            new Position(12, 12),
            new Position(5, 7)
    );

    private void handleEndOverlayInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            returnToMenu.run();
        }
    }

    private void openConfirmOverlay(String action) {
        confirmAction = action;
        showConfirmOverlay = true;
        repaint();
    }

    private boolean handleGlobalCommands(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            openConfirmOverlay("restart");
            return true;
        }

        if (e.getKeyCode() == KeyEvent.VK_Q) {
            openConfirmOverlay("quit");
            return true;
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            isPaused = true;
            repaint();
            return true;
        }

        return false;
    }

    private void handleKeyPressed(KeyEvent e) {
        if (showConfirmOverlay) {
            handleConfirmInput(e);
            return;
        }

        if (showEndOverlay) {
            handleEndOverlayInput(e);
            return;
        }

        if (isPaused) {
            handlePauseInput(e);
            return;
        }

        if (handleGlobalCommands(e)) {
            return;
        }

        PlayerTurn turn = toPlayerTurn(e);
        if (turn == null) {
            return;
        }

        engine.turnFlow(turn);
        repaint();

        if (engine.getStatus() != Status.RUNNING) {
            handleGameFinished();
        }
    }

    private PlayerTurn toPlayerTurn(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                return new PlayerTurn(Direction.UP, Action.NONE);
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                return new PlayerTurn(Direction.DOWN, Action.NONE);
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                return new PlayerTurn(Direction.LEFT, Action.NONE);
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                return new PlayerTurn(Direction.RIGHT, Action.NONE);
            case KeyEvent.VK_SPACE:
                return new PlayerTurn(Direction.NONE, Action.PLACE_TRAP);
            default:
                return null;
        }
    }


    public GamePanel(Difficulty selectedDifficulty, Runnable returnToMenu) {
        this.engine = new GameEngine(); // Initialize it as a GameEngine instead
        this.selectedDifficulty = selectedDifficulty;
        this.returnToMenu = returnToMenu;
        this.gameTimer = new Timer(REALTIME_TICK_MS, e -> handleRealtimeTick());

        this.level = LevelFactory.create("selected-level", selectedDifficulty);
        this.levelConfig = level.getConfig();

        initializeGame();

        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (infoButtonBounds != null && infoButtonBounds.contains(e.getPoint())) {
                    showInstructions = !showInstructions;
                    repaint();
                }
            }
        });

        gameTimer.start();
    }

    private void handleRealtimeTick() {
        if (isPaused || showConfirmOverlay || showEndOverlay) {
            return;
        }

        engine.updateRealTime();
        repaint();

        if (engine.getStatus() != Status.RUNNING) {
            handleGameFinished();
        }
    }

    private Board createBoard() {
        Board board = new Board();
        loadSelectedMap(board);
        return board;
    }

    private MainCharacter createPlayer(Board board) {
        Position start = board.getStartPos();
        return new MainCharacter(start, board);
    }

    private List<MovingEnemy> createEnemies(Board board, MainCharacter player) {
        List<MovingEnemy> enemies = new ArrayList<>();

        int enemiesToSpawn = levelConfig.getEnemyCount();
        int spawned = 0;

        for (Position position : INITIAL_ENEMY_POSITIONS) {
            if (spawned >= enemiesToSpawn) {
                break;
            }

            if (board.isWalkable(position) && !position.equals(player.getPosition())) {
                enemies.add(new MovingEnemy(position, board, ENEMY_SPEED));
                spawned++;
            }
        }

        return enemies;
    }

    private void addEnemyIfValid(List<MovingEnemy> enemies, Position position,
                                 Board board, MainCharacter player) {
        if (board.isWalkable(position) && !position.equals(player.getPosition())) {
            enemies.add(new MovingEnemy(position, board, ENEMY_SPEED));
        }
    }

    private void spawnInitialRewards(Board board, MainCharacter player,
                                     List<MovingEnemy> enemies) {
        RewardManager rewardManager = new RewardManager();

        for (int i = 0; i < levelConfig.getRegularRewards(); i++) {
            rewardManager.spawnRegularReward(board, player, enemies, REWARD_POINTS);
        }

        for (int i = 0; i < levelConfig.getBonusRewards(); i++) {
            rewardManager.spawnBonusReward(board, player, enemies, 25, 0, 20);
        }
    }

    private GameState createInitialGameState() {
        return new GameState(levelConfig.getRegularRewards(),
                levelConfig.getTimeLimit());
    }
    private void spawnInitialTrapPickup(Board board, MainCharacter player,
                                         List<MovingEnemy> enemies) {
        for (int i = 0; i < levelConfig.getTrapCount(); i++) {
            engine.getTrapManager().spawnTrapPickup(board, player, enemies);
        }
    }

    private void initializeGame() { // Constructor
        Board board = createBoard();
        MainCharacter player = createPlayer(board);
        List<MovingEnemy> enemies = createEnemies(board, player);

        spawnInitialRewards(board, player, enemies);

        GameState gameState = createInitialGameState();
        engine.start(board, level, player, enemies, gameState);

        spawnInitialTrapPickup(board, player, enemies);
    }

    private void loadSelectedMap(Board board) {
        String mapId = "test_31x15"; // Default map
        
        switch (selectedDifficulty) {
            case EASY:
                mapId = "easy_map";
                break;
            case MEDIUM:
                mapId = "medium_map";
                break;
            case HARD:
                mapId = "hard_map";
                break;
        }
        board.loadFromMap(mapId);
    }

    private void handleGameFinished() {
        gameTimer.stop();
        endResult = engine.buildResult();
        showEndOverlay = true;
        repaint();
    }

    private Graphics2D createOverlayGraphics(Graphics g, int alpha) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, alpha));
        g2.fillRect(0, 0, getWidth(), getHeight());
        return g2;
    }

    private void drawOverlayPanel(Graphics2D g2, int x, int y,
                                  int panelWidth, int panelHeight,
                                  int arcWidth, int arcHeight) {
        g2.setColor(new Color(18, 18, 22, 245));
        g2.fillRoundRect(x, y, panelWidth, panelHeight, arcWidth, arcHeight);

        g2.setColor(new Color(255, 255, 255, 90));
        g2.drawRoundRect(x, y, panelWidth, panelHeight, arcWidth, arcHeight);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Board board = engine.getBoard();
        if (board == null) {return;}
        //improving the appearance on screen
        int cols = engine.getBoard().getWidth();
        int rows = engine.getBoard().getHeight();

        int availableWidth = getWidth() - 60;
        int availableHeight = getHeight() - 160;

        titleSize = Math.min(availableWidth / cols, availableHeight / rows);
        titleSize = Math.max(titleSize, 20);

        int boardWidth = cols * titleSize;
        int boardHeight = rows * titleSize;

        int offsetX = (getWidth() - boardWidth) / 2;
        int offsetY = Math.max(100, (getHeight() - boardHeight) / 2);

        drawHud(g, offsetX, offsetY);
        drawInfoButton(g);
        drawBoard(g, offsetX, offsetY);
        drawTraps(g, offsetX, offsetY);
        drawEnemies(g, offsetX, offsetY);
        drawPlayer(g, offsetX, offsetY);
        drawBottomControlBar(g);

        if (showInstructions) {drawInstructionOverlay(g);}
        if (isPaused) {drawPauseOverlay(g);}
        if (showConfirmOverlay) {drawConfirmOverlay(g);}
        if (showEndOverlay && endResult != null) {drawEndGameOverlay(g);
        }
    }

    private void drawBoard(Graphics g, int offsetX, int offsetY) {
        Board board = engine.getBoard();

        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                Cell cell = board.getCell(new Position(r, c));
                CellType type = cell.getTitle();

                int x = offsetX + c * titleSize;
                int y = offsetY + r * titleSize;

                switch (type) {
                    case WALL:
                        g.setColor(new Color(92, 94, 99));
                        break;
                    case BARRIER: // Are we even going to include barriers???
                        g.setColor(new Color(110, 112, 120));
                        break;
                    case START:
                        g.setColor(new Color(80, 140, 220));
                        break;
                    case EXIT:
                        g.setColor(new Color(242,242,242));
                        break;
                    case FLOOR:
                    default:
                        g.setColor(new Color(36, 37, 41));
                        break;
                }
                g.fillRect(x, y, titleSize, titleSize);
                g.setColor(new Color(70, 72, 78));
                g.drawRect(x, y, titleSize, titleSize);

                if (cell.hasItem()) { // Draw rewards and bonus rewards
                    if (cell.getItem() instanceof RegularReward) {
                        drawCoinReward(g, x, y);
                    } else if (cell.getItem() instanceof BonusReward) {
                        drawStarReward(g, x, y);
                    }
                }
            }
        }
    }

    //helper drawing function
    private void drawCoinReward(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        int size = (int) (titleSize * 0.6);
        int px = x + (titleSize - size) / 2;
        int py = y + (titleSize - size) / 2;

        // outer coin
        g2.setColor(new Color(255, 196, 60));
        g2.fillOval(px, py, size, size);

        // inner shine
        g2.setColor(new Color(255, 232, 90));
        g2.fillOval(px + size / 6, py + size / 6, size * 2 / 3, size * 2 / 3);

        // outline
        g2.setColor(new Color(200, 130, 25));
        g2.drawOval(px, py, size, size);

        // small shine mark
        g2.setColor(new Color(255, 250, 180));
        g2.fillOval(px + size / 4, py + size / 5, size / 7, size / 7);

        g2.dispose();
    }
    private void drawStarReward(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        int size = (int) (titleSize * 0.7);
        int px = x + (titleSize - size) / 2;
        int py = y + (titleSize - size) / 2;

        int cx = px + size / 2;
        int cy = py + size / 2;

        g2.setColor(new Color(255, 230, 140));
        Polygon star = new Polygon();

        // hex-star style
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(i * 60);
            double innerAngle = Math.toRadians(i * 60 + 30);

            int outerX = (int) (cx + Math.cos(angle) * size / 2);
            int outerY = (int) (cy + Math.sin(angle) * size / 2);

            int innerX = (int) (cx + Math.cos(innerAngle) * size / 4);
            int innerY = (int) (cy + Math.sin(innerAngle) * size / 4);

            star.addPoint(outerX, outerY);
            star.addPoint(innerX, innerY);
        }

        g2.fillPolygon(star);
        g2.setColor(new Color(210, 170, 60));
        g2.drawPolygon(star);

        g2.dispose();
    }

    private void drawPlayer(Graphics g, int offsetX, int offsetY) {
        if (engine.getPlayer() == null || engine.getPlayer().getPosition() == null) {return;}

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Position p = engine.getPlayer().getPosition();
        int x = offsetX + p.getCol() * titleSize;
        int y = offsetY + p.getRow() * titleSize;

        int w = (int) (titleSize * 0.78);
        int h = (int) (titleSize * 0.88);
        int px = x + (titleSize - w) / 2;
        int py = y + (titleSize - h) / 2;

        Color outline = Color.BLACK;
        Color body = new Color(242, 242, 242);
        Color shade = new Color(220, 220, 220);
        Color accent = new Color(170, 170, 220);

        // ===== CALCULATION =====
        int bodyW = (int) (w * 0.56);
        int bodyH = (int) (h * 0.36);
        int bodyX = px + (w - bodyW) / 2;
        int bodyY = py + (int) (h * 0.42);

        int headW = (int) (w * 0.82);
        int headH = (int) (h * 0.44);
        int headX = px + (w - headW) / 2;
        int headY = py;

        // ===== LEGS =====
        int legW = Math.max(6, bodyW / 4);
        int legH = Math.max(8, bodyH / 2);
        int legY = bodyY + bodyH - 4;

        int leftLegX = bodyX + 2;
        int rightLegX = bodyX + bodyW - legW - 2;

        g2.setColor(body);
        g2.fillRoundRect(leftLegX, legY, legW, legH, 6, 6);
        g2.fillRoundRect(rightLegX, legY, legW, legH, 6, 6);

        g2.setColor(outline);
        g2.drawRoundRect(leftLegX, legY, legW, legH, 6, 6);
        g2.drawRoundRect(rightLegX, legY, legW, legH, 6, 6);

        // accent at leg
        g2.setColor(accent);
        g2.fillRoundRect(leftLegX, legY + legH - 5, legW - 1, 4, 4, 4);
        g2.fillRoundRect(rightLegX, legY + 2, legW - 1, 4, 4, 4);

        // ===== BODY =====
        g2.setColor(body);
        g2.fillRoundRect(bodyX, bodyY, bodyW, bodyH, 10, 10);
        g2.setColor(outline);
        g2.drawRoundRect(bodyX, bodyY, bodyW, bodyH, 10, 10);

        // light shadow
        g2.setColor(shade);
        g2.drawLine(bodyX + bodyW - 4, bodyY + 6, bodyX + bodyW - 4, bodyY + bodyH - 8);

        // ===== HEAD =====
        g2.setColor(body);
        g2.fillRoundRect(headX, headY, headW, headH, headW / 3, headH / 3);
        g2.setColor(outline);
        g2.drawRoundRect(headX, headY, headW, headH, headW / 3, headH / 3);

        // accent on right face-side
        int accentW = Math.max(4, headW / 8);
        g2.setColor(accent);
        g2.fillRoundRect(headX + headW - accentW - 1, headY + headH / 5, accentW, headH / 2, 6, 6);

        // eyes
        int eyeW = Math.max(3, headW / 10);
        int eyeH = Math.max(6, headH / 4);
        g2.setColor(outline);
        g2.fillRoundRect(headX + headW / 4 - eyeW / 2, headY + headH / 2 - eyeH / 3, eyeW, eyeH, 3, 3);
        g2.fillRoundRect(headX + headW * 3 / 4 - eyeW / 2, headY + headH / 2 - eyeH / 3, eyeW, eyeH, 3, 3);

        g2.dispose();
    }

    private void drawEnemies(Graphics g, int offsetX, int offsetY) {
        for (MovingEnemy enemy : engine.getEnemies()) {
            if (enemy == null || enemy.getPosition() == null) {
                continue;
            }
            int x = offsetX + enemy.getPosition().getCol() * titleSize;
            int y = offsetY + enemy.getPosition().getRow() * titleSize;

            drawGhostEnemy(g, x, y);
        }
    }
    //helper drawing function
    private void drawGhostEnemy(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        int size = (int) (titleSize * 0.72);
        int px = x + (titleSize - size) / 2;
        int py = y + (titleSize - size) / 2;

        // body
        g2.setColor(new Color(140, 95, 190));
        g2.fillRoundRect(px, py + size / 5, size, size * 3 / 5, size / 3, size / 3);
        g2.fillOval(px, py, size, size * 2 / 3);

        // bottom waves
        g2.fillOval(px, py + size * 3 / 5, size / 3, size / 3);
        g2.fillOval(px + size / 3, py + size * 3 / 5, size / 3, size / 3);
        g2.fillOval(px + 2 * size / 3, py + size * 3 / 5, size / 3, size / 3);

        // outline
        g2.setColor(new Color(75, 45, 110));
        g2.drawRoundRect(px, py + size / 5, size, size * 3 / 5, size / 3, size / 3);
        g2.drawOval(px, py, size, size * 2 / 3);

        // eyes
        g2.setColor(Color.WHITE);
        int eyeW = size / 7;
        int eyeH = size / 5;
        g2.fillOval(px + size / 4, py + size / 4, eyeW, eyeH);
        g2.fillOval(px + size / 2, py + size / 4, eyeW, eyeH);

        // pupils
        g2.setColor(new Color(35, 20, 55));
        g2.fillOval(px + size / 4 + eyeW / 4, py + size / 4 + eyeH / 4, eyeW / 2, eyeH / 2);
        g2.fillOval(px + size / 2 + eyeW / 4, py + size / 4 + eyeH / 4, eyeW / 2, eyeH / 2);

        // blush
        g2.setColor(new Color(245, 170, 190));
        g2.fillRoundRect(px + size / 6, py + size / 2, size / 8, size / 12, 4, 4);
        g2.fillRoundRect(px + size * 5 / 8, py + size / 2, size / 8, size / 12, 4, 4);

        // mouth
        g2.setColor(new Color(55, 30, 75));
        g2.fillRoundRect(px + size / 2 - size / 12, py + size * 5 / 8, size / 6, size / 10, 4, 4);

        g2.dispose();
    }

    private void drawTraps(Graphics g, int offsetX, int offsetY) {
        for (Trap trap : engine.getTrapManager().getTraps()) {
            if (trap == null || trap.getPosition() == null || !trap.isActive()) {
                continue;
            }

            Position pos = trap.getPosition();
            int x = offsetX + pos.getCol() * titleSize;
            int y = offsetY + pos.getRow() * titleSize;

            drawHoleTrap(g, x, y);
        }
    }
    //helper drawing function
    private void drawHoleTrap(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        int width = (int) (titleSize * 0.8);
        int height = (int) (titleSize * 0.5);

        int px = x + (titleSize - width) / 2;
        int py = y + (titleSize - height) / 2;

        // outer rim
        g2.setColor(new Color(120, 220, 255));
        g2.fillOval(px, py, width, height);

        // inner shadow
        g2.setColor(new Color(20, 60, 100));
        g2.fillOval(px + 2, py + 2, width - 4, height - 4);

        // deepest center
        g2.setColor(new Color(5, 20, 40));
        g2.fillOval(px + width / 4, py + height / 4, width / 2, height / 2);

        // highlight top edge
        g2.setColor(new Color(200, 255, 255, 120));
        g2.drawArc(px, py - 1, width, height, 0, 180);

        g2.dispose();
    }

    private void drawHud(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2 = (Graphics2D) g.create();

        int panelX = offsetX;
        int panelY = offsetY - 72;
        int panelW = engine.getBoard().getWidth() * titleSize;
        int panelH = 52;

        //background of HUD bar
        g2.setColor(new Color(22, 22, 22, 235));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);


        g2.setColor(new Color(90, 90, 90));
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);


        Font labelFont = new Font("Segoe UI", Font.BOLD, 19);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 19);

        FontMetrics labelMetrics = g2.getFontMetrics(labelFont);
        FontMetrics valueMetrics = g2.getFontMetrics(valueFont);

        int baseline = panelY + 33;
        int leftPadding = 22;

        String[] labels = {"Level:", "Rewards:", "Score:", "Time:", "Steps:", "Traps:"};
        String[] values = {
                String.valueOf(selectedDifficulty),
                String.valueOf(engine.getRemainingRewards()),
                String.valueOf(engine.getCurrentScore()),
                String.valueOf(engine.getTimeLeft()),
                String.valueOf(engine.getStepsUsed()),
                String.valueOf(engine.getPlayer().getTrapInventory())
        };

        int sectionWidth = (panelW - leftPadding * 2) / labels.length;

        for (int i = 0; i < labels.length; i++) {
            int sectionX = panelX + leftPadding + i * sectionWidth;

            g2.setFont(labelFont);
            g2.setColor(new Color(235, 235, 235));
            g2.drawString(labels[i], sectionX, baseline);

            int labelWidth = labelMetrics.stringWidth(labels[i]);

            g2.setFont(valueFont);
            g2.setColor(Color.WHITE);
            g2.drawString(values[i], sectionX + labelWidth + 8, baseline);
        }
        g2.dispose();
    }

    private void drawInfoButton(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        int diameter = 34;
        int margin = 20;
        int x = getWidth() - diameter - margin;
        int y = 20;

        infoButtonBounds = new Rectangle(x, y, diameter, diameter);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // rounded background
        g2.setColor(new Color(255, 255, 255, 90));
        g2.fillOval(x, y, diameter, diameter);

        // border
        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawOval(x, y, diameter, diameter);

        // Letter i
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.setColor(Color.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        String text = "i";
        int textX = x + (diameter - fm.stringWidth(text)) / 2;
        int textY = y + ((diameter - fm.getHeight()) / 2) + fm.getAscent() - 1;

        g2.drawString(text, textX, textY);
        g2.dispose();
    }
    private void drawInstructionOverlay(Graphics g) {
        Graphics2D g2 = createOverlayGraphics(g, 150);

        int panelWidth = 980;
        int panelHeight = 560;
        int x = (getWidth() - panelWidth) / 2;
        int y = (getHeight() - panelHeight) / 2;

        drawOverlayPanel(g2, x, y, panelWidth, panelHeight, 28, 28);

        // title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g2.drawString("How to Play", x + 28, y + 42);

        // subtitle
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("Learn the controls, objectives, and what each game element means.", x + 30, y + 68);

        // split 2 parts
        int leftX = x + 40;
        int rightX = x + 540;
        int startY = y + 120;

        // left: how to play
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        g2.drawString("Instructions", leftX, startY);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(new Color(235, 235, 235));

        int lineGap = 45;
        int textY = startY + 38;

        g2.drawString("• Use W, A, S, D to move up, left, down, right your character.", leftX, textY);
        textY += lineGap;

        g2.drawString("• Collect all regular rewards in order to exit.", leftX, textY);
        textY += lineGap;

        g2.drawString("• Bonus rewards give you extra score.", leftX, textY);
        textY += lineGap;

        g2.drawString("• Bonus rewards spawns and disappears every 6 moves.", leftX, textY);
        textY += lineGap;

        g2.drawString("• The game is over if caught by an enemy.", leftX, textY);
        textY += lineGap;

        g2.drawString("• Traps can be collected and deployed via 'SPACE' bar.", leftX, textY);
        textY += lineGap;

        g2.drawString("• Deployed traps can kill enemies and players.", leftX, textY);
        textY += lineGap;

        g2.drawString("• Keep an eye on score, steps, traps, and remaining time.", leftX, textY);

        // right: game elements
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(Color.WHITE);
        g2.drawString("Game Elements", rightX, startY);

        int itemY = startY + 42;
        int textOffsetX = 52;
        int rowGap = 50;
        int oldTileSize = titleSize;
        titleSize = 36;

        // Exit
        itemY += rowGap;
        drawExitTile(g2, rightX, itemY - 18);
        g2.drawString("Exit - activates when all regular rewards are collected", rightX + textOffsetX, itemY + 6);

        // Enemy
        itemY += rowGap;
        drawGhostEnemy(g2, rightX, itemY - 18);
        g2.drawString("Enemy - avoid being caught", rightX + textOffsetX, itemY + 6);

        // Regular Reward
        itemY += rowGap;
        drawCoinReward(g2, rightX, itemY - 18);
        g2.drawString("Regular Reward - 10 pts", rightX + textOffsetX, itemY + 6);

        // Bonus Reward
        itemY += rowGap;
        drawStarReward(g2, rightX, itemY - 18);
        g2.drawString("Bonus Reward - 25 pts", rightX + textOffsetX, itemY + 6);

        // Trap
        itemY += rowGap;
        drawHoleTrap(g2, rightX, itemY - 18);
        g2.drawString("Trap - 50 pts per enemy killed", rightX + textOffsetX, itemY + 6);

        g2.setColor(new Color(210, 210, 210));
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        g2.drawString("Click the i button again to close this panel.", x + 40, y + panelHeight - 28);

        titleSize = oldTileSize;
    }

    private void drawEndGameOverlay(Graphics g) {
        Graphics2D g2 = createOverlayGraphics(g, 170);

        int panelWidth = 760;
        int panelHeight = 420;
        int x = (getWidth() - panelWidth) / 2;
        int y = (getHeight() - panelHeight) / 2;
        int centerX = x + panelWidth / 2;

        drawOverlayPanel(g2, x, y, panelWidth, panelHeight, 28, 28);

        String title = endResult.isWin() ? "YOU WIN" : "GAME OVER";
        Color titleColor = endResult.isWin()
                ? new Color(95, 220, 120)
                : new Color(255, 95, 95);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 40));
        FontMetrics titleFm = g2.getFontMetrics();
        g2.setColor(titleColor);
        g2.drawString(title, centerX - titleFm.stringWidth(title) / 2, y + 65);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        FontMetrics bodyFm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);

        int textY = y + 135;
        int lineGap = 52;

        String line1 = "Score: " + endResult.getScoreCount().getTotalScore();
        g2.drawString(line1, centerX - bodyFm.stringWidth(line1) / 2, textY);
        textY += lineGap;

        String line2 = "Time Left: " + endResult.getTime();
        g2.drawString(line2, centerX - bodyFm.stringWidth(line2) / 2, textY);
        textY += lineGap;

        String line3 = "Steps Used: " + engine.getStepsUsed();
        g2.drawString(line3, centerX - bodyFm.stringWidth(line3) / 2, textY);
        textY += lineGap;

        String line4 = "Rewards Left: " + engine.getRemainingRewards();
        g2.drawString(line4, centerX - bodyFm.stringWidth(line4) / 2, textY);
        textY += lineGap;

        if (endResult.isLose() && endResult.getLoseReason() != null) {
            String reason = "Reason: " + formatLoseReason(endResult.getLoseReason());
            g2.drawString(reason, centerX - bodyFm.stringWidth(reason) / 2, textY);
            textY += lineGap;
        }

        g2.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        FontMetrics hintFm = g2.getFontMetrics();
        g2.setColor(new Color(220, 220, 220));
        String hint = "Press ESC to return to menu.";
        g2.drawString(hint, centerX - hintFm.stringWidth(hint) / 2, y + panelHeight - 28);

        g2.dispose();
    }

    //helper function
    private String formatLoseReason(LoseReason reason) {return switch (reason) {
            case ENEMY_COLLISION -> "Caught by an enemy";
            case TRAP_COLLISION -> "Triggered a trap";
            case TIME_OUT -> "Time ran out";
            case SCORE_NEGATIVE -> "Score dropped below zero";
        };}

    private void drawBottomControlBar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int barHeight = 48;
        int marginX = 28;
        int bottomMargin = 16;

        int x = marginX;
        int y = getHeight() - barHeight - bottomMargin;
        int width = getWidth() - marginX * 2;

        // background
        g2.setColor(new Color(18, 18, 22, 235));
        g2.fillRoundRect(x, y, width, barHeight, 18, 18);

        // border
        g2.setColor(new Color(255, 255, 255, 70));
        g2.drawRoundRect(x, y, width, barHeight, 18, 18);

        int sectionWidth = width / 3;
        int centerY = y + barHeight / 2 + 8;

        drawControlItem(g2, x, sectionWidth, centerY, "P", "Pause");
        drawControlItem(g2, x + sectionWidth, sectionWidth, centerY, "R", "Restart");
        drawControlItem(g2, x + sectionWidth * 2, sectionWidth, centerY, "Q", "Quit");

        g2.dispose();
    }

    // helper function
    private void drawControlItem(Graphics2D g2, int sectionX, int sectionWidth,
                                         int baselineY, String key, String label) {
        Font keyFont = new Font("Segoe UI", Font.BOLD, 24);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 22);

        int boxSize = 40;
        int gap1 = 14; // gap from box to :
        int gap2 = 16; // gap from : to label

        g2.setFont(keyFont);
        FontMetrics keyFm = g2.getFontMetrics();

        g2.setFont(textFont);
        FontMetrics textFm = g2.getFontMetrics();

        int colonWidth = textFm.stringWidth(":");
        int labelWidth = textFm.stringWidth(label);

        int totalWidth = boxSize + gap1 + colonWidth + gap2 + labelWidth;
        int startX = sectionX + (sectionWidth - totalWidth) / 2;
        int boxY = baselineY - 28;

        // box
        g2.setColor(new Color(245, 245, 245));
        g2.fillRoundRect(startX, boxY, boxSize, boxSize, 10, 10);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(startX, boxY, boxSize, boxSize, 10, 10);

        // key
        g2.setFont(keyFont);
        int keyX = startX + (boxSize - keyFm.stringWidth(key)) / 2;
        int keyY = boxY + ((boxSize - keyFm.getHeight()) / 2) + keyFm.getAscent() - 1;
        g2.drawString(key, keyX, keyY);

        // :
        int colonX = startX + boxSize + gap1;
        g2.setFont(textFont);
        g2.setColor(Color.WHITE);
        g2.drawString(":", colonX, baselineY);

        // label
        int labelX = colonX + colonWidth + gap2;
        g2.drawString(label, labelX, baselineY);
    }

    //when players press restart/quit, make sure they confirm their choice
    private void handleConfirmInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_Y) {
            if ("restart".equals(confirmAction)) {
                initializeGame();
                showConfirmOverlay = false;
                confirmAction = null;
                showEndOverlay = false;
                endResult = null;
                isPaused = false;
                gameTimer.start();
                requestFocusInWindow();
                repaint();
            } else if ("quit".equals(confirmAction)) {
                isPaused = false;
                showConfirmOverlay = false;
                confirmAction = null;
                gameTimer.stop();
                returnToMenu.run();
            }
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_N || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            showConfirmOverlay = false;
            confirmAction = null;
            repaint();
        }
    }

    private void drawConfirmOverlay(Graphics g) {
        Graphics2D g2 = createOverlayGraphics(g, 165);

        int panelWidth = 520;
        int panelHeight = 220;
        int x = (getWidth() - panelWidth) / 2;
        int y = (getHeight() - panelHeight) / 2;

        drawOverlayPanel(g2, x, y, panelWidth, panelHeight, 24, 24);

        String title = "Confirm Action";
        String question = "restart".equals(confirmAction)
                ? "Are you sure you want to restart this level?"
                : "Are you sure you want to quit to menu?";

        g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
        FontMetrics titleFm = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        g2.drawString(title, x + (panelWidth - titleFm.stringWidth(title)) / 2, y + 52);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 21));
        FontMetrics bodyFm = g2.getFontMetrics();
        g2.drawString(question, x + (panelWidth - bodyFm.stringWidth(question)) / 2, y + 108);

        g2.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        String hint = "Press Y to confirm, N or ESC to cancel.";
        FontMetrics hintFm = g2.getFontMetrics();
        g2.setColor(new Color(220, 220, 220));
        g2.drawString(hint, x + (panelWidth - hintFm.stringWidth(hint)) / 2, y + 170);

        g2.dispose();
    }

    private void handlePauseInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            isPaused = false;
            repaint();
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_R) {
            openConfirmOverlay("restart");
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_Q) {
            openConfirmOverlay("quit");
        }
    }
    private void drawPauseOverlay(Graphics g) {
        Graphics2D g2 = createOverlayGraphics(g, 165);

        int panelWidth = 520;
        int panelHeight = 250;
        int x = (getWidth() - panelWidth) / 2;
        int y = (getHeight() - panelHeight) / 2;

        drawOverlayPanel(g2, x, y, panelWidth, panelHeight, 24, 24);

        g2.setColor(new Color(255, 220, 120));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 34));
        FontMetrics titleFm = g2.getFontMetrics();
        String title = "PAUSED";
        g2.drawString(title, x + (panelWidth - titleFm.stringWidth(title)) / 2, y + 56);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        FontMetrics bodyFm = g2.getFontMetrics();

        String line1 = "Press P to resume";
        String line2 = "Press R to restart";
        String line3 = "Press Q to quit";

        g2.drawString(line1, x + (panelWidth - bodyFm.stringWidth(line1)) / 2, y + 115);
        g2.drawString(line2, x + (panelWidth - bodyFm.stringWidth(line2)) / 2, y + 155);
        g2.drawString(line3, x + (panelWidth - bodyFm.stringWidth(line3)) / 2, y + 195);

        g2.dispose();
    }

    private void drawExitTile(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        int size = (int) (titleSize * 0.72);
        int px = x + (titleSize - size) / 2;
        int py = y + (titleSize - size) / 2;

        // white tile
        g2.setColor(new Color(242, 242, 242));
        g2.fillRoundRect(px, py, size, size, 8, 8);
        g2.dispose();
    }
}
