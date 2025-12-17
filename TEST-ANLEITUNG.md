# Test-Anleitung: Alle Aufgaben ausführen und testen

## Übersicht
Diese Anleitung zeigt dir **genau** welche Befehle du für jede Aufgabe ausführen musst.

---

## Voraussetzungen prüfen

```bash
# Java Version prüfen (muss 17+ sein)
java -version

# Maven Version prüfen
mvn -version

# Docker prüfen
docker --version
docker compose version
```

---

## 🔧 AUFGABE 1: Docker Compose Setup (1 Punkt)

### Befehle ausführen:

```bash
# 1. Docker Services starten
docker compose up -d

# 2. Status prüfen (beide sollten "healthy" sein)
docker ps

# 3. Logs prüfen (optional)
docker compose logs mysql
docker compose logs redis

# 4. MySQL Verbindung testen
docker exec va-mysql mysql -uroot -pgeheim -e "SHOW DATABASES;"

# 5. Redis Verbindung testen
docker exec va-redis redis-cli ping
# Ausgabe sollte sein: PONG
```

### ✅ Erfolg prüfen:
- MySQL Container läuft und Status ist "healthy"
- Redis Container läuft und Status ist "healthy"
- Datenbank VA_APP existiert
- Redis antwortet mit "PONG"

---

## 🔧 AUFGABE 2: Bean Validation (2 Punkte)

### Befehle ausführen:

```bash
# 1. Validation-Tests ausführen
mvn test -Dtest=BasketResourceTest#testValidation*

# Oder alle Basket-Tests:
mvn test -Dtest=BasketResourceTest
```

### ✅ Erfolg prüfen:
Alle 5 Validierungstests bestehen:
- `testValidationProductNameTooLong` ✅
- `testValidationInvalidProductIdFormat` ✅
- `testValidationPriceTooLow` ✅
- `testValidationPriceTooHigh` ✅
- `testValidationValidItem` ✅

### Manuell testen (optional):

```bash
# Projekt starten
mvn compile quarkus:dev

# In neuem Terminal - Ungültiger Preis testen:
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Test Product",
    "productId": "1-2-3-4-5-6",
    "count": 1,
    "price": 150.0
  }'
# Erwartet: HTTP 400 (Preis zu hoch)

# Gültigen Artikel testen:
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Test Product",
    "productId": "1-2-3-4-5-6",
    "count": 1,
    "price": 50.0
  }'
# Erwartet: HTTP 201 (Erfolg)
```

---

## 🔧 AUFGABE 3: Warenkorb-Funktionalität mit Redis (3 Punkte)

### Befehle ausführen:

```bash
# 1. Alle Warenkorb-Tests ausführen
mvn test -Dtest=BasketResourceTest

# 2. Projekt starten (für manuelle Tests)
mvn compile quarkus:dev
```

### Manuell testen (in neuem Terminal):

```bash
# A) Artikel zum Warenkorb hinzufügen
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "productId": "1-2-3-4-5-6",
    "count": 1,
    "price": 50.0
  }'
# Erwartet: HTTP 201 + JSON mit Warenkorb und remainingBalance

# B) Warenkorb abrufen
curl -X GET "http://localhost:8080/basket" \
  -H "X-User-Id: 1"
# Erwartet: HTTP 200 + JSON mit items, total, remainingBalance

# C) Artikelanzahl ändern
curl -X PATCH "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "count": 2
  }'
# Erwartet: HTTP 200 + aktualisierter Warenkorb

# D) Artikel entfernen
curl -X DELETE "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1"
# Erwartet: HTTP 200 + Warenkorb ohne den Artikel

# E) Warenkorb leeren
curl -X DELETE "http://localhost:8080/basket" \
  -H "X-User-Id: 1"
# Erwartet: HTTP 204 (No Content)
```

### NEU: Produkt mehrmals hinzufügen ✨

```bash
# Warenkorb leeren
curl -X DELETE "http://localhost:8080/basket" -H "X-User-Id: 1"

# 1. Erstes Hinzufügen: count=1
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Laptop","productId":"1-2-3-4-5-6","count":1,"price":50.0}'
# Ergebnis: count=1, total=50.0

# 2. Zweites Hinzufügen: count=2 wird addiert
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Laptop","productId":"1-2-3-4-5-6","count":2,"price":50.0}'
# Ergebnis: count=3 (1+2), total=150.0

# 3. Drittes Hinzufügen: count=1 wird addiert
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Laptop","productId":"1-2-3-4-5-6","count":1,"price":50.0}'
# Ergebnis: count=4 (3+1), total=200.0

# Oder verwende das Test-Script:
chmod +x test-mehrfach-hinzufuegen.sh
./test-mehrfach-hinzufuegen.sh
```

### User-Isolation testen:

```bash
# User 1 fügt Artikel hinzu
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Artikel A","productId":"1-2-3-4-5-6","count":1,"price":20.0}'

# User 2 sieht den Artikel von User 1 NICHT
curl -X GET "http://localhost:8080/basket" \
  -H "X-User-Id: 2"
# Erwartet: Leerer Warenkorb (items: [])
```

### TTL (2 Minuten) testen:

```bash
# 1. Artikel hinzufügen
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Test","productId":"1-2-3-4-5-6","count":1,"price":20.0}'

# 2. TTL in Redis prüfen
docker exec va-redis redis-cli TTL basket:user:1
# Erwartet: ~120 (Sekunden)

# 3. Nach 2 Minuten Warenkorb abrufen
sleep 120
curl -X GET "http://localhost:8080/basket" -H "X-User-Id: 1"
# Erwartet: Leerer Warenkorb (automatisch gelöscht)
```

### Balance-Prüfung testen:

```bash
# User 5 hat nur 0.11 EUR Balance (siehe liquibase-changelog.xml)
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-7" \
  -H "X-User-Id: 5" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Teuer","productId":"1-2-3-4-5-7","count":1,"price":50.0}'
# Erwartet: HTTP 400 (Insufficient balance)
```

### ✅ Erfolg prüfen:
- Alle CRUD-Operationen funktionieren ✅
- User können nur eigenen Warenkorb sehen ✅
- Warenkorb wird nach 2 Minuten gelöscht ✅
- Artikel können nicht hinzugefügt werden wenn Balance zu niedrig ✅

---

## 🔧 AUFGABE 4: Liquibase Schema für Bestellungen (1 Punkt)

### Befehle ausführen:

```bash
# 1. Projekt bauen (Liquibase läuft automatisch)
mvn clean compile

# 2. Datenbank-Schema prüfen
docker exec va-mysql mysql -uroot -pgeheim VA_APP -e "SHOW TABLES;"
# Erwartet: DATABASECHANGELOG, DATABASECHANGELOGLOCK, USER, ORDERS, ORDER_ITEM

# 3. ORDERS Tabelle prüfen
docker exec va-mysql mysql -uroot -pgeheim VA_APP -e "DESCRIBE ORDERS;"

# 4. ORDER_ITEM Tabelle prüfen
docker exec va-mysql mysql -uroot -pgeheim VA_APP -e "DESCRIBE ORDER_ITEM;"

# 5. Foreign Keys prüfen
docker exec va-mysql mysql -uroot -pgeheim VA_APP -e "
  SELECT 
    TABLE_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME 
  FROM 
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
  WHERE 
    TABLE_SCHEMA = 'VA_APP' 
    AND REFERENCED_TABLE_NAME IS NOT NULL;
"
# Erwartet: FK_ORDER_USER (ORDERS -> USER), FK_ITEM_ORDER (ORDER_ITEM -> ORDERS)
```

### ✅ Erfolg prüfen:
- Tabellen ORDERS und ORDER_ITEM existieren ✅
- Foreign Keys sind korrekt ✅
- Liquibase ChangeSet 0.0.3 wurde ausgeführt ✅

---

## 🔧 AUFGABE 5: Bestellungs-Funktionalität (3 Punkte)

### Befehle ausführen:

```bash
# 1. Order-Tests ausführen
mvn test -Dtest=OrderResourceTest

# 2. Checkout-Test ausführen
mvn test -Dtest=BasketResourceTest#testCheckout

# 3. Projekt starten
mvn compile quarkus:dev
```

### Kompletten Checkout-Prozess testen:

```bash
# Schritt 1: Artikel zum Warenkorb hinzufügen
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "productId": "1-2-3-4-5-6",
    "count": 1,
    "price": 50.0
  }'

# Schritt 2: Noch einen Artikel hinzufügen
curl -X POST "http://localhost:8080/basket/2-3-4-5-6-7" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Maus",
    "productId": "2-3-4-5-6-7",
    "count": 2,
    "price": 15.0
  }'

# Schritt 3: Warenkorb prüfen
curl -X GET "http://localhost:8080/basket" \
  -H "X-User-Id: 1"
# Notiere die Balance vor dem Checkout!

# Schritt 4: Checkout durchführen
curl -X POST "http://localhost:8080/basket" \
  -H "X-User-Id: 1"
# Erwartet: HTTP 201 + JSON mit Order

# Schritt 5: Warenkorb prüfen (sollte leer sein)
curl -X GET "http://localhost:8080/basket" \
  -H "X-User-Id: 1"
# Erwartet: items: []

# Schritt 6: Bestellungen abrufen
curl -X GET "http://localhost:8080/orders" \
  -H "X-User-Id: 1"
# Erwartet: HTTP 200 + JSON mit orders Liste + aktualisierte balance
```

### Balance-Belastung prüfen:

```bash
# User 1 hat initial 120.30 EUR (siehe liquibase-changelog.xml)

# 1. Artikel für 50 EUR kaufen
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Test","productId":"1-2-3-4-5-6","count":1,"price":50.0}'

curl -X POST "http://localhost:8080/basket" -H "X-User-Id: 1"

# 2. Balance prüfen
curl -X GET "http://localhost:8080/orders" -H "X-User-Id: 1"
# Erwartet: balance: 70.30 (120.30 - 50.00)
```

### Transaktions-Rollback testen:

```bash
# User 5 hat nur 0.11 EUR - Checkout sollte fehlschlagen

# 1. Artikel hinzufügen (geht, weil Balance noch nicht belastet)
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-8" \
  -H "X-User-Id: 5" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Test","productId":"1-2-3-4-5-8","count":1,"price":10.0}'

# Warte kurz
sleep 1

# 2. Noch einen teuren Artikel hinzufügen
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-9" \
  -H "X-User-Id: 5" \
  -H "Content-Type: application/json" \
  -d '{"productName":"Teuer","productId":"1-2-3-4-5-9","count":1,"price":50.0}'
# Erwartet: HTTP 400 (Insufficient balance)

# 3. Balance in DB prüfen (sollte unverändert sein)
docker exec va-mysql mysql -uroot -pgeheim VA_APP -e "SELECT BALANCE FROM USER WHERE ID=5;"
# Erwartet: 0.11 (keine Änderung wegen Validation)
```

### ✅ Erfolg prüfen:
- Checkout erstellt Bestellung in DB ✅
- Balance wird korrekt belastet ✅
- Warenkorb wird nach Checkout geleert ✅
- GET /orders zeigt alle Bestellungen ✅
- Transaktion ist atomar (alles oder nichts) ✅

---

## 🔧 ALLE TESTS ZUSAMMEN AUSFÜHREN

```bash
# 1. Docker Services starten
docker compose up -d

# 2. Warten bis Services bereit sind
sleep 10

# 3. Alle Tests ausführen
mvn clean test

# Erwartet: Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
```

---

## 🔧 KOMPLETTES PROJEKT BAUEN

```bash
# 1. Docker Services starten
docker compose up -d

# 2. Komplettes Paket bauen
mvn clean package

# 3. Anwendung starten
java -jar target/verteilte-anwendung-runner.jar

# 4. Swagger UI öffnen im Browser:
# http://localhost:8080/swagger
```

---

## 🔍 DEBUGGING / PROBLEME BEHEBEN

### Docker-Probleme:

```bash
# Services neu starten
docker compose down
docker compose up -d

# Logs anschauen
docker compose logs -f

# MySQL zurücksetzen
docker compose down -v
docker compose up -d
```

### Build-Probleme:

```bash
# Maven Cache löschen
mvn clean

# Neu bauen
mvn compile

# Dependency-Tree prüfen
mvn dependency:tree
```

### Test-Probleme:

```bash
# Einzelnen Test ausführen
mvn test -Dtest=BasketResourceTest#testGetBasket

# Mit Debug-Output
mvn test -X -Dtest=BasketResourceTest

# Tests überspringen (nur bauen)
mvn clean package -DskipTests
```

---

## 📊 CHECKLISTE FÜR DEMO

### Vor der Demo:

```bash
☐ Docker Services laufen: docker ps
☐ Build erfolgreich: mvn clean package
☐ Alle Tests bestehen: mvn test
☐ Swagger UI erreichbar: http://localhost:8080/swagger
```

### Während der Demo zeigen:

**Aufgabe 1:**
```bash
☐ docker-compose.yml zeigen
☐ docker ps ausführen (beide Services healthy)
☐ docker exec va-mysql mysql -uroot -pgeheim -e "SHOW DATABASES;"
```

**Aufgabe 2:**
```bash
☐ Item.java Validierungen zeigen (Zeilen 13-30)
☐ mvn test -Dtest=BasketResourceTest#testValidation* ausführen
☐ Alle 5 Tests bestehen
```

**Aufgabe 3:**
```bash
☐ BasketController.java zeigen (getUserBasketKey, setBasketExpiration)
☐ curl Requests demonstrieren (GET, POST, PATCH, DELETE)
☐ User-Isolation zeigen (User 1 vs User 2)
☐ docker exec va-redis redis-cli TTL basket:user:1
```

**Aufgabe 4:**
```bash
☐ liquibase-changelog.xml zeigen (ChangeSet 0.0.3)
☐ OrderEntity.java und OrderItemEntity.java zeigen
☐ docker exec va-mysql mysql -uroot -pgeheim VA_APP -e "SHOW TABLES;"
```

**Aufgabe 5:**
```bash
☐ OrderController.java @Transactional zeigen
☐ Kompletten Checkout-Flow demonstrieren
☐ Balance-Änderung in DB zeigen
☐ GET /orders mit Ergebnis
```

---

## 🎯 QUICK REFERENCE

### Wichtigste Befehle:

```bash
# Docker starten
docker compose up -d

# Projekt bauen
mvn clean package

# Alle Tests
mvn test

# Projekt starten
mvn compile quarkus:dev

# Warenkorb testen
curl -X GET http://localhost:8080/basket -H "X-User-Id: 1"

# Swagger UI
http://localhost:8080/swagger
```

### Test-Users:

```
User ID 1 (Maximilian): Balance 120.30 EUR
User ID 2 (Mohamed):    Balance 70.87 EUR
User ID 3 (Marcin):     Balance 8920.06 EUR
User ID 4 (Mian):       Balance 3150.00 EUR
User ID 5 (Mandy):      Balance 0.11 EUR ⚠️ (für negative Tests)
```

---

## ✅ ERFOLGS-KRITERIEN

Dein Projekt ist vollständig wenn:

- [ ] `docker compose up -d` startet beide Services
- [ ] `mvn clean package` läuft ohne Fehler
- [ ] `mvn test` zeigt: **Tests run: 11, Failures: 0, Errors: 0**
- [ ] Swagger UI ist erreichbar
- [ ] Alle curl Requests funktionieren
- [ ] User-Isolation funktioniert
- [ ] TTL wird nach 2 Minuten gelöscht
- [ ] Balance-Prüfung funktioniert
- [ ] Checkout erstellt Bestellung und belastet Balance
- [ ] GET /orders zeigt Bestellungen

**Alle Kriterien erfüllt = 10/10 Punkte!** 🎉
