package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;


public class SpaceShooterGame extends GameApplication {

    public enum EntityType {
        PLAYER, BULLET
    }

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
                player.getComponent(PhysicsComponent.class).setVelocityX(-200);
            }
            @Override
            protected void onActionEnd(){
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.LEFT);


        FXGL.getInput().addAction(new UserAction("RIGHT") {
            @Override
            protected void onAction() {
                player.getComponent(PhysicsComponent.class).setVelocityX(200);
            }
            @Override
            protected void onActionEnd(){
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.RIGHT);

        FXGL.getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                spawn("bullet", player.getCenter());
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initGame(){

        getGameWorld().addEntityFactory(new SpaceFactory());

        FXGL.entityBuilder()
                .view(new Rectangle(800, 800, Color.DARKBLUE))
                .zIndex(-999)
                .buildAndAttach();

        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        Rectangle spaceshipShape = new Rectangle(42, 42, Color.LIGHTGRAY);
        spaceshipShape.setRotate(45);

        player = FXGL.entityBuilder()
                .type(EntityType.PLAYER)
                .view(spaceshipShape)
                .at(400, 600)
                .with(physics)
                .buildAndAttach();

        FXGL.run(() -> {
            Point2D gunPos = player.getPosition().add(20, -10);
            FXGL.spawn("bullet", gunPos);
        }, Duration.seconds(0.15));
    }
    @Override
    protected void initGameVars(java.util.Map<String, Object> vars) {}

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}



    public static void main(String[] args){
        launch(args);
    }
}
