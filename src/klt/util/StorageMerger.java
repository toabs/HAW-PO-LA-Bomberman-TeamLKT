package klt.util;

import java.util.HashMap;

/**
 * Created by Tobi on 24.11.2014.
 */
public class StorageMerger {

    public static void merge(String first, String second, String output){
        HashMap firstMap = SaveDataUtility.loadCompressedStorage(first);
        HashMap secondMap = SaveDataUtility.loadCompressedStorage(second);
        SaveDataUtility.writeCompressedStorage(SaveDataUtility.updateData(firstMap, secondMap), output);
    }

    public static void main(String[] args) {
        String FIRST = "KI_Bomberman1.rgo";
        String SECOND = "KI_Bomberman2.rgo";
        String OUTPUT = "KI_Bomberman.rgo";


        merge(FIRST,SECOND, OUTPUT);
    }
}
