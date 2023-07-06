CraftScript
=====

### Opus #25

A tiny scripting language for writing user-centered scripts in Minecraft.

## Documentation

Comprehensive language documentation (and some introductory tutorials) are
available [here](https://craftscript.kenzie.mx).

## Maven Information

```xml

<repository>
    <id>kenzie</id>
    <name>Kenzie's Repository</name>
    <url>https://repo.kenzie.mx/releases</url>
</repository>
``` 

```xml

<dependency>
    <groupId>mx.kenzie</groupId>
    <artifactId>craftscript</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Introduction

CraftScript is a contextualised, functional scripting language.
It is designed for the game 'Minecraft' to run small, non-invasive scripts from the context of a player.
However, the parser and script engine are easy to modify or extend so that domains may create their own additional
functionality.

Its principle feature is the 'context' -- the situation and environment that code is running in, including the
variables, functions and keywords that are available during runtime.
This allows for an unusual form of *ad hoc* polymorphism: the same interpreted code can be used to do an entirely
different thing
depending on its context.
Rather than overloading two separate functions (e.g. `fn(int)` and `fn(string)`) CraftScript can feed different
parameters, variables, known types and even language keywords to an executable statement.

## Motivations

CraftScript was designed for players in the game 'Minecraft' to write scripts to be run from their in-game avatar.

With this in mind, the actions available in a script are limited to what the executor is capable of doing.
This adds a layer of safety to the language, since a player's permissions can be limited in order to prevent destructive
actions.

I made CraftScript for a public server in which users can write, upload and run their own scripts in order
to create macros or special behaviour.

The language is designed to be fairly simple but also unambiguous, and imitates the style of Minecraft's native
commands, selectors and keys. \
There is some deviation from this, since CraftScript has several features that do not exist in the `.mcfunction` format.

[//]: # (## Parsing)

[//]: # ()

[//]: # (Scripts are read line-by-line and fed to a `ScriptLoader`.)

[//]: # (Each input &#40;line&#41; is checked against a set of statement parsers that attempt to match the line and, if successful, feed)

[//]: # (back an executable `Statement`.)

[//]: # (If the match is unsuccessful the parser is flushed and the input is fed to the next parser.)

[//]: # ()

[//]: # (Individual parsers have a fair amount of control over their parsing process, since most need to be able to parse a)

[//]: # (sub-statement of some kind.)

[//]: # ()

[//]: # (The parsers have the following guarantees:)

[//]: # ()

[//]: # (1. A parser's `matches` will always be checked immediately before `parse` is called.)

[//]: # (2. The same parser object cannot be used more than once at the same time.)

[//]: # (3. After a parse has been attempted the parser is flushed, and could be reused.)

[//]: # (4. If the parser fails to parse and is unable to leave the script in its starting state &#40;e.g. it had to look at the next)

[//]: # (   line&#41; an error will be thrown and parsing will end.)

[//]: # ()

[//]: # (Parsers will be re-used where possible - one copy is cached and provided when it is asked for as long as it is not in)

[//]: # (use. If the parser is in use another copy will be made and then discarded when finished with.)

