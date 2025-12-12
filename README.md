#OpenLibraryCatalog
##Az alkalmazás célja

Az **OpenLibraryCatalog** egy Android alapú mobilalkalmazás, amely lehetővé teszi
könyvek keresését az **Open Library** nyilvános adatbázisában.  
Az alkalmazás célja, hogy a felhasználók egyszerűen böngészhessenek könyvek között,
megtekintsék azok részleteit, valamint kedvenc könyveiket elmentsék későbbi
megtekintéshez.

Az alkalmazás modern, fragment-alapú felépítést használ, alsó navigációs sávval,
amely biztosítja az egyszerű és átlátható használatot.

---

##Felhasználói funkciók

###Kezdőlap
- Rövid bemutató szöveg az alkalmazás működéséről
- Illusztratív grafika
- Navigáció az alsó menüsáv segítségével

###Könyvkeresés
- Könyvek keresése cím vagy szerző alapján
- Találatok listázása borítóképpel, címmel, szerzővel és megjelenési évvel
- Keresési eredmények valós időben az Open Library API segítségével

###Kedvencek kezelése
- Könyvek hozzáadása és eltávolítása a kedvencek közül
- Kedvencek külön listában történő megjelenítése
- Kedvencek automatikus frissítése a nézet újratöltése nélkül

###Részletes könyvnézet
- Könyv részletes adatainak megjelenítése:
  - cím
  - szerző
  - megjelenési év
  - borítókép
  - leírás
- Navigáció vissza a keresési vagy kedvencek listára

---

##Navigáció
- Alsó navigációs sáv:
  - Kezdőlap
  - Keresés
  - Kedvencek
- Fragment-alapú képernyőkezelés Android Navigation Componenttel

---

##Adatforrás
Az alkalmazás az **Open Library** nyilvános REST API-ját használja:
- könyvkereséshez
- borítóképek letöltéséhez
- részletes leírások megjelenítéséhez

---

##Technológiai áttekintés
- Android (Java)
- Fragment alapú architektúra
- RecyclerView
- Retrofit (API kommunikáció)
- Glide (képek betöltése)
- SharedPreferences (kedvencek tárolása)
- Navigation Component
