package logic.jsonContainers;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberGroupResponse {

    @SerializedName("response")
    @Expose
    private List<List<Integer>> response = null;

    public List<List<Integer>> getResponse() {
        return response;
    }

    public void setResponse(List<List<Integer>> response) {
        this.response = response;
    }

    public List<Integer> getList(){
        ArrayList<Integer>  array = new ArrayList<>();

        for (int i = 0; i < response.size(); i++) {
            for (int j = 0; j < response.get(i).size(); j++) {
                array.add(response.get(i).get(j));
            }
        }
        return array;
    }
}