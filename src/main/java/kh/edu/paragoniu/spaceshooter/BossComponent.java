package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class BossComponent extends Component {

    // Boss stats
    private int hp = 400;

    // Movement
    private double verticalSpeed = 40;    // moves down initially
    private double horizontalSpeed = 100; // horizontal speed
    private boolean movingRight = true;   // direction

    private double minX = 50;   // left boundary
    private double maxX;        // right boundary
    private boolean initialized = false;

    private boolean shootingStarted = false;

    // =======================
    // Initialization (first update)
    // =======================
    public void init() {
        maxX = FXGL.getAppWidth() - entity.getWidth() - 50;
        movingRight = entity.getX() < maxX;
    }

    // =======================
    // Called every frame
    // =======================
    @Override
    public void onUpdate(double tpf) {
        // Initialize on first update
        if (!initialized) {
            init();
            initialized = true;
        }

        // 1️⃣ Vertical movement: move down until y = 60
        if (entity.getY() < 60) {
            entity.translateY(verticalSpeed * tpf);
        } else {

            // 2️⃣ Horizontal left-right oscillation
            if (movingRight) {
                entity.translateX(horizontalSpeed * tpf);
                if (entity.getX() >= maxX) movingRight = false;
            } else {
                entity.translateX(-horizontalSpeed * tpf);
                if (entity.getX() <= minX) movingRight = true;
            }

            // 3️⃣ Start shooting bullets only once
            if (!shootingStarted) {
                shootingStarted = true;
                FXGL.getGameTimer().runAtInterval(this::shoot, Duration.seconds(0.3));
            }
        }
        if (!shootingStarted) {
            shootingStarted = true;

            // shoot every 0.5 seconds instead of 0.3
            FXGL.getGameTimer().runAtInterval(this::shoot, javafx.util.Duration.seconds(1));
        }

    }


    // Boss takes damage

    public void damage(int amount) {
        hp -= amount;

        if (hp <= 0) {
            entity.removeFromWorld();
            FXGL.inc("score", 50); // reward
            FXGL.getip("bossHP").setValue(0);
        } else {
            FXGL.getip("bossHP").setValue(hp);
        }
    }

    public int getHp() {
        return hp;
    }


    // Boss shooting bullets

    private void shoot() {
        Point2D pos = entity.getPosition();
        double centerX = pos.getX() + entity.getWidth() / 2;
        double bottomY = pos.getY() + entity.getHeight();

        // Spawn 3 bullets with wider spacing (-40, 0, +40)
        for (int i = -1; i <= 1; i++) {
            FXGL.entityBuilder()
                    .type(SpaceShooter.EntityType.ENEMY_BULLET)
                    .at(centerX + i * 40, bottomY)
                    .viewWithBBox(new javafx.scene.shape.Circle(10, javafx.scene.paint.Color.RED))
                    .with(new EnemyBulletMoveComponent())
                    .collidable()
                    .buildAndAttach();
        }
    }



}
