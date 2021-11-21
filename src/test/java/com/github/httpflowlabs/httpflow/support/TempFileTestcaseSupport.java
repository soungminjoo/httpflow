package com.github.httpflowlabs.httpflow.support;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public abstract class TempFileTestcaseSupport {

    public TempFileTestcaseSupport(String fileContents) {
        this(getUserHomeTempFile(), fileContents);
    }

    public TempFileTestcaseSupport(File file, String fileContents) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            IOUtils.write(fileContents.getBytes(), fos);
            if (!file.exists()) {
                throw new FileNotFoundException(file.getName());
            }
            onFileCreated(file);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private static File getUserHomeTempFile() {
        String random = StringUtils.leftPad(String.valueOf((int) (Math.random() * 100000000)), 8, '0');
        return new File(System.getProperty("user.home"), ".tmp" + System.currentTimeMillis() + random);
    }

    public abstract void onFileCreated(File file);

}
