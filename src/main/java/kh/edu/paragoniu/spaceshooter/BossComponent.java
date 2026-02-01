package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Random;

/**
 * Boss Component with integrated health bar UI
 * Works with your existing SpaceFactory boss spawn
 */
public class BossComponent extends Component {

    // Boss movement
    private static final double SPEED = 60;
    private static final double CHANGE_DIR_TIME = 2.0;
    private double directionX;
    private double timeSinceLastChange = 0;
    private double shootCooldown = 0;
    private static final double SHOOT_INTERVAL = 3;
    private final Random random = new Random();

    // Boss health (will be set from your code)
    private int maxHealth = 200;  // Default
    private int currentHealth = 200;

    // Health bar UI
    private BossHealthBar healthBar;

    // Constructor without parameters (matches your factory)
    public BossComponent() {
        // Health will be set via setter or from game variable
    }

    // Constructor with health parameter (for flexibility)
    public BossComponent(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    @Override
    public void onAdded() {
        randomizeDirection();

        // Get health from game variable if it was set
        if (FXGL.getWorldProperties().exists("bossHP")) {
            int hp = FXGL.geti("bossHP");
            if (hp > 0) {
                this.maxHealth = hp;
                this.currentHealth = hp;
            }
        }

        // Create and show health bar
        healthBar = new BossHealthBar("THE BOSS", maxHealth);
        FXGL.addUINode(healthBar);
        healthBar.show();

        // Update game variable
        FXGL.set("bossHP", currentHealth);
    }

    @Override
    public void onUpdate(double tpf) {
        // Boss shooting
        shootCooldown -= tpf;
        if (shootCooldown <= 0) {
            shoot();  // This will create 3 bullets
            shootCooldown = SHOOT_INTERVAL;
        }

        // Move horizontally
        entity.translateX(directionX * SPEED * tpf);

        // Keep boss near top
        if (entity.getY() > 150) {
            entity.setY(150);
        }
        if (entity.getY() < 50) {
            entity.setY(50);
        }

        // Change direction
        timeSinceLastChange += tpf;
        if (timeSinceLastChange >= CHANGE_DIR_TIME) {
            randomizeDirection();
            timeSinceLastChange = 0;
        }

        // Screen bounds
        double screenWidth = FXGL.getAppWidth();
        if (entity.getX() <= 0 || entity.getRightX() >= screenWidth) {
            directionX *= -1;
        }

        // Update health bar
        if (healthBar != null) {
            healthBar.updateHealth(currentHealth, maxHealth);
        }
    }

    @Override
    public void onRemoved() {
        // Remove health bar when boss dies
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

        // Shoot 3 bullets spread across boss width
        FXGL.spawn("boss_bullet", centerX - width/3, bottomY);  // Left
        FXGL.spawn("boss_bullet", centerX, bottomY);           // Center
        FXGL.spawn("boss_bullet", centerX + width/3, bottomY);  // Right
    }

    /**
     * Damage the boss (called from collision handler)
     */
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
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int health) {
        this.maxHealth = health;
        this.currentHealth = health;
    }

    // ========================================
    // INNER CLASS: Boss Health Bar UI
    // ========================================

    /**
     * Fancy boss health bar (Elden Ring style)
     */
    private static class BossHealthBar extends StackPane {

        private Rectangle background;
        private Rectangle healthBar;
        private Rectangle border;
        private Text bossNameText;

        private static final double BAR_WIDTH = 600;
        private static final double BAR_HEIGHT = 30;

        public BossHealthBar(String bossName, int maxHealth) {
            // Background (dark)
            background = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
            background.setFill(Color.rgb(20, 20, 20, 0.9));

            // Health bar (red)
            healthBar = new Rectangle(BAR_WIDTH - 4, BAR_HEIGHT - 4);
            healthBar.setFill(Color.rgb(180, 40, 40));

            // Border (gold)
            border = new Rectangle(BAR_WIDTH, BAR_HEIGHT);
            border.setFill(Color.TRANSPARENT);
            border.setStroke(Color.rgb(200, 170, 100));
            border.setStrokeWidth(3);

            // Boss name
            bossNameText = new Text(bossName);
            bossNameText.setFont(Font.font("Serif", FontWeight.BOLD, 20));
            bossNameText.setFill(Color.rgb(220, 190, 120));
            bossNameText.setTranslateY(-BAR_HEIGHT - 5);

            getChildren().addAll(background, healthBar, border, bossNameText);

            // Position at top center
            setTranslateX((FXGL.getAppWidth() - BAR_WIDTH) / 2);
            setTranslateY(80);
            setVisible(false);
        }

        /**
         * Update health bar
         */
        public void updateHealth(int currentHealth, int maxHealth) {
            double healthPercent = (double) currentHealth / maxHealth;
            double newWidth = (BAR_WIDTH - 4) * healthPercent;

            healthBar.setWidth(Math.max(0, newWidth));

            // Change color based on health
            if (healthPercent > 0.6) {
                healthBar.setFill(Color.rgb(180, 40, 40)); // Red
            } else if (healthPercent > 0.3) {
                healthBar.setFill(Color.rgb(200, 100, 30)); // Orange
            } else {
                healthBar.setFill(Color.rgb(220, 60, 60)); // Bright red
            }
        }

        /**
         * Show with fade-in animation
         */
        public void show() {
            setVisible(true);
            setOpacity(0);

            FXGL.animationBuilder()
                    .duration(javafx.util.Duration.seconds(0.5))
                    .fadeIn(this)
                    .buildAndPlay();
        }

        /**
         * Hide with fade-out animation
         */
        public void hide() {
            FXGL.animationBuilder()
                    .duration(javafx.util.Duration.seconds(0.5))
                    .fadeOut(this)
                    .buildAndPlay();
        }
    }
}