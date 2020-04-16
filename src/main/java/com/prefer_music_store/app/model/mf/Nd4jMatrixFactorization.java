package com.prefer_music_store.app.model.mf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

// ND4J 기반 Matrix Factorization 알고리즘 구현
public class Nd4jMatrixFactorization implements MatrixFactorization {
    private int nUsers, nItems, nFactor;
    private double lambda, alpha;
    private INDArray r, x, y, p, c;

    private boolean printLog = true;

    private Nd4jAlternatingLeastSquares als;

    /**
     * Constructor
     * @param r: 원본 행렬 R
     * @param nFactor: 임의의 상수(50~200 사이로 지정)
     * @param lambda: 임의의 상수
     * @param alpha: 임의의 상수
     * */
    public Nd4jMatrixFactorization(double[][] r, int nFactor, double lambda, double alpha) {
        this.lambda = lambda; this.alpha = alpha;
        
        // 행렬 X와 Y 초기화
        this.x = Nd4j.rand(this.nUsers = (this.r = Nd4j.create(r)).rows(), this.nFactor = nFactor).mul(0.01);
        this.y = Nd4j.rand(this.nItems = this.r.columns(), this.nFactor).mul(0.01);
        // 행렬 P와 C 초기화
        init();

        this.als = new Nd4jAlternatingLeastSquares();
    }

    /**
     * Constructor
     * @param r: 원본 행렬 R
     * @param nFactor: 임의의 상수(50~200 사이로 지정)
     * @param lambda: 임의의 상수
     * @param alpha: 임의의 상수
     * @param printLog: 학습 경과 시 로그 출력 여부
     * */
    public Nd4jMatrixFactorization(double[][] r, int nFactor, double lambda, double alpha, boolean printLog) {
        this(r, nFactor, lambda, alpha);
        setPrintLog(printLog);
    }

    /**
     * Constructor
     * @param nUsers: 유저의 수(MF의 행 길이)
     * @param nItems: 아이템의 수(MF의 열 길이)
     * @param nFactor: 임의의 상수(50~200 사이로 지정)
     * @param lambda: 임의의 상수
     * @param alpha: 임의의 상수
     * */
    public Nd4jMatrixFactorization(int nUsers, int nItems, int nFactor, double lambda, double alpha) {
        this.r = Nd4j.zeros(this.nUsers = nUsers, this.nItems = nItems);

        this.lambda = lambda; this.alpha = alpha;
        
        // 행렬 X와 Y 초기화
        this.x = Nd4j.rand(this.nUsers, this.nFactor = nFactor);
        this.y = Nd4j.rand(this.nItems, this.nFactor);
        if (this.nUsers != 0) this.x = this.x.mul(0.01);
        if (this.nItems != 0) this.y = this.y.mul(0.01);
        // 행렬 P와 C 초기화
        init();

        this.als = new Nd4jAlternatingLeastSquares();
    }

    /**
     * Constructor
     * @param nUsers: 유저의 수(MF의 행 길이)
     * @param nItems: 아이템의 수(MF의 열 길이)
     * @param nFactor: 임의의 상수(50~200 사이로 지정)
     * @param lambda: 임의의 상수
     * @param alpha: 임의의 상수
     * @param printLog: 학습 경과 시 로그 출력 여부
     * */
    public Nd4jMatrixFactorization(int nUsers, int nItems, int nFactor, double lambda, double alpha, boolean printLog) {
        this(nUsers, nItems, nFactor, lambda, alpha);
        setPrintLog(printLog);
    }

    /**
     * Constructor
     * @param modelPath: 모델 파일명
     * */
    public Nd4jMatrixFactorization(String modelPath) {
        loadModel(modelPath);
        this.als = new Nd4jAlternatingLeastSquares();
    }

    /**
     * 로그 출력 여부 설정
	 * @param printLog: 학습 경과 시 로그 출력 여부
	 * */
    @Override
    public void setPrintLog(boolean printLog) {
        this.printLog = printLog;
    }

    /**
     * 모델 학습
	 * @param stepSize: 학습 횟수
	 * */
    @Override
    public void fit(int stepSize) {
    	// stepSize만큼 반복
        for (int step = 1; step <= stepSize; ++step) {
        	// 유저 기준 학습
            this.als.optimizeUser(this.x, this.y, this.c, this.p, this.nUsers, this.nFactor, this.lambda);
            // 아이템 기준 학습
            this.als.optimizeItem(this.x, this.y, this.c, this.p, this.nItems, this.nFactor, this.lambda);

            // 로그 출력
            if (this.printLog) {
            	// Loss Function
                double[] loss = this.als.loss(this.x, this.y, this.c, this.p, this.lambda);
                System.out.println("------------------------------Step " + step + "----------------------------");
                System.out.println("predict error: " + loss[0]);
                System.out.println("confidence error: " + loss[1]);
                System.out.println("regularization: " + loss[2]);
                System.out.println("total loss: " + loss[3]);
            }
        }
    }

    // 예측
    @Override
    public double[][] predict() {
    	// 행렬 X와 Y(전치)를 곱한 후 double[][] 타입 데이터로 변환하여 반환
        return Nd4j.matmul(this.x, this.y.transpose()).toDoubleMatrix();
    }

    // 원본 행렬 R 반환
    @Override
    public double[][] getR() {
        return this.r.toDoubleMatrix();
    }
    
    /**
     * 원본 행렬 R의 user 행 item 열의 위치에 rating 할당
	 * @param user: 행
	 * @param item: 열
	 * @param rating: 평점 값
	 * */
    @Override
    public void put(int user, int item, double rating) {
        this.r.put(user, item, rating);
        this.p.put(user, item, rating > 0 ? 1 : 0);
        this.c.put(user, item, 1 + this.alpha * rating);
    }

    /**
     * MF 행 추가
     * @param user: 새로운 행
     * */
    @Override
    public void addUser(double[] user) {
        addUsers(new double[][] { user });
    }

    /**
     * MF 여러 행 추가
     * @param _users: 새로운 행들의 집합
     * */
    @Override
    public void addUsers(double[][] _users) {
        this.nUsers += _users.length;
        this.r = Nd4j.concat(0, this.r, Nd4j.create(_users));
        this.x = Nd4j.concat(0, this.x, Nd4j.rand(_users.length, this.nFactor).mul(0.01));

        init();
    }

    // 모든 원소가 0인 행 추가
    @Override
    public void addEmptyUser() {
        double[] user = new double[this.nItems];
        Arrays.fill(user, 0);
        addUser(user);
    }
    
    /**
     * 모든 원소가 0인 행을 nUsers개만큼 추가
     * @param nUsers: 추가할 행 개수
     * */
    @Override
    public void addEmptyUsers(int nUsers) {
        double[][] users = new double[nUsers][this.nItems];
        for (double[] user : users) Arrays.fill(user, 0);
        addUsers(users);
    }

    // 모든 원소가 0인 열 추가
    @Override
    public void addEmptyItem() {
        addEmptyItems(1);
    }

    /**
     * 모든 원소가 0인 열을 nItems개만큼 추가
     * @param _items: 추가할 열 개수
     * */
    @Override
    public void addEmptyItems(int _items) {
        this.nItems += _items;
        // [nUsers, nItems]
        this.r = Nd4j.concat(1, this.r, Nd4j.zeros(this.nUsers, _items));
        this.y = Nd4j.concat(0, this.y, Nd4j.rand(_items, this.nFactor).mul(0.01));

        init();
    }
    
    /**
     * 초기화
     * @implNote c[u][i] = 1 + alpha * r[u][i]
     * @implNote p[u][i] = r[u][i] > 0 ? 1 : 0
     * */
    private void init() {
        this.c = Nd4j.create(this.r.rows(), this.r.columns());
        this.p = Nd4j.create(this.r.rows(), this.r.columns());
        for (int u = 0; u < this.r.rows(); ++u)
            for (int i = 0; i < this.r.columns(); ++i) {
                this.c.put(u, i, 1 + this.alpha * this.r.getDouble(u, i));
                this.p.put(u, i, this.r.getDouble(u, i) > 0 ? 1 : 0);
            }
    }
    
    // 모델 저장
    @Override
    public void saveModel(String fileName) {
        if (fileName.contains(".zip")) {
            String[] confs = new String[]{"conf.csv", "r.csv", "x.csv", "y.csv", "c.csv", "p.csv"};
            try {
                for (String conf : confs) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(conf));
                    if (conf.equals("conf.csv")) {
                        writer.write(this.nUsers + ","
                                + this.nItems + ","
                                + this.nFactor + ","
                                + this.lambda + ","
                                + this.alpha);
                    } else if (conf.equals("r.csv")) {
                        writer.write(this.nUsers + "," + this.nItems);
                        writer.newLine();
                        for (double[] vec : this.r.toDoubleMatrix()) {
                            StringBuilder s = new StringBuilder().append(vec[0]);
                            for (int j = 1; j < vec.length; ++j)
                                s.append(",").append(vec[j]);
                            writer.write(s.toString());
                            writer.newLine();
                        }
                    } else if (conf.equals("x.csv")) {
                        writer.write(this.nUsers + "," + this.nFactor);
                        writer.newLine();
                        for (double[] vec : this.x.toDoubleMatrix()) {
                            StringBuilder s = new StringBuilder().append(vec[0]);
                            for (int j = 1; j < vec.length; ++j)
                                s.append(",").append(vec[j]);
                            writer.write(s.toString());
                            writer.newLine();
                        }
                    } else if (conf.equals("y.csv")) {
                        writer.write(this.nItems + "," + this.nFactor);
                        writer.newLine();
                        for (double[] vec : this.y.toDoubleMatrix()) {
                            StringBuilder s = new StringBuilder().append(vec[0]);
                            for (int j = 1; j < vec.length; ++j)
                                s.append(",").append(vec[j]);
                            writer.write(s.toString());
                            writer.newLine();
                        }
                    } else if (conf.equals("c.csv")) {
                        writer.write(this.nUsers + "," + this.nItems);
                        writer.newLine();
                        for (double[] vec : this.c.toDoubleMatrix()) {
                            StringBuilder s = new StringBuilder().append(vec[0]);
                            for (int j = 1; j < vec.length; ++j)
                                s.append(",").append(vec[j]);
                            writer.write(s.toString());
                            writer.newLine();
                        }
                    } else if (conf.equals("p.csv")) {
                        writer.write(this.nUsers + "," + this.nItems);
                        writer.newLine();
                        for (double[] vec : this.p.toDoubleMatrix()) {
                            StringBuilder s = new StringBuilder().append(vec[0]);
                            for (int j = 1; j < vec.length; ++j)
                                s.append(",").append(vec[j]);
                            writer.write(s.toString());
                            writer.newLine();
                        }
                    }
                    writer.flush();
                    writer.close();
                }

                byte[] buf = new byte[1024];
                ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(fileName));
                for (String conf : confs) {
                    FileInputStream inputStream = new FileInputStream(conf);
                    zip.putNextEntry(new ZipEntry(conf));

                    int length;
                    while ((length = inputStream.read(buf)) > 0)
                        zip.write(buf, 0, length);

                    zip.closeEntry();
                    inputStream.close();
                    new File(conf).delete();
                }
                zip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 모델 로드
    @Override
    public void loadModel(String modelPath) {
        if (modelPath.contains(".zip")) {
            try {
                ZipInputStream zip = new ZipInputStream(new FileInputStream(modelPath));
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    String fileName = entry.getName(), line;
                    FileOutputStream outputStream = new FileOutputStream(fileName);
                    int length;
                    while ((length = zip.read()) != -1)
                        outputStream.write(length);
                    zip.closeEntry();
                    outputStream.flush();
                    outputStream.close();
                    File file = new File(fileName);
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String[] conf, shape;
                    if (fileName.equals("conf.csv")) {
                        conf = reader.readLine().split(",");
                        nUsers = Integer.parseInt(conf[0]);
                        nItems = Integer.parseInt(conf[1]);
                        nFactor = Integer.parseInt(conf[2]);
                        lambda = Double.parseDouble(conf[3]);
                        alpha = Double.parseDouble(conf[4]);
                    } else if (fileName.equals("r.csv")) {
                        shape = reader.readLine().split(",");
                        nUsers = Integer.parseInt(shape[0]);
                        nItems = Integer.parseInt(shape[1]);
                        r = Nd4j.create(nUsers, nItems);

                        for (int i = 0; i < r.rows() && (line = reader.readLine()) != null; ++i) {
                            String[] elements = line.split(",");
                            for (int j = 0; j < r.columns(); ++j)
                                r.put(i, j, Double.parseDouble(elements[j]));
                        }
                    } else if (fileName.equals("x.csv")) {
                        shape = reader.readLine().split(",");
                        nUsers = Integer.parseInt(shape[0]);
                        nFactor = Integer.parseInt(shape[1]);
                        x = Nd4j.create(nUsers, nFactor);

                        for (int i = 0; i < x.rows() && (line = reader.readLine()) != null; ++i) {
                            String[] elements = line.split(",");
                            for (int j = 0; j < x.columns(); ++j)
                                x.put(i, j, Double.parseDouble(elements[j]));
                        }
                    } else if (fileName.equals("y.csv")) {
                        shape = reader.readLine().split(",");
                        nItems = Integer.parseInt(shape[0]);
                        nFactor = Integer.parseInt(shape[1]);
                        y = Nd4j.create(nItems, nFactor);

                        for (int i = 0; i < y.rows() && (line = reader.readLine()) != null; ++i) {
                            String[] elements = line.split(",");
                            for (int j = 0; j < y.columns(); ++j)
                                y.put(i, j, Double.parseDouble(elements[j]));
                        }
                    } else if (fileName.equals("c.csv")) {
                        shape = reader.readLine().split(",");
                        nUsers = Integer.parseInt(shape[0]);
                        nItems = Integer.parseInt(shape[1]);
                        c = Nd4j.create(nUsers, nItems);

                        for (int i = 0; i < c.rows() && (line = reader.readLine()) != null; ++i) {
                            String[] elements = line.split(",");
                            for (int j = 0; j < c.columns(); ++j)
                                c.put(i, j, Double.parseDouble(elements[j]));
                        }
                    } else if (fileName.equals("p.csv")) {
                        shape = reader.readLine().split(",");
                        nUsers = Integer.parseInt(shape[0]);
                        nItems = Integer.parseInt(shape[1]);
                        p = Nd4j.create(nUsers, nItems);

                        for (int i = 0; i < p.rows() && (line = reader.readLine()) != null; ++i) {
                            String[] elements = line.split(",");
                            for (int j = 0; j < p.columns(); ++j)
                                p.put(i, j, Double.parseDouble(elements[j]));
                        }
                    }
                    reader.close();
                    file.delete();
                }
                zip.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
