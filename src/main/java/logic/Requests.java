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

import java.util.ArrayList;

public class Requests {
    public static String get25KRequests(int id, int count, int offset){
        return "var members = API.groups.getMembers({\"group_id\": " + id +
                ", \"v\": " + "\"" + Uriparts.VERSION + "\", \"count\":1000, \"offset\": " + offset + "}).items;"
                + "var offset = 1000;"
                + "while (offset < 25000)"
                +"{"
                + "members = members + \",\" + API.groups.getMembers({\"group_id\": " + id +
                ", \"v\": " + "\"" + Uriparts.VERSION + "\", \"count\":1000, \"offset\": " + offset + "+ offset}).items;"
                + "offset = offset + 1000;};" +
                "return members;";
    }

    public static void parseTo(ArrayList<Integer> array, JsonElement elem){
        int beginIndex = 0;
        int endIndex = 0;

        String result = elem.toString();
        result = result.substring(1);
        String temp;
        while(beginIndex < result.length()){
            endIndex = result.indexOf(",", beginIndex);
            if(endIndex == -1)
                endIndex = result.length() - 1;
            temp = result.substring(beginIndex, endIndex);
            if(!temp.equals(""))
                array.add(new Integer(temp));
            beginIndex = endIndex + 1;
        }

    }
    static ArrayList<Integer> getGroupMembers(int id, int count, VkApiClient client, UserActor actor){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < count; i = i + 25000) {
            try {
                String req = get25KRequests(id, count, i);
                Thread.sleep(333);
                System.out.println(i);
                JsonElement element = client.execute().code(actor, req).execute();
                parseTo(list, element);
            } catch (ApiException e) {
                e.printStackTrace();
                System.out.println("api");
            } catch (ClientException e) {
                e.printStackTrace();
                System.out.println("client");
            }catch (InterruptedException e){
                e.printStackTrace();

            }
        }
        return list;
    }
    public static void main(String[] args) {

        try {
            User user = User.getInstance();
            VkApiClient client = user.getApiClient();
            UserActor actor = user.getUserActor();
            Groups groups = new Groups(client);


            //получение информации по группе
            GroupsGetByIdQuery query =  groups.getById(actor).groupId("mudakoff").fields(GroupField.MEMBERS_COUNT);
            String str = query.executeAsString();
            System.out.println(str);
            Gson gson = new Gson();
            GroupData group = gson.fromJson(str, GroupData.class);
            int groupId = group.getID(0);
            int countGroup = group.getMembersCount(0);
            ArrayList<Integer> memberList = getGroupMembers(groupId, countGroup,client,actor);
            System.out.println(countGroup);
            System.out.println(memberList.size());
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
