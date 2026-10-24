# GameTurboFloatingPro V5 — Xiaomi-style UI

இந்த project Xiaomi/MIUI Game Turbo-வின் dark gaming dashboard மற்றும் floating sidebar தோற்றத்தால் inspired ஆன independent Android app.

## Build செய்வது
1. ZIP-ஐ extract செய்யவும்.
2. GitHub repository-யின் root-க்கு உள்ள files/folders upload செய்யவும்.
3. Actions tab → **Build Game Turbo V5** → **Run workflow**.
4. Build வெற்றியடைந்தால் `GameTurboFloatingPro-V5-debug-apk` artifact-ஐ download செய்து extract செய்யவும்.
5. `app-debug.apk`-ஐ Android phone-ல் install செய்யலாம். Android கேட்கும் போது unknown-app install permission-ஐ நீங்கள் நம்பும் source-க்கு மட்டும் அனுமதிக்கவும்.

## கவனிக்கவும்
- இது Xiaomi-வின் official Game Turbo அல்ல; original Xiaomi system app-ஐ மாற்றாது.
- Floating overlay-க்கு “Display over other apps” permission தேவை.
- உண்மையான Free Fire FPS-ஐ சாதாரண third-party APK-க்கு Android 10/MIUI பொதுவாக அனுமதிப்பதில்லை. எனவே போலியான FPS எண்ணைக் காட்டாமல், privileged system access இல்லாமல் reliable FPS கிடையாது என்று காட்டுகிறது.
- Network monitor Free Fire app UID-இன் RX+TX throughput-ஐ Android அனுமதித்தால் காட்டும்; இது ping/latency அல்ல. Android UID counters கிடைக்காவிட்டால் N/A அல்லது device fallback வரும்.
- Performance boost, touch response optimisation போன்ற system-level செயல்களை சாதாரண APK முழுமையாக செய்ய முடியாது.
- இந்த ZIP source project மட்டுமே; APK build/test வெற்றியடைந்ததாகக் கருத வேண்டாம். GitHub Actions build முடிவைச் சரிபார்க்கவும்.


## 60 FPS target — POCO C31
- Free Fire-ல் Graphics = Smooth வைத்துப் பாருங்கள்.
- Game-ல் High FPS option கிடைத்தால் மட்டும் enable செய்யுங்கள்; option இல்லையெனில் config/mod/third-party FPS unlocker பயன்படுத்த வேண்டாம்.
- Game தொடங்குவதற்கு முன் தேவையற்ற background apps-ஐ close செய்யவும்.
- Battery Saver-ஐ gaming நேரத்தில் off செய்து, phone சூடாக இருந்தால் break எடுக்கவும்.
- Screen recording/overlay apps கூடுதல் load தரலாம்; பிரச்சினை இருந்தால் overlay-ஐ off செய்து ஒப்பிடுங்கள்.
- இந்த app actual FPS-ஐ அளவிடாது; 60 FPS என்பது target மட்டுமே. POCO C31 hardware/game support உறுதி செய்யாமல் stable 60 FPS என்று சொல்ல முடியாது.


## Dual-edge Game Turbo-style sidebar
- Floating handle இரு screen edge-களிலும் வரும்.
- Left handle-ஐ வலப்பக்கம் swipe செய்யவும்; right handle-ஐ இடப்பக்கம் swipe செய்யவும். Tap செய்தாலும் panel திறக்கும்.
- இது Android overlay permission பயன்படுத்தும் independent panel; Xiaomi system-integrated Game Turbo அல்ல.
- Overlay-ஐ மற்ற apps மீது காட்ட permission தேவை. சில games/MIUI settings overlay-ஐ தடுக்கலாம்.
- Real FPS reading அல்லது actual 60 FPS force இதில் இல்லை.
