package com.deskind.martiniboot.analysys;

/**
 *
 * @author deski
 */
public class UpDownAnalyst implements Analyst{
	
	//initial value 
	private String current = "CALL";

    @Override
    public String makeDescision() {
    	if(current.equals("PUT")){
    		current = "CALL";
    		return "PUT";
    	}else{
    		current = "PUT";
    		return "CALL";
    	}
    }
    
    @Override
    public String getName() {
        return "Up Down Strategy";
    }
}
