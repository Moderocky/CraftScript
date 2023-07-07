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
