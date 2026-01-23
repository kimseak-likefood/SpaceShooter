module kh.edu.paragoniu.spaceshooter {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens kh.edu.paragoniu.spaceshooter to javafx.fxml;
    exports kh.edu.paragoniu.spaceshooter;
}