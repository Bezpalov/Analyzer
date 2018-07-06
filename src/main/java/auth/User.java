package auth;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import javafx.application.Application;


public class User implements Uriparts{
    private static User user;
    private static String code;

    private final TransportClient transportClient;
    private final VkApiClient apiClient;
    private final UserAuthResponse authResponse;
    private final UserActor actor;


    private User() throws Exception {
        transportClient = HttpTransportClient.getInstance();
        apiClient = new VkApiClient(transportClient);
        Application.launch(UserAuth.class);
        authResponse = apiClient.oauth()
                .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code).execute();
        actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());

    }

    public static User getInstance() {
          if(user == null) {
              try {
                  return new User();
              } catch (Exception e) {
                  System.out.println("Error #1: User constructor");
              }
          }
          return user;
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
