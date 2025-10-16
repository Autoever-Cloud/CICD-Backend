package com.example.demo.handler;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.example.demo.service.sseService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class snsController {

	private final sseService sseService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public snsController(sseService sseService) {
		this.sseService = sseService;
	}

	/**
	 * AWS SNS로부터 모든 메시지(구독 확인, 알림)를 받는 엔드포인트입니다. AWS 콘솔에서 이 엔드포인트 URL을 구독 신청해야 합니다.
	 * (예: http://내EC2주소/aws/sns/message)
	 */
	@PostMapping("/message")
	public ResponseEntity<Void> handleSnsMessage(@RequestBody Map<String, Object> payload) {
		System.out.println("Received SNS payload: " + payload);

		String messageType = (String) payload.get("Type");

		if ("SubscriptionConfirmation".equals(messageType)) {
			// "구독 확인" 요청 처리
			String subscribeUrl = (String) payload.get("SubscribeURL");
			System.out.println("Confirming SNS subscription by visiting URL: " + subscribeUrl);

			// RestTemplate으로 SubscribeURL을 방문하여 구독을 최종 확정합니다.
			new RestTemplate().getForEntity(subscribeUrl, String.class);

		} else if ("Notification".equals(messageType)) {
			// "알림" 요청 처리 (람다가 보낸 실제 데이터)
			String messageString = (String) payload.get("Message");
			System.out.println("Received Notification message: " + messageString);

			// SseService를 통해 모든 클라이언트에게 메시지를 전달합니다.
			// 프론트엔드에서 JSON 파싱을 하므로, 여기서는 받은 문자열을 그대로 전달합니다.
			sseService.sendLogToClients(messageString);
		}

		return ResponseEntity.ok().build();
	}
}
