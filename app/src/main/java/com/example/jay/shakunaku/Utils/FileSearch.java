package com.example.jay.shakunaku.Utils;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {


    /**
     * Search a directory and return a list of all directories contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] fileLists = file.listFiles();
        for(int i=0; i<fileLists.length; i++){
            if(fileLists[i].isDirectory()){
                pathArray.add(fileLists[i].getAbsolutePath());
            }
        }

        return pathArray;
    }

    /**
     * Search a directory and return a list of all files contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] fileLists = file.listFiles();
        for(int i=0; i<fileLists.length; i++){
            if(fileLists[i].isFile()){
                pathArray.add(fileLists[i].getAbsolutePath());
            }
        }

        return pathArray;
    }
}
