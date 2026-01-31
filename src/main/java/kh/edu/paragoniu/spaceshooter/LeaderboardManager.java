package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import java.util.*;

public class LeaderboardManager {

    private static List<ScoreEntry> leaderboard = new ArrayList<>();

    public static class ScoreEntry implements Comparable<ScoreEntry> {
        String name;
        int score;
        String date;

        public ScoreEntry(String name, int score, String date) {
            this.name = name;
            this.score = score;
            this.date = date;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }

    public static void addScore(String playerName, int score) {
        String date = java.time.LocalDate.now().toString();
        leaderboard.add(new ScoreEntry(playerName, score, date));
        Collections.sort(leaderboard);

        if (leaderboard.size() > 10) {
            leaderboard = leaderboard.subList(0, 10);
        }
    }

    public static void showLeaderboard() {
        StringBuilder text = new StringBuilder();
        text.append(" LEADERBOARD \n\n");

        if (leaderboard.isEmpty()) {
            text.append("No scores yet!");
        } else {
            for (int i = 0; i < leaderboard.size(); i++) {
                ScoreEntry entry = leaderboard.get(i);
                text.append(String.format("%d. %s - %d points (%s)\n",
                        i + 1, entry.name, entry.score, entry.date));
            }
        }

        FXGL.getDialogService().showMessageBox(text.toString());
    }
}