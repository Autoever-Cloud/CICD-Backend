package com.example.demo.handler;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.LLM.requestLLM;
import com.example.demo.dto.sologDTO;

@RestController
@CrossOrigin(origins = "*")
public class sologController {
	public requestLLM requestLLM;

	@PostMapping("/api/solog")
	public Map<String, String> handleSologMessage(@RequestBody sologDTO message) {
		System.out.println("React에서 받은 메시지: " + message);
		String userPrompt = message.getPrompt();
		try {
			String responseLLM = requestLLM.callGptApi(userPrompt);

			return Map.of("response", responseLLM);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Map.of("status", "error");
		}
	}
}
