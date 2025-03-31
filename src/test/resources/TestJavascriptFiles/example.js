const fs = require('fs');
const path = require('path');
const http = require('http');
const https = require('https');
const os = require('os');
const events = require('events');
const util = require('util');
const readline = require('readline');
const crypto = require('crypto');
const zlib = require('zlib');
const childProcess = require('child_process');

// Base class
class Animal {
    constructor(name, age, species) { // Exceeds max parameters (3 > 1)
        this.name = name;
        this.age = age;
        this.species = species;
        this.habitat = "unknown"; // Additional attributes to increase class size
        this.isEndangered = false; // Another field for God Class detection
    }

    speak() {
        console.log(`${this.name} makes a noise.`);
    }

    longMethod() { // Exceeds max method length (5 lines)
        let result = 0;
        for (let i = 0; i < 10; i++) {
            if (i % 2 === 0) {
                result += i;
            } else {
                result -= i;
            }
            console.log(i);
            console.log(i);
            console.log(i);
            console.log(i);
            console.log(i);
            console.log(i);
            console.log(i);
            console.log(i);
            console.log(i);
            console.log(i);
        }
        return result;
    }
}

// Class inheriting from Animal
class Mammal extends Animal {
    constructor(name, age, species, hasFur, warmBlooded) { // Exceeds max parameters (5 > 1)
        super(name, age, species);
        this.hasFur = hasFur;
        this.warmBlooded = warmBlooded;
    }

    speak() {
        console.log(`${this.name} says, "I am a mammal."`);
    }
}

// Class inheriting from Mammal
class Dog extends Mammal {
    constructor(name, age, species, breed) { // Exceeds max parameters (4 > 1)
        super(name, age, species, true, true); // High coupling
        this.breed = breed;
        this.tricks = []; // Additional field to increase class size
    }

    speak() {
        console.log(`${this.name} barks.`);
    }

    addTrick(trick) { // Method that increases coupling
        this.tricks.push(trick);
    }
}

// Another class inheriting from Mammal
class Cat extends Mammal {
    constructor(name, age, species, isIndependent, color) { // Exceeds max parameters (5 > 1)
        super(name, age, species, true, true);
        this.isIndependent = isIndependent;
        this.color = color; // Additional field
    }

    speak() {
        console.log(`${this.name} meows.`);
    }
}

// Class inheriting from Dog
class Puppy extends Dog {
    constructor(name, age, species, breed) { // Depth of inheritance tree > 2
        super(name, age, species, breed);
        this.ageGroup = 'puppy';
    }

    speak() {
        console.log(`${this.name} yips.`);
    }

    excessivelyComplexMethod() { // High cyclomatic complexity (10 > 10)
        let result = 0;
        for (let i = 0; i < 10; i++) {
            if (i % 2 === 0) {
                result += i;
            } else if (i % 3 === 0) {
                result -= i;
            } else if (i % 5 === 0) {
                result *= i;
            } else if (i % 7 === 0) {
                result /= (i + 1);
            } else {
                result += 2 * i;
            }
        }
        return result;
    }
}

// Excessively large class to trigger "God Class Detection"
class Elephant extends Animal {
    constructor(name, age, species, tuskLength, trunkLength, earSize) { // Exceeds max parameters
        super(name, age, species);
        this.tuskLength = tuskLength;
        this.trunkLength = trunkLength;
        this.earSize = earSize;
        this.weight = 5000; // Add fields to increase class size
        this.height = 3.5; // Add fields
        this.region = "Africa"; // Add fields
    }

    calculateMassIndex() { // Add methods to increase method count and class size
        return this.weight / this.height;
    }
}

// Example usage
const charlie = new Animal('Charlie', 5, 'Unknown'); // Constructor with 3 parameters
const max = new Mammal('Max', 7, 'Mammal', true, true);
const buddy = new Dog('Buddy', 3, 'Dog', 'Golden Retriever');
const whiskers = new Cat('Whiskers', 2, 'Cat', true, 'Black');
const bella = new Puppy('Bella', 1, 'Dog', 'Beagle');
const dumbo = new Elephant('Dumbo', 10, 'Elephant', 1.5, 2, 0.5);

charlie.speak();
max.speak();
buddy.speak();
whiskers.speak();
bella.speak();
dumbo.calculateMassIndex();
