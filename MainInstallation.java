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
        //Создание директроий
        File dirGames = new File(DIR_GAMES);
        File dirSrc = new File(dirGames, DIR_SRC);
        File dirRes = new File(dirGames, DIR_RES);
        File dirSaveGames = new File(dirGames, DIR_SAVE);
        File dirTemp = new File(dirGames, DIR_TEMP);
        File dirMain = new File(dirSrc, DIR_MAIN);
        File dirTest = new File(dirSrc, DIR_TEST);
        File dirDraw = new File(dirRes, DIR_DRAW);
        File dirVectors = new File(dirRes, DIR_VECTORS);
        File dirIcons = new File(dirRes, DIR_ICONS);
        //Создание файлов
        File fileMain = new File(dirMain, FILE_MAIN);
        File fileUtils = new File(dirMain, FILE_UTILS);
        File fileTemp = new File(dirTemp, FILE_TEMP);
        File fileSave1 = new File(dirSaveGames, FILE_SAVE1);
        File fileSave2 = new File(dirSaveGames, FILE_SAVE2);
        File fileSave3 = new File(dirSaveGames, FILE_SAVE3);
        File fileZip = new File(dirSaveGames, FILE_ZIP);
        //Создание игровых событий
        GameProgress gameProgress1 = new GameProgress(85, 340, 3, 45.5);
        GameProgress gameProgress2 = new GameProgress(80, 270, 5, 40.2);
        GameProgress gameProgress3 = new GameProgress(65, 110, 2, 25.1);
        List<File> listFileSave = new ArrayList<File>();

        StringBuilder log = new StringBuilder();
        if (dirGames.mkdir()) addLog(log, dirGames);
        if (dirSrc.mkdir()) addLog(log, dirSrc);
        if (dirRes.mkdir()) addLog(log, dirRes);
        if (dirSaveGames.mkdir()) addLog(log, dirSaveGames);
        if (dirTemp.mkdir()) addLog(log, dirTemp);
        if (dirMain.mkdir()) addLog(log, dirMain);
        if (dirTest.mkdir()) addLog(log, dirTest);
        if (dirDraw.mkdir()) addLog(log, dirDraw);
        if (dirVectors.mkdir()) addLog(log, dirVectors);
        if (dirIcons.mkdir()) addLog(log, dirIcons);


        try (FileWriter fileWriter = new FileWriter(fileTemp, false)) {
            if (fileMain.createNewFile()) addLog(log, fileMain);
            if (fileUtils.createNewFile()) addLog(log, fileUtils);
            if (fileTemp.exists()) addLog(log, fileTemp);
            if (fileSave1.createNewFile()) saveGame(fileSave1, gameProgress1);
            if (fileSave2.createNewFile()) saveGame(fileSave2, gameProgress2);
            if (fileSave3.createNewFile()) saveGame(fileSave3, gameProgress3);
            listFileSave.add(fileSave1);
            listFileSave.add(fileSave2);
            listFileSave.add(fileSave3);
            if (fileZip.createNewFile()) zipFiles(fileZip, listFileSave);
            for (File file : listFileSave) file.delete();
            openZip(fileZip, dirSaveGames);
            openProgress(listFileSave);
            String logFile = log.toString();
            fileWriter.write(logFile);
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static StringBuilder addLog(StringBuilder log, File file) {
        if (file.isDirectory()) log.append("Создана директория - ");
        if (file.isFile()) log.append("Создан файл - ");
        log.append(file.getName());
        log.append(", по адресу: ");
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