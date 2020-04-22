package com.prefer_music_store.app.util.data_transfer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// 클라이언트 측 소켓 통신 구현 클래스
public class DataTransferClient extends DataTransfer {

    private Socket signalSocket;
    private OutputStreamWriter signalWriter;

    public DataTransferClient(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);                     // 클라이언트 소켓 생성 및 IP, PORT 설정
            this.signalSocket = new Socket(ip, 56187);
            this.signalWriter = new OutputStreamWriter(this.signalSocket.getOutputStream(), StandardCharsets.UTF_8);

            this.inputStream = this.socket.getInputStream();        // 데이터 수신 스트림 객체 할당
            this.outputStream = this.socket.getOutputStream();      // 데이터 송신 스트림 객체 할당

            this.inputReader = new InputStreamReader(this.inputStream, StandardCharsets.UTF_8);        // 한글 전용 데이터 수신 스트림 객체 할당
            this.outputWriter = new OutputStreamWriter(this.outputStream, StandardCharsets.UTF_8);     // 한글 전용 데이터 송신 스트림 객체 할당
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // 프로그램 종료 시 소켓 연결 종료
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    // 소켓 종료 및 스트림 객체 소멸
    @Override
    public void close() {
        try {
            this.signalWriter.write("-1");
            this.signalWriter.close();

            this.signalSocket.close();

            this.inputReader.close();
            this.outputWriter.close();

            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
