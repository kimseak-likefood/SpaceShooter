package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class BossWarningFX {

    private Rectangle overlay;

    public BossWarningFX() {
        // Full-screen red overlay
        overlay = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.RED);
        overlay.setOpacity(0);               // invisible initially
        FXGL.addUINode(overlay);             // add to UI layer
        overlay.toFront();                    // ensure it is on top of other UI nodes
    }

    public void play() {
        // 1️⃣ Red flashing overlay
        overlay.toFront();                    // just in case
        FXGL.animationBuilder()
                .duration(Duration.seconds(0.15)) // fast flash
                .repeat(6)
                .autoReverse(true)
                .fade(overlay)
                .from(0.0)
                .to(0.7)
                .build()
                .start();

        // 2️⃣ BOSS INCOMING text
        Text warningText = new Text("BOSS INCOMING!");
        warningText.setFill(Color.YELLOW);
        warningText.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        warningText.setTranslateX(FXGL.getAppWidth() / 2.0 - 180);
        warningText.setTranslateY(FXGL.getAppHeight() / 2.0);
        FXGL.addUINode(warningText);
        warningText.toFront();                // ensure text is on top

        // Remove text after 1.5 seconds
        FXGL.getGameTimer().runOnceAfter(() -> FXGL.removeUINode(warningText), Duration.seconds(1.5));

        // 3️⃣ Shake the viewport
        FXGL.getGameScene().getViewport().shake(15, 1.0);
    }
}
