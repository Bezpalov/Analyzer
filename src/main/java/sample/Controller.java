package sample;

import com.google.gson.Gson;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Controller {
    private final long APP_ID = 6612887;
    private final String CLIENT_SECRET = "NZIxzorHZIotep0tRL5r";
    private String REDIRECT_URI = "https://oauth.vk.com/blank.html";
    private long code;


    public static void main(String[] args) {

        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vkClient = new VkApiClient(transportClient);


        try {
            URL Vkurl = new URL("https://oauth.vk.com/authorize?client_id=6612887&display=page&redirect_uri=https://oauth.vk.com/blank.html" +
                    "&scope=offline&response_type=code&v=5.80" +
                    "");
            BufferedReader reader = new BufferedReader(new InputStreamReader(Vkurl.openStream()));

            String input;
            while((input = reader.readLine())!= null){
                System.out.println(input);
            }
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
