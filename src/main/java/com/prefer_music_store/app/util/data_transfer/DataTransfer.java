package com.prefer_music_store.app.util.data_transfer;

import java.io.*;
import java.net.Socket;

// 소켓 통신을 위한 추상 클래스
public abstract class DataTransfer {
    protected Socket socket = null;								// 클라이언트 통신 소켓
    protected InputStream inputStream = null;					// 데이터를 받는 스트림 객체, 바이트 단위 수신
    protected OutputStream outputStream = null;					// 데이터를 보내는 스트림 객체, 바이트 단위 송신

    protected InputStreamReader inputReader = null;				// 데이터를 받는 스트림 객체, 한글 지원
    protected OutputStreamWriter outputWriter = null;			// 데이터를 보내는 스트림 객체, 한글 지원, 문자열 데이터 송신 가능

    // 데이터 송신, 512 바이트씩 끊어서 보냄
    public void send(String data) {
        try {
            for (int i = 0; i < data.length(); i += 512) {
                this.outputWriter.write(data.substring(i, Math.min(i + 512, data.length())));
                this.outputWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 데이터 수신, 512 바이트씩 끊어서 받음
    public String receive() {
        int len = -1;					// 데이터의 총 길이 저장
        char[] buf = new char[512];		// 임시 데이터 보관
        StringBuilder s = new StringBuilder();
        try {
            do {
                len = this.inputReader.read(buf);
                s.append(new String(buf), 0, len);
            } while (len == buf.length);
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
        return s.toString();
    }

    // 파일 송신
    public void sendFile(File file) {
        // 파일의 총 길이를 알아냄, 문자열 형식으로 반환
        String len = String.valueOf(file.length());
        //
        String header = "0000000000".substring(0, 10 - len.length()) + len;

        try {
            FileInputStream fis = new FileInputStream(file);
            this.outputStream.write(header.getBytes());

            byte[] buf = new byte[65536];
            while (fis.available() > 0) {
                int size = fis.read(buf);
                this.outputStream.write(buf, 0, size);
                this.outputStream.flush();
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일 수신
    public boolean receiveFile(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            byte[] buf = new byte[512];

            this.inputStream.read(buf, 0, 10);
            String header = new String(buf, 0, 10);

            int bodySize = Integer.parseInt(header);
            if (bodySize == 0) {
                fos.close();
                return false;
            }
            int readSize = 0;
            while (readSize < bodySize) {
                int size = this.inputStream.read(buf);
                fos.write(buf, 0, size);
                readSize += size;
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected();
    }

    public boolean isClosed() {
        return this.socket.isClosed();
    }

    public boolean isBound() {
        return this.socket.isBound();
    }

    // 소켓 종료, 하위 클래스에서 구현
    public abstract void close();
}