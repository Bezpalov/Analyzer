package logic;

import com.vk.api.sdk.client.actors.UserActor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class UserActorContainer {
    UserActor[] actors;
    int myID = 555712;

    public UserActorContainer(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("tokens.txt"));

           String result = reader.readLine();
           actors[0] = new UserActor(myID, result);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
