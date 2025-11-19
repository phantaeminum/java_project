# build-and-run.ps1
# Usage: Open PowerShell in this project folder and run: .\build-and-run.ps1
# This script sets example paths (edit them if needed), compiles the project, runs it and captures logs.

# --- Configuration ---
$javafx = "C:\Program Files\javafx-sdk-25.0.1\lib" # change if your JavaFX SDK is elsewhere
$sqliteJar = ".\sqlite-jdbc-3.50.3.0.jar" # CHANGE THIS to your actual sqlite-jdbc jar filename

# --- Helper ---
function Show-Header($text) {
Write-Host "\n=== $text ===\n"
}

Show-Header "Environment"
Write-Host "Java home: " (java -version 2>&1 | Select-Object -First 1)
Write-Host "JavaFX lib: $javafx"
Write-Host "SQLite JAR: $sqliteJar"

# --- Compile ---
Show-Header "Compiling"
# NOTE: The Write-Host command is for display only, so no change is needed there.
$compileCmd = "javac --module-path ""$javafx"" --add-modules javafx.controls,javafx.fxml -cp $sqliteJar *.java model\*.java dao\*.java util\*.java"
Write-Host "Running: $compileCmd"
if (-not (Test-Path "$javafx")) {
	Write-Host "Warning: JavaFX lib path '$javafx' does not exist. Verify JavaFX SDK is installed and path is correct."
}
if (-not (Test-Path "$sqliteJar")) {
	Write-Host "Warning: sqlite-jdbc JAR '$sqliteJar' does not exist. Make sure you set the correct filename in the script."
}

# Run javac and capture both stdout and stderr into build.log using Tee-Object
& javac --module-path "$javafx" --add-modules "javafx.controls,javafx.fxml" -cp "$sqliteJar" *.java model\*.java dao\*.java util\*.java 2>&1 | Tee-Object -FilePath build.log
if ($LASTEXITCODE -ne 0) {
Write-Host "Compilation FAILED. See build.log"
exit $LASTEXITCODE
}
Write-Host "Compilation succeeded. See build.log for details."

# --- Run ---
Show-Header "Running"
# NOTE: The Write-Host command is for display only.
$runCmd = "java --module-path ""$javafx"" --add-modules javafx.controls,javafx.fxml -cp ""$sqliteJar;."" Main"
Write-Host "Running: $runCmd"
$cp = "$sqliteJar;."
if (-not (Test-Path "$sqliteJar")) {
	Write-Host "ERROR: sqlite-jdbc JAR not found at '$sqliteJar'. Aborting run."
	exit 1
}

# Run java and capture both stdout and stderr into run.log
& java --module-path "$javafx" --add-modules "javafx.controls,javafx.fxml" -cp $cp Main 2>&1 | Tee-Object -FilePath run.log
$exit = $LASTEXITCODE
Write-Host "Run finished with exit code $exit. Logs: build.log, run.log"

Show-Header "Tail of run.log"
Get-Content run.log -Tail 80 | ForEach-Object { Write-Host $_ }

Write-Host "If the app fails, please attach or paste the contents of run.log (and build.log if compilation issues)."

# Keep window open when double-clicked
Read-Host -Prompt "Press Enter to close"