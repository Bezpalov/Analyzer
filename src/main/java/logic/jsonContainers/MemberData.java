package logic.jsonContainers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.management.MemoryManagerMXBean;
import java.util.List;

public class MemberData {
    @SerializedName("response")
    @Expose
    private List<MembersResponse> response = null;

    public List<MembersResponse> getMembersResponse() {
        return response;
    }

    public void setMembersResponse(List<MembersResponse> response) {
        this.response = response;
    }


}
