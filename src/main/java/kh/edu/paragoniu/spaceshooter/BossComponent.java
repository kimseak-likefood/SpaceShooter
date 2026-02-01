package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

public class BossComponent extends Component {

    // Boss movement
    private static final double SPEED = 60;
    private static final double CHANGE_DIR_TIME = 2.0;
    private double directionX;
    private double timeSinceLastChange = 0;
    private double shootCooldown = 0;
    private static final double SHOOT_INTERVAL = 3;
    private final Random random = new Random();

    // Boss health
    private int maxHealth = 200;
    private int currentHealth = 200;

    // Health bar UI
    private BossHealthBar healthBar;

    // Prevent double death logic
    private boolean bossDead = false;

    public BossComponent() {}

    public BossComponent(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    @Override
    public void onAdded() {
        randomizeDirection();

        if (FXGL.getWorldProperties().exists("bossHP")) {
            int hp = FXGL.geti("bossHP");
            if (hp > 0) {
                maxHealth = hp;
                currentHealth = hp;
            }
        }

        healthBar = new BossHealthBar("THE BOSS", maxHealth);
        FXGL.addUINode(healthBar);
        healthBar.show();

        FXGL.set("bossHP", currentHealth);
    }

    @Override
    public void onUpdate(double tpf) {
        if (bossDead)
            return;

        shootCooldown -= tpf;
        if (shootCooldown <= 0) {
            shoot();
            shootCooldown = SHOOT_INTERVAL;
        }

        entity.translateX(directionX * SPEED * tpf);

        if (entity.getY() > 150) entity.setY(150);
        if (entity.getY() < 50) entity.setY(50);

        timeSinceLastChange += tpf;
        if (timeSinceLastChange >= CHANGE_DIR_TIME) {
            randomizeDirection();
            timeSinceLastChange = 0;
        }

        double screenWidth = FXGL.getAppWidth();
        if (entity.getX() <= 0 || entity.getRightX() >= screenWidth) {
            directionX *= -1;
        }

        if (healthBar != null) {
            healthBar.updateHealth(currentHealth, maxHealth);
        }
    }

    @Override
    public void onRemoved() {
        if (healthBar != null) {
            healthBar.hide();
            FXGL.removeUINode(healthBar);
        }
    }

    private void randomizeDirection() {
        directionX = random.nextBoolean() ? 1 : -1;
    }

    private void shoot() {
        double centerX = entity.getCenter().getX();
        double bottomY = entity.getBottomY();
        double width = entity.getWidth();

        FXGL.spawn("boss_bullet", centerX - width / 3, bottomY);
        FXGL.spawn("boss_bullet", centerX, bottomY);
        FXGL.spawn("boss_bullet", centerX + width / 3, bottomY);
    }

    // =====================
    // DAMAGE & DEATH LOGIC
    // =====================

    public void damage(int amount) {
        currentHealth -= amount;
        if (currentHealth < 0) {
            currentHealth = 0;
        }

        // Update game variable
        FXGL.set("bossHP", currentHealth);

        // Check if defeated
        if (currentHealth <= 0) {
            FXGL.inc("score", 100); // Big reward!
            entity.removeFromWorld();

            // ===== SHOW "YOU WON!" =====
            Text winText = FXGL.getUIFactoryService()
                    .newText("YOU WON!", Color.GOLD, 72);

            // Temporarily add to scene to get proper bounds
            FXGL.getGameScene().addUINode(winText);

            // Center text
            winText.setTranslateX((FXGL.getAppWidth() - winText.getLayoutBounds().getWidth()) / 2);
            winText.setTranslateY((FXGL.getAppHeight() - winText.getLayoutBounds().getHeight()) / 2);

            // Fade-in animation
            winText.setOpacity(0);
            FXGL.animationBuilder()
                    .duration(javafx.util.Duration.seconds(1))
                    .fadeIn(winText)
                    .buildAndPlay();

            // Remove after 2 seconds and exit game
            FXGL.getGameTimer().runOnceAfter(() -> {
                FXGL.getGameController().exit(); // Close game
            }, javafx.util.Duration.seconds(2));
        }
    }



    private void onBossKilled() {
        entity.removeFromWorld();

        // Victory text
        Text winText = FXGL.getUIFactoryService()
                .newText("BOSS DEFEATED!", Color.GOLD, 48);

        winText.setTranslateX(
                FXGL.getAppWidth() / 2.0 - winText.getLayoutBounds().getWidth() / 2
        );
        winText.setTranslateY(FXGL.getAppHeight() / 2.0);

        FXGL.addUINode(winText);

        FXGL.animationBuilder()
                .duration(Duration.seconds(0.5))
                .fadeIn(winText)
                .buildAndPlay();

        // End game after delay
        FXGL.getGameTimer().runOnceAfter(() -> {
            // Remove the exit
            FXGL.getGameTimer().runOnceAfter(() -> {
                // Just fade out the text after 2 seconds
                FXGL.animationBuilder()
                        .duration(javafx.util.Duration.seconds(1))
                        .fadeOut(winText)
                        .buildAndPlay();
            }, javafx.util.Duration.seconds(7));

            // Immediately closes game

        }, Duration.seconds(2.5));
    }

    // =====================
    // HEALTH BAR UI
    // =====================

    private static class BossHealthBar extends StackPane {

        private Rectangle background;
        private Rectangle healthBar;
        private Rectangle border;
        private Text bossNameText;

        private static final double BAR_WIDTH = 600;
        private static final double BAR_HEIGHT = 30;

        public BossHealthBar(String bossName, int maxHealth) {
            background = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
            background.setFill(Color.rgb(20, 20, 20, 0.9));

            healthBar = new Rectangle(BAR_WIDTH - 4, BAR_HEIGHT - 4);
            healthBar.setFill(Color.rgb(180, 40, 40));

            border = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
            border.setFill(Color.TRANSPARENT);
            border.setStroke(Color.rgb(200, 170, 100));
            border.setStrokeWidth(3);

            bossNameText = new Text(bossName);
            bossNameText.setFont(Font.font("Serif", FontWeight.BOLD, 20));
            bossNameText.setFill(Color.rgb(220, 190, 120));
            bossNameText.setTranslateY(-BAR_HEIGHT - 5);

            getChildren().addAll(background, healthBar, border, bossNameText);

            setTranslateX((FXGL.getAppWidth() - BAR_WIDTH) / 2);
            setTranslateY(80);
            setVisible(false);
        }

        public void updateHealth(int currentHealth, int maxHealth) {
            double percent = (double) currentHealth / maxHealth;
            healthBar.setWidth((BAR_WIDTH - 4) * percent);
        }

        public void show() {
            setVisible(true);
            setOpacity(0);
            FXGL.animationBuilder()
                    .duration(Duration.seconds(0.5))
                    .fadeIn(this)
                    .buildAndPlay();
        }

        public void hide() {
            FXGL.animationBuilder()
                    .duration(Duration.seconds(0.5))
                    .fadeOut(this)
                    .buildAndPlay();
        }
    }
}
