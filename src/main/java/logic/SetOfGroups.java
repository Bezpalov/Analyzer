package logic;

import java.util.*;

public class SetOfGroups {
    HashMap<Integer, Integer> map;

    public SetOfGroups(){
        map = new HashMap<Integer, Integer>();
    }

    public void add(Integer groupID){
        if(map.containsKey(groupID)){
            Integer value = map.get(groupID);
            map.replace(groupID, value, ++value);
        }else
            map.put(groupID, 1);

    }

    public Integer sumOfvalues(){
        Collection<Integer> list = map.values();
        int size = list.size();
        Integer[] array = new Integer[size];
        int sum = 0;
        list.toArray(array);
        for (int i = 0; i < size; i++) {
            sum += array[i];
        }
        return sum;
    }

    @Override
    public String toString() {
        int sum = sumOfvalues();
        StringBuilder builder = new StringBuilder();

       for(Map.Entry<Integer, Integer> entry: map.entrySet()){
            int key = entry.getKey();
            int value = entry.getValue();
            double res = (double)value/sum;
            builder.append("Id: " + key +  " - % is: " + res + "\n");
       }
        return builder.toString();
    }
}
