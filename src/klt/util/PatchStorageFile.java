package klt.util;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Tobi on 01.12.2014.
 */
public class PatchStorageFile {

    public static void patch(String input, String output){
        HashMap<String, HashMap<Integer, Double>> inputStorage = SaveDataUtility.loadCompressedStorage(input);
        HashMap<String, HashMap<Integer, Double>> outputStorge = SaveDataUtility.patchOldStorages(inputStorage);
        SaveDataUtility.writeCompressedStorage(outputStorge, output);
    }
    public static void main(String[] args) {
        String INPUT = "KI_FighterAdvCompresed.rgo";
        String OUTPUT = "KI_FighterAdvCompressedPatched.rgo";
        patch(INPUT, OUTPUT);
        //SaveDataUtility.writeCompresedStorage(SaveDataUtility.loadStorage(INPUT), OUTPUT);
    }
}
