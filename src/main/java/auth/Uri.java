package auth;

public class Uri implements Uriparts {

    private String ouath = "https://oauth.vk.com/authorize?";
    private String clientID = "&client_id=" + APP_ID;
    private String display = "&display=" + DISPLAY;
    private String redirect = "&redirect_uri=" + REDIRECT_URI;
    private String scope = "&scope=" + SCOPE;
    private String response = "&response_type" + RESPONSE;
    private String version = "&v=" + VERSION;

    public String getUri(){
        StringBuilder builder = new StringBuilder();
        builder.append(ouath)
                .append(clientID)
                .append(display)
                .append(redirect)
                .append(scope)
                .append(response)
                .append(version);
        return builder.toString();
    }
}
