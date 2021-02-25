package eu.timerertim.downlomatic.graphics;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class GUI extends Application {
    public static void start(String... args) throws RuntimeException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.close();
        Platform.exit();
    }
}
