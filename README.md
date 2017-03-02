<img width="100" src="/project_docs/StartupCommandsLogo.png"></a>
# StartupCommands

StartupCommands is a Minecraft server plugin that automatically runs commands on startup with an optional delay.

## Config
Add commands in the configuration by following the examples provided, or follow the following example:

```
# Add commands below to execute on server startup/reload.
# Delay is the number of seconds to wait before executing. Use 0 for immediate execution.
commands:
  gamemode 1 TestPlayer:
    delay: 10
  ban TestPlayer:
    delay: 0
  say This is 1 test message with StartupCommands:
    delay: 5
```

Please note that normal characters that required escaping in YAML will need to be escaped in the configuration.

## Commands
- /startup help - _display StartupCommands command help_
- /startup view - _view the current startup commands_
- /startup add (optional delay in seconds) <command string> - _add a command to the startup command list_
- /startup remove <command string> - _remove a command from the startup command list_
- /startup run - _manually run the startup commands in the configuration_

The permission required for using these commands is: _startupcommands.manage_
