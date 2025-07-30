<h1><img width="75" height="75" alt="logo" src="https://github.com/user-attachments/assets/4ff40bb1-ab0e-423e-bc0b-dc4d40308d92" />
Orange Food - Online Food Delivery & Shopping App</h1>
Orange Food is a full-featured online food delivery and shopping application built with JavaFX (frontend), Hibernate (ORM), and PostgreSQL (database). It offers a seamless experience for buyers, sellers, couriers, and admins, each with a specialized dashboard.

<h1>Key Features</h1>

<h2>🛒 Buyer Dashboard</h2>

Browse restaurants & food items.

Online shopping (add to cart, checkout).

Track orders & delivery status.

View order history and transactions.

<h2>🍽️ Seller Dashboard</h2>

Add, edit, and manage food items.

Process orders & view sales analytics.

<h2>🚚 Courier Dashboard</h2>

Accept & manage delivery assignments.

Update order status in real-time.

<h2>👑 Admin Dashboard</h2>

Manage users, restaurants, and platform settings.

Monitor transactions & system performance.

<h2>Core Functionalities
<h3>🛍️ Online Shopping & Food Delivery

Browse menus, add items to cart, and place orders.

<h3>💬 Share Comments & Images

Leave reviews & upload food photos.

<h3>💰 Internal Wallet

Secure in-app payments & refunds.

<h3>📊 Order & Transaction History

Track past orders & financial transactions.

Technologies Used
Frontend: JavaFX

Backend: Java

ORM: Hibernate

Database: PostgreSQL

<h1>Developers</h1>
<h3>Mahan Pourkami

<h3>Parsa Samareh Afsari

<h1> Screenshot</h1>
<img width="1282" height="892" alt="Screenshot 2025-07-09 202101" src="https://github.com/user-attachments/assets/a3a22912-53f9-45d5-94f3-a7d662535f25" />
<img width="1282" height="892" alt="Screenshot 2025-07-09 202119" src="https://github.com/user-attachments/assets/191a556e-d29a-4837-9061-7f2de19f8db9" />
<img width="1282" height="892" alt="Screenshot 2025-07-19 150230" src="https://github.com/user-attachments/assets/f219708f-7262-4e6c-bd94-9c6c3d6969af" />
<img width="1282" height="892" alt="Screenshot 2025-07-24 191007" src="https://github.com/user-attachments/assets/96057165-6712-4fc8-b43c-de0febee30a1" />
<img width="1282" height="892" alt="Screenshot 2025-07-24 191119" src="https://github.com/user-attachments/assets/82a417dd-93cf-4fab-8676-0d505791b1ff" />
<img width="1282" height="892" alt="Screenshot 2025-07-21 004123" src="https://github.com/user-attachments/assets/a00f0fe4-5916-4663-94db-c3f292b4c87b" />
<img width="1282" height="892" alt="Screenshot 2025-07-19 145727" src="https://github.com/user-attachments/assets/fd2e92b7-9c0b-42e5-81d7-e6210cfa8593" />
<img width="1282" height="892" alt="Screenshot 2025-07-24 191509" src="https://github.com/user-attachments/assets/2a74b35c-0118-4e1a-86ba-22f2b89d1d3c" />


<h2>Installation & Setup</h2>
Prerequisites:

Java JDK 17+

PostgreSQL 13+

Maven (for dependency management)

Database Setup:

Create a PostgreSQL database.

Update hibernate.cfg.xml with your DB credentials.

```sh
mvn clean install  
java -jar target/orangefood-app.jar  
```
and also you need to adjust the configuration of the hibernate configuration file by according to your db's setting

```xml
  <session-factory>
    <!-- Required PostgreSQL connection settings -->
    <property name="jakarta.persistence.jdbc.driver">org.DB NAME.Driver</property>
    <property name="jakarta.persistence.jdbc.url">jdbc:postgresql://localhost:PORT/DB NAME</property>
    <property name="jakarta.persistence.jdbc.user">USERNAME</property>
    <property name="jakarta.persistence.jdbc.password">PASS</property>
    <property name="hibernate.dialect">org.hibernate.dialect.SQLDialect</property>
```
