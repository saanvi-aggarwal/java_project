# Event Registration System

A menu-driven Java application for managing events and participant
registrations. This academic project demonstrates object-oriented
programming, collections, linear search, input validation, and file
handling.

> **Course:** Object-Oriented Programming using Java\
> **University:** Chandigarh University\
> **Language:** Java\
> **Interface:** Command-line interface (CLI)

## Overview

The Event Registration System helps an administrator manage small events
such as workshops, seminars, competitions, and training sessions. It
supports event creation and listing, participant registration,
registration search, cancellation, and event-wise reporting.

Events and registrations are stored in `ArrayList` collections while the
application runs. Local text files are used to preserve records between
application sessions.

## Features

-   Add and view events.
-   Register participants for existing events.
-   Validate participant name, email, and phone number.
-   Prevent duplicate confirmed registrations for the same event and
    email address.
-   Enforce event capacity limits.
-   Search for a registration using its registration ID.
-   Cancel a registration without deleting its history.
-   Generate event-wise reports showing confirmed registrations,
    available seats, and occupancy.
-   Load and save records using local text files.

## Registration Workflow

``` text
Start
  |
  v
Load event and registration files
  |
  v
Select "Register Participant"
  |
  v
Find event by ID
  |
  +---- Event not found ----> Reject request
  |
  v
Validate participant details
  |
  v
Check duplicate registration and event capacity
  |
  +---- Invalid / duplicate / full ----> Reject request
  |
  v
Generate registration ID
  |
  v
Add confirmed registration
  |
  v
Save updated records
  |
  v
Return to menu
```

## Technologies and Concepts

  Technology / concept   Purpose
  ---------------------- -----------------------------------------------------
  Java                   Main programming language
  Classes and objects    Model events, participants, and registrations
  Encapsulation          Protect object fields and provide controlled access
  `ArrayList`            Store records dynamically in memory
  Linear search          Find events and registrations by ID
  Loops and conditions   Apply validation and business rules
  File I/O               Read and write persistent records
  Exception handling     Handle file and input errors

## Project Structure

The following is the intended logical structure. Update it if the actual
repository uses different filenames or folders.

``` text
EventRegistrationSystem/
├── src/
│   ├── Main.java
│   ├── Event.java
│   ├── Participant.java
│   ├── Registration.java
│   ├── RegistrationService.java
│   └── FileHandler.java
├── data/
│   ├── events.txt
│   └── registrations.txt
└── README.md
```

### Main Components

-   **`Main.java`** --- Application entry point and menu-driven
    interaction.
-   **`Event.java`** --- Stores event ID, name, date, venue, and
    capacity.
-   **`Participant.java`** --- Stores participant name, email, and phone
    number.
-   **`Registration.java`** --- Stores registration ID, participant,
    event ID, and status.
-   **`RegistrationService.java`** --- Handles event lookup,
    registration validation, ID generation, and cancellation.
-   **`FileHandler.java`** --- Loads and saves application records.

## Requirements

-   Java Development Kit (JDK) 11 or later.
-   A terminal or command prompt, or a Java IDE such as IntelliJ IDEA,
    Eclipse, or Visual Studio Code.

JDK 11 is the minimum suggested version because the described
implementation uses `String.isBlank()`.

## Getting Started

### 1. Clone the repository

``` bash
git clone <YOUR_REPOSITORY_URL>
cd EventRegistrationSystem
```

Replace `<YOUR_REPOSITORY_URL>` with your actual Git repository URL.

### 2. Compile the source files

If all Java files are in `src/` and do not declare a package:

``` bash
javac -d out src/*.java
```

### 3. Run the application

``` bash
java -cp out Main
```

If the project declares Java packages or uses a different folder
structure, adjust the commands accordingly. Alternatively, open the
project in your Java IDE and run the class containing
`public static void main(String[] args)`.

### 4. Use the application menu

The menu described for the project is:

``` text
========== EVENT REGISTRATION SYSTEM ==========

1. Add Event
2. View Events
3. Register Participant
4. Search Registration
5. Cancel Registration
6. Generate Event Report
0. Exit
```

Choose an option and enter the requested details. Exact prompts may vary
with the final implementation.

## Data Storage

The application uses local text files:

-   `data/events.txt` --- Event records.
-   `data/registrations.txt` --- Registration and participant records.

The event record format is:

``` text
eventId|name|date|venue|capacity
```

The intended registration record format is:

``` text
registrationId|participantName|email|phone|eventId|status
```

The application should create the data directory when needed. The pipe
character (`|`) is the field separator, so values containing it must be
rejected or escaped by the implementation. Keep backups of important
data files.

## Algorithms and Complexity

Let `E` be the number of events and `R` the number of registration
records.

  ------------------------------------------------------------------------
  Operation                          Time complexity Reason
  --------------------- ---------------------------- ---------------------
  Find event by ID                            `O(E)` Sequential scan of
                                                     events

  Display all events                          `O(E)` Visits every event

  Find registration by                        `O(R)` Sequential scan of
  ID                                                 registrations

  Validate and add                        `O(E + R)` Event lookup plus
  registration                                       registration scan

  Generate registration                       `O(R)` Inspects existing IDs
  ID                                                 

  Cancel registration                         `O(R)` Searches for the
                                                     registration

  Generate one event                          `O(R)` Counts matching
  report                                             confirmed records

  Load/save events                            `O(E)` Processes each event
                                                     record

  Load/save                                   `O(R)` Processes each
  registrations                                      registration record
  ------------------------------------------------------------------------

The primary in-memory storage requirement is `O(E + R)`. A linear scan
uses `O(1)` auxiliary space, excluding stored objects and temporary
parsing data.

## Validation Rules

1.  Event IDs and registration IDs must be unique.
2.  Event capacity must be a positive integer.
3.  A registration must refer to an existing event.
4.  An email address cannot have more than one confirmed registration
    for the same event.
5.  Confirmed registrations cannot exceed event capacity.
6.  A cancelled registration remains in storage but is excluded from the
    confirmed count.
7.  The described implementation checks for a basic email format and a
    10-digit phone number.

A participant may use the same email address to register for different
events, provided each event has capacity.

## Testing Checklist

Run these scenarios against the final source code:

  -----------------------------------------------------------------------
  Test                    Scenario                Expected result
  ----------------------- ----------------------- -----------------------
  T01                     Valid registration with Registration accepted
                          seats available         and ID generated

  T02                     Same email registered   Duplicate rejected
                          twice for the same      
                          event                   

  T03                     Registration when event Request rejected
                          is full                 

  T04                     Registration for an     Event-not-found message
                          unknown event ID        

  T05                     Invalid email or phone  Input rejected
                          number                  

  T06                     Search for an existing  Matching record
                          registration ID         displayed

  T07                     Cancel a confirmed      Status changes to
                          registration            `CANCELLED`; seat is
                                                  released

  T08                     Cancel an already       Already-cancelled
                          cancelled registration  message

  T09                     Restart after saving    Previously saved
                          records                 records are loaded

  T10                     Same email registers    Allowed if that event
                          for a different event   has capacity
  -----------------------------------------------------------------------

These are test specifications, not proof that the tests have already
been executed. Record actual outcomes after testing your implementation.

## Limitations

-   Text-file storage is intended for small local datasets and does not
    safely support concurrent writes.
-   Linear search becomes slower as the number of records grows.
-   Basic email and phone checks do not verify that contact details are
    reachable.
-   The described application assumes one administrator and does not
    include authentication or role-based access.
-   Delimiter-separated storage requires special handling for delimiter
    characters in user input.
-   The base project scope is a CLI; a complete graphical interface
    requires additional implementation and testing.

## Possible Improvements

-   Replace text files with MySQL or SQLite using JDBC.
-   Use database transactions to prevent overbooking by simultaneous
    users.
-   Add administrator authentication and role-based permissions.
-   Develop a complete Java Swing graphical interface.
-   Add QR-code check-in and attendance tracking.
-   Send email confirmations and cancellation notifications.
-   Add filtering, sorting, and downloadable reports.

## Author

**Saanvi Aggarwal**\
Department of Computer Science and Engineering (Artificial
Intelligence)\
Chandigarh University

------------------------------------------------------------------------

## Academic Note

This is a learning-oriented course project demonstrating Java OOP,
collections, searching, validation, and file handling. Before publishing
or submitting the repository, confirm that the README's file structure,
commands, features, and data formats match the actual source code.
