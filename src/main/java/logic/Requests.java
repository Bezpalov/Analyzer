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

import java.util.ArrayList;

public class Requests {
 ArrayList<Integer> membersOfGroup;
 ArrayList<Integer> groupsOfTheMember;

 public Requests(){
     membersOfGroup = new ArrayList<>();
     groupsOfTheMember = new ArrayList<>();
 }
    public static void main(String[] args) {

        User user = User.getInstance();
        VkApiClient client = user.getApiClient();
        UserActor actor = user.getUserActor();
        Groups groups = new Groups(client);
        Requests request = new Requests();

        String name = "jamescarrey";
        //получаем членов указанной группы
        request.membersOfGroup = request.getGroupMembers(name, client, actor, groups);

        SetOfGroups setOfGroups = new SetOfGroups();
        //бежим по массиву с отобранными юзерами и берем их группы
        for (int i = 0; i < request.membersOfGroup.size(); i++) {

            int id = request.membersOfGroup.get(i);
            MemberData mData = request.getMemberData(id, groups, actor);
            System.out.println(mData.getCount());
            request.groupsOfTheMember = (ArrayList) mData.getItems();

            for (int j = 0; j < request.groupsOfTheMember.size(); j++) {
                int groupId = request.groupsOfTheMember.get(i);
                setOfGroups.add(groupId);
            }
        }
        System.out.println(setOfGroups.toString());



    }

    private String get25KRequests(int id, int count, int offset){
        return "var members = API.groups.getMembers({\"group_id\": " + id +
                ", \"v\": " + "\"" + Uriparts.VERSION + "\", \"count\":1000, \"offset\": " + offset + "}).items;"
                + "var offset = 1000;"
                + "while (offset < 25000 && (offset +" + offset +") < " + count + ")"
                +"{"
                + "members = members + \",\" + API.groups.getMembers({\"group_id\": " + id +
                ", \"v\": " + "\"" + Uriparts.VERSION + "\", \"count\":1000, \"offset\": " + offset + "+ offset}).items;"
                + "offset = offset + 1000;};" +
                "return members;";
    }

    private void parseTo(ArrayList<Integer> array, JsonElement elem){
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
    private ArrayList<Integer> getGroupMembers(String id, VkApiClient client, UserActor actor, Groups groups){
        GroupData data = getGroupInfo(id, groups, actor);
        int count = data.getMembersCount(0);
        int groupID = data.getID(0);
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < count; i = i + 25000) {
            try {
                String req = get25KRequests(groupID, count, i);
                Thread.sleep(333);
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
    private GroupData getGroupInfo(String groupID, Groups groups, UserActor actor){

        GroupData group = null;
        try {
            GroupsGetByIdQuery query =  groups.getById(actor).groupId(groupID).fields(GroupField.MEMBERS_COUNT);
            String str = query.executeAsString();
            Gson gson = new Gson();
            group = gson.fromJson(str, GroupData.class);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return group;
    }

    private MemberData getMemberData(int userID, Groups groups, UserActor actor){

        MemberData mData = null;
        try {
            GroupsGetQuery groupQuery = groups.get(actor).userId(367336).count(1000);
            Gson gson = new Gson();
            String resutlQuery = groupQuery.executeAsString();
            mData = gson.fromJson(resutlQuery, MemberData.class);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return mData;
    }
}
