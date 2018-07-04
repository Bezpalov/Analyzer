package logic;

import auth.Uriparts;
import auth.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Requests {
 ArrayList<Integer> membersOfGroup;
 ArrayList<Integer> groupsOfTheMember;

 HashMap<Integer, MembersResponse> finalMemberGroupCollection;
 HashMap<Integer, MembersResponse> intermediateMemberGroupCollection;

 Gson gson;
 int countOfFalse;

 public Requests(){
     membersOfGroup = new ArrayList<>();
     groupsOfTheMember = new ArrayList<>();
     gson = new Gson();
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
//        for (int i = 0; i < request.membersOfGroup.size(); i++) {
//
//            int id = request.membersOfGroup.get(i);
//            MemberData mData = request.getMemberData(id, groups, actor);
//            if(mData.getResponse() == null)
//                continue;
//            System.out.println("count is: " + mData.getCount());
//
//            request.groupsOfTheMember = (ArrayList) mData.getItems();
//
//            for (int j = 0; j < request.groupsOfTheMember.size(); j++) {
//                int groupId = request.groupsOfTheMember.get(j);
//                setOfGroups.add(groupId);
//            }
//        }
//        System.out.println(setOfGroups.toString());
        request.getMembersGroup(client, actor);
        System.out.println("finalMemberGroupCollection");
        for (Map.Entry<Integer, MembersResponse> entry: request.finalMemberGroupCollection.entrySet())  {

            System.out.println(entry.getKey() + " : " + entry.getValue().getItems());
        }
        System.out.println();
        System.out.println("intermediateMemberGroupCollection");
        for(Map.Entry<Integer, MembersResponse> entry : request.intermediateMemberGroupCollection.entrySet()){

            System.out.println(entry.getKey() + " : " + entry.getValue().getItems());
        }

        System.out.println("size of a group: " + request.membersOfGroup.size());
        System.out.println("counted size: " + (request.intermediateMemberGroupCollection.size() + request.finalMemberGroupCollection.size() + request.countOfFalse));





    }

    private void getMembersGroup(VkApiClient client, UserActor actor)  {
        finalMemberGroupCollection = new HashMap<>();
        intermediateMemberGroupCollection = new HashMap<>();
        int size = membersOfGroup.size();
        int offset = 0;
        int quantity = 25;
        int id;
        int number = 0;
        countOfFalse = 0;
        String code = null;
        JsonElement elem = null;
        JsonArray array = null;
        MembersResponse response = null;

        try {
            while(number < membersOfGroup.size()){
                if(membersOfGroup.size()-1 - number < 25)
                    quantity = membersOfGroup.size() - number;

                    code = getGroupRequest(membersOfGroup, number, quantity);
                    elem = client.execute().code(actor, code).execute();
                    Thread.sleep(200);
                    System.out.println(number);
                    array = elem.getAsJsonArray();
                    for (int i = 0; i <array.size(); i++){

                        if(array.get(i).toString().equals("false")){
                            number++;
                            countOfFalse++;
                            continue;
                        }
                        response = gson.fromJson(array.get(i), MembersResponse.class);
                        if(response.getCount() <= 1000)
                            finalMemberGroupCollection.put(number, response);
                        else
                            intermediateMemberGroupCollection.put(number, response);
                        number++;
                    }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }



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

    private String getGroupRequest(ArrayList list, int offset, int quantity) {
        int temp = offset;
        StringBuilder builder = new StringBuilder("var groups = API.groups.get({user_id:" + list.get(offset) + ", v :"
                    + Uriparts.VERSION + ", count: 1000,offset: 0 });"
                    + "var arr = [];" + "arr.push(groups);");

        for (int i = offset + 1; i < (offset + quantity); i++) {
            builder.append("groups = API.groups.get({user_id: " + list.get(temp)
                    + ", v : " + Uriparts.VERSION + ", count: 1000,offset: 0 });"
                    + "arr.push(groups);");
            temp++;
        }

        builder.append("return arr;");
        return builder.toString();
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
                Thread.sleep(300);
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
            GroupsGetQuery groupQuery = groups.get(actor).userId(userID).count(1000);
            Gson gson = new Gson();
            String resutlQuery = groupQuery.executeAsString();
            mData = gson.fromJson(resutlQuery, MemberData.class);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return mData;
    }
}
