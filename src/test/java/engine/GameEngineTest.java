package engine;

import actor.Direction;
import actor.MainCharacter;
import actor.MovingEnemy;
import model.Board;
import model.Position;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class GameEngineTest {

    @Test
    void start_withNullBoard_throwsException() {
        GameEngine engine = new GameEngine();
        GameState gameState = new GameState(0, 10);

        assertThrows(IllegalArgumentException.class, () ->
                engine.start(null, null, null, null, gameState));
    }

    @Test
    void start_withNullPlayer_throwsException() {
        GameEngine engine = new GameEngine();
        Board board = new Board();
        GameState gameState = new GameState(0, 10);

        assertThrows(IllegalArgumentException.class, () ->
                engine.start(board, null, null, null, gameState));
    }

    @Test
    void start_withNullGameState_throwsException() {
        GameEngine engine = new GameEngine();
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);

        assertThrows(IllegalArgumentException.class, () ->
                engine.start(board, null, player, null, null));
    }

    @Test
    void start_withValidInputs_setsStatusToRunning() {
        GameEngine engine = new GameEngine();
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        engine.start(board, null, player, new ArrayList<>(), gameState);

        assertEquals(Status.RUNNING, gameState.getStatus());
        assertEquals(0, engine.getTickCount());
    }

    @Test
    void turnFlow_whenGameNotRunning_doesNothing() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        // start() forces RUNNING, so set non-running status after start
        gameState.setStatus(Status.WON);

        Position before = player.getPosition();
        int beforeSteps = gameState.getStepUsed();
        int beforeTicks = engine.getTickCount();

        PlayerTurn turn = new PlayerTurn(Direction.RIGHT, Action.NONE);
        engine.turnFlow(turn);

        assertEquals(before, player.getPosition());
        assertEquals(beforeSteps, gameState.getStepUsed());
        assertEquals(beforeTicks, engine.getTickCount());
    }

    @Test
    void turnFlow_whenPlayerTurnIsNull_doesNotCrashOrChangeSteps() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        Position before = player.getPosition();
        int beforeSteps = gameState.getStepUsed();

        assertDoesNotThrow(() -> engine.turnFlow(null));

        assertEquals(before, player.getPosition());
        assertEquals(beforeSteps, gameState.getStepUsed());
    }

    @Test
    void turnFlow_whenValidMove_updatesPositionAndStepCount() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.RIGHT, Action.NONE);

        engine.turnFlow(turn);

        assertEquals(new Position(1, 2), player.getPosition());
        assertEquals(1, gameState.getStepUsed());
        assertEquals(1, engine.getTickCount());
    }

    @Test
    void turnFlow_whenDirectionIsNone_doesNotMoveOrIncrementSteps() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        Position before = player.getPosition();

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.NONE);
        engine.turnFlow(turn);

        assertEquals(before, player.getPosition());
        assertEquals(0, gameState.getStepUsed());
        assertEquals(1, engine.getTickCount());
    }

    @Test
    void turnFlow_whenDirectionIsNull_doesNotMoveOrIncrementSteps() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        Position before = player.getPosition();

        engine.turnFlow(new PlayerTurn(null, Action.NONE));

        assertEquals(before, player.getPosition());
        assertEquals(0, gameState.getStepUsed());
    }

    @Test
    void turnFlow_whenActionIsNone_doesNotPlaceTrap() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.NONE);
        engine.turnFlow(turn);

        assertFalse(engine.getTrapManager().hasTrapAt(player.getPosition()));
        assertEquals(0, engine.getTrapManager().getTraps().size());
    }

    @Test
    void turnFlow_whenActionIsNull_doesNothing() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, null);
        engine.turnFlow(turn);

        assertEquals(0, engine.getTrapManager().getTraps().size());
    }

    @Test
    void turnFlow_whenEnemyListIsNull_doesNotCrash() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, null, gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.NONE);

        assertDoesNotThrow(() -> engine.turnFlow(turn));
    }

    @Test
    void turnFlow_whenPlaceTrapButPlayerHasNoTrap_doesNotPlaceTrap() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.PLACE_TRAP);
        engine.turnFlow(turn);

        assertFalse(engine.getTrapManager().hasTrapAt(player.getPosition()));
        assertEquals(0, engine.getTrapManager().getTraps().size());
    }

    @Test
    void turnFlow_whenPlaceTrapAndPlayerHasTrap_placesTrapAtPlayerPosition() {
        Board board = new Board();
        Position start = new Position(1, 1);
        MainCharacter player = new MainCharacter(start, board);
        player.addTrap(); // assuming this exists in your MainCharacter

        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.PLACE_TRAP);
        engine.turnFlow(turn);

        assertTrue(engine.getTrapManager().hasTrapAt(start));
        assertEquals(1, engine.getTrapManager().getTraps().size());
    }

    @Test
    void turnFlow_whenTrapAlreadyExistsAtPlayerPosition_doesNotPlaceSecondTrap() {
        Board board = new Board();
        Position start = new Position(1, 1);
        MainCharacter player = new MainCharacter(start, board);
        player.addTrap();
        player.addTrap();

        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.PLACE_TRAP);

        engine.turnFlow(turn);
        engine.turnFlow(turn);

        assertTrue(engine.getTrapManager().hasTrapAt(start));
        assertEquals(1, engine.getTrapManager().getTraps().size());
    }

    @Test
    void turnFlow_whenEnemyCollidesWithPlayer_setsLost() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(2, 2), board);
        GameState gameState = new GameState(0, 10);

        List<MovingEnemy> enemies = new ArrayList<>();
        enemies.add(new MovingEnemy(new Position(2, 3), board, 1));

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, enemies, gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.NONE);

        engine.turnFlow(turn);

        assertEquals(Status.LOST, gameState.getStatus());
    }

    @Test
    void turnFlow_whenPlayerTriggersTrap_setsLost() {
        Board board = new Board();
        Position trapPos = new Position(1, 1);
        MainCharacter player = new MainCharacter(trapPos, board);

        player.addTrap();

        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        engine.turnFlow(new PlayerTurn(Direction.NONE, Action.PLACE_TRAP));

        assertTrue(engine.getTrapManager().hasTrapAt(trapPos));
        assertEquals(Status.RUNNING, gameState.getStatus());

        engine.turnFlow(new PlayerTurn(Direction.RIGHT, Action.NONE));

        assertEquals(new Position(1, 2), player.getPosition());
        assertEquals(Status.RUNNING, gameState.getStatus());

        engine.turnFlow(new PlayerTurn(Direction.LEFT, Action.NONE));

        assertEquals(Status.LOST, gameState.getStatus());

        Result result = engine.buildResult();
        assertEquals(Status.LOST, result.getStatus());
        assertEquals(LoseReason.TRAP_COLLISION, result.getLoseReason());
    }

    @Test
    void turnFlow_whenTimeAlreadyZero_setsLost() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 0);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.RIGHT, Action.NONE);

        engine.turnFlow(turn);

        assertEquals(Status.LOST, gameState.getStatus());
    }

    @Test
    void turnFlow_whenPlayerStartsAtExitWithNoRewards_setsWon() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(board.getExitPos(), board);
        GameState gameState = new GameState(0, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.NONE);

        engine.turnFlow(turn);

        assertEquals(Status.WON, gameState.getStatus());
    }

    @Test
    void turnFlow_whenPlayerAtExitButRewardsRemain_doesNotSetWon() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(board.getExitPos(), board);
        GameState gameState = new GameState(1, 10);

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.NONE);

        engine.turnFlow(turn);

        assertNotEquals(Status.WON, gameState.getStatus());
    }

    @Test
    void turnFlow_beforeStart_doesNothing() {
        GameEngine engine = new GameEngine();
        PlayerTurn turn = new PlayerTurn(Direction.NONE, Action.NONE);

        assertDoesNotThrow(() -> engine.turnFlow(turn));
    }

    @Test
    void turnFlow_whenScoreIsNegative_setsLost() {
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        gameState.addPenalty(1000); // large enough to guarantee total score < 0

        GameEngine engine = new GameEngine();
        engine.start(board, null, player, new ArrayList<>(), gameState);

        engine.turnFlow(new PlayerTurn(Direction.NONE, Action.NONE));

        assertEquals(Status.LOST, gameState.getStatus());
        assertEquals(LoseReason.SCORE_NEGATIVE, engine.buildResult().getLoseReason());
    }

    @Test
    void buildResult_whenGameStateNull_throwsException() {
        GameEngine engine = new GameEngine();

        assertThrows(IllegalStateException.class, engine::buildResult);
    }

    @Test
    void buildResult_whenStatusIsWon_returnsWonResult() {
        GameEngine engine = new GameEngine();
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        engine.start(board, null, player, new ArrayList<>(), gameState);

        gameState.setStatus(Status.WON);

        Result result = engine.buildResult();

        assertEquals(Status.WON, result.getStatus());
        assertEquals(gameState.getTime(), result.getTime());
        assertEquals(gameState.getScoreCount(), result.getScoreCount());
        assertNull(result.getLoseReason());
    }

    @Test
    void buildResult_whenStatusIsLost_returnsLostResult() {
        GameEngine engine = new GameEngine();
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        engine.start(board, null, player, new ArrayList<>(), gameState);

        gameState.setStatus(Status.LOST);

        Result result = engine.buildResult();

        assertEquals(Status.LOST, result.getStatus());
        assertEquals(gameState.getTime(), result.getTime());
        assertEquals(gameState.getScoreCount(), result.getScoreCount());
        // depending on your actual Result API and how loseReason is set,
        // this may be null unless the engine reached a real lose path first
    }

    @Test
    void buildResult_whenStatusIsRunning_throwsException() {
        GameEngine engine = new GameEngine();
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(0, 10);

        engine.start(board, null, player, new ArrayList<>(), gameState);

        assertEquals(Status.RUNNING, gameState.getStatus());

        assertThrows(IllegalStateException.class, engine::buildResult);
    }

    @Test
    void getters_afterStart_returnExpectedValues() {
        GameEngine engine = new GameEngine();
        Board board = new Board();
        MainCharacter player = new MainCharacter(new Position(1, 1), board);
        GameState gameState = new GameState(3, 10);

        List<MovingEnemy> enemies = new ArrayList<>();
        MovingEnemy enemy = new MovingEnemy(new Position(2, 2), board, 1);
        enemies.add(enemy);

        engine.start(board, null, player, enemies, gameState);

        assertSame(board, engine.getBoard());
        assertSame(player, engine.getPlayer());
        assertSame(gameState, engine.getGameState());

        assertEquals(1, engine.getEnemies().size());
        assertSame(enemy, engine.getEnemies().get(0));

        assertEquals(gameState.getScoreCount().getTotalScore(), engine.getCurrentScore());
        assertEquals(gameState.getStatus(), engine.getStatus());
        assertEquals(gameState.getRegularRewardsRemaining(), engine.getRemainingRewards());
        assertEquals(gameState.getTime(), engine.getTimeLeft());
        assertEquals(gameState.getStepUsed(), engine.getStepsUsed());
    }
}
