package klt.util;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Tobi on 03.11.2014.
 */
public class SaveDataUtility {

    @SuppressWarnings("unchecked")
	public static HashMap<String, HashMap<Integer, Double>> updateData(HashMap<String, HashMap<Integer, Double>> first, HashMap<String, HashMap<Integer, Double>> second) {

        HashMap<String, HashMap<Integer, Double>> result = new HashMap<>();

        for (String keyFirst : first.keySet()) {
            if (second.containsKey(keyFirst)) {
                HashMap<Integer, Double> temp = new HashMap<>();
                for (Integer keyFirstAction : first.get(keyFirst).keySet()) {
                    double value = (first.get(keyFirst).get(keyFirstAction) + second.get(keyFirst).get(keyFirstAction)) / 2.0;
                    temp.put(keyFirstAction, value);
                }
            } else {
                result.put(keyFirst, (HashMap<Integer, Double>) first.get(keyFirst).clone());
            }
        }

        for( String keySecond : second.keySet()){
            if (!result.containsKey(keySecond)){
                result.put(keySecond, (HashMap<Integer, Double>) second.get(keySecond).clone());
            }
        }

        return result;
    }

    public static HashMap<String, HashMap<Integer, Double>> mergeQTableWithData(HashMap<String, HashMap<Integer, Double>> toMerge, String filePathOfOtherData){
        HashMap<String, HashMap<Integer, Double>> other = loadStorage(filePathOfOtherData);
        if(other != null && toMerge != null) {
            HashMap<String, HashMap<Integer, Double>> res = updateData(toMerge, other);
            return res;
        }else{
            System.err.println("NO DATA FOUND.");
            return null;
        }

    }

    public static HashMap<String, HashMap<Integer, Double>> mergeTwoQTableDatas(String firstTable, String secondTable){
        return mergeQTableWithData(loadStorage(firstTable), secondTable);
    }

    public static void writeStorage(HashMap<String, HashMap<Integer, Double>> toSave, String storagePath){
        try
        {
            FileOutputStream fout = new FileOutputStream(storagePath);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(toSave);
            oos.close();
        } catch (IOException e)
        {
            System.out.println("Error saving observationStorage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	public static HashMap<String, HashMap<Integer, Double>> loadStorage(String filePath){
        File f = new File(filePath);

        if (f.exists() && !f.isDirectory())
        {
            HashMap<String, HashMap<Integer, Double>> otherStorage = null;
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(filePath);
                ObjectInputStream ois = new ObjectInputStream(fin);
                otherStorage = (HashMap<String, HashMap<Integer, Double>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

            return otherStorage;
        }
        else
        {
            System.err.println("There is no Data to read! Path:" + filePath);
            return null;
        }
    }
}
