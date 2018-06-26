package auth;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import javafx.application.Application;


public class User implements Uriparts{
    private static String code;

    private TransportClient transportClient;
    private VkApiClient apiClient;
    private UserAuthResponse authResponse;
    private UserActor actor;


    public User() throws Exception {
        transportClient = HttpTransportClient.getInstance();
        apiClient = new VkApiClient(transportClient);
        Application.launch(UserAuth.class);
        authResponse = apiClient.oauth()
                .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code).execute();
        actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }

    static void setCode(String cd){
        code = cd;
    }

    public VkApiClient getApiClient(){
        return apiClient;
    }

    public UserAuthResponse getUserAuthResponse(){
        return authResponse;
    }

    public UserActor getUserActor(){
        return actor;
    }
}
