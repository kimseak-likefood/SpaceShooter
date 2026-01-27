package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.entity.component.Component;

public class ObstacleComponent extends Component {

    private static final double SPEED = 120;

    @Override
    public void onUpdate(double tpf) {
        entity.translateY(SPEED * tpf);

        if (entity.getY() > 820) {
            entity.removeFromWorld();
        }
    }
}