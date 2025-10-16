package com.example.demo.handler;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.service.sseService;

@RestController
@CrossOrigin("*")
public class sseController {

	private final sseService sseService;

	// 생성자를 통해 SseService를 주입받습니다.
	public sseController(sseService sseService) {
		this.sseService = sseService;
	}

	/**
	 * ✅ 프론트엔드가 SSE 연결을 시작할 때 호출하는 API produces = MediaType.TEXT_EVENT_STREAM_VALUE
	 * 는 이 엔드포인트가 SSE 연결임을 명시합니다.
	 */
	@GetMapping(value = "/api/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter connect() {
		// SseService를 통해 새로운 클라이언트 연결을 생성하고 SseEmitter 객체를 반환합니다.
		return sseService.createEmitter();
	}

	@PostMapping("/api/log")
	public ResponseEntity<Void> sendLog(@RequestBody String payload) {
		// "log" 라는 키로 들어온 메시지를 꺼내서 모든 클라이언트에게 전송
		String logMessage = payload;

		if (logMessage != null && !logMessage.isEmpty()) {
			sseService.sendLogToClients(logMessage); // 이전에 만든 SseService의 메소드 호출
			System.out.println("Log sent via API: " + logMessage);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.badRequest().build(); // log 내용이 없으면 400 에러
		}
	}
}