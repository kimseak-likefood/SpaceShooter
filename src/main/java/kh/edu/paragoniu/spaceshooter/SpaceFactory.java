package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class SpaceFactory implements EntityFactory {

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.BULLET)
                .viewWithBBox(new Circle(5, Color.YELLOW))
                .with(new BulletComponent())
                .collidable()
                .build();
    }

    @Spawns("obstacle")
    public Entity newObstacle(SpawnData data) {

        //Obstacle as cucumber
        Image enemyImg = new Image(
                ClassLoader.getSystemResource("cucumber_pixel-removebg-preview.png").toString()
        );
        ImageView enemyView = new ImageView(enemyImg);
        enemyView.setFitWidth(75);
        enemyView.setFitHeight(75);
        enemyView.setPreserveRatio(true);
        enemyView.setRotate(0.0);

        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.OBSTACLE)
                .viewWithBBox(enemyView)
                .with(new ObstacleComponent())
                .collidable()
                .build();
    }

    @Spawns("enemy_ship")
    public Entity newEnemyShip(SpawnData data)
    {
        //Enemy ship as doge
        Image enemyImg = new Image(
                ClassLoader.getSystemResource("nyan_dog_no_bg.png").toString()
        );
        ImageView enemyView = new ImageView(enemyImg);
        enemyView.setFitWidth(75);
        enemyView.setFitHeight(75);
        enemyView.setPreserveRatio(true);
        enemyView.setRotate(90.0);

        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.ENEMY_SHIP)
                .viewWithBBox(enemyView)
                .with(new EnemyShipComponent(), new EnemyBulletComponent())
                .collidable()
                .build();
    }

    @Spawns("enemy_bullet")
    public Entity newEnemyBullet(SpawnData data){
        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.ENEMY_BULLET)
                .viewWithBBox(new Circle(5, Color.RED))
                .with(new EnemyBulletMoveComponent())
                .collidable()
                .build();
    }


    // Boss entity

    @Spawns("boss")
    public Entity newBoss(SpawnData data) {

        ImageView bossView = new ImageView(
                new Image(ClassLoader.getSystemResource("dog.png").toString())
        );
        bossView.setFitWidth(160);
        bossView.setFitHeight(160);
        bossView.setPreserveRatio(true);

        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.BOSS)
                .viewWithBBox(bossView)
                .with(new BossComponent())
                .collidable()
                .build();
    }
    @Spawns("boss_bullet")
    public Entity newBossBullet(SpawnData data) {
        // Bigger red circle for boss bullets
        Circle bulletCircle = new Circle(20, Color.RED);  // Size 10 (regular is 5)

        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.ENEMY_BULLET)
                .viewWithBBox(bulletCircle)
                .with(new EnemyBulletMoveComponent())
                .collidable()
                .build();
    }

}