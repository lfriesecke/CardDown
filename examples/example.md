# Provide a simple question on the front side of the simple card

And write down the answer here.


# What Markdown elemements are supported?

There is support for paragraphs, including **bold**, *italic* ~~strikethrough~~ text and `code snippets`,

* ... bullet lists,

### ... headings,

1. ... ordered lists,

as well as [Links](https://github.com/lfriesecke/CardDown).


# How do Question Cards work? {QUESTION}

Question Cards start with a level 1 heading ending in the `{QUESTION}` tag.

## This is how Question Cards work {BACK}

Question Cards have a front and a back side. The content following level 2 headings ending in the `{FRONT}` tag, are displayed on the front side. The back sides work vice versa using the `{BACK}` tag.

## {FRONT}

But how do they actually work?


# Multiple choice card are a whole different level, because ... {CHOICE}

[ ] They are not supported yet?

This is wrong, multiple choice cards are supported.

[x] They follow a specific syntax and do not use the `{FRONT}` and the `{BACK}` content.

This is correct, only the possible answers are displayed on the front side and the whole card is presented on the back.

[x] Multiple choice cards are a little messy.

This is also correct, they do not work as good as I initially intended but since this project is deprecated I do not intent to improve it's implementation.