package logic;

import auth.Uriparts;
import auth.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.vk.api.sdk.actions.Groups;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.queries.groups.*;
import logic.jsonContainers.GroupData;
import logic.jsonContainers.MemberData;
import logic.jsonContainers.MembersResponse;

public class Requests {
    static void getGroupMembers(int id, int count, VkApiClient client, UserActor actor){
        int temp = 0;
        String req = "var members = API.groups.getMembers({\"group_id\": " + id +
                ", \"v\": " + "\"" + Uriparts.VERSION + "\", \"count\":1000, \"offset\": " + temp + "}).items;"
                            + "var offset = 1000;"
                            + "while (offset < 25000)"
                            +"{"
                                + "members = members + \",\" + API.groups.getMembers({\"group_id\": " + id +
                ", \"v\": " + "\"" + Uriparts.VERSION + "\", \"count\":1000, \"offset\": " + temp + "+ offset}).items;"
                        + "offset = offset + 1000;};" +
                        "return members;";

        try {
            JsonElement element = client.execute().code(actor, req).execute();
            Gson gson = new Gson();

            MemberData members = gson.fromJson(element.getAsJsonObject(), MemberData.class);
            System.out.println(members.getMembersResponse().size());

        } catch (ApiException e) {
            e.printStackTrace();
            System.out.println("api");
        } catch (ClientException e) {
            System.out.println("client");
        }
    }
    public static void main(String[] args) {

        try {
            User user = User.getInstance();
            VkApiClient client = user.getApiClient();
            UserActor actor = user.getUserActor();
            Groups groups = new Groups(client);


            //получение информации по группе
            GroupsGetByIdQuery query =  groups.getById(actor).groupId("jamescarrey").fields(GroupField.MEMBERS_COUNT);
            String str = query.executeAsString();
            System.out.println(str);
            Gson gson = new Gson();
            GroupData group = gson.fromJson(str, GroupData.class);
            int groupId = group.getID(0);
            int countGroup = group.getMembersCount(0);
            getGroupMembers(groupId, countGroup,client,actor);
//            GroupsGetMembersQuery jamescarrey = groups.getMembers(actor).groupId("jamescarrey").count(1000).offset(0);
//            System.out.println(jamescarrey.executeAsString());
//            client.groups().getMembers(actor).groupId(Integer.toString(groupId)).offset(0).count(1000);
//            String code = "";
//            JsonElement element = client.execute().code(actor, code).execute();
//            System.out.println(element.toString());








        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
