# CardDown

Just some Java Code used for converting flashcards stored in a Markdown file into a `.txt` file suitable for Anki.  

This project was created during my attendance of "Software Engineering" in my sixth semester at university. It basically exists only for my own use case and to show the progress I made during the semester. I do not intend to extend this project any further.

## Installation

There is no real installation. Just clone the repository and execute `main` in `src/MarkdownLoader.java`. All file paths have to be hardcoded into the `main` function since I have not implemented any CLI yet.

## Syntax & Types of flashcards

Since Markdown does not provide any suitable syntax for creating flashcards, there are tags you can add at the end of a headline. Possible tags are `{BACK}`, `{FRONT}`, `{QUESTION}` and `{CHOICE}`.

All flashcards start with a level 1 headline (`#` followed by a whitespace). There is no way to add comments, so the content followed by a headline is added to the current flashcard until a new flashcard begins.

There are three different types of flashcards:

1. Simple Cards
2. Question Cards
3. Multiple Choice Cards

*Simple Cards* do not have a tag at the end of their opening headline. They are the default type of flashcards, so if a tag at the end of a headline is not recognised, a *Simple Card* is created. *Simple Cards* basically only have a back side - only the headline is presented on the front side.

*Question Cards* have the `{QUESTION}` tag at the end of the opening headline. They have a front and a back side, seperated by headlines ending with a `{FRONT}` or a `{BACK}` tag. If no tag is provided, the content will be added to the last explicitly declared side. The default side is the front side.

*Multiple Choice Cards* have the `{CHOICE}` tag at the end of the opening headline and represent a simple multiple choice question. Possible answers have to start with a `[ ]` (wrong answers) or `[x]` (right answers). The front side contains all possible answers, while the back side contains the whole card. Therefore it is possible to write down possible answers and explanations in alternating order.

A simple example is provided in the `example` folder.

## Importing and exporting cards

In order to convert flashcards from a Markdown into an Anki `.txt` file, you need to create a `MarkdownLoader` first and use the `loadCardFile` method to import and convert a Markdown File. This way a list of `LearningCard` objects is created.

You can export these `LearningCard` objects to a simple HTML file or a Anki `.txt` file. In order to do that, you have to create a `HTMLCardGenerator` or an `AnkiCardGenerator` object. Both implement an `exportCards` method which takes a list of `LearningCard` objects and a file path and creates a corresponding file.
