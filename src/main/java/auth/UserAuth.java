package auth;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class UserAuth extends Application {
    private String authURI;
    private String authCode;
    private String code;

    public void init() throws Exception {
        super.init();
        authURI = new Uri().getUri();
    }
    public void start(final Stage primaryStage) throws Exception {
        final WebView wView = new WebView();
        final WebEngine engine = wView.getEngine();

        engine.load(authURI);
        engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
            //think about better way to resolve problem with the action on getting the code
            public void handle(WebEvent<String> event) {
                authCode = engine.getLocation();
                if(authCode.contains("code=")) {
                    primaryStage.close();
                    cutTheCode(authCode);
                    User.setCode(code);
                }

            }
        });
        VBox root = new VBox();
        root.getChildren().addAll(wView);
        root.setPrefSize(680, 350);
        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void cutTheCode(String url){
        code = url.substring(authCode.lastIndexOf("=") + 1);
    }
}
