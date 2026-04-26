package com.samtech.carapp;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class HtmlRenderer {
    private static final CarView COMPACT_VIEW = new CompactCarView();

    private HtmlRenderer() {
    }

    public static String authSelectionPage(String actionName, String actionPath) {
        return layout(actionName, """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav><a href="/">Home</a></nav>
                </header>
                <section class="panel narrow center-panel">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <img src="/logo.png" alt="Autosphere" style="height: 80px; max-width: 100%%; object-fit: contain;" />
                    </div>
                    <h2 style="text-align:center">Select Account Type for %s</h2>
                    <div class="stack-form" style="margin-top:20px;">
                        <a class="button" href="/buyer/%s">Buyer</a>
                        <a class="button" href="/seller/%s">Seller</a>
                    </div>
                </section>
                """.formatted(escape(actionName), actionPath, actionPath));
    }

    public static String homePage(List<Car> cars, Map<String, String> filters, User viewer, Set<Integer> favoriteCarIds, String message) {
        StringBuilder cards = new StringBuilder();
        for (Car car : cars) {
            cards.append(COMPACT_VIEW.render(car));
        }

        String messageBox = message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>";
        String authLinks = viewer == null
                ? "<a href='/auth/login'>Sign In</a><a href='/auth/signup'>Sign Up</a>"
                : "<a href='/buyer/favorites'>My Favorites</a><a href='/" + escape(viewer.getRole().toLowerCase()) + "/logout'>Sign Out</a>";
        return layout("Autosphere - Buy Cars & Bikes", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav>
                        <a href="/?type=Car">Used Cars</a>
                        <a href="/?type=Bike">Used Bikes</a>
                        <a href="/compare">Compare Cars</a>
                        %s
                    </nav>
                </header>
                %s
                <section class="home-layout">
                    <aside class="panel sidebar">
                        <h2>Filters</h2>
                        <form class="stack-form" method="get" action="/">
                            <input type="hidden" name="type" value="%s"/>
                            <input type="text" name="search" placeholder="Search title or make..." value="%s" class="search-input"/>
                            <input type="text" name="make" list="makesList" placeholder="Make" value="%s"/>
                            <datalist id="makesList">
                                <option value="Suzuki"></option>
                                <option value="Toyota"></option>
                                <option value="Honda"></option>
                                <option value="KIA"></option>
                                <option value="Hyundai"></option>
                                <option value="Changan"></option>
                                <option value="MG"></option>
                                <option value="Peugeot"></option>
                                <option value="Proton"></option>
                                <option value="DFSK"></option>
                                <option value="FAW"></option>
                                <option value="Haval"></option>
                                <option value="BAIC"></option>
                            </datalist>
                            <input type="text" name="model" placeholder="Model" value="%s"/>
                            <input type="text" name="color" placeholder="Color" value="%s"/>
                            <input type="text" name="city" list="citiesList" placeholder="City" value="%s"/>
                            <datalist id="citiesList">
                                <option value="Karachi"></option>
                                <option value="Lahore"></option>
                                <option value="Islamabad"></option>
                                <option value="Rawalpindi"></option>
                                <option value="Peshawar"></option>
                                <option value="Quetta"></option>
                                <option value="Multan"></option>
                                <option value="Faisalabad"></option>
                                <option value="Gujranwala"></option>
                                <option value="Sialkot"></option>
                                <option value="Hyderabad"></option>
                                <option value="Abbottabad"></option>
                                <option value="Bahawalpur"></option>
                                <option value="Sargodha"></option>
                                <option value="Sukkur"></option>
                                <option value="Jhang"></option>
                                <option value="Sheikhupura"></option>
                                <option value="Mardan"></option>
                                <option value="Gujrat"></option>
                                <option value="Kasur"></option>
                                <option value="Mingora"></option>
                            </datalist>
                            <input type="number" name="minMileage" placeholder="Min Mileage" value="%s"/>
                            <input type="number" name="maxMileage" placeholder="Max Mileage" value="%s"/>
                            <input type="number" step="0.1" name="minPrice" placeholder="Min Price" value="%s"/>
                            <input type="number" step="0.1" name="maxPrice" placeholder="Max Price" value="%s"/>
                            <select name="status">
                                <option value="">Any Status</option>
                                <option value="AVAILABLE" %s>Available</option>
                                <option value="SOLD" %s>Sold</option>
                            </select>
                            <button type="submit">Apply Filters</button>
                            <a class="button secondary" href="/">Reset</a>
                        </form>
                    </aside>
                    <section class="car-grid">%s</section>
                </section>
                """.formatted(
                authLinks,
                messageBox,
                escape(filters.getOrDefault("type", "")),
                escape(filters.getOrDefault("search", "")),
                escape(filters.getOrDefault("make", "")),
                escape(filters.getOrDefault("model", "")),
                escape(filters.getOrDefault("color", "")),
                escape(filters.getOrDefault("city", "")),
                escape(filters.getOrDefault("minMileage", "")),
                escape(filters.getOrDefault("maxMileage", "")),
                escape(filters.getOrDefault("minPrice", "")),
                escape(filters.getOrDefault("maxPrice", "")),
                "AVAILABLE".equals(filters.getOrDefault("status", "")) ? "selected" : "",
                "SOLD".equals(filters.getOrDefault("status", "")) ? "selected" : "",
                cards
        ));
    }

    public static String loginPage(String title, String action, String error, String msg) {
        String messageBox = "";
        if (error != null && !error.isBlank()) messageBox = "<div class='error'>" + escape(error) + "</div>";
        else if (msg != null && !msg.isBlank()) messageBox = "<div class='notice success'>" + escape(msg) + "</div>";

        return layout(title, """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav><a href="/">Home</a></nav>
                </header>
                <section class="panel narrow center-panel">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <img src="/logo.png" alt="Autosphere" style="height: 80px; max-width: 100%%; object-fit: contain;" />
                    </div>
                    <h2 style="text-align:center">%s</h2>
                    %s
                    <form method="post" action="%s" class="stack-form">
                        <label>Username <input name="username" required/></label>
                        <label>Password <input type="password" name="password" required/></label>
                        <button type="submit">Login</button>
                    </form>
                </section>
                """.formatted(escape(title), messageBox, action));
    }

    public static String signupPage(String title, String action, boolean sellerSignup, String error, String msg) {
        String messageBox = "";
        if (error != null && !error.isBlank()) messageBox = "<div class='error'>" + escape(error) + "</div>";
        else if (msg != null && !msg.isBlank()) messageBox = "<div class='notice success'>" + escape(msg) + "</div>";

        String sellerFields = sellerSignup
                ? """
                        <label>Email <input type="email" name="email" required/></label>
                        <label>Phone <input name="phone" required/></label>
                        """
                : "";
        return layout(title, """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav><a href="/">Home</a></nav>
                </header>
                <section class="panel narrow center-panel">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <img src="/logo.png" alt="Autosphere" style="height: 80px; max-width: 100%%; object-fit: contain;" />
                    </div>
                    <h2 style="text-align:center">%s</h2>
                    %s
                    <form method="post" action="%s" class="stack-form">
                        <label>Username <input name="username" required/></label>
                        <label>Password <input type="password" name="password" required/></label>
                        %s
                        <button type="submit">Create Account</button>
                    </form>
                </section>
                """.formatted(escape(title), messageBox, action, sellerFields));
    }

    public static String sellerDashboard(User seller, List<Car> cars, String message) {
        StringBuilder myCars = new StringBuilder();
        for (Car car : cars) {
            myCars.append("""
                    <div class="car-row">
                        <span>%s (%d)</span>
                        <span>%s - %s</span>
                        <span>Rs %,.0f</span>
                        <span>%s</span>
                        <div class="actions">
                            <form method="post" action="/seller/cars/%d/status">
                                <select name="status">
                                    <option value="AVAILABLE" %s>Available</option>
                                    <option value="SOLD" %s>Sold</option>
                                </select>
                                <button type="submit">Update</button>
                            </form>
                            <a class="button secondary" href="/seller/cars/%d/edit">Edit</a>
                            <form method="post" action="/seller/cars/%d/delete">
                                <button type="submit" class="danger">Delete</button>
                            </form>
                        </div>
                    </div>
                    """.formatted(
                    escape(car.getTitle()),
                    car.getYear(),
                    escape(car.getCity()),
                    escape(car.getColor()),
                    car.getPrice(),
                    escape(car.getStatus()),
                    car.getId(),
                    "AVAILABLE".equals(car.getStatus()) ? "selected" : "",
                    "SOLD".equals(car.getStatus()) ? "selected" : "",
                    car.getId(),
                    car.getId()
            ));
        }
        String messageBox = message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>";
        return layout("Seller Dashboard", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                        <h2 style="margin-left: 10px; color: #64748b; font-size: 1.2rem;">Seller Panel</h2>
                    </div>
                    <nav>
                        <a href="/seller/logout">Logout</a>
                    </nav>
                </header>
                <section class="panel">
                    <h2>Welcome, %s</h2>
                    %s
                    <h3>Add New Listing</h3>
                    <form class="grid-form" method="post" action="/seller/cars" enctype="multipart/form-data">
                        <select name="vehicleType" required>
                            <option value="Car">Car</option>
                            <option value="Bike">Bike</option>
                        </select>
                        <input name="title" placeholder="Listing title" required/>
                        <input name="make" placeholder="Make" required/>
                        <input name="model" placeholder="Model" required/>
                        <input name="color" placeholder="Color" required/>
                        <input name="city" placeholder="City" required/>
                        <input type="number" name="year" placeholder="Year" min="1980" max="2100" required/>
                        <input type="number" name="mileage" placeholder="Mileage (km)" min="0" required/>
                        <input type="number" name="price" placeholder="Price" step="0.1" min="0" required/>
                        <select name="transmission" required>
                            <option value="Manual">Manual</option>
                            <option value="Automatic">Automatic</option>
                        </select>
                        <select name="fuelType" required>
                            <option value="Petrol">Petrol</option>
                            <option value="Diesel">Diesel</option>
                            <option value="Electric">Electric</option>
                            <option value="Hybrid">Hybrid</option>
                        </select>
                        <select name="assembly" required>
                            <option value="Local">Local</option>
                            <option value="Imported">Imported</option>
                        </select>
                        <select name="paintCondition" id="paintCond" onchange="toggleShowered()" required>
                            <option value="Genuine">Genuine</option>
                            <option value="Completely Showered">Completely Showered</option>
                            <option value="Some parts showered">Some parts showered</option>
                        </select>
                        <input name="showeredParts" id="showeredParts" placeholder="Which parts are showered?" style="display:none;"/>
                        <input name="description" placeholder="Short description" required/>
                        <input type="file" name="images" accept="image/*" multiple required/>
                        <button type="submit" style="grid-column: 1 / -1;">Upload and Publish</button>
                    </form>
                    <script>
                        function toggleShowered() {
                            var cond = document.getElementById('paintCond').value;
                            document.getElementById('showeredParts').style.display = (cond === 'Some parts showered') ? 'block' : 'none';
                        }
                    </script>
                </section>
                <section class="panel">
                    <h3>My Listings (%d)</h3>
                    %s
                </section>
                """.formatted(escape(seller.getUsername()), messageBox, cars.size(), myCars));
    }

    public static String adminDashboard(User admin, List<Car> cars, List<User> users, String message) {
        return adminDashboard(admin, cars, users, List.of(), message);
    }

    public static String adminDashboard(User admin, List<Car> cars, List<User> users, List<String> inquiries, String message) {
        StringBuilder carRows = new StringBuilder();
        for (Car car : cars) {
            carRows.append("""
                    <tr>
                        <td>%d</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>Rs %,.0f</td>
                        <td>%s</td>
                        <td>
                            <form method="post" action="/admin/cars/%d/status">
                                <select name="status">
                                    <option value="AVAILABLE" %s>Available</option>
                                    <option value="SOLD" %s>Sold</option>
                                </select>
                                <button type="submit">Save</button>
                            </form>
                            <form method="post" action="/admin/cars/%d/delete">
                                <button class="danger" type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                    """.formatted(
                    car.getId(),
                    escape(car.getTitle()),
                    escape(car.getMake()),
                    escape(car.getSellerName()),
                    car.getPrice(),
                    escape(car.getStatus()),
                    car.getId(),
                    "AVAILABLE".equals(car.getStatus()) ? "selected" : "",
                    "SOLD".equals(car.getStatus()) ? "selected" : "",
                    car.getId()
            ));
        }

        StringBuilder userRows = new StringBuilder();
        for (User user : users) {
            userRows.append("""
                    <tr>
                        <td>%d</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>
                            %s
                        </td>
                    </tr>
                    """.formatted(
                    user.getId(),
                    escape(user.getUsername()),
                    escape(user.getRole()),
                    "ADMIN".equals(user.getRole())
                            ? "<span class='muted'>Protected</span>"
                            : "<form method='post' action='/admin/users/" + user.getId() + "/delete'><button class='danger' type='submit'>Delete</button></form>"
            ));
        }
        StringBuilder inquiryRows = new StringBuilder();
        for (String inquiry : inquiries) {
            inquiryRows.append("<li>").append(escape(inquiry)).append("</li>");
        }
        String messageBox = message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>";
        return layout("Admin Dashboard", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                        <h2 style="margin-left: 10px; color: #64748b; font-size: 1.2rem;">Admin Panel</h2>
                    </div>
                    <nav>
                        <a href="/admin/logout">Logout</a>
                    </nav>
                </header>
                <section class="panel">
                    <h2>Welcome, %s</h2>
                    %s
                    <h3>All Listings</h3>
                    <table>
                        <thead><tr><th>ID</th><th>Title</th><th>Make</th><th>Seller</th><th>Price</th><th>Status</th><th>Action</th></tr></thead>
                        <tbody>%s</tbody>
                    </table>
                </section>
                <section class="panel">
                    <h3>All Users</h3>
                    <table>
                        <thead><tr><th>ID</th><th>Username</th><th>Role</th><th>Action</th></tr></thead>
                        <tbody>%s</tbody>
                    </table>
                </section>
                <section class="panel">
                    <h3>Buyer Inquiries</h3>
                    <ul>%s</ul>
                </section>
                """.formatted(escape(admin.getUsername()), messageBox, carRows, userRows, inquiryRows));
    }

    public static String favoritesPage(User buyer, List<Car> favorites, Set<Integer> favoriteIds, String message) {
        return layout("My Favorites", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" height="40" class="brand-logo" style="margin-right:10px;"/>
                        <h1>Autosphere Favorites</h1>
                    </div>
                    <nav>
                        <a href="/">Browse Listings</a>
                        <a href="/buyer/logout">Logout</a>
                    </nav>
                </header>
                %s
                <section class="car-grid">%s</section>
                """.formatted(
                message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>",
                buildCardsForFavorites(favorites, favoriteIds)
        ));
    }

    private static String buildCardsForFavorites(List<Car> favorites, Set<Integer> favoriteIds) {
        StringBuilder cards = new StringBuilder();
        for (Car car : favorites) {
            boolean isFav = favoriteIds.contains(car.getId());
            cards.append("""
                    <div class="car-card">
                        <img src="%s" alt="%s"/>
                        <div class="car-card-content">
                            <h3>%s</h3>
                            <p><strong>Make:</strong> %s | <strong>Model:</strong> %s</p>
                            <p><strong>Color:</strong> %s | <strong>City:</strong> %s</p>
                            <p class="price">Rs %,.0f</p>
                            <p><strong>Status:</strong> <span class="%s">%s</span></p>
                            <form method="post" action="/buyer/favorites/%d/remove">
                                <button type="submit" class="%s">%s</button>
                            </form>
                        </div>
                    </div>
                    """.formatted(
                    escape(getImageSrc(car.getImagePath())),
                    escape(car.getTitle()),
                    escape(car.getTitle()),
                    escape(car.getMake()),
                    escape(car.getModel()),
                    escape(car.getColor()),
                    escape(car.getCity()),
                    car.getPrice(),
                    "SOLD".equals(car.getStatus()) ? "sold-tag" : "available-tag",
                    escape(car.getStatus()),
                    car.getId(),
                    isFav ? "danger" : "",
                    isFav ? "Remove Favorite" : "Not in Favorites"
            ));
        }
        return cards.toString();
    }

    public static String carDetailsPage(CarDetails details, User viewer, boolean isFavorite, String message) {
        String messageBox = message == null || message.isBlank() ? "" : "<div class='notice success'>" + escape(message) + "</div>";
        StringBuilder images = new StringBuilder();
        if (!details.getImagePaths().isEmpty()) {
            images.append("<div class='main-image-container'>");
            images.append("<img id='main-car-pic' src='").append(escape(details.getImagePaths().get(0))).append("' alt='car image' onclick='openModal(this.src)'/>");
            images.append("</div>");
            images.append("<div class='image-slider'>");
            for (String img : details.getImagePaths()) {
                images.append("<img src='").append(escape(img)).append("' alt='thumbnail' onclick='document.getElementById(\"main-car-pic\").src=this.src'/>");
            }
            images.append("</div>");
        }
        
        String buyerActions = "";
        if (viewer != null && "BUYER".equals(viewer.getRole())) {
            buyerActions = """
                    <div style="margin-top:20px;">
                    <form method="post" action="/buyer/favorites/%d/%s" style="margin-bottom:10px;">
                        <button type="submit" class="%s">%s</button>
                    </form>
                    <form method="post" action="/buyer/inquiries" class="stack-form">
                        <input type="hidden" name="carId" value="%d"/>
                        <input name="phone" placeholder="Your phone (optional)"/>
                        <input name="message" placeholder="Message to seller" required/>
                        <button type="submit">Send Message</button>
                    </form>
                    </div>
                    """.formatted(
                    details.getCar().getId(),
                    isFavorite ? "remove" : "add",
                    isFavorite ? "danger" : "",
                    isFavorite ? "Remove Favorite" : "Add Favorite",
                    details.getCar().getId()
            );
        }
        String sellerContact = """
                <div class="seller-contact">
                    <h3>Seller Contact</h3>
                    <p>Email: %s</p>
                    <p>Phone: %s</p>
                </div>
                """.formatted(escape(details.getSeller().getEmail()), escape(details.getSeller().getPhone()));
                
        String extraDetails = "";
        if (details.getCar().getVehicleType() != null && !details.getCar().getVehicleType().isBlank()) {
            extraDetails = """
                    <div class="spec-grid">
                        <div class="spec-item"><span>Vehicle Type</span><strong>%s</strong></div>
                        <div class="spec-item"><span>Transmission</span><strong>%s</strong></div>
                        <div class="spec-item"><span>Fuel Type</span><strong>%s</strong></div>
                        <div class="spec-item"><span>Assembly</span><strong>%s</strong></div>
                        <div class="spec-item"><span>Paint Condition</span><strong>%s</strong></div>
                        %s
                    </div>
                    """.formatted(
                        escape(details.getCar().getVehicleType()),
                        escape(details.getCar().getTransmission()),
                        escape(details.getCar().getFuelType()),
                        escape(details.getCar().getAssembly()),
                        escape(details.getCar().getPaintCondition()),
                        (details.getCar().getShoweredParts() != null && !details.getCar().getShoweredParts().isBlank()) ? "<div class=\"spec-item\" style=\"grid-column: 1 / -1\"><span>Showered Parts</span><strong>" + escape(details.getCar().getShoweredParts()) + "</strong></div>" : ""
                    );
        }
                
        return layout("Car Details", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav><a href="/">Back to Listings</a></nav>
                </header>
                %s
                <section class="panel details-layout">
                    <div class="gallery-section">
                        %s
                    </div>
                    <div class="info-section">
                        <h2>%s</h2>
                        <p class="price">Rs %,.0f</p>
                        <div class="spec-grid">
                            <div class="spec-item"><span>Make</span><strong>%s</strong></div>
                            <div class="spec-item"><span>Model</span><strong>%s</strong></div>
                            <div class="spec-item"><span>Color</span><strong>%s</strong></div>
                            <div class="spec-item"><span>City</span><strong>%s</strong></div>
                            <div class="spec-item"><span>Year</span><strong>%d</strong></div>
                            <div class="spec-item"><span>Mileage</span><strong>%,d km</strong></div>
                            <div class="spec-item"><span>Status</span><strong>%s</strong></div>
                        </div>
                        %s
                        <div class="description-box">
                            <h3>Description</h3>
                            <p>%s</p>
                        </div>
                        %s%s
                    </div>
                </section>
                
                <!-- The Modal -->
                <div id="imageModal" class="modal" onclick="this.style.display='none'">
                  <span class="close">&times;</span>
                  <img class="modal-content" id="img01">
                </div>
                <script>
                function openModal(src) {
                    document.getElementById('imageModal').style.display = 'block';
                    document.getElementById('img01').src = src;
                }
                </script>
                """.formatted(
                messageBox,
                images,
                escape(details.getCar().getTitle()),
                details.getCar().getPrice(),
                escape(details.getCar().getMake()),
                escape(details.getCar().getModel()),
                escape(details.getCar().getColor()),
                escape(details.getCar().getCity()),
                details.getCar().getYear(),
                details.getCar().getMileage(),
                escape(details.getCar().getStatus()),
                extraDetails,
                escape(details.getCar().getDescription()),
                sellerContact,
                buyerActions
        ));
    }

    public static String sellerEditPage(Car car, String message) {
        String messageBox = message == null || message.isBlank() ? "" : "<div class='error'>" + escape(message) + "</div>";
        return layout("Edit Listing", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                        <h2 style="margin-left: 10px; color: #64748b; font-size: 1.2rem;">Seller Panel</h2>
                    </div>
                    <nav><a href="/seller/dashboard">Back</a></nav>
                </header>
                <section class="panel">
                    <h2>Edit Listing</h2>
                    %s
                    <form method="post" action="/seller/cars/%d/edit" class="grid-form" enctype="multipart/form-data">
                        <select name="vehicleType" required>
                            <option value="Car" %s>Car</option>
                            <option value="Bike" %s>Bike</option>
                        </select>
                        <input name="title" value="%s" required/>
                        <input name="make" value="%s" required/>
                        <input name="model" value="%s" required/>
                        <input name="color" value="%s" required/>
                        <input name="city" value="%s" required/>
                        <input type="number" name="year" value="%d" required/>
                        <input type="number" name="mileage" value="%d" required/>
                        <input type="number" step="0.1" name="price" value="%.0f" required/>
                        <select name="transmission" required>
                            <option value="Manual" %s>Manual</option>
                            <option value="Automatic" %s>Automatic</option>
                        </select>
                        <select name="fuelType" required>
                            <option value="Petrol" %s>Petrol</option>
                            <option value="Diesel" %s>Diesel</option>
                            <option value="Electric" %s>Electric</option>
                            <option value="Hybrid" %s>Hybrid</option>
                        </select>
                        <select name="assembly" required>
                            <option value="Local" %s>Local</option>
                            <option value="Imported" %s>Imported</option>
                        </select>
                        <select name="paintCondition" id="paintCond" onchange="toggleShowered()" required>
                            <option value="Genuine" %s>Genuine</option>
                            <option value="Completely Showered" %s>Completely Showered</option>
                            <option value="Some parts showered" %s>Some parts showered</option>
                        </select>
                        <input name="showeredParts" id="showeredParts" value="%s" placeholder="Which parts are showered?" style="display:%s;"/>
                        <input name="description" value="%s" required/>
                        <input type="file" name="images" accept="image/*" multiple/>
                        <button type="submit" style="grid-column: 1 / -1;">Save Changes</button>
                    </form>
                    <script>
                        function toggleShowered() {
                            var cond = document.getElementById('paintCond').value;
                            document.getElementById('showeredParts').style.display = (cond === 'Some parts showered') ? 'block' : 'none';
                        }
                    </script>
                </section>
                """.formatted(
                messageBox,
                car.getId(),
                "Car".equals(car.getVehicleType()) ? "selected" : "",
                "Bike".equals(car.getVehicleType()) ? "selected" : "",
                escape(car.getTitle()),
                escape(car.getMake()),
                escape(car.getModel()),
                escape(car.getColor()),
                escape(car.getCity()),
                car.getYear(),
                car.getMileage(),
                car.getPrice(),
                "Manual".equals(car.getTransmission()) ? "selected" : "",
                "Automatic".equals(car.getTransmission()) ? "selected" : "",
                "Petrol".equals(car.getFuelType()) ? "selected" : "",
                "Diesel".equals(car.getFuelType()) ? "selected" : "",
                "Electric".equals(car.getFuelType()) ? "selected" : "",
                "Hybrid".equals(car.getFuelType()) ? "selected" : "",
                "Local".equals(car.getAssembly()) ? "selected" : "",
                "Imported".equals(car.getAssembly()) ? "selected" : "",
                "Genuine".equals(car.getPaintCondition()) ? "selected" : "",
                "Completely Showered".equals(car.getPaintCondition()) ? "selected" : "",
                "Some parts showered".equals(car.getPaintCondition()) ? "selected" : "",
                escape(car.getShoweredParts()),
                "Some parts showered".equals(car.getPaintCondition()) ? "block" : "none",
                escape(car.getDescription())
        ));
    }

    private static String layout(String title, String body) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>%s</title>
                    <link rel="stylesheet" href="/styles.css" />
                </head>
                <body>
                    <main class="container">%s</main>
                </body>
                </html>
                """.formatted(escape(title), body);
    }

    private static String getImageSrc(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return "https://via.placeholder.com/1200x800?text=No+Image";
        }
        return imagePath;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String compareSelectionPage(List<CarSpec> allSpecs, User viewer) {
        StringBuilder specOptions = new StringBuilder();
        for (CarSpec spec : allSpecs) {
            specOptions.append("""
                <div class="spec-checkbox-card">
                    <input type="checkbox" name="ids" id="spec_%d" value="%d" onchange="limitCheckboxes()">
                    <label for="spec_%d">%s</label>
                </div>
            """.formatted(spec.getId(), spec.getId(), spec.getId(), escape(spec.getName())));
        }

        String authLinks = viewer == null
                ? "<a href='/auth/login'>Sign In</a><a href='/auth/signup'>Sign Up</a>"
                : "<a href='/" + escape(viewer.getRole().toLowerCase()) + "/logout'>Sign Out</a>";

        return layout("Compare Cars", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav>
                        <a href="/?type=Car">Used Cars</a>
                        <a href="/?type=Bike">Used Bikes</a>
                        <a href="/compare" style="text-decoration:underline;">Compare Cars</a>
                        %s
                    </nav>
                </header>
                <section class="panel">
                    <h2>Select up to 3 cars to compare</h2>
                    <form method="get" action="/compare" class="compare-selection-form" onsubmit="return validateSelection()">
                        <div class="spec-selection-grid">
                            %s
                        </div>
                        <button type="submit" id="compare-btn" disabled style="margin-top:20px;">Compare Selected (0)</button>
                    </form>
                    <script>
                        function limitCheckboxes() {
                            const checkedBoxes = document.querySelectorAll('input[name="ids"]:checked');
                            const btn = document.getElementById('compare-btn');
                            btn.innerText = 'Compare Selected (' + checkedBoxes.length + ')';
                            
                            if (checkedBoxes.length > 0 && checkedBoxes.length <= 3) {
                                btn.disabled = false;
                            } else {
                                btn.disabled = true;
                            }

                            if (checkedBoxes.length >= 3) {
                                document.querySelectorAll('input[name="ids"]:not(:checked)').forEach(box => box.disabled = true);
                            } else {
                                document.querySelectorAll('input[name="ids"]').forEach(box => box.disabled = false);
                            }
                        }
                        
                        function validateSelection() {
                            const checkedBoxes = document.querySelectorAll('input[name="ids"]:checked');
                            if (checkedBoxes.length < 2) {
                                alert("Please select at least 2 cars to compare.");
                                return false;
                            }
                            return true;
                        }
                    </script>
                </section>
                """.formatted(authLinks, specOptions.toString()));
    }

    public static String compareResultPage(List<CarSpec> specs, User viewer) {
        String authLinks = viewer == null
                ? "<a href='/auth/login'>Sign In</a><a href='/auth/signup'>Sign Up</a>"
                : "<a href='/" + escape(viewer.getRole().toLowerCase()) + "/logout'>Sign Out</a>";

        StringBuilder tableHeader = new StringBuilder();
        StringBuilder engineRow = new StringBuilder();
        StringBuilder fuelTypeRow = new StringBuilder();
        StringBuilder maxPowerRow = new StringBuilder();
        StringBuilder maxTorqueRow = new StringBuilder();
        StringBuilder transRow = new StringBuilder();
        StringBuilder driveRow = new StringBuilder();
        StringBuilder bodyRow = new StringBuilder();
        StringBuilder seatsRow = new StringBuilder();
        StringBuilder tankRow = new StringBuilder();
        StringBuilder econRow = new StringBuilder();
        StringBuilder dimRow = new StringBuilder();
        StringBuilder wheelRow = new StringBuilder();
        StringBuilder clearanceRow = new StringBuilder();
        StringBuilder weightRow = new StringBuilder();
        StringBuilder safetyRow = new StringBuilder();
        StringBuilder priceRow = new StringBuilder();

        for (CarSpec spec : specs) {
            tableHeader.append("<th>").append(escape(spec.getName())).append("</th>");
            engineRow.append("<td>").append(escape(spec.getEngine())).append("</td>");
            fuelTypeRow.append("<td>").append(escape(spec.getFuelType())).append("</td>");
            maxPowerRow.append("<td>").append(escape(spec.getMaxPower())).append("</td>");
            maxTorqueRow.append("<td>").append(escape(spec.getMaxTorque())).append("</td>");
            transRow.append("<td>").append(escape(spec.getTransmission())).append("</td>");
            driveRow.append("<td>").append(escape(spec.getDriveType())).append("</td>");
            bodyRow.append("<td>").append(escape(spec.getBodyType())).append("</td>");
            seatsRow.append("<td>").append(spec.getSeats()).append("</td>");
            tankRow.append("<td>").append(escape(spec.getFuelTank())).append("</td>");
            econRow.append("<td>").append(escape(spec.getFuelEconomy())).append("</td>");
            dimRow.append("<td>").append(escape(spec.getDimensions())).append("</td>");
            wheelRow.append("<td>").append(escape(spec.getWheelbase())).append("</td>");
            clearanceRow.append("<td>").append(escape(spec.getGroundClearance())).append("</td>");
            weightRow.append("<td>").append(escape(spec.getKerbWeight())).append("</td>");
            safetyRow.append("<td>").append(escape(spec.getSafety())).append("</td>");
            priceRow.append("<td class='price-cell'>").append(escape(spec.getPriceRange())).append("</td>");
        }

        return layout("Compare Specifications", """
                <header class="topbar">
                    <div style="display:flex; align-items:center;">
                        <img src="/logo.png" alt="Autosphere" class="brand-logo"/>
                    </div>
                    <nav>
                        <a href="/?type=Car">Used Cars</a>
                        <a href="/?type=Bike">Used Bikes</a>
                        <a href="/compare" style="text-decoration:underline;">Compare Cars</a>
                        %s
                    </nav>
                </header>
                <section class="panel">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 20px;">
                        <h2>Compare Specifications</h2>
                        <a class="button secondary" href="/compare">Back to Selection</a>
                    </div>
                    <div class="compare-table-wrapper">
                        <table class="compare-table">
                            <thead>
                                <tr>
                                    <th class="feature-col">Features</th>
                                    %s
                                </tr>
                            </thead>
                            <tbody>
                                <tr class="group-header"><td colspan="%d">Dimensions & Capacity</td></tr>
                                <tr><td class="feature-col">Body Type</td>%s</tr>
                                <tr><td class="feature-col">Dimensions (L x W)</td>%s</tr>
                                <tr><td class="feature-col">Wheelbase</td>%s</tr>
                                <tr><td class="feature-col">Ground Clearance</td>%s</tr>
                                <tr><td class="feature-col">Kerb Weight</td>%s</tr>
                                <tr><td class="feature-col">Seating Capacity</td>%s</tr>
                                <tr><td class="feature-col">Fuel Tank</td>%s</tr>
                                
                                <tr class="group-header"><td colspan="%d">Engine & Performance</td></tr>
                                <tr><td class="feature-col">Engine</td>%s</tr>
                                <tr><td class="feature-col">Fuel Type</td>%s</tr>
                                <tr><td class="feature-col">Fuel Economy</td>%s</tr>
                                <tr><td class="feature-col">Max Power</td>%s</tr>
                                <tr><td class="feature-col">Max Torque</td>%s</tr>
                                <tr><td class="feature-col">Transmission</td>%s</tr>
                                <tr><td class="feature-col">Drive Type</td>%s</tr>
                                
                                <tr class="group-header"><td colspan="%d">Safety & Price</td></tr>
                                <tr><td class="feature-col">Safety Features</td>%s</tr>
                                <tr><td class="feature-col">Price Range</td>%s</tr>
                            </tbody>
                        </table>
                    </div>
                </section>
                """.formatted(
                        authLinks, 
                        tableHeader.toString(),
                        specs.size() + 1,
                        bodyRow.toString(), dimRow.toString(), wheelRow.toString(), clearanceRow.toString(), weightRow.toString(), seatsRow.toString(), tankRow.toString(),
                        specs.size() + 1,
                        engineRow.toString(), fuelTypeRow.toString(), econRow.toString(), maxPowerRow.toString(), maxTorqueRow.toString(), transRow.toString(), driveRow.toString(),
                        specs.size() + 1,
                        safetyRow.toString(), priceRow.toString()
                ));
    }
}
