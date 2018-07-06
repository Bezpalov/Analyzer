package logic;

import auth.Uriparts;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.vk.api.sdk.actions.Groups;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.groups.*;
import logic.jsonContainers.GroupData;
import logic.jsonContainers.MemberData;
import logic.jsonContainers.MembersResponse;


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

 String token1 = "f9dbc0cf3a9d00743b620913e59e6e3933d405969bd062c4908c323915a892718b7e233d6b0f583833351";
 String token2 = "7a550c8009bc0a658115571daf3deed84bcdfca5e48b2e9ed752cbf4bced894e60f4a46f409b2c56a5cc9";
 String token3 = "4bb3133ac53d5c4df0956775bbb1382bb42f7fa258c0fab90915c6c27742426d93029afda38ce9e6ba2c2";
 int myID = 555717;



 public Requests(){
     membersOfGroup = new ArrayList<>();
     groupsOfTheMember = new ArrayList<>();
     gson = new Gson();
     finalMemberGroupCollection = new HashMap<>();
     intermediateMemberGroupCollection = new HashMap<>();
 }
    public static void main(String[] args) {

//        User user = User.getInstance();
//        VkApiClient client = user.getApiClient();
//        UserActor actor = user.getUserActor();

        Requests request = new Requests();
        HttpTransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient client = new VkApiClient(transportClient);
        UserActor actor = new UserActor(request.myID, request.token1);
        UserActor actor2 = new UserActor(request.myID, request.token3);
        UserActor actor3 = new UserActor(request.myID, request.token3);
        UserActor[] actors = {actor, actor2, actor3};
        Groups groups = new Groups(client);

        String name = "jamescarrey";
        //получаем членов указанной группы
        request.membersOfGroup = request.getGroupMembers(name, client, actor, groups);

        SetOfGroups setOfGroups = new SetOfGroups();


        TokenThread[] tokenThreads = new TokenThread[3];
        request.initThreadObjects(tokenThreads, actors, client);

        for (int i = 0; i < tokenThreads.length; i++) {
            tokenThreads[i].start();
        }

        for (int i = 0; i < tokenThreads.length; i++) {
            try {
                tokenThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        new TokenThread(request, actor, 0, request.membersOfGroup.size(), 1, client).start();

        System.out.println("group size is: " + request.membersOfGroup.size());
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

    void initThreadObjects (TokenThread[] tokens, UserActor[] actors, VkApiClient client){
        int begin = 0;
        int end = 0;
        int step = (int)(this.membersOfGroup.size() / tokens.length);

        for (int i = 0; i < tokens.length ; i++) {

            begin = i * step;
            end = end + step - 1;
            if(i == tokens.length -1)
                end = this.membersOfGroup.size()- 1;

            tokens[i] = new TokenThread(this, actors[i], begin, end, i+1, client);

        }
    }
        void getMembersGroup(VkApiClient client, UserActor actor, int arrayNumberStart, int arrayNumberEnd, String threadNumber)  {
        HashMap<Integer, MembersResponse> prefinalMemberGroupCollection = new HashMap<>();
        HashMap<Integer, MembersResponse> preIntermediateMemberGroupCollection = new HashMap<>();
        int size = arrayNumberEnd;
        int offset = 0;
        int quantity = 25;
        int id;
        int number = arrayNumberStart;
        int countOfFalse = 0;
        String code = null;
        JsonElement elem = null;
        JsonArray array = null;
        MembersResponse response = null;

        try {
            while(number < size){
                if(membersOfGroup.size()-1 - number < 25)
                    quantity = membersOfGroup.size() - number;

                    code = getGroupRequest(membersOfGroup, number, quantity);
                    elem = client.execute().code(actor, code).execute();
                    Thread.sleep(333);
                    System.out.println(number + " Thread number " + threadNumber);
                    array = elem.getAsJsonArray();
                    for (int i = 0; i <array.size(); i++){

                        if(array.get(i).toString().equals("false")){
                            number++;
                            countOfFalse++;
                            continue;
                        }
                        response = gson.fromJson(array.get(i), MembersResponse.class);
                        if(response.getCount() <= 1000)
                            prefinalMemberGroupCollection.put(number, response);
                        else
                            preIntermediateMemberGroupCollection.put(number, response);
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
        System.out.println( threadNumber + " size is:" + (prefinalMemberGroupCollection.size() + preIntermediateMemberGroupCollection.size() + countOfFalse) );
        mergeArrays(prefinalMemberGroupCollection, finalMemberGroupCollection);
        mergeArrays(preIntermediateMemberGroupCollection, intermediateMemberGroupCollection);


    }
    private synchronized HashMap mergeArrays(HashMap<Integer, MembersResponse> from, HashMap<Integer, MembersResponse> to){
        to.putAll(from);
        return to;
    }
    private String get25KMembersRequests(int id, int count, int offset){
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
                String req = get25KMembersRequests(groupID, count, i);
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
