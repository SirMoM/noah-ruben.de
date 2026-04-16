---
title: "Elegante DSLs für komplexe Objekte erstellen mit Type-Safe Builders in Kotlin"
summary: "Wie Kotlin mit Type-Safe Builders lesbare und typsichere DSLs für komplexe Objektstrukturen und Konfigurationen ermöglicht."
date: 2026-04-01T08:15:43+02:00
tags:
  - kotlin
  - dsl
  - type-safe-builders
  - software-architecture
draft: false
author: Noah Ruben
---

In der modernen Softwareentwicklung, insbesondere bei der Arbeit mit komplexen Datenstrukturen oder Konfigurationen, suchen Entwickler stets nach Wegen, Code lesbarer, sicherer und ausdrucksstärker zu gestalten. Kotlin bietet hierfür ein mächtiges Werkzeug: **Type-Safe Builders**. Diese ermöglichen die Erstellung von domänenspezifischen Sprachen (DSLs), die nicht nur die Lesbarkeit verbessern, sondern auch Fehler zur Compile-Zeit verhindern. Ein praktischer Anwendungsfall könnte beispielsweise ein interner Tarifrechner sein, bei dem diverse Produkte mit vielen Parametern konfiguriert werden müssen.

## Inhaltsverzeichnis

1.  **Was ist eine DSL?**
2.  **Type-Safe Builders: Das Kernkonzept in Kotlin**
3.  **Grundlagen für Type-Safe Builders**
4.  **Schritt-für-Schritt-Beispiel: Der Pizza-Builder**
    *   4.1. Die klassische, Java-ähnliche Implementierung
    *   4.2. Optimierung mit idiomatischem Kotlin (`apply`)
    *   4.3. Der Sprung zur DSL: Builder mit Funktionsliteralen
    *   4.4. Erweiterung der DSL: Der `ToppingBuilder`
    *   4.5. Validierung im Builder
    *   4.6. Scope-Kontrolle mit `@DslMarker`
5.  **Vorteile und Zusammenfassung**
6.  **Weiterführende Ressourcen**
7.  **Ein größeres DSL im Einsatz**

## 1. Was ist eine DSL?

Eine **DSL (Domain-Specific Language)**, also eine domänenspezifische Sprache, ist eine (Programmier-)Sprache, die auf einen
bestimmten Anwendungsbereich oder eine Problemdomäne zugeschnitten ist. Im Gegensatz zu Allzweck-Programmiersprachen wie
Java, Python oder Kotlin selbst, ist eine DSL darauf ausgelegt, Aufgaben innerhalb ihres speziellen Kontextes besonders
einfach, prägnant und lesbar zu gestalten. Man schreibt Code, der die Sprache der Domäne spricht.

## 2. Type-Safe Builders: Das Kernkonzept in Kotlin

Typsichere Builder in Kotlin ermöglichen die Erstellung von solchen DSLs.
Sie eignen sich hervorragend für den Aufbau komplexer, hierarchischer Datenstrukturen auf eine semi-deklarative Weise.
Anstatt Objekte Schritt für Schritt mit vielen Setter-Aufrufen und expliziten Instanziierungen zu erstellen,
definiert man die Struktur durch eine Code-Syntax, die fast wie eine Konfigurationsdatei oder eine Beschreibungssprache anmutet
– jedoch mit der vollen Kraft und Sicherheit von Kotlin.

Das Herzstück sind hierbei gut benannte Funktionen, die als Builder agieren, in Kombination mit **Funktionsliteralen mit Empfänger** (Function Literals with Receiver).

Stellen Sie sich vor, Sie könnten HTML-Strukturen direkt in Kotlin-Code so schreiben:

```kotlin
html {
    head {
        title { +"XML encoding with Kotlin" }
    }
    body {
        h1 { +"XML encoding with Kotlin" }
        p  { +"this format can be used as an alternative markup to XML" }
    }
}
```

Dies ist valider Kotlin-Code und zeigt die Mächtigkeit von Type-Safe Builders.

## 3. Grundlagen für Type-Safe Builders

Um solche DSLs zu implementieren, greift Kotlin auf einige seiner Kernfeatures zurück:

* **Funktionsliterale mit Empfänger (Function Literals with Receiver)**: Z.B. `HTML.() -> Unit`. Dies erlaubt es,
  innerhalb eines Lambda-Ausdrucks Methoden und Eigenschaften des Empfängerobjekts (hier `HTML`) so aufzurufen, als wäre
  man direkt in dessen Scope (`this` kann oft weggelassen werden).
* **Höherwertige Funktionen (Higher-Order Functions)**: Funktionen, die andere Funktionen als Parameter akzeptieren oder
  zurückgeben. Die Builder-Funktionen nehmen typischerweise ein solches Funktionsliteral entgegen.
* **Extension Functions**: Ermöglichen es, Klassen um neue Funktionen zu erweitern, ohne sie direkt zu verändern.
* **Operatorüberladung**: Z.B. das `+`-Zeichen für Text (`unaryPlus`).
* **Scope-Funktionen**: Funktionen wie `apply` vereinfachen die Konfiguration von Objekten.
* **Trailing Lambdas (nachgestellte Lambdas)**: Entsprechend der Kotlin-Konvention kann ein Lambda-Ausdruck, der als
  letztes Argument einer Funktion übergeben wird, außerhalb der Klammern platziert werden, wenn der letzte Parameter der
  Funktion selbst eine Funktion ist: `val product = items.fold(1) { acc, e -> acc * e }`
  Diese Syntax ist auch als "Trailing Lambda" bekannt. Wenn das Lambda der einzige Parameter des Aufrufs ist,
  können die Klammern vollständig weggelassen werden: `run { println("...") }`

## 4. Schritt-für-Schritt-Beispiel: Der Pizza-Builder

Wir demonstrieren die Erstellung einer DSL anhand eines Beispiels: dem Konfigurieren einer Pizza.

Zunächst die Datenklassen und Enums, die wir verwenden werden:

```kotlin
enum class Size {
    SMALL, MEDIUM, LARGE
}

enum class CrustType {
    THIN, STUFFED, PAN
}

data class Pizza(
    val size: Size,
    val crustType: CrustType,
    val toppings: List<String>
) {
    override fun toString(): String {
        return "Pizza mit ${toppings.joinToString()} und einer ${crustType.name} Kruste in der Größe $size. Lecker!"
    }
}
```

### 4.1. Die klassische, Java-ähnliche Implementierung

Traditionell würde man einen Builder vielleicht so implementieren:

```kotlin
class PizzaBuilderJavaStyle {
    var size: Size = Size.MEDIUM
    var crustType: CrustType = CrustType.THIN
    private val toppings = mutableListOf<String>()

    fun setSize(size: Size): PizzaBuilderJavaStyle {
        this.size = size
        return this
    }

    fun setCrustType(crustType: CrustType): PizzaBuilderJavaStyle {
        this.crustType = crustType
        return this
    }

    fun addToppings(vararg toppings: String): PizzaBuilderJavaStyle {
        this.toppings.addAll(toppings)
        return this
    }

    fun build(): Pizza {
        return Pizza(size, crustType, toppings)
    }
}

// Verwendung
val pbJava = PizzaBuilderJavaStyle()
    .addToppings("Tomatensauce", "Käse")
    .setCrustType(CrustType.STUFFED)
println(pbJava.build())
// Output: Pizza mit Tomatensauce, Käse und einer STUFFED Kruste in der Größe MEDIUM. Lecker!
```

### 4.2. Optimierung mit idiomatischem Kotlin (`apply`)

In Kotlin nutzen Builder oft `apply` für eine "fluent API":

```kotlin
class PizzaBuilderKotlin {
    var size: Size = Size.MEDIUM
    var crustType: CrustType = CrustType.THIN
    private val toppings = mutableListOf<String>()

    fun setSize(size: Size) = apply { this.size = size }
    fun setCrustType(crustType: CrustType) = apply { this.crustType = crustType }
    fun addToppings(vararg toppings: String) = apply { this.toppings.addAll(toppings) }

    fun build() = Pizza(size, crustType, toppings)
}

// Verwendung
val pbKotlin = PizzaBuilderKotlin()
    .addToppings("Tomatensauce", "Käse", "Ananas")
    .setSize(Size.LARGE)
    .setCrustType(CrustType.THIN)
println(pbKotlin.build())
// Output: Pizza mit Tomatensauce, Käse, Ananas und einer THIN Kruste in der Größe LARGE. Lecker!
```

### 4.3. Der Sprung zur DSL: Builder mit Funktionsliteralen

Nun kommt die "Magie" der Type-Safe Builders ins Spiel.
Wir definieren eine Top-Level-Funktion `pizza`, die ein Lambda mit `PizzaBuilderForDsl` als Empfänger entgegennimmt:

```kotlin
// Für das DSL-Beispiel definieren wir eine eigene Builder-Klasse
// mit dem Namen PizzaBuilderForDsl
class PizzaBuilderForDsl {
    var size: Size = Size.MEDIUM // Default Werte
    var crustType: CrustType = CrustType.THIN
    val toppings = mutableListOf<String>() // Öffentlich für direkte Zuweisung in DSL

    // Methoden, die Sinn ergeben, bleiben erhalten
    fun addToppings(vararg newToppings: String) = apply { this.toppings.addAll(newToppings) }

    fun build() = Pizza(size, crustType, toppings.toList())
}


fun pizza(block: PizzaBuilderForDsl.() -> Unit): Pizza {
    val builder = PizzaBuilderForDsl() // Instanziiert den Builder
    builder.block()                    // Führt den Lambda-Block auf dem Builder aus
    return builder.build()             // Erstellt und gibt das Pizza-Objekt zurück
}

// Verwendung der DSL
val dslPizza = pizza {
    size = Size.LARGE // Direkter Zugriff auf Properties des Builders!
    crustType = CrustType.PAN
    addToppings("Tomatensauce", "Käse", "Pilze")
    // oder auch:
    toppings.add("Salami") // da toppings public mutableList ist
}
println(dslPizza)
// Output: Pizza mit Tomatensauce, Käse, Pilze, Salami und einer PAN Kruste in der Größe LARGE. Lecker!
```

Innerhalb der geschweiften Klammern von `pizza { ... }` befinden wir uns im Kontext einer `PizzaBuilderForDsl`-Instanz.
Daher können wir direkt auf deren (öffentliche) Member `size`, `crustType` und `addToppings` zugreifen.

### 4.4. Erweiterung der DSL: Der `ToppingBuilder`

DSLs können geschachtelt werden, um die Struktur weiter zu verbessern. Führen wir einen `ToppingBuilder` ein:

```kotlin
class ToppingBuilder {
    private val toppingsInternal = mutableListOf<String>()

    fun tomatosauce() = apply { toppingsInternal.add("Tomatensauce") }
    fun kaese() = apply { toppingsInternal.add("Käse") }
    fun pilze() = apply { toppingsInternal.add("Pilze") }
    // Weitere Beläge...

    fun build(): List<String> = toppingsInternal.toList()
}

fun PizzaBuilderForDsl.topping(block: ToppingBuilder.() -> Unit) {
    val builder = ToppingBuilder()
    builder.block()
    addToppings(*builder.build().toTypedArray())
}

// Verwendung der erweiterten DSL
val advancedDslPizza = pizza {
    size = Size.MEDIUM
    crustType = CrustType.STUFFED

    topping { // Hier nutzen wir den ToppingBuilder
        tomatosauce()
        kaese()
        pilze()
    }
    // Man könnte weiterhin addToppings direkt nutzen oder weitere topping-Blöcke hinzufügen
}
println(advancedDslPizza)
// Output: Pizza mit Tomatensauce, Käse, Pilze und einer STUFFED Kruste in der Größe MEDIUM. Lecker!
```

### 4.5. Validierung im Builder

Builder sind ein idealer Ort für Validierungslogik, bevor das eigentliche Objekt erstellt wird.
Für das Validierungsbeispiel führen wir nun die Varianten `PizzaBuilderValidated` und `ToppingBuilderValidated` ein:

```kotlin
class PizzaBuilderValidated {
    var size: Size? = null // Jetzt nullable, um Initialzustand zu prüfen
    var crustType: CrustType? = null
    private val toppingsList = mutableListOf<String>()

    fun setSize(size: Size) = apply { this.size = size }
    fun setCrustType(crustType: CrustType) = apply { this.crustType = crustType }
    fun addToppings(vararg newToppings: String) = apply { this.toppingsList.addAll(newToppings) }

    fun getToppings(): List<String> = this.toppingsList.toList()


    fun build(): Pizza {
        when {
            size == null -> throw IllegalStateException("Die Größe darf nicht leer bleiben")
            crustType == null -> throw IllegalStateException("Der Krustentyp darf nicht leer bleiben")
            toppingsList.contains("Ananas") && size != Size.LARGE -> throw UnsupportedOperationException("Ananas auf einer Pizza, die nicht Large ist? Ernsthaft?")
            toppingsList.isEmpty() -> throw IllegalStateException("Eine Pizza braucht mindestens einen Belag!")
        }
        return Pizza(size!!, crustType!!, toppingsList.toList())
    }

    fun topping(block: ToppingBuilderValidated.() -> Unit) {
        val builder = ToppingBuilderValidated()
        builder.block()
        addToppings(*builder.build().toTypedArray())
    }
}

// ToppingBuilder mit Validierung
class ToppingBuilderValidated {
    private val toppingsInternal = mutableListOf<String>()

    fun tomatosauce() = apply { toppingsInternal.add("Tomatensauce") }
    fun kaese() = apply { toppingsInternal.add("Käse") }
    fun pilze() = apply { toppingsInternal.add("Pilze") }
    fun addToppings(vararg newToppings: String) = apply { this.toppingsInternal.addAll(newToppings) }


    fun build(): List<String> {
        when {
            toppingsInternal.size > 5 -> throw IllegalStateException("Mehr als 5 Beläge sind nicht erlaubt!")
            toppingsInternal.isEmpty() -> throw IllegalStateException("Mindestens 1 Belag ist nötig im Topping-Block!")
        }
        return toppingsInternal.toList()
    }
}

// Neue pizza-Funktion, die den validierten Builder verwendet
fun pizzaValidated(block: PizzaBuilderValidated.() -> Unit): Pizza {
    val builder = PizzaBuilderValidated()
    builder.block()
    return builder.build()
}

// Test der Validierung
pizzaValidated {
    // size nicht gesetzt
    crustType = CrustType.PAN
    topping { kaese(); pilze() }
}
// Wirft einen Fehler: Die Größe darf nicht leer bleiben

pizzaValidated {
    size = Size.SMALL
    crustType = CrustType.THIN
    topping { tomatosauce(); kaese(); pilze() }
    addToppings("Ananas") // Direkt im PizzaBuilder
}
// Wirft einen Fehler: Ananas auf einer Pizza, die nicht Large ist? Ernsthaft?
```

### 4.6. Scope-Kontrolle mit `@DslMarker`

Bei verschachtelten Buildern (wie `PizzaBuilderValidated` und `ToppingBuilderValidated`) kann es passieren, dass Methoden aus einem äußeren Kontext (Scope) fälschlicherweise im inneren Kontext aufgerufen werden.

Beispiel:
```kotlin
// Angenommen PizzaBuilderValidated hätte auch eine Funktion `pilze()`
// und ToppingBuilderValidated hätte auch eine `setSize()`
pizzaValidated {
    size = Size.LARGE
    topping {
        // Hier wollen wir eigentlich ToppingBuilderValidated.pilze() aufrufen
        // Was, wenn PizzaBuilderValidated.pilze() auch existiert und aufgerufen wird?
        pilze()

        // addToppings("Curry") // Dieses `addToppings` ist von `ToppingBuilderValidated`
        // Wenn wir versehentlich PizzaBuilderValidated.addToppings() aufrufen könnten,
        // wäre das verwirrend.
    }
}
```
Um solche Mehrdeutigkeiten zu vermeiden und den Scope klar zu definieren, gibt es `@DslMarker`.

1.  Definiere eine Marker-Annotation:
    ```kotlin
    @DslMarker
    annotation class PizzaBuilderMarker
    ```

2.  Annotiere die Builder-Klassen (oder deren gemeinsame Basisklasse) mit dieser Marker-Annotation:
    ```kotlin
    @PizzaBuilderMarker
    abstract class BaseBuilder<T> { // Gemeinsame Basisklasse (optional, aber gut für Marker)
        abstract fun build(): T
    }

    @PizzaBuilderMarker // Direkt auf Klasse, wenn keine Basisklasse oder Marker auf Basisklasse reicht
    class PizzaBuilderDslMarker : BaseBuilder<Pizza>() {
        var size: Size? = null // Jetzt nullable, um Initialzustand zu prüfen
        var crustType: CrustType? = null
        private val toppingsList = mutableListOf<String>()

        fun setSize(size: Size) = apply { this.size = size }
        fun setCrustType(crustType: CrustType) = apply { this.crustType = crustType }
        fun addToppings(vararg newToppings: String) = apply { this.toppingsList.addAll(newToppings) }

        fun getToppings(): List<String> = this.toppingsList.toList()

        override fun build(): Pizza {
            when {
                size == null -> throw IllegalStateException("Die Größe darf nicht leer bleiben")
                crustType == null -> throw IllegalStateException("Der Krustentyp darf nicht leer bleiben")
                toppingsList.contains("Ananas") && size != Size.LARGE -> throw UnsupportedOperationException("Ananas auf einer Pizza, die nicht Large ist? Ernsthaft?")
                toppingsList.isEmpty() -> throw IllegalStateException("Eine Pizza braucht mindestens einen Belag!")
            }
            return Pizza(size!!, crustType!!, toppingsList.toList())
        }

        fun topping(block: ToppingBuilderDslMarker.() -> Unit) {
            val builder = ToppingBuilderDslMarker()
            builder.block()
            addToppings(*builder.build().toTypedArray())
        }
    }

    @PizzaBuilderMarker
    class ToppingBuilderDslMarker : BaseBuilder<List<String>>() {
        private val toppingsInternal = mutableListOf<String>()

        fun tomatosauce() = apply { toppingsInternal.add("Tomatensauce") }
        fun kaese() = apply { toppingsInternal.add("Käse") }
        fun pilze() = apply { toppingsInternal.add("Pilze") }

        override fun build(): List<String> {
            when {
                toppingsInternal.size > 5 -> throw IllegalStateException("Mehr als 5 Beläge sind nicht erlaubt!")
                toppingsInternal.isEmpty() -> throw IllegalStateException("Mindestens 1 Belag ist nötig im Topping-Block!")
            }
            return toppingsInternal.toList()
        }
    }

    fun pizzaWithMarker(block: PizzaBuilderDslMarker.() -> Unit): Pizza {
        val builder = PizzaBuilderDslMarker()
        builder.block()
        return builder.build()
    }
    ```

Wenn wir nun versuchen, `addToppings` vom `PizzaBuilderDslMarker` innerhalb des `topping`-Blocks aufzurufen, gibt der Compiler einen Fehler aus:
```kotlin
pizzaWithMarker {
    size = Size.LARGE
    crustType = CrustType.PAN

    topping {
        tomatosauce()
        kaese()
        pilze()
        // Dieser Aufruf führt zu einem Compilerfehler,
        // weil der äußere PizzaBuilderDslMarker hier nicht implizit verfügbar ist.
        addToppings("Champignons")

        // Für einen Zugriff auf den äußeren Builder wäre ein expliziter Receiver nötig.
        // Das zeigen wir im nächsten Beispiel.
    }
    // Dieses addToppings ist von PizzaBuilderDslMarker
    addToppings("Zwiebeln")
}

// Korrektes Beispiel mit explizitem Zugriff auf den äußeren Builder:
val pizzaResult = pizzaWithMarker(outer@{
    size = Size.LARGE
    crustType = CrustType.PAN

    // PizzaBuilderDslMarker Kontext
    addToppings("Salami vom PizzaBuilder")

    topping {
        // ToppingBuilderDslMarker Kontext
        tomatosauce()
        kaese()
        pilze()
        this@outer.addToppings("Champignons vom PizzaBuilder")
    }
})
println(pizzaResult)
// Output: Pizza mit Salami vom PizzaBuilder, Champignons vom PizzaBuilder, Tomatensauce, Käse, Pilze und einer PAN Kruste in der Größe LARGE. Lecker!

```

Der `@DslMarker` stellt sicher, dass innerhalb eines Lambda-Blocks nur Methoden des aktuellsten (innersten) Receivers
implizit aufgerufen werden können, der mit demselben Marker annotiert ist. Methoden äußerer Receiver erfordern einen
expliziten Receiver (z.B. `this@outer.method()`).

## 5. Vorteile und Zusammenfassung

Type-Safe Builders in Kotlin bieten eine elegante und sichere Methode, um DSLs zu erstellen. Die wichtigsten Vorteile sind:

1.  **Lesbarkeit**: Der Code wird deklarativer und drückt die Absicht klarer aus, fast wie eine Konfigurationssprache.
2.  **Typsicherheit**: Fehler wie falsche Typen oder unzulässige DSL-Strukturen werden bereits zur Compile-Zeit erkannt. Fehlende Pflichtangaben sind in den hier gezeigten Buildern technisch Laufzeitfehler, fallen durch die Validierung in `build()` aber bereits beim ersten Testlauf oder lokalen Start der Anwendung unmittelbar auf.
3.  **Ausdrucksstärke**: Komplexe Objektstrukturen und Konfigurationen lassen sich prägnant und intuitiv definieren.
4.  **Wartbarkeit**: Durch die klare Struktur und Lesbarkeit ist der Code einfacher zu verstehen und zu pflegen.
5.  **Wiederverwendbarkeit**: Builder und DSL-Komponenten können modular aufgebaut und wiederverwendet werden.
6.  **Fluent API**: Oft resultiert eine sehr angenehm zu nutzende "fließende" API.
7.  **Scope-Kontrolle**: Durch `@DslMarker` werden Mehrdeutigkeiten in verschachtelten DSL-Strukturen vermieden.

Besonders in Szenarien, in denen wiederholbare und strukturierte Aufgaben wie das Konfigurieren von Objekten (Produkte, UI-Komponenten, Testdaten etc.) anfallen, liefern DSLs mit Type-Safe Builders einen deutlichen Mehrwert. Kotlin, mit seinen Sprachfeatures wie Funktionsliteralen mit Empfänger, Extension Functions und Scope-Funktionen, ist hervorragend geeignet, um diese Art von DSLs effizient und sicher umzusetzen.

## 6. Weiterführende Ressourcen

*   **Offizielle Kotlin Dokumentation zu Type-Safe Builders:** [kotlinlang.org/docs/type-safe-builders.html](https://kotlinlang.org/docs/type-safe-builders.html)
*   **Operatorüberladung:** [kotlinlang.org/docs/operator-overloading.html](https://kotlinlang.org/docs/operator-overloading.html)
*   **Inline-Funktionen:** [kotlinlang.org/docs/inline-functions.html](https://kotlinlang.org/docs/inline-functions.html)
*   **Scope-Funktionen (insb. `apply`):** [kotlinlang.org/docs/scope-functions.html#apply](https://kotlinlang.org/docs/scope-functions.html#apply)

**Vorträge und Videos:**

*   ANTON ARHIPOV - Kotlin DSL in unter einer Stunde: [youtube.com/watch?v=zYNbsVv9oN0](https://www.youtube.com/watch?v=zYNbsVv9oN0)
*   KotlinConf 2018 - Erstellen interner DSLs in Kotlin von Venkat Subramaniam: [youtube.com/watch?v=JzTeAM8N1-o](https://www.youtube.com/watch?v=JzTeAM8N1-o&t=83s&pp=ygUKa290bGluIGRzbA%3D%3D)

## 7. Ein größeres DSL im Einsatz

Die gleiche Idee funktioniert auch dann noch gut, wenn ein Builder nur ein Baustein in einer größeren Struktur ist.
Im folgenden Beispiel beschreibt die äußere HTML-DSL eine einfache Bestellübersicht, während innerhalb der einzelnen
Positionen weiterhin der bekannte `pizzaWithMarker`-Builder verwendet wird.
Die kleine Hilfsfunktion `renderPizza { ... }` markiert dabei ganz bewusst nur die Übergangsstelle zwischen HTML-DSL
und Pizza-DSL, ohne die dafür nötige Glue-Logik auszubreiten:

```kotlin
val page = html {
    head {
        title { +"Bestellübersicht" }
    }
    body {
        header {
            h1 { +"Bestellübersicht" }
            p { +"Bestellung #4711 vom 01.04.2026" }
            p { +"Status: In Zubereitung" }
            p { +"Die äußere DSL beschreibt die Übersicht, die innere DSL die eigentlichen Objekte." }
        }
        main {
            section {
                h2 { +"Bestellte Positionen" }
                p { +"Die einzelnen Positionen bestehen jeweils aus einer Pizza-Konfiguration." }

                div {
                    h3 { +"Position 1" }
                    p { +"1 x Pizza Large mit Pan-Kruste" }
                    renderPizza {
                        pizzaWithMarker {
                            size = Size.LARGE
                            crustType = CrustType.PAN
                            addToppings("Salami", "Zwiebeln")

                            topping {
                                tomatosauce()
                                kaese()
                                pilze()
                            }
                        }
                    }
                }

                div {
                    h3 { +"Position 2" }
                    p { +"1 x Pizza Medium mit Stuffed-Kruste" }
                    renderPizza {
                        pizzaWithMarker {
                            size = Size.MEDIUM
                            crustType = CrustType.STUFFED

                            topping {
                                tomatosauce()
                                kaese()
                                pilze()
                            }

                            addToppings("Rucola", "Oliven")
                        }
                    }
                }

                div {
                    h3 { +"Position 3" }
                    p { +"1 x Pizza Small mit Thin-Kruste" }
                    renderPizza {
                        pizzaWithMarker {
                            size = Size.SMALL
                            crustType = CrustType.THIN

                            topping {
                                tomatosauce()
                                kaese()
                            }

                            addToppings("Peperoni")
                        }
                    }
                }
            }

            section {
                h2 { +"Zusammenfassung" }
                p { +"Auch außerhalb der eigentlichen Pizza-Konfiguration bleibt die Struktur deklarativ und kompakt." }
                div {
                    p { +"Zahlungsart: Bar bei Lieferung" }
                    p { +"Lieferhinweis: Bitte an der Seitentür klingeln." }
                    p { +"Gesamtsumme: 37,40 EUR" }
                    p { +"Der äußere HTML-Builder liefert den Rahmen, die Pizza-DSL bleibt als fokussierter Baustein wiederverwendbar." }
                }
            }
        }
        footer {
            p { +"Vielen Dank für Ihre Bestellung." }
            p { +"Gerade in größeren Konfigurationen zeigt sich, wie gut sich kleine, spezialisierte Builder miteinander kombinieren lassen." }
        }
    }
}
```
