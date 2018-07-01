package logic.jsonContainers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.management.MemoryManagerMXBean;
import java.util.ArrayList;
import java.util.List;

public class MemberData {
    @SerializedName("response")
    @Expose
    private MembersResponse response;

    public MembersResponse getResponse() {
        return response;
    }

    public void setResponse(MembersResponse response) {
        this.response = response;
    }



    public int getCount(){
        return response.getCount();
    }

    public List<Integer> getItems(){
        return response.getItems();
    }
}
