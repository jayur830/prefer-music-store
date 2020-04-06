package com.prefer_music_store.app.model.mf;

// Matrix Factorization 알고리즘
public interface MatrixFactorization {
	/**
	 * 로그 출력 여부 설정
	 * @param printLog: 학습 경과 시 로그 출력 여부
	 * */
    void setPrintLog(boolean printLog);
    
    /**
     * 모델 학습
	 * @param stepSize: 학습 횟수
	 * */
    void fit(int stepSize);
    
    // 예측
    double[][] predict();
    
    // 원본 행렬 R 반환
    double[][] getR();
    
    /**
     * 원본 행렬 R의 user 행 item 열의 위치에 rating 할당
	 * @param user: 행
	 * @param item: 열
	 * @param rating: 평점 값
	 * */
    void put(int user, int item, double rating);
    
    /**
     * MF 행 추가
     * @param user: 새로운 행
     * */
    void addUser(double[] user);
    
    /**
     * MF 여러 행 추가
     * @param _users: 새로운 행들의 집합
     * */
    void addUsers(double[][] _users);
    
    // 모든 원소가 0인 행 추가
    void addEmptyUser();
    
    /**
     * 모든 원소가 0인 행을 nUsers개만큼 추가
     * @param nUsers: 추가할 행 개수
     * */
    void addEmptyUsers(int nUsers);
    
    // 모든 원소가 0인 열 추가
    void addEmptyItem();
    
    /**
     * 모든 원소가 0인 열을 nItems개만큼 추가
     * @param nItems: 추가할 열 개수
     * */
    void addEmptyItems(int nItems);
    
    // 모델 저장
    void saveModel(String fileName);
    
    // 모델 로드
    void loadModel(String modelPath);
}
