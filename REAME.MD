// Runnite to sam da dajte prave emulator id-je da bo stable connection

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb devices
& $adb -s emulator-5554 reverse tcp:3000 tcp:3000
& $adb -s emulator-5556 reverse tcp:3000 tcp:3000