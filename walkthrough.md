# Facebook CLI to GUI Conversion

I have successfully converted your CLI application to a JavaFX GUI application with a Maven project structure, including a fully styled Home Page.

## Changes Made

1.  **Project Structure**:
    *   Maven project with `pom.xml`.
    *   Source code in `src/main/java/com/facebook`.
    *   Resources in `src/main/resources/com/facebook`.

2.  **JavaFX Views**:
    *   **Login**: `login-view.fxml` with manual login bypass (User: `admin`, Pass: `admin`).
    *   **Signup**: `signup-view.fxml` with validation.
    *   **Home**: `home-view.fxml` with a 3-column layout (Sidebar, Feed, Contacts) matching Facebook's design.

3.  **Styling**:
    *   `styles.css` with Facebook brand colors, hover effects, transitions, and "toast" error messages.
    *   **Refinements**: Dark text for better readability, sidebar wrappers with padding, and hover animations for interactive elements.

4.  **Features**:
    *   **Authentication**: Real integration with `Database` class + Manual Fallback.
    *   **Feed**: Dynamic post creation in `HomeController`.
    *   **Navigation**: Top bar and Sidebar structure ready for expansion.
    *   **Interactivity**: "Friends" sidebar item now opens a Friends List view. "Contacts" list populates dynamically (or with placeholders).

## How to Run

### Using IntelliJ IDEA (Recommended)
1.  **Reload Maven Project**: Right-click on `pom.xml` and select "Maven" > "Reload Project".
2.  **Run**: Open `src/main/java/com/facebook/HelloApplication.java` and click the green Run button.

### Manual Login (Fallback)
If you cannot connect to the database or want to test quickly:
*   **Username**: `admin`
*   **Password**: `admin`

## Next Steps
*   Connect `Database.Load_Feed()` in `HomeController.java` to show real posts.
*   Implement the specific views for "Marketplace", "Groups", etc., by swapping the center content of the `BorderPane`.
