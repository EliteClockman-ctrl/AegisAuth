$ErrorActionPreference = 'Stop'
$url = 'https://services.gradle.org/distributions/gradle-8.8-bin.zip'
$zip = 'gradle-bin.zip'
$dest = 'gradle-dist'

Write-Host "Downloading Gradle 8.8..."
Invoke-WebRequest -Uri $url -OutFile $zip
Write-Host "Extracting Gradle 8.8..."
Expand-Archive -Path $zip -DestinationPath $dest -Force
Remove-Item $zip
Write-Host "Gradle 8.8 installed successfully in $dest"
