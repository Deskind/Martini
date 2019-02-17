/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deskind.martiniboot.analysys;

import java.util.Random;

/**
 *
 * @author deski
 */
public class RandomAnalyst implements Analyst{

    @Override
    public String makeDescision() {
        String result = "";
        
        Random random = new Random();
        int randomInt = random.nextInt(777);
        
        if(randomInt%2 == 0){
            return "CALL";
        }else{
            return "PUT";
        }
    }
    
    @Override
    public String getName() {
        return "RandomStrategy";
    }
    
}
