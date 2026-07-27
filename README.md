<h1>SmithingTracker</h1>

Tracks how much you can make from your bars (Pickaxes excluded)

Scans inventory + bank(when accessed) 

KNOW your profits when smithing! Now you can simply see if you will be +/- profit!

<h1>What It Does</h1>

**Counts All Bars:** Bank + Inventory, Unnoted + Noted (Bronze to Rune)

**Item Box:**
Movable ICON Overlay showing Quantity, Value, Can make: X with Y Bars,Total Value, Total Profit of current smithed item

<img width="45" height="71" alt="image" src="https://github.com/user-attachments/assets/6a33abbc-2bce-423b-9b4f-b1fb2bee2261" />
<img width="277" height="109" alt="image" src="https://github.com/user-attachments/assets/5e18cd3b-254d-4b88-bc6f-5e76bd28ff79" />



**Tracker:** Status / Session Time / Smithed / Per Hour / Profit Hour / Total bars & Cost 

<img width="226" height="164" alt="image" src="https://github.com/user-attachments/assets/9a729faf-bcc2-4777-beca-593f21a2f355" />

**Filters:** 100+ Toggles per item/metal + Ammo(cballs)

Select Bar Type from Bronze - Rune. 

Bar price set to 0 = GE (or custom price for exact profits)

<img width="238" height="362" alt="image" src="https://github.com/user-attachments/assets/f2d62d8e-c64a-4000-8f1b-9ee2b4712d7d" />


<h1>Math</h1>

```text
totalBars = bank + inventory (with noted)

remaining = totalBars - barsUsed

canMake = remaining / barsPerItem

totalQty = owned + canMake

profit = canMake * (itemPrice - barsPerItem * barPrice)

Platebody: 5 bars, 2H/Platelegs/Plateskirt: 3, Helm/Chain/Kite/Battleaxe/etc: 2, rest: 1

remainingBars = getTotalBarsAvailable(barId) - barsUsed
canMake = remainingBars / barsPerItem
totalQty = currentCount + canMake
totalQtyValue = totalQty * itemPrice
profitEach = itemPrice - (barsPerItem * barPrice)
totalProfit = canMake * profitEach

```

<h1>Install</h1>

Plugin Hub -> Search Smithing Tracker -> Install

Or 

./gradlew build and drop jar in ~/.runelite/plugins/



<h1>License:
BSD-2</h1>
<img width="317" height="23" alt="image" src="https://github.com/user-attachments/assets/34b28e8f-1a64-4e96-8f82-28177fd99823" />

