Troubleshooting and run instructions for Community Kitchen app

This project uses JavaFX (external SDK) and SQLite (sqlite-jdbc). Common runtime errors come from missing JavaFX modules on the module-path or missing the SQLite JDBC driver on the classpath.

1) Download dependencies
- JavaFX SDK (example): C:\Program Files\javafx-sdk-25.0.1
- sqlite-jdbc (xerial): download the latest JAR from https://github.com/xerial/sqlite-jdbc/releases, e.g. sqlite-jdbc-3.42.0.0.jar, and place it in the project folder (same folder as .java files) or another known location.

2) Compile (PowerShell)
From the project folder (where Main.java is):

# Set paths (example)
$javafx = "C:\Program Files\javafx-sdk-25.0.1\lib"
$sqliteJar = ".\sqlite-jdbc-<version>.jar"  # replace with actual filename, e.g. .\sqlite-jdbc-3.42.0.0.jar

# Compile all .java files and classes in subfolders
javac --module-path "$javafx" --add-modules javafx.controls,javafx.fxml -cp $sqliteJar *.java model\*.java dao\*.java util\*.java

3) Run (PowerShell)

Important: On Windows PowerShell the semicolon (;) is a command separator unless it is inside quotes. When passing a Windows classpath that includes multiple entries (JAR and current directory), wrap the entire -cp value in quotes so PowerShell doesn't split it.

# Correct (quote the classpath value so the semicolon is passed to java)
java --module-path "$javafx" --add-modules javafx.controls,javafx.fxml -cp "$sqliteJar;." Main

# Alternative (use absolute path for current directory instead of dot):
java --module-path "$javafx" --add-modules javafx.controls,javafx.fxml -cp "$sqliteJar;C:\Users\leeon\OneDrive\Desktop\java_project" Main

# Quick checks (print variables in the same PowerShell session before running):
echo $javafx
echo $sqliteJar

Notes:
- Make sure you set the variables and run the java command in the same PowerShell session. If you open a new session, re-set them.
- If you get errors about missing JavaFX modules, make sure the path in $javafx points to the JavaFX SDK lib folder (contains javafx.controls.jar etc.).
- If you get JDBC errors ("No suitable driver"), ensure the sqlite-jdbc JAR filename is correct and that the JAR exists at the path stored in $sqliteJar.

Notes:
- On Windows PowerShell, when using -cp and you want to include current directory, separate entries with a semicolon (";") and wrap the whole -cp argument in quotes if needed.
- If you still see "No suitable driver" or SQLException about driver, ensure the sqlite-jdbc JAR is the same Java version (most are compatible) and included in -cp.
- For convenience you can copy the sqlite-jdbc JAR into the project folder and replace $sqliteJar with the filename.

If you prefer to run from an IDE (IntelliJ/VS Code), add JavaFX as a library (module-path) and add the sqlite-jdbc JAR to the project's classpath.

If errors persist, run the commands in a terminal, capture the full stack trace, and share it here so I can fix specific code issues.