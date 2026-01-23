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
    public Entity newBullet (SpawnData data){
        return FXGL.entityBuilder(data)
                .type(SpaceShooterGame.EntityType.PLAYER)
                .view(new Circle(10.0, 10.0, 5.0, Color.RED))
                .with(new BulletComponent())
                .build();
    }
}
