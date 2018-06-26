package logic;

import auth.User;
import com.google.gson.Gson;
import com.vk.api.sdk.actions.Groups;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.groups.responses.GetMembersResponse;
import com.vk.api.sdk.queries.groups.GroupsGetByIdQuery;
import com.vk.api.sdk.queries.groups.GroupsGetMembersFilter;
import com.vk.api.sdk.queries.groups.GroupsGetMembersQuery;
import com.vk.api.sdk.queries.groups.GroupsSearchSort;

import java.util.List;

public class Requests {
    public static void main(String[] args) {

        try {
            User user = new User();
            VkApiClient client = user.getApiClient();
            UserActor actor = user.getUserActor();
            Groups groups = new Groups(client);

             GetMembersResponse response = groups.getMembers(actor).groupId("jamescarrey").count(1000).offset(0).execute();
            System.out.println(response.toString());





            Gson gson = new Gson();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
