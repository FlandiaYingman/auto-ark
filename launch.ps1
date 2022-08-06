Set-Location $PSScriptRoot

git submodule update --init --recursive
git pull --recurse-submodules

./gradlew run
if ($LASTEXITCODE -ne 0)
{
    Pause
}