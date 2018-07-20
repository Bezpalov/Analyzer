package logic;

import auth.UserAuth;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

public class TokenThreads extends Thread {
    Requests request;
    UserActor actor;
    int begin;
    int end;
    int threadNumber;
    VkApiClient client;
    int step;

    public TokenThreads(Requests request, UserActor actor, int begin, int end, int threadNumber, VkApiClient client){
        this.request = request;
        this.actor = actor;
        this.begin = begin;
        this.end = end;
        this.threadNumber = threadNumber;
        this.client = client;


    }
    @Override
    public void run() {
        super.run();
        request.getMembersGroup(client, actor, begin, end, Integer.toString(threadNumber));
    }
}
