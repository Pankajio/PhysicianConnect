# README
# [Presentation](https://youtu.be/B3RZYiaiTNo?si=FPFaSQV39jDU7AYv)
# Documentation 

- [Vision](docs/VISION.md)
- [Project Architecture](docs/ARCHITECTURE.md)
- [Retrospective](docs/RESTROSPECTIVE.md)
- [Credits](docs/CREDIT.md)
- [Our Website](https://nabxz.github.io/PhysicianConnectWebsite/)


# PhysicianConnect

PhysicianConnect is a comprehensive health care coordination platform designed to facilitate medical practice operations for both physicians and receptionists. The system facilitates efficient appointment management, patient care coordination, and administrative tasks through a simple interface. Physicians can manage their schedules, review patient histories, prescribe medications, and handle referrals, while receptionists can coordinate appointments, manage billing, and handle patient communications. The platform features real-time notifications, integrated billing systems, and robust profile management for both user types. Built with a 3-tier architecture that separates the presentation (UI), business logic (services), and persistence (database access) layers, PhysicianConnect ensures modularity, scalability, and testability while maintaining a smooth user experience.

## Features

- **Appointment Slot Management:** Add, update, or remove available time slots for patient bookings.
- **View Patient Medical History:** Access past visit summaries, detailed examination records, and patient health timelines.
- **Prescribe Medication:** Search a dynamic formulary, select safe medications, add dosage details, and include prescription notes.
- **Navigation Dashboard:** Tab-based physician interface offering access to all workflows from a centralized location.
- **Real-time Notifications:** Receive instant notifications for appointment updates, new messages, and billing events.
- **Billing & Invoicing System:** Create and manage invoices, track payments, and handle insurance adjustments.
- **Profile Management:** Customize profile information including specialty, office hours, contact details, and notification preferences.
- **Messaging System:** Internal communication platform for physicians and receptionists.
- **Referral Management:** Create and track patient referrals to specialists with detailed notes and history.
- **Revenue Tracking:** Monitor practice revenue with detailed summaries and payment tracking.
- **Insurance Integration:** Support for multiple insurance types with automatic adjustment calculations.

## Dependencies & Versions

- **Java:** JDK 21.0.6 (or a compatible version)
- **Gradle:** Gradle 7.6 (or similar version)
- **SQLite JDBC:** org.xerial:sqlite-jdbc:3.36.0.3  
  *(Provides the SQLite driver for embedded database support)*
- **JUnit 5:** For unit and integration testing
- **Java Swing:** For GUI components and user interface
- **Java Time API:** For handling dates, times, and scheduling
- **Java ImageIO:** For profile photo management and image processing

## Setting Up & Running

The application uses a composition root (typically in the `main()` method) to wire up the correct persistence implementation (stub, test, or production). In production mode, the app uses an SQLite database.

### Running the Application

# Clone the Repository
If you haven't already cloned the repository, do so using:
```bash
git clone https://code.cs.umanitoba.ca/comp3350-summer2025/a01-g08-todo-everything.git
```

# Navigate to the Application Directory
From the root of the cloned project, navigate to the app folder:
```bash
cd a01-g08-todo-everything/physiciansconnect/app
```
Use forward slashes (/) for Unix-based systems (macOS, Linux) and backslashes (\) for Windows in Command Prompt or PowerShell.

# Run the Application
Use Gradle to start the application:
```bash
./gradlew run
```
On Windows (PowerShell or CMD), if you encounter permission issues with ./gradlew, try:
```bash
./gradlew.bat run
```

## Running with the .jar File

# Clone the Repository
If you haven't already cloned the repository, do so using:
```bash
git clone https://code.cs.umanitoba.ca/comp3350-summer2025/a01-g08-todo-everything.git
```

# Navigate to the Application Directory
From the root of the cloned project, navigate to the app folder:
```bash
cd a01-g08-todo-everything/physiciansconnect/app
```
Use forward slashes (/) for Unix-based systems (macOS, Linux) and backslashes (\) for Windows in Command Prompt or PowerShell.

# Run the Application
To start the application using jar run:
```bash
java -jar build/libs/physiciansconnect-2.0.jar
```

## Database Management

The SQLite database is stored as a single file (for example, `prod.db` for production). The application automatically creates the necessary tables and seeds initial data if needed.

### Wiping the Database

To wipe the database from the command line:

1. **Delete the Database File:**

   On macOS/Linux:
   ```bash
   rm path/to/prod.db
   ```

   On Windows:
   ```cmd
   del path\to\prod.db
   ```

2. **Using the SQLite Command-Line Tool:**

   To delete all records from the `notes` table:
   ```bash
   sqlite3 path/to/prod.db "DELETE FROM notes;"
   ```

   To drop the entire `notes` table:
   ```bash
   sqlite3 path/to/prod.db "DROP TABLE IF EXISTS notes;"
   ```

After wiping the database, the application will recreate the necessary tables on startup.

## 3-Tier Architecture

PhysicianConnect is designed following a 3-tier architecture, which separates the application into three distinct layers:

### 1. Presentation Layer (UI)

- **Purpose:**  
  This layer is responsible for all user interactions. In PhysicianConnect, the presentation layer is implemented using Swing (or can be swapped out for another UI framework if needed). It displays notes, handles user input (like clicking buttons or entering text), and shows the results.

- **Key Characteristics:**  
  - Decoupled from the business logic and persistence layers.
  - Only communicates with the business logic (NoteManager) to perform actions.
  - Responsible for displaying feedback (errors, confirmations) to the user.

### 2. Business Logic Layer

- **Purpose:**  
  The business logic layer handles the core operations of our application. It enforces business rules, performs validations, and mediates between the UI and the data layer. In PhysicianConnect, this is primarily handled by the manager classes.

- **Key Characteristics:**  
  - Acts as an intermediary between the presentation layer and persistence layer.
  - Contains the CRUD logic for appointments, referrals and presciptions.
  - Delegates data storage and retrieval tasks to the persistence layer.
  - Remains independent of how data is actually stored.

### 3. Persistence Layer (Data Access)

- **Purpose:**  
  The persistence layer is responsible for all interactions with the data storage. In PhysicianConnect, you have multiple implementations (a stub for testing, and a production implementation using SQLite). This layer encapsulates all the SQL queries and data manipulation logic.

- **Key Characteristics:**  
  - Implements a common interface, allowing the business logic to remain unaware of the underlying database.
  - Manages the creation of tables, seeding of data, and CRUD operations.
  - Can be swapped out easily (for example, from SQLite to another database) without impacting the rest of the application.

### Benefits of 3-Tier Architecture

- **Modularity:**  
  Each layer can be developed, tested, and maintained independently.

- **Flexibility:**  
  Changes to one layer (for example, updating the UI or changing the database engine) have minimal impact on other layers.

- **Scalability & Maintainability:**  
  Clear separation of concerns makes the application easier to extend and troubleshoot over time.

### Image Licensing Notice
All images used in the physiciansconnect/app/src/main/java/physicianconnect/src directory are sourced from publicly available stock image libraries or licensed third-party providers. These images are used for illustrative and design purposes only and are not owned by the development team. Proper licensing and usage rights should be verified before using these images in production or commercial deployments.

### Profile Photo Handling
**PhysicianConnect** supports profile photos for both **physicians** and **receptionists**, which are displayed in areas like the dashboard and profile screens. Given this is a desktop-based project, photo management is handled entirely through **local file storage**.

How It Works (In This Project)

File Naming:
Profile photos are saved using a **prefix and user ID** format:
- **Physicians**: `p_<id>.png`  
- **Receptionists**: `r_<id>.png`

Example:
- Physician with ID `123` → `p_123.png`
- Receptionist with ID `456` → `r_456.png`

All photos are stored in: src/main/resources/profile_photos/

Saving
- When a user uploads a new photo, it is **resized** to fit a maximum of **200×200 pixels**.
- The resized image is saved to the `profile_photos` folder with the appropriate filename (e.g., `p_123.png`).

Loading
- On login or when the profile view is opened, the app checks for the appropriate photo file.
- If the file exists, it is loaded and displayed.
- If not, a **placeholder image** (gray box with "No Photo" text) is shown instead.

Access Control
- Since this is a **standalone desktop app**, images are stored locally.
- No authentication or access control is applied to file storage — it assumes trusted local use.

In a Real-World Web-Based Application
If this system were deployed on a real server:

- Photos would be stored on a secure server or cloud storage, not in local folders.
- Authentication and file access control would ensure that users can only access their own images.
- Images would likely be served over HTTPS and may be protected with access tokens or session-based permissions.
- Images would be uploaded via HTTP endpoints and stored with unique paths or database references, not raw filenames.

So why did we use Local Storage?
- Because this application is a desktop-based project designed for offline use and classroom demonstration, we opted for: 
- Simple local folder storage to reduce dependency on external servers.
- Readable file paths to aid debugging and testing.
- Easy manual file replacement for testing default or custom photos.

---

## Summary

PhysicianConnect is a complete solution for medical practices that provides physicians and receptionists with key features for modern health care management. Physicians have the ability to schedule appointments, access patient history and track treatment options. Receptionists can accomplish administrative tasks, manage billing functions and coordinate patient needs. Practice efficiency and patient care quality enhance through notification systems, strong billing capabilities, and a user-friendly interface. With a use of a standard 3 tier architecture model and an SQLite backend, PhysicianConnect offers consistency and simplicity for medical practices while being scalable for larger operations.

For questions, testing setup, or to contribute, please refer to the project documentation or contact the development team.


