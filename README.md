# VexlioKits

**VexlioKits** je pokročilý Minecraft KitPvP plugin vytvořený pro moderní Minecraft servery. Tento projekt vznikl kompletním refaktoringem a modernizací původního pluginu *KitBattle*. Plugin obsahuje systém schopností (abilities), turnaje, výzvy, statistiky hráčů a plnou integraci s dalšími pluginy.

---

## 🚀 Hlavní funkce

- **Správa kitů a obchod:** Plně konfigurovatelné kity s možností nastavení cen (Coins), cooldownů na výběr a oprávnění (permissions).
- **Speciální schopnosti (Abilities):** Unikátní herní mechaniky pro jednotlivé kity (např. Dracula, Kangaroo, Spiderman, Timelord a další).
- **Herní režimy & Akce:** 
  - **Turnaje (Tournaments):** Automatické nebo ručně spouštěné turnaje s fází přípravy (grace period) a odměnami pro vítěze.
  - **Výzvy (Challenges):** PvP zápasy mezi hráči s možností uzamčení kitu.
- **Statistiky & ELO:** Sledování zabití (Kills), úmrtí (Deaths), poměru KDR, nasbíraných mincí (Coins) a ELO hodnocení.
- **Holografické tabulky:** Integrovaná podpora pro zobrazení žebříčků nejlepších hráčů pomocí DecentHolograms nebo HolographicDisplays.
- **Podpora Placeholders:** Plná integrace s **PlaceholderAPI** (PAPI) pro zobrazení statistik kdekoli na serveru.

---

## 🛠️ Kompilace a sestavení

Projekt využívá sestavovací nástroj **Gradle** a obsahuje připravený Gradle wrapper, takže k sestavení nepotřebujete mít Gradle nainstalovaný globálně.

### Požadavky
- Java Development Kit (JDK) **8** nebo novější (pro kompilaci se doporučuje **JDK 17** nebo **21**).

### Postup sestavení
1. Otevřete terminál v kořenovém adresáři projektu.
2. Spusťte příkaz pro sestavení:
   - **Windows:**
     ```bash
     .\gradlew.bat build
     ```
   - **Linux / macOS:**
     ```bash
     chmod +x gradlew
     ./gradlew build
     ```
3. Po úspěšném dokončení kompilace naleznete výsledný `.jar` soubor ve složce:
   `build/libs/VexlioKits.jar`

---

## 💻 Příkazy a oprávnění

Hlavním příkazem pluginu je `/vexliokits` s aliasy `/kits` a `/vk`.

| Příkaz | Popis | Oprávnění |
| :--- | :--- | :--- |
| `/kits` | Zobrazí hlavní menu nebo nápovědu | *Všem přístupné* |
| `/kits join <mapa>` | Připojení do vybrané KitPvP mapy | *Všem přístupné* |
| `/kits leave` | Opuštění aktuální hry | *Všem přístupné* |
| `/kits stats` | Zobrazí statistiky hráče | *Všem přístupné* |
| `/kits spawn` | Teleportuje hráče na spawn aktuální mapy | *Všem přístupné* |
| `/kits selectkit <kit>` | Výběr kitu | *Všem přístupné* |
| `/vexliokits admin` | Zobrazí administrátorské příkazy | `VexlioKits.admin` |
| `/vexliokits reload` | Znovu načte konfiguraci a zprávy | `VexlioKits.admin` |
| `/vexliokits create <mapa>` | Vytvoří novou KitPvP mapu | `VexlioKits.admin` |
| `/vexliokits kit <akce>` | Úprava, vytváření nebo rozdávání kitů | `VexlioKits.admin` |
| `/vexliokits wand` | Dá hráči výběrovou hůlku (Blaze Rod) pro regiony | `VexlioKits.admin` |

---

## 🔌 Podporované pluginy (Softdepend)

VexlioKits automaticky spolupracuje s následujícími pluginy, pokud jsou na serveru nainstalovány:
- **Vault:** Propojení ekonomiky a mincí s globálním ekonomickým systémem.
- **PlaceholderAPI:** Poskytuje hráčské a globální statistiky pro ostatní pluginy (viz seznam níže).
- **TitleManager:** Zobrazování zpráv na obrazovce.
- **AdvancedEnchantments:** Podpora vlastních očarování v kitech.
- **ItemEdit / ItemsAdder:** Podpora pro upravené předměty a vlastní textury.
- **DecentHolograms / HolographicDisplays:** Pro zobrazení interaktivních žebříčků.

---

## 📊 Placeholders (PlaceholderAPI)

Pokud používáte plugin **PlaceholderAPI**, můžete využít následující placeholdery s prefixem `%VexlioKits_...%`:

### Globální a serverové statistiky
- `%VexlioKits_players_count%` – Počet hráčů v KitPvP hře.
- `%VexlioKits_maps_count%` – Celkový počet dostupných KitPvP map.
- `%VexlioKits_challengers_count%` – Počet hráčů vyzývajících ostatní ve výzvách.
- `%VexlioKits_tournament_participants_count%` – Počet účastníků aktuálního turnaje.
- `%VexlioKits_kits_count%` – Celkový počet zaregistrovaných kitů.
- `%VexlioKits_ranks_count%` – Celkový počet ranků (úrovní).

### Statistiky konkrétního hráče
- `%VexlioKits_coins%` – Počet mincí (Coins) hráče.
- `%VexlioKits_kills%` – Počet zabití hráče.
- `%VexlioKits_deaths%` – Počet úmrtí hráče.
- `%VexlioKits_kdr%` – Poměr zabití a úmrtí (KDR, zaokrouhleno na 2 desetinná místa).
- `%VexlioKits_killstreak%` – Aktuální killstreak (zabití bez úmrtí).
- `%VexlioKits_deathstreak%` – Aktuální deathstreak (úmrtí bez zabití).
- `%VexlioKits_player_exp%` – Zkušenostní body (EXP) hráče.
- `%VexlioKits_player_rank%` – Název aktuálního ranku hráče.
- `%VexlioKits_player_rank_prefix%` – Prefix aktuálního ranku hráče.
- `%VexlioKits_player_next_rank%` – Název následujícího ranku hráče.
- `%VexlioKits_player_next_rank_exp%` – EXP potřebné pro dosažení následujícího ranku.
- `%VexlioKits_player_next_rank_exp_difference%` – Chybějící EXP do dalšího ranku.
- `%VexlioKits_elo%` – ELO hodnocení hráče.
- `%VexlioKits_map%` – Název mapy, na které se hráč právě nachází.
- `%VexlioKits_selected_kit%` – Název vybraného kitu hráče.
- `%VexlioKits_ability_cooldown%` – Zbývající cooldown schopnosti.
- `%VexlioKits_bounty%` – Vypsaná odměna (bounty) na hlavu hráče.
- `%VexlioKits_combat_log_duration%` – Zbývající čas v combat logu.
