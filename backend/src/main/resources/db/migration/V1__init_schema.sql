/* ============================================
   ShoeStore – Init Schema (Oracle)
   V1__init_schema.sql
   ============================================ */

------------------------------------------------
-- 1. USERS
------------------------------------------------
CREATE TABLE USERS (
                       USERID        NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                       EMAIL         NVARCHAR2(255)  NOT NULL,
                       PASSWORDHASH  NVARCHAR2(255)  NOT NULL,
                       FULLNAME      NVARCHAR2(100)  NOT NULL,
                       PHONE         NVARCHAR2(20),
                       CREATEDAT     TIMESTAMP       NOT NULL,
                       LASTLOGIN     TIMESTAMP,
                       ISACTIVE      NUMBER(1)       NOT NULL,
                       CONSTRAINT PK_USERS PRIMARY KEY (USERID)
);

CREATE UNIQUE INDEX IX_USERS_EMAIL ON USERS(EMAIL);

------------------------------------------------
-- 2. ROLES + USERROLES
------------------------------------------------
CREATE TABLE ROLES (
                       ROLEID    NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                       ROLENAME  NVARCHAR2(100) NOT NULL,
                       CONSTRAINT PK_ROLES PRIMARY KEY (ROLEID)
);

CREATE UNIQUE INDEX IX_ROLES_ROLENAME ON ROLES(ROLENAME);

CREATE TABLE USERROLES (
                           USERID      NUMBER(10)  NOT NULL,
                           ROLEID      NUMBER(10)  NOT NULL,
                           ASSIGNEDAT  TIMESTAMP   NOT NULL,
                           CONSTRAINT PK_USERROLES PRIMARY KEY (USERID, ROLEID)
);

CREATE INDEX IX_USERROLES_USERID ON USERROLES(USERID);
CREATE INDEX IX_USERROLES_ROLEID ON USERROLES(ROLEID);

------------------------------------------------
-- 3. CATEGORIES
------------------------------------------------
CREATE TABLE CATEGORIES (
                            CATEGORYID    NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                            CATEGORYNAME  NVARCHAR2(50)   NOT NULL,
                            DESCRIPTION   NVARCHAR2(255),
                            IMAGEURL      NVARCHAR2(500),
                            CONSTRAINT PK_CATEGORIES PRIMARY KEY (CATEGORYID)
);

CREATE UNIQUE INDEX IX_CATEGORIES_NAME ON CATEGORIES(CATEGORYNAME);

------------------------------------------------
-- 4. PRODUCTS
------------------------------------------------
CREATE TABLE PRODUCTS (
                          PRODUCTID       NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                          PRODUCTNAME     NVARCHAR2(100)  NOT NULL,
                          SLUG            NVARCHAR2(150)  NOT NULL,
                          DESCRIPTION     CLOB,
                          PRICE           NUMBER(12, 2)   NOT NULL,
                          PROMOTIONPRICE  NUMBER(12, 2),
                          CATEGORYID      NUMBER(10)      NOT NULL,
                          RATING          NUMBER(3, 2),
                          ISACTIVE        NUMBER(1)       NOT NULL,
                          CREATEDAT       TIMESTAMP       NOT NULL,
                          CONSTRAINT PK_PRODUCTS PRIMARY KEY (PRODUCTID)
);

CREATE UNIQUE INDEX IX_PRODUCTS_SLUG ON PRODUCTS(SLUG);
CREATE INDEX IX_PRODUCTS_CATEGORYID ON PRODUCTS(CATEGORYID);

------------------------------------------------
-- 5. PRODUCT VARIANTS
------------------------------------------------
CREATE TABLE PRODUCTVARIANTS (
                                 VARIANTID      NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                                 PRODUCTID      NUMBER(10)      NOT NULL,
                                 SIZEVALUE      NVARCHAR2(10)   NOT NULL,
                                 COLORNAME      NVARCHAR2(250)  NOT NULL,
                                 SKU            NVARCHAR2(250)  NOT NULL,
                                 STYLECOLOR     NVARCHAR2(50),
                                 STOCKQTY       NUMBER(10)      NOT NULL,
                                 PRICEOVERRIDE  NUMBER(12, 2),
                                 ISACTIVE       NUMBER(1)       NOT NULL,
                                 CONSTRAINT PK_PRODUCTVARIANTS PRIMARY KEY (VARIANTID)
);

CREATE UNIQUE INDEX UQ_PRODUCTVARIANTS_PROD_SIZE_COLOR
    ON PRODUCTVARIANTS (PRODUCTID, SIZEVALUE, COLORNAME);

CREATE UNIQUE INDEX IX_PRODUCTVARIANTS_SKU ON PRODUCTVARIANTS(SKU);

------------------------------------------------
-- 6. PRODUCT IMAGES
------------------------------------------------
CREATE TABLE PRODUCTIMAGES (
                               IMAGEID       NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                               PRODUCTID     NUMBER(10)      NOT NULL,
                               IMAGEURL      NVARCHAR2(500)  NOT NULL,
                               DISPLAYORDER  NUMBER(10)      NOT NULL,
                               CONSTRAINT PK_PRODUCTIMAGES PRIMARY KEY (IMAGEID)
);

CREATE INDEX IX_PRODUCTIMAGES_PRODUCTID ON PRODUCTIMAGES(PRODUCTID);

------------------------------------------------
-- 7. REVIEWS
------------------------------------------------
CREATE TABLE REVIEWS (
                         REVIEWID    NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                         PRODUCTID   NUMBER(10)      NOT NULL,
                         USERID      NUMBER(10)      NOT NULL,
                         RATING      NUMBER(10)      NOT NULL,
                         COMMENT     CLOB,
                         REVIEWDATE  TIMESTAMP       NOT NULL,
                         CONSTRAINT PK_REVIEWS PRIMARY KEY (REVIEWID)
);

CREATE UNIQUE INDEX UQ_REVIEWS_PRODUCT_USER
    ON REVIEWS (PRODUCTID, USERID);

------------------------------------------------
-- 8. ADDRESSES
------------------------------------------------
CREATE TABLE ADDRESSES (
                           ADDRESSID   NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                           USERID      NUMBER(10)      NOT NULL,
                           FULLNAME    NVARCHAR2(100)  NOT NULL,
                           PHONE       NVARCHAR2(20)   NOT NULL,
                           TENDUONG    NVARCHAR2(100)  NOT NULL,
                           XAQUAN      NVARCHAR2(100)  NOT NULL,
                           TINHTHANH   NVARCHAR2(100)  NOT NULL,
                           ISDEFAULT   NUMBER(1)       NOT NULL,
                           CONSTRAINT PK_ADDRESSES PRIMARY KEY (ADDRESSID)
);

CREATE INDEX IX_ADDRESSES_USERID ON ADDRESSES(USERID);

------------------------------------------------
-- 9. ORDERS
------------------------------------------------
CREATE TABLE ORDERS (
                        ORDERID            NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                        USERID             NUMBER(10)      NOT NULL,
                        ORDERDATE          TIMESTAMP       NOT NULL,
                        TOTALAMOUNT        NUMBER(12, 2)   NOT NULL,
                        ORDERSTATUS        NVARCHAR2(50),
                        SHIPPINGNAME       NVARCHAR2(100)  NOT NULL,
                        SHIPPINGPHONE      NVARCHAR2(20)   NOT NULL,
                        SHIPPINGTENDUONG   NVARCHAR2(255)  NOT NULL,
                        SHIPPINGXAQUAN     NVARCHAR2(100)  NOT NULL,
                        SHIPPINGTINHTHANH  NVARCHAR2(100)  NOT NULL,
                        CONSTRAINT PK_ORDERS PRIMARY KEY (ORDERID)
);

CREATE INDEX IX_ORDERS_USERID ON ORDERS(USERID);

------------------------------------------------
-- 10. ORDER ITEMS
------------------------------------------------
CREATE TABLE ORDERITEMS (
                            ORDERITEMID  NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                            ORDERID      NUMBER(10)      NOT NULL,
                            VARIANTID    NUMBER(10)      NOT NULL,
                            QUANTITY     NUMBER(10)      NOT NULL,
                            UNITPRICE    NUMBER(12, 2)   NOT NULL,
                            SUBTOTAL     NUMBER(18, 2)   NOT NULL,
                            CONSTRAINT PK_ORDERITEMS PRIMARY KEY (ORDERITEMID)
);

CREATE INDEX IX_ORDERITEMS_ORDERID ON ORDERITEMS(ORDERID);
CREATE INDEX IX_ORDERITEMS_VARIANTID ON ORDERITEMS(VARIANTID);

------------------------------------------------
-- 11. CART
------------------------------------------------
CREATE TABLE CART (
                      CARTID     NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                      USERID     NUMBER(10)      NOT NULL,
                      VARIANTID  NUMBER(10)      NOT NULL,
                      QUANTITY   NUMBER(10)      NOT NULL,
                      ADDEDAT    TIMESTAMP       NOT NULL,
                      CONSTRAINT PK_CART PRIMARY KEY (CARTID)
);

CREATE UNIQUE INDEX UQ_CART_USER_VARIANT
    ON CART (USERID, VARIANTID);

------------------------------------------------
-- 12. PASSWORD RESET TOKENS
------------------------------------------------
CREATE TABLE PASSWORDRESETTOKENS (
                                     TOKENID    NUMBER(10) GENERATED ALWAYS AS IDENTITY,
                                     EMAIL      NVARCHAR2(255)  NOT NULL,
                                     CODE       NVARCHAR2(6)    NOT NULL,
                                     CREATEDAT  TIMESTAMP       NOT NULL,
                                     EXPIRESAT  TIMESTAMP       NOT NULL,
                                     ISUSED     NUMBER(1)       NOT NULL,
                                     USEDAT     TIMESTAMP,
                                     CONSTRAINT PK_PASSWORDRESETTOKENS PRIMARY KEY (TOKENID)
);

CREATE INDEX IX_PRT_EMAIL ON PASSWORDRESETTOKENS(EMAIL);

------------------------------------------------
-- FOREIGN KEYS
------------------------------------------------

-- Addresses → Users
ALTER TABLE ADDRESSES
    ADD CONSTRAINT FK_ADDRESSES_USERS_USERID
        FOREIGN KEY (USERID) REFERENCES USERS(USERID)
            ON DELETE CASCADE;

-- Cart → Users, ProductVariants
ALTER TABLE CART
    ADD CONSTRAINT FK_CART_USERS_USERID
        FOREIGN KEY (USERID) REFERENCES USERS(USERID)
            ON DELETE CASCADE;

ALTER TABLE CART
    ADD CONSTRAINT FK_CART_PRODUCTVARIANTS_VARIANTID
        FOREIGN KEY (VARIANTID) REFERENCES PRODUCTVARIANTS(VARIANTID);

-- ProductVariants → Products
ALTER TABLE PRODUCTVARIANTS
    ADD CONSTRAINT FK_PRODUCTVARIANTS_PRODUCTS_PRODUCTID
        FOREIGN KEY (PRODUCTID) REFERENCES PRODUCTS(PRODUCTID)
            ON DELETE CASCADE;

-- Products → Categories
ALTER TABLE PRODUCTS
    ADD CONSTRAINT FK_PRODUCTS_CATEGORIES_CATEGORYID
        FOREIGN KEY (CATEGORYID) REFERENCES CATEGORIES(CATEGORYID);

-- ProductImages → Products
ALTER TABLE PRODUCTIMAGES
    ADD CONSTRAINT FK_PRODUCTIMAGES_PRODUCTS_PRODUCTID
        FOREIGN KEY (PRODUCTID) REFERENCES PRODUCTS(PRODUCTID)
            ON DELETE CASCADE;

-- Reviews → Products, Users
ALTER TABLE REVIEWS
    ADD CONSTRAINT FK_REVIEWS_PRODUCTS_PRODUCTID
        FOREIGN KEY (PRODUCTID) REFERENCES PRODUCTS(PRODUCTID)
            ON DELETE CASCADE;

ALTER TABLE REVIEWS
    ADD CONSTRAINT FK_REVIEWS_USERS_USERID
        FOREIGN KEY (USERID) REFERENCES USERS(USERID);

-- Orders → Users
ALTER TABLE ORDERS
    ADD CONSTRAINT FK_ORDERS_USERS_USERID
        FOREIGN KEY (USERID) REFERENCES USERS(USERID);

-- OrderItems → Orders, ProductVariants
ALTER TABLE ORDERITEMS
    ADD CONSTRAINT FK_ORDERITEMS_ORDERS_ORDERID
        FOREIGN KEY (ORDERID) REFERENCES ORDERS(ORDERID)
            ON DELETE CASCADE;

ALTER TABLE ORDERITEMS
    ADD CONSTRAINT FK_ORDERITEMS_PRODUCTVARIANTS_VARIANTID
        FOREIGN KEY (VARIANTID) REFERENCES PRODUCTVARIANTS(VARIANTID);

-- UserRoles → Users, Roles
ALTER TABLE USERROLES
    ADD CONSTRAINT FK_USERROLES_USERS_USERID
        FOREIGN KEY (USERID) REFERENCES USERS(USERID)
            ON DELETE CASCADE;

ALTER TABLE USERROLES
    ADD CONSTRAINT FK_USERROLES_ROLES_ROLEID
        FOREIGN KEY (ROLEID) REFERENCES ROLES(ROLEID)
            ON DELETE CASCADE;
