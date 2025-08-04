# Japanese Language Parser

A personal project designed to help me study Japanese. After not being able to find a study tool advanced enough 
for my needs, I naturally decided to make one myself.

## Features To Come:
 - [Translation from Japanese to English](#translation)
 - [A dictionary of Japanese words and their English translations](#dictionary)
 - [An interactive study tool using active translation](#the-interactive-study-tool)

## Translation

To translate a Japanese word, phrase, sentence, or sentences to English, this program uses recursion to break grammar down into its individual components. For example, take the Japanese sentence *そのひとはがっこうにいきます* (*that person goes to school*). To get to its English translation, the program first seperates the noun - *そのひと* (*that person*) - and the action - *がっこうにいきます* (*go to school*). Then, using a stored table of particles used by each noun (に, を, etc.) and their English meanings, the program identifies verb - *いく* (*to go*) - and the noun being operated upon - *がっこう* (*school*) - to reach the translation "that person goes to school".

## Dictionary

The program contains a set of japanese nouns, verbs, and adjectives, along with their translations. Each type of vocabulary is a subclass containing methods for conjugating it as needed: past, present, negative, te-form for verbs, etc. In order to cleanly conjugate verbs, methods to translate between hiragana (japanese characters) and romanji (english pronunciation of japanese), ex. between "わたし" and "watashi" (*I, me*).

## The Interactive Study Tool

The study tool will randomly generate sentences and prompt the user for its translation, offering a more in-depth study method than repeatedly shoving flashcards into your brain. The senteces given will be scalable by complexity, ex. from "he ran" to "after leaving home at 8:00 am, he ran five miles to a nearby park, feeling refreshed afterward". The sentences are not garunteed to make sense, but ridiculous prompts make for a more memorable experience.

## How To Use
This project is currently under development and not in working order.