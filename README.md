CraftScript
=====

### Opus #25

A tiny scripting language for writing user-centered scripts in Minecraft.

## Motivations

CraftScript is a contextualised, simple scripting language.
It was designed to be run in the game 'Minecraft' from the context of a player or non-player entity.

With this in mind, the actions available in a script are limited to what the executor is capable of doing.
This adds a layer of safety to the language, since a player's permissions can be limited in order to prevent destructive
actions.

I made CraftScript for a public server in which users can write, upload and run their own scripts in order
to create macros or special behaviour.

The language is designed to be fairly simple but also unambiguous, and imitates the style of Minecraft's native
commands, selectors and keys. \
There is some deviation from this, since CraftScript has several features that do not exist in the `.mcfunction` format.

## Language Features

Syntax will be described in the following format:

| Type           | Description                                  |
|----------------|----------------------------------------------|
| Input          | The input type written in percent `%` signs. |
| Literal        | The raw characters.                          |
| Required Space | An underscore `_` character.                 |
| Repetition     | Three period `...` characters.               |

New lines must be respected. Spaces are typically optional but occasionally required.

### Variable Use

Get the variable value.

```
%name%
```

#### Examples

```
var = 10
if var == 10 /print {var}
```

### Variable Assignment

Set the variable (L) to the result of the statement (R).

```
%name%=%statement%
```

#### Examples

```
name = "BaeFell"
age = 10
word = "hello"
var = word
sum = 5 + 5
first = second = "hello"
```

### Type Creator

Creates a data object with given properties.
MUST be followed by a block.
The block must contain ONLY variable assignment root statements

```
type_#%name%_%block%
   %variable assignment%
   ...
%close block%
```

#### Examples

```
person = type #person {
   name = "BaeFell"
   age = 42
}
if person[name] == "George" /print no
/print {person[name]} is {person[age]} years old
```

### Add

Return the sum of the statement (L) plus the statement (R).
If either statement provides a string (text) then the result will be a concatenated string.
If either statement is a non-numerical value the result will be `null`.

```
%statement%+%statement%
```

#### Examples

```
fifteen = 10 + 5
result = fifteen + 10
if result == 25 /print yes
greeting = "hello " + "there"
```

### Subtract

Return the statement (L) minus the value of the statement (R).
If either statement is a non-numerical value the result will be `null`.

```
%statement%-%statement%
```

#### Examples

```
five = 10 - 5
result = five - 2
```

### Multiply

Return the sum of the statement (L) multiplied by the statement (R).
If either statement is a non-numerical value the result will be `null`.

```
%statement%*%statement%
```

#### Examples

```
fifty = 10 * 5
result = fifty * 10
```

### Multiply

Return the sum of the statement (L) divided by the statement (R).
If either statement is a non-numerical value the result will be `null`.

```
%statement%/%statement%
```

#### Examples

```
ten = 50 / 5
result = ten / 2
```

### String Literal

A (semi-literal) text value.
This supports interpolation using the `{` and `}` brackets.

```
"%text%"
```

#### Examples

```
word = "hello"
name = "baefell"
greeting = "{word}, {name}"
assert greeting == "hello, baefell"
```

### Boolean Literal

A literal true/false value.

```
true
```

```
false
```

#### Examples

```
value = true
assert value
assert !false
```

### Property Getter

Retrieve a property from an object.
This returns a value. If the property was unable to be found on the object the value will be `null`.
The property name must be literal.

```
%statement%[%property%]
```

#### Examples

```
length = "hello"[length]
assert length == 5
assert length[type] == #integer

player = type #player {
   name = "BaeFell"
   age = 42
}
assert player[name] == "BaeFell"
assert player[age] == 42
```

### Property Setter

Accesses a property in an object.
This behaviour is somewhat ill-defined, since it may not alter the property.
Some types may use setters to call unary methods, e.g. `string[char_at=1]` calling `string.charAt(1)`.

```
%statement%[%property%=%statement%]
```

#### Examples

```
player = type #player {
   name = "BaeFell"
   age = 42
}
assert player[name] == "BaeFell"

player[name="Phil"]
player[age=21]

assert player[name] == "Phil"
assert player[age] == 21

assert "hello"[char_at=0] == "h"
```

### Null Literal

An empty (nothing) value.

```
null
```

#### Examples

```
word = null
assert word == null
```

### List Literal

A list of values.

```
[%statement%,...]
```

#### Examples

```
list = ["hello"]
list = ["hello", "there"]
assert list[size] == 2
for word = list {
   /print {word}
}

list = ["hello", "there", 1, 2, null]
```

### Kind Literal

A type reference.
Please note that 'kinds' are approximate (rather than definite) types.

```
#%name%
```

#### Examples

```
type = #string
assert "hello"[type] == #string
```

### Inversion

Converts the following statement to a boolean value and inverts this.

```
!%statement%
```

#### Examples

```
var = !10 == 10
assert !var
```

### Interpolation

A helper copy of string interpolation for use in code.
This is added only for parity and has no necessary use.
This may assist with order of operations.

```
{%statement%}
```

#### Examples

```
var = 10
var = {var}
var = {10 + 10}
```

### If Statement

Converts a check statement (L) to a boolean value. If this value equates to true, runs an action statement (R).
If the value is not true the action statement will be skipped.
A block start `{` is permitted after this.

```
if_%statement%_%statement%
```

#### Examples

```
if 5 == 5 /print yes
var = 5
if var == 5 {
   var = 10
}
```

### For Statement

Consumes a list of values, assigning each one to a variable (L). For each value, runs an action statement (R).
If the list of values is empty the action statement will not be run.
A block start `{` is permitted after this.

```
for_%variable_assignment%_%statement%
```

#### Examples

```
for word = ["hello", "there"] {
   assert word[length] == 5
}
```

### Equals Statement

Compares the result of an antecedent statement (L) to the result of a consequent statement (R).
Returns the boolean value of the comparison.

```
%statement%==%statement%
```

#### Examples

```
assert 5 == 5
assert !{5 == 10}
```

### Command Statement

Executes a Minecraft command from the context of the script runner.
This supports interpolation using the `{` and `}` brackets.
The result of this is typically a boolean value, determining whether the command executed correctly.

The script runner will receive any text output or effects of the command.

```
/%text%
```

#### Examples

```
/teleport @s ~ ~1 ~

word = "world"
/say hello {word}!
/say {word[length]} letters
```

### Block Open

Opens a block.
This hijacks the parser to combine the following lines into a single executable statement.

Nothing may follow the opening character in the same line.

```
{
```

#### Examples

```
{
   /print hello there
}

if 5 == 10 {
   /print uh oh something is wrong
}

for word = ["hello", "there"] {
   /print {word}
   assert word[length] == 5
}
```

### Block Close

Closes a block and returns to its previous parsing task.

This MUST be placed on its own line.
This must follow a block opening.

```
}
```

#### Examples

```
{
   /print hello there
}

if 5 == 10 {
   /print uh oh something is wrong
}

for word = ["hello", "there"] {
   /print {word}
   assert word[length] == 5
}
```

### Assertion

Evaluates the result of a statement as a boolean.
If the value is true, returns true.

If the value is false, this throws an error and terminates the running of the script.

```
assert_%statement%
```

#### Examples

```
assert 10 == 10
assert "hello" == "hello"
var = 5
assert var == 5
assert "hello"[char_at=0] == "h"

```

## Parsing

Scripts are read line-by-line and fed to a `ScriptLoader`.
Each input (line) is checked against a set of statement parsers that attempt to match the line and, if successful, feed
back an executable `Statement`.
If the match is unsuccessful the parser is flushed and the input is fed to the next parser.

Individual parsers have a fair amount of control over their parsing process, since most need to be able to parse a
sub-statement of some kind.

The parsers have the following guarantees:

1. A parser's `matches` will always be checked immediately before `parse` is called.
2. The same parser object cannot be used more than once at the same time.
3. After a parse has been attempted the parser is flushed, and could be reused.
4. If the parser fails to parse and is unable to leave the script in its starting state (e.g. it had to look at the next
   line) an error will be thrown and parsing will end.

Parsers will be re-used where possible - one copy is cached and provided when it is asked for as long as it is not in
use. If the parser is in use another copy will be made and then discarded when finished with.

