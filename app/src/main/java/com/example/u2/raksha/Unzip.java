package com.example.u2.raksha;

/**
 * Created by u2 on 11/9/2015.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileOutputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.util.zip.ZipEntry;
        import org.apache.commons.compress.archivers.ArchiveException;
        import org.apache.commons.compress.archivers.ArchiveInputStream;
        import org.apache.commons.compress.archivers.ArchiveStreamFactory;
        import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

public class Unzip {
    private Context context;
    final String OUTPUT_FOLDER = Environment.getExternalStorageDirectory()+"/Android/media/com.Raksha";

    public void unzip(String file) throws FileNotFoundException, IOException, ArchiveException {

        File inputFile = new File(file);

        InputStream is = new FileInputStream(inputFile);
        ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream("zip", is);
        ZipEntry entry = null;
        //Toast.makeText(context.getApplicationContext(),"sdasdas",Toast.LENGTH_SHORT);
        while ((entry = (ZipArchiveEntry) ais.getNextEntry()) != null) {

            if (entry.getName().endsWith("/")) {
                File dir = new File(OUTPUT_FOLDER + File.separator + entry.getName());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                continue;
            }

            File outFile = new File(OUTPUT_FOLDER + File.separator + entry.getName());

            if (outFile.isDirectory()) {
                continue;
            }

            if (outFile.exists()) {
                continue;
            }

            FileOutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = ais.read(buffer)) > 0) {
                out.write(buffer, 0, length);
                out.flush();
            }

        }
    }
}
