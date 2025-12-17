#!/bin/bash

echo "========================================="
echo "Test: Produkt mehrmals hinzufügen"
echo "========================================="
echo ""

# Warenkorb leeren
echo "1. Warenkorb leeren..."
curl -s -X DELETE "http://localhost:8080/basket" -H "X-User-Id: 1"
echo ""
echo ""

# Erstes Mal: Produkt mit count=1 hinzufügen
echo "2. Produkt zum ersten Mal hinzufügen (count=1)..."
curl -s -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "productId": "1-2-3-4-5-6",
    "count": 1,
    "price": 50.0
  }' | jq '.'
echo ""
echo ""

# Zweites Mal: Gleiches Produkt mit count=2 hinzufügen
echo "3. Gleiches Produkt nochmal hinzufügen (count=2)..."
curl -s -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "productId": "1-2-3-4-5-6",
    "count": 2,
    "price": 50.0
  }' | jq '.'
echo ""
echo ""

# Drittes Mal: Gleiches Produkt mit count=1 hinzufügen
echo "4. Gleiches Produkt nochmal hinzufügen (count=1)..."
curl -s -X POST "http://localhost:8080/basket/1-2-3-4-5-6" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "productId": "1-2-3-4-5-6",
    "count": 1,
    "price": 50.0
  }' | jq '.'
echo ""
echo ""

echo "Ergebnis: Das Produkt sollte jetzt count=4 haben (1+2+1)"
echo "Total sollte 200.0 sein (4 x 50.0)"
