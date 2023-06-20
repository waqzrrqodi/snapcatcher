package com.snapcatcher.snapcatcher;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.os.Environment;

import com.scottyab.rootbeer.RootBeer;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button rootButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootButton = findViewById(R.id.rootButton);
        rootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeRootCommand();
            }
        });
    }

    private void executeRootCommand() {
        RootBeer rootBeer = new RootBeer(MainActivity.this);
        if (rootBeer.isRooted()) {
            // Device is rooted, execute shell commands with root access

            String sourceFolderPath = "/data/data/com.snapchat.android/files/file_manager/chat_snap/";
            String destinationFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Snaps/Auto/";

            try {
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());

                // Remount /data partition with read-write permissions
                String remountCommand = "mount -o rw,remount /data\n";
                outputStream.writeBytes(remountCommand);
                outputStream.flush();

                // Change directory to source folder
                String cdCommand = "cd " + sourceFolderPath + "\n";
                outputStream.writeBytes(cdCommand);
                outputStream.flush();

                // Execute the rename command for each file
                String renameCommand = "for file in *.0; do mv \"$file\" \"${file%.0}.png\"; done\n";
                outputStream.writeBytes(renameCommand);
                outputStream.flush();

                // Copy the renamed files to the destination folder if they do not already exist
                String copyCommand = "for file in *.png; do if [ ! -f " + destinationFolderPath + "\"$file\" ]; then cp \"$file\" " + destinationFolderPath + "; fi; done\n";
                outputStream.writeBytes(copyCommand);
                outputStream.flush();

                // Exit the shell
                outputStream.writeBytes("exit\n");
                outputStream.flush();

                // Wait for the process to complete
                int exitCode = process.waitFor();
                outputStream.close();

                if (exitCode == 0) {
                    Toast.makeText(MainActivity.this, "Files renamed and copied successfully", Toast.LENGTH_SHORT).show();
                } else {
                    showError("Failed to rename and copy files");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                showError("Error executing command: " + e.getMessage());
            }
        } else {
            // Device is not rooted, handle the situation
            showError("Device is not rooted");
        }
    }



    private void showError(String errorMessage) {
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        // Alternatively, you can update a TextView with the error message
        // textViewError.setText(errorMessage);
    }



}
