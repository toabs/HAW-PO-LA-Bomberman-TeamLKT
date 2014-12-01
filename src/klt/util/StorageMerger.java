package klt.util;

import java.util.HashMap;

/**
 * Created by Tobi on 24.11.2014.
 */
public class StorageMerger {

    public static void merge(String first, String second, String output){
        HashMap firstMap = SaveDataUtility.loadStorage(first);
        HashMap secondMap = SaveDataUtility.loadStorage(second);
        SaveDataUtility.writeStorage(SaveDataUtility.updateData(firstMap, secondMap), output);
    }

    public static void main(String[] args) {
        String FIRST = "KI_FighterAdvA.rgo";
        String SECOND = "KI_FighterAdvB.rgo";
        String OUTPUT = "KI_FighterAdv.rgo";


        merge(FIRST,SECOND, OUTPUT);
    }
}
