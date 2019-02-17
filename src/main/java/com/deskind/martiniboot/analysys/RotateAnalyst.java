package com.deskind.martiniboot.analysys;

import java.util.ArrayList;
import java.util.List;

import com.deskind.martiniboot.MartiniBootApplication;

public class RotateAnalyst implements Analyst {
	
	private byte currentAnalystPointer = 0;
	
	private List<Analyst> analysts = new ArrayList<>();
	
	public RotateAnalyst(RandomAnalyst randomAnalyst, UpDownAnalyst upDownAnalyst,
			DoubleCallPutAnalyst doubleCallPutAnalyst) {
		super();
		
		//put to collection
		analysts.add(doubleCallPutAnalyst);
		analysts.add(upDownAnalyst);
		analysts.add(randomAnalyst);
	}

	@Override
	public String makeDescision() {
		Analyst analyst = analysts.get(currentAnalystPointer);
		
		return analyst.makeDescision();
	}
	
	public void rotate() {
		currentAnalystPointer++;
		
		if(currentAnalystPointer == analysts.size())
			currentAnalystPointer = 0;
		
		MartiniBootApplication.getMainController().writeMessage("Analyst changed to : " + analysts.get(currentAnalystPointer).getName(), true, true);
	}

	@Override
	public String getName() {
		return "Rotate analyst";
	}

}
