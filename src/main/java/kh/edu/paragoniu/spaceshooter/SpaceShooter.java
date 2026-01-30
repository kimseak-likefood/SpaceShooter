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

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

//test kimseak

public class SpaceShooter extends GameApplication {

    public enum EntityType {
        PLAYER, BULLET, OBSTACLE, ENEMY_SHIP, ENEMY_BULLET
    }
    private int fireMode;
    private Entity player;
    private static final Point2D GUN_OFFSET = new Point2D(20, -10);

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
//        FXGL.getInput().addAction(new UserAction("Shoot") {
//            @Override
//            protected void onActionBegin() {
//                spawn("bullet", player.getCenter());
//            }
//        }, KeyCode.SPACE);


//        FXGL.getInput().addAction(new UserAction("Single Shot") {
//            @Override
//            protected void onActionBegin() {
//                fireMode = 1;
//            }
//        }, KeyCode.DIGIT1);
//
//        FXGL.getInput().addAction(new UserAction("Double Shot") {
//            @Override
//            protected void onActionBegin() {
//                fireMode = 2;
//            }
//        }, KeyCode.DIGIT2);
//
//        FXGL.getInput().addAction(new UserAction("Triple Shot") {
//            @Override
//            protected void onActionBegin() {
//                fireMode = 3;
//            }
//        }, KeyCode.DIGIT3);

        // Add pause/menu key
        FXGL.getInput().addAction(new UserAction("Pause") {
            @Override
            protected void onActionBegin() {
                FXGL.getGameController().pauseEngine();
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

        getGameWorld().addEntityFactory(new SpaceFactory());

        // Background
        try {
            Image bgImage = new Image(ClassLoader.getSystemResource("background.jpg").toString());
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
                ClassLoader.getSystemResource("nyan_doge.gif").toString()
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
//            int currentFireMode;
//
//            if (score >= 100) {
//                currentFireMode = 3; // Triple shot at 200+ points
//            } else if (score >= 30) {
//                currentFireMode = 2; // Double shot at 100+ points
//            } else {
//                currentFireMode = 1; // Single shot below 100 points
//            }

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

        FXGL.getGameTimer().runAtInterval(() ->{

            int score = FXGL.geti("score");

            // Determine how many enemies to spawn
            int minEnemies = 2;
            int maxEnemies = 4;

            if (score >= 10) {
                minEnemies = 4;
                maxEnemies = 6;
            }
            if (score >= 30) {
                minEnemies = 5;
                maxEnemies = 8;
            }
            if (score >= 50) {
                minEnemies = 7;
                maxEnemies = 10;
            }

            int enemy_ships = FXGL.random(minEnemies, maxEnemies);

            for (int i = 0; i < enemy_ships; i++) {
                FXGL.spawn("enemy_ship",
                        FXGL.random(0, FXGL.getAppWidth() - 60),
                        -40
                );
            }
        }, Duration.seconds(3));

    }

    @Override
    protected void initGameVars(java.util.Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("lives", 3);
        vars.put("enemiesKilled", 0);
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

            if (FXGL.geti("lives") <= 0) {
                GameOverHandler.handleGameOver();
            }
        });

        // Player hits Enemy Ship
        FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.ENEMY_SHIP, (player, enemy) -> {
            enemy.removeFromWorld();
            FXGL.inc("lives", -1);

            if (FXGL.geti("lives") <= 0) {
                GameOverHandler.handleGameOver();
            }
        });

        // Obstacle hits Player
        FXGL.onCollisionBegin(EntityType.PLAYER, EntityType.OBSTACLE, (player, obstacle) -> {
            obstacle.removeFromWorld();
            FXGL.inc("lives", -1);

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
    }



    public static void main(String[] args){
        launch(args);
    }
}