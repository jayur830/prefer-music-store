package com.prefer_music_store.app.util.data_transfer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DataTransferServer extends DataTransfer {

    private ServerSocket serverSocket;
    private static ServerSocket signalServer;
    private static Socket signalSocket;
    private static InputStreamReader signalReader;

    public DataTransferServer(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.socket = this.serverSocket.accept();

            if (signalServer == null || signalSocket == null || signalServer.isClosed() || signalSocket.isClosed()) {
                signalServer = new ServerSocket(56187);
                signalSocket = signalServer.accept();
                signalReader = new InputStreamReader(signalSocket.getInputStream(), StandardCharsets.UTF_8);
            }

            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();

            this.inputReader = new InputStreamReader(this.inputStream, StandardCharsets.UTF_8);        // 한글 전용 데이터 수신 스트림 객체 할당
            this.outputWriter = new OutputStreamWriter(this.outputStream, StandardCharsets.UTF_8);     // 한글 전용 데이터 송신 스트림 객체 할당
        } catch (IOException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        Thread thread = new Thread(() -> {
            while (true) {
                int signal = Integer.parseInt(signalReceive());
                if (signal == -1) {
                    System.out.println("Disconnected with client");
                    close();
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public boolean isClosed() {
        return this.serverSocket.isClosed();
    }

    @Override
    public boolean isBound() {
        return this.serverSocket.isBound();
    }

    @Override
    public void close() {
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 신호 수신
    public String signalReceive() {
        int len;					// 데이터의 총 길이 저장
        char[] buf = new char[8];		// 임시 데이터 보관
        StringBuilder s = new StringBuilder();
        try {
            do {
                len = signalReader.read(buf);
                s.append(new String(buf), 0, len);
            } while (len == buf.length);
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
        return s.toString();
    }
}
