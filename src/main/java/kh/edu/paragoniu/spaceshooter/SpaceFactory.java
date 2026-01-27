package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import com.almasb.fxgl.entity.component.Component;

public class SpaceFactory implements EntityFactory {

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.BULLET)
                .view(new Circle(5, Color.YELLOW))
                .with(new BulletComponent())
                .collidable()
                .build();
    }

    @Spawns("obstacle")
    public Entity newObstacle(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.OBSTACLE)
                .view(new Rectangle(30, 30, Color.LIMEGREEN))
                .with(new ObstacleComponent())
                .collidable()
                .build();
    }

    @Spawns("enemy_ship")
    public Entity newEnemyShip(SpawnData data)
    {
        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.ENEMY_SHIP)
                .view(new Rectangle(30, 30, Color.RED ))
                .with(new EnemyShipComponent()
                ,new EnemyBulletComponent())
                .build();
    }

    @Spawns("enemy_bullet")
    public Entity newEnemyBullet(SpawnData data){
        return FXGL.entityBuilder(data)
                .type(SpaceShooter.EntityType.ENEMY_BULLET)
                .view(new Circle(5, Color.RED))
                .with(new EnemyBulletMoveComponent())
                .collidable()
                .build();
    }
}