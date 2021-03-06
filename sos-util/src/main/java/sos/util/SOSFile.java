package sos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/** @author Ghassan Beydoun */
@Deprecated
public class SOSFile {

    public static SOSLogger logger = null;
    static final int BUFF_SIZE = 100000;
    static final byte[] buffer = new byte[BUFF_SIZE];

    public static boolean canReadFile(final File file, final SOSStandardLogger sosLogger) throws Exception {
        boolean rep = true;
        int repeatCount = 5;
        while (rep && repeatCount != -1) {
            try {
                if (!file.exists()) {
                    rep = false;
                    throw new Exception("..file does not exist: " + file.getAbsolutePath());
                }
                java.io.FileOutputStream f = new java.io.FileOutputStream(file, true);
                f.close();
                rep = false;
            } catch (Exception e) {
                repeatCount--;
                if (repeatCount == -1) {
                    throw new Exception("..file cannot be accessed: " + file.getAbsolutePath() + ": " + e);
                }
                if (sosLogger != null) {
                    sosLogger.debug1("trial " + (5 - repeatCount) + " of 5 to access order file: " + file.getAbsolutePath());
                }
                Thread.sleep(1000);
            }
        }
        return !rep;
    }

    public static boolean copyFile(final File source, final File dest) throws Exception {
        return copyFile(source, dest, false);
    }

    public static boolean copyFile(final File source, final File dest, final boolean append) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        try {
            if (logger != null) {
                logger.debug5("Copying file " + source.getAbsolutePath() + " with buffer of " + BUFF_SIZE + " bytes");
            }
            in = new FileInputStream(source);
            out = new FileOutputStream(dest, append);
            while (true) {
                synchronized (buffer) {
                    int amountRead = in.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(buffer, 0, amountRead);
                }
            }
            if (logger != null) {
                logger.debug5("File " + source.getAbsolutePath() + " with buffer of " + BUFF_SIZE + " bytes");
            }
            return true;
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static boolean copyFile(final String source, final String dest) throws Exception {
        return copyFile(new File(source), new File(dest), false);
    }

    public static int deleteFile(final File file) {
        int count = 0;
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                count += deleteFile(file2);
            }
        }
        boolean flgFileDeleted = file.delete();
        if (flgFileDeleted) {
            count++;
        } else {
            throw new RuntimeException(String.format("File '%1$s' not deleted due to an error.", file.getAbsolutePath()));
        }
        return count;
    }

    public static Vector<File> getFilelist(final String folder, final String regexp, final int flag) throws Exception {
        Vector<File> filelist = new Vector<File>();
        if (folder == null || folder.isEmpty()) {
            throw new FileNotFoundException("empty directory not allowed!!");
        }
        File f = new File(folder);
        if (!f.exists()) {
            throw new FileNotFoundException("directory does not exist: " + folder);
        }
        filelist = new Vector<File>();
        File[] files = f.listFiles(new SOSFilelistFilter(regexp, flag));
        for (File file : files) {
            if (file.isDirectory()) {
                //
            } else if (file.isFile()) {
                filelist.add(file);
            }
        }
        return filelist;
    }

    public static Vector<File> getFilelist(final String folder, final String regexp, final int flag, final boolean withSubFolder) throws Exception {
        Vector<File> filelist = new Vector<File>();
        File file = null;
        File[] subDir = null;
        file = new File(folder);
        subDir = file.listFiles();
        filelist.addAll(getFilelist(folder, regexp, flag));
        if (withSubFolder) {
            for (File element : subDir) {
                if (element.isDirectory()) {
                    filelist.addAll(getFilelist(element.getPath(), regexp, flag, true));
                }
            }
        }
        return filelist;
    }

    public static Vector<File> getFolderlist(final String folder, final String regexp, final int flag) throws Exception {
        Vector<File> filelist = new Vector<File>();
        if (folder == null || folder.isEmpty()) {
            throw new FileNotFoundException("empty directory not allowed!!");
        }
        File f = new File(folder);
        if (!f.exists()) {
            throw new FileNotFoundException("directory does not exist: " + folder);
        }
        filelist = new Vector<File>();
        File[] files = f.listFiles(new SOSFilelistFilter(regexp, flag));
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!".".equals(files[i].getName()) && !"..".equals(files[i].getName())) {
                    filelist.add(files[i]);
                }
            }
        }
        return filelist;
    }

    public static Vector<File> getFolderlist(final String folder, final String regexp, final int flag, final boolean withSubFolder) throws Exception {
        Vector<File> filelist = new Vector<File>();
        File file = null;
        File[] subDir = null;
        file = new File(folder);
        subDir = file.listFiles();
        filelist.addAll(getFolderlist(folder, regexp, flag));
        if (withSubFolder) {
            for (File element : subDir) {
                if (element.isDirectory()) {
                    filelist.addAll(getFolderlist(element.getPath(), regexp, flag, true));
                }
            }
        }
        return filelist;
    }

    public static SOSLogger getLogger() {
        return logger;
    }

    public static String readFile(final File source) throws Exception {
        int size = (int) source.length();
        int bytesRead = 0;
        byte[] data = null;
        FileInputStream in = null;
        String retsVal = "";
        try {
            in = new FileInputStream(source);
            data = new byte[size];
            while (bytesRead < size) {
                bytesRead += in.read(data, bytesRead, size - bytesRead);
            }
            retsVal = retsVal + new String(data);
            return retsVal;
        } catch (Exception e) {
            throw new Exception("\n -> ..error in " + SOSClassUtil.getMethodName() + " " + e.getMessage(), e);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public static String readFileUnicode(final File source) throws Exception {
        FileInputStream in = null;
        SOSUnicodeInputStream uniIn = null;
        try {
            in = new FileInputStream(source);
            uniIn = new SOSUnicodeInputStream(in, null);
            uniIn.getEncoding();
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = uniIn.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            return out.toString();
        } catch (Exception e) {
            throw new Exception("\n -> ..error in " + SOSClassUtil.getMethodName() + " " + e.getMessage(), e);
        } finally {
            if (uniIn != null) {
                uniIn.close();
            }
        }
    }

    public static boolean renameTo(final File source, final File dest, final SOSStandardLogger sosLogger) throws Exception {
        boolean retVal = true;
        try {
            if (sosLogger != null) {
                sosLogger.debug1("..trying to move File " + source.getAbsolutePath() + " to " + dest.getAbsolutePath());
            }
            copyFile(source, dest);
            if (!source.delete()) {
                retVal = false;
            }
            return retVal;
        } catch (Exception e) {
            throw new Exception("\n ..error in " + SOSClassUtil.getMethodName() + " " + e.getMessage(), e);
        }
    }

    public static boolean renameTo(final String source, final String dest, final SOSStandardLogger sosLogger) throws Exception {
        return renameTo(new File(source), new File(dest), sosLogger);
    }

    public static void setLogger(final SOSLogger logger) {
        SOSFile.logger = logger;
    }

    public static String subFileMask(final String filespec, final String substitute) throws IOException {
        if (filespec == null) {
            throw new IOException("file specification is null.");
        }
        String retVal = new String();
        int ipos1 = filespec.indexOf("[");
        int ipos2 = filespec.lastIndexOf("]");
        if (ipos1 == -1 || ipos2 == -1) {
            return filespec;
        }
        String midStr = new String();
        String startStr = new String();
        String endStr = new String();
        if (ipos1 != 0) {
            startStr = filespec.substring(0, ipos1);
        }
        midStr = substitute.concat(filespec.substring(ipos2 + 1, filespec.length()));
        retVal = startStr.concat(midStr).concat(endStr);
        return retVal;
    }

}