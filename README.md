# Autosphere (Java OOP Project)

A simple car buying/selling web app in Java with:

- Buyer home page with all listed cars
- Filters (make, model, color, city, min/max price)
- Car status filter (available/sold)
- Seller login/signup, car upload (multiple images), edit, delete
- Admin login with full access (manage cars and users)
- Buyer login/signup, favorites, and contact seller inquiries
- Car details page with full info and seller contact details
- Password hashing (SHA-256) for stored user credentials
- SQLite database with 10 sample car records

No external Tomcat setup is required. Just run the Java main app.

## Tech Stack

- Java 17
- Javalin (embedded web server)
- SQLite (local file database)
- JDBC

## Run

1. Make sure Java 17+ and Maven are installed.
2. In this project directory, run:

```bash
mvn compile exec:java
```

3. Open:

`http://localhost:7000`

## Default Accounts

- Seller: `seller1` / `seller123`
- Seller: `seller2` / `seller123`
- Admin: `admin` / `admin123`
- Buyer: `buyer1` / `buyer123`

## Project Structure

- `src/main/java/com/samtech/carapp/Main.java` - app entry and routes
- `src/main/java/com/samtech/carapp/Database.java` - DB schema and sample data
- `src/main/java/com/samtech/carapp/CarRepository.java` - car CRUD/filter logic
- `src/main/java/com/samtech/carapp/UserRepository.java` - login/user management
- `src/main/java/com/samtech/carapp/HtmlRenderer.java` - page rendering
- `src/main/resources/public/styles.css` - app styling
- `data/car_marketplace.db` - SQLite DB file (created at first run)
- `uploads/` - uploaded car images (created at first run)

## Extra OOP Features Included

- Clean class-based design (models, repositories, renderer, app entry)
- Session-based role access for buyer/seller/admin separation
- Dynamic search filtering and sorted listing output
- File upload handling with unique image names
- Inheritance + polymorphism through `CarView` -> `CompactCarView` renderer strategy
