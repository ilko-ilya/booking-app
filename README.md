# "EasyStay! 🏡✨"

Welcome to EasyStay: our booking app that will simplify your housing search. Discover the utmost ease in finding rental
accommodations with EasyStay! Our platform is designed to make the search for rental housing effortless and
user-friendly, focusing on your needs from start to finish.

### User-Focused Approach

EasyStay adopts a user-centric approach to ensure that your housing search is not only simplified but also enjoyable.
Our platform prioritizes your comfort and convenience, making every step of the process hassle-free.

### Intuitive Interface

Navigate through our intuitive interface, designed to make your search for rental properties convenient and stress-free.
From registration to finding and booking your ideal accommodation, we've crafted every step with your comfort in mind.

### Comprehensive Property Information

Access detailed information about each property effortlessly. Learn about property types, locations, sizes, amenities,
daily rates, and real-time availability. We believe that informed decisions lead to satisfied tenants, and our platform
is here to empower you with all the necessary details.

### Administrative Ease

For property administrators, our platform provides a powerful toolkit for efficient housing inventory management.
Adding, updating, and removing properties is simple, with real-time availability information at your disposal. Say
goodbye to paperwork and hello to a more streamlined administrative process.

### Your Rental Journey, Your Control

Take control of your booking experience with EasyStay. From creating and managing bookings to making modifications, we
put the power in your hands. Our platform ensures that the rental process is transparent and user-friendly.

EasyStay – where your housing search becomes easy, efficient, and enjoyable. Thank you for choosing us to simplify your
rental journey! 🏡✨

## Ok, let's get started
### Follow these simple steps, and you'll know how to use this project:
#### 1. Before we started you make sure that you have installed:
- Java 17
- Docker 

#### 2. You have to clone repository

  ``` 
  https://github.com/ilko-ilya/booking-app
  ``` 
#### 3. Create Environment File in root directory

  ``` 
  .env
  ``` 
#### 4. Build and run Docker container
  ``` 
  docker-compose build
  docker-compose up
  ``` 
   These commands will build and run Docker container for your application

#### 5. Access the Application
Once the container is running, you can access the EasyStay application in your web browser. By default, it should be available at http://localhost:8081.

#### Now you're all set to explore the full functionality of EasyStay! Continue reading to discover the diverse features our application offers. If you have any questions or run into issues, feel free to ask. Happy booking! 🏡✨
      
## Functionality

### For Non-Authenticated Users

- Registration: Create a new user account by providing essential details such as email, password, repeat password, first
  name, and last name.
  ``` 
  POST: /api/auth/register
  ``` 

#### Example JSON registration request body:

    ```
    {
      "email": "john.doe@gmail.com",
      "password": "password123",
      "repeatPassword": "password123",
      "firstName": "John",
      "lastName": "Doe"
    }

- Login: Log in to the platform using your registered email and password.

   ```
   POST: /api/auth/login
   ```

#### Example JSON login request body:

    ```
    {
      "email": "john.doe@gmail.com",
      "password": "password123"
    }
    ```

### For Users with role CUSTOMER

- Browse Accommodations: Explore a list of available accommodations on the platform.

   ```
   GET: /api/accommodations
   ```

- Get Accommodation Details: View detailed information about a specific accommodation.

   ```
   GET: /api/accommodations/{id}
   ```
- Make a Booking: Create a new booking for a selected accommodation.

   ```
   POST: /api/bookings
   ```
  #### Example JSON booking request body:
  ```
  {
    "accommodationId": 123,
    "checkInDate": "2024-04-01",
    "checkOutDate": "2024-04-10"
  }
  ```
  
- View Bookings: Check a list of your previous bookings.

   ```
   GET: /api/bookings/my
   ```
- View Booking Details: Review detailed information about a specific booking.

   ```
   GET: /api/bookings/{id}
   ```
  
- Modify Booking: Update details of a specific booking.

   ```
   PUT: /api/bookings/{id}
   ```
   #### Example JSON booking update request body:

  ```
  {
    "checkInDate": "2024-04-05",
    "checkOutDate": "2024-04-15",
    "status": "CONFIRMED"
  }
  ```
  
- Cancel Booking: Cancel a booking.

   ```
   DELETE: /api/bookings/{id}
   ```
  
- View Payments: Check a list of your payments.

   ```
   GET: /api/payments/my
   ```

### For Users with Role MANAGER

- Add New Accommodation: Add new accommodation to the platform with details like type, location, size, amenities, daily rate, and availability.

   ```
   POST: /api/accommodations
   ```
  
- Update Accommodation Details: Modify details of a specific accommodation.

   ```
   PUT: /api/accommodations/{id}
   ```
- Delete Accommodation: Remove accommodation from the platform.

   ```
   DELETE: /api/accommodations/{id}
   ```
- Modify Accommodation: Update details of a specific accommodation.

   ```
   PATCH: /api/accommodations/{id}
   ```
  
- View All Bookings: Check a list of all bookings on the platform.

   ```
   GET: /api/bookings
   ```
- View Booking Details: Review detailed information about a specific booking.

   ```
   GET: /api/bookings/{id}
   ```
  
- View Payments: Check a list of all payments on the platform.

   ```
   GET: /api/payments
   ```

Explore these functionalities to make the most of your EasyStay experience! Happy booking! 🏡✨


# Technologies:
- Java 17
- Spring Boot, Spring Security, Spring data JPA
- REST, Mapstruct
- MySQL, Liquibase
- Maven, Docker
- Lombok, Swagger
- Junit, Mockito, testcontainers
- Stripe API
- Telegram API

## Closing Notes
Thank you for choosing EasyStay for your accommodation search! We aim to make your booking experience as convenient and enjoyable as possible.

If you have any questions, suggestions, or feedback, feel free to contact our support team:

Email: support@easystay.com
Phone: +123 456 7890
We also invite you to share your EasyStay experience and leave a review. Your feedback is valuable to us!

Thank you for your choice, and happy booking with EasyStay! 🏡✨
