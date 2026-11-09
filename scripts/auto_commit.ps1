param(
    [switch]$Push,
    [switch]$DryRun
)

$argsList = @()
if ($Push) { $argsList += "--push" }
if ($DryRun) { $argsList += "--dry-run" }

py scripts/auto_commit.py @argsList
