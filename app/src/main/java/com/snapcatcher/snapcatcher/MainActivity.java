import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;

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
        String sourceFolderPath = "/data/data/com.snapchat.android/files/file_manager/chat_snap/";
        String destinationFolderPath = getExternalFilesDir(null) + "/Snaps/Auto/";

        File sourceFolder = new File(sourceFolderPath);
        File destinationFolder = new File(destinationFolderPath);

        if (!sourceFolder.exists()) {
            showError("Source folder does not exist");
            return;
        }

        if (!destinationFolder.exists()) {
            showError("Destination folder does not exist");
            return;
        }

        String command = "cd " + sourceFolderPath + " && for file in *.0; do mv \"$file\" \"${file%.0}.png\"; done";

        Command rootCommand = new Command(0, command) {
            @Override
            public void commandCompleted(int id, int exitCode) {
                if (exitCode == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Files renamed successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    showError("Failed to rename files");
                }
            }
        };

        try {
            Shell.startRootShell().add(rootCommand);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error executing command: " + e.getMessage());
        }
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}