package com.prefer_music_store.app.model.mf;

// 모델 옵티마이저
public interface AlternatingLeastSquares<T> {
	/**
	 * 유저 기준 학습
	 * @param x: 유저 행렬
     * @param y: 아이템 행렬
     * @param c: 신뢰도
     * @param p: 선호도
     * @param nUsers: MF의 행의 개수
     * @param nFactor: 임의의 상수(50~200 사이로 지정)
     * @param lambda: 임의의 상수
	 * */
    public void optimizeUser(T x, T y, T c, T p, int nUsers, int nFactor, double lambda);
    
    /**
     * 아이템 기준 학습
     * @param x: 유저 행렬
     * @param y: 아이템 행렬
     * @param c: 신뢰도
     * @param p: 선호도
     * @param nItems: MF의 열의 개수
     * @param nFactor: 임의의 상수(50~200 사이로 지정)
     * @param lambda: 임의의 상수
     * */
    public void optimizeItem(T x, T y, T c, T p, int nItems, int nFactor, double lambda);
    
    /**
     * Loss Function
     * @param x: 유저 행렬
     * @param y: 아이템 행렬
     * @param c: 신뢰도
     * @param p: 선호도
     * @param lambda: 임의의 상수
     * */
    public double[] loss(T x, T y, T c, T p, double lambda);
}
