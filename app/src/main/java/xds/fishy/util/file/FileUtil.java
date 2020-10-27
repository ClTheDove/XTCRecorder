package xds.fishy.util.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileUtil {
    public static String getValidFilename(String filename) {
        Pattern pattern = Pattern.compile("/");
        Matcher matcher = pattern.matcher(filename);
        return matcher.replaceAll("");
    }

    public static String getFilenameByPath(String path) {
        return new File(path).getName();
    }

    public static String getFilenamePrefix(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getFileNoPrefixName(String fileName) {
        String prefix = getFilenamePrefix(fileName);
        return fileName.substring(0, fileName.length() - prefix.length());
    }

    public static String getFilenamePrefixByPath(String path) {
        return getFilenamePrefix(getFilenameByPath(path));
    }

    public static String getFileNoPrefixNameByPath(String path) {
        return getFileNoPrefixName(getFilenameByPath(path));
    }

    public static boolean isFileExistsByPath(String path) {
        return new File(path).exists();
    }

    public static boolean isFileByPath(String path) {
        if (!isFileExistsByPath(path)) return false;
        return new File(path).isFile();
    }

    public static boolean isDirectoryByPath(String path) {
        if (!isFileExistsByPath(path)) return false;
        return new File(path).isDirectory();
    }

    public static ArrayList<String> getFilenameListArrayListByPath(String path, String[] extFilter, boolean allowDirectory) {
        File[] files = getFileArrayByPath(path, extFilter, allowDirectory);
        if (files == null) return null;
        return getFilenameArrayList(files);
    }

    public static ArrayList<String> getFilenameListArrayListSortedByModifyTimeByPath(String path, String[] extFilter, boolean allowDirectory) {
        File[] files = getFileArraySortedByModifyTimeByPath(path, extFilter, allowDirectory);
        if (files == null) return null;
        return getFilenameArrayList(files);
    }

    private static ArrayList<String> getFilenameArrayList(File[] files) {
        ArrayList<String> array = new ArrayList<>();
        for (File file : files) {
            array.add(getFileNoPrefixName(file.getName()));
        }
        return array;
    }

    private static File[] getFileArraySortedByModifyTimeByPath(String path, final String[] extFilter, final boolean allowDirectory) {
        File[] files = getFileArrayByPath(path, extFilter, allowDirectory);
        if (files == null) return null;
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File file, File file2) {
                return Long.compare(file2.lastModified(), file.lastModified());
            }
        });
        return files;
    }

    private static File[] getFileArrayByPath(String path, final String[] extFilter, final boolean allowDirectory) {
        return new File(path).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                boolean allowed = allowDirectory || file.isFile();
                if (extFilter.length == 0) return allowed;
                String prefix = getFilenamePrefix(file.getName());
                return allowed && Arrays.asList(extFilter).contains(prefix);
            }
        });
    }

    public static boolean mkdirByPath(String path) {
        return new File(path).mkdir();
    }

    public static boolean mkdirsByPath(String path) {
        return new File(path).mkdirs();
    }

    public static boolean deleteFileByPath(String path) {
        File file = new File(path);
        if (!file.exists()) return false;
        if (file.isFile()) {
            return file.delete();
        } else if (file.isDirectory()) {
            return deleteDirectoryByPath(path);
        }
        return false;
    }

    public static boolean deleteDirectoryByPath(String path) {
        File parent = new File(path);
        if (!parent.exists() || !parent.isDirectory()) return false;
        File[] parentFiles = parent.listFiles();
        if (parentFiles != null) {
            for (File child : parentFiles) {
                if (child.isFile()) {
                    if (!child.delete()) return false;
                } else if (child.isDirectory()) {
                    if (!deleteDirectoryByPath(child.getAbsolutePath())) return false;
                }
            }
        }
        return parent.delete();
    }

    public static boolean renameFileByPath(String path, String path2) {
        File file = new File(path);
        File file2 = new File(path2);
        if ((!file.exists()) || file2.exists()) return false;
        return file.renameTo(file2);
    }
}
