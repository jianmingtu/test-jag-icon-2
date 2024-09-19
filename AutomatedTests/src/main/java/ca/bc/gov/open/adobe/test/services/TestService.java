package ca.bc.gov.open.adobe.test.services;

import com.eviware.soapui.tools.SoapUITestCaseRunner;
import java.io.*;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Value("${test.username}")
    private String username;

    @Value("${test.password}")
    private String password;

    @Value("${test.api-host-1}")
    private String apiHost1;

    @Value("${test.api-host-2}")
    private String apiHost2;

    @Value("${test.api-host-MyFiles}")
    private String apiHostMyFiles;

    public TestService() {}

    public void setAuthentication(String filename) throws IOException {
        InputStream template = getClass().getResourceAsStream("/" + filename);
        Scanner scanner = new Scanner(template);

        File project = new File("./" + filename.replace("-template", ""));
        if (project.exists()) {
            project.delete();
        }
        project.createNewFile();
        BufferedWriter writer =
                new BufferedWriter(new FileWriter("./" + filename.replace("-template", "")));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("{AUTHENTICATION_USERNAME}")) {
                line = line.replaceAll("\\{AUTHENTICATION_USERNAME}", username);
            }
            if (line.contains("{AUTHENTICATION_PASSWORD}")) {
                line = line.replaceAll("\\{AUTHENTICATION_PASSWORD}", password);
            }

            if (line.contains("{API_HOST_1}")) {
                line = line.replaceAll("\\{API_HOST_1}", apiHost1);
            }
            if (line.contains("{API_HOST_2}")) {
                line = line.replaceAll("\\{API_HOST_2}", apiHost2);
            }
            if (line.contains("{API_HOST_MyFiles}")) {
                line = line.replaceAll("\\{API_HOST_MyFiles}", apiHostMyFiles);
            }

            writer.append(line + "\n");
        }
        writer.flush();
        writer.close();
        scanner.close();
    }

    private File zipAndReturnErrors() throws IOException {
        File dir = new File(".");
        FileFilter fileFilter = new WildcardFileFilter("*ICON2*-FAILED.txt");
        File[] files = dir.listFiles(fileFilter);
        if (files == null || files.length == 0) {
            return null;
        }
        sanitizeErrorFiles(files);
        FileOutputStream fos = new FileOutputStream("TestErrors.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fOut = new File("TestErrors.zip");

        for (var fileToZip : files) {
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
            fileToZip.delete();
        }
        zipOut.close();
        fos.close();
        return fOut;
    }

    private void sanitizeErrorFiles(File[] files) throws IOException {
        for (File f : files) {
            String fileContent = FileUtils.readFileToString(f);
            fileContent = fileContent.replaceAll(username, "*".repeat(8));
            fileContent = fileContent.replaceAll(password, "*".repeat(8));
            FileUtils.write(f, fileContent);
        }
    }

    public File runAllTests() throws IOException {

        SoapUITestCaseRunner runner = new SoapUITestCaseRunner();
        try {
            runner.setProjectFile("ICON2HSR-soapui-project.xml");
            runner.run();
        } catch (Exception Ignore) {

        }
        try {
            runner.setProjectFile("ICON2Audit-soapui-project.xml");
            runner.run();
        } catch (Exception Ignore) {

        }
        try {
            runner.setProjectFile("ICON2Auth-soapui-project.xml");
            runner.run();
        } catch (Exception Ignore) {

        }
        try {
            runner.setProjectFile("ICON2Message-soapui-project.xml");
            runner.run();
        } catch (Exception Ignore) {

        }
        try {
            runner.setProjectFile("ICON2MyInfo-soapui-project.xml");
            runner.run();
        } catch (Exception Ignore) {

        }
        try {
            runner.setProjectFile("ICON2MyFiles-soapui-project.xml");
            runner.run();
        } catch (Exception Ignore) {

        }
        try {
            runner.setProjectFile("ICON2ERPT-soapui-project.xml");
            runner.run();
        } catch (Exception Ignore) {

        }

        return zipAndReturnErrors();
    }
}
