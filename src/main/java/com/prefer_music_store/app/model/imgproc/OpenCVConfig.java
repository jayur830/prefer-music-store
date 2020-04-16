package com.prefer_music_store.app.model.imgproc;

public class OpenCVConfig {
    private static boolean loadedLibrary = false;

    public static void loadNativeLibrary() {
        if (!loadedLibrary) {
            System.loadLibrary("opencv_java410");
            loadedLibrary = true;
        } else {
            String[] libPaths = System.getProperty("java.library.path").split(";");
            boolean find = false;
            for (String path : libPaths) {
                System.out.println(path);
                if (path.equals("C:\\opencv\\build\\java\\x64")) {
                    find = true;
                    break;
                }
            }
            if (!find) System.loadLibrary("opencv_java410");
        }
    }
}
