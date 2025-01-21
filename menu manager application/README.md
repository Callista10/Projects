## Overview

This is a basic order management application. Order data is stored in a JSON file and manipulated through a Java interface. The application is designed to accommodate two types of users: guests and administrators.

## Features

**For general users:**

1. **View Menu Categories:** This feature allows customers to view all available categories on the menu.
2. **View Menu Details:** This feature allows customers to select a category and view detailed information of all items within that category, including the item name, description, and price.

**For admin users:**

1. **Add Menu Items:** This feature allows administrators to add new items to a specific category. They need to provide the category, item name, item description, and price.
2. **Update Existing Items:** This feature allows administrators to update the description and price of existing items in a category.
3. **Delete Items:** This feature allows administrators to delete items from a category.
4. **Edit Categories:** This feature allows administrators to change the name of existing categories.
5. **View Order History:** Administrators can view all past order history.
6. **Query Specific Order:** Administrators can query an order with a specific order number.
7. **Register New Admins:** Users who are already administrators can register new admin users.
8. **Login:** Users can login using their username and password.
9. **Logout:** Users can logout of their account.

## Environment Requirements

- Java version: 16.0.2
- Gradle version: 7.5.1
- Junit5

## Preparation
1. Open a terminal window
2. Type `gradle -v`
3. If Gradle is not installed, follow the instructions [here](https://gradle.org/install/). 
4. Please use gradle with version at least 7.5.1. If you have an older version, please update it by the following command:
```
gradle wrapper --gradle-version 7.5.1
```
5. Open a new project in text editor (e.g. VS Code or IntelliJ) with Gradle support.
6. Perform gradle init to initialise the project.
```
gradle init
```
7. Clone the repository to your local machine.
```
git clone https://github.sydney.edu.au/SOFT2412-COMP9412-2023S2/Lab02-Hunter-Group4-A1.git
```
6. Check the gradle works properly by running the following command:
- Clean the cache of the project:
```
./gradlew clean
```
- Build the project:
```
./gradlew build
```

## How to Run

This application is a Gradle project. To run the application interactively, navigate to the directory containing the `build.gradle` file and run the following command:
```
./gradlew runInteractive --console=plain
```

## How to Test

Tests are written using JUnit. To run the tests, use the following command in the same directory as the `build.gradle` file:

```
./gradlew clean test
```

## Dependencies

This application uses the following libraries:

- json-simple (version: 1.1.1): For parsing and manipulating the JSON file.
- Jackson (version: 2.12.3): For serialization and deserialization of JSON.

The Gradle build file should manage these dependencies automatically.

## How to Contribute

If you'd like to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create a new branch in your forked repository.
3. Make your changes in this new branch.
4. Submit a pull request from the new branch in your forked repository to the main branch in the original repository.

Remember to write a clear and concise commit message and pull request description, explaining your changes and the reasons for them.

## License

This application is open-source. You are free to use and modify it, but you must credit the original author and source.

Reference:
1. https://www.geeksforgeeks.org/how-to-convert-map-to-json-to-hashmap-in-java/

2. https://www.baeldung.com/java-convert-hashmap-to-json-object

