package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class EnemyBulletMoveComponent extends Component {

    private static final double SPEED = 250;

    @Override
    public void onUpdate(double tpf) {
        // Move bullet downward
        entity.translateY(SPEED * tpf);

        // Remove bullet when it leaves the screen
        if (entity.getY() > FXGL.getAppHeight()) {
            entity.removeFromWorld();
        }
    }
}