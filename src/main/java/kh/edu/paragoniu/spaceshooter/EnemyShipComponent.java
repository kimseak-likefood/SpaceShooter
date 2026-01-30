package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import java.util.Random;

public class EnemyShipComponent extends Component {


    // ADDED: Dynamic speed scaling with score

    private double speedMultiplier = 0.6; // ADDED


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

        //Move vertically
        entity.translateY(SPEED * tpf);

        if (entity.getY() > FXGL.getAppHeight()) {
            entity.removeFromWorld();
        }

        // Move horizontally
        entity.translateX(directionX * SPEED * tpf);

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

        // ADDED: Increase enemy speed based on score

        int score = FXGL.geti("score");

        // ADDED: Scaled movement

        entity.translateY(SPEED * speedMultiplier * tpf);
        entity.translateX(directionX * SPEED * speedMultiplier * tpf);


    }

    private void randomizeDirection() {
        // -1 = left, 1 = right
        directionX = random.nextBoolean() ? 1 : -1;
    }
}

