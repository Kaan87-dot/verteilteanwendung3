@echo off
echo =========================================
echo Test: Produkt mehrmals hinzufuegen
echo =========================================
echo.

echo 1. Warenkorb leeren...
curl -X DELETE "http://localhost:8080/basket" -H "X-User-Id: 1"
echo.
echo.

echo 2. Produkt zum ersten Mal hinzufuegen (count=1)...
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" -H "X-User-Id: 1" -H "Content-Type: application/json" -d "{\"productName\":\"Laptop\",\"productId\":\"1-2-3-4-5-6\",\"count\":1,\"price\":50.0}"
echo.
echo.

echo 3. Gleiches Produkt nochmal hinzufuegen (count=2)...
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" -H "X-User-Id: 1" -H "Content-Type: application/json" -d "{\"productName\":\"Laptop\",\"productId\":\"1-2-3-4-5-6\",\"count\":2,\"price\":50.0}"
echo.
echo.

echo 4. Gleiches Produkt nochmal hinzufuegen (count=1)...
curl -X POST "http://localhost:8080/basket/1-2-3-4-5-6" -H "X-User-Id: 1" -H "Content-Type: application/json" -d "{\"productName\":\"Laptop\",\"productId\":\"1-2-3-4-5-6\",\"count\":1,\"price\":50.0}"
echo.
echo.

echo 5. Warenkorb abrufen (Ergebnis pruefen)...
curl -X GET "http://localhost:8080/basket" -H "X-User-Id: 1"
echo.
echo.

echo Ergebnis: Das Produkt sollte jetzt count=4 haben (1+2+1)
echo Total sollte 200.0 sein (4 x 50.0)
echo.
pause
