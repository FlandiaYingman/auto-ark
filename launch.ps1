Set-Location $PSScriptRoot

git pull

./gradlew run
if ($LASTEXITCODE -ne 0)
{
    Pause
}