(ns cloudseed.models.shopping
  "catalog, shopping card, orders"
  (:import [java.io IOException]
           [java.util.zip  DataFormatException])
  (:require [cloudseed.config :refer [config]]
            [clj-webdriver.taxi :as scraper :refer [set-driver! to click exists? input-text submit quit page-source get-url find-elements]]
            [clojure.core.async :as async :refer :all]
            [clojure.core.match :refer [match]]
            [korma.db :refer [defdb mysql]]
            [korma.core :refer [defentity database insert values has-one pk select where join with many-to-many
                                table delete limit exec-raw]]))

(declare shopper cart product cart-product purchase-order purchase-order purchase-order-product)

;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;; DATABASE CONNECTION
;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
(def shop-dbh (mysql {:db (:shopping-db-name config) :user (:shopping-db-user config)
                      :password (:shopping-db-password config) :host (:shopping-db-address config)}))

;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;; ORM TABLE WRAPPERs
;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
(defentity shopper
  (database shop-dbh)
  (has-one cart))

(defentity product
  (database shop-dbh))

(defentity cart-product
  (table :cart_product)
  (database shop-dbh))

(defentity cart
  (database shop-dbh)
  (pk :id)
  (many-to-many  product :cart_product))

(defentity purchase-order
  (database shop-dbh)
  (table :purchase_order)
  (has-one shopper)
  (many-to-many  product :purchase_order_product))

(defentity purchase-order-product
  (table :purchase_order_product)
  (database shop-dbh))

;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;; TRANSACTION FUNCTIONS
;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
(defn calculate-tax
  [taxable-amount]
  (let [tax-rate 0.05]
    (* taxable-amount tax-rate)))

(defn calculate-discount
  [tax sub-total]
  (let [discount-rate 1]
    (* sub-total discount-rate)))

(defn calculate-shipping
  "This is going to need to hit some shipping web service"
  [products to-address] 1)

;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;; CART MANAGEMENT FUNCTIONS
;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
(defn add-to-cart [product-id cart-id]
   (insert cart-product (values [{:product_id product-id :cart_id cart-id}])))

(defn remove-from-cart [product-id cart-id quantity]
  (exec-raw  shop-dbh  ["delete from cart_product where product_id = ? and cart_id =? limit ?" [product-id, cart-id, quantity]]))

(defn shopping-cart
  "Return the contents and relevent transaction totals from the current state of the shopping cart"
  [shopper-id]
  (let [model (select shopper (where {:id shopper-id}) (with cart (with product)) )
        products  (:product (first model))
        sub-total (apply + (map :price products))
        tax (calculate-tax sub-total)
        discounts (calculate-discount tax sub-total)]
    {:cart-id  (:cart_id (first (select shopper (where {:id shopper-id}) (with cart))))
     :products products
     :sub-total sub-total
     :tax tax
     :total (- (+ tax sub-total) discounts)}))

;; TODO add logic to make sure a shopper can only have one cart
(defn new-shopping-cart
  "Create an empty shopping cart for the user"
  [shopper-id]
  {:cart-id (:GENERATED_KEY (insert cart (values  {:shopper_id shopper-id})))
   :products []
   :total 0
   :sub-total 0
   :tax 0})

;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;; CHECKOUT FUNCTIONS
;;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
(defn login-to-general-ledger
  "Creates a real web browser instance logged into the general ledger web application"
  [& {:keys [driver ledger-username ledger-password ledger-login-uri ledger-host]
      :or {driver (scraper/new-driver :browser :htmlunit (format "http://%s/sql-ledger/login.pl") ledger-host)}}]
    (do (scraper/set-driver! driver)
        (scraper/to ledger-login-uri)
        (-> (scraper/element "input[name*='login']")
            (scraper/input-text ledger-username))
        (-> (scraper/element "input[name*='password']")
            (scraper/input-text ledger-password))
        (scraper/click "input[type*='submit']"))
    driver)

(defn create-ledger-customer
  "Using an actual web browser drive a general ledger web application so as to add a new billable customer"
  [& {:keys [customer ledger-host ledger-username ledger-password driver]
      :as args
      :or {driver (login-to-general-ledger args)}}]
  (let [new-customer-uri (format "http://%s/sql-ledger/ct.pl?path=bin/mozilla&action=add&level=Customers--Add%20Customer&login=%s&js=1&db=customer" ledger-host ledger-username)]
    (do (scraper/to driver new-customer-uri)
        (scraper/input-text driver (first (scraper/find-elements driver {:xpath "/html/body/form/table/tbody/tr[2]/td/table/tbody/tr[3]/td[1]/table/tbody/tr[2]/td/input"})) (:username customer))
        (scraper/click driver (first (scraper/find-elements driver {:xpath "/html/body/form/table/tbody/tr[2]/td/table/tbody/tr[3]/td[1]/table/tbody/tr[2]/td/input"}))))
    driver))

(defn email-invoice
  "Mail an invoice to a user who just purchased the content of their shopping cart online"
  [& {:keys [invoice email-subject email-body driver username password ledger-host driver]
      :as args
      :or {driver (login-to-general-ledger args)}}]
  (let [email-invoice-uri (format  "http://%s/sql-ledger/bp.pl?path=bin/mozilla&action=search&level=Batch--Email--Sales%20Invoices&login=%s&js=1&vc=customer&batch=email&type=invoice" ledger-host username)]
    (do (scraper/to driver email-invoice-uri)
        (scraper/switch-to-frame driver 0)
        (scraper/click   (filter (fn [invoice-id] (= (:text invoice-id) (:id invoice)))  (find-elements {:xpath  "/html/body/form/table[1]/tbody/tr[4]/td/table/tbody/tr[2]/td"})))
        (scraper/input-text driver (first (scraper/find-elements driver {:xpath "/html/body/form/table[2]/tbody/tr[1]/td/input"})) email-subject)
        (scraper/input-text driver (first (scraper/find-elements driver {:xpath "/html/body/form/table[2]/tbody/tr[2]/td/b/textarea"})) email-body)
        (scraper/click driver (first (scraper/find-elements driver {:xpath "/html/body/form/table/tbody/tr[2]/td/table/tbody/tr[3]/td[1]/table/tbody/tr[2]/td/input"}))))
    driver))

(defn create-invoice
  "For a customer create an invoice for a list of products"
  [& {:keys [customer products ledger-host ledger-username ledger-password driver]
      :as args
      :or {driver (login-to-general-ledger args)}}]
  (let [new-invoice-uri "http://ops1.causalmarketing.com/sql-ledger/login.pl"]
    (do (scraper/to new-invoice-uri)
        (scraper/quick-fill {"#first_name" "Rich"}
                            {"a.foo" click}))
    driver))

(defn bill-customer
  "Use the payment gateway to accept payment from customer"
  [])

;; wrapping
;; asynchronous
;; stateful machine i/o type behavior

(defprotocol PointOfSale
  (checkout [this args]))

(defn checkout-shopping-cart
  "Asynchronous control workflow checkout customer's shopping cart using payment, shipping info creating and emailing an invoice.
   Returns the results of the worflow"
  [& {:keys [shopping-cart customer shipping payment ledger-host ledger-username ledger-password driver]
      :as args
      :or {driver (login-to-general-ledger args)}}]
  (let [processing-invoice? (atom {:active true})
        email-channel (chan)
        customer-channel (chan)
        invoice-channel (chan)
        payment-channel (chan)
        results-channel (chan)
        channels [email-channel customer-channel payment-channel invoice-channel]

        ;; state machine transitions
        dispatcher (fn [ch tuple]
                     (let [[msg-token data] (take 2 tuple)
                           msg (merge data args)]
                       (match [msg-token]
                              [:customer-create] (put! invoice-channel (create-ledger-customer msg))
                              [:invoice-create] (put! payment-channel  (create-invoice msg))
                              [:submit-payment] (put! email-channel  (bill-customer  msg))
                              [:email] (put! results-channel (email-invoice msg))
                              [:failure] {:message :rejected}
                              [:email-received] (swap! processing-invoice? :active false))))]

    ;; Run and transistion the state machine
    (go (while processing-invoice?
          (let [[val ch] (alts! channels)]
            (dispatcher ch val))))

    ;; relevant return data to the caller
     (<!! results-channel)))
