<h1><img width="100" height="100" alt="logo" src="https://github.com/user-attachments/assets/713a5366-1085-44c7-8767-1b5dd1bf90d4" />

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

<h1> Screenshots</h1>
<img width="1282" height="892" alt="Screenshot 2025-08-02 180140" src="https://github.com/user-attachments/assets/b49cfa74-cb25-424a-9896-8f89680b50d1" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180149" src="https://github.com/user-attachments/assets/47a672bf-9161-44b0-bfe3-aa1027ee58e6" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 185724" src="https://github.com/user-attachments/assets/38cfdf9b-b82f-45bc-8bcc-c224b3856bef" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180155" src="https://github.com/user-attachments/assets/ea69d8ef-e573-480d-914b-2fd42cbfc7a7" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 181606" src="https://github.com/user-attachments/assets/95b38cf4-c21f-4a4b-95be-15f4cde8d71f" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180252" src="https://github.com/user-attachments/assets/05d38136-e96a-4253-a9b3-cbb105920a23" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180640" src="https://github.com/user-attachments/assets/42b43918-2fa6-410b-ad64-a9a420d34097" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180646" src="https://github.com/user-attachments/assets/26c6a595-9ca3-405e-9503-381e1a9d5b29" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180659" src="https://github.com/user-attachments/assets/b6e247ff-cdc8-477f-b158-be17b3ea80a6" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180717" src="https://github.com/user-attachments/assets/10db90c1-fb42-4668-ad39-335f5c57a2c6" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180721" src="https://github.com/user-attachments/assets/6b31146b-531f-4a79-b9c2-ac83201ca622" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180727" src="https://github.com/user-attachments/assets/777235a1-6aa9-429c-87f2-55f176ca63b2" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180733" src="https://github.com/user-attachments/assets/9c0c1cd1-6cc3-4d98-95f8-58e5340a533b" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180748" src="https://github.com/user-attachments/assets/ab9a4cfb-0760-42f3-a477-8d69018bef4c" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180948" src="https://github.com/user-attachments/assets/a9b42439-86be-48bd-8d27-3eb58ba7d363" />
<img width="1282" height="892" alt="Screenshot 2025-08-02 180827" src="https://github.com/user-attachments/assets/cf0089e5-4d1a-4baa-bba4-254aa05ed79d" />



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
