# Test-Anleitung für Windows CMD

## Produkt mehrmals hinzufügen - Windows Befehle

### Projekt starten (in separatem CMD-Fenster):
```cmd
mvn compile quarkus:dev
```

### In neuem CMD-Fenster testen:

**1. Warenkorb leeren:**
```cmd
curl -X DELETE "http://localhost:8080/basket" -H "X-User-Id: 1"
```

**2. Erstes Hinzufügen (count=1):**
```cmd
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" -H "X-User-Id: 1" -H "Content-Type: application/json" -d "{\"productName\":\"Laptop\",\"productId\":\"1-2-3-4-5-6\",\"count\":1,\"price\":50.0}"
```

**3. Zweites Hinzufügen (count=2):**
```cmd
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" -H "X-User-Id: 1" -H "Content-Type: application/json" -d "{\"productName\":\"Laptop\",\"productId\":\"1-2-3-4-5-6\",\"count\":2,\"price\":50.0}"
```

**4. Drittes Hinzufügen (count=1):**
```cmd
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" -H "X-User-Id: 1" -H "Content-Type: application/json" -d "{\"productName\":\"Laptop\",\"productId\":\"1-2-3-4-5-6\",\"count\":1,\"price\":50.0}"
```

**5. Warenkorb abrufen (Ergebnis prüfen):**
```cmd
curl -X GET "http://localhost:8080/basket" -H "X-User-Id: 1"
```

**Erwartetes Ergebnis:**
- count sollte 4 sein (1+2+1)
- total sollte 200.0 sein (4 x 50.0)

---

## Wichtig für Windows:

1. **Keine Backslashes** - Alles muss in einer Zeile sein
2. **Doppelte Anführungszeichen escapen** - `\"` statt `"`im JSON
3. **Einfache Anführungszeichen funktionieren nicht** in Windows CMD für JSON

---

## Alternative: PowerShell

Wenn du PowerShell nutzt (empfohlen), sind die Befehle einfacher:

**Warenkorb leeren:**
```powershell
curl -Method DELETE -Uri "http://localhost:8080/basket" -Headers @{"X-User-Id"="1"}
```

**Artikel hinzufügen:**
```powershell
$body = @{
    productName = "Laptop"
    productId = "1-2-3-4-5-6"
    count = 1
    price = 50.0
} | ConvertTo-Json

curl -Method POST -Uri "http://localhost:8080/basket/1-2-3-4-5-6" -Headers @{"X-User-Id"="1"; "Content-Type"="application/json"} -Body $body
```

---

## Alternative: Git Bash / WSL

Wenn du Git Bash oder WSL hast, funktionieren die Linux-Befehle aus TEST-ANLEITUNG.md direkt.

---

## Postman verwenden

Am einfachsten für Windows:

1. **Postman öffnen**
2. **Neue Request erstellen:**
   - Method: `POST`
   - URL: `http://localhost:8080/basket/1-2-3-4-5-6`
   - Headers: 
     - `X-User-Id: 1`
     - `Content-Type: application/json`
   - Body (raw JSON):
     ```json
     {
       "productName": "Laptop",
       "productId": "1-2-3-4-5-6",
       "count": 1,
       "price": 50.0
     }
     ```
3. **Send 3x drücken**
4. **GET Request machen:**
   - Method: `GET`
   - URL: `http://localhost:8080/basket`
   - Headers: `X-User-Id: 1`
   - Send drücken → count sollte 4 sein!
