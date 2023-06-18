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

## Keywords

| Word       | Use                                                     |
|------------|---------------------------------------------------------|
| `assert`   | Make certain a value is true.                           |
| `require`  | Make certain variables are present.                     |
| `import`   | Obtains the output of an external program.              |
| `if`       | Run a statement on a condition.                         |
| `for`      | Run a statement for every element in a collection.      |
| `run`      | Run an executable object.                               |
| `struct`   | Create a data structure with a fixed set of properties. |
| `function` | Creates an executable object to run a statement.        |
| `null`     | The empty value.                                        |
| `false`    | The false value.                                        |
| `true`     | The true value.                                         |

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

### Structure

Creates a data object with given properties.
MUST be followed by a block.
The block must contain ONLY variable assignment root statements

```
struct_%block%
   %variable assignment%
   ...
%close block%
```

#### Examples

```
person = struct {
   name = "BaeFell"
   age = 42
}
if person[name] == "George" /print no
/print {person[name]} is {person[age]} years old
```

### Sum

Return the sum of the statement (L) and the statement (R).
If either statement is a non-numerical value the result will be `null`. \
The `+` operator will attempt to concatenate strings.

```
%statement%+%statement%
%statement%-%statement%
%statement%*%statement%
%statement%/%statement%
```

#### Examples

```
fifteen = 10 + 5
result = fifteen + 10
if result == 25 /print yes
greeting = "hello " + "there"

five = 10 - 5
result = five - 2

fifty = 10 * 5
result = fifty * 10

ten = 50 / 5
result = ten / 2
```

### Compare

Return the comparison result of the statement (L) and the statement (R).
This is typically a boolean. Some comparators can return a numerical or atypical object result.

| Symbol | Operation                 |
|--------|---------------------------|
| `<=`   | Less than or equal to.    |
| `<`    | Less than.                |
| `>=`   | Greater than or equal to. |
| `>`    | Greater than              |
| `&`    | And.                      |
| `\|`   | Or.                       |
| `^`    | Exclusive or.             |
| `?`    | Alternatively (if null).  |
| `==`   | Equal to.                 |
| `!=`   | Not equal to.             |

```
%statement%<=%statement%
%statement%<%statement%
%statement%>=%statement%
%statement%>%statement%
%statement%&%statement%
%statement%|%statement%
%statement%^%statement%
%statement%?%statement%
%statement%==%statement%
%statement%!=%statement%
```

#### Examples

```
assert 10 > 5
assert 10 >= 10
assert 5 < 10
assert 10 <= 10

assert true & true
assert true | false
assert false ^ true

assert null ? true

assert "hello" == "there"
assert "hello" != "there"
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

player = struct {
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
player = struct {
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

### Selector

A selector in the Minecraft format, starting with the 'at' `@` character.
This supports interpolation using the `{` and `}` brackets. \
This returns a list if multiple elements are selected or a single object if only one is found.

Please note domains are expected to override the selector to provide a relevant universe.

```
@%finder%[%property%=%value%,...]
```

#### Examples

```
players = @a[distance=..10]
for player = players {
   /print {player}
}

for entity = @e[world={world}] {
   /print {entity}
}

/print hello, {@s}
```

### Script Literal

A script literal. The complete syntax must be an exact name of a loaded script.
This defers to execution (to respect loading) but throws an error if no script is present.

```
%name%.script
```

#### Examples

```
script = other.script

run other.script
run script [name="BaeFell,age=42]
```

### Run

Runs an executable, returning its result.
Any key<->value data store can be provided as a starting variable map.
The result of the executable is returned.

```
run_%statement%
```

```
run_%statement%_%statement%
```

#### Examples

```
run other.script
run other.script [name="BaeFell,age=42]

var = 10
run other.script [var=var]

vars = [hello="there"]
run function vars
```

### Import

Acquires a resource from an external script.
This runs the script and stores its result in the named variable.

This does NOT provide the script object, since this is already obtainable using the Script Literal syntax.

This is primarily designed for interfacing with native libraries (e.g. `math`).

```
import_[%name%,...]
```

#### Examples

```
import [math]

result = run math[floor] 5.5
assert result == 5

result = run math[ceil] 5.5
assert result == 6

/print {run math[max] [5, 6]} is 6 
```

### Require

Asserts that the given variable keys are known.
Their values may be `null`, as long as they have been assigned in the context.
This will attempt to pack anonymous arguments into the variable keys if none are present.

This is designed for input checking of functions and sub-scripts.

```
require_[%name%,...]
```

#### Examples

```
require [name, age]
require []
require [name, result]
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

### Map Literal

A map of key <-> value pairs.
This is analogous to a structure but lacks the utility.

There is NO empty map literal `[]` since this is indiscernible from an empty list.

```
[%name%=%statement%,...]
```

#### Examples

```
map = [word="hello", name="BaeFell"]
var = "hello"
map = [var=var]
assert map[var] == var
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

### Function Statement

Creates an executable object that can be run under control.
This is designed to be followed by a block section.

Root variables are unavailable in functions - they must be provided in the `run` statement.

Internally, this simply returns raw child statement.

```
function_%statement%
```

#### Examples

```
run function {
   /print hello
}

func = function /print {greeting}
run func [greeting="hello"]

func = function {
   require [name, age]
   /print {name} is {age} years old
}
parameters = struct {
   name = "BaeFell"
   age = 42
}
run func parameters

func = function {
   assert {var} == null
}
var = 10
run func
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

