package com.spaceman.fileHander;

import java.util.Collection;
import java.util.HashMap;

public class GettingFiles {
    
    private static HashMap<String, Files> list = new HashMap<>();
    
    public static void loadFiles() {
        
        list = new HashMap<>();
        
        list.put("FileName", new Files("FileName.yml"));//todo add files here
    }
    
    public static Files getFile(String file) {
        return list.getOrDefault(file.replace(".yml", ""), null);
    }
    
    public static Collection<Files> getFiles() {
        return list.values();
    }
    
    public static void reloadFile(String file) {
        list.put(file.replace(".yml", ""), new Files(file + (file.endsWith(".yml") ? "" : ".yml")));
    }
}
