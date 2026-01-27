package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class EnemyBulletComponent extends Component {

    private double fireCooldown = 1.5;

    @Override
    public void onUpdate(double tpf) {
        fireCooldown -= tpf;

        if (fireCooldown <= 0) {
            shoot();
            fireCooldown = FXGL.random(1.0, 2.5);
        }
    }

    private void shoot() {
        FXGL.spawn("enemy_bullet",
                entity.getCenter().getX(),
                entity.getBottomY()
        );
    }
}
