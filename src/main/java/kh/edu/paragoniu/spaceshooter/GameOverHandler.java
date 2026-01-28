package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;

public class GameOverHandler {

    public static void handleGameOver() {
        int finalScore = FXGL.geti("score");

        FXGL.getDialogService().showInputBox(
                "Game Over! Your score: " + finalScore + "\n\nEnter your name:",
                name -> {
                    if (name != null && !name.trim().isEmpty()) {
                        LeaderboardManager.addScore(name.trim(), finalScore);
                        LeaderboardManager.showLeaderboard();
                    }
                    FXGL.getGameController().gotoMainMenu();
                }
        );
    }
}