package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
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

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;


public class SpaceShooter extends GameApplication {

    public enum EntityType {
        PLAYER, BULLET, OBSTACLE, ENEMY_SHIP, ENEMY_BULLET
    }
    private int fireMode = 1;
    private Entity player;
    private static final Point2D GUN_OFFSET = new Point2D(20, -10);

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setHeight(800);
        gameSettings.setWidth(800);
        gameSettings.setTitle("SpaceShooterGame");
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
        }, KeyCode.LEFT);


        FXGL.getInput().addAction(new UserAction("RIGHT") {
            @Override
            protected void onAction() {
                player.getComponent(PhysicsComponent.class).setVelocityX(300);
            }
            @Override
            protected void onActionEnd(){
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.RIGHT);


//        FXGL.getInput().addAction(new UserAction("Shoot") {
//            @Override
//            protected void onActionBegin() {
//                spawn("bullet", player.getCenter());
//            }
//        }, KeyCode.SPACE);


        FXGL.getInput().addAction(new UserAction("Single Shot") {
            @Override
            protected void onActionBegin() {
                fireMode = 1;
            }
        }, KeyCode.DIGIT1);

        FXGL.getInput().addAction(new UserAction("Double Shot") {
            @Override
            protected void onActionBegin() {
                fireMode = 2;
            }
        }, KeyCode.DIGIT2);

        FXGL.getInput().addAction(new UserAction("Triple Shot") {
            @Override
            protected void onActionBegin() {
                fireMode = 3;
            }
        }, KeyCode.DIGIT3);
    }

    @Override
    protected void initGame(){

        getGameWorld().addEntityFactory(new SpaceFactory());

        // Background
        FXGL.entityBuilder()
                .view(new Rectangle(800, 800, Color.DARKBLUE))
                .zIndex(-999)
                .buildAndAttach();

        // Physics component for player
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        // Load image and create ImageView
        Image playerImg = new Image(
                ClassLoader.getSystemResource("nyan_cat.png").toString()
        );
        ImageView playerView = new ImageView(playerImg);
        playerView.setFitWidth(50);
        playerView.setFitHeight(50);
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

        FXGL.run(() -> {

            Point2D center = player.getCenter();

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
        FXGL.run(() -> {

            double x = FXGL.random(0, 770);

            FXGL.spawn("obstacle", x, -40);

        }, Duration.seconds(1));

        for (int i = 0; i < 5; i++) {
            FXGL.spawn("enemy_ship",
                    FXGL.random(0, FXGL.getAppWidth() - 60),
                    FXGL.random(30, 120)
            );
        }


    }
    @Override
    protected void initGameVars(java.util.Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("lives", 3);
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(
                new com.almasb.fxgl.physics.CollisionHandler(
                        EntityType.BULLET, EntityType.OBSTACLE) {

                    @Override
                    protected void onCollisionBegin(Entity bullet, Entity obstacle) {
                        bullet.removeFromWorld();
                        obstacle.removeFromWorld();
                        FXGL.inc("score", 10);
                    }
                }
        );
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

        var autoFireText = FXGL.getUIFactoryService()
                .newText("AUTO FIRE", Color.RED, 18);

        autoFireText.setTranslateX(650);
        autoFireText.setTranslateY(30);

        FXGL.addUINode(scoreText);
        FXGL.addUINode(livesText);
        FXGL.addUINode(autoFireText);
    }



    public static void main(String[] args){
        launch(args);
    }
}