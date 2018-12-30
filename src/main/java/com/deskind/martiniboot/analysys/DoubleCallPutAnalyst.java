package com.deskind.martiniboot.analysys;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author deski
 */
public class DoubleCallPutAnalyst implements Analyst{
    private List<String> tList;
    private int pointer = 0;

    public DoubleCallPutAnalyst() {
        tList = new ArrayList<>();
        tList.add("CALL");
        tList.add("CALL");
        tList.add("PUT");
        tList.add("PUT");
    }
    
    public String makeDescision(){
        
        if(pointer == tList.size())	
        	pointer = 0;
        
        return tList.get(pointer++);
    }

    @Override
    public String getName() {
        return "DoubleCallPutAnalyst";
    }
}
