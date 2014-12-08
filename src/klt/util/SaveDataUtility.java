package klt.util;

import klt.ObservationWithActions;

import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Tobi on 03.11.2014.
 */
public class SaveDataUtility {

	public static HashMap<String, HashMap<Integer, Double>> updateData(HashMap<String, HashMap<Integer, Double>> first, HashMap<String, HashMap<Integer, Double>> second) {

        System.out.println("Start to merge!");

        HashMap<String, HashMap<Integer, Double>> result = new HashMap<>();

        for (String keyFirst : first.keySet()) {
            if (second.containsKey(keyFirst)) {
                HashMap<Integer, Double> temp = new HashMap<>();
                for (Integer keyFirstAction : first.get(keyFirst).keySet()) {
                    double value = (first.get(keyFirst).get(keyFirstAction) + second.get(keyFirst).get(keyFirstAction)) / 2.0;
                    temp.put(keyFirstAction, value);
                }
                result.put(keyFirst, temp);
            } else {
                result.put(keyFirst, (HashMap<Integer, Double>) first.get(keyFirst).clone());
            }
        }

        for( String keySecond : second.keySet()){
            if (!result.containsKey(keySecond)){
                result.put(keySecond, (HashMap<Integer, Double>) second.get(keySecond).clone());
            }
        }

        System.out.println("Number of observations: " + result.keySet().size());

        System.out.println("Merge Successful!");

        return result;
    }

    public static HashMap<String, HashMap<Integer, Double>> patchOldStorages(HashMap<String, HashMap<Integer, Double>> oldStorage){
        HashMap<String, HashMap<Integer, Double>> res = new HashMap<>();

        System.out.println("Number of Observations: " + oldStorage.keySet().size());

        String newObsString;
        for (String observation : oldStorage.keySet()){
            newObsString = ObservationWithActions.trimObservationString(observation);
            res.put(newObsString, oldStorage.get(observation));
        }
        return res;
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
        System.out.println("Start to write storagefile: " + storagePath);
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
        System.out.println("Successfully wrote storagefile: " + storagePath);
    }

    public static void writeCompressedStorage(HashMap<String, HashMap<Integer, Double>> toSave, String storagePath){
        System.out.println("Start to write compresed storagefile: " + storagePath);
        try
        {
            FileOutputStream fout = new FileOutputStream(storagePath);
            GZIPOutputStream gzip = new GZIPOutputStream(fout);
            ObjectOutputStream oos = new ObjectOutputStream(gzip);
            oos.writeObject(toSave);
            oos.close();
        } catch (IOException e)
        {
            System.out.println("Error saving observationStorage: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Successfully wrote storagefile: " + storagePath);
    }

	public static HashMap<String, HashMap<Integer, Double>> loadStorage(String filePath){
        File f = new File(filePath);

        System.out.println("Start to load file: " + filePath);

        HashMap<String, HashMap<Integer, Double>> storage = new HashMap<>();
        if (f.exists() && !f.isDirectory())
        {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(filePath);
                ObjectInputStream ois = new ObjectInputStream(fin);
                storage = (HashMap<String, HashMap<Integer, Double>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

            System.out.println("Successfully loaded File: " + filePath);

            return storage;
        }
        else
        {
            System.err.println("There is no Data to read! Path:" + filePath);
            return storage;
        }
    }

    public static HashMap<String, HashMap<Integer, Double>> loadCompressedStorage(String filePath){
        File f = new File(filePath);

        System.out.println("Start to load file: " + filePath);

        HashMap<String, HashMap<Integer, Double>> storage = new HashMap<>();
        if (f.exists() && !f.isDirectory())
        {
            FileInputStream fin;
            try {
                fin = new FileInputStream(filePath);
                GZIPInputStream gzip = new GZIPInputStream(fin);
                ObjectInputStream ois = new ObjectInputStream(gzip);
                storage = (HashMap<String, HashMap<Integer, Double>>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

            System.out.println("Successfully loaded File: " + filePath);

            return storage;
        }
        else
        {
            System.err.println("There is no Data to read! Path:" + filePath);
            return storage;
        }
    }
}
