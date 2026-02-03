package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

//test kimseak sssss

public class SpaceShooter extends GameApplication {

    public enum EntityType {
        PLAYER, BULLET, OBSTACLE, ENEMY_SHIP, ENEMY_BULLET, ENEMY, BOSS
    }
    private int fireMode;
    private Entity player;
    private static final Point2D GUN_OFFSET = new Point2D(20, -10);
    private boolean bossSpawned = false;
    private Rectangle damageOverlay;


    private static final double PLAYER_SIZE = 75; // match ImageView size

    private void flashDamage() {
        damageOverlay.setOpacity(0.5); // flash red
        FXGL.getGameTimer().runOnceAfter(() -> damageOverlay.setOpacity(0), javafx.util.Duration.seconds(0.2));
    }




    // ADDED: Direction-aware boundary control

    private void keepPlayerInBoundsSmart() {

        PhysicsComponent physics = player.getComponent(PhysicsComponent.class);

        double x = player.getX();
        double y = player.getY();

        double maxX = FXGL.getAppWidth() - PLAYER_SIZE;
        double maxY = FXGL.getAppHeight() - PLAYER_SIZE;

        // LEFT wall
        if (x <= 0 && physics.getVelocityX() < 0) {
            physics.setVelocityX(0);
            player.setX(0);
        }

        // RIGHT wall
        if (x >= maxX && physics.getVelocityX() > 0) {
            physics.setVelocityX(0);
            player.setX(maxX);
        }

        // TOP wall
        if (y <= 0 && physics.getVelocityY() < 0) {
            physics.setVelocityY(0);
            player.setY(0);
        }

        // BOTTOM wall
        if (y >= maxY && physics.getVelocityY() > 0) {
            physics.setVelocityY(0);
            player.setY(maxY);
        }
    }






    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setHeight(800);
        gameSettings.setWidth(800);
        gameSettings.setTitle("SpaceShooterGame");
        gameSettings.setVersion("1.0");

        // Enable menu with Resume and custom options
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setGameMenuEnabled(true);
    }

    @Override
    protected  void initInput(){
        FXGL.getInput().addAction(new UserAction("LEFT") {
            @Override
            protected void onAction() {
                player.getComponent(PhysicsComponent.class).setVelocityX(-300);
            }
            @Override
            protected void onActionEnd(){
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.A);


        FXGL.getInput().addAction(new UserAction("RIGHT") {
            @Override
            protected void onAction() {
                player.getComponent(PhysicsComponent.class).setVelocityX(300);
            }
            @Override
            protected void onActionEnd(){
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Down") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PhysicsComponent.class).setVelocityY(300);
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityY(0);
            }
        }, KeyCode.S);

        FXGL.getInput().addAction(new UserAction("Up") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PhysicsComponent.class).setVelocityY(-300);
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityY(0);
            }
        }, KeyCode.W);
//
        final boolean[] isGamePaused = {false};

// Then in initInput() method:
        FXGL.getInput().addAction(new UserAction("Pause/Resume") {
            private boolean wasPausedByMenu = false;

            @Override
            protected void onActionBegin() {
                // If we previously paused via menu, just resume
                if (wasPausedByMenu) {
                    FXGL.getGameController().resumeEngine();
                    wasPausedByMenu = false;
                    return;
                }

                // Otherwise, pause the engine
                FXGL.getGameController().pauseEngine();

                // Set a flag so next ESC press will resume
                wasPausedByMenu = true;
            }
        }, KeyCode.ESCAPE);

        // Add leaderboard key
        FXGL.getInput().addAction(new UserAction("Show Leaderboard") {
            @Override
            protected void onActionBegin() {
                LeaderboardManager.showLeaderboard();
            }
        }, KeyCode.L);
    }

    @Override
    protected void initGame(){
        bossSpawned = false;
        getGameWorld().addEntityFactory(new SpaceFactory());

        // ADDED: Smart frame boundary enforcement

        FXGL.getGameTimer().runAtInterval(
                this::keepPlayerInBoundsSmart,
                Duration.seconds(0.001)
        );

        // Background
        try {
            Image bgImage = new Image(ClassLoader.getSystemResource("space_bg.gif").toString());
            javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(bgImage);
            imageView.setFitWidth(800);
            imageView.setFitHeight(800);

            FXGL.entityBuilder()
                    .view(imageView)
                    .zIndex(-999)
                    .buildAndAttach();
        } catch (Exception e) {
            // If image not found, use blue background
            FXGL.entityBuilder()
                    .view(new Rectangle(800, 800, Color.DARKBLUE))
                    .zIndex(-999)
                    .buildAndAttach();
        }

        // Physics component for player
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        // Load image and create ImageView
        Image playerImg = new Image(
                ClassLoader.getSystemResource("nyan_cat.png").toString()
        );
        ImageView playerView = new ImageView(playerImg);
        playerView.setFitWidth(75);
        playerView.setFitHeight(75);
        playerView.setPreserveRatio(true);
        playerView.setRotate(-90.0);

        // Create player entity
        player = FXGL.entityBuilder()
                .at(400, 710)
                .type(EntityType.PLAYER)
                .viewWithBBox(playerView)
                .with(physics)
                .with(new CollidableComponent(true))
                .buildAndAttach();

        // Auto-fire system with automatic upgrade based on score
        FXGL.getGameTimer().runAtInterval(() -> {

            Point2D center = player.getCenter();

            // Calculate current fire mode based on score
            int score = FXGL.geti("score");

            fireMode = score >= 60 ? 3
                    : score >= 30 ? 2
                    : 1;

            switch (fireMode) {

                case 1: // SINGLE
                    FXGL.spawn("bullet", center);
                    break;

                case 2: // DOUBLE
                    FXGL.spawn("bullet", center.add(-10, 0));
                    FXGL.spawn("bullet", center.add(10, 0));
                    break;

                case 3: // TRIPLE
                    FXGL.spawn("bullet", center.add(-15, 0));
                    FXGL.spawn("bullet", center);
                    FXGL.spawn("bullet", center.add(15, 0));
                    break;
            }

        }, Duration.seconds(0.15));

        FXGL.getGameTimer().runAtInterval(() -> {

            double x = FXGL.random(0, 770);

            FXGL.spawn("obstacle", x, -40);

        }, Duration.seconds(1));

        FXGL.getGameTimer().runAtInterval(() -> {

            int score = FXGL.geti("score");

            // number of enemies in one row (scales with score)
            int enemiesInRow = 3;

            if (score >= 20) enemiesInRow = 4;
            if (score >= 40) enemiesInRow = 5;
            if (score >= 70) enemiesInRow = 6;
            if (score >= 100) enemiesInRow = 7;
            if (score >= 150) enemiesInRow = 8;

            double screenWidth = FXGL.getAppWidth();
            double startY = -40; // near top edge
            double spacing = screenWidth / (enemiesInRow + 1);

            for (int i = 0; i < enemiesInRow; i++) {
                double x = spacing * (i + 1) - 30; // center enemy

                FXGL.spawn("enemy_ship", x, startY);
            }

        }, Duration.seconds(3));



        // ADDED: Extra enemy spawner based on high score

        FXGL.getGameTimer().runAtInterval(() -> {

            int score = FXGL.geti("score");

            if (score >= 40) {
                int extraEnemies = score / 20; // more score = more enemies

                for (int i = 0; i < extraEnemies; i++) {
                    FXGL.spawn("enemy_ship",
                            FXGL.random(0, FXGL.getAppWidth() - 60),
                            -60
                    );
                }
            }

        }, Duration.seconds(5));


        // ADDED: Extra obstacles based on score

        FXGL.getGameTimer().runAtInterval(() -> {

            int score = FXGL.geti("score");

            if (score >= 30) {
                int extraObstacles = score / 15;

                for (int i = 0; i < extraObstacles; i++) {
                    FXGL.spawn("obstacle",
                            FXGL.random(0, FXGL.getAppWidth() - 30),
                            -40
                    );
                }
            }

        }, Duration.seconds(4));


        // Enemy synchronized shooting

        FXGL.getGameTimer().runAtInterval(() -> {

            FXGL.getGameWorld()
                    .getEntitiesByType(EntityType.ENEMY)
                    .forEach(enemy -> {

                        FXGL.spawn("enemy_bullet",
                                enemy.getX() + enemy.getWidth() / 2 - 5,
                                enemy.getY() + enemy.getHeight()
                        );
                    });

        }, Duration.seconds(1.2));
        


        // ADDED: Boss spawn at score 100

        // Boss spawns ONCE at score 100
        FXGL.getGameTimer().runAtInterval(() -> {
            int score = FXGL.geti("score");

            if (!bossSpawned && score >= 50) {
                FXGL.spawn("boss",
                        FXGL.getAppWidth() / 2.0 - 80,
                        -120
                );
                bossSpawned = true;
                FXGL.getip("bossHP").setValue(400);
            }
        }, Duration.seconds(1));

    }

    @Override
    protected void initGameVars(java.util.Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("lives", 3);
        vars.put("enemiesKilled", 0);
        vars.put("bossHP", 0);
    }

    @Override
    protected void initPhysics() {
        // Bullet hits Obstacle
        FXGL.onCollisionBegin(EntityType.BULLET, EntityType.OBSTACLE, (bullet, obstacle) -> {
            bullet.removeFromWorld();
            obstacle.removeFromWorld();
            FXGL.inc("score", 1);
        });

        // Bullet hits Enemy Ship - THE KEY FIX
        FXGL.onCollisionBegin(EntityType.BULLET, EntityType.ENEMY_SHIP, (bullet, enemy) -> {
            bullet.removeFromWorld();
            enemy.removeFromWorld();
            FXGL.inc("score", 1);
            FXGL.inc("enemiesKilled", 1);
        });

        // Enemy Bullet hits Player
        FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.ENEMY_BULLET, (player, enemyBullet) -> {
            enemyBullet.removeFromWorld();
            FXGL.inc("lives", -1);
            flashDamage();
            if (FXGL.geti("lives") <= 0) {
                GameOverHandler.handleGameOver();
            }
        });

        // Player hits Enemy Ship
        FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.ENEMY_SHIP, (player, enemy) -> {
            enemy.removeFromWorld();
            FXGL.inc("lives", -1);
            flashDamage();
            if (FXGL.geti("lives") <= 0) {
                GameOverHandler.handleGameOver();
            }
        });

        // Obstacle hits Player
        FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.OBSTACLE, (player, obstacle) -> {
            obstacle.removeFromWorld();
            FXGL.inc("lives", -1);
            flashDamage();
            if (FXGL.geti("lives") <= 0) {
                GameOverHandler.handleGameOver();
            }
        });


        //  Bullet hits Boss

        FXGL.onCollisionBegin(EntityType.BULLET, EntityType.BOSS, (bullet, boss) -> {

            bullet.removeFromWorld();

            boss.getComponent(BossComponent.class).damage(1);
        });

        FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.ENEMY_BULLET, (player, enemyBullet) -> {
            enemyBullet.removeFromWorld();
            FXGL.inc("lives", -1);

            flashDamage(); // ⚡ call flash here

            if (FXGL.geti("lives") <= 0) {
                GameOverHandler.handleGameOver();
            }
        });

        FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.ENEMY_SHIP, (player, enemy) -> {
            enemy.removeFromWorld();
            FXGL.inc("lives", -1);

            flashDamage();

            if (FXGL.geti("lives") <= 0) {
                GameOverHandler.handleGameOver();
            }
        });

        FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.OBSTACLE, (player, obstacle) -> {
            obstacle.removeFromWorld();
            FXGL.inc("lives", -1);

            flashDamage();

            if (FXGL.geti("lives") <= 0) {
                GameOverHandler.handleGameOver();
            }
        });
    }

    @Override
    protected void initUI() {
        var scoreText = FXGL.getUIFactoryService()
                .newText("", Color.WHITE, 24);

        scoreText.textProperty().bind(
                FXGL.getip("score").asString("SCORE: %d")
        );

        scoreText.setTranslateX(20);
        scoreText.setTranslateY(30);

        var livesText = FXGL.getUIFactoryService()
                .newText("", Color.WHITE, 24);

        livesText.textProperty().bind(
                FXGL.getip("lives").asString("LIVES: %d")
        );

        livesText.setTranslateX(20);
        livesText.setTranslateY(60);

        // Dynamic fire mode indicator that shows current level
        var fireModeText = FXGL.getUIFactoryService()
                .newText("", Color.YELLOW, 18);

        fireModeText.textProperty().bind(
                FXGL.getip("score").asString().map(scoreStr -> {
                    int score = Integer.parseInt(scoreStr);
                    if (score >= 60) {
                        return "FIRE MODE: TRIPLE ⚡⚡⚡";
                    } else if (score >= 30) {
                        return "FIRE MODE: DOUBLE ⚡⚡";
                    } else {
                        return "FIRE MODE: SINGLE ⚡";
                    }
                })
        );

        fireModeText.setTranslateX(550);
        fireModeText.setTranslateY(30);

        var enemiesText = FXGL.getUIFactoryService()
                .newText("", Color.LIGHTGREEN, 18);

        enemiesText.textProperty().bind(
                FXGL.getip("enemiesKilled").asString("ENEMIES: %d")
        );

        enemiesText.setTranslateX(20);
        enemiesText.setTranslateY(90);

        var controlsText = FXGL.getUIFactoryService()
                .newText("L: Leaderboard | ESC: Pause", Color.LIGHTGRAY, 14);

        controlsText.setTranslateX(20);
        controlsText.setTranslateY(780);

        FXGL.addUINode(scoreText);
        FXGL.addUINode(livesText);
        FXGL.addUINode(fireModeText);
        FXGL.addUINode(enemiesText);
        FXGL.addUINode(controlsText);
        // Boss HP Text
        var bossHPText = FXGL.getUIFactoryService()
                .newText("", Color.RED, 20);

        bossHPText.textProperty().bind(
                FXGL.getip("bossHP").asString("BOSS HP: %d")
        );

        bossHPText.setTranslateX(550);
        bossHPText.setTranslateY(60);

        FXGL.addUINode(bossHPText);

        // Damage overlay (screen flash when player gets hit)
        damageOverlay = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.RED);
        damageOverlay.setOpacity(0); // invisible initially
        FXGL.addUINode(damageOverlay);

    }

    public static void main(String[] args){
        launch(args);
    }
}