package com.example.demo.handler;

import java.util.Map;

// import 문 추가
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.LLM.requestLLM;
import com.example.demo.dto.sologDTO;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class sologController {

    private final requestLLM requestLLM;

    @Autowired
    public sologController(requestLLM requestLLM) {
        this.requestLLM = requestLLM;
    }

    @PostMapping("/api/solog")
	public Map<String, String> handleSologMessage(@RequestBody sologDTO message) {
		System.out.println("React에서 받은 메시지: " + message);
		String userPrompt = message.getPrompt();
		String type = "report";
		try {
			String responseLLM = requestLLM.callGptApi(userPrompt, type);

			return Map.of("response", responseLLM);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Map.of("status", "error");
		}
	}
	@PostMapping("/api/solog/chatbot")
	public Map<String, String> handleSologChatbot(@RequestBody sologDTO message) {
		System.out.println("React에서 받은 메시지: " + message);
		String userPrompt = message.getPrompt();
		String type = "chatbot";
		try {
			String responseLLM = requestLLM.callGptApi(userPrompt, type);

			return Map.of("response", responseLLM);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Map.of("status", "error");
		}
	}
}