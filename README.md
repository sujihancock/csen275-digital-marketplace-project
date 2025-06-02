# CSEN275 Project: Online Digital Marketplace for Handmade Goods

Online marketplace for individuals and small businesses to sell handmade or artisanal goods, with an emphasis on craftsmanship and uniqueness.

1. Product Listings: Sellers can create detailed
   listings of their handmade products, including
   images, descriptions, and prices.

2. Search and Filter: Buyers can search for items
   based on categories (e.g., jewelry, art, clothing) or
   keywords.

3. Secure Payments: Integrate a secure payment
   gateway for transactions, including options like
   credit/debit cards or digital wallets.

4. Seller and Buyer Reviews: Enable users to leave
   reviews on products and sellers to build trust
   within the marketplace.

Group members: Suji Hancock, Cedric Kwong, Zhengxin Tao

## Instructions to run application

Backend:

- `cd digital-marketplace`

  - create .env file in the following format:

    ```
    MYSQL_URL=...
    MYSQL_USER=...
    MYSQL_PASSWORD=...

    STRIPE_API_KEY=sk_test_...
    CLIENT_URL=http://localhost:3000
    ```

- ```bash
  mvn clean
  mvn package
  java -jar target/digital-marketplace-0.0.1-SNAPSHOT.jar
  ```

Frontend:

- `cd frontend`
  - create .env file in the following format:
    (detailed instructions in the frontend/README.md)
    ```
    REACT_APP_STRIPE_PUBLIC_KEY=pk_test...
    ```
- ```bash
   npm start
  ```
