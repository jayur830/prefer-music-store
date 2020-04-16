package com.prefer_music_store.app.model.imgproc;

import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Component;

import java.util.List;

// Haar Cascade 기반 얼굴 영역 탐지
@Component
public class FaceDetection {
    static { OpenCVConfig.loadNativeLibrary(); }

    private CascadeClassifier classifier;

    public FaceDetection() {
    	// xml 파일(모델) 로드
        try {
            this.classifier = new CascadeClassifier(Class.forName("com.prefer_music_store.app.model.imgproc.FaceDetection").getResource("/model/haarcascade_frontalface_alt2.xml").getFile().substring(1));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 탐지된 여러 얼굴 영역을 리스트로 반환
    public List<Rect> predict(Mat img) {
        MatOfRect rects = new MatOfRect();				// 얼굴 영역들의 좌표들을 저장할 인스턴스
        this.classifier.detectMultiScale(img, rects);	    // 얼굴 영역 탐지, 수행 후 rects에 얼굴 영역의 좌표들이 저장됨
        return rects.toList();							                // 리스트로 반환
    }
}
