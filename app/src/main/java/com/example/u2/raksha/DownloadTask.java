package com.example.u2.raksha;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.widget.Toast;

import org.apache.commons.compress.archivers.ArchiveException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by u2 on 11/8/2015.
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    public DownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            File folder = new File(Environment.getExternalStorageDirectory()+"/Android/media/com.Raksha");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                // Do something on success
            } else {
                // Do something else on failure
            }
            input = connection.getInputStream();
            output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/Android/media/com.Raksha/cmap.zip");

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        if (result != null)
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();

        Unzip zip = new Unzip();
        try {
            zip.unzip(Environment.getExternalStorageDirectory()+"/Android/media/com.Raksha/cmap.zip");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        } catch (ArchiveException e) {
            e.printStackTrace();
            Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
        }
    }


/*    protected void unzipTask(File zipFile, File targetDirectory) throws IOException {

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                *//*Toast.makeText(this.getApplicationContext(),"DONE ZIPPING",Toast.LENGTH_SHORT ).show();*//*
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory()){
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            *//* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            *//*
            }
        } finally {
            zis.close();
        }
    }*/
}