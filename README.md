# Claude Widget — Android hjemmeskærm-widget

En native Android-widget (Kotlin) der lader dig chatte med Claude direkte fra
din hjemmeskærm, med din egen API-nøgle. Samtalen vises som en scrollbar liste
af bobler, og du skriver beskeder via en lille dialog.

## Funktioner
- Chat-widget på hjemmeskærmen med bruger/Claude-bobler
- Bruger din egen Anthropic API-nøgle (gemmes lokalt)
- Valg af model (Opus / Sonnet / Haiku)
- Husker samtalen
- Indstillingsskærm + "ryd samtale"

---

## Sådan bygger og installerer du den

### Du skal bruge
- **Android Studio** (gratis): https://developer.android.com/studio
- En **Anthropic API-nøgle**: https://console.anthropic.com/keys
- En Android-telefon eller emulator (Android 8.0 / API 26 eller nyere)

### Trin

1. **Åbn projektet**
   - Start Android Studio
   - Vælg "Open" og peg på `ClaudeWidget`-mappen
   - Lad Gradle synkronisere (Android Studio henter selv Gradle-wrapper og
     dependencies — kræver internet første gang)

2. **Byg & kør**
   - Tilslut din telefon med USB (med USB-debugging slået til) ELLER start en emulator
   - Tryk på den grønne "Run"-knap (▶)
   - Appen installeres på enheden

3. **Sæt din API-nøgle**
   - Åbn appen "Claude Widget"
   - Indsæt din API-nøgle, vælg model, tryk "Gem"

4. **Tilføj widgetten til hjemmeskærmen**
   - Tryk og hold på et tomt sted på hjemmeskærmen
   - Vælg "Widgets"
   - Find "Claude Widget" og træk den ud på skærmen
   - Tryk på input-bjælken for at chatte

---

## Lav en installerbar APK (uden Android Studio i fremtiden)

I Android Studio:
- Menu: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
- APK'en lægges i `app/build/outputs/apk/debug/app-debug.apk`
- Overfør den til telefonen og åbn den for at installere
  (du skal måske tillade "installation fra ukendte kilder")

Eller via terminal i projektmappen:
```bash
./gradlew assembleDebug
```

---

## Projektstruktur

```
ClaudeWidget/
├── app/src/main/
│   ├── java/com/claudewidget/app/
│   │   ├── ChatWidgetProvider.kt   # selve widgetten
│   │   ├── MessageListService.kt   # scrollbar beskedliste
│   │   ├── InputActivity.kt        # skriv-besked dialog + API-kald
│   │   ├── SettingsActivity.kt     # API-nøgle & model
│   │   ├── ClaudeApi.kt            # Anthropic API-klient
│   │   └── ChatStorage.kt          # lokal lagring
│   ├── res/                        # layouts, drawables, farver, strenge
│   └── AndroidManifest.xml
└── build.gradle.kts m.fl.
```

## Sikkerhed
Din API-nøgle gemmes lokalt på enheden i appens private SharedPreferences og
sendes udelukkende til Anthropics API. Del aldrig din nøgle med andre.
```
