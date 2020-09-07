# Opomnik zdravil 
Android aplikacija napisana v Kotlin programskem jeziku in je namenjena rednemu preverjanju jemanja zdravil. Aplikacija deluje skupaj s aplikacijo NadzorZdravil, ki je objavljena na tem [repositoriju](https://github.com/maykec/NadzorZdravil)

## Opis aplikacije
Ko uporabik aplikacijo naloži, jo mora prvič odpreti sam. Potrebno je pritisniti na gumb "Start service" 
Požene se android servis, ki se sihornizira z bazo opomnikov na Google Firebase. Ob primernem času servis pokaže obvestilo, da je potrebno vzeti zdravilo. Obvestilo mora uporabnik ročno potrditi. Servis meri čas od prejema obvestila do potrdive obvestila. Na tak način lahko v aplikaciji NadzorZdravil vidimo morebitne zamude ali preskoke pri jemanju zdravil


### Prikaz obvestila
![](https://raw.githubusercontent.com/maykec/NadzorZdravil/master/blob/notification.png)

Uporabnik mora pritistni na gumb "Potrdi prejem zdravila". Čas od prejema aplikacije in pritiska na gumb se meri. 


## Zagon aplikacje
Aplikacijo je potrebbo odpreti v Android Studio

1. `git clone https://github.com/maykec/OpomnikZdravil`
2. Potrebno je imeti konfiguriran Google Firebase račun
