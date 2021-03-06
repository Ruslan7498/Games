import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.*;

class MainInstallation {
    public static final String DIR_GAMES = "C:\\Games";
    public static final String DIR_SRC = "src";
    public static final String DIR_RES = "res";
    public static final String DIR_SAVE = "savegames";
    public static final String DIR_TEMP = "temp";
    public static final String DIR_MAIN = "main";
    public static final String DIR_TEST = "test";
    public static final String DIR_DRAW = "drawables";
    public static final String DIR_VECTORS = "vectors";
    public static final String DIR_ICONS = "icons";
    public static final String FILE_MAIN = "Main.java";
    public static final String FILE_UTILS = "Utils.java";
    public static final String FILE_TEMP = "temp.txt";
    public static final String FILE_SAVE1 = "save1.dat";
    public static final String FILE_SAVE2 = "save2.dat";
    public static final String FILE_SAVE3 = "save3.dat";
    public static final String FILE_ZIP = "save.zip";

    public static void main(String[] args) {
        StringBuilder log = new StringBuilder();   
        //???????? ???????? ??????????
        File dirGames = new File(DIR_GAMES);
        if (dirGames.mkdir()) addLog(log, dirGames);
        //???????? ?????????? ? ?????????? ?????????? ? ???
        File dirSrc = makeDir(dirGames, DIR_SRC);
        addLog(log, dirSrc);
        File dirRes = makeDir(dirGames, DIR_RES);
        addLog(log, dirRes);
        File dirSaveGames = makeDir(dirGames, DIR_SAVE);
        addLog(log, dirSaveGames);
        File dirTemp = makeDir(dirGames, DIR_TEMP);
        addLog(log, dirTemp);
        File dirMain = makeDir(dirSrc, DIR_MAIN);
        addLog(log, dirMain);
        File dirTest = makeDir(dirSrc, DIR_TEST);
        addLog(log, dirTest);
        File dirDraw = makeDir(dirRes, DIR_DRAW);
        addLog(log, dirDraw);
        File dirVectors = makeDir(dirRes, DIR_VECTORS);
        addLog(log, dirVectors);
        File dirIcons = makeDir(dirRes, DIR_ICONS);
        addLog(log, dirIcons);
        //???????? ?????? ? ?????????? ?????????? ? ???
        File fileMain = makeFile(dirMain, FILE_MAIN);
        addLog(log, fileMain);
        File fileUtils = makeFile(dirMain, FILE_UTILS);
        addLog(log, fileUtils);
        //???????? ??????? ???????
        GameProgress gameProgress1 = new GameProgress(85, 340, 3, 45.5);
        GameProgress gameProgress2 = new GameProgress(80, 270, 5, 40.2);
        GameProgress gameProgress3 = new GameProgress(65, 110, 2, 25.1);
        // ???????? ?????? ??????????
        File fileSave1 = makeFile(dirSaveGames, FILE_SAVE1);
        saveGame(fileSave1, gameProgress1);
        File fileSave2 = makeFile(dirSaveGames, FILE_SAVE2);
        saveGame(fileSave2, gameProgress2);
        File fileSave3 = makeFile(dirSaveGames, FILE_SAVE3);
        saveGame(fileSave3, gameProgress3);
        File fileZip = makeFile(dirSaveGames, FILE_ZIP);
        // ???????? ?????? ??????????? ??????
        List<File> listFileSave = new ArrayList<File>();
        listFileSave.add(fileSave1);
        listFileSave.add(fileSave2);
        listFileSave.add(fileSave3);

        File fileTemp = new File(dirTemp, FILE_TEMP);
        try (FileWriter fileWriter = new FileWriter(fileTemp, false)) {
            if (fileTemp.exists()) addLog(log, fileTemp);
            String logFile = log.toString();
            fileWriter.write(logFile);
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        zipFiles(fileZip, listFileSave);
        for (File file : listFileSave) file.delete();
        openZip(fileZip, dirSaveGames);
        openProgress(listFileSave);
    }

    private static File makeDir(File dirPath, String dirName) {
        File dirNew = new File(dirPath, dirName);
	dirNew.mkdir();
        return dirNew;
    }

    private static File makeFile(File filePath, String fileName) {
        File fileNew = new File(filePath, fileName);
        try {
	    fileNew.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return fileNew;
    }

    public static StringBuilder addLog(StringBuilder log, File file) {
        if (file.isDirectory()) log.append("??????? ?????????? - ");
        if (file.isFile()) log.append("?????? ???? - ");
        log.append(file.getName());
        log.append(", ?? ??????: ");
        log.append(file.getPath());
        log.append(" ");
        return log;
    }

    public static void saveGame(File file, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(file, true);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void zipFiles(File zip, List<File> listFileSave) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zip))) {
            for (File save : listFileSave) {
                try (FileInputStream fis = new FileInputStream(save)) {
                    ZipEntry entry = new ZipEntry(save.getName());
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void openZip(File zip, File open) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                File fileOpen = new File(open, name);
                FileOutputStream fout = new FileOutputStream(fileOpen, false);
                for (int i = zin.read(); i != -1; i = zin.read()) fout.write(i);
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void openProgress(List<File> listFileSave) {
        GameProgress gameProgress = null;
        for (File file : listFileSave) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                gameProgress = (GameProgress) ois.readObject();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println(gameProgress);
        }
    }
}