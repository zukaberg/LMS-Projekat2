# LMS-Projekat2
Projekat 2 - PUJ
[readme.md](https://github.com/user-attachments/files/24800313/readme.md)
Life Managment System

Desktop aplikacija kreirana u JAVA programskom jeziku uz pomoć Swinga i MongoDB-a za bazu podataka.
Aplikacija omogućava korisniku da prati finansije, dnevne obroke, spavanje i učenje. Kreirana aplikacija
je projekat iz predmeta Programiranje u JAVI na IPIAkademiji u Tuzli.

-Project setup-

TEHNOLOGIJE:
-JAVA SDK Amazon Corretto 11.0.28
-MongoDB
-Java Swing za GUI

-Pokretanje aplikacije-

Aplikacija se pokreće iz main klase koja prvo otvara registracijsku formu, a nakon toga main dashboard
sa tabovima za pregled profila, financeapp te my trackers.

-Struktura projekta-

.forms gdje su SwingUI paneli
.dao (data access object) klase za rad sa bazom, connection string
.models gdje su model klase
.utils - pomoćne klase (theme, avatar util)

Na svim trackerima i formama imamo implementirane CRUD operacije za 100% upravljanje nad našim podacima i trackerima.

-Instrukcije-

1.Pokrenite aplikaciju
2.Na login ekranu kreirajte nalog te se ulogujte sa svojim podacima koje kreirate
3.Na main dashboardu imamo 3 taba:
-View profile - pregled profila, vaši podaci te edit dugme za uređivanje profila i izmjenu podataka
-Financeapp tab - dodavanje prihoda i rashoda i lagano praćenje svog balansa
-My trackers tab - odabir trackera, dodavanje novih zapisa, pregled tabela, uređivanje i brisanje
4.Logout - odjava sa vašeg accounta

-Moguća poboljšanja aplikacije
1.Hashiranje passworda zbog bolje zaštite podataka
2.Filteri na tabelama zbog lakše pretrage
3.Export funkcije npr u PDF...
4.Grafički prikaz u vidu chartova na osnovu podataka iz MongoDB-a
