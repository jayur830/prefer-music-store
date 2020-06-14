package com.prefer_music_store.app.model.imgproc;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.io.File;
import java.io.IOException;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

/**
 * CNN 기반 성별, 나이 추정
 * @apiNote input : 63 * 63
 * @apiNote output : 1 * 2, 1 * 101
 */
@Component
public class AgeGenderEstimation {
	private ComputationGraph nn = null;

	public AgeGenderEstimation() {
		try {
			// 모델 로드
			this.nn = ModelSerializer.restoreComputationGraph(getClass().getResource("/model").getPath() + "age_gender_estimation.zip");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 성별 및 나이 추정, 이미지 파일명을 인자로 전달
	public int[] predict(String fileName) {
		// 이미지 파일을 로드하여 인자로 전달 후 성별 및 나이 추정 결과 반환
		return predict(Imgcodecs.imread(fileName));
	}

	// 성별 및 나이 추정, OpenCV의 Mat(이미지를 3차원 행렬 형태로 저장) 인스턴스를 인자로 전달
	public int[] predict(Mat img) {
		int[] predictValue = null;
		try {
			// 63 * 63 크기로 이미지 사이즈 조정
			Imgproc.resize(img, img, new Size(63, 63));
			// INDArray 타입으로 변환 후 인자로 전달, 성별 및 나이 추정
			predictValue = predict(new NativeImageLoader().asMatrix(img));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return predictValue;
	}
	
	// 성별 및 나이 추정, bytedeco 패키지 내 OpenCV의 Mat(이미지를 3차원 행렬 형태로 저장, ) 인스턴스를 인자로 전달
	public int[] predict(org.bytedeco.opencv.opencv_core.Mat img) {
		int[] predictValue = null;
		try {
			// 63 * 63 크기로 이미지 사이즈 조정
			resize(img, img, new org.bytedeco.opencv.opencv_core.Size(63, 63));
			// INDArray 타입으로 변환 후 인자로 전달, 성별 및 나이 추정
			predictValue = predict(new NativeImageLoader().asMatrix(img));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return predictValue;
	}

	// 성별 및 나이 추정, 3차원 픽셀 데이터를 인자로 전달
	public int[] predict(double[][][] buf) {
		// INDArray 인스턴스로 변환 후 인자로 전달, 성별 및 나이 추정
		return predict(Nd4j.create(new double[][][][] { buf }));
	}

	// 성별 및 나이 추정, ND4J의 INDArray 인스턴스에 픽셀 데이터를 할당하여 인자로 전달
	public int[] predict(INDArray feature) {
		/** 
		 * Neural Network에 feature를 입력한 결과
		 * output[0]: 성별, 1 * 2 의 1차원 배열(0: 여성, 1: 남성)
		 * output[1]: 나이, 1 * 100의 1차원 배열(0세 ~ 99세)
		 * */
		INDArray[] output = this.nn.output(feature);
		
		int gender = output[0].argMax(1).getInt(0);
		int age = output[1].argMax(1).getInt(0);

		return new int[] { gender, age };
	}
}