Spring Boot + Spring Security + JPA + Thymeleaf + MySQL

Spring Boot alkalmazás, amely felhasználók és adataik kezelésére szolgál.

Az alkalmazás ismertetése:

Az alkalmazásban két szerepkört, USER és ADMIN használunk.

Az ADMIN szerepkör jogosultságai:

saját adatok módosítása
lista az összes felhasználóról, ahol lehetőség van:
    kiválasztott felhasználó törlésére (logikai törlés, csak P státusz esetén),
    kiválasztott felhasználónak ADMIN jogosultság adására,
    kiválasztott felhasználó adatainak megtekintésére.
kijelentkezés.

A USER szerpkör jogosultságai:

adatok módosítása,
törlés kérvényezése,
kijelentkezés.

Felhasználó adatai egy adatbázisban tároljuk, az alábbi séma szerint:

vezetéknév,
kersztnév,
születési dátum,
felhasználónév,
jelszó,
E-mail cím,
szerpkör (ADMIN, USER),
státusz (A - Aktív, P - törlésre vár, D - törölt).

A felhasználó regisztrál a weboldalunkon, a majd kap egy e-mail-t amely tartalmaz egy linket, a likre katintva véglegesíti a regisztrációját. Regisztrálni csak USER szerepkörrel lehet. A bejelentkezés felhasználónév és jelszóval történik.

