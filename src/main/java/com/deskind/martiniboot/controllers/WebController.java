package com.deskind.martiniboot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deskind.martiniboot.MartiniBootApplication;
import com.deskind.martiniboot.trade.Signal;
import com.deskind.martiniboot.trade.flow.Flow;

@RestController
@RequestMapping("/martini")
public class WebController {
	
	//http://localhost:8880/martini/test?symbol=R_25&type=CALL&duration=30&duration_unit=s
	
	@GetMapping("/test")
	public void test(@RequestParam("symbol") String symbol,
						@RequestParam("type") String type,
						@RequestParam("duration") int duration,
						@RequestParam("duration_unit") String durationUnit) {
		
		MainController controller = MartiniBootApplication.getMainController();
		Flow flow = MartiniBootApplication.getFlow();
		
		Signal signal = new Signal(symbol, type, duration, durationUnit);
		
		if(flow == null) {
			controller.writeMessage("+++ Trading process not started", true, true);
			return;
		}
		
		flow.makeSignalBet(signal);
		
	}
}	
