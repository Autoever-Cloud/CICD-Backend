package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class sseService {

	// 동시성 문제를 해결하기 위해 CopyOnWriteArrayList를 사용합니다.
	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	/**
	 * ✅ 새로운 클라이언트가 연결을 요청할 때 호출됩니다.
	 */
	public SseEmitter createEmitter() {
		// 1. SseEmitter 객체를 생성합니다. 타임아웃은 넉넉하게 10분으로 설정합니다.
		SseEmitter emitter = new SseEmitter(600_000L);

		// 2. 생성된 emitter를 리스트에 추가하여 관리합니다.
		this.emitters.add(emitter);
		System.out.println("New client connected. Total clients: " + emitters.size());

		// 3. 연결이 종료되거나(onCompletion), 타임아웃(onTimeout)될 경우 emitter를 리스트에서 제거합니다.
		emitter.onCompletion(() -> {
			this.emitters.remove(emitter);
			System.out.println("Client disconnected. Total clients: " + emitters.size());
		});
		emitter.onTimeout(() -> {
			emitter.complete();
			// onCompletion 콜백이 호출되므로 여기서 따로 remove 할 필요는 없습니다.
		});

		// 4. 첫 연결 시, 연결이 성공했다는 503 Service Unavailable 방지용 더미 데이터를 보냅니다.
		try {
			emitter.send(SseEmitter.event().name("connect").data("Connection established"));
		} catch (IOException e) {
			System.err.println("Error sending initial connection event: " + e.getMessage());
		}

		return emitter;
	}

	/**
	 * ✅ SNS 등 외부로부터 로그 데이터를 받았을 때 호출할 메소드입니다.
	 * 
	 * @param logData 전송할 로그 데이터
	 */
	public void sendLogToClients(String logData) {
		// 현재 연결된 모든 클라이언트에게 로그 데이터를 전송합니다.
		for (SseEmitter emitter : this.emitters) {
			try {

				// "log"라는 이름의 이벤트를 전송합니다.
				emitter.send(SseEmitter.event().name("log").data(logData));
			} catch (IOException e) {
				// 클라이언트와의 연결이 끊겼을 경우의 예외 처리
				System.err.println("Error sending log to client: " + e.getMessage());
				// 여기서 emitter.complete()를 호출하여 연결을 명시적으로 종료할 수 있습니다.
			}
		}
	}
}