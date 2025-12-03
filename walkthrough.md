# Facebook CLI to GUI Conversion

I have successfully converted your CLI application to a JavaFX GUI application with a Maven project structure.

## Changes Made

1.  **Project Structure**:
    *   Converted to a Maven project (`pom.xml` added).
    *   Moved source code to `src/main/java/com/facebook`.
    *   Added `package com.facebook;` to all Java files.
    *   Created `src/main/resources/com/facebook` for FXML and CSS files.

2.  **JavaFX Setup**:
    *   **Entry Point**: `com.facebook.HelloApplication` is the new main class for the GUI.
    *   **Login Screen**: `login-view.fxml` and `LoginController.java`.
    *   **Signup Screen**: `signup-view.fxml` and `SignupController.java`.
    *   **Styling**: Added `styles.css` with Facebook-like styling.
    *   **Modularity**: Added `module-info.java` for JavaFX compatibility.

3.  **Integration**:
    *   The GUI uses your existing `Database` class for authentication and user creation.
    *   `Main.current` is updated upon successful login.

## How to Run

### Using IntelliJ IDEA (Recommended)
1.  **Reload Maven Project**: Right-click on `pom.xml` and select "Maven" > "Reload Project".
2.  **Run**: Open `src/main/java/com/facebook/HelloApplication.java` and click the green Run button next to the `main` method.

### Using Command Line (if Maven is installed)
```bash
mvn clean javafx:run
```

## Next Steps
*   The Login and Signup screens are fully functional and styled.
*   Upon login, the application currently prints "Login Successful!" to the console. You can now implement the Home Page and other views by creating new FXML files and Controllers, similar to how Login and Signup were done.
