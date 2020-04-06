package com.prefer_music_store.app.model.mf;

import java.util.Arrays;

// 행렬 연산 유틸리티 클래스 직접 구현
public class MatrixUtil {
	/**
	 * 전치행렬
	 * @param src: 원본 행렬
	 * */
    public static double[][] transpose(double[][] src) {
        double[][] dst = new double[src[0].length][src.length];
        for (int i = 0; i < src.length; ++i)
            for (int j = 0; j < src[i].length; ++j)
                dst[j][i] = src[i][j];
        return dst;
    }

    /**
     * 모든 원소가 value인 rows * cols 크기의 행렬
     * @param rows: 새로 생성할 행렬의 행 개수
     * @param cols: 새로 생성할 행렬의 열 개수
     * @param value: 새로 생성할 행렬의 모든 원소로 할당할 값
     * */
    public static double[][] fill(int rows, int cols, double value) {
        double[][] matrix = new double[rows][cols];
        for (double[] mat : matrix) Arrays.fill(mat, value);
        return matrix;
    }

    /**
     * 행렬 A와 B의 합
     * @param a: 행렬 A
     * @param b: 행렬 B
     * */
    public static double[][] sum(double[][] a, double[][] b) {
        double[][] matrix = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; ++i)
            for (int j = 0; j < a[i].length; ++j)
                matrix[i][j] = a[i][j] + b[i][j];
        return matrix;
    }

    /**
     * 행렬 A와 B의 차
     * @param a: 행렬 A
     * @param b: 행렬 B
     * */
    public static double[][] subtract(double[][] a, double[][] b) {
        double[][] matrix = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; ++i)
            for (int j = 0; j < a[i].length; ++j)
                matrix[i][j] = a[i][j] - b[i][j];
        return matrix;
    }

    /**
     * 상수 A와 B의 곱
     * @param a: 상수 A
     * @param b: 행렬 B
     * */
    public static double[][] product(double a, double[][] b) {
        double[][] dst = new double[b.length][b[0].length];
        for (int i = 0; i < dst.length; ++i)
            for (int j = 0; j < dst[i].length; ++j)
                dst[i][j] = a * b[i][j];
        return dst;
    }

    /**
     * 행렬 A와 B의 곱
     * @param a: 행렬 A
     * @param b: 행렬 B
     * */
    public static double[][] product(double[][] a, double[][] b) {
        double[][] dst = new double[a.length][b[0].length];
        for (int i = 0; i < dst.length; ++i)
            for (int j = 0; j < dst[i].length; ++j)
                for (int k = 0; k < a[0].length; ++k)
                    dst[i][j] += a[i][k] * b[k][j];
        return dst;
    }

    /**
     * 벡터 A와 B의 내적
     * @param a: 벡터 A
     * @param b: 벡터 B
     * */
    public static double dot(double[] a, double[] b) {
        double total = 0;
        for (int i = 0; i < a.length; ++i)
            total += a[i] * b[i];
        return total;
    }

    public static double[] multiple(double[][] a, double[] b) {
        double[] vector = new double[a.length];
        for (int i = 0; i < vector.length; ++i)
            vector[i] = dot(a[i], b);
        return vector;
    }

    /**
     * 역행렬
     * @param matrix: 원본 행렬
     * */
    public static double[][] inverse(double[][] matrix) {
        double[][] src = matrix.clone(), dst = identity(src.length);
        for (int j = 0; j < src[0].length; ++j)
            for (int i = j, count = 0; count < src.length; i = i != src.length - 1 ? i + 1 : 0, ++count) {
                double factor = src[i][j];
                if (i == j)
                    for (int k = 0; k < src[i].length; ++k) {
                        src[i][k] /= factor;
                        dst[i][k] /= factor;
                    }
                else
                    for (int k = 0; k < src[i].length; ++k) {
                        src[i][k] += -factor * src[j][k];
                        dst[i][k] += -factor * dst[j][k];
                    }
            }
        return dst;
    }

    /**
     * 단위행렬
     * @param length: n * n의 단위행렬에서 n의 값
     * */
    public static double[][] identity(int length) {
        return diagonal(length, 1);
    }

    /**
     * 대각행렬: 왼쪽 위 원소부터 오른쪽 아래 원소까지가 전부 특정 값으로 할당되며, 나머지 원소가 전부 0인 행렬
     * @param length: 행, 열의 공통 길이
     * @param value: 채워넣을 값
     * */
    public static double[][] diagonal(int length, double value) {
        double[][] matrix = new double[length][length];
        for (int i = 0; i < length; ++i)
            for (int j = 0; j < length; ++j)
                matrix[i][j] = i == j ? value : 0;
        return matrix;
    }

    /**
     * 행렬을 대각행렬로 변환
     * @param mat: 원본 행렬
     * */
    public static double[][] diagonal(double[][] mat) {
        double[][] matrix = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; ++i)
            for (int j = 0; j < mat[0].length; ++j)
                matrix[i][j] = i == j ? mat[i][j] : 0;
        return matrix;
    }

    /**
     * 벡터를 대각행렬로 변환
     * @param vector: 원본 벡터
     * */
    public static double[][] diagonal(double[] vector) {
        double[][] matrix = new double[vector.length][vector.length];
        for (int i = 0; i < matrix.length; ++i)
            for (int j = 0; j < matrix[i].length; ++j)
                matrix[i][j] = i == j ? vector[i] : 0;
        return matrix;
    }

    /**
     * 행렬의 모든 원소를 요소별 제곱
     * @param matrix: 원본 행렬
     * */
    public static double[][] square(double[][] matrix) {
        double[][] mat = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; ++i)
            for (int j = 0; j < matrix[i].length; ++j)
                mat[i][j] = matrix[i][j] * matrix[i][j];
        return mat;
    }

    /**
     * 행렬의 모든 원소의 합
     * @param matrix: 원본 행렬
     * */
    public static double sum(double[][] matrix) {
        double total = 0;
        for (double[] mat : matrix)
            for (double m : mat) total += m;
        return total;
    }

    /**
     * 행렬의 모든 원소 중 최댓값
     * @param matrix: 원본 행렬
     * */
    public static double max(double[][] matrix) {
        double max = Double.NEGATIVE_INFINITY;
        for (double[] mat : matrix)
            for (double m : mat) max = Math.max(max, m);
        return max;
    }

    /**
     * 행렬의 모든 원소 중 최솟값
     * @param matrix: 원본 행렬
     * */
    public static double min(double[][] matrix) {
        double min = Double.POSITIVE_INFINITY;
        for (double[] mat : matrix)
            for (double m : mat) min = Math.min(min, m);
        return min;
    }

    /**
     * 행렬의 모든 원소를 0~1 범위로 정규화
     * @param matrix: 원본 행렬
     * */
    public static double[][] norm(double[][] matrix) {
        double min = min(matrix), max = max(matrix);
        return subtract(product(1 / (max - min), matrix), product(1 / (max - min), fill(matrix.length, matrix[0].length, min)));
    }

    /**
     * 행렬의 모든 원소를 min~max 범위로 정규화
     * @param matrix: 원본 행렬
     * @param min: 정규화 할 최소 범위
     * @param max: 정규화 할 최대 범위
     * */
    public static double[][] norm(double[][] matrix, double min, double max) {
        return sum(product(max - min, norm(matrix)), fill(matrix.length, matrix[0].length, min));
    }

    /**
     * 행렬 A와 B의 요소별 곱셈
     * @param a: 행렬 A
     * @param b: 행렬 B
     * */
    public static double[][] elementWise(double[][] a, double[][] b) {
        double[][] matrix = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; ++i)
            for (int j = 0; j < a[i].length; ++j)
                matrix[i][j] = a[i][j] * b[i][j];
        return matrix;
    }

    /**
     * 행렬의 특정 열벡터
     * @param mat: 원본 행렬
     * @param col: 열 위치
     * */
    public static double[] col(double[][] mat, int col) {
        double[] vector = new double[mat.length];
        for (int i = 0; i < vector.length; ++i)
            vector[i] = mat[i][col];
        return vector;
    }

    /**
     * Y에 대한 X의 연립방정식 풀이
     * @param x: 행렬 X (n * m)
     * @param y: 벡터 Y (1 * n, 전치 필요)
     * */
    public static double[] solve(double[][] x, double[] y) {
        return transpose(product(inverse(x), transpose(new double[][] { y })))[0];
    }

    /**
     * 행렬 출력
     * @param matrix: 원본 행렬
     * */
    public static void print(double[][] matrix) {
        for (double[] mat : matrix) {
            for (double m : mat)
                System.out.printf("%3.2f ", m);
            System.out.println();
        }
    }
}
