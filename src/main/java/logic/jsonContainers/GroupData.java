package logic.jsonContainers;



import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupData {

    @SerializedName("response")
    @Expose
    private List<GroupResponse> response = null;

    public List<GroupResponse> getResponse() {
        return response;
    }

    public void setResponse(List<GroupResponse> response) {
        this.response = response;
    }

    public int getID(int responseIndex){
        return response.get(responseIndex).getId();
    }

    public String getName(int responseIndex){
        return response.get(responseIndex).getName();
    }

    public int getMembersCount(int responseIndex){
        return response.get(responseIndex).getMemberCount();
    }

}
