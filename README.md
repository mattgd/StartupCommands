<img width="100" src="/project_docs/StartupCommandsLogo.png"></a>
# StartupCommands [![Build Status](https://travis-ci.org/mattgd/StartupCommands.svg?branch=master)](https://travis-ci.org/mattgd/StartupCommands)

StartupCommands is a Minecraft server plugin that automatically runs commands on startup with an optional delay.

## Config
Add commands in the configuration by following the examples provided, or follow the following example:

```
# Add commands below to execute on server startup/reload.
# delay is the number of seconds to wait before executing. Use 0 for immediate execution.
# notify-on-exec is a boolean value to enable/disable console notifications on command execution.
commands:
  gamemode 1 TestPlayer:
    delay: 10
    notify-on-exec: false
  ban TestPlayer:
    delay: 0
  say This is a test message with StartupCommands:
    delay: 3
```

Please note that normal characters that required escaping in YAML will need to be escaped in the configuration.

## Commands
- /startup help - _display StartupCommands command help_
- /startup view - _view the current startup commands_
- /startup add/create (optional delay in seconds) <command string> - _add a command to the startup command list_
- /startup remove/delete <command string> - _remove a command from the startup command list_
- /startup run - _manually run the startup commands in the configuration_

The permission required for using these commands is: _startupcommands.manage_
