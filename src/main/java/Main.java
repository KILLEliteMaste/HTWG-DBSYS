import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    public static Parent parentRoot;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/dbsys.fxml"));
        parentRoot = root;
        primaryStage.setTitle("Reservierung");
        primaryStage.setScene(new Scene(root, 800, 500));
        //primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
            Platform.exit();
        });
    }
}
