package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import java.util.Random;

public class EnemyShipComponent extends Component {

    private static final double SPEED = 100;
    private static final double CHANGE_DIR_TIME = 1.5; // seconds

    private double directionX;
    private double timeSinceLastChange = 0;

    private final Random random = new Random();

    @Override
    public void onAdded() {
        randomizeDirection();
    }

    @Override
    public void onUpdate(double tpf) {
        // Move horizontally
        entity.translateX(directionX * SPEED * tpf);

        // Keep enemy near the top
        if (entity.getY() > 200) {
            entity.setY(200);
        }

        // Change direction randomly over time
        timeSinceLastChange += tpf;
        if (timeSinceLastChange >= CHANGE_DIR_TIME) {
            randomizeDirection();
            timeSinceLastChange = 0;
        }

        // Screen bounds check
        double screenWidth = FXGL.getAppWidth();

        if (entity.getX() <= 0 || entity.getRightX() >= screenWidth) {
            directionX *= -1;
        }
    }

    private void randomizeDirection() {
        // -1 = left, 1 = right
        directionX = random.nextBoolean() ? 1 : -1;
    }
}

