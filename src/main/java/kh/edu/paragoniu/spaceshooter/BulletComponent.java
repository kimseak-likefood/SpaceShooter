package kh.edu.paragoniu.spaceshooter;

import com.almasb.fxgl.entity.component.Component;

public class BulletComponent extends Component {
    private final double speed = 150;
    private static final double BULLET_RADIUS = 5;

    @Override
    public void onUpdate(double tpf){
        entity.translateY(-speed * tpf);

        if(entity.getY()< -BULLET_RADIUS * 2){
            entity.removeFromWorld();
        }
    }
}
