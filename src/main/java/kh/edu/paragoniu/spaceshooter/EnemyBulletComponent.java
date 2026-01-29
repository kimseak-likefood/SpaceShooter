package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class EnemyBulletComponent extends Component {


    // ADDED: Faster enemy shooting at high score

    private double minCooldown = 0.5; // ADDED


    private double fireCooldown = 1.5;

    @Override
    public void onUpdate(double tpf) {
        fireCooldown -= tpf;

        if (fireCooldown <= 0) {
            shoot();
            fireCooldown = FXGL.random(1.0, 2.5);
        }

        // ADDED: Reduce cooldown as score increases
        int score = FXGL.geti("score");
        minCooldown = Math.max(0.3, 1.5 - score * 0.01);

    }

    private void shoot() {
        FXGL.spawn("enemy_bullet",
                entity.getCenter().getX(),
                entity.getBottomY()
        );
        // ADDED: Faster bullets with score
        fireCooldown = FXGL.random(minCooldown, minCooldown + 1.0);

    }
}
